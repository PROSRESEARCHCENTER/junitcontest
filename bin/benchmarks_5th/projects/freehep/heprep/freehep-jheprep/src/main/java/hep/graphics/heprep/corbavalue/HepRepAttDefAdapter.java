package hep.graphics.heprep.corbavalue;

import hep.graphics.heprep.HepRepAttDef;
import hep.graphics.heprep.ref.DefaultHepRepAttDef;
import hep.graphics.heprep.util.HepRepUtil;

/**
 *
 * @author Mark Donszelmann
 *
 * @version $Id: HepRepAttDefAdapter.java 8584 2006-08-10 23:06:37Z duns $
 */

public class HepRepAttDefAdapter implements HepRepAttDef {

    private hep.graphics.heprep.corbavalue.idl.HepRepAttDef hepRepAttDef;
    private String lowerCaseName;

    /**
     * Create a wrapper for an Attribute Definition
     * @param hepRepAttDef corba att def
     */
    public HepRepAttDefAdapter(hep.graphics.heprep.corbavalue.idl.HepRepAttDef hepRepAttDef) {
        this.hepRepAttDef = hepRepAttDef;
        lowerCaseName = hepRepAttDef.name.toLowerCase().intern();
    }

    public HepRepAttDef copy() throws CloneNotSupportedException {
        return new DefaultHepRepAttDef(getName(), getDescription(), getCategory(), getExtra());
    }

    public String getName() {
        return hepRepAttDef.name;
    }

    public String getLowerCaseName() {
        return lowerCaseName;
    }

    public String getDescription() {
        return hepRepAttDef.desc;
    }

    public String getCategory() {
        return hepRepAttDef.category;
    }

    public String getExtra() {
        return hepRepAttDef.extra;
    }

    public boolean equals(Object o) {
        if (o == this) return true;
        if (o instanceof HepRepAttDef) {
            HepRepAttDef def = (HepRepAttDef)o;
            boolean r = getLowerCaseName().equals(def.getLowerCaseName())
                     && getDescription().equals(def.getDescription())
                     && getCategory().equals(def.getCategory())
                     && getExtra().equals(def.getExtra());
            if (HepRepUtil.debug() && !r) {
                System.out.println(this+" != "+def);
            }
            return r;
        }
        return false;
    }
    
    public int hashCode() {
        return getLowerCaseName().hashCode() + getDescription().hashCode() + getCategory().hashCode() + getExtra().hashCode();
    }

    public String toString() {
        return getClass()+" ["+
               "name(lcase)="+getLowerCaseName()+", "+
               "description="+getDescription()+", "+
               "category="+getCategory()+", "+
               "extra="+getExtra()+"]";
    }
}

