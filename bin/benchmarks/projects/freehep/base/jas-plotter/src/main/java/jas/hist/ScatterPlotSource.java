package jas.hist;

/**
 * Interface to be implemented by DataSources which can provide ScatterPlot
 * data (but not 2D histogram data)
 * @see Rebinnable2DHistogramData
 * @see HasScatterPlotData
 */

public interface ScatterPlotSource extends DataSource
{
	public double getXMin();
	public double getXMax();
	public double getYMin();
	public double getYMax();

	/**
	 * Currently we only support DATE and DOUBLE types
	 */
	public int getXAxisType();
	/**
	 * Currently we only support DOUBLE
	 */
	public int getYAxisType();
	/**
	 * Starts the enumeration of points from the beginning, and the enumeration
	 * will include only points in the given range.
	 */
	public ScatterEnumeration startEnumeration(double xMin, double xMax, double yMin, double yMax);
	/**
	 * Starts the enumeration of points from the beginning, and the enumeration
	 * will include all points stored in the partition.
	 */
	public ScatterEnumeration startEnumeration();
}