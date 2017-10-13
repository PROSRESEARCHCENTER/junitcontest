/*
 * AbstractDevModelFunction.java
 *
 * Created on September 1, 2002, 4:07 AM
 */

package hep.aida.ref.function;
import hep.aida.IAnnotation;
import hep.aida.IFunction;
import hep.aida.IRangeSet;
import hep.aida.ref.Annotation;

import java.util.ArrayList;

/**
 *
 * @author  serbo
 */
public abstract class AbstractDevModelFunction implements hep.aida.dev.IDevModelFunction {

    protected int dimension;
    //private int numberOfParameters;
    protected double[] x;
    protected double[] p;
    protected String[] varNames;
    protected String[] parNames;
    protected IAnnotation annotation;
    protected String codeletString;
    protected String title;
    protected IFunction function;

    protected ArrayList[] min;
    protected ArrayList[] max;

    private boolean providesGradient;
    private double[] gradient;

    private boolean providesParameterGradient;
    private double[] parameterGradient;

    private boolean providesNormalization;
    private boolean isNormalized;
    private boolean normalizationValid;
    private double normalizationAmplitude;

    protected IRangeSet[] rangeSet;

    public AbstractDevModelFunction() {
	init();
    }

    public String normalizationParameter() {
        throw new UnsupportedOperationException("The normalizationParameter() method has not been implemented yet");
    }
    
    
    public abstract int dimension();

    public abstract int numberOfParameters();

    public abstract double functionValue(double[] var);

    private void init() {
	dimension = -1;
	x = null;
	p = null;
	varNames = new String[] {"x0"};
	parNames = new String[] {"p0"};
	annotation = null;
	codeletString = null;
	function = null;
	min = null;
	max = null;
	gradient = null;
	parameterGradient = null;
	
	providesGradient = false;
	providesParameterGradient = false;
	providesNormalization = false;
	isNormalized = false;
	normalizationValid = false;
	normalizationAmplitude = Double.NaN;

	annotation = new Annotation();
	rangeSet = new RangeSet[dimension];
	rangeSet[0] = new RangeSet();
    }

    public final double value(double[] var) { 
	if (normalizationValid) 
	    return normalizationAmplitude * functionValue(var);
	else {
	    normalizationAmplitude = normalizationAmplitude();
	    normalizationValid = true;
	    return normalizationAmplitude * functionValue(var);	    
	}
    }

    public IAnnotation annotation() { return annotation; }

    public String variableName(int i)  { return varNames[i]; }

    public String[] variableNames() { return varNames; }

    public String[] parameterNames() { return parNames; }

    public int indexOfParameter(String name) {
	for (int i=0; i<numberOfParameters(); i++) {
	    if (name.equals(parNames[i])) return i;
	}
	throw new IllegalArgumentException("Function does not have variable named \"" + name + "\"");
    }

    public void setParameters(double[] params) { 
	normalizationValid = false;
	for (int i=0; i<numberOfParameters(); i++) p[i] = params[i]; 
    }

    public void setParameter(String name, double x) throws IllegalArgumentException { 
	normalizationValid = false;
	p[indexOfParameter(name)] = x; 
    }

    public double[] parameters() { return p; }

    public double parameter(String name) { 
	return p[indexOfParameter(name)];
    } 

    public boolean isEqual(IFunction f) {
	throw new UnsupportedOperationException("This method is not implemented yet");
    }

    public boolean providesGradient() { return providesGradient; }

    public double[] gradient(double[] x) {
	return gradient;
    }

    public String codeletString() { return codeletString; }


    // IDevFunction methods
    public void setCodeletString(String codelet) { codeletString = codelet; }

    public void setDimension(int dim) {
	dimension = dim;
	x = new double[dim];
	if (dim != varNames.length) {
	    varNames = new String[dim];
	    min = new ArrayList[dim];
	    max = new ArrayList[dim];

	    for (int i=0; i<dim; i++) {
		min[i] = new ArrayList();
		max[i] = new ArrayList();
		varNames[i] = "x" + i;
	    }
	}
    }

    public void setProvidesGradient(boolean yes) { providesGradient = yes; }

    public boolean setVariableNames(String[] names) {
	if (dimension != names.length)
	    throw new IllegalArgumentException("Number of parameters in the function (" + dimension +
					       ") is not equal to the number of elements in array (" + names.length + ").");
	for (int i=0; i<dimension; i++) { varNames[i] = names[i]; }
	return true;
    }
 

    public void setNumberOfParameters(int parnum) {
	p = new double[parnum];
	if (parnum != parNames.length) parNames = new String[parnum];
	for (int i=0; i<numberOfParameters(); i++) { parNames[i] = "p" + i; }
    }

    public boolean setParameterNames(String[] names) {
	if (numberOfParameters() != names.length)
	    throw new IllegalArgumentException("Number of parameters in the function (" + numberOfParameters() +
					       ") is not equal to the number of elements in array (" + names.length + ").");
	for (int i=0; i<numberOfParameters(); i++) { parNames[i] = names[i]; }
	return true;
    }

    public void setAnnotation(IAnnotation ptr) { annotation = ptr; }


    // IModelFunction methods
    public boolean providesNormalization() { return providesNormalization; }

    public void normalize(boolean on) { 
	if (on && !isNormalized && providesNormalization) {
	    normalizationAmplitude = normalizationAmplitude();
	    isNormalized = true;
	}
    }

    public boolean isNormalized() { return isNormalized; }

    public double[] parameterGradient(double[] x) {
	return parameterGradient;
    }

    public boolean providesParameterGradient() { 
        return providesParameterGradient; 
    }

    public void setNormalizationRange(double rMin, double rMax, int iAxis) {
	normalizationValid = false;

	min = new ArrayList[dimension];
	max = new ArrayList[dimension];

	for (int i=0; i<dimension; i++) {
	    min[i] = new ArrayList();
	    max[i] = new ArrayList();
	}

	min[iAxis].add(new Double(rMin));
	max[iAxis].add(new Double(rMax));
    }

    public void includeNormalizationRange(double xMin, double xMax, int iAxis) {
	normalizationValid = false;

	min[iAxis].add(new Double(xMin));
	max[iAxis].add(new Double(xMax));
    }

    public void excludeNormalizationRange(double xMin, double xMax, int iAxis) {
	normalizationValid = false;
    }

    public void includeNormalizationAll(int iAxis) {
	normalizationValid = false;
    }

    public void excludeNormalizationAll(int iAxis) {
	normalizationValid = false;
    }

    public void includeNormalizationAll() {
	normalizationValid = false;
    }

    public void excludeNormalizationAll() {
	normalizationValid = false;
    }

    public IRangeSet normalizationRange(int iAxis) { return rangeSet[iAxis]; }
    
    // IDevModelFunction methods
  /// Fails if you try to set (NOT provides AND is_normalized).
    public boolean setNormalization(boolean provides, boolean is_normalized) {
	if (!provides && is_normalized)
	    throw new IllegalArgumentException("Function can not be \"normalized\" and not provide normalization at the same time");
	providesNormalization = provides;
	isNormalized = is_normalized;
	return true;
    }

    public void setProvidesParameterGradient(boolean yes) { providesParameterGradient = yes; }


    // Extra methods
    public final double normalizationAmplitude() {
	return normalizationAmplitude(min, max);
    }

    public double normalizationAmplitude(ArrayList[] xMin, ArrayList[] xMax) {
	return 1;
    }

    public String title() { return title; }

    public void setTitle(String t) { title = t; }
}
