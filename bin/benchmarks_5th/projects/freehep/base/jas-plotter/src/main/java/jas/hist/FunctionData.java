package jas.hist;

public interface FunctionData
	extends DataSource
{
	public double valueAt(double x) throws FunctionValueUndefined;
	public abstract double[] getParameterValues();
	public String[] getParameterNames();	
	public abstract void setParameter(int index, double value) throws InvalidFunctionParameter;
	
}

