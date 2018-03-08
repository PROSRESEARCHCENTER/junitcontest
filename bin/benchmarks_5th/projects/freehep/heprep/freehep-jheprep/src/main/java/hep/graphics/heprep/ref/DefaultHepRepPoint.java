// Copyright 2000-2003, FreeHEP.
package hep.graphics.heprep.ref;

import hep.graphics.heprep.HepRepAttValue;
import hep.graphics.heprep.HepRepInstance;
import hep.graphics.heprep.HepRepPoint;
import hep.graphics.heprep.util.HepRepMath;
import hep.graphics.heprep.util.HepRepUtil;

import java.io.Serializable;

/**
 *
 * @author M.Donszelmann
 *
 * @version $Id: DefaultHepRepPoint.java 8584 2006-08-10 23:06:37Z duns $
 */

public class DefaultHepRepPoint extends DefaultHepRepAttribute implements HepRepPoint, Serializable {
 
    protected double x, y, z;
    protected HepRepInstance instance;

    protected DefaultHepRepPoint(HepRepInstance instance, double x, double y, double z) {
        super();

        if (instance == null) throw new RuntimeException("HepRepPoints cannot be created without a HepRepInstance.");
        this.instance = instance;

        this.x = x;
        this.y = y;
        this.z = z;

        // auto add to parent
        instance.addPoint(this);
    }

    public HepRepInstance getInstance() {
        return instance;
    }
    
/* Disabled for FREEHEP-386
    public boolean equals(Object o) {
        if (!super.equals(o)) return false;
        if (o instanceof HepRepPoint) {
            HepRepPoint ref = (HepRepPoint)o;
            return true;
// FREEHEP-386
//            return (ref.getX() == getX()) && (ref.getY() == getY()) && (ref.getZ() == getZ());
        }
        return false;
    }

    public int hashCode() {
        long r =  Double.doubleToLongBits(getX()) + 
                  Double.doubleToLongBits(getY()) + 
                  Double.doubleToLongBits(getZ());
        return (int)r;
    }
*/    
    public String toString() {
        return getClass()+": ("+x+", "+y+", "+z+")";
    }
    
    /**
     * look for attribute on this node, otherwise delegate to instance
     */
    public HepRepAttValue getAttValue(String lowerCaseName) {
       HepRepAttValue value = getAttValueFromNode(lowerCaseName);
       return (value != null) ? value : instance.getAttValue(lowerCaseName);
    }

    public HepRepPoint copy(HepRepInstance parent) throws CloneNotSupportedException {
        DefaultHepRepPoint copy = new DefaultHepRepPoint(parent, getX(), getY(), getZ());
        HepRepUtil.copyAttributes(this, copy);
        return copy;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public double[] getXYZ(double[] xyz) {
        if (xyz == null) xyz = new double[3];
        xyz[0] = x;
        xyz[1] = y;
        xyz[2] = z;
        return xyz;
    }

    // Delegated to HepRepMath
    public double getRho() {
        return HepRepMath.getRho(x, y);
    }
    public double getPhi() {
        return HepRepMath.getPhi(x, y);
    }
    public double getTheta() {
        return HepRepMath.getTheta(x, y, z);
    }
    public double getR() {
        return HepRepMath.getR(x, y, z);
    }
    public double getEta() {
        return HepRepMath.getEta(x, y, z);
    }
    public double getX(double xVertex, double yVertex, double zVertex) {
        return HepRepMath.getX(x, xVertex);
    }
    public double getY(double xVertex, double yVertex, double zVertex) {
        return HepRepMath.getY(y, yVertex);
    }
    public double getZ(double xVertex, double yVertex, double zVertex) {
        return HepRepMath.getZ(z, zVertex);
    }
    public double getRho(double xVertex, double yVertex, double zVertex) {
        return HepRepMath.getRho(x, y, xVertex, yVertex);
    }
    public double getPhi(double xVertex, double yVertex, double zVertex) {
        return HepRepMath.getPhi(x, y, xVertex, yVertex);
    }
    public double getTheta(double xVertex, double yVertex, double zVertex) {
        return HepRepMath.getTheta(x, y, z, xVertex, yVertex, zVertex);
    }
    public double getR(double xVertex, double yVertex, double zVertex) {
        return HepRepMath.getR(x, y, z, xVertex, yVertex, zVertex);
    }
    public double getEta(double xVertex, double yVertex, double zVertex) {
        return HepRepMath.getEta(x, y, z, xVertex, yVertex, zVertex);
    }
}

