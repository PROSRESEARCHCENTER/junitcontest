// Copyright 2000-2003, FreeHEP.
package hep.graphics.heprep.ref;

import hep.graphics.heprep.HepRepAttDef;

import java.io.Serializable;

/**
 *
 * @author M.Donszelmann
 *
 * @version $Id: DefaultHepRepAttDef.java 8584 2006-08-10 23:06:37Z duns $
 */

public class DefaultHepRepAttDef implements HepRepAttDef, Serializable {

    private String desc, category, extra;
    private String name;
    private String lowerCaseName;

    /**
     * Create a Attribute Definition
     * @param name name of the definition
     * @param desc description of the definition
     * @param category category of the definition
     * @param extra unit of the definition
     */
    public DefaultHepRepAttDef(String name, String desc, String category, String extra) {
        this.name = name.intern();
        this.lowerCaseName = name.toLowerCase().intern();
        this.desc = (desc == null) ? null : desc.intern();
        this.category = (category == null) ? null : category.intern();
        this.extra = (extra == null) ? null : extra.intern();
    }

    public HepRepAttDef copy() throws CloneNotSupportedException {
        return new DefaultHepRepAttDef(name, desc, category, extra);
    }

    void replace(DefaultHepRepAttDef def) {
        name = def.name;
        lowerCaseName = def.lowerCaseName;
        desc = def.desc;
        category = def.category;
        extra = def.extra;
    }

    public String getName() {
        return name;
    }

    public String getLowerCaseName() {
        return lowerCaseName;
    }

    public String getDescription() {
        return desc;
    }

    public String getCategory() {
        return category;
    }

    public String getExtra() {
        return extra;
    }
    
/* Disabled for FREEHEP-386
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
*/
    
    public String toString() {
        return getClass()+" ["+
               "name(lcase)="+getLowerCaseName()+", "+
               "description="+getDescription()+", "+
               "category="+getCategory()+", "+
               "extra="+getExtra()+"]";
    }
}

