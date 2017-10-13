/*
 * OneDBinner.java
 *
 * This class can handle only symmetric bin errors
 *
 * Created on July 18, 2002, 5:02 PM
 */

package hep.aida.ref.histogram.binner;

import hep.aida.ref.histogram.HistUtils;

/**
 *
 * @author  The AIDA team at SLAC
 *
 */
public class BasicBinner1D extends AbstractBinner1D {
    
    public BasicBinner1D(int bins) {
        super(bins);
    }
    
    public BasicBinner1D(Binner1D binner) {
        super(binner);
    }
    
    
    // Service methods
    
    
    // Binner1D methods
    
    public double plusError(int bin) {
        return Math.sqrt(sumWW[bin]);
    }
    
    public double minusError(int bin) {
        return plusError(bin);
    }    
}
