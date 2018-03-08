/*
 * JELBaseModelFunction.java
 *
 * Created on September 16, 2002, 3:27 PM
 */

package hep.aida.ref.function;
import hep.aida.ref.AidaUtils;

import java.util.StringTokenizer;

/**
 *
 * @author  serbo
 */
public class JELBaseModelFunction extends BaseModelFunction {

    private JELBaseModelFunction() {}
    public JELBaseModelFunction(String treeName, int dim, String valexpr, String parameters, String description, String gradexpr) {
	//System.out.println("JELBaseModelFunction:: valexpr="+valexpr+",  parameters="+parameters+
	//		   ",   description="+description+",  gradexpr="+gradexpr);
	setName(treeName);
        FunctionCore f = createJELFunctionCore(dim, valexpr, parameters, gradexpr);
	init(description, f , f);
    }


    public JELFunctionCore createJELFunctionCore(int dim, String valexpr, String parameters, String gradexpr) {
	JELFunctionCore core = null;

	// Reformat parameters String into String[]
	String[] parNames = null;
	int nPar = 0;
	if (parameters != null && !parameters.equals("")) {
	    StringTokenizer st = new StringTokenizer(parameters, ",;");
	    nPar = st.countTokens();
	    if (nPar >0 ) parNames = new String[nPar];
	    int i =0;
	    while (st.hasMoreTokens()) {
		String token = st.nextToken().trim();
                checkParameterName(token);
		parNames[i] = token;
		//System.out.println(i+"\t"+parNames[i]);
		i++;
	    }
	}

	// Reformat gradient expression String into String[]
	String[] gr = null;
	int nGr = 0;
	if (gradexpr != null && !gradexpr.equals("") && !gradexpr.trim().toLowerCase().equals("null")) {
	    String[] array = AidaUtils.parseString(gradexpr);
	    nGr = array.length;
	    if (nGr != dim ) 
		throw new IllegalArgumentException("Dimension of the gradient ("+nGr+") is not equal to the dimension of the function ("+dim+")");
	    gr = new String[dim];
	    int i =0;
	    for (int j=0; j<dim; j++) {
		String token = array[j].trim();
		gr[j] = token;
		//System.out.println(i+"\t"+gr[i]);
	    }
	} 
        /*
	if (gradexpr != null && !gradexpr.equals("") && !gradexpr.trim().toLowerCase().equals("null")) {
	    String[] array = AidaUtils.parseString(gradexpr);
	    nGr = array.length;
	    if (nGr != nPar ) 
		throw new IllegalArgumentException("Dimension of the gradient ("+nGr+") is not equal to the number of parameters ("+nPar+")");
	    gr = new String[nPar];
	    int i =0;
	    for (int j=0; j<nGr; j++) {
		String token = array[j].trim();
		gr[j] = token;
		//System.out.println(i+"\t"+gr[i]);
	    }
	} 
        */
	core = new JELFunctionCore(dim, nPar, valexpr, parNames, gr );

	return core;
    }

    private void checkParameterName(String token) {
        if (token.startsWith("x") || token.startsWith("X")) {
            String tmp = token.substring(1);
            try {
                int i = Integer.parseInt(tmp);
            } catch (NumberFormatException e) {
                return;
            }
            String mess = "Illegal parameter name: \""+token+"\".\nNames like x0, x1, x2, ..., are reserved. Please rename this parameter..";
            throw new IllegalArgumentException(mess);
        }
        return;
    }
    
}

