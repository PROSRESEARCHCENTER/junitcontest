package org.freehep.math.minuit.example.tutorial;

import org.freehep.math.minuit.FCNGradientBase;

/**
 *
 * @version $Id
 */
class Quad1F implements FCNGradientBase
{   
   public double valueOf(double[] par)
   {
      double x = par[0];
      return x*x;
   }
   public double[] gradient(double[] par)
   {
      double x = par[0];
      return new double[]{ 2*x };
   }
}
