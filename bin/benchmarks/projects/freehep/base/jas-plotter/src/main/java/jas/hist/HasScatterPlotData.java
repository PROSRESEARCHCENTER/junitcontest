package jas.hist;

public interface HasScatterPlotData extends Rebinnable2DHistogramData
{
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
	/**
	 * Even if a DataSource implements this interface it may not have any
	 * ScatterPlot data available at this time, hence the need for this method.
	 */
	public boolean hasScatterPlotData();
}
