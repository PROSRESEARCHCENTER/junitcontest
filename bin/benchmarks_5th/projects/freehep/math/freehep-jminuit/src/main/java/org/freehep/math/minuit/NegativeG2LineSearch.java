package org.freehep.math.minuit;

/** In case that one of the components of the second derivative g2 calculated
 * by the numerical gradient calculator is negative, a 1dim line search in
 * the direction of that component is done in order to find a better position
 * where g2 is again positive.
 * @version $Id: NegativeG2LineSearch.java 8584 2006-08-10 23:06:37Z duns $
 */
abstract class NegativeG2LineSearch
{
   
   static MinimumState search(MnFcn fcn, MinimumState st, GradientCalculator gc, MnMachinePrecision prec)
   {
      boolean negG2 = hasNegativeG2(st.gradient(), prec);
      if(!negG2) return st;
      
      int n = st.parameters().vec().size();
      FunctionGradient dgrad = st.gradient();
      MinimumParameters pa = st.parameters();
      boolean iterate = false;
      int iter = 0;
      do
      {
         iterate = false;
         for(int i = 0; i < n; i++)
         {
            if(dgrad.g2().get(i) < prec.eps2())
            {
               // do line search if second derivative negative
               MnAlgebraicVector step = new MnAlgebraicVector(n);
               step.set(i,dgrad.gstep().get(i)*dgrad.vec().get(i));
               if(Math.abs(dgrad.vec().get(i)) >  prec.eps2())
                  step.set(i, step.get(i) * (-1./Math.abs(dgrad.vec().get(i))));
               double gdel = step.get(i)*dgrad.vec().get(i);
               MnParabolaPoint pp = MnLineSearch.search(fcn, pa, step, gdel, prec);
               step = MnUtils.mul(step,pp.x());
               pa = new MinimumParameters(MnUtils.add(pa.vec(),step), pp.y());
               dgrad = gc.gradient(pa, dgrad);
               iterate = true;
               break;
            }
         }
      } 
      while(iter++ < 2*n && iterate);
      
      MnAlgebraicSymMatrix mat = new MnAlgebraicSymMatrix(n);
      for(int i = 0; i < n; i++)
         mat.set(i,i, Math.abs(dgrad.g2().get(i)) > prec.eps2() ? 1./dgrad.g2().get(i) : 1.);
      
      MinimumError err = new MinimumError(mat, 1.);
      double edm = new VariableMetricEDMEstimator().estimate(dgrad, err);
      
      return new MinimumState(pa, err, dgrad, edm, fcn.numOfCalls());
   }
   
   static boolean hasNegativeG2(FunctionGradient grad, MnMachinePrecision prec)
   {
      for(int i = 0; i < grad.vec().size(); i++)
         if(grad.g2().get(i) < prec.eps2()) return true;
      
      return false;
   }
}
