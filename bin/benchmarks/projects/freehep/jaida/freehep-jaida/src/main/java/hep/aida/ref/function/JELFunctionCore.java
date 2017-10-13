/*
 * FunctionCore.java
 *
 * Created on September 4, 2002, 6:15 AM
 */

package hep.aida.ref.function;


/**
 *
 * @author  serbo
 */

// Creates FunctionCore from a String expression
public  class JELFunctionCore extends FunctionCore {

    private String expression;
    private boolean providesGradient;

    private JELCompiledExpression compExpression;
    private JELCompiledExpression[] compGradient;

    public JELFunctionCore(int dim, int nPar, String expr, String[] pNames, String[] gradient) {
	super(dim, nPar);

	if (expr != null && !expr.equals("")) expression = expr;
	else
	    throw new IllegalArgumentException("Can not create function from an empty script!");

	// Check parameter names
	if (pNames!= null && nPar != pNames.length)
	    throw new IllegalArgumentException("Number of parameters ("+nPar+
					       ") is different from number of parameter names ("+pNames.length+")");
	// Create JEL compiled expression
	compExpression = new JELCompiledExpression(dimension, numberOfParameters, expression, pNames);

	// Setup and compile gradients
	compGradient = null;
	if (gradient != null && gradient.length > 0) { 
	    providesGradient = true; 
	    compGradient = new JELCompiledExpression[dim];
	    for (int i=0; i<dim; i++)  { compGradient[i] = new JELCompiledExpression(dimension, numberOfParameters, gradient[i], pNames); }
	} else {
	    providesGradient = false;
	}

	// Set parameter names
	if (pNames != null) setParameterNames(pNames);
    }

    // Value of the function WITHOUT Normalization factor (as if N=1)
    public double functionValue(double[] var) { return compExpression.evaluate(var, parameters()); }

    public boolean setParameterNames(String[] params) { 
	super.setParameterNames(params);
	compExpression.setParameterNames(params);
	if (compGradient != null) {
	    for (int i=0; i<dimension; i++) { compGradient[i].setParameterNames(params); }
	}
	return true;
    }

    // Each concrete FunctionCore has to implement those methods that deal with Gradients and Normalization
    public boolean providesGradient() { return providesGradient; }
    
    public double[] gradient(double[] x) { 
	
	if (compGradient == null || !providesGradient)
	throw new UnsupportedOperationException("This function does not provide gradient");

	double[] grad = new double[dimension];
	for (int i=0; i<dimension; i++)  { grad[i] = compGradient[i].evaluate(x, parameters()); }

	return grad;
    }

    public boolean providesParameterGradient() { return false; }

    public double[] parameterGradient(double[] x) {
	throw new UnsupportedOperationException("JELFunctionCore does not provide parameter gradient");
    }

    public boolean providesNormalization() { return false; }

    public double normalizationAmplitude(double[] xMin, double[] xMax)  {
	throw new UnsupportedOperationException("JELFunctionCore does not provide normalization");
    }

}
