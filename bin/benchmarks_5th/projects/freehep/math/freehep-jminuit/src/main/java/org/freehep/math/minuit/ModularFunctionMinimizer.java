package org.freehep.math.minuit;

/**
 *
 * @version $Id: ModularFunctionMinimizer.java 8584 2006-08-10 23:06:37Z duns $
 */
abstract class ModularFunctionMinimizer
{
   FunctionMinimum minimize(FCNBase fcn, MnUserParameterState st, MnStrategy strategy, int maxfcn, double toler, double errorDef, boolean useAnalyticalGradient, boolean checkGradient)
   {
      MnUserFcn mfcn = new MnUserFcn(fcn, errorDef, st.trafo());

      GradientCalculator gc;
      if (fcn instanceof FCNGradientBase && useAnalyticalGradient) gc = new AnalyticalGradientCalculator((FCNGradientBase) fcn, st.trafo(), checkGradient);
      else gc = new Numerical2PGradientCalculator(mfcn, st.trafo(), strategy);
      
      int npar = st.variableParameters();
      if(maxfcn == 0) maxfcn = 200 + 100*npar + 5*npar*npar;
      MinimumSeed mnseeds = seedGenerator().generate(mfcn, gc, st, strategy);
      
      return minimize(mfcn, gc, mnseeds, strategy, maxfcn, toler);
   }
   
   abstract MinimumSeedGenerator seedGenerator();
   abstract MinimumBuilder builder();
   
   FunctionMinimum minimize(MnFcn mfcn, GradientCalculator gc, MinimumSeed seed, MnStrategy strategy, int maxfcn, double toler)
   {
      return builder().minimum(mfcn, gc, seed, strategy, maxfcn, toler*mfcn.errorDef());
   }  
}
