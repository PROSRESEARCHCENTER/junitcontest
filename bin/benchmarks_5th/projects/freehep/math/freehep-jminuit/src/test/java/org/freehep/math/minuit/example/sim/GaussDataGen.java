package org.freehep.math.minuit.example.sim;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;
import java.util.Scanner;

/**
 *
 * @version $Id: GaussDataGen.java 8584 2006-08-10 23:06:37Z duns $
 */
class GaussDataGen
{
   private Random random = new Random();
   GaussDataGen()
   {
      this(100);
   }
   GaussDataGen(InputStream in) throws IOException
   {
      Scanner scanner = new Scanner(in);
      int npar = scanner.nextInt();
      theSimMean = scanner.nextDouble();
      theSimVar = scanner.nextDouble();
      
      thePositions = new double[npar];
      theMeasurements = new double[npar];
      theVariances = new double[npar];
      
      for (int i=0; i<npar; i++)
      {
         thePositions[i] = scanner.nextDouble();
         theMeasurements[i] = scanner.nextDouble();
         theVariances[i] = scanner.nextDouble();
      }
   }
   GaussDataGen(int npar)
   {
      thePositions = new double[npar];
      theMeasurements = new double[npar];
      theVariances = new double[npar];
      
      // errors of measurements (Gaussian, mean=0., sig = 0.01)
      double mvariance = 0.01*0.01;
      
      // simulate data
      theSimMean = nextFlat(0,50);
      theSimVar = nextFlat(6,5);
      double sim_sig = Math.sqrt(theSimVar);
      double sim_const = 1.;
      GaussFunction gauss_sim = new GaussFunction(theSimMean, sim_sig, sim_const);
      
      for (int i = 0; i < npar; i++)
      {
         
         //x-position, from -5sigma < mean < +5sigma
         double position = theSimMean-5.*sim_sig + i*10.*sim_sig/npar;
         thePositions[i] = position;
         
         //y-position (function value)
         double epsilon = nextGaussian(0., 0.01);
         theMeasurements[i] = gauss_sim.valueAt(position) + epsilon;
         theVariances[i] = mvariance;
      }
   }
   private double nextFlat(double mean, double delta)
   {
      return 2.*delta*(random.nextDouble() - 0.5) + mean;
   }
   private double nextGaussian(double mean, double sigma)
   {
      return random.nextGaussian()*sigma + mean;
   }
   double[] positions()
   {
      return thePositions;
   }
   double[] measurements()
   {
      return theMeasurements;
   }
   double[] variances()
   {
      return theVariances;
   }
   
   double sim_mean()
   {
      return theSimMean;
   }
   double sim_var()
   {
      return theSimVar;
   }
   double sim_const()
   {
      return 1.;
   }
   
   private double theSimMean;
   private double theSimVar;
   private double[] thePositions;
   private double[] theMeasurements;
   private double[] theVariances;
}
