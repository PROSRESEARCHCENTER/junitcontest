// Copyright 2004, FreeHEP.
package hep.graphics.heprep1.adapter;

import hep.graphics.heprep.HepRepType;

/**
 *
 * @author Mark Donszelmann
 * @version $Id: HepRepTypeFromHepRepAdapter.java 8584 2006-08-10 23:06:37Z duns $
 */
public class HepRepTypeFromHepRepAdapter extends AbstractHepRepTypeAdapter {

    /**
     * Wrapper for HepRep1 HepRep
     * @param heprep1 heprep1 heprep
     * @param parent heprep2 parent type
     */
    public HepRepTypeFromHepRepAdapter(hep.graphics.heprep1.HepRep heprep1,
                                       HepRepType parent) {
        super(heprep1, parent);
    }

    public void addType(HepRepType type) {
        types.add(type);
    }    
    
    public String getName() {
        return "HepRep1";
    }
}
