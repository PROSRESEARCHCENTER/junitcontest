package hep.aida.ref.optimizer.jminuit;

import hep.aida.ref.optimizer.AbstractOptimizerConfiguration;

/**
 *
 * @author The AIDA team @SLAC.
 */
class JMinuitConfiguration extends AbstractOptimizerConfiguration
{
   /**
    * The kind of strategies available in Minuit.
    *
    */
   public final static int LOW_CALL_STRATEGY    = 0;
   public final static int MEDIUM_CALL_STRATEGY = 1;
   public final static int HIGH_CALL_STRATEGY   = 2;
   
   /**
    * The methods available in Minuit.
    *
    */
   public final static String IMPROVE  = "IMP";
   public final static String MIGRAD   = "MIG";
   public final static String MINIMIZE = "MINI";
   public final static String SIMPLEX  = "SIMP";
   public final static String SEEK     = "SEE";
   
   private double errorDefinition = 1;
   private int errorDefinitionInt = DEFAULT_ERROR;

   /**
    * Creates a new instance of MinuitOptimizerConfiguration
    *
    */
   JMinuitConfiguration()
   {
      setTolerance(0.00001);
      setMaxIterations(0);
      setPrintLevel(NO_OUTPUT);
//        setPrintLevel(DETAILED_OUTPUT);
      setErrorDefinition(DEFAULT_ERROR);
      setStrategy(MEDIUM_CALL_STRATEGY);
      setMethod(MIGRAD);
   }
   
   /**
    * Tell the optmizer what kind of errors to calculate.
    * @param errorDefinition The type of error to be calculated.
    *
    */
   public void setErrorDefinition(int errorDefinition)
   {
      switch( errorDefinition )
      {
         case CHI2_FIT_ERROR:
         case DEFAULT_ERROR:
            this.errorDefinition = 1;
            this.errorDefinitionInt = errorDefinition;
            break;
         case LOGL_FIT_ERROR:
            this.errorDefinition = 0.5;
            this.errorDefinitionInt = errorDefinition;
            break;
         default:
            throw new IllegalArgumentException("Unsupported error definition" + errorDefinition);
      }
   }
   
   /**
    * Get the optimizer's error definition.
    * @return The error definition.
    *
    */
   public int errorDefinition()
   {
      return errorDefinitionInt;
   }

   public double errorDef() {
        return errorDefinition;
    }
   
   /**
    * Set the method to be used by the optimizer in the optimization procedure.
    * @param method The method to be adapted.
    *
    */
   public void setMethod(String method)
   {
      method.toUpperCase();
      if ( method.startsWith(IMPROVE) || method.startsWith(MIGRAD) || method.startsWith(SIMPLEX) || method.startsWith(MINIMIZE) || method.startsWith(SEEK) )
         super.setMethod(method);
      else
         throw new IllegalArgumentException("Unsupported method : "+method);
   }
   
   /**
    * Set the precision required in the optimizer's calculations.
    * The highest possible is the machine's precision.
    * @param precision The precision.
    * @return <code>true</code> if the precision was set succesfully,
    *         <code>false</code> otherwise.
    *
    */
   public void setPrecision(double precision)
   {
      super.setPrecision(precision);
      //optimizer.commands().setPrecision( precision );
   }
   
   /**
    * Set the strategy to be used by the optimizer in the optimization procedure.
    * @param strategy The strategy.
    *
    */
   public void setStrategy(int strategy)
   {
      switch ( strategy )
      {
         case LOW_CALL_STRATEGY:
         case MEDIUM_CALL_STRATEGY:
         case HIGH_CALL_STRATEGY:
            super.setStrategy(strategy);
            break;
         default:
            throw new IllegalArgumentException("Unsupported strategy : "+strategy);
      }
      
      //optimizer.commands().setStrategy( strategy );
   }
   
   /**
    * Specify if the optimizer has to use the gradient as provided by the IFunction.
    * @param useFunctionGradient <code>true</code> if the Optimizer has to use the IFunction's
    *                    calculation of the gradient, <code>false</code> otherwise.
    *
    */
   public void setUseFunctionHessian(boolean useHessian)
   {
      throw new UnsupportedOperationException();
   }
   
}
