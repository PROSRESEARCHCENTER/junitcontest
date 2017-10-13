// Copyright 2000, CERN, Geneva, Switzerland.
package org.freehep.util.commandline;

/**
 * Too few arguments were provided.
 *
 * @author Mark Donszelmann
 * @version $Id: MissingArgumentException.java 8584 2006-08-10 23:06:37Z duns $
 */ 
public class MissingArgumentException extends CommandLineException {
    public MissingArgumentException(String msg) {
        super(msg);
    }
    
    public MissingArgumentException() {
        super("Missing Argument Exception");
    }
}
