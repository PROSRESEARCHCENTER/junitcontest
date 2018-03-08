// Copyright 2000-2005, FreeHEP.
package hep.graphics.heprep;

/**
 * Exception indicates that an HepRep Attribute Value was set or read with 
 * the incorrect type.
 * 
 * @author Mark Donszelmann
 * @version $Id: HepRepTypeException.java 8584 2006-08-10 23:06:37Z duns $
 */

public class HepRepTypeException extends RuntimeException {

    private static final long serialVersionUID = 8247202788421287104L;

    /**
     * Default HepRep Type Exception
     */
    public HepRepTypeException() {
        super();
    }

    /**
     * HepRep Type Exception with message
     * @param msg message
     */
    public HepRepTypeException(String msg) {
        super(msg);
    }
}
