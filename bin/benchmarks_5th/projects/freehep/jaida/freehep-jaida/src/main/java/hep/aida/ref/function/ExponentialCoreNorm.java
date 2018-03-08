/*
 *ExponentialCoreNorm .java
 *
 * Created on September 6, 2002, 3:28 PM
 */

package hep.aida.ref.function;


/**
 *
 * @author serbo
 */

/**
 * Normalised Exponential (E) distribution in the form:
 *    f = (1/N)*exp((x-origin)/exponent)) has 2 parameters, 
 * Normalization Amplitude N is calculated by the "normalizationAmplitude" method
 */
public class ExponentialCoreNorm extends FunctionCore {

    protected boolean providesNormalization;

    public ExponentialCoreNorm(String str) {
	super(1, 1, new double[] {1.} );
	setTitle("ExponentialCoreNorm::"+str);
	providesNormalization = true;

	String[] names = new String[] { "exponent" };
	setParameterNames(names);
    }

    public ExponentialCoreNorm(String str, double[] pVal) {
	super(1, 1, pVal);
	setTitle("ExponentialCoreNorm::"+str);
	providesNormalization = true;

	String[] names = new String[] { "exponent" };
	setParameterNames(names);
    }

    // Value of the function WITHOUT Normalization factor (as if N=1)
    public double functionValue(double[] var) {
	double val =  Math.exp(var[0]*p[0]);
	return val;
    }

    // Each concrete FunctionCore has to implement those methods that deal with Gradients and Normalization
    public boolean providesGradient() { return true; }

    public double[] gradient(double[] var)  {
	return new double[] { functionValue(var)*p[0] };
    }

    public boolean providesParameterGradient() { return true; }

    public double[] parameterGradient(double[] var) { 
	return new double[] { functionValue(var)*var[0] };
    }

    public boolean providesNormalization() { return providesNormalization; }

    public double normalizationAmplitude(double[] xMin, double[] xMax) {
	double val = 0;
	val = (functionValue(new double[] {xMax[0]}) - functionValue(new double[] {xMin[0]}))/p[0];
	//System.out.println("\t  val="+val+"\t xMin="+xMin[0]+"\t xMax="+xMax[0]+"\t par="+p[0]);
	
	return val;
    } 

}
