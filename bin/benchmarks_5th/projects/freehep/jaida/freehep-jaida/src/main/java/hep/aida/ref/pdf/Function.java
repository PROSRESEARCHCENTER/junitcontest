package hep.aida.ref.pdf;

import hep.aida.IAnnotation;
import hep.aida.IModelFunction;
import hep.aida.IRangeSet;
import hep.aida.ref.Annotation;

import java.util.ArrayList;

/**
 * Base function. Any function should extend this class.
 *
 * @author The FreeHEP team @ SLAC.
 *
 */
public class Function extends Variable implements IModelFunction, VariableListener, FunctionDispatcher {
    
    private VariableList parList = new VariableList(VariableList.PARAMETER);
    private VariableList depList = new VariableList(VariableList.DEPENDENT);
    private VariableList funList = new VariableList(VariableList.FUNCTION);
//    private VariableList varList = new VariableList(VariableList.ANY);
    
    private ArrayList listeners = new ArrayList();
    
    private boolean isNormalized = false;
    private double normalization = 1;
    
    private Parameter norm;
    
    private String codeletString;
    private Annotation annotation;
    
    public Function(String name) {
        super(name, Variable.FUNCTION);
        
        norm = new Parameter("norm",1);
        
        setCodeletString("codelet:"+this.getClass()+":class:");
        
        annotation = new Annotation();
        annotation.addItem(Annotation.titleKey, "");
        annotation.setFillable(true);
    }
    
    
    /**
     * Variables Management.
     *
     */
    public void addVariable(Variable v) {
        if ( v instanceof Dependent )
            addDependent( (Dependent)v, true );
        else if ( v instanceof Parameter )
            addParameter( (Parameter)v, true );
        else if ( v instanceof Function )
            addFunction( (Function)v, true );
    }
    public void addVariables(VariableList list) {
        for ( int i = 0; i < list.size(); i++ ) {
            Variable v = list.get(i);
            addVariable(v);
            variableChanged(v);
        }
    }
    
    private void addDependent(Dependent dep, boolean addListener) {
        checkVariable(dep);
        if ( addListener )
            dep.addVariableListener(this);
        depList.add(dep);
    }
    
    private void addParameter(Parameter par, boolean addListener) {
        checkVariable(par);
        if ( addListener )
            par.addVariableListener(this);
        parList.add(par);
    }
    
    private void addFunction(Function func, boolean addListener) {
        if ( addListener )
            func.addVariableListener(this);
        funList.add(func);
        for ( int i = 0; i < func.numberOfDependents(); i++ )
            addDependent( func.getDependent(i), false );
        for ( int i = 0; i < func.numberOfParameters(); i++ )
            addParameter( func.getParameter(i), false );
    }
    
    private Function getCompositeFunction(Variable var) {
        for ( int i = 0; i < funList.size(); i++ ) {
            Function f = (Function) funList.get(i);
            if ( f.hasVariable(var) )
                return f;
        }
        throw new IllegalArgumentException("Variable "+var.name()+" is not composite!");
    }
    
    private void checkVariable(Variable var) {
        if ( ! isValidVariableName( var.name() ) )
            throw new IllegalArgumentException("A Variable with name "+var.name()+" already belongs to this Function.");
        if ( hasVariable(var) )
            throw new IllegalArgumentException("Variable "+var.name()+" already belongs to this Function");
    }
    
    private boolean isValidVariableName(String name) {
        if ( depList.contains(name) || parList.contains(name) )
            return false;
        if ( norm != null && name.equals( norm.name() ) )
            return false;
        return true;
    }
    
    protected boolean hasDependent(Dependent dep) {
        return depList.contains(dep);
    }
    
    protected boolean hasParameter(Parameter par) {
        return parList.contains(par);
    }
    
    protected boolean hasVariable(Variable var) {
        if (depList.contains(var) || parList.contains(var) )
            return true;
        if ( norm != null && var == norm )
            return true;
        return false;
    }
    
    public boolean variableChangingUnits(Variable var, Units units) {
        return false;
    }
    
    public void variableChangedUnits(Variable var) {
        notifyFunctionChanged(new FunctionChangedEvent( FunctionChangedEvent.FUNCTION_CHANGED ) );
    }
    
    public boolean variableChangingValue(Variable var, double value) {
        return true;
    }

