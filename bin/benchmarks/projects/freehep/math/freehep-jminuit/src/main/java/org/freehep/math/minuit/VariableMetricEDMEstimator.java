package org.freehep.math.minuit;

/**
 *
 * @author tonyj
 * @version $Id: VariableMetricEDMEstimator.java 8584 2006-08-10 23:06:37Z duns $
 */
class VariableMetricEDMEstimator
{
   double estimate(FunctionGradient g, MinimumError e)
   {
      if(e.invHessian().size()  == 1)
         return 0.5*g.grad().get(0)*g.grad().get(0)*e.invHessian().get(0,0);
      
      double rho = MnUtils.similarity(g.grad(), e.invHessian());
      return 0.5*rho;
   }
}
