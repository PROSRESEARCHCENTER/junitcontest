// these interfaces may move at some point to something like: hep.heprep
package hep.graphics.heprep1.ref;

import hep.graphics.heprep1.HepRepColor;
import hep.graphics.heprep1.HepRepCut;
import hep.graphics.heprep1.HepRepFont;

import java.awt.Color;
import java.io.Serializable;

/**
 *
 * @author M.Donszelmann
 *
 * @version $Id: DefaultHepRepCut.java 8584 2006-08-10 23:06:37Z duns $
 */

public class DefaultHepRepCut implements HepRepCut, Serializable {

    private String name;
    private String comparison;
    private Object value;

    // we cannot store or transfer objects in general since CORBA does not allow this...
    private DefaultHepRepCut(String name, String comparison, Object value) {
        this.name = name;
        this.comparison = comparison;
        this.value = value;
    }
    
    /**
     * Create Cut
     * @param name name of the cut
     * @param comparison comparison
     * @param value value to compare
     */
    public DefaultHepRepCut(String name, String comparison, String value) {
        this(name, comparison, (Object)value);
    }

    /**
     * Create Cut
     * @param name name of the cut
     * @param comparison comparison
     * @param value value to compare
     */
    public DefaultHepRepCut(String name, String comparison, long value) {
        this(name, comparison, (Object)(new Long(value)));
    }

    /**
     * Create Cut
     * @param name name of the cut
     * @param comparison comparison
     * @param value value to compare
     */
    public DefaultHepRepCut(String name, String comparison, double value) {
        this(name, comparison, (Object)(new Double(value)));
    }

    public String getName() {
        return name;
    }
    
    // FIXME: what should the return value be here
    public int showLabel() {
        return 0;
    }
    
    public String getComparison() {
        return comparison;
    }
    
    public Object getValue() {
        return value;
    }
    
    public String getString() {
        return (String)value;
    }
    
    public long getLong() {
        return ((Long)value).longValue();
    }
    
    public int getInteger() {
        return (int)getLong();
    }
    
    public double getDouble() {
        return ((Double)value).doubleValue();
    }

    public boolean getBoolean() {
        return ((Boolean)value).booleanValue();
    }
    
    public Color getColor() {
        return HepRepColor.get(getString());
    }

    public int getFontStyle() {
        return HepRepFont.getStyle(getString());
    }
}
