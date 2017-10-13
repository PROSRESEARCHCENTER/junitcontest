package hep.aida.ref.test.jaida;

import hep.aida.IAnalysisFactory;
import hep.aida.ITreeFactory;
import hep.aida.test.TestRoot;

/**
 *
 * @author Tony Johnson
 */
public class TestXRootDaemon extends TestRoot
{
   public TestXRootDaemon(String testName)
   {
      super(testName);
   }
   
   protected void openFile() throws java.io.IOException
   {
      // Open the test data
      IAnalysisFactory af = IAnalysisFactory.create();
      ITreeFactory tf = af.createTreeFactory();
      tree = tf.create("xroot://glast-xrootd01.slac.stanford.edu/u/gl/glast/xrootd/testdata/pawdemo.root","xroot",true,false,"scheme=anonymous");
   }   
}
