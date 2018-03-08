package org.freehep.math.minuit;

/**
 *
 * @version $Id: GradientCalculator.java 8584 2006-08-10 23:06:37Z duns $
 */
interface GradientCalculator
{
  FunctionGradient gradient(MinimumParameters par);
  FunctionGradient gradient(MinimumParameters par, FunctionGradient grad);
}
