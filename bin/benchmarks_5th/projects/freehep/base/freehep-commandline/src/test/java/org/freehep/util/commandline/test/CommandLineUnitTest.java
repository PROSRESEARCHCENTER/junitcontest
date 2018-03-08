package org.freehep.util.commandline.test;

import java.util.Vector;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.freehep.util.commandline.CommandLine;
import org.freehep.util.commandline.CommandLineException;
import org.freehep.util.commandline.MissingArgumentException;
import org.freehep.util.commandline.NoSuchOptionException;

/**
 * Some simple tests.
 *
 */
public class CommandLineUnitTest extends TestCase {

    private CommandLine cl;

	public CommandLineUnitTest(String name) {
		super(name);
	}

	protected void setUp() {
        String cmd = "Fake Java Virtual Machine (version 0.9)";
	    cl = new CommandLine("java", cmd, 3);
	    
	    cl.addOption("classpath","cp", "path", "set search path for application classes");
        cl.addBailOutOption("help", "h", "show help on command");
        cl.addOption("notSetFlag", "nsf", "never used");
        cl.addOption("notSetOption", "nso", "dummy", "not used");
        cl.addOption("notSetQualifiedFlag", null, new String[] {"dummy"},"not used");
        cl.addOption("startup", "st", "startup time", "specify startup time");
        cl.addOption("verbose", "v", new String[] {"class","gc","jni"}, "enable verbose output");
        cl.addBailOutOption("version", null, "print product version");
        cl.addOption("x", null, "option x");
        cl.addOption("y", null, "option y");
        cl.addMultiOption("I", "includedir", "add includedir to search path");
        cl.addMultiOption("D", "key=value", "define a property");
        
        cl.addParameter("class1", "first classfile");
        cl.addParameter("class2", "second classfile");
        cl.addParameter("class3", "third classfile");
        cl.addParameter("optionalfile", "optional classfile");
        cl.addParameter("args...", "further arguments");	    
	}

	public void testVersion() throws CommandLineException {
	    assertTrue(!cl.parse(new String[] {"-version"}));
		assertTrue(cl.hasOption("version"));
	}

	public void testDoubleVersion() throws CommandLineException {
		assertTrue(!cl.parse(new String[] {"--version"}));
		assertTrue(cl.hasOption("version"));
	}

	public void testHelp() throws CommandLineException {
		assertTrue(!cl.parse(new String[] {"-help"}));
		assertTrue(cl.hasOption("help"));
	}

	public void testH() throws CommandLineException {
		assertTrue(!cl.parse(new String[] {"-h"}));
		assertTrue(cl.hasOption("h"));
	}

	public void testOptionParameters() throws CommandLineException {
		assertTrue(cl.parse(new String[] {"-verbose","parameter1","parameter2","parameter3"}));
        assertTrue(cl.hasOption("verbose"));
        assertTrue(cl.hasOption("v"));
        String[] args = cl.getArguments();
        assertEquals("parameter1", args[0]);
        assertEquals("parameter2", args[1]);
        assertEquals("parameter3", args[2]);
	}

	public void testDoubleOptionParameters() throws CommandLineException {
		assertTrue(cl.parse(new String[] {"-x-y","parameter1","parameter2","parameter3"}));
        assertTrue(cl.hasOption("x"));
        assertTrue(cl.hasOption("y"));
        String[] args = cl.getArguments();
        assertEquals("parameter1", args[0]);
        assertEquals("parameter2", args[1]);
        assertEquals("parameter3", args[2]);
	}

	public void testValueOptionParametersOptional() throws CommandLineException {
		assertTrue(cl.parse(new String[] {"-cp","classpath","parameter1","parameter2","parameter3","optional1","optional2"}));
        assertEquals("classpath", cl.getOption("classpath"));
        assertEquals("classpath", cl.getOption("cp"));
        String[] args = cl.getArguments();
        assertEquals("parameter1", args[0]);
        assertEquals("parameter2", args[1]);
        assertEquals("parameter3", args[2]);
        assertEquals("optional1", args[3]);
        assertEquals("optional2", args[4]);
	}

