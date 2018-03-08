// Copyright 2000-2003, FreeHEP.
package hep.graphics.heprep.ref;

import hep.graphics.heprep.HepRepTreeID;

import java.io.Serializable;


/**
 *
 * @author M.Donszelmann
 *
 * @version $Id: DefaultHepRepTreeID.java 8584 2006-08-10 23:06:37Z duns $
 */

public class DefaultHepRepTreeID implements HepRepTreeID, Serializable {

    private String qualifier;
    private String name;
    private String version;

    protected DefaultHepRepTreeID(String name, String version) {
        this(name, version, "top-level");
    }

    protected DefaultHepRepTreeID(String name, String version, String qualifier) {
        this.name = name;
        this.version = version;
        this.qualifier = qualifier;
    }

    public String getQualifier() {
        return qualifier;
    }

    public void setQualifier(String qualifier) {
        this.qualifier = qualifier;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
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
    
    public String toString() {
        return getClass()+": "+getQualifier()+":"+getName()+":"+getVersion();
    }
}

