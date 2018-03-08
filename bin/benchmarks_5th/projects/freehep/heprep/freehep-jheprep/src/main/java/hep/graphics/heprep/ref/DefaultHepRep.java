// Copyright 2000-2005, FreeHEP.
package hep.graphics.heprep.ref;

import java.io.*;
import java.util.*;

import hep.graphics.heprep.*;
import hep.graphics.heprep.xml.*;
import hep.graphics.heprep.util.*;

/**
 *
 * @author M.Donszelmann
 *
 * @version $Id: DefaultHepRep.java 8584 2006-08-10 23:06:37Z duns $
 */

public class DefaultHepRep implements HepRep, Serializable {

    private Properties properties = new Properties();
    private List/*<String>*/ layers = null;
    private MapList/*<TreeID, HepRepInstanceTrees>*/ instanceTrees = new HashMapList();
    private MapList/*<TreeID, HepRepTypeTrees>*/ typeTrees = new HashMapList(); 
    
    protected DefaultHepRep() {
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
        DefaultHepRep copy = new DefaultHepRep();

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

    // NOTE: in preparation for HEPREP-13
    /**
     * Set a property on the HepRep, which can then be used by the viewer
     * @param key name of the property
     * @param value value of the property
     * @return previous value of the property of null
     */
    public String setProperty(String key, String value) {
        return (String)properties.setProperty(key, value);
    }
    
    /**
     * Returns a property from the HepRep
     * @param key name of the property
     * @return value of the property or null
     */
    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    public List/*<String>*/ getLayerOrder() {
        if (layers == null) addLayer("default");
        return layers;
    }

    public void addLayer(String layer) {
        if (layers == null) layers = new ArrayList();
        layers.add(layer);
    }

    public void addTypeTree(HepRepTypeTree type) {
        typeTrees.put(new DefaultHepRepTreeID(type.getName(), type.getVersion()), type);
    }

    public void removeTypeTree(HepRepTypeTree typeTree) {
        typeTrees.remove(new DefaultHepRepTreeID(typeTree.getName(), typeTree.getVersion()));
    }

    public HepRepTypeTree getTypeTree(String name, String version) {
        return (HepRepTypeTree)typeTrees.get(new DefaultHepRepTreeID(name, version));
    }

    public Set/*<HepRepTypeTree>*/ getTypeTrees() {
        return typeTrees.valueSet();
    }

    public List/*<HepRepTypeTree>*/ getTypeTreeList() {
        return typeTrees.valueList();
    }

    public void addInstanceTree(HepRepInstanceTree instance) {
        instanceTrees.put(new DefaultHepRepTreeID(instance.getName(), instance.getVersion()), instance);
    }

    public void overlayInstanceTree(HepRepInstanceTree instanceTree) {
        // check to see if the instanceTree exists
        HepRepInstanceTree originalTree = getInstanceTreeTop(instanceTree.getName(), instanceTree.getVersion());
        if (originalTree == null) {
            throw new RuntimeException("HepRep.overlayInstanceTree cannot find instanceTree("+instanceTree.getName()+", "+instanceTree.getVersion()+")");
        }

        // call overlay
        originalTree.overlay(instanceTree);
    }

    public void removeInstanceTree(HepRepInstanceTree instanceTree) {
        instanceTrees.remove(new DefaultHepRepTreeID(instanceTree.getName(), instanceTree.getVersion()));
    }

    public HepRepInstanceTree getInstanceTreeTop(String name, String version) {
        return (HepRepInstanceTree)instanceTrees.get(new DefaultHepRepTreeID(name, version));
    }

    public HepRepInstanceTree getInstances(String name, String version,
                                           String[] typeNames) {
        // FIXME JHEPREP-5
        return getInstanceTreeTop(name, version);
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
        // FIXME JHEPREP-6
        return getInstanceTreeTop(name, version);
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

    /**
     * Dump the content of the HepRep for debugging
     */
    public void display() {
        System.out.println("HepRep");
        System.out.println("  Layers: "+getLayerOrder().size());
        for (Iterator i=getLayerOrder().iterator(); i.hasNext(); ) {
            String layer = (String)i.next();
            System.out.println("    "+layer);
        }
        System.out.println("  TypeTrees: "+typeTrees.size());
        for (Iterator i=new ValueSet(typeTrees).iterator(); i.hasNext(); ) {
            DefaultHepRepTypeTree tree = (DefaultHepRepTypeTree)i.next();
            tree.display("    ");
        }
        System.out.println("  InstanceTrees: "+instanceTrees.size());
        for (Iterator i=new ValueSet(instanceTrees).iterator(); i.hasNext(); ) {
            DefaultHepRepInstanceTree tree = (DefaultHepRepInstanceTree)i.next();
            tree.display("    ");
        }
    }
    
/* Disabled for FREEHEP-386
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
*/
}

