// Copyright 2005, FreeHEP.
package hep.graphics.heprep.util;

import java.util.*;

import hep.graphics.heprep.*;

/**
 * Handles the registration and lookups of types with flat/hierarchical names.
 *
 * @author M.Donszelmann
 * @version $Id: HepRepTypes.java 8584 2006-08-10 23:06:37Z duns $
 */

public class HepRepTypes {

// NOTE: we could use HepRepTreeID to index this Map, but we need to override equals() and
//       hashCode() in DefaultHepRepTreeID and DefaultHepRepTree...
    private Map/*<name:version, HepRepTypeTree>*/ typeTrees = new HashMap();
    private Map/*<name:version,Map<String,HepRepType>>*/ flatTypeTrees = new HashMap();      
                               // if null, typetree does not have flat space

    /**
     * Create an HepRepTypes registry
     */
    public HepRepTypes() {
    }

    /**
     * Add a typetree with given id
     * @param id tree id
     * @param tree type tree
     */
    public void put(HepRepTreeID id, HepRepTypeTree tree) {
        typeTrees.put(getID(id), tree);
        flatTypeTrees.put(getID(id), new HashMap());
    }        

    /**
     * Add a named type for given tree
     * @param tree tree
     * @param name name of type
     * @param type type
     */
    public void put(HepRepTypeTree tree, String name, HepRepType type) {
        Map/*<String, HepRepType>*/ types = (Map)flatTypeTrees.get(getID(tree));
        if (types != null) {
            if (types.get(name) == null) {
                types.put(name, type);
            } else {
                flatTypeTrees.put(getID(tree), null);
            }
        }
    } 

    /**
     * Lookup type in flat namespace and hierarchical namespace.
     * @param treeID tree to look in
     * @param name name of type to look for
     * @param parent parent instance to get full type name for lookup in hierarchical namespace
     * @return type or null if not found
     */
    public HepRepType getType(HepRepTreeID treeID, String name, HepRepInstance parent) {
        // check flat namespace
        HepRepType type = getTypeFromFlatNamespace(treeID, name);
        if (type != null) return type;
        
        // check hierarchical namespace
        type = getTypeFromHierarchicalNamespace(treeID, name);
        if (type != null) return type;

        // (GLAST) check hierarchical namespace by pre-pending parents full typename
        return getTypeFromHierarchicalNamespace(treeID, parent.getType().getFullName()+"/"+name);
    }
    
    
    private HepRepType getTypeFromFlatNamespace(HepRepTreeID treeID, String name) {
                                                        
        // flat types
        Map/*<String, HepRepType>*/ flatTypes = (Map)flatTypeTrees.get(getID(treeID));
        if (flatTypes == null) return null;
        
        return (HepRepType)flatTypes.get(name);
    }
     
    private HepRepType getTypeFromHierarchicalNamespace(HepRepTreeID treeID, String name) {           
        HepRepType type = null;

        // hierarchical types        
        HepRepTypeTree tree = (HepRepTypeTree)typeTrees.get(getID(treeID));
        if (tree == null) return null;
        
        // remove leading slash
        if (name.charAt(0) == '/') name = name.substring(1);
        
        Collection types = tree.getTypeList();
            
        // look if all partNames can be found in all types
        int slash;
        do {
            // split name into leading partName and everything else behind the slash
            slash = name.indexOf("/");
            String partName = (slash < 0) ? name : name.substring(0, slash);
            name = (slash < 0) ? "" : name.substring(slash+1);
            
            // look for partName in all types
            if (types == null) return null;
            Iterator typeIterator = types.iterator();
            boolean found = false;
            while (!found && typeIterator.hasNext()) {
                type = (HepRepType)typeIterator.next();
                if (type.getName().equals(partName)) {
                    types = type.getTypeList();
                    found = true;
                }
            }
            if (!found) return null;
        } while (slash >= 0);

        return type;
    }

    /**
     * Combines name and version in one string
     * @param id tree id
     * @return name version string
     */
    public String getID(HepRepTreeID id) {
        return id.getName()+":"+id.getVersion();
    }
}
