// Copyright 2000, FreeHEP.
package hep.graphics.heprep.util;

import java.awt.Color;
import org.freehep.swing.ColorConverter;

/**
 * Static class to allow for color manipulation
 *
 * @author M.Donszelmann
 *
 * @version $Id: HepRepColor.java 8584 2006-08-10 23:06:37Z duns $
 */

public class HepRepColor {
    
    /**
     * This color is used to tag items which should not be drawn.  Users
     * should test to see if this color is set, and then simply not draw the
     * relevant figures.  Simply drawing with this color will probably not
     * have the desired effect.  This color can be selected only by name. */
    public static final Color invisible = new Color(255,255,255,0);
    private static final ColorConverter cc = new ColorConverter() {
        /**
         * Colors too close to black (e.g. 1, 0, 0), are to be converted using floats and thus counting as (255, 0, 0).
         */
        protected Color createColor(int red, int green, int blue, int alpha) {
            if ((red <= 1) && (green <= 1) && (blue <= 1) && ((alpha <= 1) || (alpha == 255))) {
                // rgba either 0 or 1
                Color c = new Color((float)red, (float)green, (float)blue, (alpha == 255) ? 1.0f : (float)alpha);
//                System.out.println(c);
                return c;
            }
            return super.createColor(red, green, blue, alpha);
        }
    };
    
    static {
        cc.addEntry(invisible, "Invisible");
    }
   
    // not to be instantiated
    private HepRepColor() {
    }
   
    /**
     * this method returns a Color. Colors are supposedly immutable
     * and are returned from the same table.
     * The RGBA formats, where Alpha is optional and defaults to 1, are:
     * <pre>
     *      by name:          "yellow"                      , where alpha is always 1.0
     *      by int r,g,b,a:   "128, 255, 64, 255"           , where alpha (a) is optional
     *      by float r,g,b,a: "0.5, 1.0, 0.25, 1.0"         , where alpha (a) is optional
     *      by single number: "64637" or "0x0FFF08"         , where alpha is always 1.0
     * </pre>
     *
     *
     * @param name name/number of the color
     * @return requested Color or defaulting to white in case of a invalid name (message is printed).
     * @see #get(Color name)
     */
    public static final Color get(String name) {
        try {
            return cc.stringToColor(name);  
        } catch (ColorConverter.ColorConversionException x) {
            System.err.println(x.getMessage()+" defaulting to 'white'.");
            return Color.white;
        } 
    }
   
    
    /**
     * Converts color into string
     * 
     * @param color color
     * @return Name for color
     * @see #get(String name)
     */
    public static String get(Color color) {
        return cc.colorToString(color);
    }
}
