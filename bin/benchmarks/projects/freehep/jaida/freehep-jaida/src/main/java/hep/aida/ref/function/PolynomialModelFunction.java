/*
 * PolynomialModelFunction.java
 *
 * Created on September 3, 2002, 4:07 AM
 */

package hep.aida.ref.function;


/**
 *
 * @author  serbo
 */
public class PolynomialModelFunction extends AbstractDevModelFunction {

    public PolynomialModelFunction(String str) {
	super();

	if(!str.toLowerCase().startsWith("p"))
	    throw new IllegalArgumentException("PolynomialModelFunction Function Qualifier must start with \"P\"");
	int dim = Integer.parseInt(str.substring(1));
	setDimension(1);
	setNumberOfParameters(dim+1);

	setCodeletString("PolynomialModelFunction of power " + dim);
	setTitle(str);
    }

    public int dimension() { return dimension; }

    public int numberOfParameters() { return p.length; }

    public double functionValue(double[] var) {
	double val = 0;
	for (int i=1; i<numberOfParameters(); i++) { val += p[i]*Math.pow(var[0], i); }
	return val+p[0];
    }


}
