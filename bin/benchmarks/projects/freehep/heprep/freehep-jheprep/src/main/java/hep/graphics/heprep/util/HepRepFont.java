// Copyright 2000, FreeHEP.
package hep.graphics.heprep.util;

import java.awt.Font;
import java.util.StringTokenizer;

/**
 * Static class to allow for font manipulation
 *
 * @author M.Donszelmann
 *
 * @version $Id: HepRepFont.java 8584 2006-08-10 23:06:37Z duns $
 */

public class HepRepFont {

    // Not to be instantiated
    private HepRepFont() {
    }

    /**
     * this method returns an integer encoding the font style
     *
     * @param name of the style ("italic+bold" or "italic, bold"
     * @return encoded font style
     */
    public static final int getStyle(String name) {
        name = name.toLowerCase();
        
        StringTokenizer st = new StringTokenizer(name, ", +");
        
        int style = Font.PLAIN;
        
        while(st.hasMoreElements()) {
            String s = ((String)st.nextElement()).toLowerCase().intern();
            if (s == "plain") {
                style += Font.PLAIN;
            } else if (s == "bold") {
                style += Font.BOLD;
            } else if (s == "italic") {
                style += Font.ITALIC;
            } else {
                System.err.println("Unrecognized fontstyle: '"+s+"', ignored.");
            }
        }
        
        return style;
    }
}
