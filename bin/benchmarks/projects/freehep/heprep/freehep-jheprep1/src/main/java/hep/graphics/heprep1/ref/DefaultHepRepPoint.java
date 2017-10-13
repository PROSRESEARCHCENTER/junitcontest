// Copyright 2000-2004, FreeHEP.

package hep.graphics.heprep1.ref;

import java.io.*;

import hep.graphics.heprep1.*;

/**
 *
 * @author M.Donszelmann
 *
 * @version $Id: DefaultHepRepPoint.java 8584 2006-08-10 23:06:37Z duns $
 */

public class DefaultHepRepPoint extends DefaultHepRepAttribute implements HepRepPoint, Serializable {

    double x, y, z;

    /**
     * Create Point
     * @param parent point parent
     * @param x x
     * @param y y
     * @param z z
     */
    public DefaultHepRepPoint(DefaultHepRepAttribute parent, double x, double y, double z) {
        super(parent);
        if (parent!=null)
            parent.add(this);
        this.x = x;
        this.y = y;
        this.z = z;
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

    public double[] getPoint() {
        double[] d = { x, y, z };
        return d;
    }

    public HepRepPrimitive getPrimitive() {
        return (HepRepPrimitive)getParent();
    }

    public boolean equals(Object o) {
        if (o instanceof DefaultHepRepPoint) {
            DefaultHepRepPoint ref = (DefaultHepRepPoint)o;
            return (ref.getX() == getX()) && (ref.getY() == getY()) && (ref.getZ() == getZ());
        }
        return super.equals(o);
    }

}

