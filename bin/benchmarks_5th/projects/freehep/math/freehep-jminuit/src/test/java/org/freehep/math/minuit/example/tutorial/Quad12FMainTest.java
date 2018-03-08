package org.freehep.math.minuit.example.tutorial;

import junit.framework.TestCase;
import org.freehep.math.minuit.FunctionMinimum;
import org.freehep.math.minuit.MnMigrad;
import org.freehep.math.minuit.MnUserParameters;

/**
 *
 * @version $Id: Quad12FMainTest.java 8584 2006-08-10 23:06:37Z duns $
 */
public class Quad12FMainTest extends TestCase
{
   
   public Quad12FMainTest(String testName)
   {
      super(testName);
   }
   
   public static junit.framework.Test suite()
   {
      
      junit.framework.TestSuite suite = new junit.framework.TestSuite(Quad12FMainTest.class);
      return suite;
   }
   
   /**
    * Test of main method, of class org.freehep.math.minuit.tests.tutorial.Quad1FMain.
    */
   public void test1()
   {
      Quad12F fcn = new Quad12F();
      //test constructor
      MnUserParameters upar = new MnUserParameters();
      upar.add("x", 1., 0.1);
      upar.add("y", 1., 0.1);
      upar.add("z", 1., 0.1);
      upar.add("w", 1., 0.1);
      upar.add("x0", 1., 0.1);
      upar.add("y0", 1., 0.1);
      upar.add("z0", 1., 0.1);
      upar.add("w0", 1., 0.1);
      upar.add("x1", 1., 0.1);
      upar.add("y1", 1., 0.1);
      upar.add("z1", 1., 0.1);
      upar.add("w1", 1., 0.1);

      MnMigrad migrad = new MnMigrad(fcn, upar);
      FunctionMinimum min = migrad.minimize();
      assertEquals(260,min.nfcn());
      assertEquals(3.37177e-09,min.fval(),1e-15);
      assertEquals(3.37177e-09,min.edm(), 1e-15);
   }

}