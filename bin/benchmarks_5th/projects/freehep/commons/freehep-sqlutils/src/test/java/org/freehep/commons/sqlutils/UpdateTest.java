
package org.freehep.commons.sqlutils;

import static junit.framework.Assert.fail;
import junit.framework.TestCase;
import org.freehep.commons.sqlutils.schema.Table0001;

/**
 *
 * @author bvan
 */
public class UpdateTest extends TestCase {
    
    public void testUpdate(){
        String misMatch = "Update mismatch, expected: '%s', actual: '%s'";
        
        Table0001 t0001 = new Table0001();
        
        try{
            new Update( new Table0001() ).formatted();
            fail("Should have thrown a runtime exception: Update needs a clause");
        } catch (RuntimeException rx){ }
        
        String expected0001 = "UPDATE Table0001 SET name = 'test'";
        Update upd = new Update( t0001 ).set( t0001.name.eq( "test"));
        String actual = upd.formatted();
        check(misMatch, expected0001, actual);

        String expected0002 = "UPDATE Table0001 SET name = 'test' WHERE pk > 1234";
        upd = new Update( t0001 )
                .set( t0001.name.eq( "test"))
                .where( t0001.pk.gt( 1234L ));
        actual = upd.formatted();
        check(misMatch, expected0002, actual);
        
        String expected0003 = "UPDATE Table0001 SET name = 'test', pk = 4321 WHERE pk = 1234";
        upd = new Update( t0001 )
                .set( t0001.name.eq( "test"), t0001.pk.eq( 4321L) )
                .where( t0001.pk.eq( 1234L ));
        actual = upd.formatted();
        check(misMatch, expected0003, actual);
        
    }
    
    void check(String message, String expected, String actual){
        assertTrue(String.format( message, expected, actual), expected.equals( actual));
    }
}
