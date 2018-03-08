/*
 * RmiAxis.java
 *
 * Created on October 26, 2003, 10:05 PM
 */

package hep.aida.ref.remote.rmi.data;

import hep.aida.IAxis;

import java.io.Serializable;

/**
 * This class contains information for the IAxis
 * @author  serbo
 */

public class RmiAxis implements IAxis, Serializable {
    
    static final long serialVersionUID = 8424716035826782276L;
    private int nBins;
    private double lowerEdge;
    private double upperEdge;
    private boolean fixedBinning;
    
    /** Creates a new instance of RmiAxis */
    public RmiAxis(int nBins, double lowerEdge, double upperEdge) {
        this.nBins = nBins;
        this.lowerEdge = lowerEdge;
        this.upperEdge = upperEdge;
        this.fixedBinning = true;
    }
    
    
    // IAxis methods
    
    public double binLowerEdge(int index) {
        if (index == IAxis.UNDERFLOW_BIN) return Double.NEGATIVE_INFINITY;
        if (index == IAxis.OVERFLOW_BIN) return upperEdge();
        return lowerEdge + binWidth(0)*index;
    }
    
    public double binUpperEdge(int index) {
        if (index == IAxis.UNDERFLOW_BIN) return lowerEdge;
        if (index == IAxis.OVERFLOW_BIN) return Double.POSITIVE_INFINITY;
        return lowerEdge + binWidth(0)*(index+1);
    }
    
    public double binWidth(int index) { return (upperEdge - lowerEdge)/nBins; }
    
    public double binCenter(int index) {
        if ( index == IAxis.OVERFLOW_BIN )
            return Double.POSITIVE_INFINITY;
        if ( index == IAxis.UNDERFLOW_BIN )
            return Double.NEGATIVE_INFINITY;
        return binLowerEdge(0) + binWidth(0)*index + binWidth(0)/2;
    }
    
    public int bins() { return nBins; }
    
    public int coordToIndex(double coord) {
        if (coord < lowerEdge) return IAxis.UNDERFLOW_BIN;
        int index = (int) Math.floor((coord - lowerEdge)/binWidth(0));
        if (index >= nBins) return IAxis.OVERFLOW_BIN;
        return index;
    }
    
    public boolean isFixedBinning() { return fixedBinning; }
    
    public double lowerEdge() { return lowerEdge; }
    
    public double upperEdge() { return upperEdge; }
    
}
