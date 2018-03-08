// Copyright FreeHEP 2005.
package hep.graphics.heprep1.corba;

import java.util.Enumeration;

import hep.graphics.heprep1.*;

/**
 *
 * @author M.Donszelmann
 *
 * @version $Id: HepRepTypeAdapter.java 8584 2006-08-10 23:06:37Z duns $
 */

public class HepRepTypeAdapter extends HepRepAttributeAdapter implements HepRepType {

    private hep.graphics.heprep1.corba.idl.HepRepType hepRepType;

    /**
     * Add a CORBA Wrapper
     * @param parent attribute parent
     * @param hepRepType corba type
     */
    public HepRepTypeAdapter(HepRepAttribute parent, hep.graphics.heprep1.corba.idl.HepRepType hepRepType) {
        super(parent);
        this.hepRepType = hepRepType;  
    }
    
    public String getName() {
        return hepRepType.name;
    }
    
    public String getVersion() {
        return hepRepType.version;
    }
    
    public HepRep getRoot() {
        HepRepAttribute parent = getParent();
        while (!(parent instanceof HepRep)) {
            parent = parent.getParent();
        }
        return (HepRep)parent;
    }
    
    public Enumeration getTypes() {
        return new Enumeration() {
            private int i = 0;
            
            public boolean hasMoreElements() {
                return i < hepRepType.types.length;
            }
            
            public Object nextElement() {
                Object element = new HepRepTypeAdapter(HepRepTypeAdapter.this, hepRepType.types[i]);
                i++;
                return element;
            }
        };
    }

    public Enumeration getPoints() {
        return new Enumeration() {
            private int i = 0;
            
            public boolean hasMoreElements() {
                return i < hepRepType.points.length;
            }
            
            public Object nextElement() {
                Object element = new HepRepPointAdapter(HepRepTypeAdapter.this, hepRepType.points[i]);
                i++;
                return element;
            }
        };
    }

    public Enumeration getPrimitives() {
        return new Enumeration() {
            private int i = 0;
            
            public boolean hasMoreElements() {
                return i < hepRepType.primitives.length;
            }
            
            public Object nextElement() {
                Object element = new HepRepPrimitiveAdapter(HepRepTypeAdapter.this, hepRepType.primitives[i]);
                i++;
                return element;
            }
        };
    }

    public Enumeration getInstances() {
        return new Enumeration() {
            private int i = 0;
            
            public boolean hasMoreElements() {
                return i < hepRepType.instances.length;
            }
            
            public Object nextElement() {
                Object element = new HepRepInstanceAdapter(HepRepTypeAdapter.this, hepRepType.instances[i]);
                i++;
                return element;
            }
        };
    }

    public String toString() {
        return "[HepRepType (corba):"+getName()+":"+getVersion()+"]";
    }

    protected hep.graphics.heprep1.corba.idl.HepRepAttValue[] getAttValuesFromNode() {
        return hepRepType.attValues;
    }
            
    protected hep.graphics.heprep1.corba.idl.HepRepAttDef[] getAttDefsFromNode() {
        return hepRepType.attDefs;
    }
    
    public boolean equals(Object obj) {
        if (obj instanceof HepRepTypeAdapter) {
            HepRepTypeAdapter hrt = (HepRepTypeAdapter)obj;
            return hepRepType.equals(hrt.hepRepType);
        }
        return super.equals(obj);
    }
    
    public int hashCode() {
        return hepRepType.hashCode();
    }
}

