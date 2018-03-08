/*
 *ExponentialCore2DNotNorm .java
 *
 * Created on October 23, 2002, 11:04 AM
 */

package hep.aida.ref.function;


/**
 *
 * @author  AIDA Team @ SLAC
 */

/**
 * Not normalised Gaussian 2D (G2) distribution in the form:
 *    f = (amplitude)*exp(-(x-meanX)^2/(2*sigmaX^2))*exp(-(y-meanY)^2/(2*sigmaY^2))  has 5 parameters
 * 
 */
public class GaussianCore2DNotNorm extends FunctionCore {

    protected boolean providesNormalization;
    protected final double r2 = Math.sqrt(2);

    public GaussianCore2DNotNorm(String str) {
	super(2, 5, new double[] {1., 0., 1., 0., 1.});
	setTitle("GaussianCore2DNotNorm::"+str);
	providesNormalization = false;

	String[] names = new String[] { "amplitude", "meanX", "sigmaX", "meanY", "sigmaY" };
	setParameterNames(names);
    }

    public GaussianCore2DNotNorm(String str, double[] pVal) {
	super(2, 5, pVal);
	setTitle("GaussianCore2DNotNorm::"+str);
	providesNormalization = false;

	String[] names = new String[] { "amplitude", "meanX", "sigmaX", "meanY", "sigmaY" }; 
	setParameterNames(names);
    }

    // Value of the function WITHOUT Normalization factor (as if N=1)
    public double functionValue(double[] var) {
	return p[0]*Math.exp( -Math.pow( var[0] - p[1], 2 )/(2*Math.pow( p[2], 2 )) ) *
	            Math.exp( -Math.pow( var[1] - p[3], 2 )/(2*Math.pow( p[4], 2 )) );
    }

    // Each concrete FunctionCore has to implement those methods that deal with Gradients and Normalization
    public boolean providesGradient() { return true; }

    public double[] gradient(double[] var)  {
	return new double[] { functionValue(var)*(-2.)*(var[0] - p[1])/(2*Math.pow( p[2], 2 )), 
			      functionValue(var)*(-2.)*(var[1] - p[3])/(2*Math.pow( p[4], 2 )) };
    }

    public boolean providesParameterGradient() { return true; }

    public double[] parameterGradient(double[] var) {
	double f = functionValue(var);
	return new double[] {  f/p[0],
			       f*2.*(var[0] - p[1])/(2*Math.pow( p[2], 2 )),
			       f*2.*(var[0] - p[1])/(2*Math.pow( Math.abs(p[2]), 3 )),
			       f*2.*(var[1] - p[3])/(2*Math.pow( p[4], 2 )),
			       f*2.*(var[1] - p[3])/(2*Math.pow( Math.abs(p[4]), 3 )) };
    }

    public boolean providesNormalization() { return providesNormalization; }

    public double normalizationAmplitude(double[] xMin, double[] xMax) {
	throw new UnsupportedOperationException(title() + " *****  Can not calculate normalization for a not normalized function");
    }

}
