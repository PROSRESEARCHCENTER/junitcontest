
package org.freehep.commons.sqlutils;

import junit.framework.TestCase;
import org.freehep.commons.sqlutils.schema.Table0001;
import org.freehep.commons.sqlutils.schema.Table0001Named;

/**
 *
 * @author bvan
 */
public class DeleteTest extends TestCase  {
    
    
    public void testDelete(){
        String misMatch = "Delete mismatch, expected: '%s', actual: '%s'";
        
        Table0001 t0001 = new Table0001();
        
        try{
            new Delete().from( new Table0001() ).formatted();
            fail("Should have thrown a runtime exception: delete statement is protected");
        } catch (RuntimeException rx){ }
        
        String expected0001 = "DELETE FROM Table0001";
        Delete del = new Delete().from( new Table0001() );
        del.setProtected( false );
        String actual = del.formatted();
        del = new Delete(new Table0001());
        del.setProtected( false );
        actual = del.formatted();
        check(misMatch, expected0001, actual);
        
        String expected0002 = "DELETE FROM Table0001 WHERE pk = 1234";
        actual = new Delete().from( t0001 ).where( t0001.pk.eq( 1234L)).formatted();
        check(misMatch, expected0002, actual);
        actual = new Delete(t0001).where( t0001.pk.eq( 1234L)).formatted();
        check(misMatch, expected0002, actual);
    }
    
    void check(String message, String expected, String actual){
        assertTrue(String.format( message, expected, actual), expected.equals( actual));
    }
}
