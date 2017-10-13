// these interfaces may move at some point to something like: hep.heprep
package hep.graphics.heprep1;

import java.awt.Color;
import java.util.Hashtable;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

/**
 * Static class to allow for color manipulation
 *
 * @author M.Donszelmann
 *
 * @version $Id: HepRepColor.java 8584 2006-08-10 23:06:37Z duns $
 */

public class HepRepColor {

    private static final Hashtable colors = new Hashtable(25); // colors by lowercase string
    
    static {
        colors.put("black",     Color.black);
        colors.put("blue",      Color.blue);
        colors.put("cyan",      Color.cyan);
        colors.put("darkGray",  Color.darkGray);
        colors.put("gray",      Color.gray);
        colors.put("green",     Color.green);
        colors.put("lightGray", Color.lightGray);
        colors.put("magenta",   Color.magenta);
        colors.put("orange",    Color.orange);
        colors.put("pink",      Color.pink);
        colors.put("red",       Color.red);
        colors.put("white",     Color.white);
        colors.put("yellow",    Color.yellow);
    }

    /**
     * this method returns a Color. Colors are supposedly immutable
     * and are returned from the same table.
     * The formats allowed are:
     * <pre>
     *      by name:          "yellow"
     *      by int r,g,b:     "128, 255, 64"
     *      by float r,g,b:   "0.5, 1.0, 0.25"
     *      by single number: "64637" or "0x0FFF08"
     * </pre>
     *
     * @param name name/number of the color
     * @return requested Color or defaulting to white in case of a invalid name (message is printed).
     */
    public static final Color get(String name) {
        name = name.toLowerCase();
        
        // first look up if its name exists in the table
        Color c = (Color)colors.get(name);
                
        if (c == null) {
            try {
                // check if the format is r,g,b
                if (name.indexOf(',') > 0) {
                    StringTokenizer st = new StringTokenizer(name, ",");
                    boolean isInteger =  false;
                    int[] i = new int[3];
                    float[] f = new float[3];
                    try {
                        i[0] = Integer.parseInt(st.nextToken());
                        isInteger = true;
                    } catch (NumberFormatException nfe1) {
                        f[0] = (new Float(st.nextToken())).floatValue();
                    }

                    if (isInteger) {
                        i[1] = Integer.parseInt(st.nextToken());
                        i[2] = Integer.parseInt(st.nextToken());
                        c = new Color(i[0], i[1], i[2]);
                    } else {
                        f[1] = (new Float(st.nextToken())).floatValue();
                        f[2] = (new Float(st.nextToken())).floatValue();
                        c = new Color(f[0], f[1], f[2]);
                    }
                } else {
                    // the format should be rgb in a single number
                    c = Color.decode(name);
                }
            } catch (NumberFormatException nfe) {
                System.err.println("Not a valid color name/number: '"+name+"', defaulting to 'white'.");
                c = Color.white;
            } catch (NoSuchElementException nsee) {
                System.err.println("Not a valid color name/number: '"+name+"', defaulting to 'white'.");
                c = Color.white;
            }
        }
        
        //FIXME: colors may be stored multiple times.
        colors.put(name, c);
        return c;
    }
}
