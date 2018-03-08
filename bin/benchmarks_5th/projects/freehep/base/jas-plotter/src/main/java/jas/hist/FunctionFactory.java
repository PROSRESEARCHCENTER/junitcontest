package jas.hist;
import javax.swing.Icon;

public interface FunctionFactory
{
	Basic1DFunction createFunction(JASHist h) throws FunctionFactoryError;
	String getFunctionName();
	Icon getFunctionIcon();
}
