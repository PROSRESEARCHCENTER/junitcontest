package org.freehep.util.test;

import java.util.Enumeration;
import java.util.Iterator;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import junit.textui.TestRunner;
import org.freehep.util.DoubleHashtable;

/**
 *
 * @author duns
 * @version $Id: DoubleHashtableTest.java 8584 2006-08-10 23:06:37Z duns $
 */
public class DoubleHashtableTest extends TestCase
{
   
   public DoubleHashtableTest(java.lang.String testName)
   {
      super(testName);
   }
   
   public static Test suite()
   {
      TestSuite suite = new TestSuite(DoubleHashtableTest.class);
      return suite;
   }
   
   public void testTable()
   {
      DoubleHashtable table = new DoubleHashtable();
      
      assertTrue(table.isEmpty());
      
      table.put("Donszelmann", "Mark", "CERN");
      table.put("Donszelmann", "Mark", "SLAC");
      table.put("Donszelmann", "Niels", "Knoworries");
      table.put("Johnson", "Tony", "SLAC");
      table.put(null, "Mark", "Family");
      table.put("Donszelmann", null, "Family");
      table.put(null, null, "Family");
      table.put("Perl", "Joseph", null);
      
      assertFalse(table.isEmpty());
      
      assertNotNull(table.get("Donszelmann"));
      assertNotNull(table.get("Donszelmann", "Mark"));
      assertEquals(table.get("Donszelmann", "Mark"),"SLAC");
      assertNotNull(table.get("Donszelmann", null));
      
      assertEquals(table.get("Donszelmann", null),"Family");
      assertNotNull(table.get(null, null));
      
      assertNotNull(table.get(null, "Mark"));
      
      assertTrue(table.containsKey("Perl", "Joseph"));
      assertNull(table.get("Perl", "Joseph"));
      
      table.remove("Johnson", "Tony");
      assertNull(table.get("Johnson", "Tony"));
      
      int count = 0;
      for (Enumeration e=table.elements(); e.hasMoreElements(); )
      {
         e.nextElement();
         count++;
      }
      assertEquals(count,table.size());
      
      for (Iterator i=table.iterator(); i.hasNext(); )
      {
         String value = (String)i.next();
         if ((value != null) && value.equals("SLAC"))
         {
            i.remove();
         }
      }
      assertNull(table.get("Donszelmann","Mark"));
      
      table.clear();
      assertTrue(table.isEmpty());
   }
   
   public static void main(java.lang.String[] args)
   {
      TestRunner.run(suite());
   }
}
