// Copyright 2000-2003, FreeHEP.
package hep.graphics.heprep.rmi;

import java.rmi.*;
import java.util.*;

import hep.graphics.heprep.*;
import hep.graphics.heprep.ref.*;
import hep.graphics.heprep.util.*;

/**
 *
 * @author M.Donszelmann
 *
 * @version $Id: HepRepAdapter.java 8584 2006-08-10 23:06:37Z duns $
 */

public class HepRepAdapter implements HepRep {

    private HepRepFactory factory;
    private RMIHepRep heprep;
    private MapList/*<TreeID, HepRepInstanceTrees>*/ instanceTrees = new HashMapList();
    private MapList/*<TreeID, HepRepTypeTrees>*/ typeTrees = new HashMapList(); 

    /**
     * Create a wrapper for the top-level RMI HepRep object
     * @param heprep rmi heprep
     */
    public HepRepAdapter(RMIHepRep heprep) {
        this.heprep = heprep;
        factory = new DefaultHepRepFactory();
    }

    public HepRep copy() throws CloneNotSupportedException {
        return copy(null);
    }

    public HepRep copy(HepRepSelectFilter filter) throws CloneNotSupportedException {
        HepRep copy = factory.createHepRep();

        // copy layers
        for (Iterator i=getLayerOrder().iterator(); i.hasNext(); ) {
            copy.addLayer((String)i.next());
        }

        // copy type trees
        for (Iterator i=getTypeTreeList().iterator(); i.hasNext(); ) {
            HepRepTypeTree typeTree = (HepRepTypeTree)i.next();
            copy.addTypeTree(typeTree.copy());
        }

        // copy instance trees
        for (Iterator i=getInstanceTreeList().iterator(); i.hasNext(); ) {
            HepRepInstanceTree instanceTree = (HepRepInstanceTree)i.next();
            HepRepTreeID typeTreeID = instanceTree.getTypeTree();
            HepRepTypeTree typeTree = copy.getTypeTree(typeTreeID.getName(), typeTreeID.getVersion());
            copy.addInstanceTree(instanceTree.copy(typeTree, filter));
        }
        return copy;
    }

    public void addLayer(String layer) {
        throw new RuntimeException("HepRepAdapter.addLayer is not implemented.");
    }

    public List getLayerOrder() {
        try {
            return heprep.getLayerOrder();
        } catch (RemoteException re) {
            return null;
        }
    }

    public void addTypeTree(HepRepTypeTree typeTree) {
        if (typeTree == null) return;
        typeTrees.put(factory.createHepRepTreeID(typeTree.getName(), typeTree.getVersion()), typeTree);
    }

    public void removeTypeTree(HepRepTypeTree typeTree) {
        if (typeTree == null) return;
        typeTrees.remove(factory.createHepRepTreeID(typeTree.getName(), typeTree.getVersion()));
    }

    public HepRepTypeTree getTypeTree(String name, String version) {
        try {
            HepRepTypeTree typeTree = (HepRepTypeTree)typeTrees.get(factory.createHepRepTreeID(name, version));
            if (typeTree == null) {
                typeTree = heprep.getTypeTree(name, version);
                addTypeTree(typeTree);
            }
            return typeTree;
        } catch (RemoteException re) {
            return null;
        }
    }

    public Set/*<HepRepTypeTree>*/ getTypeTrees() {
        return typeTrees.valueSet();
    }

    public List/*<HepRepTypeTree>*/ getTypeTreeList() {
        return typeTrees.valueList();
    }

    public void addInstanceTree(HepRepInstanceTree instanceTree) {
        if (instanceTree == null) return;
        instanceTrees.put(factory.createHepRepTreeID(instanceTree.getName(), instanceTree.getVersion()), instanceTree);
    }

    public void overlayInstanceTree(HepRepInstanceTree instanceTree) {
        throw new RuntimeException("HepRepAdapter.overlayInstanceTree is not implemented.");
    }

    public void removeInstanceTree(HepRepInstanceTree instanceTree) {
        if (instanceTree == null) return;
        instanceTrees.remove(factory.createHepRepTreeID(instanceTree.getName(), instanceTree.getVersion()));
    }

    public HepRepInstanceTree getInstanceTreeTop(String name, String version) {
        try {
            HepRepInstanceTree instanceTree = (HepRepInstanceTree)instanceTrees.get(factory.createHepRepTreeID(name, version));
            if (instanceTree == null) {
                instanceTree = heprep.getInstanceTreeTop(name, version);
                addInstanceTree(instanceTree);
            }
            return instanceTree;
        } catch (RemoteException re) {
            return null;
        }
    }

    public HepRepInstanceTree getInstances(String name, String version, String[] typeNames) {
        try {
            HepRepInstanceTree instanceTree = (HepRepInstanceTree)instanceTrees.get(factory.createHepRepTreeID(name, version));
            if (instanceTree == null) {
                instanceTree = heprep.getInstances(name, version, typeNames);
                addInstanceTree(instanceTree);
            }
            return instanceTree;
        } catch (RemoteException re) {
            return null;
        }
    }

    public HepRepInstanceTree getInstancesAfterAction(
                                    String name,
                                    String version,
                                    String[] typeNames,
                                    HepRepAction[] actions,
                                    boolean getPoints,
                                    boolean getDrawAtts,
                                    boolean getNonDrawAtts,
                                    String[] invertAtts) {
        try {

            HepRepInstanceTree instanceTree = heprep.getInstancesAfterAction(
                                        name,
                                        version,
                                        typeNames,
                                        actions,
                                        getPoints,
                                        getDrawAtts,
                                        getNonDrawAtts,
                                        invertAtts);
            addInstanceTree(instanceTree);
            return instanceTree;
        } catch (RemoteException re) {
            return null;
        }
    }

    public String checkForException() {
        return "Not Implemented";
    }

    public Set/*<HepRepInstanceTree>*/ getInstanceTrees() {
        return instanceTrees.valueSet();
    }

    public List/*<HepRepInstanceTree>*/ getInstanceTreeList() {
        return instanceTrees.valueList();
    }

    public boolean equals(Object o) {
        if (o instanceof HepRep) {
            HepRep ref = (HepRep)o;
            if (!ref.getLayerOrder().equals(getLayerOrder())) return false;
            if (!ref.getTypeTreeList().equals(getTypeTreeList())) return false;
            if (!ref.getInstanceTreeList().equals(getInstanceTreeList())) return false;
            return true;
        }
        return false;
    }
    
    public int hashCode() {
        long code = getLayerOrder().hashCode();
        code += getTypeTreeList().hashCode();
        code += getInstanceTreeList().hashCode();
        return (int)code;
    }
}