    public String normalizationParameter() {
        return getNormalizationParameter().name();
    }
    
    /**
     * This method is invoked when a variable in the function has changed its value.
     *
     */
    public void variableChanged(Variable var) {
        
    }
    
    public void variableChangedValue(Variable var) {
        variableChanged(var);
        if ( parList.contains(var) ) {
            notifyFunctionChanged(new FunctionChangedEvent( FunctionChangedEvent.PARAMETER_VALUE_CHANGED ) );
            updateNormalization();
        }
    }
    
    public boolean variableChangingName(Variable var, String name) {
        return isValidVariableName(name);
    }
    
    public void variableChangedName(Variable var) {
        notifyFunctionChanged(new FunctionChangedEvent( FunctionChangedEvent.PARAMETER_NAME_CHANGED ) );
    }
    
    public void setValue(double value) {
        super.setValue(value);
        notifyFunctionChanged(new FunctionChangedEvent( FunctionChangedEvent.FUNCTION_VALUE_CHANGED ) );
    }
    
    public double functionValue() {
        throw new RuntimeException("This method MUST be overwritten!!");
    }

    /**
     * This method is used to generate toy Mc data sets.
     * It should be overwritten by functions whose maximum value can be provided.
     * If it is not overwriten the maximum value will be evaluated with a mc
     * set of data points.
     *
     */
    public double functionMaxValue() {
        return Double.NaN;
    }
    
    
    private double numericalIntegral() {
        return numericalIntegral(depList);
    }
    
    private double numericalIntegral(VariableList l) {
        int dim = l.size();
        if ( dim == 0 )
            return 0;
        
        double val = 0;
        
        //Save the dependent's value before integrating
        double[] x = new double[dim];
        for ( int i = 0; i < x.length; i++ )
            x[i] = l.get(i).value();
        
        if ( dim == 1 )
            val = FunctionIntegrator.integralTrapezoid(this, l);
        else
            val = FunctionIntegrator.integralMC(this, l);
        
        //Restore the dependent's value after the integration
        for ( int i = 0; i < x.length; i++ )
            l.get(i).setValue(x[i]);
        return val;
    }
    
    protected void updateNormalization() {
        normalization = 0;
        VariableList l = new VariableList(VariableList.DEPENDENT);
        for ( int i = 0; i < numberOfDependents(); i++ ) {
            Dependent dep = getDependent(i);
            if ( ( ! isComposite(dep) ) && hasAnalyticalNormalization(dep) )
                normalization += evaluateAnalyticalNormalization(dep);
            else
                l.add(dep);
        }
        if ( l.size() > 0 )
            normalization += numericalIntegral(l);
    }
    
    /**
     * To be overwritten if Function provides analytical normalization.
     *
     */
    public boolean hasAnalyticalNormalization(Dependent dep) {
        return false;
    }
    
    public double evaluateAnalyticalNormalization(Dependent dep) {
        VariableList l = new VariableList(VariableList.DEPENDENT);
        l.add(dep);
        return numericalIntegral(l);
    }
    
    protected double maxValue() {
        updateNormalization();
        return functionMaxValue()/normalization;
    }
    
    public double value() {
        double fVal = functionValue();
        if ( fVal < 0 ) {
            System.out.println("Negative value for "+name()+" : "+fVal+". Setting it to 0.");
            fVal = 0;
        }
        if (isNormalized) {
            fVal /= normalization;
            if ( isNormalized )
                if ( fVal > 1 ) 
                    throw new RuntimeException("Probability greater than 1 for "+name()+" : "+fVal);
        }
        if ( norm != null )
            fVal *= norm.value();
        return fVal;
    }
    
    /**
     * To be overwritten by classes extending Function.
     * This method is used internally by this class to evaluate the derivative
     * with respect to a given Variable.
     *
     */
    public double evaluateAnalyticalVariableGradient(Variable var) {
        throw new IllegalArgumentException("This function does not provide the gradient");
    }
    
    private double evaluateVariableGradient(Variable var) {
        if ( var == norm )
            return functionValue();
        if ( providesGradientWithRespectToVariable(var) )
            return getNormalizationParameter().value()*evaluateAnalyticalVariableGradient(var);
        else
            return FunctionDerivative.derivative(this, var, 1.);
    }
    
