package jas.plot;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Hashtable;
/**
 * This class simply displays a given array of strings.  Use for a histogram with a string
 * partition, or if there are very specific labels (numeric or other) you want on the axis.
 * This class displays strings in two formats, represented by two static constants:
 * <ul>
 *  <li><code>CENTER_TEXT_IN_DIVISION</code>: a tick mark and label will be centered in a division</li>
 *  <li><code>TEXT_BESIDE_DIVISION</code>: a tick mark and label will be on either side of a division</li>
 * </ul>
 * <p>Use the method <code>setLabelPlacementStyle(int)</code> to change the setting.
 * <p>Labels are displayed in the order as they appear in the array given to the method
 * <code>setLabels(String[])</code> from left to right on a horizontal axis and from bottom to top
 * on a vertical axis.
 *  @see #CENTER_TEXT_IN_DIVISION
 *  @see #TEXT_BESIDE_DIVISION
 *  @see #setLabelPlacementStyle(int)
 *  @see #setLabels(String[])
 *  @author Jonas Gifford
 */
public final class StringAxis extends AxisType implements StringCoordinateTransformation
{
	/** Represents a display style where a tick and label are centered in a division. */
	public static final int CENTER_TEXT_IN_DIVISION = 1;

	/** Represents a display style where a tick and label are on either side of a division. */
	public static final int TEXT_BESIDE_DIVISION    = 2;
/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * begin constructors
 */
	/**
	 * Creates a new string axis type object, with labels and ticks centered in a division.
	 *  @see #CENTER_TEXT_IN_DIVISION
	 */
	public StringAxis()
	{
		this(CENTER_TEXT_IN_DIVISION);
	}

	/**
	 * Creates a new string axis type object.
	 *  @param labelPlacementStyle how to place labels (supply either <code>CENTER_TEXT_IN_DIVISION</code> or
	 *                             <code>TEXT_BESIDE_DIVISION</code>)
	 *  @see #CENTER_TEXT_IN_DIVISION
	 *  @see #TEXT_BESIDE_DIVISION
	 */
	public StringAxis(final int labelPlacementStyle)
	{
		this.labelPlacementStyle = labelPlacementStyle;
	}
/*
 * end constructors
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * begin public interface
 */
	/**
	 * Sets the label placement style.  Supply either <code>CENTER_TEXT_IN_DIVISION</code> or
	 * <code>TEXT_BESIDE_DIVISION</code>.
	 *  @see #CENTER_TEXT_IN_DIVISION
	 *  @see #TEXT_BESIDE_DIVISION
	 */
	public void setLabelPlacementStyle(final int labelPlacementStyle)
	{
		this.labelPlacementStyle = labelPlacementStyle;
	}

	/**
	 * Returns the label placement style: either <code>CENTER_TEXT_IN_DIVISION</code> or
	 * <code>TEXT_BESIDE_DIVISION</code>.
	 *  @see #CENTER_TEXT_IN_DIVISION
	 *  @see #TEXT_BESIDE_DIVISION
	 */
	public int getLabelPlacementStyle()
	{
		return labelPlacementStyle;
	}

	/** 
	 * Sets the labels to display.
	 */
	public void setLabels(String[] labels)
	{
		if (hash == null)
			hash = new Hashtable(labels.length);
		else
			hash.clear();
		this.labels = labels;
		for (int i = 0; i < labels.length; i++)
			hash.put(labels[i], new Integer(i));
		if (axis != null && axis.getAxisOrientation() == Axis.HORIZONTAL && (layers == null || layers.length != labels.length))
			layers = new int[labels.length];
		labelsValid = false; // we set this flag so that when the Axis object gets told to assume a new length
		                     // it knows that we need to calculate new space requirements (because of the new
		                     // labels)
	}

