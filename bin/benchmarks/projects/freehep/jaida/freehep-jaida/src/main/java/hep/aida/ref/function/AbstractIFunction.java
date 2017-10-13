/*
 * AbstractIFunction.java
 *
 * Created on February 18, 2004, 12:02 PM
 */
package hep.aida.ref.function;

import hep.aida.IModelFunction;
import hep.aida.IRangeSet;
import hep.aida.ref.Annotation;
import hep.aida.ref.ManagedObject;

import java.util.ArrayList;


/**
 * AbstractIFunction is implementation of the IFunction.
 * User has to implement "value" method.  
 * @author  serbo
 */
public abstract class AbstractIFunction extends ManagedObject implements IModelFunction, Cloneable, FunctionDispatcher {
    

    protected String[] variableNames;
    protected String[] parameterNames;
    protected String codeletString; 
    
    private Annotation annotation;
    private int dimension;
    private int numberOfParameters;
    private ArrayList listeners = new ArrayList();
    
    private RangeSet[] rangeSet;

    protected double[] p;
    protected double[] gradient;
    
    /** No-argument constructor to be used for cloning.
     *  Created function can not be used directly
     */
    public AbstractIFunction() {
        super("");
        String className = this.getClass().getName();
        codeletString = "codelet:"+className+":classPath:";               
    }
    
    
    /** Creates a new instance of AbstractIFunction 
     *  with default variable names (x0, x1, ...) 
     *  and default parameter names (p0, p1, ...)
     */
    public AbstractIFunction(String title, int dimension, int numberOfParameters) {
        this();
        variableNames = new String[dimension];
        for (int i=0; i<dimension; i++) { variableNames[i] = "x"+i; }
        
        parameterNames = new String[numberOfParameters];
        for (int i=0; i<numberOfParameters; i++) { parameterNames[i] = "par"+i; }

        init(title);
    }
    
    /** Creates a new instance of AbstractIFunction 
     *  with specified variable and parameter names.
     *  This constructor must be implemented by all subclasses
     *  in order for codelet-based creation to work properly
     */
    public AbstractIFunction(String[] variableNames, String[] parameterNames) {
        this("", variableNames, parameterNames);
    }
    public AbstractIFunction(String title, String[] variableNames, String[] parameterNames) {
        this();
        this.variableNames = variableNames;
        this.parameterNames = parameterNames;

        init(title);        
    }
    
    public Object clone() {
        try {
            AbstractIFunction copy = (AbstractIFunction) super.clone();
            copy.codeletString = codeletString;     
            copy.dimension = dimension;
            copy.numberOfParameters = numberOfParameters;
            
            copy.variableNames  = (String[]) variableNames.clone();
            copy.parameterNames = (String[]) parameterNames.clone();
            copy.p = (double[]) p.clone();
            copy.gradient = (double[]) gradient.clone();
            copy.listeners = (ArrayList) listeners.clone();
            copy.annotation = new Annotation(annotation);
            copy.rangeSet = new RangeSet[dimension()];
            for (int i=0; i<dimension(); i++) {
                double[] min = (double[]) rangeSet[i].lowerBounds().clone();
                double[] max = (double[]) rangeSet[i].upperBounds().clone();
                copy.rangeSet[i] = new RangeSet(min, max);
            }            
            
            return copy;
        } catch (Exception e) {
            throw new RuntimeException("Error while cloning "+codeletString, e);
        }
    }

    
    // Service methods
    
    protected void init(String title) {
        dimension = variableNames.length;
        numberOfParameters = parameterNames.length;
        p = new double[numberOfParameters];
        gradient = new double[dimension];
        
        rangeSet = new RangeSet[dimension()];
        for (int i=0; i<dimension(); i++)
            rangeSet[i] = new RangeSet();

        if (title == null) title = "";
        annotation = new Annotation();
        annotation.addItem(Annotation.titleKey, title);
        annotation.setFillable(true);
        
        String className = this.getClass().getName();        
        String vn = "";
        String pn = "";
        for (int i=0; i<variableNames.length; i++)  vn = vn + "," + variableNames[i];
        for (int i=0; i<parameterNames.length; i++) pn = pn + "," + parameterNames[i];
        codeletString = "codelet:"+className+":classPath:" + vn + ":" + pn; 
    }
    
