// Copyright 2000-2003, FreeHEP.
package hep.graphics.heprep.corbavalue;

import java.util.*;

import hep.graphics.heprep.*;
import hep.graphics.heprep.ref.*;
import hep.graphics.heprep.util.*;

/**
 *
 * @author Mark Donszelmann
 *
 * @version $Id: HepRepTypeAdapter.java 8584 2006-08-10 23:06:37Z duns $
 */

public class HepRepTypeAdapter extends HepRepDefinitionAdapter implements HepRepType {

    private hep.graphics.heprep.corbavalue.idl.HepRepType hepRepType;
    private transient MapList/*<HepRepType>*/ types;

    /**
     * Create a CORBA wrapper for a Type
     * @param hepRepType corba type
     */
    public HepRepTypeAdapter(hep.graphics.heprep.corbavalue.idl.HepRepType hepRepType) {
        super(hepRepType);
        this.hepRepType = hepRepType;
    }

    public HepRepType copy(HepRepType parent) throws CloneNotSupportedException {
        HepRepType copy = new DefaultHepRepFactory().createHepRepType(parent, getName());
        copy.setDescription(getDescription());
        copy.setInfoURL(getInfoURL());
        HepRepUtil.copyAttributes(this, copy);

        // copy all def values
        for (Iterator i=getAttDefsFromNode().iterator(); i.hasNext(); ) {
            HepRepAttDef def = (HepRepAttDef)i.next();
            copy.addAttDef(def.copy());
        }

        // copy sub-type
        for (Iterator i=getTypeList().iterator(); i.hasNext(); ) {
            HepRepType type = (HepRepType)i.next();
            HepRepType typeCopy = type.copy(copy);
            copy.addType(typeCopy);
        }

        return copy;
    }

    public HepRepType getSuperType() {
        return new HepRepTypeAdapter(hepRepType.parent);
    }

    public String getName() {
        return hepRepType.name;
    }

    public String getFullName() {
        return (getSuperType() == null) ? getName() : getSuperType().getFullName() + "/"+getName();
    }

    public String getDescription() {
        return hepRepType.desc;
    }

    public void setDescription(String description) {
        throw new UnsupportedOperationException();
    }

    public String getInfoURL() {
        return hepRepType.infoURL;
    }

    public void setInfoURL(String infoURL) {
        throw new UnsupportedOperationException();
    }

    public Set/*<HepRepType>*/ getTypes() {
        if (types == null) {
            types = new HashMapList();
            int n = hepRepType.types.length;
            for (int i=0; i < n; i++) {
                types.put(hepRepType.types[i].name, new HepRepTypeAdapter(hepRepType.types[i]));
            }
        }
        return types.valueSet();
    }

    public List/*<HepRepType>*/ getTypeList() {
        if (types == null) {
            types = new HashMapList();
            int n = hepRepType.types.length;
            for (int i=0; i < n; i++) {
                types.put(hepRepType.types[i].name, new HepRepTypeAdapter(hepRepType.types[i]));
            }
        }
        return types.valueList();
    }

    public boolean equals(Object o) {
        if (o == this) return true;
        if (!super.equals(o)) return false;
        if (o instanceof HepRepType) {
            HepRepType type = (HepRepType)o;

            if (!type.getName().equals(getName())) return false;
            if (!type.getDescription().equals(getDescription())) return false;
            if (!type.getInfoURL().equals(getInfoURL())) return false;
            if (!type.getTypeList().equals(getTypeList())) return false;
            
            return true;
        }
        return false;
    }
    
    public int hashCode() {
        long code = super.hashCode();
        code += getName().hashCode();
        code += getDescription().hashCode();
        code += getInfoURL().hashCode();
        code += getTypeList().hashCode();
        return (int)code;
    }

    public void addType(HepRepType type) {
        throw new RuntimeException("HepRepTypeAdapter.addType is not implemented.");
    }

    public HepRepAttDef getAttDef(String name) {
        return DefaultHepRepType.getAttDef(this, name.toLowerCase());
    }

    public HepRepAttValue getAttValue(String name) {
        return DefaultHepRepType.getAttValue(this, name.toLowerCase());
    }

    public String toString() {
        return "[HepRepType (corba):"+getName()+"]";
    }
}

