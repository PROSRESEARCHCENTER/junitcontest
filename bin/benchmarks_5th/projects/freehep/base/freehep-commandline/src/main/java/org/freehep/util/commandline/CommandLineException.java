// Copyright 2000, CERN, Geneva, Switzerland.
package org.freehep.util.commandline;

/**
 * Superclass of all Command Line exceptions.
 *
 * @author Mark Donszelmann
 * @version $Id: CommandLineException.java 8584 2006-08-10 23:06:37Z duns $
 */ 
public abstract class CommandLineException extends Exception {
    public CommandLineException(String msg) {
        super(msg);
    }
}
