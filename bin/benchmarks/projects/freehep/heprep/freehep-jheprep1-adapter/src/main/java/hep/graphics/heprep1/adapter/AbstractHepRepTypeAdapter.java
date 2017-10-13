// Copyright 2004, FreeHEP.
package hep.graphics.heprep1.adapter;

import java.util.*;

import hep.graphics.heprep.HepRepAttDef;
import hep.graphics.heprep.HepRepAttValue;
import hep.graphics.heprep.HepRepType;
import hep.graphics.heprep.ref.DefaultHepRepType;
import hep.graphics.heprep.util.ListSet;
import hep.graphics.heprep.util.ArrayListSet;

/**
 *
 * @author Mark Donszelmann
 * @version $Id: AbstractHepRepTypeAdapter.java 8584 2006-08-10 23:06:37Z duns $
 */
public abstract class AbstractHepRepTypeAdapter extends HepRepDefinitionAdapter implements HepRepType {

    protected HepRepType parent;
    protected ListSet/*<HepRepType>*/ types;

    /**
     * Type Wrapper
     * @param attribute1 heprep1 type
     * @param parent parent type
     */
    public AbstractHepRepTypeAdapter(hep.graphics.heprep1.HepRepAttribute attribute1,
                                     HepRepType parent) {
        super(attribute1);
        this.parent = parent;
        this.types = new ArrayListSet();
    }

    public void addType(HepRepType type) {
        new UnsupportedOperationException();
    }
    
    public String getFullName() {
        return (getSuperType() == null) ? getName() : getSuperType().getFullName() + "/"+getName();
    }

    public String getDescription() {
        return "";
    }

    public void setDescription(String description) {
        new UnsupportedOperationException();
    }

    public String getInfoURL() {
        return "";
    }

    public void setInfoURL(String infoURL) {
        new UnsupportedOperationException();
    }

    public HepRepType getSuperType() {
        return parent;
    }

    public Set/*<HepRepType>*/ getTypes() {
        return types;
    }

    public List/*<HepRepType>*/ getTypeList() {
        return types;
    }

    /**
     * searched for a definition with given name. Search up the type tree if needed.
     */
    public HepRepAttDef getAttDef(String name) {
        return DefaultHepRepType.getAttDef(this, name.toLowerCase());
    }
    
    /**
     * searched for a value with given name. Search up the type tree if needed.
     */
    public HepRepAttValue getAttValue(String name) {
        return DefaultHepRepType.getAttValue(this, name.toLowerCase());
    }

    public HepRepType copy(HepRepType parent) throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }
}
