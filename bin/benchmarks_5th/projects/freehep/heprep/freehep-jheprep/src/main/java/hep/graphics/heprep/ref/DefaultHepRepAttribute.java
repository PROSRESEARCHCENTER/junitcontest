// Copyright 2000-2005, FreeHEP.
package hep.graphics.heprep.ref;

import java.awt.Color;
import java.io.*;
import java.util.*;

import hep.graphics.heprep.*;
import hep.graphics.heprep.util.*;


/**
 *
 * @author M.Donszelmann
 *
 * @version $Id: DefaultHepRepAttribute.java 8584 2006-08-10 23:06:37Z duns $
 */

public abstract class DefaultHepRepAttribute implements HepRepAttribute, Serializable {

    private Map/*<LowerCaseName, HepRepAttValue>*/ atts;

    protected DefaultHepRepAttribute() {
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
//        LinkedList.writeList(stream, attValueList);
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
//        System.out.println("DHRAttribute: Deserializing "+this);
        stream.defaultReadObject();
//        attValueList = LinkedList.readList(stream);
    }

    public Set/*<HepRepAttValue>*/ getAttValuesFromNode() {
        return new ValueSet(atts);
    }

    public void addAttValue(HepRepAttValue hepRepAttValue) {
        if (atts == null) atts = new Hashtable();
        atts.put(hepRepAttValue.getLowerCaseName(), hepRepAttValue);
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

    /**
     * Add an Named Color Attribute
     * @param key attribute name
     * @param colorName name of the color
     * @param showLabel code what to show as a label
     */
    public void addAttColor(String key, String colorName, int showLabel) {
        // FIXME: JHEPREP-19
        addAttValue(new DefaultHepRepAttValue(key, HepRepColor.get(colorName), showLabel));
    }

    /**
     * Add an RGBA Color Attribute
     * @param key attribute name
     * @param r red value 0..1
     * @param g green value 0..1
     * @param b blue value 0..1
     * @param alpha alpha value 0..1
     * @param showLabel code what to show as a label
     */
    public void addAttColor(String key, double r, double g, double b, double alpha, int showLabel) {
        addAttColor(key, r+", "+g+", "+b+", "+alpha, showLabel);
    }

    /**
     * Add a Type Attribute
     * @param name attribute name
     * @param value attribute value
     * @param type attribute type
     * @param showLabel code what to show as a label
     */
    public void addAttValue(String name, String value, String type, int showLabel) {
        addAttValue(new DefaultHepRepAttValue(name, value, type, showLabel));
    }

    public HepRepAttValue getAttValueFromNode(String lowerCaseName) {
        if (atts == null) return null;
        return (HepRepAttValue)atts.get(lowerCaseName);
    }

    public HepRepAttValue removeAttValue(String key) {
        if (atts == null) return null;
        return (HepRepAttValue)atts.remove(key.toLowerCase());
    }
    
/* Disabled for FREEHEP-386
    public boolean equals(Object o) {
        if (o == this) return true;
        if (o instanceof HepRepAttribute) {
            HepRepAttribute att = (HepRepAttribute)o;
            if (att.getAttValuesFromNode().equals(getAttValuesFromNode())) return true;
            return false;
        }
        return false;
    }
    
    public int hashCode() {
        return (int)getAttValuesFromNode().hashCode();
    }
*/

    public abstract HepRepAttValue getAttValue(String name);
}

