// Copyright 2000-2003, FreeHEP.
package hep.graphics.heprep.ref;

import java.io.*;
import java.util.*;

import hep.graphics.heprep.*;
import hep.graphics.heprep.util.*;


/**
 *
 * @author M.Donszelmann
 *
 * @version $Id: DefaultHepRepTypeTree.java 8584 2006-08-10 23:06:37Z duns $
 */

public class DefaultHepRepTypeTree extends DefaultHepRepTreeID implements HepRepTypeTree, Serializable {

    private MapList/*<Name, HepRepType>*/ types;

    protected DefaultHepRepTypeTree(HepRepTreeID typeTree) {
        super(typeTree.getName(), typeTree.getVersion());
    }

    public HepRepTypeTree copy() throws CloneNotSupportedException {
        DefaultHepRepTypeTree copy = new DefaultHepRepTypeTree(this);

        // copy type
        for (Iterator i=getTypeList().iterator(); i.hasNext(); ) {
            HepRepType type = (HepRepType)i.next();
            HepRepType typeCopy = type.copy(null);
            copy.addType(typeCopy);
        }

        return copy;
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
//        System.out.println("DHRTypeTree: Serializing "+this);
        stream.defaultWriteObject();
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
//        System.out.println("DHRTypeTree: Deserializing "+this);
        stream.defaultReadObject();
    }

    public void addType(HepRepType type) {
        if (types == null) types = new HashMapList();
        types.put(type.getName(), type);
    }

    public Set/*<HepRepType>*/ getTypes() {
        return types.valueSet();
    }

    public List/*<HepRepType>*/ getTypeList() {
        return types.valueList();
    }

    public HepRepType getType(String fullName) {
        return HepRepUtil.getType(types.valueList(), fullName);
    }

/* Disabled for FREEHEP-386
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
*/

    /**
     * @return a string representation of this HepRepTypeTree
     */
    public String toString() {
        return "HepRepTypeTree: "+getQualifier()+":"+getName()+":"+getVersion();
    }

    /**
     * Dumps Type Tree for debugging purposes
     * @param indent indent string
     */
    public void display(String indent) {
        System.out.println(indent+toString());
        for (Iterator i=getTypeList().iterator(); i.hasNext(); ) {
            DefaultHepRepType type = (DefaultHepRepType)i.next();
            type.display(indent+"  ");
        }
    }
}