    public void setCodeletString(String str) { codeletString = str; }
    
    // IFunction methods
    
    /** Provide value for your function here. Something like:
     *  return p[0]+p[1]*v[0]+p[2]*v[0]*v[0]; 
     */
    public abstract double value(double[] v);
    
    
    public boolean providesGradient() {
        return false;
    }
    
    public double[] gradient(double[] values) {
        return gradient;
    }
    
    public String codeletString() {
        return codeletString;
    }
    
    public hep.aida.IAnnotation annotation() {
        return annotation;
    }
    
    public int dimension() {
        return dimension;
    }
    
    public int indexOfParameter(String str) {
        int index = -1;
        for (int i=0; i<numberOfParameters; i++) {
            if (str.equals(parameterNames[i])) {
                index = i;
                break;
            }
        }        
        return index;
    }
    
    public int numberOfParameters() {
        return numberOfParameters;
    }
    
    public double parameter(String str) {
        int index = indexOfParameter(str);
        if (index == -1) throw new IllegalArgumentException("Parameter \""+str+"\" does not exist");
        return p[index];
    }
    
    public String[] parameterNames() {
        return parameterNames;
    }
    
    public double[] parameters() {
        return p;
    }
    
    public void setParameter(String str, double param) throws java.lang.IllegalArgumentException {
        int index = indexOfParameter(str);
        if (index == -1) throw new IllegalArgumentException("Parameter \""+str+"\" does not exist");
        p[index] = param;
        notifyFunctionChanged(new FunctionChangedEvent( FunctionChangedEvent.PARAMETER_VALUE_CHANGED ) );
    }
    
    public void setParameters(double[] pars) throws java.lang.IllegalArgumentException {        
        if (pars.length != numberOfParameters) 
            throw new IllegalArgumentException("Wrong number of input parameters:"+pars.length+", must be "+numberOfParameters);
        for (int i=0; i<numberOfParameters; i++) p[i] = pars[i];
        notifyFunctionChanged(new FunctionChangedEvent( FunctionChangedEvent.PARAMETER_VALUE_CHANGED ) );
    }
    
    public void setTitle(String title) throws java.lang.IllegalArgumentException {
        annotation.setValue(Annotation.titleKey, title);
        notifyFunctionChanged(new FunctionChangedEvent( FunctionChangedEvent.TITLE_CHANGED ) );
    }
    
    public String title() {
        return annotation.value(Annotation.titleKey);
    }
    
    public String variableName(int index) {
        return variableNames[index];
    }
    
    public String[] variableNames() {
         return variableNames;
    }
    
    public void excludeNormalizationAll() {
        for (int i=0; i<dimension(); i++) rangeSet[i].excludeAll();
        notifyFunctionChanged(new FunctionChangedEvent( FunctionChangedEvent.RANGE_CHANGED ) );
    }
    
    public void includeNormalizationAll() {
        for (int i=0; i<dimension(); i++) rangeSet[i].includeAll();
        notifyFunctionChanged(new FunctionChangedEvent( FunctionChangedEvent.RANGE_CHANGED ) );
    }
    
    public boolean isNormalized() {
        return false;
    }
    
    public IRangeSet normalizationRange(int iAxis) { 
        return rangeSet[iAxis]; 
    }
    
    public void normalize(boolean param) {
    }
    
    public double[] parameterGradient(double[] values) {
        return null;
    }
    
    public boolean providesNormalization() {
        return false;
    }
    
    public boolean providesParameterGradient() {
        return false;
    }
    
    public String normalizationParameter() {
        throw new UnsupportedOperationException("This method needs to be overwritten to perform unbinned fits");        
    }
    
    
    public boolean isEqual(hep.aida.IFunction iFunction) {
	throw new UnsupportedOperationException("Not implemented");
    }
    
    
    public void addFunctionListener( FunctionListener listener ) {
        listeners.add(listener);
    }
    
    public void removeFunctionListener( FunctionListener listener ) {
        listeners.remove(listener);
    }

    void notifyFunctionChanged(FunctionChangedEvent event) {
        for ( int i = 0; i < listeners.size(); i++ )
            ( (FunctionListener) listeners.get(i) ).functionChanged(event);
    }    
    
}
