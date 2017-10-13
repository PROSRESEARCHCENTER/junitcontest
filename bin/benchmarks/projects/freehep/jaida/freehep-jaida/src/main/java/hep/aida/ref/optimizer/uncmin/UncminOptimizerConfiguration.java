/*
 * UncminOptimizerConfiguration.java
 *
 * Created on May 28, 2002, 11:22 AM
 */

package hep.aida.ref.optimizer.uncmin;

/**
 *
 *  Implementation of IOptimizerConfiguration for the Uncmin optimizer
 *
 * @author Tony Johnson, Victor Serbo, Max Turri
 *
 */
public class UncminOptimizerConfiguration extends hep.aida.ref.optimizer.AbstractOptimizerConfiguration {

    /**
     * The methods available in Uncmin.
     *
     */
    public final static String LINE_SEARCH   = "LIN";
    public final static String DOUBLE_DOGLEG = "DOU";
    public final static String MORE_HEBDON   = "MOR";

    /**
     * The kind of strategies available in Uncmin.
     * With the LOW_CALL_STRATEGY (default) the Hessian is evaluated
     * by secant update rather than analytically or by finite difference.
     * The option with "_CHECK" enables some authomatic checks.
     *
     */
    public final static int LOW_CALL_STRATEGY    = 0;
    public final static int HIGH_CALL_STRATEGY   = 1;
    public final static int LOW_CALL_STRATEGY_CHECK  = 2;
    public final static int HIGH_CALL_STRATEGY_CHECK = 3;
        
    /** Creates a new instance of UncminOptimizerConfiguration */
    public UncminOptimizerConfiguration() {
//        setTolerance(1.e-10);    
        setTolerance(0.00001);    
        setMaxIterations(50);
        setMethod(LINE_SEARCH);
        setStrategy(LOW_CALL_STRATEGY);
//        setStrategy(HIGH_CALL_STRATEGY);
    }

    /**
     * Set the method to be used by the optimizer in the optimization procedure.
     * @param method The method to be adapted.
     *
     */
    public void setMethod(String method) {
        method = method.toUpperCase();
        if ( method.startsWith(LINE_SEARCH) || method.startsWith(DOUBLE_DOGLEG) || method.startsWith(MORE_HEBDON) )
            super.setMethod(method);
        else 
            throw new IllegalArgumentException("Unsupported method : "+method);
    }
        
   /**
     * Set the strategy to be used by the optimizer in the optimization procedure.
     * @param strategy The strategy.
     *
     */
    public void setStrategy(int strategy) {
        switch ( strategy ) {
            case LOW_CALL_STRATEGY:
            case HIGH_CALL_STRATEGY:
            case LOW_CALL_STRATEGY_CHECK:
            case HIGH_CALL_STRATEGY_CHECK:
                super.setStrategy(strategy);
                break;
            default:
                throw new IllegalArgumentException("Unsupported strategy : "+strategy);
        }
    }    
    
    public void setPrecision( double precision ) {
        throw new UnsupportedOperationException();
    }

    public void setErrorDefinition(int errorDefinition) throws java.lang.IllegalArgumentException {
    }
    
    public int errorDefinition()  {
        return Integer.MIN_VALUE;
    }
    
}
