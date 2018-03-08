// Copyright 2000-2003, FreeHEP.
package hep.graphics.heprep.corbavalue;

import hep.graphics.heprep.*;
import hep.graphics.heprep.ref.*;
import hep.graphics.heprep.util.*;

/**
 *
 * @author Mark Donszelmann
 *
 * @version $Id: HepRepPointAdapter.java 8584 2006-08-10 23:06:37Z duns $
 */

public class HepRepPointAdapter extends HepRepAttributeAdapter implements HepRepPoint {

    private hep.graphics.heprep.corbavalue.idl.HepRepPoint hepRepPoint;
    private HepRepInstance instance;

    /**
     * Create a CORBA wrapper for a Point
     * @param hepRepPoint corba point
     * @param instance parent instance
     */
    public HepRepPointAdapter(hep.graphics.heprep.corbavalue.idl.HepRepPoint hepRepPoint, HepRepInstance instance) {
        super(hepRepPoint);
        this.hepRepPoint = hepRepPoint;
        this.instance = instance;
    }

    public HepRepPoint copy(HepRepInstance parent) throws CloneNotSupportedException {
        HepRepPoint copy = new DefaultHepRepFactory().createHepRepPoint(parent, getX(), getY(), getZ());
        HepRepUtil.copyAttributes(this, copy);
        return copy;
    }

    public HepRepInstance getInstance() {
        return instance;
    }

    public HepRepAttValue getAttValue(String lowerCaseName) {
        HepRepAttValue attValue = getAttValueFromNode(lowerCaseName);
        return (attValue != null) ? attValue : getInstance().getAttValue(lowerCaseName);
    }

    public double getX() {
        return hepRepPoint.x;
    }

    public double getY() {
        return hepRepPoint.y;
    }

    public double getZ() {
        return hepRepPoint.z;
    }

    public double[] getXYZ(double[] xyz) {
        if (xyz == null) xyz = new double[3];
        xyz[0] = hepRepPoint.x;
        xyz[1] = hepRepPoint.y;
        xyz[2] = hepRepPoint.z;
        return xyz;
    }

    public boolean equals(Object o) {
        if (!super.equals(o)) return false;
        if (o instanceof HepRepPoint) {
//            HepRepPoint ref = (HepRepPoint)o;
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
    
    // Delegated to HepRepMath
    public double getRho() {
        return HepRepMath.getRho(hepRepPoint.x, hepRepPoint.y);
    }
    public double getPhi() {
        return HepRepMath.getPhi(hepRepPoint.x, hepRepPoint.y);
    }
    public double getTheta() {
        return HepRepMath.getTheta(hepRepPoint.x, hepRepPoint.y, hepRepPoint.z);
    }
    public double getR() {
        return HepRepMath.getR(hepRepPoint.x, hepRepPoint.y, hepRepPoint.z);
    }
    public double getEta() {
        return HepRepMath.getEta(hepRepPoint.x, hepRepPoint.y, hepRepPoint.z);
    }
    public double getX(double xVertex, double yVertex, double zVertex) {
        return HepRepMath.getX(hepRepPoint.x, xVertex);
    }
    public double getY(double xVertex, double yVertex, double zVertex) {
        return HepRepMath.getY(hepRepPoint.y, yVertex);
    }
    public double getZ(double xVertex, double yVertex, double zVertex) {
        return HepRepMath.getZ(hepRepPoint.z, zVertex);
    }
    public double getRho(double xVertex, double yVertex, double zVertex) {
        return HepRepMath.getRho(hepRepPoint.x, hepRepPoint.y, xVertex, yVertex);
    }
    public double getPhi(double xVertex, double yVertex, double zVertex) {
        return HepRepMath.getPhi(hepRepPoint.x, hepRepPoint.y, xVertex, yVertex);
    }
    public double getTheta(double xVertex, double yVertex, double zVertex) {
        return HepRepMath.getTheta(hepRepPoint.x, hepRepPoint.y, hepRepPoint.z, xVertex, yVertex, zVertex);
    }
    public double getR(double xVertex, double yVertex, double zVertex) {
        return HepRepMath.getR(hepRepPoint.x, hepRepPoint.y, hepRepPoint.z, xVertex, yVertex, zVertex);
    }
    public double getEta(double xVertex, double yVertex, double zVertex) {
        return HepRepMath.getEta(hepRepPoint.x, hepRepPoint.y, hepRepPoint.z, xVertex, yVertex, zVertex);
    }
}

