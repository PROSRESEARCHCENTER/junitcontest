package hep.aida.ref.function;

import hep.aida.IAnnotation;
import hep.aida.IFunction;
import hep.aida.ref.Annotation;
import hep.aida.ref.ManagedObject;

import java.util.ArrayList;

/**
 *
 * @author The FreeHEP team @ SLAC
 *
 */
public class SumOfFunctions extends ManagedObject implements IFunction {
        
    private ArrayList functions;
    private boolean hasFunctions = false;
    private int dimension;
    private Annotation annotation = new Annotation();
    private String[] parameters;
    private double[] parValues;
    private String name;
    private String title;
    
    public SumOfFunctions(String name, ArrayList functions) {
        super(name);
        this.functions = functions;
        this.name = name;
        updateFunction();
    }
    
    void updateFunction() {
        if ( nFunctions() > 0 ) {
            hasFunctions = true;
            dimension = function(0).dimension();
            int nPars = function(0).numberOfParameters();
            for ( int i = 1; i < nFunctions(); i++ ) {
                if ( function(i).dimension() != dimension )
                    throw new IllegalArgumentException("To be added functions must have the same dimension");
                nPars += function(i).numberOfParameters();
            }
            
            parameters = new String[nPars];
            parValues = new double[nPars];
            int count = 0;
            for ( int i = 0; i < nFunctions(); i++ ) {
                String[] pars = function(i).parameterNames();
                for ( int j = 0; j < pars.length; j++ ) {
                    String parName = pars[j];
                    for ( int k = 0; k < parameters.length; k++ )
                        if ( parName.equals( parameters[k] ) )
                            parName += "_"+indexOfFunction(function(i));
                    parameters[count++] = parName;
                }
            }
            title = codeletString();
        } else {
            hasFunctions = false;
            parameters = null;
            parValues = null;
            title = "";
        }
    }
    
    public IAnnotation annotation() {
        return (IAnnotation) annotation;
    }    
    
    public String normalizationParameter() {
        throw new UnsupportedOperationException();
    }
    
    public String codeletString() {
        if ( hasFunctions ) {
            String codelet = FunctionCatalog.prefix+CodeletUtils.modelFromCodelet(function(0).codeletString());
            for ( int i = 1; i < nFunctions(); i++ )
                codelet +=" + "+CodeletUtils.modelFromCodelet(function(i).codeletString());
            codelet += ":catalog";
            return codelet;
        }
        return null;
    }
    
    public int dimension() {
        return dimension;
    }
    
    public double[] gradient(double[] values) {
        if ( ! providesGradient() )
            throw new IllegalArgumentException("This function does not provide the gradient.");
        double[] gradient = function(0).gradient(values);
        for ( int i = 1; i < nFunctions(); i++ ) {
            double[] tmpGrad = function(i).gradient(values);
            for ( int j = 0; j < dimension(); j++ )
                gradient[j] += tmpGrad[j];
        }
        return gradient;
    }
    
    public int indexOfParameter(String str) {
        for ( int i = 0; i < parameters.length; i++ )
            if ( str.equals( parameters[i] ) )
                return i;
        throw new IllegalArgumentException("Illegal parameter name "+str);
    }
    
    public boolean isEqual(hep.aida.IFunction iFunction) {
        return false;
    }
    
    public int numberOfParameters() {
        return parameters.length;
    }
    
    public double parameter(String str) {
        int index = indexOfParameter(str);
        for ( int i = 0; i < nFunctions(); i++ ) {
            IFunction func = function(i);
            int nPars = func.numberOfParameters();
            if ( index > nPars-1 )
                index -= nPars;
            else
                return func.parameter( parameterNames()[index] );
        }
        throw new IllegalArgumentException("Illegal parameter "+str);
    }
    
    public String[] parameterNames() {
        return parameters;
    }
    
    public double[] parameters() {
        int count = 0;
        for ( int i = 0; i < nFunctions(); i++ ) {
            double[] pars = function(i).parameters();
            for ( int j = 0; j < pars.length; j++ )
                parValues[count++] = pars[j];
        }
        return parValues;
    }
    
    public boolean providesGradient() {
        for ( int i = 0; i < nFunctions(); i++ )
            if ( ! function(i).providesGradient() )
                return false;
        return true;
    }
    
    public void setParameter(String str, double param) throws java.lang.IllegalArgumentException {
        int index = indexOfParameter(str);
        for ( int i = 0; i < nFunctions(); i++ ) {
            IFunction func = function(i);
            int nPars = func.numberOfParameters();
            if ( index > nPars-1 )
                index -= nPars;
            else {
                func.setParameter( func.parameterNames()[index], param );
                return;
            }
        }
    }
    
    public void setParameters(double[] values) throws java.lang.IllegalArgumentException {
        if ( values.length != numberOfParameters() )
            throw new IllegalArgumentException("Illegal size "+values.length+". It has to be equal to the number of parameters "+numberOfParameters()+".");
        for ( int i = 0; i < values.length; i++ )
            setParameter(parameters[i], values[i]);
    }
    
    public void setTitle(String str) throws java.lang.IllegalArgumentException {
        this.title = str;
    }
    
    public String title() {
        return title;
    }
    
    public double value(double[] values) {
        double result = 0;
        for ( int i = 0; i < nFunctions(); i++ )
            result += function(i).value(values);
        return result;
    }
    
    public String variableName(int param) {
        return function(0).variableName(param);
    }
    
    public String[] variableNames() {
        return function(0).variableNames();
    }
    
    public void addFunction(IFunction func) {
        functions.add(func);
        updateFunction();
    }

    public void removeFunction(IFunction func) {
        functions.remove(func);
        updateFunction();
    }
    
    public void removeAllFunctions() {
        functions.clear();
        updateFunction();
    }

    public IFunction function(int index) {
        return (IFunction) functions.get(index);
    }
    
    public int indexOfFunction(IFunction function) {
        return functions.indexOf(function);
    }
    
    public int nFunctions() {
        return functions.size();
    }
    
    public boolean containsFunction(IFunction function) {
        return functions.contains(function);
    }
}
