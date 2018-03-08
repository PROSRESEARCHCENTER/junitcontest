/*
 * FminOptimizerConfiguration.java
 *
 * Created on May 24, 2002, 4:16 PM
 */

package hep.aida.ref.optimizer.fmin;

/**
 *
 *  Implementation of IOptimizerConfiguration for the Fmin optimizer
 *
 * @author Tony Johnson, Victor Serbo, Max Turri
 *
 */
public class FminOptimizerConfiguration extends hep.aida.ref.optimizer.AbstractOptimizerConfiguration {
        
    /**
     * The methods available in Fmin.
     *
     */
    public final static String FMIN  = "FMIN";

    /** 
     * Creates a new instance of FminOptimizerConfiguration
     *
     */
    public FminOptimizerConfiguration() {
        setTolerance(0.001);
        setMethod(FMIN);
    }

    /**
     * Set the method to be used by the optimizer in the optimization procedure.
     * @param method The method to be adapted.
     *
     */
    public void setMethod(String method) {
        method.toUpperCase();
        if ( method.startsWith(FMIN) )
            super.setMethod(method);
        else 
            throw new IllegalArgumentException("Fmin Optimizer does not support the method : "+method);
    }
    
    public void setMaxIterations(int iterations) {
        throw new UnsupportedOperationException( "FminOptimizer does not support this method");
    }
    public int maxIterations()  {
        throw new UnsupportedOperationException( "FminOptimizer does not support this method");
    }
    public void setUseFunctionGradient(boolean useFG) {
        throw new UnsupportedOperationException( "FminOptimizer does not support this method");
    }    
    public void setStrategy(int strategy) {
        throw new UnsupportedOperationException( "FminOptimizer does not support this method");
    }
    public int strategy() {
        throw new UnsupportedOperationException( "FminOptimizer does not support this method");
    }
    public void setUseFunctionHessian(boolean useHessian) {
        throw new UnsupportedOperationException();
    }
    public void setPrecision( double precision ) {
        throw new UnsupportedOperationException();
    }
}
