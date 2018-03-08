package org.freehep.math.minuit;

/**
 *
 * @version $Id: CombinedMinimizer.java 8584 2006-08-10 23:06:37Z duns $
 */
class CombinedMinimizer extends ModularFunctionMinimizer
{
   
   MinimumSeedGenerator seedGenerator()
   {
      return theMinSeedGen;
   }
   MinimumBuilder builder()
   {
      return theMinBuilder;
   }
   
   private MnSeedGenerator theMinSeedGen = new MnSeedGenerator();
   private CombinedMinimumBuilder theMinBuilder = new CombinedMinimumBuilder();
}
