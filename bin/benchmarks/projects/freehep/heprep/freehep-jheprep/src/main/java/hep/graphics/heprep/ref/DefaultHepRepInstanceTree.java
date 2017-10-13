// Copyright 2000-2003, FreeHEP.
package hep.graphics.heprep.ref;

import hep.graphics.heprep.HepRepInstance;
import hep.graphics.heprep.HepRepInstanceTree;
import hep.graphics.heprep.HepRepSelectFilter;
import hep.graphics.heprep.HepRepTreeID;
import hep.graphics.heprep.HepRepTypeTree;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


/**
 *
 * @author M.Donszelmann
 *
 * @version $Id: DefaultHepRepInstanceTree.java 8584 2006-08-10 23:06:37Z duns $
 */

public class DefaultHepRepInstanceTree extends DefaultHepRepTreeID implements HepRepInstanceTree, Serializable {

    private HepRepTreeID typeTree;
    private Set/*<HepRepTreeID>*/ instanceTrees = new HashSet();
    private List/*<HepRepTreeID>*/ instanceTreeList = new ArrayList();
    private List/*<HepRepInstance>*/ instanceList;

    protected DefaultHepRepInstanceTree(String name, String version, HepRepTreeID typeTree) {
        super(name, version);
        this.typeTree = typeTree;
    }

    public void overlay(HepRepInstanceTree instanceTree) {
        // check that typeTree is equal
        if (!typeTree.equals(instanceTree.getTypeTree())) {
            throw new RuntimeException("HepRepInstanceTree cannot overlay; different typeTrees given.");
        }

        // check that related instance trees are equal in size
        if (getInstanceTreeList().size() != instanceTree.getInstanceTreeList().size()) {
            throw new RuntimeException("HepRepInstanceTree cannot overlay; number of related instanceTrees not equal.");
        }

        // check that #of instances is equal
        if (getInstances().size() != instanceTree.getInstances().size()) {
            throw new RuntimeException("HepRepInstanceTree cannot overlay; structure incompatible.");
        }

        // call overlay on each of them (order has to be the same)
        Iterator j=instanceTree.getInstances().iterator();
        for (Iterator i=getInstances().iterator(); i.hasNext(); ) {
            HepRepInstance instance = (HepRepInstance)i.next();
            instance.overlay((HepRepInstance)j.next());
        }
    }

    public HepRepInstanceTree copy(HepRepTypeTree typeTree) throws CloneNotSupportedException {
        return copy(typeTree, null);
    }

    public HepRepInstanceTree copy(HepRepTypeTree typeTree, HepRepSelectFilter filter) throws CloneNotSupportedException {
        DefaultHepRepInstanceTree copy = new DefaultHepRepInstanceTree(getName(), getVersion(), typeTree);
        // copy instances
        for (Iterator i=getInstances().iterator(); i.hasNext(); ) {
            HepRepInstance instance = (HepRepInstance)i.next();
            if ((filter == null) || (filter.select(instance))) {
                // auto addition due to parent
                instance.copy(typeTree, copy, filter);
            }
        }

        // copy referred instance tree names
        for (Iterator i=getInstanceTreeList().iterator(); i.hasNext(); ) {
            HepRepTreeID id = (HepRepTreeID)i.next();
            copy.addInstanceTree(new DefaultHepRepTreeID(id.getName(), id.getVersion(), id.getQualifier()));
        }

        return copy;
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
//        System.out.println("DHRInstanceTree: Serializing "+this);
        stream.defaultWriteObject();
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
//        System.out.println("DHRInstanceTree: Deserializing "+this);
        stream.defaultReadObject();
    }

    public void addInstance(HepRepInstance instance) {
        if (instanceList == null) instanceList = new ArrayList();
        instanceList.add(instance);
    }

    public void removeInstance(HepRepInstance instance) throws UnsupportedOperationException {
        if (instanceList != null) instanceList.remove(instance);
    }

    public List/*<HepRepInstance>*/ getInstances() {
        if (instanceList == null) return Collections.EMPTY_LIST;
        return instanceList;
    }


    public void addInstanceTree(HepRepTreeID instanceTree) {
        instanceTree.setQualifier("relatedinstancetree");
        instanceTrees.add(instanceTree);
        instanceTreeList.add(instanceTree);
    }

    public HepRepTreeID getTypeTree() {
        return typeTree;
    }

    public Set/*<HepRepTreeID>*/ getInstanceTrees() {
        return instanceTrees;
    }

    public List/*<HepRepTreeID>*/ getInstanceTreeList() {
        return instanceTreeList;
    }

   /**
     * @return a string representation of this HepRepInstanceTree
     */
    public String toString() {
        return "HepRepInstanceTree: "+getQualifier()+":"+getName()+":"+getVersion();
    }

    /**
     * Dumps InstanceTree for debugging purposes
     * @param indent indent string
     */
    public void display(String indent) {
        System.out.println(indent+toString());
        int n = 0;
        int p = 0;
        int v = 0;
        for (Iterator i=getInstances().iterator(); i.hasNext(); ) {
            DefaultHepRepInstance instance = (DefaultHepRepInstance)i.next();
            n += instance.getNoOfInstances();
            p += instance.getNoOfPoints();
            v += instance.getNoOfAttValues();
        }
        System.out.println(indent+"   #Instances: "+n);
        System.out.println(indent+"   #Points: "+p);
        System.out.println(indent+"   #Atts: "+v);
    }
    
/* Disabled for FREEHEP-386
    public boolean equals(Object o) {
        if (!super.equals(o)) return false;
        if (o instanceof HepRepInstanceTree) {
            HepRepInstanceTree ref = (HepRepInstanceTree)o;
            if (!ref.getTypeTree().getName().equals(getTypeTree().getName())) return false;
            if (!ref.getTypeTree().getVersion().equals(getTypeTree().getVersion())) return false;
            if (!ref.getInstanceTreeList().equals(getInstanceTreeList())) return false;
            if (!ref.getInstances().equals(getInstances())) return false;
            
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
*/
}