	/** Returns the labels used for this axis. */
	public String[] getLabels()
	{
		return labels;
	}
/*
 * end public interface
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * begin axis type methods
 */
	CoordinateTransformation getCoordinateTransformation()
	{
		return this;
	}
	void assumeAxisLength(final int lengthOfAxisLine)
	{
      if ( labels == null || labels.length == 0) return;
		labelsValid = true;

      Font font = axis.getFont();
		final FontMetrics fm = axis.getToolkit().getFontMetrics(font);

		final int distanceFromEnd = labelPlacementStyle == CENTER_TEXT_IN_DIVISION ?
			lengthOfAxisLine / labels.length / 2 : 0;

		if (axis.getAxisOrientation() == Axis.HORIZONTAL)
		{
			// the overflow is set here...
			if (labelPlacementStyle == CENTER_TEXT_IN_DIVISION)
			{
				spaceRequirements.flowPastEnd = Math.max(fm.stringWidth(labels[labels.length - 1]) / 2 -
					lengthOfAxisLine / labels.length / 2, 0);
			}
			else
				spaceRequirements.flowPastEnd = fm.stringWidth(labels[labels.length - 1]) / 2;

			// then the width...
			spaceRequirements.width = Math.max(0, fm.stringWidth(labels[0]) / 2 - distanceFromEnd);

			// now for the height...
			// extraRows is the number of rows of text besides the first one
			int extraRows = 0;
			if (lengthOfAxisLine > 0)
			// this test fails only when the plot has been shrunk so far that the x axis has been
			// pushed to the right of the y axis but it is important to test for this because
			// if lengthOfAxisLine is negative we get an infinite regress where the int array
			// gets bigger and bigger until all of the memory is gone
			{
				int[] lastTakenPixelOnRow = new int[3];
				// we wouldn't really expect more than three rows, but we will expand if necessary

				final int n = labelPlacementStyle == TEXT_BESIDE_DIVISION ? labels.length - 1 : labels.length;
				int xStart = spaceRequirements.width + Axis.padAroundEdge;
				if (labelPlacementStyle == CENTER_TEXT_IN_DIVISION)
					xStart += lengthOfAxisLine / labels.length / 2;
				for (int i = 0; i < labels.length; i++)
				{
					final int x = xStart + i * lengthOfAxisLine / n;
					int row = 0;
					final int halfOfStringWidth = fm.stringWidth(labels[i]) / 2;
					if (i != 0)
					{
						while (x - halfOfStringWidth < lastTakenPixelOnRow[row])
						{
							if (++row >= lastTakenPixelOnRow.length)
							{
								int[] newArray = new int[lastTakenPixelOnRow.length * 2];
								System.arraycopy(lastTakenPixelOnRow, 0, newArray, 0, lastTakenPixelOnRow.length);
								lastTakenPixelOnRow = newArray;
							}
						}
					}
					if (row > extraRows)
						extraRows = row;
					lastTakenPixelOnRow[row] = x + halfOfStringWidth + minSpaceBetweenLabels;
					layers[i] = row;
				}
			}
			spaceRequirements.height = fm.getMaxAscent() + fm.getMaxDescent() + Axis.padFromAxis
				// for each additional row we add the line height
				+ extraRows * fm.getHeight();
		}
		else
		{
			spaceRequirements.width = longestStringLength(fm,labels) + Axis.padFromAxis;
			spaceRequirements.height = Math.max(0, fm.getAscent() / 2 + fm.getMaxDescent() - distanceFromEnd);

			if (labelPlacementStyle == CENTER_TEXT_IN_DIVISION)
			{
				spaceRequirements.flowPastEnd = Math.max(fm.getMaxAscent() - fm.getAscent() / 2 -
					lengthOfAxisLine / labels.length / 2, 0);
			}
			else
				spaceRequirements.flowPastEnd = fm.getMaxAscent() - fm.getAscent() / 2;
		}
	}
	void paintAxis(final PlotGraphics g, 
		final double originX, final double originY, final double length,
		final Color textColor, final Color majorTickColor, final Color minorTickColor)
	{
		if (labels != null)
		{
			final int n = labelPlacementStyle == TEXT_BESIDE_DIVISION ? labels.length - 1 : labels.length;
			final FontMetrics fm = g.getFontMetrics();
			if (axis.getAxisOrientation() == Axis.HORIZONTAL)
			{
				final double xStart = labelPlacementStyle == TEXT_BESIDE_DIVISION ? originX : originX + (length / labels.length) / 2;
				final double y = originY + fm.getMaxAscent() + Axis.padFromAxis;
				final double lineHeight = fm.getHeight();
				for (int i = 0; i < labels.length; i++)
				{
					final double x = xStart + i * length / n;
					g.setColor(majorTickColor);
					g.drawLine(x, originY + majorTickLength, x, originY - majorTickLength);
					g.setColor(textColor);
					g.drawString(labels[i], x - fm.stringWidth(labels[i]) / 2, y + layers[i] * lineHeight);
				}
			}
			else
			{
				final double x = axis.onLeftSide ? originX - Axis.padFromAxis : originX + Axis.padFromAxis;
				final double yStart = (labelPlacementStyle == TEXT_BESIDE_DIVISION ? originY : originY - (length / labels.length) / 2);
				final double height = fm.getAscent() / 2;
				for (int i = 0; i < labels.length; i++)
				{
					final double y = yStart - i * length / n;
					g.setColor(textColor);
					g.drawString(labels[i], axis.onLeftSide ? x - fm.stringWidth(labels[i]) : x, y + height);
					g.setColor(majorTickColor);
					g.drawLine(originX + majorTickLength, y, originX - majorTickLength, y);
				}
			}
		}
	}
	int getMajorTickMarkLength()
	{
		return majorTickLength;
	}
/*
 * end axis type methods
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * begin private methods
 */
   private int longestStringLength(FontMetrics fm, final String[] labels)
	{
		int longestLength = 0;
		for (int i = 0; i < labels.length; i++)
		{
			int length = fm.stringWidth(labels[i]);
			if (length > longestLength)
				longestLength = length;
		}
		return longestLength;
	}
	private double indexToLocation(int index)
	{
		final double minL = axis.getMinLocation();
		final double maxL = axis.getMaxLocation();
		if (labelPlacementStyle == CENTER_TEXT_IN_DIVISION)
			return minL + (maxL - minL) * index / labels.length + ((maxL - minL) / labels.length) / 2;
		else
			return minL + (maxL - minL) * index / labels.length;
	}
/*
 * end private methods
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * begin coordinate transformation methods
 */
	public double convert(String s)
	{
		try
		{
			return indexToLocation(((Integer) hash.get(s)).intValue());
		}
		catch (Exception e)
		{
			return -1;
		}
	}
	public double binWidth()
	{
		return (double) (axis.getMaxLocation() - axis.getMinLocation()) / labels.length;
	}
/*
 * end coordinate transformation methods
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * begin externalization methods
 */
	public void writeExternal(final ObjectOutput out) throws IOException
	{
		out.writeInt(labelPlacementStyle);
	}
	public void readExternal(final ObjectInput in) throws IOException
	{
		labelPlacementStyle = in.readInt();
	}
/*
 * end externalization methods
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
	private int labelPlacementStyle;
	private Hashtable hash;
	private final int majorTickLength = 5;
	private final int minorTickLength = 3;
	private final int minSpaceBetweenLabels = 3;
	private String[] labels;
	private int[] layers; // each element in this array holds the layer for the label in the corresponding
	                      // element of the above array
}
