/*
 * MinpackOptimizerConfiguration.java
 *
 * Created on May 27, 2002, 4:29 PM
 */

package hep.aida.ref.optimizer.minpack;

/**
 *
 *  Implementation of IOptimizerConfiguration for the Minpack optimizer
 *
 * @author Tony Johnson, Victor Serbo, Max Turri
 *
 */
public class MinpackOptimizerConfiguration extends hep.aida.ref.optimizer.AbstractOptimizerConfiguration {
    
    
    /** 
     * Creates a new instance of MinpackOptimizerConfiguration
     *
     */
    public MinpackOptimizerConfiguration() {
        setTolerance(0.001);    
    }
    
    public void setMaxIterations(int iterations) {
        throw new UnsupportedOperationException( "MinpackOptimizer does not support this method");
    }
    public int maxIterations()  {
        throw new UnsupportedOperationException( "MinpackOptimizer does not support this method");
    }    
    public void setUseFunctionGradient(boolean useFG) {
        throw new UnsupportedOperationException( "MinpackOptimizer does not support this method");
    }
    public void setMethod(String method) {
        throw new UnsupportedOperationException( "MinpackOptimizer does not support this method");
    }
    public String method() {
        throw new UnsupportedOperationException( "MinpackOptimizer does not support this method");
    }
    public void setStrategy(int strategy) {
        throw new UnsupportedOperationException( "MinpackOptimizer does not support this method");
    }
    public int strategy() {
        throw new UnsupportedOperationException( "MinpackOptimizer does not support this method");
    }
    public void setUseFunctionHessian(boolean useHessian) {
        throw new UnsupportedOperationException();
    }
    public void setPrecision( double precision ) {
        throw new UnsupportedOperationException();
    }
    
}
