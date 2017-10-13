// Copyright 2000-2003, FreeHEP.
package hep.graphics.heprep.corbavalue;

import hep.graphics.heprep.HepRep;
import hep.graphics.heprep.HepRepAction;
import hep.graphics.heprep.HepRepFactory;
import hep.graphics.heprep.HepRepInstanceTree;
import hep.graphics.heprep.HepRepSelectFilter;
import hep.graphics.heprep.HepRepTreeID;
import hep.graphics.heprep.HepRepTypeTree;
import hep.graphics.heprep.ref.DefaultHepRepFactory;
import hep.graphics.heprep.util.HashMapList;
import hep.graphics.heprep.util.MapList;
import hep.graphics.heprep.xml.XMLHepRepReader;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Mark Donszelmann
 *
 * @version $Id: HepRepAdapter.java 8584 2006-08-10 23:06:37Z duns $
 */

public class HepRepAdapter implements HepRep {

    private HepRepFactory factory;
    private hep.graphics.heprep.corbavalue.idl.HepRep hepRep;
    private MapList/*<TreeID, HepRepInstanceTrees>*/ instanceTrees = new HashMapList();
    private MapList/*<TreeID, HepRepTypeTrees>*/ typeTrees = new HashMapList(); 

    /**
     * Create a wrapper for an HepRep
     * @param hepRep corba heprep
     */
    public HepRepAdapter(hep.graphics.heprep.corbavalue.idl.HepRep hepRep) {
        super();
        this.hepRep = hepRep;
        factory = new DefaultHepRepFactory();

        try {
            XMLHepRepReader.readDefaults();
        } catch(Exception e) {
            System.err.println("Warning: unable to read HepRep default attributes from XML");
        }
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
        return Arrays.asList(hepRep.getLayerOrder());
    }

    public void addTypeTree(HepRepTypeTree typeTree) {
        typeTrees.put(factory.createHepRepTreeID(typeTree.getName(), typeTree.getVersion()), typeTree);
    }

    public void removeTypeTree(HepRepTypeTree typeTree) {
        typeTrees.remove(factory.createHepRepTreeID(typeTree.getName(), typeTree.getVersion()));
    }

    public HepRepTypeTree getTypeTree(String name, String version) {
        HepRepTypeTree typeTree = (HepRepTypeTree)typeTrees.get(factory.createHepRepTreeID(name, version));
        if (typeTree == null) {
            typeTree = new HepRepTypeTreeAdapter(hepRep.getTypeTree(name, version));
            typeTrees.put(factory.createHepRepTreeID(typeTree.getName(), typeTree.getVersion()), typeTree);
        }
        return typeTree;
    }

    public Set/*<HepRepTypeTree>*/ getTypeTrees() {
        return typeTrees.valueSet();
    }

    public List/*<HepRepTypeTree>*/ getTypeTreeList() {
        return typeTrees.valueList();
    }

    public void addInstanceTree(HepRepInstanceTree instanceTree) {
        instanceTrees.put(factory.createHepRepTreeID(instanceTree.getName(), instanceTree.getVersion()), instanceTree);
    }

    public void overlayInstanceTree(HepRepInstanceTree instanceTree) {
        throw new RuntimeException("HepRepAdapter.overlayInstanceTree is not implemented.");
    }

    public void removeInstanceTree(HepRepInstanceTree instanceTree) {
        instanceTrees.remove(factory.createHepRepTreeID(instanceTree.getName(), instanceTree.getVersion()));
    }

    public HepRepInstanceTree getInstanceTreeTop(String name, String version) {
        HepRepInstanceTree instanceTree = (HepRepInstanceTree)instanceTrees.get(factory.createHepRepTreeID(name, version));
        if (instanceTree == null) {
            instanceTree = new HepRepInstanceTreeAdapter(hepRep.getInstanceTreeTop(name, version));
            instanceTrees.put(factory.createHepRepTreeID(instanceTree.getName(), instanceTree.getVersion()), instanceTree);
        }
        return instanceTree;
    }

    public HepRepInstanceTree getInstances(String name, String version,
                                           String[] typeNames) {
        // FIXME, not cached
        return new HepRepInstanceTreeAdapter(hepRep.getInstances(name, version, typeNames));
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
        hep.graphics.heprep.corbavalue.idl.HepRepAction[] hepRepActions =
            new hep.graphics.heprep.corbavalue.idl.HepRepAction[actions.length];
        for (int i=0; i<hepRepActions.length; i++) {
            hepRepActions[i] = null;
// FIXME: look up how to create objects in the corbavalue interface
// the following does not compile because the class is abstract
//                new hep.graphics.heprep.corbavalue.idl.HepRepAction(actions[i].getName(), actions[i].getExpression());
        }
        // FIXME, not cached
        return new HepRepInstanceTreeAdapter(
                        hepRep.getInstancesAfterAction(
                                    name, version, typeNames, hepRepActions,
                                    getPoints, getDrawAtts, getNonDrawAtts,
                                    invertAtts));
    }

    public String checkForException() {
        return hepRep.checkForException();
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

