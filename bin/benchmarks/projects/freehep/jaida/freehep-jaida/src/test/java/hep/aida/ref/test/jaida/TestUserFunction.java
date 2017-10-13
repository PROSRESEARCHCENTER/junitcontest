package hep.aida.ref.test.jaida;

import hep.aida.IAnnotation;
import hep.aida.IFunction;
import hep.aida.ext.IManagedFunction;

public class TestUserFunction implements IManagedFunction {

    private String name;
    private String codelet;
    private double[] par;
    private String[] parNames;
    private double[] var;
    private String[] varNames;

    private String title = "";

    public TestUserFunction() {
	name = null;
	codelet = "hep.aida.ref.test.TestUserFunction";
	par = new double[] { 1.1, 2.2, 3.3 };
	parNames = new String[] {"pp0", "pp1", "pp2" };
	var = new double[] { 11.1, 22.2};
	varNames = new String[] {"xx0", "xx1" };
    }
    
    public String type() { return "IFunction"; }
    
    public String name() { return name; }
    public void setName(String str) { name = str; }

    public double value(double[] x) { return par[0]*x[0]*x[0] + par[1]*x[1] + par[2]; }

    public int dimension() {return var.length; }

    public boolean isEqual(IFunction f) { return true; }

    public double[] gradient(double[] x) { return new double[] { 2*par[0]*x[0], par[1] }; }

    public boolean providesGradient() { return true; }

    public String variableName(int i) { return varNames[i]; }

    public String[] variableNames() { return varNames; }

    public void setParameters(double[] params) { par = params; }

	public double[] parameters() { return par; }

    public int numberOfParameters() {return par.length; }

    public String[] parameterNames() { return parNames; }

    public void setParameter(String name, double x) throws IllegalArgumentException {
	
    }

    public double parameter(String name) {return par[indexOfParameter(name)]; }

    public int indexOfParameter(String name) { 
	int index;
	for (index=0; index<par.length; index++) if (name.equals(parNames[index])) break;

	return index;
}

    public IAnnotation annotation() { return null; }

    public String codeletString() { return codelet; }

    public void setTitle(String str) throws java.lang.IllegalArgumentException {
        this.title = str;
    }    
    
    public String title() {
        return title;
    }
    
    public String normalizationParameter() {
        throw new UnsupportedOperationException();
    }

}
