/*
 *
 *  User level interface to the optimizer configuration.
 *
 * @author Tony Johnson, Victor Serbo, Max Turri
 *
 */
package hep.aida.ext;

public interface IOptimizerConfiguration {

    /**
     * The error definition for the optimizers.
     *
     */
    public static final int DEFAULT_ERROR  = 0;
    public static final int CHI2_FIT_ERROR = 1;
    public static final int LOGL_FIT_ERROR = 2;
    
    /**
     * The printout levels for the optimizer.
     *
     */
    public static final int NO_OUTPUT       = -1;
    public static final int NORMAL_OUTPUT   = -2;
    public static final int DETAILED_OUTPUT = -3;

    /**
     * Set the optimizer's tolerance. The tolerance is used to determine if the
     * optimizer converged to an optimal solution.
     * @param tolerance The tolerance.
     * @return <code>true</code> if the tolerance was set succesfully,
     *         <code>false</code> otherwise.
     *
     */
    public void setTolerance(double tolerance) throws IllegalArgumentException;

    /**
     * Get the optimizer's tolerance.
     * @return The tolerance.
     *
     */
    public double tolerance();
    
    /**
     * Set the precision required in the optimizer's calculations.
     * The highest possible is the machine's precision.
     * @param precision The precision.
     * @return <code>true</code> if the precision was set succesfully,
     *         <code>false</code> otherwise.
     *
     */
    public void setPrecision(double precision) throws IllegalArgumentException;
    
    /**
     * Get the internal precision of the Optimizer.
     * @return The precision.
     *
     */
    public double precision();

    /**
     * Tell the optmizer what kind of errors to calculate.
     * @param errDef The type of error to be calculated.
     *
     */
    public void setErrorDefinition(int errDef) throws IllegalArgumentException;
    
    /**
     * Get the optimizer's error definition.
     * @return The error definition.
     *
     */
    public int errorDefinition();
    
    /**
     * Set the maximum number of iterations to be performed in the optimization procedure.
     * If the optimizer did not converge before maxIter iterations the optimization will stop.
     * @param maxIter The maximum number of iterations.
     *
     */
    public void setMaxIterations(int maxIter) throws IllegalArgumentException;

    /**
     * Get the maximum number of iterations allowed before exiting the optimization procedure.
     * @return The maximum number of iterations.
     *
     */
    public int maxIterations();

    /**
     * Set the printout level.
     * @param printLevel The printout level.
     * @return <code>true</code> if the level was set succesfully,
     *         <code>false</code> otherwise.
     *
     */
    public void setPrintLevel(int printLevel) throws IllegalArgumentException;

    /**
     * Get the printout level.
     * @return the printout level.
     *
     */
    public int printLevel();
    
    /**
     * Set the title for the current optimization problem.
     * @param title The title.
     *
     */
    public void setTitle(java.lang.String title);

    /**
     * Get the title.
     * @return The title.
     *
     */
    public String title();

    /**
     * Specify if the optimizer has to use the gradient as provided by the IFunction.
     * @param useGradient <code>true</code> if the Optimizer has to use the IFunction's 
     *                    calculation of the gradient, <code>false</code> otherwise.
     *
     */
    public void setUseFunctionGradient(boolean useGradient);

    /**
     * See if the optimizer uses the IFunction's evaluation of the gradient.
     * @return <code>true</code> if the optimizer uses the IFunction's evaluation of the gradient.
     *
     */
    public boolean useFunctionGradient();

    /**
     * Specify if the optimizer has to use the Hessian as provided by the IFunction.
     * @param useHessian <code>true</code> if the Optimizer has to use the IFunction's 
     *                    calculation of the Hessian, <code>false</code> otherwise.
     *
     */
    public void setUseFunctionHessian(boolean useHessian);

    /**
     * See if the optimizer uses the IFunction's evaluation of the Hessian.
     * @return <code>true</code> if the optimizer uses the IFunction's evaluation of the Hessian.
     *
     */
    public boolean useFunctionHessian();

    /**
     * Set the strategy to be used by the optimizer in the optimization procedure.
     * @param strategy The strategy.
     *
     */
    public void setStrategy(int strategy) throws IllegalArgumentException;

    /**
     * Get the strategy used by the optimizer in the optimization procedure.
     * @return The strategy.
     *
     */
    public int strategy();
    
    /**
     * Set the method to be used by the optimizer in the optimization procedure.
     * @param method The method to be adapted.
     *
     */
    public void setMethod( java.lang.String method ) throws IllegalArgumentException;
    
    /**
     * Get the method used by the optimizer in the optimization procedure.
     * @return The method used.
     *
     */
    public String method();
    
}
