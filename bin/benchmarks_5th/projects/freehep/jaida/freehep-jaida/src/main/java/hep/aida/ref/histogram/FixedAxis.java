package hep.aida.ref.histogram;
import hep.aida.IAxis;

/**
 * Fixed-width axis; A reference implementation of hep.aida.IAxis.
 *
 * @author The AIDA team @ SLAC.
 * @version $Id: FixedAxis.java 8584 2006-08-10 23:06:37Z duns $
 */
public class FixedAxis implements IAxis {
    private int bins;
    private double min;
    private double binWidth;
    private int xunder, xover;
    private double max;
    /**
     * Create an Axis
     * @param bins Number of bins
     * @param min Minimum for axis
     * @param max Maximum for axis
     */
    public FixedAxis(int bins, double min, double max) {
        if (bins < 1) throw new IllegalArgumentException("bins="+bins);
        if (max <= min) throw new IllegalArgumentException("max <= min");
        
        // Note, for internal consistency we save only min and binWidth
        // and always use these quantities for all calculations. Due to
        // rounding errors the return value from upperEdge is not necessarily
        // exactly equal to max
        
        this.bins = bins;
        this.min = min;
        this.binWidth =  (max - min)/bins;
        this.max = max;
        
        // our internal definition of overflow/underflow differs from
        // that of the outside world
        this.xunder = 0;
        this.xover = bins+1;
    }
    
    /**
     * Check if two Axis are equal.
     * @param o the Object to check
     * @return <code>true</code> if <code>o</code> is an instance of FixedAxis and
     *         it has the same number of bins, minimum and bin width.
     */
    public boolean equals(Object o) {
        if (o instanceof FixedAxis) {
            FixedAxis other = (FixedAxis) o;
            return this.bins == other.bins && this.min == other.min && this.binWidth == other.binWidth;
        }
        return false;
    }
    
    public double binCenter(int index) {
        if ( index == IAxis.OVERFLOW_BIN )
            return Double.POSITIVE_INFINITY;
        if ( index == IAxis.UNDERFLOW_BIN )
            return Double.NEGATIVE_INFINITY;
        return min + binWidth*index + binWidth/2;
    }
    
    /**
     * Get the number of bins in the Axis.
     * @return the number of bins.
     */
    public int bins() {
        return bins;
    }
    
    /**
     * Get the lower edge of a bin.
     * @param index the bin's index
     * @return the bin's lower edge. If <code>index</code> corresponds
     *         to the UNDERFLOW_BIN, Double.NEGATIVE_INFINITY is returned.
     *         If <code>index</code> corresponds to OVERFLOW_BIN, the
     *         upper edge of the axis is returned.
     */
    public double binLowerEdge(int index) {
        if (index == IAxis.UNDERFLOW_BIN) return Double.NEGATIVE_INFINITY;
        if (index == IAxis.OVERFLOW_BIN) return upperEdge();
        if ( Double.isInfinite(min) )
            return min;
        return min + binWidth*index;
    }
    
    /**
     * Get the upper edge of a bin.
     * @param index the bin's index
     * @return the bin's upper edge. If <code>index</code> corresponds
     *         to the OVERFLOW_BIN, Double.POSITIVE_INFINITY is returned.
     *         If <code>index</code> corresponds to UNDERFLOW_BIN, the
     *         lower edge of the axis is returned.
     */
    public double binUpperEdge(int index) {
        if (index == IAxis.UNDERFLOW_BIN) return min;
        if (index == IAxis.OVERFLOW_BIN) return Double.POSITIVE_INFINITY;
        if ( Double.isInfinite(min) )
            return min;
        return min + binWidth*(index+1);
    }
    
    /**
     * Get the bin width.
     * @param index the bin's index. For a Fixed Axis the bin's width is constant.
     * @return the bin's width.
     **/
    public double binWidth(int index) {
        if (index == IAxis.UNDERFLOW_BIN) return Double.POSITIVE_INFINITY;
        if (index == IAxis.OVERFLOW_BIN) return Double.POSITIVE_INFINITY;
        return binWidth;
    }
    
    /**
     * Get the bin's index corresponding to an axis's value.
     * @param coord an axis value
     * @return the bin's index corresponding to <code>coord</code>
     */
    public int coordToIndex(double coord) {
        if (coord < min) return IAxis.UNDERFLOW_BIN;
        int index = (int) Math.floor((coord - min)/binWidth);
        if (index >= bins) return IAxis.OVERFLOW_BIN;
        return index;
    }
    
    /**
     * Get the Axis lower edge.
     * @return the axis lower edge
     */
    public double lowerEdge() {
        return min;
    }
    
    /**
     * Get the Axis upper edge.
     * @return the axis upper edge
     */
    public double upperEdge() {
        if ( Double.isInfinite(max) )
            return max;
        return min + binWidth*bins;
    }
    
    /**
     * This package private method is similar to coordToIndex except
     * that it returns our internal definition for overflow/underflow
     */
    int xgetBin(double coord) {
        if (coord < min) return xunder;
        int index = (int) Math.floor((coord - min)/binWidth);
        if (index > bins) return xover;
        return index+1;
    }
    
    /**
     * Package private method to map from the external representation of bin
     * number to our internal representation of bin number
     */
    int xmap(int index) {
        if (index >= bins) throw new IllegalArgumentException("bin="+index);
        if (index >= 0) return index+1;
        if (index == IAxis.UNDERFLOW_BIN) return xunder;
        if (index == IAxis.OVERFLOW_BIN) return xover;
        throw new IllegalArgumentException("bin="+index);
    }
    
    public boolean isFixedBinning() {
        return true;
    }
}
