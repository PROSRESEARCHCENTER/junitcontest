package jas.hist;

/**
 * This is the interface that must be implemented by any data source for a 
 * 1D histogram. Despite the name, the data does not actually have to be
 * rebinnable, the isRebinnable() method should return false if the data
 * cannot be rebinned.
 * @see Rebinnable2DHistogramData
 */
public interface Rebinnable1DHistogramData
	extends DataSource
{
	/**
	 * Called to request the binned data be returned. 
	 * If the DataSource
	 * indicated it was not rebinnable then the bins, min and max arguments are guaranteed
	 * to be the same values as returned by getMin(), getMax() and getBins() methods.
	 * <p>
	 * The routine returns a two dimensional array where the first dimension
	 * has the following meaning:
	 * <ul>
	 * <li>[0] An array of data points (one entry per bin)	 
	 * <li>[1] An array of plus errors (one entry per bin)
	 * <li>[2] An array of minus errors (one entry per bin)
	 * </ul>
	 * If the minus error is absent the errors are assumed to be symmetric,
	 * if the plus error is also absent the errors are assumed to be the
	 * square root of the data.
	 * @param bin The number of bins requested
	 * @param min The min of the range over which to bin
	 * @param max The max of the range over which to bin
	 * @param wantErrors If false indicates that the errors are not required and need not be calculated (most implementations will ignore this parameter)
	 * @param hurry If true indicates the results should be provided as fast as possible, even if some approximation is needed (most implementations will ignore this parameter)
	 * @return An array representing the binned data and errors (see description above)
	 */
	public double[][] rebin(int bins, double min, double max, boolean wantErrors, boolean hurry);
	/**
	 * Returns the (suggested) minimum value for the X axis
	 */
	public double getMin();
	/**
	 * Returns the (suggested) maximum value for the X axis
	 */
	public double getMax();
	/**
	 * Returns the (suggested) number of bins to use
	 */
	public int getBins();
	/**
	 * Returns true if the data source is capable of recalculating the bin
	 * contents, or false of the interface is not-capable of recalculating the
	 * bin contents. In the former case the values returned by getMin, getMax and
	 * getBins are just taken to be suggestions, in the latter case they are
	 * taken to be fixed in stone.
	 * @return True if the datasource is rebinnable
	 */
	public boolean isRebinnable();
	/**
	 * Returns one of DOUBLE,INTEGER,STRING,DATE or DELTATIME
	 */
	public int getAxisType();
	/**
	 * Returns the axis labels.
	 * Only relevant if the axisType is STRING, otherwise can return null
	 * @return An array of bin labels to use on the X axis
	 */
	public String[] getAxisLabels();
}
