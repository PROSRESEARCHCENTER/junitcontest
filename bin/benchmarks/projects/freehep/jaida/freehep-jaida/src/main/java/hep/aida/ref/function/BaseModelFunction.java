/*
 * BaseModelFunction.java
 *
 * Created on September 4, 2002, 5:26 AM
 */

package hep.aida.ref.function;
import hep.aida.IAnnotation;
import hep.aida.IFunction;
import hep.aida.IModelFunction;
import hep.aida.IRangeSet;
import hep.aida.ref.Annotation;
import hep.aida.ref.ManagedObject;

import java.util.ArrayList;

/**
 *
 * @author  serbo
 */
public class BaseModelFunction extends ManagedObject implements IModelFunction, FunctionDispatcher {
    
    protected String[] varNames;
    protected IAnnotation annotation;
    protected String codeletString;
    protected String title;
    protected FunctionCore function;
    protected FunctionCore functionNotNormalized;
    protected FunctionCore functionNormalized;
    
    //private boolean providesNormalization;
    private boolean isNormalized;
    private boolean normalizationValid;
    private double normalizationAmplitude;
    private RangeSet[] rangeSet;
    
    private ArrayList listeners = new ArrayList();
    
    // One of FunctionCores can be "null"
    protected BaseModelFunction() {
        super(null);
    }
    public BaseModelFunction(String name, String tit, IFunction func) {
        super(name);
        IFunctionCoreNotNorm notNorm = new IFunctionCoreNotNorm(func);
        init(tit, notNorm, null);
        annotation = func.annotation();
        
        setCodeletString(func.codeletString());
        String[] funcVarNames = func.variableNames();
        for (int i=0; i<function.dimension(); i++) {
            varNames[i] = funcVarNames[i];
        }
    }
    public BaseModelFunction(String name, String title, FunctionCore notNorm, FunctionCore norm) {
        super(name);
        init(title, notNorm, norm);
    }
    protected void init(String tit, FunctionCore notNorm, FunctionCore norm) {
        if (notNorm == null && norm == null)
            throw new IllegalArgumentException("Normalized and NotNormalized FunctionCores can not both be null");
        
        annotation = new Annotation();
        annotation.addItem(Annotation.titleKey,"Title", true);
        if (tit != null) setTitle(tit);
        
        codeletString = title;
        
        functionNotNormalized = notNorm;
        functionNormalized = norm;
        
        if (notNorm != null) {
            isNormalized = false;
            function = functionNotNormalized;
            //providesNormalization = false;
            normalizationValid = true;
            normalizationAmplitude = 1.;
        } else {
            isNormalized = true;
            function = functionNormalized;
            //providesNormalization = true;
            normalizationValid = false;
            normalizationAmplitude = 1.;
        }
        
        rangeSet = new RangeSet[function.dimension()];
        varNames = new String[function.dimension()];
        for (int i=0; i<function.dimension(); i++) {
            varNames[i] = "x" + i;
            rangeSet[i] = new RangeSet();
        }
    }
    
    public FunctionCore core() {
        return function;
    }
    
    public int dimension() { return function.dimension(); }
    
    public int numberOfParameters() { return function.numberOfParameters(); }
    
    public double functionValue(double[] var) { return function.functionValue(var); }
    
    public final double value(double[] var) {
        if (!normalizationValid) {
            calculateNormalizationAmplitude();
            //System.out.print("   Value = " + normalizationAmplitude * function.functionValue(var)+", var = "+var[0]);
        }
        double fVal = function.functionValue(var);
//        System.out.println("&&& BaseModelFunction value "+fVal+" "+1./normalizationAmplitude);
        double val =  normalizationAmplitude * fVal;
        if (isNormalized && val<0) val = 0.;
        return val;
    }
    
    public IAnnotation annotation() { return annotation; }
    
    public String variableName(int i)  { return varNames[i]; }
    
    public String[] variableNames() { return varNames; }
    
    public String[] parameterNames() { return function.parameterNames(); }
    
    public int indexOfParameter(String name) { return function.indexOfParameter(name);	}
    
    public void setParameters(double[] params) {
        //System.out.print("\nSetting parameters:  ");
        //for (int i=0; i<numberOfParameters(); i++) { System.out.print(params[i]+"   "); }
        //System.out.print("\n");
        if (isNormalized) normalizationValid = false;
        function.setParameters(params);
        notifyFunctionChanged(new FunctionChangedEvent( FunctionChangedEvent.PARAMETER_VALUE_CHANGED ) );
    }
    
