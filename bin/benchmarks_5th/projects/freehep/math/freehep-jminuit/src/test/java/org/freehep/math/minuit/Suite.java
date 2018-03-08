package org.freehep.math.minuit;

import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 *
 * @version $Id: Suite.java 8584 2006-08-10 23:06:37Z duns $
 */
public class Suite extends TestCase
{
   
   private TestSuite suite;
   
   public Suite(java.lang.String testName)
   {
      super(testName);
      suite = (TestSuite) suite();
   }
   
   private TestSuite getSuite()
   {
      return suite;
   }
   
   public static junit.framework.Test suite()
   {
      TestSuite suite = new TestSuite();
      
      // Add all the test suites here
      
      suite.addTestSuite(MnAlgebraicSymMatrixTest.class);
      suite.addTestSuite(MnMachinePrecisionTest.class);
      suite.addTestSuite(MnUserTransformationTest.class);
      suite.addTest(org.freehep.math.minuit.example.tutorial.Suite.suite());
      suite.addTest(org.freehep.math.minuit.example.sim.Suite.suite());
      return suite;
   }
}
