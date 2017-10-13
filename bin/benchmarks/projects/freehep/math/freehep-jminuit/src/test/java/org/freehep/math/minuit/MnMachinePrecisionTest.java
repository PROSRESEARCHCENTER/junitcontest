package org.freehep.math.minuit;

import junit.framework.TestCase;
import junit.framework.*;

/**
 *
 * @author tonyj
 */
public class MnMachinePrecisionTest extends TestCase
{
   
   public MnMachinePrecisionTest(String testName)
   {
      super(testName);
   }

   protected void setUp() throws java.lang.Exception
   {
   }

   protected void tearDown() throws java.lang.Exception
   {
   }

   public static junit.framework.Test suite()
   {
      junit.framework.TestSuite suite = new junit.framework.TestSuite(MnMachinePrecisionTest.class);
      return suite;
   }

   /**
    * Test of eps method, of class org.freehep.minuit.MnMachinePrecision.
    */
   public void testEps()
   {
      MnMachinePrecision prec = new MnMachinePrecision();
      
      assertTrue(prec.eps()<1e-15);
      assertTrue(prec.eps()>1e-16);
   }

   /**
    * Test of eps2 method, of class org.freehep.minuit.MnMachinePrecision.
    */
   public void testEps2()
   {
      MnMachinePrecision prec = new MnMachinePrecision();
      
      assertTrue(prec.eps2()<1e-7);
      assertTrue(prec.eps2()>1e-8);
   }

   /**
    * Test of setPrecision method, of class org.freehep.minuit.MnMachinePrecision.
    */
   public void testSetPrecision()
   {
      MnMachinePrecision prec = new MnMachinePrecision();
      prec.setPrecision(1e-14);
      assertEquals(1e-14,prec.eps(),1e-16);
      assertEquals(2*Math.sqrt(1e-14),prec.eps2(),1e-16);
   }
}
