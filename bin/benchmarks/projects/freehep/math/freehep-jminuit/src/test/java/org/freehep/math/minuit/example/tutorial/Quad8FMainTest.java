package org.freehep.math.minuit.example.tutorial;

import junit.framework.TestCase;
import org.freehep.math.minuit.FunctionMinimum;
import org.freehep.math.minuit.MnMigrad;
import org.freehep.math.minuit.MnUserParameters;

/**
 *
 * @version $Id: Quad8FMainTest.java 8584 2006-08-10 23:06:37Z duns $
 */
public class Quad8FMainTest extends TestCase
{
   
   public Quad8FMainTest(String testName)
   {
      super(testName);
   }
   
   public static junit.framework.Test suite()
   {
      
      junit.framework.TestSuite suite = new junit.framework.TestSuite(Quad8FMainTest.class);
      return suite;
   }
   
   /**
    * Test of main method, of class org.freehep.math.minuit.tests.tutorial.Quad1FMain.
    */
   public void test1()
   {
      Quad8F fcn = new Quad8F();
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

      MnMigrad migrad = new MnMigrad(fcn, upar);
      FunctionMinimum min = migrad.minimize();
      assertTrue(min.isValid());
      assertEquals(160,min.nfcn());
      assertEquals(2.24785e-09,min.fval(),1e-14);
      assertEquals(2.24785e-09,min.edm(), 1e-14);
   }

}