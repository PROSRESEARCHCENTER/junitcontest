// Copyright FreeHEP 2005.
package hep.graphics.heprep1.corba;

import java.util.Enumeration;

import hep.graphics.heprep1.*;
import hep.graphics.heprep1.ref.*;
import hep.graphics.heprep1.util.*;

/**
 *
 * @author M.Donszelmann
 *
 * @version $Id: HepRepAttributeAdapter.java 8584 2006-08-10 23:06:37Z duns $
 */

public abstract class HepRepAttributeAdapter extends DefaultHepRepAttribute implements HepRepAttribute {

    /**
     * Add a CORBA Wrapper
     * @param parent attribute parent
     */
    public HepRepAttributeAdapter(HepRepAttribute parent) {
        super(parent);
    }
            
    public Enumeration getAttValues() {
        return HepRepUtil.enumeration(super.getAttValues(), new Enumeration() {
            private int i = 0;
            private hep.graphics.heprep1.corba.idl.HepRepAttValue[] attVals = getAttValuesFromNode();
            
            public boolean hasMoreElements() {
                return i < attVals.length;
            }
            
            public Object nextElement() {
                Object element = new HepRepAttValueAdapter(attVals[i]);
                i++;
                return element;
            }
        });
    }
    
    public Enumeration getAttDefs() {
        return HepRepUtil.enumeration(super.getAttDefs(), new Enumeration() {
            private int i = 0;
            private hep.graphics.heprep1.corba.idl.HepRepAttDef[] attDefs = getAttDefsFromNode();        
            
            public boolean hasMoreElements() {
                return i < attDefs.length;
            }
            
            public Object nextElement() {
                Object element = new HepRepAttDefAdapter(attDefs[i]);
                i++;
                return element;
            }
        });
    }

    public HepRepAttValue getAttValueFromNode(String name) {
        HepRepAttValue attValue = super.getAttValueFromNode(name);
        if (attValue != null) return attValue;
        hep.graphics.heprep1.corba.idl.HepRepAttValue[] attVals = getAttValuesFromNode();
        for (int i=0; i<attVals.length; i++) {
            if (name.equalsIgnoreCase(attVals[i].name)) return new HepRepAttValueAdapter(attVals[i]);
        }
        return (getParent() == null) ? HepRepDefaults.getAttValue(name) : null;
    }
        
    public HepRepAttDef getAttDefFromNode(String name) {
        HepRepAttDef attDef = super.getAttDefFromNode(name);
        if (attDef != null) return attDef;
        hep.graphics.heprep1.corba.idl.HepRepAttDef[] attDefs = getAttDefsFromNode();        
        for (int i=0; i<attDefs.length; i++) {
            if (name.equalsIgnoreCase(attDefs[i].name)) return new HepRepAttDefAdapter(attDefs[i]);
        }
        return null;
    }
    
    protected abstract hep.graphics.heprep1.corba.idl.HepRepAttValue[] getAttValuesFromNode();
    protected abstract hep.graphics.heprep1.corba.idl.HepRepAttDef[] getAttDefsFromNode();
}

