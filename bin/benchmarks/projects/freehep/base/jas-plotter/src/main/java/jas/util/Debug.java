package jas.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;

public class Debug {
	public static void init() {
		if (debugInitialized) return;
		try {
			theStdOutFile = new File(theStdOutFileName);
			theStdErrFile = new File(theStdErrFileName);
				PrintStream outFile = new PrintStream(
										new BufferedOutputStream(
											new FileOutputStream(theStdOutFile)));
				PrintStream errFile = new PrintStream(
										new BufferedOutputStream(
											new FileOutputStream(theStdErrFile)));
			theFileOutWriter = new PrintWriter(outFile, true);
			theFileErrWriter = new PrintWriter(errFile, true);
		} catch (IOException eh) {
			System.out.println("IOException initializing Debug system!");
			eh.printStackTrace();
		}
		debugInitialized = true;
	}
	public static void setFileOutput() {
		redirectToFile = true;
	}
	public static void setNormalOutput() {
		redirectToFile = false;
	}
	public static void pushFileOutput() {
		oldRedirectValue = redirectToFile;
		redirectToFile = true;
	}
	public static void pushNormalOutput() {
		oldRedirectValue = redirectToFile;
		redirectToFile = false;
	}
	public static void popOutput() {
		redirectToFile = oldRedirectValue;
	}
	public static void say(String s) {
		init();
		if (redirectToFile) {
			theFileOutWriter.println(s);
		} else {
			theConsoleOutWriter.println(s);
		}
	}
	public static void complain(String s) {
		init();
		if (redirectToFile) {
			theFileErrWriter.println(s);
		} else {
			theConsoleErrWriter.println(s);
		}
	}
	public static void debugSay(String s) {
		if (debugMode) {
			say(s);
		}
	}
	public static void debugComplain(String s) {
		if (debugMode) {
			complain(s);
		}
	}
	private static			File		theStdOutFile;
	private static			File		theStdErrFile;
	private static final	PrintWriter	theConsoleOutWriter = new PrintWriter(System.out, true);
	private static final	PrintWriter	theConsoleErrWriter = new PrintWriter(System.err, true);
	private static			PrintWriter	theFileOutWriter;
	private static			PrintWriter	theFileErrWriter;
	private static final	String		theStdOutFileName	= "stdout.txt";
	private static final	String		theStdErrFileName	= "stderr.txt";
	private static final	boolean		debugMode			= true;
	private	static			boolean		debugInitialized	= false;
	private	static			boolean		redirectToFile		= false;
	private static			boolean		oldRedirectValue	= false;
}
