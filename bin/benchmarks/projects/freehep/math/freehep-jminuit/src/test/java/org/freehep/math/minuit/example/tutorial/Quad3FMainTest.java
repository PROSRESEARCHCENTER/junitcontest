package org.freehep.math.minuit.example.tutorial;

import junit.framework.TestCase;
import org.freehep.math.minuit.FunctionMinimum;
import org.freehep.math.minuit.MnMigrad;
import org.freehep.math.minuit.MnUserParameters;

/**
 *
 * @version $Id: Quad3FMainTest.java 8584 2006-08-10 23:06:37Z duns $
 */
public class Quad3FMainTest extends TestCase
{
   
   public Quad3FMainTest(String testName)
   {
      super(testName);
   }
   
   public static junit.framework.Test suite()
   {
      
      junit.framework.TestSuite suite = new junit.framework.TestSuite(Quad3FMainTest.class);
      return suite;
   }
   
   /**
    * Test of main method, of class org.freehep.math.minuit.tests.tutorial.Quad1FMain.
    */
   public void test1()
   {
      // using migrad, numerical derivatives
      Quad3F fcn = new Quad3F();
      MnUserParameters upar = new MnUserParameters();
      upar.add("x", 1., 0.1);
      upar.add("y", 1., 0.1);
      upar.add("z", 1., 0.1);
      MnMigrad migrad = new MnMigrad(fcn, upar);
      migrad.setUseAnalyticalDerivatives(false);
      FunctionMinimum min = migrad.minimize();
      assertTrue(min.isValid());
      assertEquals(30,min.nfcn());
      assertEquals(5.86125e-20,min.fval(),1e-25);
      assertEquals(5.86125e-20,min.edm(), 1e-25);
   }
   public void test2()
   {
      // using VariableMetricMinimizer, analytical derivatives
      Quad3F fcn = new Quad3F();
      double[] par = {1.,1.,1.};
      double[] err = {1.,1.,1.};
      MnMigrad migrad = new MnMigrad(fcn, par, err);
      FunctionMinimum min = migrad.minimize();
      assertTrue(min.isValid());
      assertEquals(24,min.nfcn());
      assertEquals(0,min.fval(),1e-35);
      assertEquals(0,min.edm(), 1e-35);
   }
}