package hep.aida.ref.remote.test.remoteAida;

import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 *
 * @author The AIDA team @ SLAC.
 *
 */
public class RAIDATestSuite extends TestCase {
    
    private TestSuite suite;
    
    public RAIDATestSuite(java.lang.String testName) {
        super(testName);
        
        suite = (TestSuite) suite();
    }
    
    private TestSuite getSuite() {
        return suite;
    }
    
    private TestCase getTest( String testName ) {
        for ( int i = 0; i < this.getSuite().testCount(); i++ ) {
            TestSuite testSuite = (TestSuite)(getSuite().testAt(i));
            for ( int j = 0; j < testSuite.testCount(); j++ ) {
                TestCase test = (TestCase)(testSuite.testAt(j));
                if ( test.getName().equals( testName ) ) return test;
            }
        }
        return null;
    }
    
    
    public static junit.framework.Test suite() {
        TestSuite suite = new TestSuite();
        
        // Add all the test suites here
        suite.addTestSuite( TestRHistogram.class );	
        suite.addTestSuite( TestRCloud.class );	
        suite.addTestSuite( TestRDataPointSet.class );	
        suite.addTestSuite( TestRTree.class );	
        
        return suite;
    }
}
