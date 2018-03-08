package hep.aida.ref.test.jaida;

import junit.framework.TestCase;

/**
 *
 * @author The FreeHEP Team @ SLAC.
 *
 */
public class TestAidaSpecs extends TestCase {
    
    public TestAidaSpecs(String name) {
        super(name);
    }
    
    public static junit.framework.Test suite() {
        junit.framework.TestSuite suite = new junit.framework.TestSuite() {
            protected void tearDown() {
                System.out.println("********** Tearing down the test suite ");
            }            
        };        
        hep.aida.test.TestSuite t = new hep.aida.test.TestSuite("tmp");
        t.addStandardTests(suite);
        return suite;
    }
    
}
