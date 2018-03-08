// Copyright 2004, FreeHEP.
package hep.graphics.heprep1.adapter;

import hep.graphics.heprep.HepRepAttValue;
import hep.graphics.heprep.HepRepInstance;
import hep.graphics.heprep.HepRepPoint;
import hep.graphics.heprep.util.HepRepMath;

/**
 *
 * @author Mark Donszelmann
 * @version $Id: HepRepPointAdapter.java 8584 2006-08-10 23:06:37Z duns $
 */
public class HepRepPointAdapter extends HepRepAttributeAdapter implements HepRepPoint {

    private hep.graphics.heprep1.HepRepPoint point;
    private HepRepInstance parent;

    /**
     * Wrapper for HepRep1 Point
     * @param point heprep1 point
     * @param parent heprep2 parent instance
     */
    public HepRepPointAdapter(hep.graphics.heprep1.HepRepPoint point,
                              HepRepInstance parent) {
        super(point);
        this.point = point;
        this.parent = parent;
    }

    public HepRepInstance getInstance() {
        return parent;
    }

    public HepRepPoint copy(HepRepInstance parent) throws CloneNotSupportedException {
        throw new CloneNotSupportedException();
    }

    public double getX() {
        return point.getX();
    }

    public double getY() {
        return point.getY();
    }

    public double getZ() {
        return point.getZ();
    } 
       
    public double[] getXYZ(double[] xyz) {
        if (xyz == null) xyz = new double[3];
        xyz[0] = getX();
        xyz[1] = getY();
        xyz[2] = getZ();
        return xyz;
    }

    /**
     * look for attribute on this node, otherwise delegate to instance
     */
    public HepRepAttValue getAttValue(String lowerCaseName) {
       HepRepAttValue value = getAttValueFromNode(lowerCaseName);
       return (value != null) ? value : parent.getAttValue(lowerCaseName);
    }

    // Delegated to HepRepMath
    public double getRho() {
        return HepRepMath.getRho(getX(), getY());
    }
    public double getPhi() {
        return HepRepMath.getPhi(getX(), getY());
    }
    public double getTheta() {
        return HepRepMath.getTheta(getX(), getY(), getX());
    }
    public double getR() {
        return HepRepMath.getR(getX(), getY(), getX());
    }
    public double getEta() {
        return HepRepMath.getEta(getX(), getY(), getX());
    }
    public double getX(double xVertex, double yVertex, double zVertex) {
        return HepRepMath.getX(getX(), xVertex);
    }
    public double getY(double xVertex, double yVertex, double zVertex) {
        return HepRepMath.getY(getY(), yVertex);
    }
    public double getZ(double xVertex, double yVertex, double zVertex) {
        return HepRepMath.getZ(getZ(), zVertex);
    }
    public double getRho(double xVertex, double yVertex, double zVertex) {
        return HepRepMath.getRho(getX(), getY(), xVertex, yVertex);
    }
    public double getPhi(double xVertex, double yVertex, double zVertex) {
        return HepRepMath.getPhi(getX(), getY(), xVertex, yVertex);
    }
    public double getTheta(double xVertex, double yVertex, double zVertex) {
        return HepRepMath.getTheta(getX(), getY(), getZ(), xVertex, yVertex, zVertex);
    }
    public double getR(double xVertex, double yVertex, double zVertex) {
        return HepRepMath.getR(getX(), getY(), getZ(), xVertex, yVertex, zVertex);
    }
    public double getEta(double xVertex, double yVertex, double zVertex) {
        return HepRepMath.getEta(getX(), getY(), getZ(), xVertex, yVertex, zVertex);
    }
}
