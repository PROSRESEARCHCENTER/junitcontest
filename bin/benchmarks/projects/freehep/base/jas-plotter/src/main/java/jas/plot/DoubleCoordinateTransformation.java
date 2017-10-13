package jas.plot;
/**
 * Converts between double values and pixel values for the <code>DoubleAxis</code>
 * type.
 *  @see DoubleAxis
 */
public interface DoubleCoordinateTransformation extends CoordinateTransformation, Transformation
{
	/** Returns a pixel value for a given double value. */
	double convert(double d);

	/** Returns a double value for a given pixel. */
	double unConvert(double i);

	/** Returns the minimum value on the axis. */
	double getPlotMin();

	/** Returns the maximum value on the axis. */
	double getPlotMax();

	/** Returns the point where this axis intersects its partner */
	//double getIntersection();
}
