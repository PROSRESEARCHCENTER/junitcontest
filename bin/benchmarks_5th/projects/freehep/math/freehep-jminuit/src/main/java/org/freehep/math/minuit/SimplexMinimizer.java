package org.freehep.math.minuit;

/**
 *
 * @version $Id: SimplexMinimizer.java 8584 2006-08-10 23:06:37Z duns $
 */
class SimplexMinimizer extends ModularFunctionMinimizer
{
   public SimplexMinimizer()
   {
      theSeedGenerator = new SimplexSeedGenerator();
      theBuilder = new SimplexBuilder();
   }
   
   public MinimumSeedGenerator seedGenerator()
   {
      return theSeedGenerator;
   }
   public MinimumBuilder builder()
   {
      return theBuilder;
   }
   
   private SimplexSeedGenerator theSeedGenerator;
   private SimplexBuilder theBuilder;
}
