// Copyright FreeHEP 2000-2005.
package hep.graphics.heprep1;

import java.awt.Color;


/**
 *
 * @author M.Donszelmann
 *
 * @version $Id: HepRepAttValue.java 8584 2006-08-10 23:06:37Z duns $
 */

public interface HepRepAttValue {

    /** 
     * ShowLabel: NONE
     */
    public static int SHOW_NONE =   0x0000;

    /** 
     * ShowLabel: NAME
     */
    public static int SHOW_NAME =   0x0001;

    /** 
     * ShowLabel: DESCRIPTION
     */
    public static int SHOW_DESC =   0x0002;

    /** 
     * ShowLabel: VALUE
     */
    public static int SHOW_VALUE =  0x0004;

    /** 
     * ShowLabel: EXTRA
     */
    public static int SHOW_EXTRA =  0x0008;

    /**
     * @return Capitalized Name
     */
    public String getName();
    
    /**
     * @return flag bits if should be shown as label
     */
    public int showLabel();
    
    /**
     * @return value as Object
     */
    public Object getValue();

    /**
     * @return value as Capitalized string
     */
    public String getString();

    /**
     * @return value as long
     */
    public long getLong();

    /**
     * @return value as integer
     */
    public int getInteger();

    /**
     * @return value as double
     */
    public double getDouble();

    /**
     * @return value as boolean
     */
    public boolean getBoolean();

    /**
     * @return value as Color
     */
    public Color getColor();

    /**
     * @return value as FontStyle
     */
    public int getFontStyle();
}
