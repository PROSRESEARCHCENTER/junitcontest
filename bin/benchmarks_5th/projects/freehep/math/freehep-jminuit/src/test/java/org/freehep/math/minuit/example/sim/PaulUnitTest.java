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
 * @version $Id: PaulUnitTest.java 8584 2006-08-10 23:06:37Z duns $
 */
public class PaulUnitTest extends TestCase
{
   
   public PaulUnitTest(String testName)
   {
      super(testName);
   }

   public static junit.framework.Test suite()
   {

      junit.framework.TestSuite suite = new junit.framework.TestSuite(PaulUnitTest.class);
      return suite;
   }

   /**
    * Test of main method, of class org.freehep.math.minuit.tests.sim.PaulTest.
    */
   public void testMain()
   {

      List<Double> positions = new ArrayList<Double>();
      List<Double> measurements = new ArrayList<Double>();
      List<Double> var = new ArrayList<Double>();
      int nmeas = 0;
      
      Scanner in = new Scanner(PaulUnitTest.class.getResourceAsStream("paul.txt"));
      
      // read input data
      {
         while(in.hasNextDouble())
         {
            double x = in.nextDouble();
            double weight = in.nextDouble();
            double width = in.nextDouble();
            double err = in.nextDouble();
            
            positions.add(x);
            double ni = weight*width;
            measurements.add(ni);
            var.add(ni);
            nmeas += ni;
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
      double dx = pos[1]-pos[0];
      double area = 0.;
      for( int i = 0; i < meas.length; i++)
      {
         norm += meas[i];
         x += (meas[i]*pos[i]);
         x2 += (meas[i]*pos[i]*pos[i]);
         area += dx*meas[i];
      }
      double mean = x/norm;
      double rms2 = x2/norm - mean*mean;
      
      MnUserParameters upar = new MnUserParameters();
      upar.add("mean", mean, 0.1);
      upar.add("sigma", Math.sqrt(rms2), 0.1);
      upar.add("area", area, 0.1);
      
      MnMigrad migrad = new MnMigrad(theFCN, upar);
      FunctionMinimum min = migrad.minimize();
      
      assertTrue(min.isValid());
      assertEquals(52,min.nfcn());
      assertEquals(33.3891,min.fval(),1e-4);
      assertEquals(2.10274e-06,min.edm(), 1e-11);      
      
      System.out.println("start minos");
      MnMinos minos = new MnMinos(theFCN, min);
      MinosError e0 = minos.minos(0);
      MinosError e1 = minos.minos(1);
      MinosError e2 = minos.minos(2);
      
      assertTrue(e0.isValid());
      assertEquals(min.userState().value(0),0.989562,1e-6);
      assertEquals(e0.lower(),-0.018585,1e-6);
      assertEquals(e0.upper(),0.0185529,1e-6);
      
      assertTrue(e1.isValid());
      assertEquals(min.userState().value(1),0.287736,1e-6);
      assertEquals(e1.lower(),-0.0148909,1e-6);
      assertEquals(e1.upper(),0.0155851,1e-6);
      
      assertTrue(e2.isValid());
      assertEquals(min.userState().value(2),13.5005,1e-4);
      assertEquals(e2.lower(),-0.82966,1e-6);
      assertEquals(e2.upper(),0.827595,1e-6);
      
   }  
}
