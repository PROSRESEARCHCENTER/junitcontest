package hep.io.root.daemon.xrootd;

import java.io.IOException;
import junit.framework.TestCase;

/**
 *
 * @author tonyj
 */
public class XrootdPrepareTest extends TestCase
{
   
   public XrootdPrepareTest(String testName)
   {
      super(testName);
   }
   
   public void testPrepare() throws IOException
   {
      if (!XrootdConnectionTest.isAtSLAC()) return;
      Session handle = new Session("glast-rdr.slac.stanford.edu",1094,"tonyj");
      handle.ping();
      
      String file = "/glast/mc/DC2/ChickenLittle-GR-v7r3p24-2/029/614/ChickenLittle-GR-v7r3p24-2_029614_merit_merit.root";
      FileStatus status = handle.stat(file);      
      
      handle = new Session("glast-rdr.slac.stanford.edu",1094,"tonyj");
      handle.ping();
      String[] pList = {file};
      int options = XrootdProtocol.kXR_stage | XrootdProtocol.kXR_notify;
      int priority = 3;
      
      String msg = handle.prepare(pList,options,priority);
      System.out.println(msg);
   }
   
}
