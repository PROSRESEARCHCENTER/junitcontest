/*
 * FminOptimizer.java
 *
 * Created on May 24, 2002, 3:58 PM
 */

package hep.aida.ref.optimizer.fmin;
import hep.aida.ref.optimizer.OptimizerResult;
/**
 *
 *  Implementation of IOptimizerConfiguration for the Fmin optimizer
 *
 * @author Tony Johnson, Victor Serbo, Max Turri
 *
 */
public class FminOptimizer extends hep.aida.ref.optimizer.AbstractOptimizer {
    
    /** Creates a new instance of FminOptimizer */
    public FminOptimizer() {
        result = new OptimizerResult();
        configuration = new FminOptimizerConfiguration();
        domainConstraint = null;
    }
    
    /**
     * Perform the optimization.
     * @return <code>true</code> if the optimization was
     *        successfull, <code>false</code> otherwise.
     *
     */
    public void optimize() {
/*        
        if ( function == null ) throw new IllegalArgumentException("Cannot optimize!! The function was not set correctely!");
        
        String method = configuration.method();

        if ( method.equals( FminOptimizerConfiguration.FMIN ) ) {
            IVariable[] vars = ((AbstractModelFunction)function).variables();
            Variable var = null;
            for ( int i = 0; i<vars.length; i++ ) {
                if ( ! vars[i].isFixed() )
                    if ( var != null ) throw new IllegalArgumentException("Problem in Fmin optimize!! With method "+method+" it is only possible to performe 1-d optimization!!");
                    else var = (Variable)vars[i];
            }
            if ( var == null ) throw new IllegalArgumentException("Problem in Fmin optimize!! There are no free variables to optimize");
            if ( var.nRanges() > 1 ) throw new IllegalArgumentException("Problem in Fmin optimize!! Variable "+var.name()+" can only have a single range!!");
                            
            double lb = var.getRange(0).lowerBound();
            double ub = var.getRange(0).upperBound();

            FminFunc func = new FminFunc( function, var );
            double xmin = Fmin.fmin(lb,ub,func, configuration.tolerance());
            
        }
 */
    }

   /* 
    private class FminFunc implements Fmin_methods {    
        
        protected IFunction func;
        protected IVariable var;
        
        FminFunc( IFunction func, IVariable var ) {
            this.func = func;
            this.var = var;
        }
        
        public double f_to_minimize(double x) { 
            var.setValue( x ) ;
            return ((AbstractModelFunction)func).value();
        }
    }
    */
}



