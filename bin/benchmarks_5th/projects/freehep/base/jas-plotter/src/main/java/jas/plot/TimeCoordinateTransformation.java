package jas.plot;
/**
 * Converts coordinates between time values to pixel values for the
 * <code>TimeAxis</code> type.
 *  @see TimeAxis
 *  @author Jonas Gifford
 */
public interface TimeCoordinateTransformation extends CoordinateTransformation
{
	/** Returns a pixel value for a time value. */
	double convert(long d);
}
