/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.freehep.commons.sqlutils;

import junit.framework.TestCase;
import org.freehep.commons.sqlutils.format.PrettyFormatter;
import org.freehep.commons.sqlutils.schema.Table0001;
import org.freehep.commons.sqlutils.schema.Table0001x;

/**
 *
 * @author bvan
 */
public class SelectTest extends TestCase {
    public SelectTest(String testName){
        super( testName );
    }
    
    @Override
    protected void setUp() throws Exception{
        super.setUp();
    }
    
    public void testUnionAll(){
        String msg = "Union mismatch. Expected: '%s', actual: '%s'";
        
        Table0001 t0001 = new Table0001();
        Table0001x t0001x = new Table0001x();
        String expected0001 = "SELECT pk, name FROM Table0001 UNION ALL SELECT pk, name FROM Table0001x";
        String actual = Select
                .unionAll( t0001.selectAllColumns(), t0001x.selectAllColumns())
                .formatted();
        check(msg, expected0001, actual);
        
        String expected0002 = "SELECT pk, name FROM Table0001 WHERE pk = 1234 UNION ALL SELECT pk, name FROM Table0001x WHERE pk = 1234";
        actual = Select
                .unionAll( t0001.selectAllColumns(), t0001x.selectAllColumns())
                .where( t0001.pk.eq( 1234L))
                .formatted();
        check(msg, expected0002, actual);
        
        String expected0003 = "SELECT t1_t1x.pk, t1_t1x.name FROM (SELECT pk, name FROM Table0001 UNION ALL SELECT pk, name FROM Table0001x ) t1_t1x WHERE t1_t1x.pk = 1234";
        Select uni = Select.unionAll( t0001.selectAllColumns(), t0001x.selectAllColumns());
        actual = new Select( uni.as( "t1_t1x").getColumns() )
                .from( uni )
                .where( uni.getSelection( "pk" ).eq( 1234L))
                .formatted();
        check(msg, expected0003, actual);
        
        PrettyFormatter formatter = new PrettyFormatter();
        System.out.println( formatter.format( actual ));
    }
    
    void check(String message, String expected, String actual){
        assertTrue(String.format( message, expected, actual), expected.equals( actual));
    }
}
