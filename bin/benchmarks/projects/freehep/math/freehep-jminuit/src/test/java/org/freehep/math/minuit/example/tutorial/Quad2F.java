package org.freehep.math.minuit.example.tutorial;

import org.freehep.math.minuit.FCNGradientBase;

/**
 *
 * @version $Id: Quad2F.java 8584 2006-08-10 23:06:37Z duns $
 */
class Quad2F implements FCNGradientBase
{ 
   public double valueOf(double[] par)
   {
      double x = par[0];
      double y = par[1];
      
      return x*x  + y*y;
   }
   
   public double[] gradient(double[] par)
   {
      
      double x = par[0];
      double y = par[1];
      
      double[] result = { 2.*x, 2.*y };
      return result;
   }
}
