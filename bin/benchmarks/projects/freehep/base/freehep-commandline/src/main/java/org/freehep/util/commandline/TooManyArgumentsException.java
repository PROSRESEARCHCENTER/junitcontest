// Copyright 2000, CERN, Geneva, Switzerland.
package org.freehep.util.commandline;

/**
 * Too many arguments were provided. 
 *
 * @author Mark Donszelmann
 * @version $Id: TooManyArgumentsException.java 8584 2006-08-10 23:06:37Z duns $
 */ 
public class TooManyArgumentsException extends CommandLineException {
    public TooManyArgumentsException(String msg) {
        super(msg);
    }
    
    public TooManyArgumentsException() {
        super("Too Many Arguments Exception");
    }
}
