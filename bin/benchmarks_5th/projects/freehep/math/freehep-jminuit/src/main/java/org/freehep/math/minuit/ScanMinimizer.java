package org.freehep.math.minuit;

/**
 *
 * @version $Id: ScanMinimizer.java 8584 2006-08-10 23:06:37Z duns $
 */
class ScanMinimizer extends ModularFunctionMinimizer
{
   ScanMinimizer()
   {
      theSeedGenerator = new SimplexSeedGenerator();
      theBuilder = new ScanBuilder();
   }
   
   MinimumSeedGenerator seedGenerator()
   {
      return theSeedGenerator;
   }
   MinimumBuilder builder()
   {
      return theBuilder;
   }
   
   private SimplexSeedGenerator theSeedGenerator;
   private ScanBuilder theBuilder;
}