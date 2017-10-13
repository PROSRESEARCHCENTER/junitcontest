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
 * @version $Id: HepRepTypeTreeAdapter.java 8584 2006-08-10 23:06:37Z duns $
 */

public class HepRepTypeTreeAdapter extends HepRepTreeIDAdapter implements HepRepTypeTree {

    private hep.graphics.heprep.corbavalue.idl.HepRepTypeTree hepRepTypeTree;
    private transient MapList/*<HepRepType>*/ types;

    /**
     * Create a CORBA wrapper for a Type Tree
     * @param hepRepTypeTree corba type tree
     */
    public HepRepTypeTreeAdapter(hep.graphics.heprep.corbavalue.idl.HepRepTypeTree hepRepTypeTree) {
        super(hepRepTypeTree);
        this.hepRepTypeTree = hepRepTypeTree;
    }

    public HepRepTypeTree copy() throws CloneNotSupportedException {
        HepRepTypeTree copy = new DefaultHepRepFactory().createHepRepTypeTree(this);

        // copy type
        for (Iterator i=getTypeList().iterator(); i.hasNext(); ) {
            HepRepType type = (HepRepType)i.next();
            HepRepType typeCopy = type.copy(null);
            copy.addType(typeCopy);
        }

        return copy;
    }

    public Set/*<HepRepType>*/ getTypes() {
        if (types == null) {
            types = new HashMapList();
            int n = hepRepTypeTree.types.length;
            for (int i=0; i < n; i++) {
                types.put(hepRepTypeTree.types[i].name, new HepRepTypeAdapter(hepRepTypeTree.types[i]));
            }
        }
        return types.valueSet();
    }

    public List/*<HepRepType>*/ getTypeList() {
        if (types == null) {
            types = new HashMapList();
            int n = hepRepTypeTree.types.length;
            for (int i=0; i < n; i++) {
                types.put(hepRepTypeTree.types[i].name, new HepRepTypeAdapter(hepRepTypeTree.types[i]));
            }
        }
        return types.valueList();
    }

    public boolean equals(Object o) {
        if (o == this) return true;
        if (!super.equals(o)) return false;
        if (o instanceof HepRepTypeTree) {
            HepRepTypeTree tree = (HepRepTypeTree)o;
            if (!tree.getTypeList().equals(getTypeList())) return false;
            
            return true;
        }
        return false;
    }
    
    public int hashCode() {
        long code = super.hashCode();
        code += getTypeList().hashCode();
        return (int)code;
    }

    public HepRepType getType(String fullName) {
        return HepRepUtil.getType(types.valueList(), fullName);
    }

    public void addType(HepRepType type) {
        throw new RuntimeException("HepRepTypeTreeAdapter.addType is not implemented.");
    }

    public String toString() {
        return "[HepRepTypeTree (corba):"+getQualifier()+":"+getName()+":"+getVersion()+"]";
    }

}

