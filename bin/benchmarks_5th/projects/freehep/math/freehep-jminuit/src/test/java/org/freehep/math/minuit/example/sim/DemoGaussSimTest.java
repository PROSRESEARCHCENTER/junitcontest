package org.freehep.math.minuit.example.sim;

import junit.framework.TestCase;
import junit.framework.*;
import java.io.IOException;
import java.util.List;
import org.freehep.math.minuit.ContoursError;
import org.freehep.math.minuit.FunctionMinimum;
import org.freehep.math.minuit.MinosError;
import org.freehep.math.minuit.MnContours;
import org.freehep.math.minuit.MnMigrad;
import org.freehep.math.minuit.MnMinos;
import org.freehep.math.minuit.MnPlot;
import org.freehep.math.minuit.MnUserParameters;
import org.freehep.math.minuit.Point;

/**
 *
 * @version $Id: DemoGaussSimTest.java 8584 2006-08-10 23:06:37Z duns $
 */
public class DemoGaussSimTest extends TestCase
{
   private double mean, rms, area;
   private GaussFcn theFCN;
   
   public DemoGaussSimTest(String testName)
   {
      super(testName);
   }
   
   public static junit.framework.Test suite()
   {
      junit.framework.TestSuite suite = new junit.framework.TestSuite(PaulUnitTest.class);
      return suite;
   }
   
   protected void setUp() throws java.lang.Exception
   {
      GaussDataGen gdg = new GaussDataGen(DemoGaussSim.class.getResourceAsStream("GaussDataGen.txt"));
      
      double[] pos = gdg.positions();
      double[] meas = gdg.measurements();
      double[] var = gdg.variances();
      
      // create FCN function
      theFCN = new GaussFcn(meas, pos, var);
      
      // create initial starting values for parameters
      double x = 0.;
      double x2 = 0.;
      double norm = 0.;
      double dx = pos[1]-pos[0];
      area = 0.;
      for( int i = 0; i < meas.length; i++)
      {
         norm += meas[i];
         x += (meas[i]*pos[i]);
         x2 += (meas[i]*pos[i]*pos[i]);
         area += dx*meas[i];
      }
      mean = x/norm;
      double rms2 = x2/norm - mean*mean;
      rms = rms2 > 0. ? Math.sqrt(rms2) : 1.;
   }
   
