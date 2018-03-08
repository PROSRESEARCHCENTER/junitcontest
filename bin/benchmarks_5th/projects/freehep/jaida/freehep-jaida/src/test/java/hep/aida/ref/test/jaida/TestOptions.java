package hep.aida.ref.test.jaida;

import hep.aida.ref.AidaUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import junit.framework.TestCase;


/**
 *
 * @author tonyj
 * @version $Id: TestOptions.java 10546 2007-02-21 23:39:01Z duns $
 */
public class TestOptions extends TestCase
{
   public TestOptions(String testName)
   {
      super(testName);
   }

   public void testOptionsParsing()
   {
      String tests[] = {
         "a= b ;c = d ",
         "a=\"Some Options\",c= \"My , Funny Value\" ",
         "testCase",
         "  ",
         null,
         "username=,password=,sql=\"select * from Gooddata\",url=\"jdbc:odbc:DRIVER={Microsoft Access Driver (*.mdb)};DBQ=C:\\Documents and Settings\\tonyj\\My Documents\\mq.mdb\",driver=sun.jdbc.odbc.JdbcOdbcDriver",
         "a=b, hep.aida.ref.sql.user=test"
      };
      int count[] = { 2, 2, 1, 0, 0, 5, 2};
      
      for (int i=0; i<tests.length; i++)
      {
         Map result = AidaUtils.parseOptions(tests[i]);
         assertEquals(result.size(),count[i]);
         switch (i)
         {
            case 0: assertEquals(result.get("a"),"b"); assertEquals(result.get("c"),"d"); break;
            case 1: assertEquals(result.get("a"), "Some Options"); assertEquals(result.get("c"),"My , Funny Value"); break;
            case 2: assertEquals(result.get("testCase"),"true"); break;
            case 5: assertEquals(result.get("username"),""); assertEquals(result.get("url"),"jdbc:odbc:DRIVER={Microsoft Access Driver (*.mdb)};DBQ=C:\\Documents and Settings\\tonyj\\My Documents\\mq.mdb"); break;
            case 6: assertEquals(result.get("hep.aida.ref.sql.user"),"test"); break;
            default: 
         }
      }
   }
   public void testOptionsParsing2()
   {
      Random r = new Random();
      int n = 1000;
      int max = 100;
      Map in = new HashMap();
      StringBuffer options = new StringBuffer(max*n);
      for (int i=0; i<n; i++)
      {
         int size = r.nextInt(max);
         StringBuffer buf = new StringBuffer(size);
         for (int j=0; j<size; j++) 
         {
            char c = (char) (32+ r.nextInt(64));
            if (c != '"') buf.append(c);
         }
         in.put("key"+i, buf.toString());
         options.append("key");
         options.append(i);
         options.append("=\"");
         options.append(buf);
         options.append("\"");
         options.append(";");
      }
      Map out = AidaUtils.parseOptions(options.toString());
      assertEquals(in,out);
   }

    public void testOptionsParsing3() {
        String options = "annotation.xAxis=date, annotation.customOverlay=hep.aida.ref.plotter.adapter.TimeHistoryOverlay";
        String[] array = AidaUtils.parseString(options);
        assertEquals(array.length,2);
        assertEquals(array[0], "annotation.xAxis=date");
        assertEquals(array[1], "annotation.customOverlay=hep.aida.ref.plotter.adapter.TimeHistoryOverlay");
        

        String path = "/hist/dir1/subdir/h11";
        String name = AidaUtils.parseName(path);
        String dir = AidaUtils.parseDirName(path);
        assertEquals(name,"h11");
        assertEquals(dir,"/hist/dir1/subdir/");
        
        path = "/dir0/";
        name = AidaUtils.parseName(path);
        dir = AidaUtils.parseDirName(path);
        assertEquals(name,"");
        assertEquals(dir,"/dir0/");
        
        path = "/hist1D";
        name = AidaUtils.parseName(path);
        dir = AidaUtils.parseDirName(path);
        assertEquals(name,"hist1D");
        assertEquals(dir,"/");
        
        path = "hist1D";
        name = AidaUtils.parseName(path);
        dir = AidaUtils.parseDirName(path);
        assertEquals(name,"hist1D");
        assertEquals(dir,"");
        
        path = "/hist/dir1/subdir/";
        name = AidaUtils.parseName(path);
        dir = AidaUtils.parseDirName(path);
        assertEquals(name,"");
        assertEquals(dir,"/hist/dir1/subdir/");
        
    }
    
    
}