    public void setParameter(String name, double x) throws IllegalArgumentException {
        if (isNormalized) normalizationValid = false;
        function.setParameter(name, x);
        notifyFunctionChanged(new FunctionChangedEvent( FunctionChangedEvent.PARAMETER_VALUE_CHANGED ) );
    }
    
    public double[] parameters() { return function.parameters(); }
    
    public double parameter(String name) {
        return function.parameter(name);
    }
    
    public boolean isEqual(IFunction f) {
        throw new UnsupportedOperationException("This method is not implemented yet");
    }
    
    public boolean providesGradient() { return function.providesGradient(); }
    
    public double[] gradient(double[] x) {
        double[] val = new double[dimension()];
        if (function.providesGradient()) {
            if (!normalizationValid) {
                calculateNormalizationAmplitude();
            }
            val = function.gradient(x);
            for (int i=0; i<val.length; i++) val[i] =  normalizationAmplitude*val[i];
            //System.out.print("gradient: Value = " + val[0] +", var = "+x[0]);
        } else throw new UnsupportedOperationException("This function does not provide gradient");
        //else return numericGradient(x);
        
        return val;
    }
    
    public String codeletString() { return codeletString; }
    
    public void setCodeletString(String codelet) { codeletString = codelet; }
    
    public String normalizationParameter() {
        throw new UnsupportedOperationException("This has not been implemented yet");
    }
    
    
    // IModelFunction methods
    public boolean providesNormalization() { return function.providesNormalization(); }
    
    public void normalize(boolean on) {
        boolean notify = on != isNormalized;
        if (on) {
            if (functionNormalized == null)
                throw new IllegalArgumentException("This function can not be converted into Normalized form!");
            function = functionNormalized;
            isNormalized = true;
            normalizationValid = false;
            normalizationAmplitude = 1.;
        }
        else {
            if (functionNotNormalized == null)
                throw new IllegalArgumentException("This function can not be converted into Not-Normalized form!");
            function = functionNotNormalized;
            isNormalized = false;
            normalizationValid = true;
            normalizationAmplitude = 1.;
        }
        if ( notify )
            notifyFunctionChanged(new FunctionChangedEvent( FunctionChangedEvent.RANGE_CHANGED ) );
    }
    
    public boolean isNormalized() { return isNormalized; }
    
    public double[] parameterGradient(double[] x) {
        double[] val = new double[numberOfParameters()];
        if (function.providesParameterGradient()) {
            if (!normalizationValid) {
                calculateNormalizationAmplitude();
            }
            val = function.parameterGradient(x);
            for (int i=0; i<val.length; i++) val[i] =  normalizationAmplitude*val[i];
            //System.out.print("parameterGradient: Value = " + val[0] +", var = "+x[0]);
        } else throw new UnsupportedOperationException("This function does not provide parameter gradient");
        //else return numericParameterGradient(x);
        return val;
    }
    
    public boolean providesParameterGradient() { return function.providesParameterGradient(); }
    
    public IRangeSet normalizationRange(int iAxis) { return rangeSet[iAxis]; }
    
    public void includeNormalizationAll() {
        if (isNormalized) normalizationValid = false;
        for (int i=0; i<dimension(); i++) rangeSet[i].includeAll();
        notifyFunctionChanged(new FunctionChangedEvent( FunctionChangedEvent.RANGE_CHANGED ) );
    }
    
    public void excludeNormalizationAll() {
        if (isNormalized) normalizationValid = false;
        for (int i=0; i<dimension(); i++) rangeSet[i].excludeAll();
        notifyFunctionChanged(new FunctionChangedEvent( FunctionChangedEvent.RANGE_CHANGED ) );
    }

    public double getNormalizationAmplitude() {
        return normalizationAmplitude;
    }

