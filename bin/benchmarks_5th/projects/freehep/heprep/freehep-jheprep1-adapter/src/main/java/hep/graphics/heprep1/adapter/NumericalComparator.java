// Copyright 2004, FreeHEP.
package hep.graphics.heprep1.adapter;

import java.util.*;

/**
 * FIXME: move somewhere else in freehep-base.
 *
 * @author Mark Donszelmann
 * @version $Id: NumericalComparator.java 8584 2006-08-10 23:06:37Z duns $
 */
public class NumericalComparator implements Comparator {
    
    /**
     * Sorts in numerical order, followed by Strings in alphabetical order.
     */
    public int compare(Object o1, Object o2) {
        String s1 = (String)o1;
        String s2 = (String)o2;
        double d1 = 0;
        double d2 = 0;
        boolean o1isDouble = false;
        boolean o2isDouble = false;
        
        try {
            d1 = Double.parseDouble(s1);
            o1isDouble = true;
        } catch (NumberFormatException nfe) {
        }
        
        try {
            d2 = Double.parseDouble(s2);
            o2isDouble = true;
        } catch (NumberFormatException nfe) {
        }
        
        if (o1isDouble) {
            if (o2isDouble) {
                return d1 < d2 ? -1 : d1 > d2 ? 1 : 0;
            } else {
                return 1;
            }
        } else {
            if (o2isDouble) {
                return -1;
            } else {
                return s1.compareTo(s2);
            }
        }
    }
    
}