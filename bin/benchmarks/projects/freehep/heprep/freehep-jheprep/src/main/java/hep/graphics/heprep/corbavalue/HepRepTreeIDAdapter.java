// Copyright 2000-2003, FreeHEP.
package hep.graphics.heprep.corbavalue;

import hep.graphics.heprep.HepRepTreeID;

/**
 *
 * @author Mark Donszelmann
 *
 * @version $Id: HepRepTreeIDAdapter.java 8584 2006-08-10 23:06:37Z duns $
 */

public class HepRepTreeIDAdapter implements HepRepTreeID {

    private hep.graphics.heprep.corbavalue.idl.HepRepTreeID hepRepTreeID;

    /**
     * Create a CORBA wrapper for a TreeID
     * @param hepRepTreeID corba tree id
     */
    public HepRepTreeIDAdapter(hep.graphics.heprep.corbavalue.idl.HepRepTreeID hepRepTreeID) {
        super();
        this.hepRepTreeID = hepRepTreeID;
    }

    public String getQualifier() {
        return "top-level";
    }

    public void setQualifier(String qualifier) {
        throw new UnsupportedOperationException("HepRepTreeIDAdapter.setQualifier is not implemented.");
    }

    public String getName() {
        return hepRepTreeID.name;
    }

    public String getVersion() {
        return hepRepTreeID.version;
    }

    // NOTE: qualifier not part of equals and hashCode
    public boolean equals(Object o) {
        if (o == this) return true;
        if (o instanceof HepRepTreeID) {
            HepRepTreeID id = (HepRepTreeID)o;
            if (!getName().equals(id.getName())) return false;
            return (getVersion() == null) ? (id.getVersion() == null) : getVersion().equals(id.getVersion());
        }
        return false;
    }
    
    // NOTE: qualifier not part of equals and hashCode
    public int hashCode() {
        long code = getName().hashCode();
        code += (getVersion() != null) ? getVersion().hashCode() : 0;
        return (int)code;
    }
}

