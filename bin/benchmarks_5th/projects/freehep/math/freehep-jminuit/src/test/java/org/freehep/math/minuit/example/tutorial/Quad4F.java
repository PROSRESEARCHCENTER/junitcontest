package org.freehep.math.minuit.example.tutorial;

import org.freehep.math.minuit.FCNBase;

/**
 *
 * @version $Id: Quad4F.java 8584 2006-08-10 23:06:37Z duns $
 */
class Quad4F implements FCNBase
{ 
   public double valueOf(double[] par)
   {
      double x = par[0];
      double y = par[1];
      double z = par[2];
      double w = par[3];
      
      return ( (1./70.)*(21*x*x + 20*y*y + 19*z*z - 14*x*z - 20*y*z) + w*w );
   }
}
