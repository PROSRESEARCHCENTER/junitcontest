/*
 * AbstractOptimizerConfiguration.java
 *
 * Created on May 28, 2002, 11:24 AM
 */

package hep.aida.ref.optimizer;

/**
 *
 *  Implementation of IOptimizerConfiguration for the AbstractOptimizer implementation
 *
 * @author Tony Johnson, Victor Serbo, Max Turri
 *
 */
public abstract class AbstractOptimizerConfiguration implements hep.aida.ext.IOptimizerConfiguration {
        
    private int printLevel = NORMAL_OUTPUT;
    private java.lang.String title = "Default Optimization Job";
    private double tolerance = 0.0001;
    private int maxIterations = 200;
    private boolean useFunctionGradient = false;
    private boolean useFunctionHessian = false;
    private String method;
    private int strategy;
    private double precision;
        
    /**
     * Tell the optmizer what kind of errors to calculate.
     * @param errorDefinition The type of error to be calculated.
     *
     */
    public void setErrorDefinition(int errorDefinition) throws java.lang.IllegalArgumentException {
        throw new UnsupportedOperationException( "Optimizer does not support this method");
    }
    
    /**
     * Get the optimizer's error definition.
     * @return The error definition.
     *
     */
    public int errorDefinition()  {
        throw new UnsupportedOperationException( "Optimizer does not support this method");
    }

    /**
     * Set the maximum number of iterations to be performed in the optimization procedure.
     * If the optimizer did not converge before maxIter iterations the optimization will stop.
     * @param maxIterations The maximum number of iterations.
     *
     */

    public void setMaxIterations(int maxIterations) {
        this.maxIterations = maxIterations;
    }
    
    /**
     * Get the maximum number of iterations allowed before exiting the optimization procedure.
     * @return The maximum number of iterations.
     *
     */
    public int maxIterations() {
        return maxIterations;
    }

    /**
     * Set the method to be used by the optimizer in the optimization procedure.
     * @param method The method to be adapted.
     *
     */
    public void setMethod(String method) {
        this.method = method;
    }
    
    /**
     * Get the method used by the optimizer in the optimization procedure.
     * @return The method used.
     *
     */
    public String method() {
        return method;
    }
    
    /**
     * Set the precision required in the optimizer's calculations.
     * The highest possible is the machine's precision.
     * @param precision The precision.
     * @return <code>true</code> if the precision was set succesfully,
     *         <code>false</code> otherwise.
     *
     */
    public void setPrecision(double precision) {
        this.precision = precision;
    }
    
    /**
     * Get the internal precision of the Optimizer.
     * @return The precision.
     *
     */
    public double precision() {
        return precision;
    }


    /**
     * Set the printout level.
     * @param printLevel The printout level.
     * @return <code>true</code> if the level was set succesfully,
     *         <code>false</code> otherwise.
     *
     */
    public void setPrintLevel(int printLevel) {
        this.printLevel = printLevel;
    }
    
    /**
     * Get the printout level.
     * @return the printout level.
     *
     */
    public int printLevel() {
        return printLevel;
    }

    /**
     * Set the strategy to be used by the optimizer in the optimization procedure.
     * @param strategy The strategy.
     *
     */
    public void setStrategy(int strategy) {
        this.strategy = strategy;
    }
    
    /**
     * Get the strategy used by the optimizer in the optimization procedure.
     * @return The strategy.
     *
     */
    /**
     * Get the strategy used by the optimizer in the optimization procedure.
     * @return The strategy.
     *
     */
    public int strategy() {
        return strategy;
    }

    /**
     * Set the title for the current optimization problem.
     * @param title The title.
     *
     */
    public void setTitle(String title) {
        this.title = title;
    }
    
    /**
     * Get the title.
     * @return The title.
     *
     */
    public String title() {
        return title;
    }

    /**
     * Set the optimizer's tolerance. The tolerance is used to determine if the
     * optimizer converged to an optimal solution.
     * @param tolerance The tolerance.
     * @return <code>true</code> if the tolerance was set succesfully,
     *         <code>false</code> otherwise.
     *
     */
    public void setTolerance(double tolerance) {
        this.tolerance = tolerance;
    }

    /**
     * Get the optimizer's tolerance.
     * @return The tolerance.
     *
     */
    public double tolerance() {
        return tolerance;
    }    
        
    /**
     * Specify if the optimizer has to use the gradient as provided by the IFunction.
     * @param useGradient <code>true</code> if the Optimizer has to use the IFunction's 
     *                    calculation of the gradient, <code>false</code> otherwise.
     *
     */
    public void setUseFunctionGradient(boolean useGradient) {
        this.useFunctionGradient = useGradient;
    }
    
    /**
     * See if the optimizer uses the IFunction's evaluation of the gradient.
     * @return <code>true</code> if the optimizer uses the IFunction's evaluation of the gradient.
     *
     */
    public boolean useFunctionGradient() {
        return useFunctionGradient;
    }
    
    /**
     * Specify if the optimizer has to use the Hessian as provided by the IFunction.
     * @param useHessian <code>true</code> if the Optimizer has to use the IFunction's 
     *                    calculation of the Hessian, <code>false</code> otherwise.
     *
     */
    public void setUseFunctionHessian(boolean useHessian) {
        this.useFunctionHessian = useHessian;
    }

    /**
     * See if the optimizer uses the IFunction's evaluation of the Hessian.
     * @return <code>true</code> if the optimizer uses the IFunction's evaluation of the Hessian.
     *
     */
    public boolean useFunctionHessian() {
        return useFunctionHessian;
    }
       
}
