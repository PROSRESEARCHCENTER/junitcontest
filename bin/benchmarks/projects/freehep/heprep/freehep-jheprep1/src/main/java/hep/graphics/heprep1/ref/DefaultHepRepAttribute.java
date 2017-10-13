// Copyright 2000-2004, FreeHEP.

package hep.graphics.heprep1.ref;

import hep.graphics.heprep1.HepRepAttDef;
import hep.graphics.heprep1.HepRepAttValue;
import hep.graphics.heprep1.HepRepAttribute;
import hep.graphics.heprep1.HepRepInstance;
import hep.graphics.heprep1.HepRepPoint;
import hep.graphics.heprep1.HepRepPrimitive;
import hep.graphics.heprep1.HepRepType;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Hashtable;

/**
 *
 * @author M.Donszelmann
 *
 * @version $Id: DefaultHepRepAttribute.java 8584 2006-08-10 23:06:37Z duns $
 */

public abstract class DefaultHepRepAttribute implements HepRepAttribute, Serializable {

    private HepRepAttribute parent;
    private Hashtable attVals;      // HepRepAttValue stored by case lowercase name
    private Hashtable attDefs;      // HepRepAttDef stored by case lowercase name
    
    /**
     * Empty Enumeration 
     */
    public static EmptyEnumeration empty = new EmptyEnumeration();
        
    /**
     * Create Attribute
     * @param parent attribute parent
     */
    public DefaultHepRepAttribute(HepRepAttribute parent) {
        this.parent = parent;
    }
    
    public HepRepAttribute getParent() {
        return parent;
    }
    
    public Enumeration getAttValues() {
        return (attVals == null) ? empty : attVals.elements();
    }
    
    public Enumeration getAttDefs() {
        return (attDefs == null) ? empty : attDefs.elements();
    }

    public HepRepAttDef getAttDef(String name) {
        HepRepAttribute table = this;
        HepRepAttDef def = null;
        while (((def = table.getAttDefFromNode(name)) == null) && 
                (table.getParent() != null)) {
            table = table.getParent();
        }

        return def;
    }
    
    public HepRepAttValue getAttValue(String name) {
        HepRepAttribute table = this;
        HepRepAttValue val = null;
        while (((val = table.getAttValueFromNode(name)) == null) && 
                (table.getParent() != null)) {
            table = table.getParent();
        }

        return val;            
    }

    public void addValue(String key, String value) {
        addValue(key, value, HepRepAttValue.SHOW_NONE);
    }

    public void addValue(String key, int value, int showLabel) {
        addValue(key, Integer.toString(value), showLabel);
    }

    public void addValue(String key, double value, int showLabel) {
        addValue(key, Double.toString(value), showLabel);
    }

    public void addValue(String key, boolean value, int showLabel) {
        addValue(key, (value) ? "true" : "false", showLabel);
    }

    public void addValue(String key, String value, int showLabel) {
        if (attVals == null) {
            attVals = new Hashtable();
        }

        attVals.put(key.toLowerCase(), new DefaultHepRepAttValue(key, value, showLabel));
    }

    public void addColor(String key, String colorName, int showLabel) {
        // FIXME: we could check the colornames here
        addValue(key, colorName, showLabel);
    }
    
    public void addColor(String key, double r, double g, double b, double alpha, int showLabel) {
        addColor(key, r+", "+g+", "+b+", "+alpha, showLabel);
    }
    
    public void addDefinition(String name, String desc, String type, String extra) {
        if (attDefs == null) {
            attDefs = new Hashtable();
        }
        
        attDefs.put(name.toLowerCase() , new DefaultHepRepAttDef(name, desc, type, extra));
    }
    
    public HepRepAttDef getAttDefFromNode(String name) {
        return (attDefs == null) ? null : (HepRepAttDef)attDefs.get(name.toLowerCase());            
    }
    
    public HepRepAttValue getAttValueFromNode(String name) {
        HepRepAttValue value = null;
        if (attVals != null) value = (HepRepAttValue)attVals.get(name.toLowerCase());
        if ((value == null) && (getParent() == null)) value = HepRepDefaults.getAttValue(name.toLowerCase());
        return value;
    }
    

    /**
     * Add point
     * @param arg point
     */
    public void add(HepRepPoint arg) {
        throw new IllegalArgumentException("Cannot add: "+arg.getClass().toString()+" to node: "+getClass().toString());
    }

    /**
     * Add primitive 
     * @param arg primitive
     */
    public void add(HepRepPrimitive arg) {
        throw new IllegalArgumentException("Cannot add: "+arg.getClass().toString()+" to node: "+getClass().toString());
    }

    /**
     * Add instance
     * @param arg instance
     */
    public void add(HepRepInstance arg) {
        throw new IllegalArgumentException("Cannot add: "+arg.getClass().toString()+" to node: "+getClass().toString());
    }

    /**
     * Add type
     * @param arg type
     */
    public void add(HepRepType arg) {
        throw new IllegalArgumentException("Cannot add: "+arg.getClass().toString()+" to node: "+getClass().toString());
    }


    static class EmptyEnumeration implements Enumeration {
        public boolean hasMoreElements() {
            return false;
        }
        public Object nextElement() {
            return null;
        }
    }
}

