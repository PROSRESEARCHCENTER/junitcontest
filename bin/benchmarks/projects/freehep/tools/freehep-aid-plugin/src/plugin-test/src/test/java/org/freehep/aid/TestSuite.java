package org.freehep.aid;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Method;

import junit.framework.*;

import org.freehep.util.Assert;
import org.freehep.util.io.UniquePrintStream;

/**
 * @author Mark Donszelmann
 * @version $Id: TestSuite.java 8584 2006-08-10 23:06:37Z duns $
 */
public class TestSuite {

    // for running under Junit from ant
    public TestSuite(String name) {
        suite();
    }

    public static class TestCase extends junit.framework.TestCase {

        private String src;
        private String dst;
        private String prefix;
        private String name;
        private String ext;

        public TestCase(String src, String dst, String prefix, String name, String ext) {
            super("Test for "+prefix+name+"."+ext);
            this.src = src;
            this.dst = dst;
            this.prefix = prefix;
            this.name = name;
            this.ext = ext;
        }

        protected void runTest() throws Throwable {
            File actual   = new File(src+prefix+name+"."+ext);
            File expected = new File(dst+prefix+name+"."+ext);
            Assert.assertEquals(actual, expected, false);
        }
    }

    private static void addTest(String src, String dst, junit.framework.TestSuite suite, String name) {
        suite.addTest(new TestCase(src, dst              , ""      , name, "java"));
// Not generated
//        suite.addTest(new TestCase(dir                   , "Abstract", name, "java"));
        suite.addTest(new TestCase(src+"include/AIDTEST/", src+"include/AIDTEST/", ""      , name, "h"));
// JNI generation code not correct anyway yet.
//        suite.addTest(new TestCase(dir+"jni/"            , "J"     , name, "h"));
//        suite.addTest(new TestCase(dir+"jni/"            , "J"     , name, "cpp"));
    }

    public static junit.framework.TestSuite suite() {
        junit.framework.TestSuite suite = new junit.framework.TestSuite();
        String src = "target/aid-generated/org/freehep/aid/";
        String dst = "ref/org/freehep/aid/";
        addTest(src, dst, suite, "ITestArrays");
        addTest(src, dst, suite, "ITestCollections");
        addTest(src, dst, suite, "ITestConstants");
        addTest(src, dst, suite, "ITestEmpty");
        addTest(src, dst, suite, "ITestEnumerations");
        addTest(src, dst, suite, "ITestGenericsDefine");
        addTest(src, dst, suite, "ITestGenericsDefine2");
        addTest(src, dst, suite, "ITestGenericsUse");
        addTest(src, dst, suite, "ITestInterface");
        addTest(src, dst, suite, "ITestObjects");
        addTest(src, dst, suite, "ITestPackageClosure");
        addTest(src, dst, suite, "ITestPrimitives");
        addTest(src, dst, suite, "ITestVoid");

        suite.addTest(new TestCase(src+"dev/", dst+"dev/", ""        , "ITestNameSpace", "java"));
// Not generated
//        suite.addTest(new TestCase("target/aid-generated/org/freehep/aid/dev/", "Abstract", "ITestNameSpace", "java"));
        suite.addTest(new TestCase(src+"include/AIDTEST_Dev/", dst+"include/AIDTEST_Dev/",
                                   "", "ITestNameSpace", "h"));
        suite.addTest(new TestCase(src+"dev/", dst+"dev/", ""        , "ITestNameSpace2", "java"));
// Not generated
//        suite.addTest(new TestCase("target/aid-generated/org/freehep/aid/dev/", "Abstract", "ITestNameSpace2", "java"));
        suite.addTest(new TestCase(src+"include/AIDTEST_Dev/", dst+"include/AIDTEST_Dev/",
                                   "", "ITestNameSpace2", "h"));
// JNI generation code not correct anyway yet.
//        suite.addTest(new TestCase("target/aid-generated/org/freehep/aid/jni/",
//                                   "J", "ITestNameSpace", "h"));
//        suite.addTest(new TestCase("target/aid-generated/org/freehep/aid/jni/",
//                                   "J", "ITestNameSpace", "cpp"));
        return suite;
    }


    public static void main(String[] args) {
        UniquePrintStream stderr = new UniquePrintStream(System.err);
        System.setErr(stderr);
        junit.textui.TestRunner.run(suite());
        stderr.finish();
    }
}
