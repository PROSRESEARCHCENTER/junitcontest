// Copyright 2000-2005, FreeHEP.
package hep.graphics.heprep.ref;

import java.io.*;
import java.util.*;

import hep.graphics.heprep.*;
import hep.graphics.heprep.util.*;

/**
 *
 * @author M.Donszelmann
 *
 * @version $Id: DefaultHepRepInstance.java 8584 2006-08-10 23:06:37Z duns $
 */

public class DefaultHepRepInstance extends DefaultHepRepAttribute implements HepRepInstance, Serializable {

    private HepRepType type = null;

    private HepRepInstance parent;
    private List/*<HepRepPoint>*/ pointList;
    private double[][] points;
    private List/*<HepRepInstance>*/ instanceList;
    private transient Object userObject;              // to cache information, not persistent
    
    private transient boolean valid;                  // is cache info up to date
    private transient String layer;                   // cached info
    private transient boolean hasFrame;               // cached info

    protected DefaultHepRepInstance(HepRepInstance parent, HepRepType type) {
        super();
        if (type == null) throw new RuntimeException("HepRepInstance cannot be created without a HepRepType.");
        this.type = type;
        this.parent = parent;
        this.valid = false;
        this.layer = null;
        this.hasFrame = false;
        if (parent != null) parent.addInstance(this);
    }

    protected DefaultHepRepInstance(HepRepInstanceTree parent, HepRepType type) {
        this((HepRepInstance)null, type);
        this.valid = false;
        this.layer = null;
        this.hasFrame = false;
        parent.addInstance(this);
    }

//    private DefaultHepRepInstance(HepRepType type) {
//        this((HepRepInstance)null, type);
//    }

    public HepRepInstance getSuperInstance() {
        return parent;
    }

    public void overlay(HepRepInstance instance) {
        // check #of subinstances are equal
        if (getInstances().size() != instance.getInstances().size()) {
            throw new RuntimeException("HepRepInstance cannot overlay; not a compatible structure in terms of sub-instances.");
        }

        // check #of points are equal if greater than 0
        if ((getPoints().size() > 0) && (getPoints().size() != instance.getPoints().size())) {
            throw new RuntimeException("HepRepInstance cannot overlay; not a compatible structure in terms of points.");
        }

        // add points of instance if #of points is 0
        for (Iterator i=instance.getPoints().iterator(); i.hasNext(); ) {
            HepRepPoint point = (HepRepPoint)i.next();
            addPoint(point);
        }

        // merge all attValues, where instance overrides the values of "this".
        for (Iterator i=instance.getAttValuesFromNode().iterator(); i.hasNext(); ) {
            HepRepAttValue value = (HepRepAttValue)i.next();
            addAttValue(value);
        }
        
        optimize();
        validate();
    }

    private void writeObject(ObjectOutputStream stream) throws IOException {
//        System.out.println("DHRInstance: Serializing "+this);
        stream.defaultWriteObject();
    }

