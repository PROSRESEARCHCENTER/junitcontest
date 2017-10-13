// Copyright 2004, FreeHEP.
package hep.graphics.heprep1.adapter;

import hep.graphics.heprep.ref.DefaultHepRepAttValue;

import java.awt.Color;

/**
 *
 * @author Mark Donszelmann
 * @version $Id: HepRepAttValueAdapter.java 8584 2006-08-10 23:06:37Z duns $
 */
public class HepRepAttValueAdapter extends DefaultHepRepAttValue {

    /**
     * Wrapper for HepRep1 AttValue
     * @param attValue heprep1 AttValue
     * @param name heprep2 name
     * @param value heprep2 value
     * @param showLabel heprep2 showlabel
     */
    public HepRepAttValueAdapter(hep.graphics.heprep1.HepRepAttValue attValue, String name, String value, int showLabel) {
        super(name, value, showLabel);
    }   

    /**
     * Wrapper for HepRep1 AttValue
     * @param attValue heprep1 AttValue
     * @param name heprep2 name
     * @param value heprep2 value
     * @param showLabel heprep2 showlabel
     */
    public HepRepAttValueAdapter(hep.graphics.heprep1.HepRepAttValue attValue, String name, Color value, int showLabel) {
        super(name, value, showLabel);
    }   

    /**
     * Wrapper for HepRep1 AttValue
     * @param attValue heprep1 AttValue
     * @param name heprep2 name
     * @param value heprep2 value
     * @param showLabel heprep2 showlabel
     */
    public HepRepAttValueAdapter(hep.graphics.heprep1.HepRepAttValue attValue, String name, long value, int showLabel) {
        super(name, value, showLabel);
    }   

    /**
     * Wrapper for HepRep1 AttValue
     * @param attValue heprep1 AttValue
     * @param name heprep2 name
     * @param value heprep2 value
     * @param showLabel heprep2 showlabel
     */
    public HepRepAttValueAdapter(hep.graphics.heprep1.HepRepAttValue attValue, String name, int value, int showLabel) {
        super(name, value, showLabel);
    }   

    /**
     * Wrapper for HepRep1 AttValue
     * @param attValue heprep1 AttValue
     * @param name heprep2 name
     * @param value heprep2 value
     * @param showLabel heprep2 showlabel
     */
    public HepRepAttValueAdapter(hep.graphics.heprep1.HepRepAttValue attValue, String name, double value, int showLabel) {
        super(name, value, showLabel);
    }   

    /**
     * Wrapper for HepRep1 AttValue
     * @param attValue heprep1 AttValue
     * @param name heprep2 name
     * @param value heprep2 value
     * @param showLabel heprep2 showlabel
     */
    public HepRepAttValueAdapter(hep.graphics.heprep1.HepRepAttValue attValue, String name, boolean value, int showLabel) {
        super(name, value, showLabel);
    }   

    /**
     * Wrapper for HepRep1 AttValue
     * @param attValue heprep1 AttValue
     * @param name heprep2 name
     * @param value heprep2 value
     * @param type heprep2 attribute type
     * @param showLabel heprep2 showlabel
     */
    public HepRepAttValueAdapter(hep.graphics.heprep1.HepRepAttValue attValue, String name, String value, String type, int showLabel) {
        super(name, value, showLabel);
    }   
}
