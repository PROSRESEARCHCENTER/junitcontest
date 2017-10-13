/*
 * OptimizerResult.java
 *
 * Created on May 23, 2002, 9:21 AM
 */

package hep.aida.ref.optimizer;

import hep.aida.ext.IDomainConstraint;
import hep.aida.ext.IOptimizerConfiguration;
import hep.aida.ext.IOptimizerResult;
import hep.aida.ext.IVariable;

/**
 *
 *  Implementation of IOptimizerResult
 *
 * @author Tony Johnson, Victor Serbo, Max Turri
 *
 */
public class OptimizerResult implements IOptimizerResult {
    
    private IDomainConstraint domainConstraint = null;
    private IOptimizerConfiguration optimizerConfiguration = null;
    
    private int status = IOptimizerResult.UNDEFINED;
    
    private double[][] covMatrix = null;
    private double[][] corrMatrix = null;
    
    private double[] pars = null;

    /** 
     * Creates a new instance of OptimizerResult 
     *
     */
    public OptimizerResult() {
    }
    
    public double[] parameters() {
        return pars;
    }
    
    public void setParameters(double[] pars) {
        this.pars = pars;
    }
    
    /**
     * The constraints used in the optimization.
     * @return The domain of constraints.
     *
     */
    public IDomainConstraint constraints() {
        return domainConstraint;
    }
    
    /**
     * The optimizer's configuration.
     * @return The optimizer's configuration.
     *
     */
    public IOptimizerConfiguration configuration() {
        return optimizerConfiguration;
    }
    
    /**
     * The covariance matrix.
     * Still under discussion
     *
     */
    public double[][] covarianceMatrix() {
        return covMatrix;
    }
    public void setCovarianceMatrix( double[][] covMatrix ) {
        this.covMatrix = covMatrix;
    }
    
    /**
     * The correlation matrix.
     * Still under discussion.
     *
     */
    public double[][] correlationMatrix() {
        return corrMatrix;
    }
    public void setCorrelationMatrix( double[][] corrMatrix ) {
        this.corrMatrix = corrMatrix;
    }
    
    
    /**
     * Get the contour with respec to two variables.
     * @param nSigma The number of sigmas at which the contour should be calculated.
     * @param nPoints The maximum number of points on the contour (might be less).
     * @param var1 The first variable.
     * @param var2 The second variable.
     * @return The Object containing the contour (to be fixed).
     *
     */
    public java.lang.Object contour(int nSigma, int nPoints, IVariable var1, IVariable var2) {
        return null;
    }
    
    /**
     * Scan the function with respect to two variables.
     * @param nPoints The number of points to be used in the scan.
     * @param var1 The first variable.
     * @param var2 The second variable.
     * @return The Object containing the scan (to be fixed).
     *
     */
    public java.lang.Object scan(int nPoints, IVariable var1, IVariable var2) {
        return null;
    }

    /**
     * Scan the function with respect to a variable.
     * @param nPoints The number of points to be used in the scan.
     * @param var The variable.
     * @return The Object containing the scan (to be fixed).
     *
     */
    public java.lang.Object scan(int nPoints, IVariable var) {
        return null;
    }

    /**
     * The status of the optimizer after the optimization procedure.
     * @return The status of the optimization.
     *
     */
    public int optimizationStatus() {
        return status;
    }
    
    public void setOptimizationStatus( int status )  {
        this.status = status;
    }
    
}
