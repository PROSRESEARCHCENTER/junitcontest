package org.freehep.math.minuit;

/**
 *
 * @version $Id: SimplexSeedGenerator.java 8584 2006-08-10 23:06:37Z duns $
 */
class SimplexSeedGenerator implements MinimumSeedGenerator
{
   public MinimumSeed generate(MnFcn fcn, GradientCalculator gc, MnUserParameterState st, MnStrategy stra)
   {
      int n = st.variableParameters();
      MnMachinePrecision prec = st.precision();
      
      // initial starting values
      MnAlgebraicVector x = new MnAlgebraicVector(n);
      for(int i = 0; i < n; i++) x.set(i,st.intParameters().get(i));
      double fcnmin = fcn.valueOf(x);
      MinimumParameters pa = new MinimumParameters(x, fcnmin);
      InitialGradientCalculator igc = new InitialGradientCalculator(fcn, st.trafo(), stra);
      FunctionGradient dgrad = igc.gradient(pa);
      MnAlgebraicSymMatrix mat = new MnAlgebraicSymMatrix(n);
      double dcovar = 1.;
      for(int i = 0; i < n; i++)
         mat.set(i,i, Math.abs(dgrad.g2().get(i)) > prec.eps2() ? 1./dgrad.g2().get(i) : 1.);
      MinimumError err = new MinimumError(mat, dcovar);
      double edm = new VariableMetricEDMEstimator().estimate(dgrad, err);
      MinimumState state = new MinimumState(pa, err, dgrad, edm, fcn.numOfCalls());
      
      return new MinimumSeed(state, st.trafo());
   } 
}
