
package org.freehep.commons.sqlutils;

import junit.framework.TestCase;
import org.freehep.commons.sqlutils.schema.Table0001;
import org.freehep.commons.sqlutils.schema.Table0001x;

/**
 *
 * @author bvan
 */
public class InsertTest extends TestCase {
    public InsertTest(String testName){
        super( testName );
    }
    
    public void testInsert(){
        String misMatch = "Insert statement mismatch, expected: '%s', actual: '%s'";
        Table0001 t0001 = new Table0001();
        Table0001x t0001x = new Table0001x();
        
        String expected0001 = "INSERT INTO Table0001 TABLE Table0001x";
        String actual = new Insert().into( t0001 ).source( t0001x ).formatted();
        check(misMatch, expected0001, actual);

        String expected0002 = "INSERT INTO Table0001 SELECT pk, name FROM Table0001x";
        actual = new Insert().into( t0001 ).source( t0001x.selectAllColumns() ).formatted();
        check(misMatch, expected0002, actual);
        
        // TODO: Is this the spec?
        String expected0003 = "INSERT INTO Table0001_Names ( name ) TABLE Table0001";
        actual = new Insert().into( new Table("Table0001_Names") )
                .columns( t0001.name).source( t0001 ).formatted();
        check(misMatch, expected0003, actual);
        
        String expected0004 = "INSERT INTO Table0001_Names ( name ) SELECT name FROM Table0001";
        actual = new Insert().into( new Table("Table0001_Names") )
                .columns( t0001.name).source( t0001.select(t0001.name) ).formatted();
        check(misMatch, expected0004, actual);
        
        t0001.as( "alias0001");
        
        String expected0005 = "INSERT INTO Table0001_Names ( name ) TABLE Table0001";
        actual = new Insert().into( new Table("Table0001_Names") )
                .columns( t0001.name ).source( t0001 ).formatted();
        check(misMatch, expected0005, actual);
        
        String expected0006 = "INSERT INTO Table0001_Names ( name ) SELECT alias0001.name FROM Table0001 alias0001";
        Table t1n = new Table("Table0001_Names");
        Column cSel = t0001.name;
        
        actual = new Insert().into( new Table("Table0001_Names") )
                .columns( t1n._( "name") )
                .source( t0001.select( cSel ) )
                .formatted();
        check(misMatch, expected0006, actual);

        String expected0007 = "INSERT INTO Table0001_Names ( name ) SELECT alias0001.name nAlias FROM Table0001 alias0001";
        Select sel0001 = t0001.select( cSel.as( "nAlias") );
        actual = new Insert().into( new Table("Table0001_Names") )
                .columns( t1n._( "name") )
                .source( sel0001 )
                .formatted();
        check(misMatch, expected0007, actual);
        
        // Make sure Select alias doesn't propagate
        String expected0008 = "INSERT INTO Table0001_Names ( name ) SELECT alias0001.name nAlias FROM Table0001 alias0001";
        t1n = new Table("Table0001_Names");
        actual = new Insert().into( new Table("Table0001_Names") )
                .columns( t1n._( "name") )
                .source( sel0001.as( "alias0001x") )
                .formatted();
        check(misMatch, expected0008, actual);
        
        //String expected0002 = "INSERT INTO Table0001 TABLE Table0001x";
        //String actual = new Insert().into( t0001 ).source( t0001x ).formatted();
    }
    
    
    
    
    
    
    void check(String message, String expected, String actual){
        assertTrue(String.format( message, expected, actual), expected.equals( actual));
    }
}
