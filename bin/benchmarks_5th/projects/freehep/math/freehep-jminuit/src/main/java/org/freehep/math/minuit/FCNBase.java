package org.freehep.math.minuit;

/** User function base class, has to be implemented by the user.
 * @version $Id: FCNBase.java 8584 2006-08-10 23:06:37Z duns $
 */
public interface FCNBase
{
   /**
    * Returns the value of the function with the given parameters.
    */
   double valueOf(double[] par);
}