	public void testQualifiedOptionParametersOptional() throws CommandLineException {
		assertTrue(cl.parse(new String[] {"-verbose:gc","parameter1","parameter2","parameter3","optional1"}));
        assertEquals("gc", cl.getOption("verbose"));
        assertEquals("gc", cl.getOption("v"));
        String[] args = cl.getArguments();
        assertEquals("parameter1", args[0]);
        assertEquals("parameter2", args[1]);
        assertEquals("parameter3", args[2]);
        assertEquals("optional1", args[3]);
	}

	public void testMultiOptionSingle() throws CommandLineException {
		assertTrue(cl.parse(new String[] {"-Iincludedir1", "par1", "par2", "par3"}));
        Vector multi = cl.getMultiOption("I");
        assertEquals("includedir1", multi.get(0));
        String[] args = cl.getArguments();
        assertEquals("par1", args[0]);
        assertEquals("par2", args[1]);
        assertEquals("par3", args[2]);
	}

	public void testMultiOptionMulti() throws CommandLineException {
		assertTrue(cl.parse(new String[] {"-Iincludedir1", "-Iincludedir2", "par1", "par2", "par3"}));
        Vector multi = cl.getMultiOption("I");
        assertEquals("includedir1", multi.get(0));
        assertEquals("includedir2", multi.get(1));
        String[] args = cl.getArguments();
        assertEquals("par1", args[0]);
        assertEquals("par2", args[1]);
        assertEquals("par3", args[2]);
	}

	public void testKeyValuePairs() throws CommandLineException {
		assertTrue(cl.parse(new String[] {"-Dvar1=value1","-Dvar2=value2","par1","par2","par3"}));
        Vector key = cl.getMultiOption("D");
        assertEquals("var1=value1", key.get(0));
        assertEquals("var2=value2", key.get(1));
        String[] args = cl.getArguments();
        assertEquals("par1", args[0]);
        assertEquals("par2", args[1]);
        assertEquals("par3", args[2]);
	}

	public void testUnknownKeyValueOption() throws CommandLineException {
	    try {
		    assertTrue(cl.parse(new String[] {"-startup=now","classpath","parameter1","parameter2","parameter3"}));
	    } catch (NoSuchOptionException nsoe) {
	        return;
	    }
	    fail("Expected NoSuchOptionException");
	}

	public void testUnknownFlag() throws CommandLineException {
	    try {
		    assertTrue(cl.parse(new String[] {"-unknownflag","parameter1","parameter2","parameter3"}));
	    } catch (NoSuchOptionException nsoe) {
	        return;
	    }
	    fail("Expected NoSuchOptionException");
	}

	public void testUnknownOption() throws CommandLineException {
	    try {
		    assertTrue(cl.parse(new String[] {"-unknownoption","somevalue","parameter1","parameter2","parameter3"}));
	    } catch (NoSuchOptionException nsoe) {
	        return;
	    }
	    fail("Expected NoSuchOptionException");
	}

	public void testMissingArgument() throws CommandLineException {
	    try {
		    assertTrue(cl.parse(new String[] {"-verbose","too","few parameters"}));
	    } catch (MissingArgumentException mae) {
	        return;
	    }
	    fail("Expected MissingArgumentException");
	}

/*
	public void testTooManyArguments() throws CommandLineException {
	    try {
		    assertTrue(cl.parse(new String[] {"-verbose","too","few parameters"}));
	    } catch (TooManyArgumentsException tmae) {
	        return;
	    }
	    fail("Expected TooManyArgumentsException");
	}
*/

	public void testMissingValue() throws CommandLineException {
        try {
    	    assertTrue(cl.parse(new String[] {"-cp"}));
	    } catch (MissingArgumentException mae) {
	        return;
	    }
	    fail("Expected MissingArgumentException");
    }
    
	public static Test suite() {
		return new TestSuite(CommandLineUnitTest.class);
	}
	
	public static void main (String[] args) {
		junit.textui.TestRunner.run (suite());
	}
}