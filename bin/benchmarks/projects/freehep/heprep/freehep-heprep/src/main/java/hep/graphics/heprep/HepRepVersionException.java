package hep.graphics.heprep;

import java.io.*;

/**
 * Exception thrown when there is a version mismatch or the HepRep version 
 * could not be deduced.
 * 
 * @author Mark Donszelmann
 * @version $Id: HepRepVersionException.java 8584 2006-08-10 23:06:37Z duns $
 */

public class HepRepVersionException extends IOException {

    private static final long serialVersionUID = 2137208014190117701L;

    /**
     * Default HepRep Version Exception
     */
    public HepRepVersionException() {
        super();
    }

    /**
     * HepRep Version Exception with message
     * @param msg message
     */
    public HepRepVersionException(String msg) {
        super(msg);
    }
}
