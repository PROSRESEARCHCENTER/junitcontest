package org.freehep.math.minuit.example.tutorial;

import org.freehep.math.minuit.FCNGradientBase;

/**
 *
 * @version $Id: Quad3F.java 8584 2006-08-10 23:06:37Z duns $
 */
class Quad3F implements FCNGradientBase
{
   public double valueOf(double[] par)
   {
      double x = par[0];
      double y = par[1];
      double z = par[2];
      
      return ( x*x  + y*y + z*z );
   }
   
   public double[] gradient(double[] par)
   {
      double x = par[0];
      double y = par[1];
      double z = par[2];
      
      double[] result = { 2.*x, 2.*y, 2.*z};
      return result;
   }
}