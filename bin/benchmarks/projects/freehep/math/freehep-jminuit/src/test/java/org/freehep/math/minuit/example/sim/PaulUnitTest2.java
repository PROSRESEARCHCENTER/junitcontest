package org.freehep.math.minuit.example.sim;

import junit.framework.TestCase;
import junit.framework.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import org.freehep.math.minuit.FunctionMinimum;
import org.freehep.math.minuit.MinosError;
import org.freehep.math.minuit.MnMigrad;
import org.freehep.math.minuit.MnMinos;
import org.freehep.math.minuit.MnUserParameters;

/**
 *
 * @version $Id: PaulUnitTest2.java 8584 2006-08-10 23:06:37Z duns $
 */
public class PaulUnitTest2 extends TestCase
{
   public PaulUnitTest2(String testName)
   {
      super(testName);
   }
   
   public static junit.framework.Test suite()
   {
      
      junit.framework.TestSuite suite = new junit.framework.TestSuite(PaulUnitTest2.class);
      return suite;
   }
   public static void testPaulTest2() throws IOException
   {
      List<Double> positions = new ArrayList<Double>();
      List<Double> measurements = new ArrayList<Double>();
      List<Double> var = new ArrayList<Double>();
      int nmeas = 0;
      
      Scanner in = new Scanner(PaulTest.class.getResourceAsStream("paul2.txt"));
      
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
      GaussFcn theFCN = new GaussFcn(m, p, v);
      
      double[] meas = theFCN.measurements();
      double[] pos = theFCN.positions();
      
      // create initial starting values for parameters
      double x = 0.;
      double x2 = 0.;
      double norm = 0.;
      double area = 0.;
      double dx = pos[1]-pos[0];
      for(int i = 0; i < meas.length; i++)
      {
         norm += meas[i];
         x += (meas[i]*pos[i]);
         x2 += (meas[i]*pos[i]*pos[i]);
         area += dx*meas[i];
      }
      double mean = x/norm;
      double rms2 = x2/norm - mean*mean;
      
      double[] init_val =
      { mean, Math.sqrt(rms2), area};
      
      MnUserParameters upar = new MnUserParameters();
      upar.add("mean", mean, 1.);
      upar.add("sigma", Math.sqrt(rms2), 1.);
      upar.add("area", area, 10.);
      
      MnMigrad migrad = new MnMigrad(theFCN, upar);
      FunctionMinimum min = migrad.minimize();
      
      assertTrue(min.isValid());
      assertEquals(86,min.nfcn());
      assertEquals(805.354,min.fval(),1e-3);
      assertEquals(5.43032e-11,min.edm(),1e-15);
      assertEquals(22.2893, min.userParameters().value(0),1e-4);
      assertEquals(5.32324, min.userParameters().value(1),1e-5);
      assertEquals(2564.95, min.userParameters().value(2),1e-2);
      assertEquals(0.1087, min.userParameters().error(0),1e-4);
      assertEquals(0.08852, min.userParameters().error(1),1e-5);
      assertEquals(51.07, min.userParameters().error(2),1e-2);
      assertEquals(0.0118217,min.userCovariance().get(0,0),1e-7);
      assertEquals(0.00116002,min.userCovariance().get(1,0),1e-8);
      assertEquals(0.12912,min.userCovariance().get(2,0),1e-5);
      assertEquals(0.00783559,min.userCovariance().get(1,1),1e-8);
      assertEquals(0.193434,min.userCovariance().get(1,2),1e-6);
      assertEquals(2608.35,min.userCovariance().get(2,2),1e-2);
      
      System.out.println("start minos");
      MnMinos minos = new MnMinos(theFCN, min);
      MinosError e0 = minos.minos(0);
      MinosError e1 = minos.minos(1);
      MinosError e2 = minos.minos(2);
      
      assertTrue(e0.isValid());
      assertEquals(-0.108695,e0.lower(),1e-5);
      assertEquals(0.108737,e0.upper(),1e-5);
      assertTrue(e1.isValid());
      assertEquals(-0.087359,e1.lower(),1e-5);
      assertEquals(0.0888145,e1.upper(),1e-5);
      assertTrue(e2.isValid());
      assertEquals(-50.8563,e2.lower(),1e-4);
      assertEquals(50.856,e2.upper(),1e-4);
   }
   
}
