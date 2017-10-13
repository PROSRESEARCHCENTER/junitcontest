/*
 *IFunctionCoreNotNorm .java
 *
 * Created on October 28, 2002, 12:53 PM
 */

package hep.aida.ref.function;
import hep.aida.IAnnotation;
import hep.aida.IFunction;
import hep.aida.ref.Annotation;



/**
 *
 * @author  serbo
 */

/**
 * Wrapper around ordinary IFunction
 */
public class IFunctionCoreNotNorm extends FunctionCore {

    protected boolean providesNormalization;
    private IFunction function;

    public IFunctionCoreNotNorm(IFunction func) {
	super(func.dimension(), func.numberOfParameters(), null);
	function = func;
	
	initIFunctionCore();
    }

    public IFunctionCoreNotNorm(IFunction func, double[] pVal) {
	super(func.dimension(), func.numberOfParameters(), pVal);
	function = func;

	initIFunctionCore();
    }
    
    private void initIFunctionCore() {
	String tit = null;
	try {
            IAnnotation an = function.annotation();
	    if (an != null) tit = an.value(Annotation.titleKey);
	} catch (IllegalArgumentException e) { }

	if (tit != null && !tit.equals("")) setTitle(tit);

	providesNormalization = false;

	String[] names = function.parameterNames();
	setParameterNames(names);
    }

    // Value of the function WITHOUT Normalization factor (as if N=1)
    public double functionValue(double[] var) {
	return function.value(var);
    }

    // Each concrete FunctionCore has to implement those methods that deal with Gradients and Normalization
    public boolean providesGradient() { return function.providesGradient(); }

    public double[] gradient(double[] var)  {
	return function.gradient(var);
    }

    public boolean providesParameterGradient() { 
        if ( function instanceof FunctionCore )
            return ( (FunctionCore) function ).providesParameterGradient();
        return false; 
    }

    public double[] parameterGradient(double[] var) {
	throw new UnsupportedOperationException(title() + "IFunctionCore does not provide parameter gradient");
    }

    public boolean providesNormalization() { return providesNormalization; }

    public double normalizationAmplitude(double[] xMin, double[] xMax) {
	throw new UnsupportedOperationException(title() + " *****  Can not calculate normalization for a not normalized function");
    }

    // Overwrite more methods from FunctionCore
    public double[] parameters() { return function.parameters(); }

    public double parameter(String name) { return (function.parameters())[indexOfParameter(name)]; } 

    public void setParameters(double[] params) { 
	//System.out.print("\nSetting parameters:  ");
	//for (int i=0; i<numberOfParameters(); i++) { System.out.print(params[i]+"   "); }
	//System.out.print("\n");
	//for (int i=0; i<numberOfParameters(); i++) { p[i] = params[i]; }
	function.setParameters(params);
    }

    public void setParameter(String name, double x) throws IllegalArgumentException { 
	//p[indexOfParameter(name)] = x;

	String fName = (function.parameterNames())[indexOfParameter(name)];
	function.setParameter(fName, x);
	//System.out.println("Setting parameter:  "+name+" = "+x);
    }

/* FIXME, needs to move to test
    public static void main(String[] args) {

	IFunction f = new hep.aida.ref.test.jaida.TestUserFunction();
	((hep.aida.ext.IManagedFunction) f).setName("TestFunction");
	BaseModelFunction bmf = new BaseModelFunction("BaseName", "BaseTitle", f);

	System.out.println(bmf.toString());

	double[] parValues = new double[] { 2., 3., 5. };
	bmf.setParameters(parValues);

	for (int i=0; i<20; i++) {
	    double x = i*1.;
	    double y = i*2.;
	    double[] var = new double[] { x, y };
	    double valueExp = parValues[0]*x*x + parValues[1]*y + parValues[2];
	    double[] gradientExp = new double[] { 2*parValues[0]*x, parValues[1] };

	    System.out.println(i+"   Value="+bmf.value(var)+", delta="+(bmf.value(var)-valueExp) +
			       ";    grad_X="+bmf.gradient(var)[0]+", delta="+(bmf.gradient(var)[0]-gradientExp[0]) +
			       ";    grad_Y="+bmf.gradient(var)[1]+", delta="+(bmf.gradient(var)[1]-gradientExp[1]) );
	}

	System.out.println(bmf.toString());
    }
*/
}
