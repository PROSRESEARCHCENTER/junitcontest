// Copyright 2004, FreeHEP.
package hep.graphics.heprep1.adapter;

import hep.graphics.heprep.HepRepType;

/**
 *
 * @author Mark Donszelmann
 * @version $Id: HepRepTypeAdapter.java 8584 2006-08-10 23:06:37Z duns $
 */
public class HepRepTypeAdapter extends AbstractHepRepTypeAdapter {

    private hep.graphics.heprep1.HepRepType type1;

    /**
     * Wrapper for HepRep1 Type
     * @param type1 heprep1 type
     * @param attribute1 heprep1 attribute
     * @param parent heprep2 type
     */
    public HepRepTypeAdapter(hep.graphics.heprep1.HepRepType type1,
                             hep.graphics.heprep1.HepRepAttribute attribute1,
                             HepRepType parent) {
        super(attribute1, parent);
        this.type1 = type1;
    }
    
    public String getName() {
        return type1.getName();
    }
    
    public boolean equals(Object obj) {
        if (obj instanceof HepRepTypeAdapter) {
            HepRepTypeAdapter hrt = (HepRepTypeAdapter)obj;
            return type1.equals(hrt.type1);
        }
        return super.equals(obj);
    }
    
    public int hashCode() {
        return type1.hashCode();
    }
    
    public String toString() {
        return "HepRepTypeAdapter: "+getName();
    }
}
