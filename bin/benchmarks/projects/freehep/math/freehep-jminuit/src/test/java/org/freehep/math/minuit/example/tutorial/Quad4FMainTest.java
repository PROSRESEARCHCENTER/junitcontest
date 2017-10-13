package org.freehep.math.minuit.example.tutorial;

import junit.framework.TestCase;
import org.freehep.math.minuit.FunctionMinimum;
import org.freehep.math.minuit.MnMigrad;
import org.freehep.math.minuit.MnUserParameters;


/**
 *
 * @version $Id: Quad4FMainTest.java 8584 2006-08-10 23:06:37Z duns $
 */
public class Quad4FMainTest extends TestCase
{
   
   public Quad4FMainTest(String testName)
   {
      super(testName);
   }
   
   public static junit.framework.Test suite()
   {
      
      junit.framework.TestSuite suite = new junit.framework.TestSuite(Quad4FMainTest.class);
      return suite;
   }
   
   /**
    * Test of main method, of class org.freehep.math.minuit.tests.tutorial.Quad1FMain.
    */
   public void test1()
   {
      Quad4F fcn = new Quad4F();
      MnUserParameters upar = new MnUserParameters();
      upar.add("x", 1., 0.1);
      upar.add("y", 1., 0.1);
      upar.add("z", 1., 0.1);
      upar.add("w", 1., 0.1);

      MnMigrad migrad = new MnMigrad(fcn, upar);
      FunctionMinimum min = migrad.minimize();
      assertTrue(min.isValid());
      assertEquals(74,min.nfcn());
      assertEquals(1.12392e-09,min.fval(),1e-14);
      assertEquals(1.12392e-09,min.edm(), 1e-14);
   }

}