    /**
     * To be overwritten by classes extending Function.
     * This method is used internally by this class to determine if a function has
     * can provide an analytical gradient with respect to a given Variable.
     *
     */
    public boolean hasAnalyticalVariableGradient(Variable var) {
        return false;
    }
    
    public boolean providesGradientWithRespectToVariable(Variable var) {
        if ( isComposite(var) ) {
            Function composite = getCompositeFunction(var);
            return composite.providesGradientWithRespectToVariable(var) && providesGradientWithRespectToVariable(composite);
        } else {
            if ( hasVariable(var) )
                return hasAnalyticalVariableGradient(var);
            else
                throw new IllegalArgumentException("Variable "+var.name()+" does not belong to this function.");
        }
    }
    
    public double[] gradient() {
        double[] grad = new double[numberOfDependents()];
        for ( int i = 0; i < grad.length; i++ )
            grad[i] = evaluateVariableGradient(getDependent(i))/normalization;
        return grad;
    }
    
    public double[] parameterGradient() {
        double[] parGrad = new double[numberOfParameters()];
        for ( int i = 0; i < parGrad.length; i++ )
            parGrad[i] = evaluateVariableGradient(getParameter(i))/normalization;
        return parGrad;
    }
    
    public Parameter getParameter(int index) {
        if ( index == parList.size() && norm != null )
            return norm;
        return (Parameter) parList.get(index);
    }
    
    public Parameter getParameter(String parName) {
        if ( norm != null && parName.equals(norm.name()) )
            return norm;
        return (Parameter) parList.get(parName);
    }
    
    public int numberOfParameters() {
        int nPars = parList.size();
        if ( ! isNormalized && norm != null )
            nPars += 1;
        return nPars;
    }
    
    public Dependent getDependent(int index) {
        return (Dependent) depList.get(index);
    }
    
    public Dependent getDependent(String parName) {
        return (Dependent) depList.get(parName);
    }
    
    public int numberOfDependents() {
        return depList.size();
    }
    
    public boolean isComposite(Variable var) {
        for ( int i = 0; i < funList.size(); i++ ) {
            Function f = (Function) funList.get(i);
            if ( f.hasVariable(var) )
                return true;
        }
        return false;
    }
    
    /**
     * The normalization Parameter for a function is ALWAYS the last one.
     *
     */
    public Parameter getNormalizationParameter() {
        return norm;
    }
    
    public void setNormalizationParamter(Parameter par) {
        norm = par;
    }
    
    public void addFunctionListener( FunctionListener listener ) {
        listeners.add(listener);
    }
    
    public void removeFunctionListener( FunctionListener listener ) {
        listeners.remove(listener);
    }
    
    protected void notifyFunctionChanged(FunctionChangedEvent event) {
        for ( int i = 0; i < listeners.size(); i++ )
            ( (FunctionListener) listeners.get(i) ).functionChanged(event);
    }
    
    public void variableChangedRange(Variable var) {
        notifyFunctionChanged(new FunctionChangedEvent( FunctionChangedEvent.RANGE_CHANGED ) );
        updateNormalization();
    }
    
    public void normalizationRangeChanged() {
        notifyFunctionChanged(new FunctionChangedEvent( FunctionChangedEvent.RANGE_CHANGED ) );
        updateNormalization();
    }
    
    public boolean isNormalized() {
        return isNormalized;
    }
    
    public void normalize(boolean normalize) {
        Parameter norm = getNormalizationParameter();
        if ( norm != null ) {
            if ( normalize ) {
                if ( ! isNormalized() ) {
                    norm.setValue(1);
                    norm.setFixed(true);
                }
            } else {
                norm.setFixed(false);
            }
        }
        isNormalized = normalize;
        updateNormalization();
    }
    
    public boolean providesNormalization() {
        return false;
    }
    
    // Methods required by IModelFunction
    
    public IAnnotation annotation() {
        return annotation;
    }
    
    public String codeletString() {
        return codeletString;
    }
    
    public void setCodeletString(String str) {
        codeletString = str;
    }
    
    public double value(double[] v) {
        if ( v.length != dimension() )
            throw new IllegalArgumentException("Illegal dimension for the vector "+v.length+". It should match the Function's dimension "+dimension());
        for ( int i = 0; i < dimension(); i++ )
            getDependent(i).setValue(v[i]);
        return value();
    }
    
