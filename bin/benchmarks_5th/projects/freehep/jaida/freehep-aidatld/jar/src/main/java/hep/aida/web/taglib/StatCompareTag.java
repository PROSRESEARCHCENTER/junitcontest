package hep.aida.web.taglib;

import hep.aida.IManagedObject;

/**
 * Performs the statistical comparison of two objects and prints
 * the result. So far supported objects are {@link ICloud1D}
 * and {@link IHistogram1D}.
 *
 * @author The AIDA Team @ SLAC
 *
 */
public interface StatCompareTag {

    /**
     * Set the first object to compare. This must either be an 
     * {@link IManagedObject} or the name of a variable in a JSP 
     * scope holding an {@link IManagedObject}.
     * 
     * @param var1
     *            the first object to in comparison (data)
     */
    public void setVar1(Object var1);
    
    /**
     * Set the second object to compare. This must either be an 
     * {@link IManagedObject} or the name of a variable in a JSP 
     * scope holding an {@link IManagedObject}.
     * 
     * @param var2
     *            the second object to in comparison (reference)
     */
    public void setVar2(Object var2);
    
    /**
     * Control tag output
     * 
     * @param verbose Set to <code>false</code> to print nothing on the page
     */
    public void setVerbose(boolean verbose);
    
    /**
     * If set, the IComparisonResult will be added to the pageContext 
     * under that name
     * 
     * @param resultVar The result variable name
     */
    public void setResultVar(String resultVar);
    
    /**
     * Set the comparison algorithm
     * 
     * @param algorithm The comparison algorithm
     */
    public void setAlgorithm(String algorithm);
    
    /**
     * Set the comparison options
     * 
     * @param options The comparison options
     */
    public void setOptions(String options);
}