package jas.plot;
import java.awt.Color;
import java.awt.FontMetrics;
/**
 * Subclasses encapsulate details for a particular axis type.
 *  @see DoubleAxis
 *  @see DateAxis
 *  @see StringAxis
 *  @see TimeAxis
 *  @author Jonas Gifford
 */
public abstract class AxisType implements java.io.Externalizable
{
	/**
	 * This method is called by the Axis object that receives this object, so
	 * you never need to call this yourself.
	 *  @param the axis this type will be used on
	 */
	final void setAxis(Axis axis)
	{
		this.axis = axis;
	}

	/**
	 * Given a length for the axis, calculate and return the space required
	 * for the axis component.  Calculate new labels if necessary.  Set fields
	 * in the object <code>sizeRequirements</code>.  You should invoke this method
	 * on the <code>Axis</code> object instead of on this class directly.
	 *  @see #sizeRequirements
	 *  @see Axis
	 *  @see Axis#assumeAxisLength(int)
	 */
	abstract void assumeAxisLength(int length);

	/**
	 * Return a subclass of CoordinateTransformation specific to the
	 * type of this axis.
	 */
	abstract CoordinateTransformation getCoordinateTransformation();

	/**
	 * Paint the axis labels, ticks, etc.
	 *  @param g the Graphics object to paint with
	 *  @param origin the actual location of the origin (from top left)
	 *  @param axisLength the length of the axis to mark up
	 *  @param textColor the text color
	 *  @param majorTickMarkColor the color of the major ticks
	 *  @param minorTickMarkColor the color of the minor ticks
	 */
	abstract void paintAxis(PlotGraphics g, double originX, double originY, double axisLength, Color textColor,
		Color majorTickMarkColor, Color minorTickMarkColor);

	/**
	 * The length is defined as the distance drawn above the axis and the distance
	 * below the axis, <B>not</B> the total length of the line.
	 * The layout manager needs this value to calculate space requirements.
	 */
	abstract int getMajorTickMarkLength();

	final int longestStringLength(FontMetrics fm, final AxisLabel[] labels)
	{
		int longestLength = 0;
		for (int i = 0; i < labels.length; i++)
		{
			int length = fm.stringWidth(labels[i].text);
			if (length > longestLength)
				longestLength = length;
		}
		return longestLength;
	}
	final public Axis getAxis()
	{
		return axis;
	}

	/** the axis object this type will be used on */
	protected Axis axis;

	boolean labelsValid;

	final SpaceRequirements spaceRequirements = new SpaceRequirements();
}
