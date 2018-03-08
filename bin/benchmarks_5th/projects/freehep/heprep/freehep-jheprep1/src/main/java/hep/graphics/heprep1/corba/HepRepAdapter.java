// Copyright FreeHEP 2005.
package hep.graphics.heprep1.corba;

import java.util.Enumeration;
import java.util.Vector;

import org.omg.CORBA.Any;
import org.omg.CORBA.ORB;

import hep.graphics.heprep1.*;

/**
 *
 * @author M.Donszelmann
 *
 * @version $Id: HepRepAdapter.java 8584 2006-08-10 23:06:37Z duns $
 */

public class HepRepAdapter extends HepRepAttributeAdapter implements HepRep {

    private hep.graphics.heprep1.corba.idl.HepRep hepRep;
    private Vector types = new Vector();

    /**
     * Add a CORBA Wrapper
     * @param hepRep corba heprep
     */
    public HepRepAdapter(hep.graphics.heprep1.corba.idl.HepRep hepRep) {
        super(null);
        this.hepRep = hepRep;
    }

    public Enumeration getTypeInfo() {
        return new Enumeration() {
            private hep.graphics.heprep1.corba.idl.HepRepTypeInfo[] typeInfoList = hepRep.getTypeInfo();
            private int i= 0;
            public boolean hasMoreElements() {
                return i < typeInfoList.length;
            }
            public Object nextElement() {
                Object element = new HepRepTypeInfoAdapter(HepRepAdapter.this, typeInfoList[i]);
                i++;
                return element;
            }
        };
    }
    
    public HepRepType getRepresentablesUncut(String name,
					     String version) {
        HepRepType hepRepType = new HepRepTypeAdapter(null, hepRep.getRepresentablesUncut(name, version));
        addType(hepRepType);
        return hepRepType;
    }
    
    public HepRepType getRepresentables(String name,
					String version, 
                                        HepRepCut[] cutList,
                                        boolean getPoints,
                                        boolean getDrawAtts,
                                        boolean getNonDrawAtts,
                                        HepRepAttName[] invertAtts) {
        
        // create idl cut list
        hep.graphics.heprep1.corba.idl.HepRepCut[] cuts = new hep.graphics.heprep1.corba.idl.HepRepCut[cutList.length];
        for (int i=0; i<cutList.length; i++) {
            Any any = ORB.init().create_any();
            if (cutList[i].getValue() instanceof String) {
                any.insert_string(cutList[i].getString());
            } else if (cutList[i].getValue() instanceof Long) {
                any.insert_longlong(cutList[i].getLong());
            } else if (cutList[i].getValue() instanceof Double) {
                any.insert_double(cutList[i].getDouble());
            } else {
                throw new ClassCastException("HepRepCut class value '"+cutList[i].getValue().getClass()+"' is not recognized.");
            }                
            cuts[i] = new hep.graphics.heprep1.corba.idl.HepRepCut(cutList[i].getName(),
                                                                   cutList[i].getComparison(),
                                                                   any);
        }
        
        // create idl attname list
        hep.graphics.heprep1.corba.idl.HepRepAttName[] attNames = new hep.graphics.heprep1.corba.idl.HepRepAttName[invertAtts.length];
        for (int i=0; i<invertAtts.length; i++) {
            attNames[i] = new hep.graphics.heprep1.corba.idl.HepRepAttName(invertAtts[i].getName());
        }
        
        HepRepType hepRepType =  new HepRepTypeAdapter(null, hepRep.getRepresentables(name,
												    version,
												    cuts, 
												    getPoints, getDrawAtts, getNonDrawAtts,
												    attNames));
        addType(hepRepType);
        return hepRepType;
    }

    public Enumeration getTypes() {
        return types.elements();
    }
    
    public void addType(HepRepType type) {
        types.addElement(type);
    }
    
    public boolean removeType(HepRepType type) {
        return types.removeElement(type);
    }

    protected hep.graphics.heprep1.corba.idl.HepRepAttValue[] getAttValuesFromNode() {
        return hepRep.getAttValues();
    }
            
    protected hep.graphics.heprep1.corba.idl.HepRepAttDef[] getAttDefsFromNode() {
        return hepRep.getAttDefs();
    }
}

