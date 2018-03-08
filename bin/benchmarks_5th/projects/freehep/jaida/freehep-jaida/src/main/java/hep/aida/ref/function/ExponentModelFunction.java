/*
 * ExponentModelFunction.java
 *
 * Created on September 3, 2002, 4:07 AM
 */

package hep.aida.ref.function;

/**
 *
 * @author  serbo
 */
public class ExponentModelFunction extends AbstractDevModelFunction {

    public ExponentModelFunction(String str) {
	super();

	if(!str.toLowerCase().startsWith("e"))
	    throw new IllegalArgumentException("Exponent Function Qualifier must start with \"e\"");
	int dim = Integer.parseInt(str.substring(1));
	setDimension(dim);
	setNumberOfParameters(dim+1);

	setCodeletString("ExponentModelFunction");
	setNormalization(true, false);
    }

    public int dimension() { return dimension; }

    public int numberOfParameters() { return p.length; }

    public double functionValue(double[] var) {
	double val = 1;
	for (int i=0; i<dimension; i++) { val = val*Math.exp(p[i]*var[i]); }
	return val*p[numberOfParameters()-1];
    }

}
