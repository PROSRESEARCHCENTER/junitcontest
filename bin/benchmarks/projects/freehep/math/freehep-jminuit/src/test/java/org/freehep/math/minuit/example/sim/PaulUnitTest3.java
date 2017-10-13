package org.freehep.math.minuit.example.sim;

import junit.framework.TestCase;
import junit.framework.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import org.freehep.math.minuit.FunctionMinimum;
import org.freehep.math.minuit.MnMigrad;
import org.freehep.math.minuit.MnUserParameters;

/**
 *
 * @version $Id: PaulUnitTest3.java 8584 2006-08-10 23:06:37Z duns $
 */
public class PaulUnitTest3 extends TestCase
{
   public PaulUnitTest3(String testName)
   {
      super(testName);
   }
   
   public static junit.framework.Test suite()
   {
      
      junit.framework.TestSuite suite = new junit.framework.TestSuite(PaulUnitTest3.class);
      return suite;
   }
   public static void testPaulTest3()
   {
      List<Double> positions = new ArrayList<Double>();
      List<Double> measurements = new ArrayList<Double>();
      List<Double> var = new ArrayList<Double>();
      double nmeas = 0;
      
      Scanner in = new Scanner(PaulTest.class.getResourceAsStream("paul3.txt"));
      
      // read input data
      {
         while(in.hasNextDouble())
         {
            double x = in.nextDouble();
            double y = in.nextDouble();
            double width = in.nextDouble();
            double err = in.nextDouble();
            double unl = in.nextDouble();
            double un2 = in.nextDouble();
            if(err < 1.e-8) continue;
            positions.add(x);
            measurements.add(y);
            var.add(err*err);
            nmeas += y;
         }
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
      // create FCN function
      GaussFcn2 theFCN = new GaussFcn2(m, p, v);
      
      double[] meas = theFCN.measurements();
      double[] pos = theFCN.positions();
      
      // create initial starting values for parameters
      double x = 0.;
      double x2 = 0.;
      double norm = 0.;
      double dx = pos[1]-pos[0];
      double area = 0.;
      for (int i = 0; i < meas.length; i++)
      {
         norm += meas[i];
         x += (meas[i]*pos[i]);
         x2 += (meas[i]*pos[i]*pos[i]);
         area += dx*meas[i];
      }
      double mean = x/norm;
      double rms2 = x2/norm - mean*mean;
      
      double[] init_val = { mean, Math.sqrt(rms2), area, mean, Math.sqrt(rms2), area };
      
      MnUserParameters upar = new MnUserParameters();
      upar.add("mean1", mean, 10.);
      upar.add("sig1", Math.sqrt(rms2), 10.);
      upar.add("area1", area, 10.);
      upar.add("mean2", mean, 10.);
      upar.add("sig2", Math.sqrt(rms2), 10.);
      upar.add("area2", area, 10.);
      
      MnMigrad migrad = new MnMigrad(theFCN, upar);
      FunctionMinimum min = migrad.minimize();
      if(!min.isValid())
      {
         //try with higher strategy
         MnMigrad migrad2 = new MnMigrad(theFCN, upar, 2);
         min = migrad2.minimize();
      }
      assertTrue(min.isValid());
      assertEquals(447,min.nfcn());
      assertEquals(230.957,min.fval(),1e-3);
      assertEquals(2.55173e-06,min.edm(),1e-11);
      
      assertEquals(7090.35, min.userParameters().value(0),1e-2);
      assertEquals(1162.14, min.userParameters().value(1),1e-2);
      assertEquals(1802.39, min.userParameters().value(2),1e-2);
      assertEquals(10344.4, min.userParameters().value(3),1e-1);
      assertEquals(3457.85, min.userParameters().value(4),1e-2);
      assertEquals(1343.35, min.userParameters().value(5),1e-2);
      
      assertEquals(45.62, min.userParameters().error(0),1e-2);
      assertEquals(42.5, min.userParameters().error(1),1e-2);
      assertEquals(75.05, min.userParameters().error(2),1e-2);
      assertEquals(188.5, min.userParameters().error(3),1e-1);
      assertEquals(87.4, min.userParameters().error(4),1e-2);
      assertEquals(73.07, min.userParameters().error(5),1e-2);
   }
}