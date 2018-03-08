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
 * @version $Id: DefaultHepRepType.java 8584 2006-08-10 23:06:37Z duns $
 */

public class DefaultHepRepType extends DefaultHepRepDefinition implements HepRepType, Serializable {

    private HepRepType parent;
    private String name;
    private String description = "No Description";
    private String infoURL = "No Info URL";
    private MapList/*<Name, HepRepType>*/ types;

    // for root level and for easy JNI interface
    protected DefaultHepRepType(String name) {
        this((HepRepType)null, name);
    }

    protected DefaultHepRepType(HepRepType parent, String name) {
        this.parent = parent;
        this.name = name.intern();

        // HepRepTypes are sometimes used without a parent (top-level)
        if (parent != null) {
            parent.addType(this);
        }
    }

    protected DefaultHepRepType(HepRepTypeTree parent, String name) {
        this((HepRepType)null, name);
        parent.addType(this);
    }

    public HepRepType getSuperType() {
        return parent;
    }

    /**
     * searched for a definition with given name. Search up the type tree if needed.
     */
    public HepRepAttDef getAttDef(String name) {
        return getAttDef(this, name.toLowerCase());
    }

    /**
     * Finds a Attribute Definition by looking from type, up through the type tree. 
     * If not found the default Attribute Definition will be returned.
     * 
     * @param type type to start from
     * @param lowerCaseName name of the Attribute Definition
     * @return most specific occurence of the Attribute Definition, or null if not found
     */
    public static HepRepAttDef getAttDef(HepRepType type, String lowerCaseName) {
        HepRepAttDef def = null;
        while ((def == null) && (type != null)) {
            def = type.getAttDefFromNode(lowerCaseName);
            type = type.getSuperType();
        }

        // found, otherwise return default
        if (def != null) {
            return def;
        } else {
            return HepRepDefaults.getAttDef(lowerCaseName);
        }
    }

    /**
     * searched for a value with given name. Search up the type tree if needed.
     */
    public HepRepAttValue getAttValue(String name) {
        return getAttValue(this, name.toLowerCase());
    }

    /**
     * Finds a Attribute Value by looking from type, up through the type tree. 
     * If not found the default Attribute Value will be returned.
     * 
     * @param type type to start from
     * @param lowerCaseName name of the Attribute Value
     * @return most specific occurence of the Attribute Value, or null if not found
     */
    public static HepRepAttValue getAttValue(HepRepType type, String lowerCaseName) {
        HepRepAttValue value = null;
        while ((value == null) && (type != null)) {
            value = type.getAttValueFromNode(lowerCaseName);
            type = type.getSuperType();
        }

        // found, otherwise return default
        if (value !=  null) {
            return value;
        } else {
            return HepRepDefaults.getAttValue(lowerCaseName);
        }
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
        stream.defaultWriteObject();
//        LinkedList.writeList(stream, typeList);
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
        stream.defaultReadObject();
//        typeList = LinkedList.readList(stream);
    }

    public HepRepType copy(HepRepType parent) throws CloneNotSupportedException {
        DefaultHepRepType copy = new DefaultHepRepType(parent, getName());
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
//            System.out.println("Copy Subtype");
            HepRepType type = (HepRepType)i.next();
            type.copy(copy);
            // no need to add the typecopy to the copy since it is already its parent.
        }
        return copy;
    }

    public String getName() {
        return name;
    }

    public String getFullName() {
        return (getSuperType() == null) ? getName() : getSuperType().getFullName() + "/"+getName();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getInfoURL() {
        return infoURL;
    }

    public void setInfoURL(String infoURL) {
        this.infoURL = infoURL;
    }

    public void addType(HepRepType type) {
        if (types == null) types = new HashMapList();
        types.put(type.getName(), type);
    }

    public Set/*<HepRepType>*/ getTypes() {
        return types != null ? types.valueSet() : Collections.EMPTY_SET;
    }
    
    public List/*<HepRepType>*/ getTypeList() {
        return types != null ? types.valueList() : Collections.EMPTY_LIST;
    }
    
/* Disabled for FREEHEP-386
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!super.equals(o)) return false;
        if (o instanceof HepRepType) {
            HepRepType type = (HepRepType)o;
            if (!type.getFullName().equals(getFullName())) return false;
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
*/

   /**
     * @return a string representation of this HepRepType
     */
    public String toString() {
        return "HepRepType: "+getName();
    }

    /**
     * Dumps Type for debugging purposes
     * @param indent indent string
     */
    public void display(String indent) {
        System.out.println(indent+toString());
        System.out.println(indent+getDescription());
        System.out.println(indent+getInfoURL());
        System.out.println(indent+"#Defs: "+getAttDefsFromNode().size());
        System.out.println(indent+"#Atts: "+getAttValuesFromNode().size());
        for (Iterator i=getTypeList().iterator(); i.hasNext(); ) {
            DefaultHepRepType type = (DefaultHepRepType)i.next();
            type.display(indent+"  ");
        }
    }
}

