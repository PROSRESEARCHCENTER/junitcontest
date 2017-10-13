package org.freehep.math.minuit;

import java.util.logging.Logger;

/**
 *
 * @version $Id: CombinedMinimumBuilder.java 16142 2014-09-05 02:52:34Z tonyj $
 */
class CombinedMinimumBuilder implements MinimumBuilder
{
   @Override
   public FunctionMinimum minimum(MnFcn fcn, GradientCalculator gc, MinimumSeed seed, MnStrategy strategy, int maxfcn, double toler)
   {
      FunctionMinimum min = theVMMinimizer.minimize(fcn, gc, seed, strategy, maxfcn, toler);
      
      if(!min.isValid())
      {
         logger.info("CombinedMinimumBuilder: migrad method fails, will try with simplex method first.");
         MnStrategy str = new MnStrategy(2);
         FunctionMinimum min1 = theSimplexMinimizer.minimize(fcn, gc, seed, str, maxfcn, toler);
         if(!min1.isValid())
         {
            logger.info("CombinedMinimumBuilder: both migrad and simplex method fail.");
            return min1;
         }
         MinimumSeed seed1 = theVMMinimizer.seedGenerator().generate(fcn, gc, min1.userState(), str);
         
         FunctionMinimum min2 = theVMMinimizer.minimize(fcn, gc, seed1, str, maxfcn, toler);
         if(!min2.isValid())
         {
            logger.info("CombinedMinimumBuilder: both migrad and method fails also at 2nd attempt.");
            logger.info("CombinedMinimumBuilder: return simplex minimum.");
            return min1;
         }
         
         return min2;
      }
      return min;
   }
   
   private final VariableMetricMinimizer theVMMinimizer = new VariableMetricMinimizer();
   private final SimplexMinimizer theSimplexMinimizer = new SimplexMinimizer();
   private static final Logger logger = Logger.getLogger(CombinedMinimumBuilder.class.getName());
}
