/*
 *ExponentialCoreNorm .java
 *
 * Created on October 5, 2002, 1:41 PM
 */

package hep.aida.ref.function;

import org.apache.commons.math.special.Erf;


/**
 *
 * @author  serbo
 */

/**
 * Not normalised Gaussian (G) distribution in the form:
 *    f = (1/N)*exp(-(x-mean)^2/(2*sigma^2))  has 2 parameters
 * Normalization Amplitude is calculated by the "normalizationAmplitude" method
 */
public class GaussianCoreNorm extends FunctionCore {

    protected boolean providesNormalization;
    protected final double r2 = Math.sqrt(2);

    public GaussianCoreNorm(String str) {
	super(1, 2, new double[] {0., 1.});
	setTitle("GaussianCoreNorm::"+str);
	providesNormalization = true;

	String[] names = new String[] { "mean", "sigma" };
	setParameterNames(names);
    }

    public GaussianCoreNorm(String str, double[] pVal) {
	super(1, 2, pVal);
	setTitle("GaussianCoreNorm::"+str);
	providesNormalization = true;

	String[] names = new String[] { "mean", "sigma" };
	setParameterNames(names);
    }

    // Value of the function WITHOUT Normalization factor (as if N=1)
    public double functionValue(double[] var) {
	return Math.exp( -Math.pow( var[0] - p[0], 2 )/(2*Math.pow( p[1], 2 )) );
    }

    // Each concrete FunctionCore has to implement those methods that deal with Gradients and Normalization
    public boolean providesGradient() { return true; }

    public double[] gradient(double[] var)  {
	return new double[] { functionValue(var)*(-2.)*(var[0] - p[0])/(2*Math.pow( p[1], 2 )) };
    }

    public boolean providesParameterGradient() { return true; }

    public double[] parameterGradient(double[] var) {
	double y = functionValue(var);
	return new double[] {  y*2.*(var[0] - p[0])/(2*Math.pow( p[1], 2 )),
			       y*2.*Math.pow(var[0] - p[0],2)/(2*Math.pow( Math.abs(p[1]), 3 )) };
    }

    public boolean providesNormalization() { return providesNormalization; }

    public double normalizationAmplitude(double[] xMin, double[] xMax) {
        try {

            double ue = Erf.erf( (xMax[0]-p[0])/(r2*p[1]) );
            double le = Erf.erf( (xMin[0]-p[0])/(r2*p[1]) );
            return Math.sqrt( Math.PI/2 )*p[1]*( ue - le );
        } catch (Exception e) {
            System.out.println("Problem evaluating normalization between "+xMin[0]+" and "+xMax[0]);
        }
        return Double.NaN;
    }

}
