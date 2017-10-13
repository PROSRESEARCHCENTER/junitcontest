/*
 * PolynomialCoreNorm.java
 *
 * Created on September 5, 2002, 1:40 PM
 */

package hep.aida.ref.function;


/**
 *
 * @author  serbo
 */

/**
 * Normalised Polynomial (Pn) distribution in the form:
 *    f = (1 + p1*x + p2*x*x + ... )/N  , has n-1 paremeters
 *    Normalization N is calculated by the "normalizationAmplitude" method
 */
public class PolynomialCoreNorm extends PolynomialCoreNotNorm {

    public PolynomialCoreNorm(String str) {
	super(1, getDimension(str)-1);
	setTitle("PolynomialCoreNorm::"+str);
	providesNormalization = true;
	String[] pNames = new String[numberOfParameters];
	for (int i=0; i<numberOfParameters; i++) { pNames[i] = "p" + (i+1); }
	setParameterNames(pNames);
    }

    public PolynomialCoreNorm(String str, double[] pVal) {
	super(1, getDimension(str)-1, pVal);
	setTitle("PolynomialCoreNorm::"+str);
	providesNormalization = true;
	String[] pNames = new String[numberOfParameters];
	for (int i=0; i<numberOfParameters; i++) { pNames[i] = "p" + (i+1); }
	setParameterNames(pNames);
    }

    // Value of the function WITHOUT Normalization factor (as if N=1)
    public double functionValue(double[] var) {
	double val = 0;
	for (int i=0; i<numberOfParameters; i++) { val += p[i]*Math.pow(var[0], i+1); }
	val +=1.;
	return val;
    }

    public double[] gradient(double[] var)  {
	double[] tmp = new double[] {0.};
	if (numberOfParameters == 0) return tmp;
	double val = p[0];
	for (int i=1; i<numberOfParameters; i++) { val += i*p[i]*Math.pow(var[0], i); }
	tmp[0] = val;
	return tmp;
    }

    public double[] parameterGradient(double[] var) { 
	double[] tmp = new double[numberOfParameters];
        tmp[0] = 1;
	for (int i=1; i<numberOfParameters; i++) {tmp[i] = Math.pow(var[0], i); } 
	return tmp;
    }


    public double normalizationAmplitude(double[] xMin, double[] xMax) {
	double val = 0;
	val += (xMax[0]-xMin[0]);
	for (int i=0; i<numberOfParameters; i++) {
	    val += (p[i]/(i+2))*(Math.pow(xMax[0], i+2) - Math.pow(xMin[0], i+2));
//	    System.out.println("   i="+i+" \t  val="+val+"\t xMin="+xMin[0]+"\t xMax="+xMax[0]+"\t par="+p[i]);
	}
	return val;
    }

}