    private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
//        System.out.println("DHRInstance: Deserializing "+this);
        stream.defaultReadObject();
    }

    public HepRepInstance copy(HepRepTypeTree typeTree, HepRepInstance parent)
            throws CloneNotSupportedException {
        return copy(typeTree, parent, null);
    }

    public HepRepInstance copy(HepRepTypeTree typeTree, HepRepInstanceTree parent)
            throws CloneNotSupportedException {
        return copy(typeTree, parent, null);
    }

    public HepRepInstance copy(HepRepTypeTree typeTree, HepRepInstance parent, HepRepSelectFilter filter)
            throws CloneNotSupportedException {
        HepRepType type = typeTree.getType(getType().getFullName());
        DefaultHepRepInstance instanceCopy = new DefaultHepRepInstance(parent, type);
        return copy(typeTree, instanceCopy, filter);
    }

    public HepRepInstance copy(HepRepTypeTree typeTree, HepRepInstanceTree parent, HepRepSelectFilter filter)
            throws CloneNotSupportedException {
        HepRepType type = typeTree.getType(getType().getFullName());
        DefaultHepRepInstance instanceCopy = new DefaultHepRepInstance(parent, type);
        return copy(typeTree, instanceCopy, filter);
    }

    private HepRepInstance copy(HepRepTypeTree typeTree, DefaultHepRepInstance instanceCopy, HepRepSelectFilter filter)
            throws CloneNotSupportedException {
        HepRepUtil.copyAttributes(this, instanceCopy);

	    // copy points
        for (Iterator i=getPoints().iterator(); i.hasNext(); ) {
            HepRepPoint point = (HepRepPoint)i.next();
            // auto addition due to parent
            point.copy(instanceCopy);
        }

        // copy sub-instances
        for (Iterator i=getInstances().iterator(); i.hasNext(); ) {
            HepRepInstance instance = (HepRepInstance)i.next();
            if ((filter == null) || (filter.select(instance))) {
                // auto addition due to parent
                instance.copy(typeTree, instanceCopy, filter);
            }
        }

        // FIXME may not be the smartest way to handle this.
        instanceCopy.optimize();
        instanceCopy.validate();

        return instanceCopy;
    }

    public void setUserObject(Object object) {
        userObject = object;
    }

    public Object getUserObject() {
        return userObject;
    }

    public HepRepType getType() {
        return type;
    }

    public void addPoint(HepRepPoint point) throws UnsupportedOperationException {
        if (pointList == null) pointList = new ArrayList();
        pointList.add(point);
    }

    public List/*<HepRepPoint>*/ getPoints() {
        if ((pointList == null) && (points == null)) return Collections.EMPTY_LIST;
        if (pointList != null) return pointList;
        
        // convert the array to a list of HepRepPoints
        List list = new ArrayList(points.length);
        for (int i=0; i<points.length; i++) {
            list.add(new DefaultHepRepPoint(this, points[0][i], points[1][i], points[2][i]));
        }
        return list;
    }

    public int getPoints(double[][] xyz) {
        if ((pointList == null) && (points == null)) return 0;

        if (points == null) optimize();
        if (points == null) return -1;
        
        int nPoints = points[0].length;
        int xPoints = xyz[0].length;
        
        // skip points in case the array is too small
        int inc = (xPoints < nPoints) ? nPoints / xPoints : 0;
        inc++;
        
        int i=0;
        int j=0;
        while (j<nPoints) {
            xyz[0][i] = points[0][j]; 
            xyz[1][i] = points[1][j]; 
            xyz[2][i] = points[2][j];
            i++; 
            j+=inc;
        }
// FIXME !!! this should be ok.
/*    
        System.arraycopy(points[0], 0, xyz[0], 0, points[0].length);
        System.arraycopy(points[1], 0, xyz[1], 0, points[1].length);
        System.arraycopy(points[2], 0, xyz[2], 0, points[2].length);
*/    
        return (inc > 1) ? -nPoints : nPoints;
    }
    
    /**
     * Optimizes the internal storage of the instance. 
     * If no HepRepPoints contain attributes the points are converted to an array.
     */
    public void optimize() {
        
        //
        // Convert List<HepRepPoint> to double[3][n]
        //
        if ((points != null) || (pointList == null)) return;
        
        points = new double[3][pointList.size()];
        
        int j = 0;
        for (Iterator i = pointList.iterator(); i.hasNext(); ) {
            HepRepPoint p = (HepRepPoint)i.next();
            
            // check if any points have attributes defined
            if (!p.getAttValuesFromNode().isEmpty()) {
//                System.out.println("Could not optimize data, points contain attributes");
                points = null;
                return;
            }
            points[0][j] = p.getX();
            points[1][j] = p.getY();
            points[2][j] = p.getZ();
            j++;
        }
// FIXME later
//        pointList = null;
    }

    public void addInstance(HepRepInstance instance) throws UnsupportedOperationException {
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

    /**
     * search for attribute on node, then search on type
     */
    public HepRepAttValue getAttValue(String name) {
        String lowerCaseName = name.toLowerCase();
        HepRepAttValue value = getAttValueFromNode(lowerCaseName);
        return (value != null) ? value : type.getAttValue(lowerCaseName);
    }

    public String toString() {
        return "DefaultHepRepInstance: "+getType();
    }

    int getNoOfInstances() {
        int n = 0;
        for (Iterator i=getInstances().iterator(); i.hasNext(); ) {
            DefaultHepRepInstance instance = (DefaultHepRepInstance)i.next();
            n++;
            n += instance.getNoOfInstances();
        }
        return n;
    }

    int getNoOfPoints() {
        int p = getPoints().size();

        for (Iterator i=getInstances().iterator(); i.hasNext(); ) {
            DefaultHepRepInstance instance = (DefaultHepRepInstance)i.next();
            p += instance.getNoOfPoints();
        }
        return p;
    }

    int getNoOfAttValues() {
        int v = getAttValuesFromNode().size();
        
        for (Iterator i=getInstances().iterator(); i.hasNext(); ) {
            DefaultHepRepInstance instance = (DefaultHepRepInstance)i.next();
            v += instance.getNoOfAttValues();
        }
        return v;        
    }

/* Disabled for FREEHEP-386
    public boolean equals(Object o) {
        if (!super.equals(o)) return false;
        if (o instanceof HepRepInstance) {
            HepRepInstance ref = (HepRepInstance)o;
            if (!ref.getType().equals(getType())) return false;
            if (!ref.getPoints().equals(getPoints())) return false;
            if (!ref.getInstances().equals(getInstances())) return false;
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
*/ 
    
    //
    // public methods not in HepRepInstance interface
    //
    /**
     * Verifies if the cached values are valid.
     * @return true if cache is valid
     */
    public boolean isValid() {
        return valid;
    }

    /**
     * Validates the cache.
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

