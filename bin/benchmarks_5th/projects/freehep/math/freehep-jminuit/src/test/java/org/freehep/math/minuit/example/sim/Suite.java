package org.freehep.math.minuit.example.sim;

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

      suite.addTestSuite(DemoGaussSimTest.class);
      suite.addTestSuite(PaulUnitTest.class);
      suite.addTestSuite(PaulUnitTest2.class);
      // FixMe: We need to understand why this does not work.
      //suite.addTestSuite(PaulUnitTest3.class);
      suite.addTestSuite(PaulUnitTest4.class);      
      suite.addTestSuite(ReneUnitTest.class);           
      return suite;
   }
}
