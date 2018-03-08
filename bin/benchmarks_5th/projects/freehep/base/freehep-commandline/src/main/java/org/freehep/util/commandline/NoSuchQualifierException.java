// Copyright 2000, CERN, Geneva, Switzerland.
package org.freehep.util.commandline;

/**
 * Qualifier does not exist.
 *
 * @author Mark Donszelmann
 * @version $Id: NoSuchQualifierException.java 8584 2006-08-10 23:06:37Z duns $
 */ 
public class NoSuchQualifierException extends CommandLineException {
    public NoSuchQualifierException(String msg) {
        super(msg);
    }
    
    public NoSuchQualifierException() {
        super("No Such Qualifier Exception");
    }
}
