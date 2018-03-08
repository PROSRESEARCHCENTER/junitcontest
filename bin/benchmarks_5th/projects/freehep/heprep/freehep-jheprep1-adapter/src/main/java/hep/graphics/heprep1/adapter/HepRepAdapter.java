// Copyright 2004, FreeHEP.
package hep.graphics.heprep1.adapter;

import java.util.*;

import hep.graphics.heprep.HepRep;
import hep.graphics.heprep.HepRepAction;
import hep.graphics.heprep.HepRepAttributeAdapter;
import hep.graphics.heprep.HepRepInstance;
import hep.graphics.heprep.HepRepInstanceTree;
import hep.graphics.heprep.HepRepIterator;
import hep.graphics.heprep.HepRepSelectFilter;
import hep.graphics.heprep.HepRepTreeID;
import hep.graphics.heprep.HepRepType;
import hep.graphics.heprep.HepRepTypeTree;
import hep.graphics.heprep.ref.DefaultHepRepIterator;
import hep.graphics.heprep.util.HashMapList;
import hep.graphics.heprep.util.MapList;
import hep.graphics.heprep.xml.XMLHepRepReader;

/**
 *
 * @author Mark Donszelmann
 * @version $Id: HepRepAdapter.java 8584 2006-08-10 23:06:37Z duns $
 */
public class HepRepAdapter implements HepRep {

    private MapList/*<TreeID, HepRepInstanceTrees>*/ instanceTrees = new HashMapList();
    private MapList/*<TreeID, HepRepTypeTrees>*/ typeTrees = new HashMapList(); 
    private HepRepAdapterFactory factory;
    List/*<String>*/ layerOrder;

    /**
     * Wrapper for HepRep
     * @param heprep1 top-level heprep1
     */
    public HepRepAdapter(hep.graphics.heprep1.HepRep heprep1) {
        this.factory = HepRepAdapterFactory.getFactory();

        try {
            XMLHepRepReader.readDefaults();
        } catch(Exception e) {
            System.err.println("Warning: unable to read HepRep default attributes from XML");
        }
        
        // add trees
        HepRepTreeID id = factory.createHepRepTreeID("Types", "1.0");
        HepRepTypeTree typeTree = factory.createHepRepTypeTree(id);
        typeTrees.put(id, typeTree);
        HepRepType rootType = factory.createHepRepType(heprep1, null);
        typeTree.addType(rootType);

        HepRepInstanceTree instanceTree = factory.createHepRepInstanceTree("Instances", "1.0", id);
        instanceTrees.put(factory.createHepRepTreeID(instanceTree.getName(), instanceTree.getVersion()), instanceTree);
        HepRepInstance rootInstance = factory.createHepRepInstance(heprep1, null, rootType);
        instanceTree.addInstance(rootInstance);

        for (Enumeration e=heprep1.getTypes(); e.hasMoreElements(); ) {
            rootType.addType(factory.createHepRepType((hep.graphics.heprep1.HepRepType)e.nextElement(), rootType, rootInstance));
        }
    }

    public void addLayer(String layer) {
        throw new UnsupportedOperationException();
    }
    
    public List/*<String>*/ getLayerOrder() {
        if (layerOrder == null) {
            final Set/*<String>*/ set = new HashSet();
            // find all the layers
            HepRepIterator iterator = new DefaultHepRepIterator(getInstanceTreeList());
            iterator.addHepRepAttributeListener("Layer", new HepRepAttributeAdapter() {
                public void setAttribute(HepRepInstance instance, String key, String value, String lowerCaseValue, int showLabel) {
                    set.add(value);
                }
            });
            while (iterator.hasNext()) {
                iterator.nextInstance();
            }
            
            // Sort them into a list
            layerOrder = new ArrayList(set);
            Collections.sort(layerOrder, new NumericalComparator());
        }            
        return layerOrder;
    }
    
    public void addTypeTree(HepRepTypeTree typeTree) {
        throw new UnsupportedOperationException();
    }
    
    public void removeTypeTree(HepRepTypeTree typeTree) {
        throw new UnsupportedOperationException();
    }
    
    /**
     * @deprecated use getTypeTreeList()
     */
    public Set/*<HepRepTypeTree>*/ getTypeTrees() {
        return typeTrees.valueSet();
    }
    
    public List/*<HepRepTypeTree>*/ getTypeTreeList() {
        return typeTrees.valueList();
    }
    
    public HepRepTypeTree getTypeTree(String name, String version) {
        return (HepRepTypeTree)typeTrees.get(factory.createHepRepTreeID(name, version));
    }
    
    public void addInstanceTree(HepRepInstanceTree instanceTree) {
        throw new UnsupportedOperationException();
    }
    
    public void overlayInstanceTree(HepRepInstanceTree instanceTree) {
        throw new UnsupportedOperationException();
    }
    
    public void removeInstanceTree(HepRepInstanceTree instanceTree) {
        throw new UnsupportedOperationException();
    }
    
    /**
     * @deprecated use getInstanceTreeList()
     */
    public Set/*<HepRepInstanceTree>*/ getInstanceTrees() {
        return instanceTrees.valueSet();
    }
    
    public List/*<HepRepInstanceTree>*/ getInstanceTreeList() {
        return instanceTrees.valueList();
    }
    
    public HepRepInstanceTree getInstanceTreeTop(String name, String version) {
        return (HepRepInstanceTree)instanceTrees.get(factory.createHepRepTreeID(name, version));
    }
    
    public HepRepInstanceTree getInstances(String name, String version, String[] typeNames) {
        // FIXME FREEHEP-364
        return getInstanceTreeTop(name, version);
    }
    
    public HepRepInstanceTree getInstancesAfterAction(String name, String version, String[] typeNames, HepRepAction[] actions, boolean getPoints, boolean getDrawAtts, boolean getNonDrawAtts, String[] invertAtts) {
        // FIXME FREEHEP-365
        return getInstanceTreeTop(name, version);
    }
    
    public String checkForException() {
        return "Not Implemented";
    }
    
    public HepRep copy() throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }
    
    public HepRep copy(HepRepSelectFilter filter) throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }

}