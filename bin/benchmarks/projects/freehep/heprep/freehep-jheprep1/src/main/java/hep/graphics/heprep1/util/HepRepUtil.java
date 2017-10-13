// Copyright 2004, FreeHEP.
package hep.graphics.heprep1.util;

import java.util.*;

/**
 * @author Mark Donszelmann
 * @version $Id: HepRepUtil.java 8584 2006-08-10 23:06:37Z duns $
 */
public class HepRepUtil {
    
    private HepRepUtil() {
    }
    
    /**
     * Enumerates two enumerations in order
     * @param first first enumeration
     * @param second second enumeration
     * @return combined enumeration
     */
    public static Enumeration enumeration(Enumeration first, Enumeration second) {
        final Enumeration f = first;
        final Enumeration s = second;
        
        return new Enumeration() {

            public boolean hasMoreElements() {
                return f.hasMoreElements() || s.hasMoreElements();
            }

            public Object nextElement() {
                if (f.hasMoreElements()) {
                    return f.nextElement();
                }

                return s.nextElement();
            }

        };
    }
}