   public void testDemoGaussSim1()
   {
      // starting values for parameters
      double[] init_par =
      { mean, rms, area };
      
      // starting values for initial uncertainties
      double[] init_err =
      { 0.1, 0.1, 0.1 };
      
      // create minimizer (default constructor)
      MnMigrad migrad = new MnMigrad(theFCN, init_par, init_err);
      
      // minimize
      FunctionMinimum min = migrad.minimize();
      
      assertTrue(min.isValid());
      assertEquals(70,min.nfcn());
      assertEquals(88.3066,min.fval(),1e-4);
      assertEquals(1.71831e-8,min.edm(),1e-13);
      assertEquals(34.0075, min.userParameters().value(0),1e-4);
      assertEquals(2.25102, min.userParameters().value(1),1e-5);
      assertEquals(1.01683, min.userParameters().value(2),1e-5);
      assertEquals(0.04170, min.userParameters().error(0),1e-5);
      assertEquals(0.04332, min.userParameters().error(1),1e-5);
      assertEquals(0.01653, min.userParameters().error(2),1e-5);
      assertEquals(0.00173909,min.userCovariance().get(0,0),1e-8);
      assertEquals(1.28972e-005,min.userCovariance().get(1,0),1e-10);
      assertEquals(2.89595e-006,min.userCovariance().get(2,0),1e-11);
      assertEquals(0.00187654,min.userCovariance().get(1,1),1e-8);
      assertEquals(0.000423809,min.userCovariance().get(1,2),1e-9);
      assertEquals(0.000273141,min.userCovariance().get(2,2),1e-9);
   }
   public void testDemoGaussSim2()
   {
      // demonstrate standard minimization using MIGRAD
      // create Minuit parameters with names
      MnUserParameters upar = new MnUserParameters();
      upar.add("mean", mean, 0.1);
      upar.add("sigma", rms, 0.1);
      upar.add("area", area, 0.1);
      
      // create MIGRAD minimizer
      MnMigrad migrad = new MnMigrad(theFCN, upar);
      
      // minimize
      FunctionMinimum min = migrad.minimize();
      
      assertTrue(min.isValid());
      assertEquals(70,min.nfcn());
      assertEquals(88.3066,min.fval(),1e-4);
      assertEquals(1.71831e-8,min.edm(),1e-13);
      assertEquals(34.0075, min.userParameters().value(0),1e-4);
      assertEquals(2.25102, min.userParameters().value(1),1e-5);
      assertEquals(1.01683, min.userParameters().value(2),1e-5);
      assertEquals(0.04170, min.userParameters().error(0),1e-5);
      assertEquals(0.04332, min.userParameters().error(1),1e-5);
      assertEquals(0.01653, min.userParameters().error(2),1e-5);
      assertEquals(0.00173909,min.userCovariance().get(0,0),1e-8);
      assertEquals(1.28972e-005,min.userCovariance().get(1,0),1e-10);
      assertEquals(2.89595e-006,min.userCovariance().get(2,0),1e-11);
      assertEquals(0.00187654,min.userCovariance().get(1,1),1e-8);
      assertEquals(0.000423809,min.userCovariance().get(1,2),1e-9);
      assertEquals(0.000273141,min.userCovariance().get(2,2),1e-9);
   }
   public void testDemoGaussSim3()
   {
      // demonstrate full interaction with parameters over subsequent
      // minimizations
      
      // create Minuit parameters with names
      MnUserParameters upar = new MnUserParameters();
      upar.add("mean", mean, 0.1);
      upar.add("sigma", rms, 0.1);
      upar.add("area", area, 0.1);
      
      // access parameter by name to set limits...
      upar.setLimits("mean", mean-0.01, mean+0.01);
      
      // ... or access parameter by index
      upar.setLimits(1, rms-0.1, rms+0.1);
      
      // create Migrad minimizer
      MnMigrad migrad = new MnMigrad(theFCN, upar);
      
      // fix a parameter...
      migrad.fix("mean");
      
      // ... and minimize
      FunctionMinimum min = migrad.minimize();
      
      assertTrue(min.isValid());
      assertEquals(45,min.nfcn());
      assertEquals(146.33,min.fval(),1e-2);
      assertEquals(8.66356e-007,min.edm(),1e-12);
      assertEquals(34.17, min.userParameters().value(0),1e-2);
      assertEquals(2.564, min.userParameters().value(1),1e-3);
      assertEquals(1.08, min.userParameters().value(2),1e-3);
      assertEquals(0.003823, min.userParameters().error(1),1e-6);
      assertEquals(0.01422, min.userParameters().error(2),1e-5);
      assertEquals(9.37847e-012,min.userCovariance().get(0,0),1e-17);
      assertEquals(1.12707e-011,min.userCovariance().get(1,0),1e-16);
      assertEquals(0.000202116,min.userCovariance().get(1,1),1e-9);
      
      // release a parameter...
      migrad.release("mean");
      
      // ... and fix another one
      migrad.fix(1);
      
      // and minimize again
      min = migrad.minimize();
      
      assertTrue(min.isValid());
      assertEquals(38,min.nfcn());
      assertEquals(144.797,min.fval(),1e-3);
      assertEquals(2.39746e-008,min.edm(),1e-12);
      assertEquals(34.16, min.userParameters().value(0),1e-2);
      assertEquals(2.564, min.userParameters().value(1),1e-3);
      assertEquals(1.08, min.userParameters().value(2),1e-3);
      assertEquals(0.01301, min.userParameters().error(0),1e-5);
      assertEquals(0.01422, min.userParameters().error(2),1e-5);
      assertEquals(8.78799e-014,min.userCovariance().get(0,0),1e-19);
      assertEquals(-1.6233e-013,min.userCovariance().get(1,0),1e-17);
      assertEquals(0.000202116,min.userCovariance().get(1,1),1e-9);
      
      // release the parameter...
      migrad.release(1);
      
      // ... and minimize with all three parameters (still with limits!)
      min = migrad.minimize();
      
      assertTrue(min.isValid());
      assertEquals(34,min.nfcn());
      assertEquals(144.797,min.fval(),1e-3);
      assertEquals(6.14672e-012,min.edm(),1e-16);
      assertEquals(34.16, min.userParameters().value(0),1e-2);
      assertEquals(2.564, min.userParameters().value(1),1e-3);
      assertEquals(1.08, min.userParameters().value(2),1e-3);
      assertEquals(0.01301, min.userParameters().error(0),1e-5);
      assertEquals(0.00381, min.userParameters().error(1),1e-5);
      assertEquals(0.01422, min.userParameters().error(2),1e-5);
      //      assertEquals(2.26114e-021,min.userCovariance().get(0,0),1e-26);
      //      assertEquals(1.81642e-029,min.userCovariance().get(1,0),1e-34);
      //      assertEquals(-2.62338e-017,min.userCovariance().get(2,0),1e-22);
      //      assertEquals(9.9027e-023,min.userCovariance().get(1,1),1e-27);
      //      assertEquals(4.1886e-017,min.userCovariance().get(1,2),1e-21);
      //      assertEquals(0.000202116,min.userCovariance().get(2,2),1e-9);
      
      // remove all limits on parameters...
      migrad.removeLimits("mean");
      migrad.removeLimits("sigma");
      
      // ... and minimize again with all three parameters (now without limits!)
      min = migrad.minimize();
      
      assertTrue(min.isValid());
      assertEquals(68,min.nfcn());
      assertEquals(88.3066,min.fval(),1e-4);
      assertEquals(1.0233e-009,min.edm(),1e-13);
      assertEquals(34.0075, min.userParameters().value(0),1e-4);
      assertEquals(2.25102, min.userParameters().value(1),1e-5);
      assertEquals(1.01683, min.userParameters().value(2),1e-5);
      assertEquals(0.04170, min.userParameters().error(0),1e-5);
      assertEquals(0.04332, min.userParameters().error(1),1e-5);
      assertEquals(0.01653, min.userParameters().error(2),1e-5);
      assertEquals(0.00173909,min.userCovariance().get(0,0),1e-8);
      assertEquals(1.29012e-005,min.userCovariance().get(1,0),1e-10);
      assertEquals(2.89609e-006,min.userCovariance().get(2,0),1e-11);
      assertEquals(0.00187655,min.userCovariance().get(1,1),1e-8);
      assertEquals(0.000423811,min.userCovariance().get(1,2),1e-9);
      assertEquals(0.000273141,min.userCovariance().get(2,2),1e-9);
      
   }
   public void testDemoGaussSim4()
   {
      // test single sided limits
      MnUserParameters upar = new MnUserParameters();
      upar.add("mean", mean, 0.1);
      upar.add("sigma", rms-1., 0.1);
      upar.add("area", area, 0.1);
      
      // test lower limits
      upar.setLowerLimit("mean", mean-0.01);
      
      // test upper limits
      upar.setUpperLimit("sigma", rms-0.5);
      
      // create MIGRAD minimizer
      MnMigrad migrad = new MnMigrad(theFCN, upar);
      
      // ... and minimize
      FunctionMinimum min = migrad.minimize();
      
      assertTrue(min.isValid());
      assertEquals(94,min.nfcn());
      assertEquals(106.789,min.fval(),1e-3);
      assertEquals( 1.65312e-007,min.edm(),1e-12);
      assertEquals(34.16, min.userParameters().value(0),1e-2);
      assertEquals(2.164, min.userParameters().value(1),1e-3);
      assertEquals(0.9955, min.userParameters().value(2),1e-4);
      assertEquals(0.005398, min.userParameters().error(0),1e-6);
      assertEquals(0.009405, min.userParameters().error(1),1e-6);
      assertEquals(0.01306, min.userParameters().error(2),1e-5);
      //      assertEquals(9.88027e-012,min.userCovariance().get(0,0),1e-17);
      //      assertEquals(-9.21938e-019,min.userCovariance().get(1,0),1e-24);
      //      assertEquals( 1.20884e-012,min.userCovariance().get(2,0),1e-17);
      //      assertEquals(2.52747e-011,min.userCovariance().get(1,1),1e-16);
      //      assertEquals(6.34517e-011,min.userCovariance().get(1,2),1e-16);
      //      assertEquals(0.000170588,min.userCovariance().get(2,2),1e-9);
   }
   public void testDemoGaussSim5()
   {
      // demonstrate MINOS error analysis
      
      // create Minuit parameters with names
      MnUserParameters upar = new MnUserParameters();
      upar.add("mean", mean, 0.1);
      upar.add("sigma", rms, 0.1);
      upar.add("area", area, 0.1);
      
      // create Migrad minimizer
      MnMigrad migrad = new MnMigrad(theFCN, upar);
      
      // minimize
      FunctionMinimum min = migrad.minimize();
      
      // create MINOS error factory
      MnMinos minos = new MnMinos(theFCN, min);
      
      {
         assertEquals(-0.0416995,minos.lower(0),1e-7);
         assertEquals(0.0417135,minos.upper(0),1e-7);
         assertEquals(-0.0428834,minos.lower(1),1e-7);
         assertEquals(0.0437614,minos.upper(1),1e-7);
         assertEquals(-0.0164772,minos.lower(2),1e-7);
         assertEquals(0.016578,minos.upper(2),1e-7);
      }
      
      {
         // 2-sigma MINOS errors (rich interface)
         MinosError e0 = minos.minos(0,4.);
         MinosError e1 = minos.minos(1,4.);
         MinosError e2 = minos.minos(2,4.);
         
         assertTrue(e0.isValid());
         assertEquals(56,e0.nfcn());
         assertEquals(-0.08343,e0.lower(),1e-5);
         assertEquals(0.08347,e0.upper(),1e-5);
         assertTrue(e1.isValid());
         assertEquals(76,e1.nfcn());
         assertEquals(-0.08466,e1.lower(),1e-5);
         assertEquals( 0.08903,e1.upper(),1e-5);
         assertTrue(e2.isValid());
         //assertEquals(56,e2.nfcn());
         assertEquals(-0.03283,e2.lower(),1e-5);
         assertEquals(0.03332,e2.upper(),1e-5);
      }
   }
   public void testDemoGaussSim6()
   {
      // demonstrate how to use the CONTOURs
      
      // create Minuit parameters with names
      MnUserParameters upar = new MnUserParameters();
      upar.add("mean", mean, 0.1);
      upar.add("sigma", rms, 0.1);
      upar.add("area", area, 0.1);
      
      // create Migrad minimizer
      MnMigrad migrad = new MnMigrad(theFCN, upar);
      
      // minimize
      FunctionMinimum min = migrad.minimize();
      
      // create contours factory with FCN and minimum
      MnContours contours = new MnContours(theFCN, min);
    
      //95% confidence level for 2 parameters contour
      // (rich interface)
      ContoursError cont4 = contours.contour(0, 1, 5.99, 20);
      
      assertEquals(33.91,cont4.points().get(0).first,1e-2);
      assertEquals(2.253,cont4.points().get(0).second,1e-3);
      assertEquals(34.11,cont4.points().get(10).first,1e-2);
      assertEquals(2.254,cont4.points().get(10).second,1e-3);
      assertEquals(33.91,cont4.points().get(19).first,1e-2);
      assertEquals(2.293,cont4.points().get(19).second,1e-3);
   }
}
