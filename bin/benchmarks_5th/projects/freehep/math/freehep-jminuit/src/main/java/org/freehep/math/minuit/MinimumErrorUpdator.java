package org.freehep.math.minuit;

/**
 *
 * @version $Id: MinimumErrorUpdator.java 8584 2006-08-10 23:06:37Z duns $
 */
interface MinimumErrorUpdator
{
    MinimumError update(MinimumState state, MinimumParameters par, FunctionGradient grad);
}
