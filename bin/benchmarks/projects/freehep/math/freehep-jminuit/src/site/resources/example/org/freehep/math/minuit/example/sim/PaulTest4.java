package org.freehep.math.minuit.example.sim;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import org.freehep.math.minuit.FCNBase;
import org.freehep.math.minuit.FunctionMinimum;
import org.freehep.math.minuit.MnMigrad;
import org.freehep.math.minuit.MnUserParameters;
import org.freehep.math.minuit.MnSimplex;

/**
 *
 * @version $Id: PaulTest4.java 8584 2006-08-10 23:06:37Z duns $
 */
public class PaulTest4
{
   public static void main(String[] args)
   {
      List<Double> positions = new ArrayList<Double>();
      List<Double> measurements = new ArrayList<Double>();
      List<Double> var = new ArrayList<Double>();
      
      Scanner in = new Scanner(PaulTest.class.getResourceAsStream("paul4.txt"));
      
      // read input data
      {
         while(in.hasNextDouble())
         {
            double x = in.nextDouble();
            double y = in.nextDouble();
            double err = in.nextDouble();
            
            positions.add(x);
            measurements.add(y);
            var.add(err*err);
         }
         System.out.printf("size= %d\n",var.size());
      }
      double[] m = new double[var.size()];
      double[] p = new double[var.size()];
      double[] v = new double[var.size()];
      for (int i=0; i<var.size(); i++)
      {
         m[i] = measurements.get(i);
         p[i] = positions.get(i);
         v[i] = var.get(i);
      }
      {
         // create Chi2 FCN function
         System.out.println(">>> test Chi2");
         PowerLawChi2FCN theFCN = new PowerLawChi2FCN(m, p, v);
         
         MnUserParameters upar = new MnUserParameters();
         upar.add("p0", -2.3, 0.2);
         upar.add("p1", 1100., 10.);
         
         MnMigrad migrad = new MnMigrad(theFCN, upar);
         migrad.setErrorDef(0.5);
         System.out.println("start migrad ");
         FunctionMinimum min = migrad.minimize();
         if(!min.isValid())
         {
            //try with higher strategy
            System.out.println("FM is invalid, try with strategy = 2.");
            migrad = new MnMigrad(theFCN, upar, 2);
            min = migrad.minimize();
         }
         System.out.println("minimum: "+min);
      }
      {
         System.out.println(">>> test log LikeliHood");
         // create LogLikelihood FCN function
         PowerLawLogLikeFCN theFCN = new PowerLawLogLikeFCN(m, p);
         
         MnUserParameters upar = new MnUserParameters();
         upar.add("p0", -2.1, 0.2);
         upar.add("p1", 1000., 10.);
         
         MnMigrad migrad = new MnMigrad(theFCN, upar);
         System.out.println("start migrad ");
         FunctionMinimum min = migrad.minimize();
         if(!min.isValid())
         {
            //try with higher strategy
            System.out.println("FM is invalid, try with strategy = 2.");
            migrad = new MnMigrad(theFCN, upar, 2);
            min = migrad.minimize();
         }
         System.out.println("minimum: "+min);
      }
      {
         System.out.println(">>> test Simplex");
         PowerLawChi2FCN chi2 = new PowerLawChi2FCN(m, p, v);
         PowerLawLogLikeFCN mlh = new PowerLawLogLikeFCN(m, p);
         
         MnUserParameters upar;
         double[] par = {-2.3, 1100.};
         double[] err = { 1., 1.};
         
         MnSimplex simplex = new MnSimplex(chi2, par, err);
         
         System.out.println("start simplex");
         FunctionMinimum min = simplex.minimize();
         System.out.println("minimum: "+min);
         
         MnSimplex simplex2 = new MnSimplex(mlh, par, err);   
         simplex2.setErrorDef(0.5);
         FunctionMinimum min2 = simplex2.minimize();
         System.out.println("minimum: "+min2);
      }
   }
   static class PowerLawFunc
   {
      
      PowerLawFunc(double p0, double p1)
      {
         theP0 = p0;
         theP1 = p1;
      }
      
      double valueOf(double x)
      {
         return p1()*Math.exp(Math.log(x)*p0());
      }
      
      double p0()
      {
         return theP0;
      }
      double p1()
      {
         return theP1;
      }
      
      private double theP0;
      private double theP1;
   };
   static class PowerLawChi2FCN implements FCNBase
   {
      
      PowerLawChi2FCN(double[] meas, double[] pos, double[] mvar)
      {
         theMeasurements = meas;
         thePositions = pos;
         theMVariances = mvar;
      }
      
      public double valueOf(double[] par)
      {
         assert(par.length == 2);
         PowerLawFunc pl = new PowerLawFunc(par[0], par[1]);
         double chi2 = 0.;
         
         for(int n = 0; n < theMeasurements.length; n++)
         {
            chi2 += ((pl.valueOf(thePositions[n]) - theMeasurements[n])*(pl.valueOf(thePositions[n]) - theMeasurements[n])/theMVariances[n]);
         }
         
         return chi2;
      }
      
      private double[] theMeasurements;
      private double[] thePositions;
      private double[] theMVariances;
   };
   static class PowerLawLogLikeFCN implements FCNBase
   {
      
      PowerLawLogLikeFCN(double[] meas, double[] pos)
      {
         theMeasurements = meas;
         thePositions = pos;
      }
      
      
      public double valueOf(double[] par)
      {
         assert(par.length == 2);
         PowerLawFunc pl = new PowerLawFunc(par[0], par[1]);
         double logsum = 0.;
         
         for(int n = 0; n < theMeasurements.length; n++)
         {
            double k = theMeasurements[n];
            double mu = pl.valueOf(thePositions[n]);
            logsum += (k*Math.log(mu) - mu);
         }
         
         return -logsum;
      }
      
      private double[] theMeasurements;
      private double[] thePositions;
   };
} 
