package hep.aida.util.comparison;

import hep.aida.ext.IComparisonResult;

/**
 *
 * @author The FreeHEP team @ SLAC.
 *
 */
public class ComparisonResult implements IComparisonResult {
    
    private double lowerBound, upperBound;
    private int nDof;
    private double quality;
        
    public int nDof() {
        return nDof;
    }
    
    public double quality() {
        return quality;
    }
    
    public boolean isMatching() {
        return quality >= lowerBound && quality <= upperBound;
    }
    
    public void setnDof(int nDof) {
        if ( nDof < 0 )
            throw new IllegalArgumentException("Cannot have negative degreens of freedom: "+nDof);
        this.nDof = nDof;
    }
    
    public void setQuality(double quality) {
//        if ( quality < 0 )
//            throw new IllegalArgumentException("Cannot have negative quality: "+quality);
        this.quality = quality;
    }
    
    public void setMatchBounds(double lowerBound, double upperBound) {
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }
    
}
