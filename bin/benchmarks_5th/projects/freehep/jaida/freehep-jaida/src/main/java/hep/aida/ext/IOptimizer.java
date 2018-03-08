/**
 *
 *  User level interface to the optimizer.
 *
 * @author Tony Johnson, Victor Serbo, Max Turri
 *
 */
package hep.aida.ext;

import hep.aida.IFunction;

public interface IOptimizer {
        
    /**
     * Set the IFunction to be optimized.
     * When setting a function the current  
     * configuration is reset.
     * @param function The IFunction to be optimized.
     * @return <code>true</code> if the IFunction is set succesfully,
     *         <code>false</code> otherwise.
     *
     */
    public void setFunction( IFunction function ) throws IllegalArgumentException;
    
    /**
     * Set the domain of constraints for the IOptimizer.
     * If a domain of constraints already exists it will be overwritten.
     * @param domainConstraint The domain of constraints.
     * @return <code>true</code> if the IDomainConstraint is set succesfully,
     *         <code>false</code> otherwise.
     *
     */
    public void setConstraints( IDomainConstraint domainConstraint ) throws IllegalArgumentException;
    
    /**
     * Perform the optimization.
     * @return <code>true</code> if the optimization was
     *         successfull, <code>false</code> otherwise.
     *
     */
    public void optimize();
    
    /**
     * Get the optimization results.
     * @return The result.
     *
     */
    public IOptimizerResult result();

    /**
     * Reset the function, the domain of constraints and the internal configuration.
     *
     */
    public void reset();

    /**
     * Get the IOptimizer configuration
     * @return The configuration.
     *
     */
    public IOptimizerConfiguration configuration();
    
    /**
     * Set the internal configuration of the Optimizer.
     * @param config The configuration.
     *
     */
    public void setConfiguration( IOptimizerConfiguration config ) throws IllegalArgumentException;
    
    /**
     * Get the IVariableSettings corresponding to a give variable.
     * If the IVariableSettings does not exist, a new one is created.
     * @param name The variable's name.
     * @return     The corresponging IVariableSettings
     *
     */
    public IVariableSettings variableSettings(String name);

    /**
     * Get the list of the names of the IVariableSettings defined.
     * @return The list of the names.
     *
     */
    public String[] listVariableSettings();

    /**
     * Reset all variable settings.
     *
     */
    public void resetVariableSettings();

    /**
     * Check if this IOptimizer accept constraints.
     * @return <code>true</code> if the IOptimizer accepts contraints.
     *         <code>false</code> otherwise.
     *
     */
    public boolean acceptsConstraints();
    
    /**
     * Check if this IOptimizer can calculate contours.
     * @return <code>true</code> if the IOptimizer can calculate contours.
     *         <code>false</code> otherwise.
     *
     */
    public boolean canCalculateContours();

    
    /**
     * Calculate the contour for two given parameters. The number of points and the number of sigmas
     * can also be specified.
     * @param par1    The name of the first parameter.
     * @param par2    The name of the second parameter.
     * @param npts    The number of points on the contour.
     * @param nSigmas The number of sigmas for this contour.
     * @return        A double array of dimension [2][npts]. For each point on the contour the value of (par1,par2) is returned.
     *
     */
    public double[][] calculateContour(String par1, String par2, int npts, double nSigmas);    
    
}
