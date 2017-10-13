// Copyright 2000-2004, FreeHEP.

package hep.graphics.heprep1.ref;

import java.awt.Color;
import java.io.Serializable;

import hep.graphics.heprep1.*;

/**
 *
 * @author M.Donszelmann
 *
 * @version $Id: DefaultHepRepAttValue.java 8584 2006-08-10 23:06:37Z duns $
 */

public class DefaultHepRepAttValue implements HepRepAttValue, Serializable {

    private String name, value;
    private int showLabel;
    private Color color;

    /**
     * Create Attribute Value
     * @param name name of the value
     * @param value value
     * @param showLabel shat to show in label
     */
    public DefaultHepRepAttValue(String name, String value, int showLabel) {
        this.name = name;
        this.value = value;
        this.showLabel = showLabel;
        this.color = null;
    }
    
    public String getName() {
        return name;
    }

    public int showLabel() {
        return showLabel;
    }
    
    public Object getValue() {
        return value;
    }
    
    public String getString() {
        return value;
    }
    
    public long getLong() {
        return Long.parseLong(value);
    }
    
    public int getInteger() {
        return (int)getLong();
    }
    
    public double getDouble() {
        return Double.valueOf(value).doubleValue();
    }
    
    public boolean getBoolean() {
        return Boolean.valueOf(value).booleanValue();
    }
    
    public Color getColor() {
        if (color == null) {
            color = HepRepColor.get(value);
        }
        return color;
    }

    public int getFontStyle() {
        return HepRepFont.getStyle(value);
    }
    
    public String toString() {
        return "AttValue["+getName()+": "+getValue()+"]";
    }
}

