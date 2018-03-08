package hep.aida.ref.pdf;

import hep.aida.IFunction;
import hep.aida.IModelFunction;

/**
 * Wrapper of IFunction. The result is a Function.
 *
 * @author The FreeHEP team @ SLAC.
 *
 */
public class IFunctionWrapper extends Function {
    
    private IFunction f;
    private boolean isModel = false;
    private IModelFunction model;
    private boolean hasNormalization = false;

//    private double[] pars;
    private double[] vars;
    
    public IFunctionWrapper(String name, IFunction f) {
        super(name);
        
        this.f = f;
        
        if ( f instanceof IModelFunction ) {
            isModel = true;
            model = (IModelFunction)f;
            hasNormalization = model.providesNormalization();
        }
        
        String[] dependents = f.variableNames();
        vars = new double[dependents.length];
        VariableList list = new VariableList();
        for ( int i = 0; i < dependents.length; i++ )
            list.add( new Dependent(dependents[i],-10, 10));
        
        String[] parNames = f.parameterNames();
        double[] parValues = f.parameters();
//        pars = new double[parValues.length];
        for ( int i = 0; i < parNames.length; i++ )
            list.add( new Parameter(parNames[i],parValues[i]) );
        
        addVariables(list);
        
        // A Function has a dedicated parameter for the normalization.
        // This is not true for a general IModelFunction.
        // By setting the normalization parameter to null we convert to the
        // IModelFunction modus operandi.
        super.setNormalizationParamter(null);
    }
    
    public void variableChanged(Variable var) {
        if ( var instanceof Parameter ) {
        if ( getParameter( var.name()) != null )
            f.setParameter(var.name(), var.value());
        }
    }
    
    private void loadDependents() {
        for ( int i = 0; i < f.dimension(); i++ )
            vars[i] = getDependent(i).value();        
    }

    public boolean hasAnalyticalVariableGradient(Variable var) {
        return f.providesGradient();
    }
    
    public double functionValue() {
        loadDependents();
        return f.value(vars);
    }

    public double value() {
        if ( isModel && hasNormalization ) {
            loadDependents();
            return model.value(vars);
        } else
            return super.value();
    }
    
    public double[] gradient() {
        loadDependents();
        return f.gradient(vars);
    }
    
    public double[] parameterGradient() {
        if ( isModel ) {
            loadDependents();
            return model.parameterGradient(vars);
        }
        else
            throw new IllegalArgumentException("Cannot provide parameters gradient");
    }
        
    protected void updateNormalization() {
        if ( ! (isModel && hasNormalization) )
            super.updateNormalization();
    }

    public boolean isNormalized() {
        if ( isModel && hasNormalization )
            return model.isNormalized();
        return super.isNormalized();
    }
    
    public void normalize(boolean normalize) {
        if ( isModel && hasNormalization )
            model.normalize(normalize);
        else
            super.normalize(normalize);
    }

    public boolean hasAnalyticalNormalization(Dependent dep) {
        return hasNormalization;
    }
    
    public double evaluateAnalyticalNormalization(Dependent dep) {
        return 1;
    }
}
