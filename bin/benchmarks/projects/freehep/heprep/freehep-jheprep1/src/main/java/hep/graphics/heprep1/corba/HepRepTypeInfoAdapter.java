// Copyright FreeHEP 2005.
package hep.graphics.heprep1.corba;

import java.util.Enumeration;

import hep.graphics.heprep1.*;

/**
 *
 * @author M.Donszelmann
 *
 * @version $Id: HepRepTypeInfoAdapter.java 8584 2006-08-10 23:06:37Z duns $
 */

public class HepRepTypeInfoAdapter extends HepRepAttributeAdapter implements HepRepTypeInfo {

    private hep.graphics.heprep1.corba.idl.HepRepTypeInfo hepRepTypeInfo;

    /**
     * Add a CORBA Wrapper
     * @param parent attribute parent
     * @param hepRepTypeInfo corba type info
     */
    public HepRepTypeInfoAdapter(HepRepAttribute parent, hep.graphics.heprep1.corba.idl.HepRepTypeInfo hepRepTypeInfo) {
        super(parent);
        this.hepRepTypeInfo = hepRepTypeInfo;  
    }
    
    hep.graphics.heprep1.corba.idl.HepRepTypeInfo getHepRepTypeInfo() {
        return hepRepTypeInfo;
    }
    
    public String getName() {
        return hepRepTypeInfo.name;
    }
    
    public String getVersion() {
        return hepRepTypeInfo.version;
    }
    
    public Enumeration getSubTypes() {
        return new Enumeration() {
            private int i = 0;
            
            public boolean hasMoreElements() {
                return i < hepRepTypeInfo.subTypes.length;
            }
            
            public Object nextElement() {
                Object element = new HepRepTypeInfoAdapter(HepRepTypeInfoAdapter.this, hepRepTypeInfo.subTypes[i]);
                i++;
                return element;
            }
        };
    }

    public String toString() {
        return "[HepRepTypeInfo (corba):"+getName()+":"+getVersion()+"]";
    }

    protected hep.graphics.heprep1.corba.idl.HepRepAttValue[] getAttValuesFromNode() {
        return hepRepTypeInfo.attValues;
    }
            
    protected hep.graphics.heprep1.corba.idl.HepRepAttDef[] getAttDefsFromNode() {
        return hepRepTypeInfo.attDefs;
    }
}