    // Extra methods
    public void calculateNormalizationAmplitude() {
        normalizationValid = true;
        double val = 0;
        normalizationAmplitude = 1;
        if (!function.providesNormalization()) {
            if ( dimension() == 1 ) val = FunctionIntegrator.integralTrapezoid(this);
            else val = FunctionIntegrator.integralMC(this);
        }
        else {            
            if (dimension() == 1) {
                double[] xMax = rangeSet[0].upperBounds();
                double[] xMin = rangeSet[0].lowerBounds();
                for (int k=0; k<rangeSet[0].size(); k++) {
                    val += function.normalizationAmplitude(xMin, xMax);
                }
            } else if (dimension() == 2) {
                double[] xMax = new double[2];
                double[] xMin = new double[2];
                
                double[] xMax0 = rangeSet[0].upperBounds();
                double[] xMin0 = rangeSet[0].lowerBounds();
                
                double[] xMax1 = rangeSet[1].upperBounds();
                double[] xMin1 = rangeSet[1].lowerBounds();
                
                for (int k=0; k<rangeSet[0].size(); k++) {
                    xMin[0] = xMin0[k];
                    xMax[0] = xMax0[k];
                    
                    for (int j=0; j<rangeSet[1].size(); j++) {
                        xMin[1] = xMin1[j];
                        xMax[1] = xMax1[j];
                        val += function.normalizationAmplitude(xMin, xMax);
                    }
                }
            } else if (dimension() == 3) {
                double[] xMax = new double[3];
                double[] xMin = new double[3];
                
                double[] xMax0 = rangeSet[0].upperBounds();
                double[] xMin0 = rangeSet[0].lowerBounds();
                
                double[] xMax1 = rangeSet[1].upperBounds();
                double[] xMin1 = rangeSet[1].lowerBounds();
                
                double[] xMax2 = rangeSet[2].upperBounds();
                double[] xMin2 = rangeSet[2].lowerBounds();
                
                for (int k=0; k<rangeSet[0].size(); k++) {
                    xMin[0] = xMin0[k];
                    xMax[0] = xMax0[k];
                    
                    for (int j=0; j<rangeSet[1].size(); j++) {
                        xMin[1] = xMin0[j];
                        xMax[1] = xMax0[j];
                        val += function.normalizationAmplitude(xMin, xMax);
                        for (int i=0; i<rangeSet[2].size(); i++) {
                            xMin[2] = xMin0[i];
                            xMax[2] = xMax0[i];
                            val += function.normalizationAmplitude(xMin, xMax);
                        }
                    }
                }
            } else
                throw new IllegalArgumentException("Temporary support only up to 3 dimensions");
            
        }
        normalizationAmplitude = 1./val;
    }
    
    public String title() {
        String t = (annotation != null) ? annotation.value(Annotation.titleKey) : title;
        return t;
    }
    
    public void setTitle(String t) {
        title = t;
        if (annotation != null) annotation.setValue(Annotation.titleKey, title);
        notifyFunctionChanged(new FunctionChangedEvent( FunctionChangedEvent.TITLE_CHANGED ) );
    }
    
    public boolean setParameterNames(String[] params) { 
        boolean result = function.setParameterNames(params); 
        notifyFunctionChanged(new FunctionChangedEvent( FunctionChangedEvent.PARAMETER_NAME_CHANGED ) );
        return result;
    }
    
    public double[] numericGradient(double[] x) {
        throw new UnsupportedOperationException("Numeric Gradient is not implemented yet");
    }
    public double[] numericParameterGradient(double[] x) {
        throw new UnsupportedOperationException("Numeric Parameter Gradient is not implemented yet");
    }
    public RangeSet[] getRangeSet() { return rangeSet; }
    
    public String toString() {
        String str = "BaseModelFunction:  Title="+title()+", name="+name();
        
        str += "\n\tDimension: "+dimension()+", number of parameters: "+numberOfParameters()+", Codelet String: " + codeletString();
        
        str += "\n\tVariable Names: ";
        String[] varNames = variableNames();
        for (int i=0; i<dimension(); i++) str += varNames[i] + ", ";
        
        str += "\t Parameters: ";
        String[] parNames  = parameterNames();
        double[] parValues = parameters();
        for (int i=0; i<numberOfParameters(); i++) str += parNames[i] + "=" + parValues[i] + ", ";
        
        str += "\n\tProvides Gradient: " + providesGradient();
        str += ",  Provides Parameter Gradient: " + providesParameterGradient();
        str += ",  Provides Normalization: " + providesNormalization();
        
        return str;
    }
    
    public void addFunctionListener(FunctionListener listener) {
        listeners.add(listener);
    }
    
    public void removeFunctionListener(FunctionListener listener) {
        listeners.remove(listener);
    }

    void notifyFunctionChanged(FunctionChangedEvent event) {
        for ( int i = 0; i < listeners.size(); i++ )
            ( (FunctionListener) listeners.get(i) ).functionChanged(event);
    }
    
}