    public double[] gradient(double[] v) {
        if ( v.length != dimension() )
            throw new IllegalArgumentException("Illegal dimension for the vector "+v.length+". It should match the Function's dimension "+dimension());
        for ( int i = 0; i < dimension(); i++ )
            getDependent(i).setValue(v[i]);
        return gradient();
    }
    
    public int dimension() {
        return numberOfDependents();
    }
    
    public int indexOfParameter(String parName) {
        return parList.indexOf(parName);
    }
    
    public double parameter(String parName) {
        return getParameter(parName).value();
    }
    
    public String[] parameterNames() {
        String[] parNames = new String[numberOfParameters()];
        for ( int i = 0; i < parNames.length; i++ )
            parNames[i] = getParameter(i).name();
        return parNames;
    }
    
    public double[] parameters() {
        double[] parValues = new double[numberOfParameters()];
        for ( int i = 0; i < parValues.length; i++ )
            parValues[i] = getParameter(i).value();
        return parValues;
    }
    
    public void setParameter(String parName, double parValue) throws java.lang.IllegalArgumentException {
        getParameter(parName).setValue(parValue);
    }
    
    public void setParameters(double[] pars) throws java.lang.IllegalArgumentException {
        if ( pars.length != numberOfParameters() )
            throw new IllegalArgumentException("Wrong number of input parameters:"+pars.length+", must be "+numberOfParameters());
        for (int i=0; i<pars.length; i++)
            getParameter(i).setValue(pars[i]);
    }
    
    public void setTitle(String title) throws java.lang.IllegalArgumentException {
        annotation.setValue(Annotation.titleKey, title);
    }
    
    public String title() {
        return annotation.value(Annotation.titleKey);
    }
    
    public String variableName(int index) {
        return getDependent(index).name();
    }
    
    public String[] variableNames() {
        String[] depNames = new String[dimension()];
        for ( int i = 0; i < depNames.length; i++ )
            depNames[i] = getDependent(i).name();
        return depNames;
    }
    
    public double functionValue(double[] v) {
        if ( v.length != dimension() )
            throw new IllegalArgumentException("Illegal dimension for the vector "+v.length+". It should match the Function's dimension "+dimension());
        for ( int i = 0; i < dimension(); i++ )
            getDependent(i).setValue(v[i]);
        return functionValue();
    }
    
    public void excludeNormalizationAll() {
        for (int i=0; i<dimension(); i++)
            getDependent(i).range().excludeAll();
        normalizationRangeChanged();
    }
    
    public void includeNormalizationAll() {
        for (int i=0; i<dimension(); i++)
            getDependent(i).range().includeAll();
        normalizationRangeChanged();
    }
    
    public IRangeSet normalizationRange(int i) {
        return (IRangeSet) getDependent(i).range();
    }
    
    public double[] parameterGradient(double[] v) {
        if ( v.length != dimension() )
            throw new IllegalArgumentException("Illegal dimension for the vector "+v.length+". It should match the Function's dimension "+dimension());
        for ( int i = 0; i < dimension(); i++ )
            getDependent(i).setValue(v[i]);
        
        double[] tmpGrad = parameterGradient();
        if ( isNormalized() )
            return tmpGrad;
        
        double[] grad = new double[tmpGrad.length+1];
        for ( int i = 0; i < tmpGrad.length; i++ )
            grad[i] = tmpGrad[i];
        grad[grad.length-1] = functionValue();
        return grad;
    }
    
    public boolean isEqual(hep.aida.IFunction iFunction) {
        throw new UnsupportedOperationException("This method has not been implemented.");
    }
    
    public boolean providesGradient() {
        for ( int i = 0; i < numberOfDependents(); i++ ) {
            if ( ! providesGradientWithRespectToVariable( getDependent(i) ) )
                return false;
            
        }
        return true;
    }
    
    public boolean providesParameterGradient() {
        for ( int i = 0; i < numberOfParameters(); i++ ) {
            if ( ! providesGradientWithRespectToVariable( getParameter(i) ) )
                return false;
            
        }
        return true;
    }
    
    protected void setVariableValue(double value) {
        throw new IllegalArgumentException("Cannot set the value of a function ");
    }
    
}
