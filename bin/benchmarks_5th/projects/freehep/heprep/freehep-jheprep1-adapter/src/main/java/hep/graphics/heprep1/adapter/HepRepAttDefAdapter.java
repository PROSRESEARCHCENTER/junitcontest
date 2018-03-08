// Copyright 2004, FreeHEP.
package hep.graphics.heprep1.adapter;

import hep.graphics.heprep.ref.DefaultHepRepAttDef;

/**
 *
 * @author Mark Donszelmann
 * @version $Id: HepRepAttDefAdapter.java 8584 2006-08-10 23:06:37Z duns $
 */
public class HepRepAttDefAdapter extends DefaultHepRepAttDef {

    /**
     * Wrapper for HepRep1 AttDef
     * @param attDef heprep1 AttDef
     * @param name heprep2 name
     * @param desc heprep2 description
     * @param category heprep2 category
     * @param extra heprep2 extra
     */
    public HepRepAttDefAdapter(hep.graphics.heprep1.HepRepAttDef attDef, String name, String desc, String category, String extra) {
        super(name, desc, category, extra);
    }
}