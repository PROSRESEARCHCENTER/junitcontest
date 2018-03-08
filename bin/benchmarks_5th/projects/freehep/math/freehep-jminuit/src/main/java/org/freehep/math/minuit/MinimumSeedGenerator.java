package org.freehep.math.minuit;

/** base class for seed generators (starting values); the seed generator
 * prepares initial starting values from the input (MnUserParameterState)
 * for the minimization;
 * @version $Id: MinimumSeedGenerator.java 8584 2006-08-10 23:06:37Z duns $
 */
interface MinimumSeedGenerator
{
  MinimumSeed generate(MnFcn fcn, GradientCalculator calc, MnUserParameterState user, MnStrategy stra);
}
