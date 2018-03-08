// Copyright 2004-2005, FreeHEP.
package hep.graphics.heprep;

/**
 * Exception used to indicate failure of conversion to HepRep.
 * 
 * @author Mark Donszelmann
 * @version $Id: HepRepConversionException.java 8584 2006-08-10 23:06:37Z duns $
 */

public class HepRepConversionException extends RuntimeException {

    private static final long serialVersionUID = 4435606384275686524L;
    private static final String defaultMessage = "Cannot convert to HepRep";

    /**
     * Default HepRep Conversion Exception
     */
    public HepRepConversionException() {
        this(defaultMessage);
    }

    /**
     * HepRep Conversion Exception with message
     * @param msg specific message
     */
    public HepRepConversionException(String msg) {
        super(msg);
    }

    /**
     * HepRep Conversion Exception with cause
     * @param cause underlying exception
     */
    public HepRepConversionException(Throwable cause) {
        super(defaultMessage, cause);
    }

    /**
     * HepRep Conversion Exception with message and cause
     * @param msg specific message
     * @param cause underlying exception
     */
    public HepRepConversionException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
