// Copyright 2000, CERN, Geneva, Switzerland.
package org.freehep.util.commandline;

/**
 * Option does not exist, or an ampty option is provided.
 *
 * @author Mark Donszelmann
 * @version $Id: NoSuchOptionException.java 8584 2006-08-10 23:06:37Z duns $
 */ 
public class NoSuchOptionException extends CommandLineException {
    public NoSuchOptionException(String msg) {
        super(msg);
    }
    
    public NoSuchOptionException() {
        super("No Such Option Exception");
    }
}
