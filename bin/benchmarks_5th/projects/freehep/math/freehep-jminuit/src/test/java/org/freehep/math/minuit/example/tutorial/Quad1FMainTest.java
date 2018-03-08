package org.freehep.math.minuit.example.tutorial;

import junit.framework.TestCase;
import org.freehep.math.minuit.FunctionMinimum;
import org.freehep.math.minuit.MinosError;
import org.freehep.math.minuit.MnMigrad;
import org.freehep.math.minuit.MnMinos;
import org.freehep.math.minuit.MnUserParameters;

/**
 *
 * @version $Id: Quad1FMainTest.java 8584 2006-08-10 23:06:37Z duns $
 */
public class Quad1FMainTest extends TestCase
{
   
   public Quad1FMainTest(String testName)
   {
      super(testName);
   }
   
   public static junit.framework.Test suite()
   {
      
      junit.framework.TestSuite suite = new junit.framework.TestSuite(Quad1FMainTest.class);
      return suite;
   }
   
   /**
    * Test of main method, of class org.freehep.math.minuit.tests.tutorial.Quad1FMain.
    */
   public void testQuad1F1()
   {
      Quad1F fcn = new Quad1F();
      MnUserParameters upar = new MnUserParameters();
      upar.add("x", 1., 0.1);
      MnMigrad migrad = new MnMigrad(fcn, upar);
      migrad.setUseAnalyticalDerivatives(false);
      FunctionMinimum min = migrad.minimize();
      assertTrue(min.isValid());
      assertEquals(13,min.nfcn());
      assertEquals(6.83776e-21,min.fval(),1e-26);
      assertEquals(6.83776e-21,min.edm(), 1e-26);
   }
   public void testQuad1F2()
   {
      // using VariableMetricMinimizer, analytical derivatives
      Quad1F fcn = new Quad1F();
      double[] par = {1.};
      double[] err = {0.1};
      MnMigrad migrad = new MnMigrad(fcn,par,err);
      FunctionMinimum min = migrad.minimize();
      assertTrue(min.isValid());
      assertEquals(15,min.nfcn());
      assertEquals(4.43734e-31,min.fval(),1e-35);
      assertEquals(4.43734e-31,min.edm(), 1e-35);
   }
   public void testQuad1F3()
   {
      // test Minos for one parameter
      Quad1F fcn = new Quad1F();
      double[] par = {1.};
      double[] err = {0.1 };
      MnMigrad migrad = new MnMigrad(fcn,par, err);
      FunctionMinimum min = migrad.minimize();
      MnMinos minos = new MnMinos(fcn, min);
      MinosError me = minos.minos(0);
      assertTrue(me.isValid());
      assertEquals(min.userState().value(0),6.66134e-16,1e-20);
      assertEquals(me.lower(),-1,1e-7);
      assertEquals(me.upper(),+1,1e-7);
      
      MnMinos minos2 = new MnMinos(fcn, min);
      MinosError me2 = minos.minos(0,4);
      assertTrue(me2.isValid());
      assertEquals(min.userState().value(0),6.66134e-16,1e-20);
      assertEquals(me2.lower(),-2,1e-7);
      assertEquals(me2.upper(),+2,1e-7);     
   }
}
