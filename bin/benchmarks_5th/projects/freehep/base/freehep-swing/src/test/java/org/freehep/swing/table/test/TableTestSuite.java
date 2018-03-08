package org.freehep.swing.table.test;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 *
 * @author Tony Johnson
 * @version $Id: TableTestSuite.java 8584 2006-08-10 23:06:37Z duns $
 *
 */
public class TableTestSuite extends TestCase
{
   
   private TestSuite suite;
   
   public TableTestSuite(java.lang.String testName)
   {
      super(testName);
      suite = (TestSuite) suite();
   }
   
   public TestSuite getSuite()
   {
      return suite;
   }
   
   public static Test suite()
   {
      TestSuite suite = new TestSuite();
      
      // Add all the test suites here
      
      suite.addTestSuite( DefaultSortableTableModelTest.class );
      
      return suite;
   }
}
