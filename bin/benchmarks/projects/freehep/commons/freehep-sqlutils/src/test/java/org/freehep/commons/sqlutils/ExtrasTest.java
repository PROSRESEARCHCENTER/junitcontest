
package org.freehep.commons.sqlutils;

import junit.framework.TestCase;
import org.freehep.commons.sqlutils.extras.Case;
import org.freehep.commons.sqlutils.schema.Table0001;

/**
 *
 * @author bvan
 */
public class ExtrasTest extends TestCase {

    
    public void testCaseStatement(){
        String message = "Case statement mismatch: expected '%s', actual '%s'";
        
        Table0001 t1 = new Table0001();
        
        String expected0001 = "SELECT CASE WHEN pk = 1234 THEN '1234' ELSE '4321' END FROM Table0001";
        Val<String> _then = new Val<>("1234");
        Val<String> _else = new Val<>("4321");
        Case _case = new Case(t1.pk.eq( 1234L), _then, _else);
        String actual = new Select( _case ).from( t1 ).formatted();
        check(message, expected0001, actual);
        
        String expected0002 = "SELECT CASE WHEN pk = 1234 THEN '1234' ELSE '4321' END fakeName FROM Table0001";
        _case = new Case(t1.pk.eq( 1234L), _then, _else).as( "fakeName", Case.class);
        actual = new Select( _case ).from( t1 ).formatted();
        check(message, expected0002, actual);
        
        t1.as( "t1");
        String expected0003 = "SELECT CASE WHEN t1.pk = 1234 THEN '1234' ELSE '4321' END fakeName FROM Table0001 t1";
        _case = new Case(t1.pk.eq( 1234L), _then, _else).as( "fakeName", Case.class);
        actual = new Select( _case ).from( t1 ).formatted();
        check(message, expected0003, actual);
        
        String expected0004 = "SELECT CASE WHEN t1.pk = 1234 THEN '1234' ELSE t1.name END fakeName FROM Table0001 t1";
        _case = new Case(t1.pk.eq( 1234L), _then, t1.name).as( "fakeName", Case.class);
        actual = new Select( _case ).from( t1 ).formatted();
        check(message, expected0004, actual);

    }
    
    void check(String message, String expected, String actual){
        assertTrue(String.format( message, expected, actual), expected.equals( actual));
    }
}
