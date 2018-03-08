// Copyright FreeHEP 2005.
package hep.graphics.heprep1.corba;

import hep.graphics.heprep1.HepRepAttDef;

/**
 *
 * @author M.Donszelmann
 *
 * @version $Id: HepRepAttDefAdapter.java 8584 2006-08-10 23:06:37Z duns $
 */

public class HepRepAttDefAdapter implements HepRepAttDef {

    private hep.graphics.heprep1.corba.idl.HepRepAttDef hepRepAttDef;

    /**
     * Add a CORBA Wrapper
     * @param hepRepAttDef corba att def
     */
    public HepRepAttDefAdapter(hep.graphics.heprep1.corba.idl.HepRepAttDef hepRepAttDef) {
        this.hepRepAttDef = hepRepAttDef;
    }

    public String getName() {
        return hepRepAttDef.name;
    }
    
    public String getDescription() {
        return hepRepAttDef.desc;
    }

    public String getType() {
        return hepRepAttDef.type;
    }

    public String getExtra() {
        return hepRepAttDef.extra;
    }
}

