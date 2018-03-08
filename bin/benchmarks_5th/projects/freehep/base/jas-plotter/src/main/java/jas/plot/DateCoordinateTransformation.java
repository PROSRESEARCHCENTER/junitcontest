package jas.plot;
/**
 * A coordinate transformation for the <code>DateAxis</code> type.
 *  @see DateAxis
 *  @author Jonas Gifford
 */
public interface DateCoordinateTransformation extends CoordinateTransformation
{
	/** Returns a pixel value on the axis for the given date value. */
	double convert(long d);

	/** Returns a date value for the given pixel value. */
	long map(double i);
	
	/** Returns the minimum value on the axis. */
	long getAxisMin();

	/** Returns the maximum value on the axis. */
	long getAxisMax();
}
