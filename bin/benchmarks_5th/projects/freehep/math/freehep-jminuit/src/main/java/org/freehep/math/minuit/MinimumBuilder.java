package org.freehep.math.minuit;

/**
 *
 * @version $Id: MinimumBuilder.java 8584 2006-08-10 23:06:37Z duns $
 */
interface MinimumBuilder
{
  FunctionMinimum minimum(MnFcn fcn, GradientCalculator gc,  MinimumSeed seed,  MnStrategy strategy, int maxfcn, double toler);
}
