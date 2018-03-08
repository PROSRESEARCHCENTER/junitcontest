package hep.aida.ref.histogram;
import hep.aida.IAxis;
/**
 * Variable-width axis; A reference implementation of hep.aida.IAxis.
 *
 * @author The AIDA team @ SLAC.
 * @version 1.0, 23/03/2000
 */
public class VariableAxis implements IAxis {
    private double min;
    private int bins;
    private double[] edges;
    /**
     * Constructs and returns an axis with the given bin edges.
     * Example: <tt>edges = (0.2, 1.0, 5.0)</tt> yields an axis with 2 in-range bins <tt>[0.2,1.0), [1.0,5.0)</tt> and 2 extra bins <tt>[-inf,0.2), [5.0,inf]</tt>.
     * @param edges the bin boundaries the partition shall have;
     *        must be sorted ascending and must not contain multiple identical elements.
     * @throws IllegalArgumentException if <tt>edges.length < 1</tt>.
     */
    public VariableAxis(double[] edges) {
        if (edges.length < 1) throw new IllegalArgumentException();
        
        // check if really sorted and has no multiple identical elements
        for (int i=0; i<edges.length-1; i++) {
            if (edges[i+1] <= edges[i]) {
                throw new IllegalArgumentException("edges must be sorted ascending and must not contain multiple identical values");
            }
        }
        
        this.min = edges[0];
        this.bins = edges.length - 1;
        this.edges = (double[]) edges.clone();
    }
    
    public double binCenter(int index) {
        if ( index == IAxis.OVERFLOW_BIN )
            return Double.POSITIVE_INFINITY;
        if ( index == IAxis.UNDERFLOW_BIN )
            return Double.NEGATIVE_INFINITY;
        return (binLowerEdge(index) + binUpperEdge(index)) / 2;
    }

    public double binLowerEdge(int index) {
        if (index == IAxis.UNDERFLOW_BIN) return Double.NEGATIVE_INFINITY;
        if (index == IAxis.OVERFLOW_BIN) return upperEdge();
        return edges[index];
    }
    public int bins() {
        return bins;
    }
    public double binUpperEdge(int index) {
        if (index == IAxis.UNDERFLOW_BIN) return lowerEdge();
        if (index == IAxis.OVERFLOW_BIN) return Double.POSITIVE_INFINITY;
        return edges[index+1];
    }
    public double binWidth(int index) {
        return binUpperEdge(index) - binLowerEdge(index);
    }
    public int coordToIndex(double coord) {
        if (coord < min) return IAxis.UNDERFLOW_BIN;
        int	index = java.util.Arrays.binarySearch(this.edges,coord);
        if (index < 0) index = -index -1-1; // not found
        if (index >= bins) return IAxis.OVERFLOW_BIN;
        return index;
    }
    public double lowerEdge() {
        return min;
    }
    /**
     * Returns a string representation of the specified array.  The string
     * representation consists of a list of the arrays's elements, enclosed in square brackets
     * (<tt>"[]"</tt>).  Adjacent elements are separated by the characters
     * <tt>", "</tt> (comma and space).
     * @return a string representation of the specified array.
     */
    protected static String toString(double[] array) {
        StringBuffer buf = new StringBuffer();
        buf.append("[");
        int maxIndex = array.length - 1;
        for (int i = 0; i <= maxIndex; i++) {
            buf.append(array[i]);
            if (i < maxIndex)
                buf.append(", ");
        }
        buf.append("]");
        return buf.toString();
    }
    public double upperEdge() {
        return edges[edges.length-1];
    }
    
    public boolean isFixedBinning() {
        return false;
    }
    
    
    /**
     * Check if two Axis are equal.
     * @param o the Object to check
     * @return <code>true</code> if <code>o</code> is an instance of FixedAxis and
     *         it has the same number of bins, minimum and bin width.
     */
    public boolean equals(Object o) {
        if (! ( o instanceof VariableAxis ) ) return false;
        VariableAxis other = (VariableAxis) o;
        if ( this.bins != other.bins ) return false;
        for ( int i = 0; i < bins; i ++ )
            if ( this.binUpperEdge(i) != other.binUpperEdge(i) ||  this.binLowerEdge(i) != other.binLowerEdge(i) ) return false;
        return true;
    }
    
    
}
