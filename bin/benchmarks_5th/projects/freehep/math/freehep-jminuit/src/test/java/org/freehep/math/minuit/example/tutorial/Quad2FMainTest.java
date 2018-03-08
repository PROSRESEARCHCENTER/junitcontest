package org.freehep.math.minuit.example.tutorial;

import java.util.List;
import junit.framework.TestCase;
import org.freehep.math.minuit.FunctionMinimum;
import org.freehep.math.minuit.MnContours;
import org.freehep.math.minuit.MnMigrad;
import org.freehep.math.minuit.MnUserParameters;
import org.freehep.math.minuit.Point;

/**
 *
 * @version $Id: Quad2FMainTest.java 8584 2006-08-10 23:06:37Z duns $
 */
public class Quad2FMainTest extends TestCase
{
   
   public Quad2FMainTest(String testName)
   {
      super(testName);
   }
   
   public static junit.framework.Test suite()
   {
      
      junit.framework.TestSuite suite = new junit.framework.TestSuite(Quad2FMainTest.class);
      return suite;
   }
   
   /**
    * Test of main method, of class org.freehep.math.minuit.tests.tutorial.Quad1FMain.
    */
   public void testQuad2F1()
   {
      Quad2F fcn = new Quad2F();
      MnUserParameters upar = new MnUserParameters();
      upar.add("x", 1., 0.1);
      upar.add("y", 1., 0.1);
      MnMigrad migrad = new MnMigrad(fcn, upar);
      migrad.setUseAnalyticalDerivatives(false);
      FunctionMinimum min = migrad.minimize();
      assertTrue(min.isValid());
      assertEquals(20,min.nfcn());
      assertEquals(3.9075e-20,min.fval(),1e-25);
      assertEquals(3.9075e-20,min.edm(), 1e-25);
   }
   public void testQuad2F2()
   {
      // using VariableMetricMinimizer, analytical derivatives
      Quad2F fcn = new Quad2F();
      double[] par = {1., 1.};
      double[] err = {0.1, 0.1};
      MnMigrad migrad = new MnMigrad(fcn, par, err);
      FunctionMinimum min = migrad.minimize();
      assertTrue(min.isValid());
      assertEquals(24,min.nfcn());
      assertEquals(8.87469e-31,min.fval(),1e-36);
      assertEquals(8.87483e-31,min.edm(), 1e-36);
   }
   public void testQuad2F3()
   {
      // test Contours for two parameters
      Quad2F fcn = new Quad2F();
      double[] par = { 1., 1.};
      double[] err = { 0.1, 0.1};
      MnMigrad migrad = new MnMigrad(fcn, par, err);
      FunctionMinimum min = migrad.minimize();
      MnContours contours = new MnContours(fcn, min);
      //1-sigma around the minimum
      List<Point> cont = contours.points(0, 1, 1, 20);
      assertEquals(-1,cont.get(0).first,1e-7);
      assertEquals(6.66134e-16,cont.get(0).second,1e-21);      
      assertEquals(-0.92388,cont.get(19).first,1e-6);
      assertEquals(0.382683,cont.get(19).second,1e-6);   
      //2-sigma around the minimum
      System.out.println("2-sigma contours");
      List<Point> cont4 = contours.points(0, 1, 4, 20);
      assertEquals(-2,cont4.get(0).first,1e-7);
      assertEquals(6.66134e-16,cont4.get(0).second,1e-21);      
      assertEquals(-1.84776,cont4.get(19).first,1e-6);
      assertEquals(0.765367,cont4.get(19).second,1e-6); 
   }
}
