package jas.util;

/**
 * A class that encapsulates a value and its error.
 * Primarily for use with ScientificFormat
 * @see jas.util.ScientificFormat
 */
public class DoubleWithError
{
	public DoubleWithError(double value, double error)
	{
		this.value = value;
		this.error = error;
	}
	public void setError(double error)
	{
		this.error = error;
	}
	public double getError()
	{
		return error;
	}
	public void setValue(double value)
	{
		this.value = value;
	}
	public double getValue()
	{
		return value;
	}
	public String toString()
	{
		return String.valueOf(value)+plusorminus+error;
	}
   public final static String plusorminus = "\u00b1";
	private double value, error;
}
