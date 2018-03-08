package jas.hist;
import java.util.Enumeration;
import javax.swing.JMenu;

interface SupportsFunctions
{
	int numberOfFunctions();
	Enumeration getFunctions();
	void removeAllFunctions();
	void update(JASHist1DFunctionData func);
	JASHist1DFunctionData addFunction(Basic1DFunction d);
	void removeFunction(JASHist1DFunctionData d);
	void fillFunctionMenu(JMenu menu);
}

