package org.freehep.math.minuit.example.sim;

import org.freehep.math.minuit.FCNBase;

/**
 *
 * @version $Id: GaussFcn.java 8584 2006-08-10 23:06:37Z duns $
 */
class GaussFcn implements FCNBase
{
   GaussFcn(double[] meas, double[] pos, double[] mvar)
   {
      theMeasurements = meas;
      thePositions = pos;
      theMVariances = mvar;
   }
   
   public double valueOf(double[] par)
   {
      assert(par.length == 3);
      GaussFunction gauss = new GaussFunction(par[0], par[1], par[2]);
      
      double chi2 = 0.;
      for(int n = 0; n < theMeasurements.length; n++)
      {
         chi2 += ((gauss.valueAt(thePositions[n]) - theMeasurements[n])*(gauss.valueAt(thePositions[n]) - theMeasurements[n])/theMVariances[n]);
      }
      return chi2;
   }
   
   double[] measurements()
   {
      return theMeasurements;
   }
   double[] positions()
   {
      return thePositions;
   }
   double[] variances()
   {
      return theMVariances;
   }
   
   private double[] theMeasurements;
   private double[] thePositions;
   private double[] theMVariances;
}