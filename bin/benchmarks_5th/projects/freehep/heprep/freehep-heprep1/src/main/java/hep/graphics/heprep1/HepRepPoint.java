// Copyright FreeHEP 2000-2005.
package hep.graphics.heprep1;


/**
 *
 * @author M.Donszelmann
 *
 * @version $Id: HepRepPoint.java 8584 2006-08-10 23:06:37Z duns $
 */

public interface HepRepPoint extends HepRepAttribute {

    /**
     * @return x
     */
    public double getX();
    
    /**
     * @return y
     */
    public double getY();
    
    /**
     * @return z
     */
    public double getZ();
    
    /**
     * @return array of length 3 with x,y,z coordinates
     */
    public double[] getPoint();
    
    /**
     * @return parent primitive
     */
    public HepRepPrimitive getPrimitive();
}
