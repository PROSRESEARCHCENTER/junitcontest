// Copyright FreeHEP 2005.
package hep.graphics.heprep1.corba;

import java.util.Enumeration;

import hep.graphics.heprep1.*;

/**
 *
 * @author M.Donszelmann
 *
 * @version $Id: HepRepPrimitiveAdapter.java 8584 2006-08-10 23:06:37Z duns $
 */

public class HepRepPrimitiveAdapter extends HepRepAttributeAdapter implements HepRepPrimitive {

    private hep.graphics.heprep1.corba.idl.HepRepPrimitive hepRepPrimitive;

    /**
     * Add a CORBA Wrapper
     * @param parent attribute parent
     * @param hepRepPrimitive corba primitive
     */
    public HepRepPrimitiveAdapter(HepRepAttribute parent, hep.graphics.heprep1.corba.idl.HepRepPrimitive hepRepPrimitive) {
        super(parent);
        this.hepRepPrimitive = hepRepPrimitive;  
    }

    public HepRepInstance getInstance() {
        return (HepRepInstance)getParent();
    }
    
    public Enumeration getPoints() {
        return new Enumeration() {
            private int i = 0;
            
            public boolean hasMoreElements() {
                return i < hepRepPrimitive.points.length;
            }
            
            public Object nextElement() {
                Object element = new HepRepPointAdapter(HepRepPrimitiveAdapter.this, hepRepPrimitive.points[i]);
                i++;
                return element;
            }
        };
    }

    protected hep.graphics.heprep1.corba.idl.HepRepAttValue[] getAttValuesFromNode() {
        return hepRepPrimitive.attValues;
    }
            
    protected hep.graphics.heprep1.corba.idl.HepRepAttDef[] getAttDefsFromNode() {
        return hepRepPrimitive.attDefs;
    }
}

