// Copyright 2004, FreeHEP.
package hep.graphics.heprep1.adapter;

import java.util.*;

/**
 *
 * @author Mark Donszelmann
 * @version $Id: AttributeNameTranslator.java 8584 2006-08-10 23:06:37Z duns $
 */
public class AttributeNameTranslator {

    private static final Map/*<name2, names1[]>*/ heprep1;
    private static final Map/*<name1, name2>*/ heprep2;
    
    static {
        heprep1 = new HashMap();
        heprep2 = new HashMap();
        // NOTE: lowercase only, include name2 in names1.
        put("color",                new String[] {"color", "linecolor", "markcolor"} );
        put("framecolor",           new String[] {"framecolor", "lineframecolor", "markframecolor"} );
        put("framewidth",           new String[] {"framewidth", "lineframewidth", "markframewidth"} );
        put("hasframe",             new String[] {"hasframe", "linehasframe", "markhasframe"} );
//        put("phi",                  new String[] {"phi", "phi1", "phi2"} );
//        put("theta",                new String[] {"theta", "theta1", "theta2"} );
//        put("radius",               new String[] {"radius", "radius1", "radius2", "radiusx", "radiusy", "radiusz"} );
        
        // FIXME: NOTE: there are some such as "sum, sum1" which need more than a translation..
    }
    
    private AttributeNameTranslator() {
    }
    
    // FIXME, strictly not correct because a lookup of "phi" may result in "phi1" being found, while "phi2"
    // is meant... We need something better for this, unless it is never used...
    /**
     * Puts names into lookup tables for heprep1 to heprep2 lookups and vice-versa.
     * @param name2 heprep2 name
     * @param names1 possible heprep1 translations
     */
    public static void put(String name2, String[] names1) {
        for (int i=0; i<names1.length; i++) {
            heprep2.put(names1[i], name2);
        }
        heprep1.put(name2, names1);
    }
    
    /**
     * Translates name from heprep2 to heprep1
     * @param name2 heprep2 name
     * @return array of heprep1 names
     */
    public static String[] getName1(String name2) {
        return (String[])heprep1.get(name2.toLowerCase());
    }
    
    /**
     * Translates name from heprep1 to heprep2
     * @param name1 heprep1 name
     * @return heprep2 name
     */
    public static String getName2(String name1) {
        String name2 = (String)heprep2.get(name1.toLowerCase());
        return (name2 != null) ? name2 : name1;
    }
}
