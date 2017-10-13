// Copyright FreeHEP 2005.
package hep.graphics.heprep1.corba;

import java.util.Enumeration;

import hep.graphics.heprep1.*;
import hep.graphics.heprep1.corba.idl.HepRepTypeHelper;

/**
 *
 * @author M.Donszelmann
 *
 * @version $Id: HepRepInstanceAdapter.java 8584 2006-08-10 23:06:37Z duns $
 */

public class HepRepInstanceAdapter extends HepRepAttributeAdapter implements HepRepInstance {

    private hep.graphics.heprep1.corba.idl.HepRepInstance hepRepInstance;

    /**
     * Add a CORBA Wrapper
     * @param parent attribute parent
     * @param hepRepInstance corba instance
     */
    public HepRepInstanceAdapter(HepRepAttribute parent, hep.graphics.heprep1.corba.idl.HepRepInstance hepRepInstance) {
        super(parent);
        this.hepRepInstance = hepRepInstance;  
    }

    public HepRepType getType() {
        return (HepRepType)getParent();
    }

    public Enumeration getTypes() {
        return new Enumeration() {
            private int i = 0;
            
            public boolean hasMoreElements() {
                return i < hepRepInstance.types.length;
            }
            
            public Object nextElement() {
                Object element = new HepRepTypeAdapter(HepRepInstanceAdapter.this, 
                                                       HepRepTypeHelper.extract(hepRepInstance.types[i]));
                i++;
                return element;
            }
        };
    }

    public Enumeration getPoints() {
        return new Enumeration() {
            private int i = 0;
            
            public boolean hasMoreElements() {
                return i < hepRepInstance.points.length;
            }
            
            public Object nextElement() {
                Object element = new HepRepPointAdapter(HepRepInstanceAdapter.this, hepRepInstance.points[i]);
                i++;
                return element;
            }
        };
    }

    public Enumeration getPrimitives() {
        return new Enumeration() {
            private int i = 0;
            
            public boolean hasMoreElements() {
                return i < hepRepInstance.primitives.length;
            }
            
            public Object nextElement() {
                Object element = new HepRepPrimitiveAdapter(HepRepInstanceAdapter.this, hepRepInstance.primitives[i]);
                i++;
                return element;
            }
        };
    }

    protected hep.graphics.heprep1.corba.idl.HepRepAttValue[] getAttValuesFromNode() {
        return hepRepInstance.attValues;
    }
            
    protected hep.graphics.heprep1.corba.idl.HepRepAttDef[] getAttDefsFromNode() {
        return hepRepInstance.attDefs;
    }
}

