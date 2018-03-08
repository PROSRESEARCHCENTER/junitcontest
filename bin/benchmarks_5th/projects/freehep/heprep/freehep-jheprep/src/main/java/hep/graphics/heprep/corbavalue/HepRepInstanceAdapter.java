// Copyright 2000-2003, FreeHEP.
package hep.graphics.heprep.corbavalue;

import hep.graphics.heprep.HepRepAttValue;
import hep.graphics.heprep.HepRepInstance;
import hep.graphics.heprep.HepRepInstanceTree;
import hep.graphics.heprep.HepRepPoint;
import hep.graphics.heprep.HepRepSelectFilter;
import hep.graphics.heprep.HepRepType;
import hep.graphics.heprep.HepRepTypeTree;
import hep.graphics.heprep.ref.DefaultHepRepFactory;

import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Mark Donszelmann
 *
 * @version $Id: HepRepInstanceAdapter.java 8584 2006-08-10 23:06:37Z duns $
 */

public class HepRepInstanceAdapter extends HepRepAttributeAdapter implements HepRepInstance {

    private HepRepInstance parent;
    private hep.graphics.heprep.corbavalue.idl.HepRepInstance hepRepInstance;
    private transient Object userObject;
    private transient LinkedList points;
    private transient LinkedList instances;

    private transient boolean valid;                  // is cache info up to date
    private transient String layer;                   // cached info
    private transient boolean hasFrame;               // cached info

    /**
     * Create a CORBA wrapper for an Instance
     * @param hepRepInstance corba instance
     * @param parent parent instance
     */
    public HepRepInstanceAdapter(hep.graphics.heprep.corbavalue.idl.HepRepInstance hepRepInstance, HepRepInstance parent) {
        super(hepRepInstance);
        this.parent = parent;
        this.hepRepInstance = hepRepInstance;
    }

    public HepRepInstance getSuperInstance() {
        return parent;
    }

    public void overlay(HepRepInstance instance) {
        throw new RuntimeException("HepRepInstanceAdapter.overlay is not implemented.");
    }

    public HepRepInstance copy(HepRepTypeTree typeTree, HepRepInstance parent) throws CloneNotSupportedException {
        return copy(typeTree, parent, null);
    }

    public HepRepInstance copy(HepRepTypeTree typeTree, HepRepInstanceTree parent) throws CloneNotSupportedException {
        return copy(typeTree, parent, null);
    }

    public HepRepInstance copy(HepRepTypeTree typeTree, HepRepInstance parent, HepRepSelectFilter filter) throws CloneNotSupportedException {
        HepRepType type = typeTree.getType(getType().getFullName());
        HepRepInstance copy = new DefaultHepRepFactory().createHepRepInstance(parent, type);
        return copy(typeTree, copy, filter);
    }

    public HepRepInstance copy(HepRepTypeTree typeTree, HepRepInstanceTree parent, HepRepSelectFilter filter) throws CloneNotSupportedException {
        HepRepType type = typeTree.getType(getType().getFullName());
        HepRepInstance copy = new DefaultHepRepFactory().createHepRepInstance(parent, type);
        return copy(typeTree, copy, filter);
    }

/*    private HepRepInstance copy(HepRepTypeTree typeTree, DefaultHepRepInstance copy, HepRepSelectFilter filter) throws CloneNotSupportedException {
        HepRepUtil.copyAttributes(this, copy);

    	// copy points
        for (Iterator i=getPoints().iterator(); i.hasNext(); ) {
            HepRepPoint point = (HepRepPoint)i.next();
            copy.addPoint(point.copy(copy));
        }

        // copy sub-instances
        for (Iterator i=getInstances().iterator(); i.hasNext(); ) {
            HepRepInstance instance = (HepRepInstance)i.next();
            if ((filter == null) || (filter.select(instance))) {
                copy.addInstance(instance.copy(typeTree, copy, filter));
            }
        }

        return copy;
    }
*/
    public void setUserObject(Object object) {
        userObject = object;
    }

    public Object getUserObject() {
        return userObject;
    }

     public HepRepType getType() {
        return new HepRepTypeAdapter(hepRepInstance.typeName);
    }

    public List/*<HepRepInstance>*/ getInstances() {
        if (instances == null) {
            instances = new LinkedList();
            int n = hepRepInstance.instances.length;
            for (int i=0; i < n; i++) {
                instances.add(new HepRepInstanceAdapter(hepRepInstance.instances[i], this));
            }
        }
        return instances;
    }

    public List/*<HepRepPoint>*/ getPoints() {
        if (points == null) {
            points = new LinkedList();
            int n = hepRepInstance.points.length;
            for (int i=0; i < n; i++) {
                points.add(new HepRepPointAdapter(hepRepInstance.points[i], this));
            }
        }
        return points;
    }

    public int getPoints(double[][] xyz) {
        return -1;
    }

    public void addPoint(HepRepPoint point) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("HepRepInstanceAdapter.addPoint is not implemented.");
    }

    public void addInstance(HepRepInstance instance) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("HepRepInstanceAdapter.addInstance is not implemented.");
    }

    public void removeInstance(HepRepInstance instance) throws UnsupportedOperationException {
        throw new UnsupportedOperationException("HepRepInstanceAdapter.removeInstance is not implemented.");
    }

    public HepRepAttValue getAttValue(String name) {
        String lowerCaseName = name.toLowerCase();
        HepRepAttValue attValue = getAttValueFromNode(lowerCaseName);
        return (attValue != null) ? attValue : getType().getAttValue(lowerCaseName);
    }
    
    public boolean equals(Object o) {
        if (!super.equals(o)) return false;
        if (o instanceof HepRepInstance) {
            HepRepInstance ref = (HepRepInstance)o;
            if (!ref.getType().equals(getType())) return false;
            if (!ref.getInstances().equals(getInstances())) return false;
            if (!ref.getPoints().equals(getPoints())) return false;
            
            return true;
        }
        return false;
    }
    
    public int hashCode() {
        long code = getType().hashCode();
        code += getInstances().hashCode();
        code += getPoints().hashCode();
        return (int)code;
    }
    
    //
    // public methods not in HepRepInstance interface
    //
    
    /**
     * Verifies if the cached values are valid 
     * @return true if the values are valid
     */
    public boolean isValid() {
        return valid;
    }

    /**
     * Validates the cached values
     */
    public void validate() {
        if (valid) return;
        
        layer = getAttValue("layer").getString();
        hasFrame = getAttValue("hasframe").getBoolean();

        valid = true;
    }

    public String getLayer() {
        validate();
        return layer;
    }    

    public boolean hasFrame() {
        validate();
        return hasFrame;
    }
}

