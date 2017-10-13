// Copyright 2000-2003, FreeHEP.
package hep.graphics.heprep.corbavalue;

import hep.graphics.heprep.HepRepFactory;
import hep.graphics.heprep.HepRepInstance;
import hep.graphics.heprep.HepRepInstanceTree;
import hep.graphics.heprep.HepRepSelectFilter;
import hep.graphics.heprep.HepRepTreeID;
import hep.graphics.heprep.HepRepTypeTree;
import hep.graphics.heprep.ref.DefaultHepRepFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Mark Donszelmann
 *
 * @version $Id: HepRepInstanceTreeAdapter.java 8584 2006-08-10 23:06:37Z duns $
 */

public class HepRepInstanceTreeAdapter extends HepRepTreeIDAdapter implements HepRepInstanceTree {

    private hep.graphics.heprep.corbavalue.idl.HepRepInstanceTree hepRepInstanceTree;
    private transient List/*<HepRepInstance>*/ instances;
    private transient Set/*<HepRepTreeID>*/ instanceTrees;
    private transient List/*<HepRepTreeID>*/ instanceTreeList;

    /**
     * Create a CORBA wrapper for an Instance Tree
     * @param hepRepInstanceTree corba instance tree
     */
    public HepRepInstanceTreeAdapter(hep.graphics.heprep.corbavalue.idl.HepRepInstanceTree hepRepInstanceTree) {
        super(hepRepInstanceTree);
        this.hepRepInstanceTree = hepRepInstanceTree;
    }

    public void overlay(HepRepInstanceTree instanceTree) {
        throw new RuntimeException("HepRepInstanceTreeAdapter.overlay is not implemented.");
    }

    public HepRepInstanceTree copy(HepRepTypeTree typeTree) throws CloneNotSupportedException {
        return copy(typeTree, null);
    }

    public HepRepInstanceTree copy(HepRepTypeTree typeTree, HepRepSelectFilter filter) throws CloneNotSupportedException {
        HepRepFactory factory = new DefaultHepRepFactory();
        HepRepInstanceTree copy = factory.createHepRepInstanceTree(getName(), getVersion(), getTypeTree());
        // copy instances
        for (Iterator i=getInstances().iterator(); i.hasNext(); ) {
            HepRepInstance instance = (HepRepInstance)i.next();
            if ((filter == null) || (filter.select(instance))) {
                copy.addInstance(instance.copy(typeTree, copy, filter));
            }
        }

        // copy referred instance tree names
        for (Iterator i=getInstanceTreeList().iterator(); i.hasNext(); ) {
            HepRepTreeID id = (HepRepTreeID)i.next();
            copy.addInstanceTree(factory.createHepRepTreeID(id.getName(), id.getVersion(), id.getQualifier()));
        }

        return copy;
    }

    public List/*<HepRepInstance>*/ getInstances() {
        if (instances == null) {
            instances = new LinkedList();
            int n = hepRepInstanceTree.instances.length;
            for (int i=0; i<n; i++) {
                instances.add(new HepRepInstanceAdapter(hepRepInstanceTree.instances[i], null));
            }
        }
        return instances;
    }

    public void addInstance(HepRepInstance instance) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("HepRepInstanceTreeAdapter.addInstance is not implemented.");
    }

    public void addInstanceTree(HepRepTreeID instanceTree) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("HepRepInstanceTreeAdapter.addInstanceTree is not implemented.");
    }

    public void removeInstance(HepRepInstance instance) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("HepRepInstanceAdapter.removeInstance is not implemented.");
    }

    public HepRepTreeID getTypeTree() {
        return new HepRepTreeIDAdapter(hepRepInstanceTree.typeTreeID);
    }

    public Set/*<HepRepTreeID>*/ getInstanceTrees() {
        if (instanceTrees == null) {
            int n = hepRepInstanceTree.instanceTreeIDs.length;
            instanceTrees = new HashSet(n);
            for (int i=0; i<n; i++) {
                instanceTrees.add(new HepRepTreeIDAdapter(hepRepInstanceTree.instanceTreeIDs[i]));
            }
        }
        return instanceTrees;
    }

    public List/*<HepRepTreeID>*/ getInstanceTreeList() {
        if (instanceTreeList == null) {
            int n = hepRepInstanceTree.instanceTreeIDs.length;
            instanceTreeList = new ArrayList(n);
            for (int i=0; i<n; i++) {
                instanceTreeList.add(new HepRepTreeIDAdapter(hepRepInstanceTree.instanceTreeIDs[i]));
            }
        }
        return instanceTreeList;
    }

    public String toString() {
        return "[HepRepInstanceTree (corba):"+getQualifier()+":"+getName()+":"+getVersion()+"]";
    }
   
    public boolean equals(Object o) {
        if (!super.equals(o)) return false;
        if (o instanceof HepRepInstanceTree) {
            HepRepInstanceTree ref = (HepRepInstanceTree)o;
            if (!ref.getTypeTree().getName().equals(getTypeTree().getName())) return false;
            if (!ref.getTypeTree().getVersion().equals(getTypeTree().getVersion())) return false;
            if (!ref.getInstances().equals(getInstances())) return false;
            if (!ref.getInstanceTreeList().equals(getInstanceTreeList())) return false;
            
            return true;
        }
        return false;
    }
    
    public int hashCode() {
        long code = getTypeTree().hashCode();
        code += getInstances().hashCode();
        code += getInstanceTreeList().hashCode();
        return (int)code;
    }
}

