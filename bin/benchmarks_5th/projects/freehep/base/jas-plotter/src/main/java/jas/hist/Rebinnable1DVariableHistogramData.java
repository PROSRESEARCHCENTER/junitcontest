package jas.hist;

/**
 * Extends Rebinnable1DHistogramData interface for the case of Histogram1D
 * with variable bin width
 * Example: <tt>edges = (0.2, 1.0, 5.0)</tt> yields an axis with 2 in-range bins
 * <tt>[0.2, 1.0), [1.0, 5.0)</tt> and 2 extra bins <tt>[-inf, 0.2), [5.0, +inf]</tt>.
 * @see Rebinnable1DHistogramData
 */
public interface Rebinnable1DVariableHistogramData
	extends Rebinnable1DHistogramData
{
    double[] getBinEdges();
}
