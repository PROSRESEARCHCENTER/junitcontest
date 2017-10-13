// Copyright FreeHEP 2005.
package hep.graphics.heprep1.corba;

import hep.graphics.heprep1.HepRepAttribute;
import hep.graphics.heprep1.HepRepPoint;
import hep.graphics.heprep1.HepRepPrimitive;

/**
 *
 * @author M.Donszelmann
 *
 * @version $Id: HepRepPointAdapter.java 8584 2006-08-10 23:06:37Z duns $
 */

public class HepRepPointAdapter extends HepRepAttributeAdapter implements HepRepPoint {

    private hep.graphics.heprep1.corba.idl.HepRepPoint hepRepPoint;

    /**
     * Add a CORBA Wrapper
     * @param parent attribute parent
     * @param hepRepPoint corba point
     */
    public HepRepPointAdapter(HepRepAttribute parent, hep.graphics.heprep1.corba.idl.HepRepPoint hepRepPoint) {
        super(parent);
        this.hepRepPoint = hepRepPoint;  
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
    
    public HepRepPrimitive getPrimitive() {
        return (HepRepPrimitive)getParent();
    }
    
    public double[] getPoint() {
        double[] d = { hepRepPoint.x, hepRepPoint.y, hepRepPoint.z };
        return d;
    }

    protected hep.graphics.heprep1.corba.idl.HepRepAttValue[] getAttValuesFromNode() {
        return hepRepPoint.attValues;
    }
            
    protected hep.graphics.heprep1.corba.idl.HepRepAttDef[] getAttDefsFromNode() {
        return hepRepPoint.attDefs;
    }
}

