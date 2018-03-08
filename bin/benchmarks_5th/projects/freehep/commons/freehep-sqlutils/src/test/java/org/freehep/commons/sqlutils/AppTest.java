package org.freehep.commons.sqlutils;

import org.freehep.commons.sqlutils.Val;
import java.util.Arrays;
import java.util.HashMap;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.freehep.commons.sqlutils.Column;
import org.freehep.commons.sqlutils.Delete;
import org.freehep.commons.sqlutils.Insert;
import org.freehep.commons.sqlutils.Op;
import org.freehep.commons.sqlutils.Param;
import org.freehep.commons.sqlutils.Sql;
import org.freehep.commons.sqlutils.Select;
import org.freehep.commons.sqlutils.Table;
import static org.freehep.commons.sqlutils.Op.*;
import org.freehep.commons.sqlutils.Update;
import org.freehep.commons.sqlutils.format.SQLFormatter;
import org.freehep.commons.sqlutils.interfaces.Schema;

/**
 * Unit test for simple App.
 */
public class AppTest
        extends TestCase {
    
    public static class ProcessInstance extends Table {
        @Schema(name="ProcessInstance") 
        public Column<Long> pk;
        
        @Schema 
        public Column<Long> process;
        
        @Schema 
        public Column<Long> stream;
        
        @Schema(name="processingStatus") 
        public Column<String> status;
        
        @Schema(name="IsLatest") 
        public Column<Integer> latest;

        public ProcessInstance() {
            super("ProcessInstance");
        }
    };
    
    public static class Stream extends Table {
        @Schema(name="Stream") public Column pk;
        @Schema(name="ParentStream") public Column parent;
        @Schema(name="Task") public Column task;
        @Schema(name="StreamID") public Column id;
        @Schema(name="StreamStatus") public Column status;
        @Schema(name="IsLatest") public Column latest;

        public Stream() {
            super("Stream");
        }
    };

    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest(String testName) {
        super(testName);
        SQLFormatter.getDefault().setDebug( true );

        HashMap<String, String> testStrings = new HashMap<String, String>();

        Table pi = new Table("processinstance");
        Table pif = new Table("processinstancefile");
        Table s = new Table("stream");
        Table psc = new Table("processcompletioncondition");

        Param<Long> stream = new Param(Long.class);
        stream.setName("stream");
        stream.setValue(new Long(1234));

        Param<Long> pstream = new Param<Long>("pstream", new Long(2345));

        Param<Number> dpar = new Param<Number>();
        dpar.setName("dpar");
        dpar.setValue(1234);

        Select sstream = new Select(s._("stream")).from(s);
        sstream.where(or(s._("stream").eq(stream),
                and(s._("parentstream").eq(5678L),
                    Op.eq(new Sql("PII.GestStreamIsLatestPath(Stream)"),1)
                )));

        sstream.as("harr");
        Select psc_ss = new Select(psc._("Process")).from(psc).where(psc._("DependentProcess").eq(dpar));

        Select all = new Select(pif._("ProcessInstanceFile"), pif._("fileName"))
                .from(sstream)
                .where(pif._("processinstance")
                .in(
                new Select(pi._("processinstance"))
                .from(pi)
                .where(pi._("stream").in(sstream))))
                .where(pi._("process").in(psc_ss));

        sstream.dump();

        psc_ss.dump();

        all.dump();

        pi = new Table("processinstance").as("pi"); //= new Table("processinstance","pi");
        Table p = new Table("process").as("p");
        Table bp = new Table("batchprocess").as("bp");

        Select ss = new Select(pi._("processintance")).from(pi)
                .where(pi._("processinstance").in(new Sql("(1232,24522,2462)")));


        Select q = new Select(pi._("fake").as("notfake"));
        q.selection(
                new Val(48).as("anumber"),
                pi._("fake2"),
                p._("fake3"));
        q.selection(bp._("*").as("jk"));
        q.from(
                (new Select("*").from(new Table("processinstance"))).as("pi")
                );
        
        
        String s001_expected = "SELECT * FROM processinstance";
        System.out.println("Test s001, expected: " + s001_expected);
        Select s001 = (new Select("*").from(new Table("processinstance"))).as("pi");
        assertTrue("Got: " + s001.formatted(), s001.formatted().equals( s001_expected));
        
        String s002_expected = "SELECT * FROM ( SELECT * FROM processinstance ) pi";
        System.out.println("Test s002, expected: " + s002_expected);
        Select s002 = new Select("*").from(s001);
        assertTrue("Got: " + s002.formatted(), s002.formatted().equals( s002_expected));
        
        String q_debug = q.formatted();
        SQLFormatter.getDefault().setDebug( false );
        String q_nodebug = q.formatted();
        SQLFormatter.getDefault().setDebug( true );
        
        
        q.join(p, pi._("fake").eq(p._("fake2")));
        q_debug = q.formatted();
        SQLFormatter.getDefault().setDebug( false );
        q_nodebug = q.formatted();
        SQLFormatter.getDefault().setDebug( true );

        Param<Long> bpParam = new Param<Long>();
        bpParam.setName("firstparam");
        bpParam.setValue(new Long(1235L));

        Expr o = or(bp._("process").eq(p._("process")), p._("ha").lteq(87582));
        Expr a = and(o, bp._("None").not_null());
        q.where(a);
        q.where(bp._("this").eq(bpParam));
        q.where(
                bp._("fake4").eq("fake3"),
                pi._("processinstance").in(ss));

        q.join(bp, bp._("process").eq(p._("process")));
        
        q_debug = q.formatted();
        SQLFormatter.getDefault().setDebug( false );
        q_nodebug = q.formatted();
        SQLFormatter.getDefault().setDebug( true );
        
        testStrings.put(q_debug, q_debug);
        testStrings.put(q_nodebug, q_nodebug);

        q.dump();
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(AppTest.class);
    }

    /**
     * Rigourous Expr :-)
     */
    public void testApp() {
        assertTrue(true);
    }

    /**
     * Expr of main method, of class App.
     */
    public void testMain() {
    }
}
