/*
 *
 *  User level interface to the result of an optimization.
 *
 * @author Tony Johnson, Victor Serbo, Max Turri
 *
 */
package hep.aida.ext;


public interface IOptimizerResult {
        
    public final static int UNDEFINED = 0, APPROXIMATE = 1, CONVERGED_NEG_MATRIX = 2, CONVERGED = 3, CONVERGED_SMALL_GRADIENT = 4,
    CONVERGED_SMALL_STEP_SIZE = 5, NOT_CONVERGED = 6, REACHED_MAX_ITER = 7, TOO_MANY_LARGE_STEPS = 8;

    
    /**
     * The constraints used in the optimization.
     * @return The domain of constraints.
     *
     */
    public IDomainConstraint constraints();
    
    /**
     * The optimizer's configuration.
     * @return The optimizer's configuration.
     *
     */
    public IOptimizerConfiguration configuration();
    
    public double[] parameters();
    
    /**
     * The covariance matrix.
     * Still under discussion
     *
     */
    public double[][] covarianceMatrix();
    
    /**
     * The correlation matrix.
     * Still under discussion.
     *
     */
    public double[][] correlationMatrix();
    
    /**
     * Get the contour with respec to two variables.
     * @param nSigma The number of sigmas at which the contour should be calculated.
     * @param nPoints The maximum number of points on the contour (might be less).
     * @param var1 The first variable.
     * @param var2 The second variable.
     * @return The Object containing the contour (to be fixed).
     *
     */
    public Object contour(int nSigma, int nPoints, IVariable var1, IVariable var2);
    
    /**
     * Scan the function with respect to two variables.
     * @param nPoints The number of points to be used in the scan.
     * @param var1 The first variable.
     * @param var2 The second variable.
     * @return The Object containing the scan (to be fixed).
     *
     */
    public Object scan(int nPoints, IVariable var1, IVariable var2);

    /**
     * Scan the function with respect to a variable.
     * @param nPoints The number of points to be used in the scan.
     * @param var The variable.
     * @return The Object containing the scan (to be fixed).
     *
     */
    public Object scan(int nPoints, IVariable var);

    /**
     * The status of the optimizer after the optimization procedure.
     * @return The status of the optimization.
     *
     */
    public int optimizationStatus();
    
}
