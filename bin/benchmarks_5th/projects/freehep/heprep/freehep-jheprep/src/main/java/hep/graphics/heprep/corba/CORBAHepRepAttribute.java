// Copyright 2000-2006, FreeHEP.
package hep.graphics.heprep.corba;

import hep.graphics.heprep.HepRepAttValue;
import hep.graphics.heprep.HepRepAttribute;
import hep.graphics.heprep.ref.DefaultHepRepAttValue;
import hep.graphics.heprep.util.ValueSet;

import java.awt.Color;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Mark Donszelmann
 *
 * @version $Id: CORBAHepRepAttribute.java 8584 2006-08-10 23:06:37Z duns $
 */

public abstract class CORBAHepRepAttribute implements HepRepAttribute {
    private Map/*<LowerCaseName, HepRepAttValue>*/ atts;

    /**
     * Create CORBA wrapper for attribute
     */
    public CORBAHepRepAttribute() {
        super();
    }

    private void fillAtts() {
        if (atts == null) {
            atts = new Hashtable();
            int n = getAttValues().length;
            for (int i=0; i<n; i++) {
                HepRepAttValue value = new HepRepAttValueAdapter(getAttValues()[i]);
                atts.put(value.getLowerCaseName(), value);
            }
        }
    }

    public Set getAttValuesFromNode() {
        fillAtts();
        return new ValueSet(atts);
    }

    public HepRepAttValue getAttValueFromNode(String lowerCaseName) {
        lowerCaseName = lowerCaseName.intern();
        fillAtts();
        return (HepRepAttValue)atts.get(lowerCaseName);
    }

    public void addAttValue(String key, String value) {
        addAttValue(key, value, HepRepAttValue.SHOW_NONE);
    }

    public void addAttValue(String key, int value) {
        addAttValue(key, value, HepRepAttValue.SHOW_NONE);
    }

    public void addAttValue(String key, long value) {
        addAttValue(key, value, HepRepAttValue.SHOW_NONE);
    }

    public void addAttValue(String key, double value) {
        addAttValue(key, value, HepRepAttValue.SHOW_NONE);
    }

    public void addAttValue(String key, boolean value) {
        addAttValue(key, value, HepRepAttValue.SHOW_NONE);
    }

    public void addAttValue(String key, Color value) {
        addAttValue(key, value, HepRepAttValue.SHOW_NONE);
    }

    public void addAttValue(String key, String value, int showLabel) {
        addAttValue(new DefaultHepRepAttValue(key, value, showLabel));
    }

    public void addAttValue(String key, int value, int showLabel) {
        addAttValue(new DefaultHepRepAttValue(key, value, showLabel));
    }

    public void addAttValue(String key, long value, int showLabel) {
        addAttValue(new DefaultHepRepAttValue(key, value, showLabel));
    }

    public void addAttValue(String key, double value, int showLabel) {
        addAttValue(new DefaultHepRepAttValue(key, value, showLabel));
    }

    public void addAttValue(String key, boolean value, int showLabel) {
        addAttValue(new DefaultHepRepAttValue(key, value, showLabel));
    }

    public void addAttValue(String key, Color value, int showLabel) {
        addAttValue(new DefaultHepRepAttValue(key, value, showLabel));
    }

    public void addAttValue(HepRepAttValue attValue) {
        fillAtts();
        atts.put(attValue.getLowerCaseName(), attValue);
    }

    public HepRepAttValue removeAttValue(String key) {
        if (atts == null) return null;
        return (HepRepAttValue)atts.remove(key.toLowerCase());
    }

    public boolean equals(Object o) {
        if (o == this) return true;
        if (o instanceof HepRepAttribute) {
            HepRepAttribute att = (HepRepAttribute)o;

            HepRepAttValue layer = getAttValueFromNode("layer");
            HepRepAttValue attLayer = att.getAttValueFromNode("layer");
            if (layer != null) {
                if (!layer.equals(attLayer)) return false;
            } else {
                if (attLayer != null) return false;
            }

            return att.getAttValuesFromNode().equals(getAttValuesFromNode());
        }
        return false;
    }
    
    public int hashCode() {
        long code = 0;
        HepRepAttValue layer = getAttValueFromNode("layer");
        if (layer != null) code += layer.hashCode();
        code += getAttValuesFromNode().hashCode();
        return (int)code;
    }

    protected abstract hep.graphics.heprep.corba.idl.HepRepAttValue[] getAttValues();
}

