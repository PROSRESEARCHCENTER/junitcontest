package jas.plot;
/**
 * A coordinate transformation allows for conversions between values
 * on an axis and pixels on the screen.  Each axis type(<code>DoubleAxis</code>,
 * <code>TimeAxis</code>, etc.) has a type-specific coordinate transformation
 * that extends this interface and contains methods specific to the type
 * on that axis.
 *  @see DoubleCoordinateTransformation
 *  @see TimeCoordinateTransformation
 *  @see DateCoordinateTransformation
 *  @see StringCoordinateTransformation
 *  @author Jonas Gifford
 */
public interface CoordinateTransformation
{
}
