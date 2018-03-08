package org.freehep.math.minuit.example.sim;

import org.freehep.math.minuit.FCNBase;

/**
 *
 * @version $Id: GaussFcn2.java 8584 2006-08-10 23:06:37Z duns $
 */
class GaussFcn2 implements FCNBase
{
   GaussFcn2(double[] meas, double[] pos, double[] mvar)
   {
      theMeasurements = meas;
      thePositions = pos;
      theMVariances = mvar;
      theMin =0;
      init();
   }
   
   void init()
   {
      // calculate initial value of chi2
      int nmeas = theMeasurements.length;
      double x = 0.;
      double x2 = 0.;
      double norm = 0.;
      double dx = thePositions[1]-thePositions[0];
      double c = 0.;
      for(int i = 0; i < nmeas; i++)
      {
         norm += theMeasurements[i];
         x += (theMeasurements[i]*thePositions[i]);
         x2 += (theMeasurements[i]*thePositions[i]*thePositions[i]);
         c += dx*theMeasurements[i];
      }
      double mean = x/norm;
      double rms2 = x2/norm - mean*mean;
      
      double[] par = { mean, Math.sqrt(rms2), c, mean, Math.sqrt(rms2), c };
      theMin = valueOf(par);
   }
   
   public double valueOf(double[] par)
   {
      assert(par.length == 6);
      
      GaussFunction gauss1 = new GaussFunction(par[0], par[1], par[2]);
      GaussFunction gauss2 = new GaussFunction(par[3], par[4], par[5]);
      
      double chi2 = 0.;
      int nmeas = theMeasurements.length;
      for(int n = 0; n < nmeas; n++)
      {
         double e1 = gauss1.valueAt(thePositions[n]) + gauss2.valueAt(thePositions[n]) - theMeasurements[n];
         chi2 += e1*e1/theMVariances[n];
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
   private double theMin;
}