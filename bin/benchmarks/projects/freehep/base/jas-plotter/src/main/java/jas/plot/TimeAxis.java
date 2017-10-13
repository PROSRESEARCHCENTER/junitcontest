package jas.plot;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
/**
 * An axis type for representing times.
 * <p>Note that this is not the same as dates.  A date represents a particular
 * point in time (e.g., when an event took place).  This axis represents
 * time values, such as the time between two events.  Therefore, while a date
 * value may be represented as "Jan 31, 1994", a time value might be represented as "3 weeks".
 *  @see DateAxis
 *  @author Jonas Gifford
 */
final public class TimeAxis extends AxisType implements TimeCoordinateTransformation
{
/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * begin code for handling unit size
 */
	/**	The integer code for millisecond units. */
	public static final int MILLISECONDS = 0;

	/**	The integer code for second units. */
	public static final int SECONDS      = 1;

	/**	The integer code for minute units. */
	public static final int MINUTES      = 2;

	/**	The integer code for hour units. */
	public static final int HOURS        = 3;

	/**	The integer code for day units. */
	public static final int DAYS         = 4;

	/**	The integer code for week units. */
	public static final int WEEKS        = 5;

	/**	The integer code for month units. */
	public static final int MONTHS       = 6;

	/**	The integer code for year units. */
	public static final int YEARS        = 7;

	/**
	 * Set <code>OMIT</code> as the length of a time unit to have that unit not
	 * considered as a candidate for axis units.
	 */
	public static final long OMIT        = 0L;

	private long[] unitLengths =
	{
		1L,                            // milliseconds
		1000L,                         // seconds
		1000L * 60L,                   // minutes
		1000L * 60L * 60L,             // hours
		1000L * 60L * 60L * 24L,       // days
		1000L * 60L * 60L * 24L * 7L,  // weeks
		OMIT,                          // months
		1000L * 60L * 60L * 24L * 365L // years
	};

	/**
	 * Allows units to be viewed as valued different from the default.  For example,
	 * a year by default is <code>1000L * 60L * 60L * 24L * 365L</code> milliseconds,
	 * but a call such as this may be desirable:<br>
	 * <code>setUnitLength(TimeAxis.YEARS, (long) (1000 * 60 * 60 * 24 * 365.24));</code>
	 */
	public void setUnitLength(final int unit, final long length)
	{
		if (length < 0L || length != OMIT &&
			(unit != MILLISECONDS && length <= unitLengths[unit - 1] ||
			 unit != YEARS        && length >= unitLengths[unit + 1]))
		{
			throw new IllegalArgumentException();
		}
		if (length == OMIT && unitIndex == unit)
			labelsValid = false; // these labels are no good, so on the next validation we'll get a new set
		unitLengths[unit] = length;
	}

	/**
	 * Returns the number of milliseconds for this unit, or <code>OMIT</code>.
	 *  @see #OMIT
	 */
	public long getUnitLength(int unit)
	{
		return unitLengths[unit];
	}
/*
 * end code for handling unit size
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * begin interface to unit description
 */
	private final String[] unitNames = 
	{
		"milliseconds",
		"seconds",
		"minutes",
		"hours",
		"days",
		"weeks",
		"months",
		"years"
	};

	/** Returns a string representation of the units showing on the axis. */
	public String getUnits()
	{
		return unitNames[unitIndex];
	}
/*
 * end interface to unit description
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
	/**
	 * Sets whether this object should round the minimum down and the maximum
	 * up to make labels land exactly on the min and max of the axis range.
	 */
	public void setUseSuggestedRange(final boolean useSuggestedRange)
	{
		if (this.useSuggestedRange != useSuggestedRange)
			labelsValid = false; // these labels are no good, so on the next validation we'll get a new set
		this.useSuggestedRange = useSuggestedRange;
	}
/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * begin interface to range
 */
	/** Sets the minimum value for the axis data. */
	public void setMin(final long min)
	{
		if (dataMin != min)
			labelsValid = false; // these labels are no good, so on the next validation we'll get a new set
		dataMin = min;
	}

	/** Sets the maximum value for the axis data. */
	public void setMax(final long max)
	{
		if (dataMax != max)
			labelsValid = false; // these labels are no good, so on the next validation we'll get a new set
		dataMax = max;
	}

	/**
	 * Returns the minimum value on the axis range.  This value may be
	 * smaller than the data minimum if the axis has been told to use the
	 * suggested range.
	 *  @see #setUseSuggestedRange(boolean)
	 *  @see #setMin(long)
	 *  @see #getDataMin()
	 */
	public long getAxisMin()
	{
		return axisMin;
	}

	/**
	 * Returns the maximum value on the axis range.  This value may be
	 * larger than the data maximum if the axis has been told to use the
	 * suggested range.
	 *  @see #setUseSuggestedRange(boolean)
	 *  @see #setMax(long)
	 *  @see #getDataMax()
	 */
	public long getAxisMax()
	{
		return axisMax;
	}

	/**
	 * Returns the minimum value on the data range, as set by the method
	 * <code>setMin(long)</code>.  This value may be
	 * larger than the axis minimum if the axis has been told to use the
	 * suggested range.
	 *  @see #setMin(long)
	 *  @see #setUseSuggestedRange(boolean)
	 *  @see #getAxisMin()
	 */
	public long getDataMin()
	{
		return dataMin;
	}

	/**
	 * Returns the maximum value on the axis range, as set by the method
	 * <code>setMax(long)</code>.  This value may be
	 * smaller than the axis maximum if the axis has been told to use the
	 * suggested range.
	 *  @see #setMax(long)
	 *  @see #setUseSuggestedRange(boolean)
	 *  @see #getAxisMax()
	 */
	public long getDataMax()
	{
		return dataMax;
	}
/*
 * end interface to range
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * begin AxisType methods
 */
	/**
	 * Returns an instance of TimeCoordinateTransformation.
	 *  @see TimeCoordinateTransformation
	 */
	CoordinateTransformation getCoordinateTransformation()
	{
		return this;
	}
	void paintAxis(final PlotGraphics g, final double originX, final double originY, final double axisLength,
		final Color textColor, final Color majorTickColor, final Color minorTickColor)
	{
		final FontMetrics fm = g.getFontMetrics();
		if (axis.getAxisOrientation() == Axis.HORIZONTAL)
		{
			final double y = originY + fm.getMaxAscent() + Axis.padFromAxis;
			for (int i = 0; i < labels.length; i++)
			{
				final String text = labels[i].text;
				final double x = originX + labels[i].position * axisLength;
				g.setColor(textColor);
				g.drawString(text, x - fm.stringWidth(text) / 2, y);
				g.setColor(majorTickColor);
				g.drawLine(x, originY + majorTickLength, x, originY - majorTickLength);
			}
		}
		else
		{
			final double x = axis.onLeftSide ? originX - Axis.padFromAxis : originX + Axis.padFromAxis;
			final double height = fm.getAscent() / 2;
			for (int i = 0; i < labels.length; i++)
			{
				final String text = labels[i].text;
				final double y = originY - labels[i].position * axisLength;
				g.setColor(textColor);
				g.drawString(text, axis.onLeftSide ? x - fm.stringWidth(text) : x, y + height);
				g.setColor(majorTickColor);
				g.drawLine(originX - majorTickLength, y, originX + majorTickLength, y);
			}
		}
	}
	
	// The method below uses several calculations with the same idea:
	//    Math.max(<some distance>, 0);
	// The integer <some distance> represents the distance that the
	// label goes past the end of the axis.  If the label
	// doesn't go past the end of the axis, then <some distance>
	// would be negative, in which case the flow past the end is 0.
	void assumeAxisLength(final int axisLength)
	{
      Font font = axis.getFont();
		final FontMetrics fm = axis.getToolkit().getFontMetrics(font);
		final int maxNumberOfDivisions = getMaxNumberOfDivisions(fm, axisLength);
		if (!labelsValid || labels == null || labels.length > maxNumberOfDivisions || labels.length < maxNumberOfDivisions / 2)
			createNewLabels(maxNumberOfDivisions);
		if (axis.getAxisOrientation() == Axis.VERTICAL)
		{
			spaceRequirements.width = longestStringLength(fm,labels) + Axis.padFromAxis;
			spaceRequirements.height = Math.max(fm.getAscent() / 2 - (int) (labels[0].position * axisLength), 0);
			// numbers only, so no descent
			spaceRequirements.flowPastEnd = Math.max(fm.getMaxAscent() - fm.getAscent() / 2 -
				(int) ((1.0 - labels[labels.length - 1].position) * axisLength), 0);
		}
		else
		{
			spaceRequirements.width = Math.max(fm.stringWidth(labels[0].text) / 2 - (int) (labels[0].position * axisLength), 0);
			spaceRequirements.height = fm.getMaxAscent() + Axis.padFromAxis;
			// numbers only, so no descent
			spaceRequirements.flowPastEnd = Math.max(fm.stringWidth(labels[labels.length - 1].text) / 2 -
				(int) ((1.0 - labels[labels.length - 1].position) * axisLength), 0);
		}
	}
	int getMajorTickMarkLength()
	{
		return majorTickLength;
	}
/*
 * end AxisType methods
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * begin private methods
 */
	private void createNewLabels(final int maxNDivisions)
	{
		labelsValid = true;

		unitIndex = getUnitIndex((dataMax - dataMin) / (long) maxNDivisions);
		final long clumpLength = getClumpSize((dataMax - dataMin) / unitLengths[unitIndex] / (long) maxNDivisions);

		if (useSuggestedRange)
			// make axisMin the next smallest multiple of clumpLength (it may stay the same if it is already a multiple)
			axisMin = dataMin - dataMin % clumpLength;
		else
			// axisMin is the same as dataMin because we don't adjust the range
			axisMin = dataMin;

		if (useSuggestedRange && dataMax % clumpLength != 0L)
			// make axisMax the next largest multiple of clumpLength
			axisMax = dataMax + clumpLength - dataMax % clumpLength;
		else
			// axisMax is the same as dataMax either because we don't adjust the range or
			// because it is already a multiple of clumpLength
			axisMax = dataMax;

		int nLabels = (int) ((axisMax - axisMin) / clumpLength);
		if (useSuggestedRange || axisMin % clumpLength == 0 || axisMin % clumpLength > axisMax % clumpLength)
			nLabels++;

		labels = new AxisLabel[nLabels];
		long labelValue = useSuggestedRange || axisMin % clumpLength == 0 ? axisMin : axisMin + clumpLength - axisMin % clumpLength;
		for (int i = 0; i < nLabels; i++)
		{
			labels[i] = new AxisLabel();
			labels[i].text = String.valueOf(labelValue / unitLengths[unitIndex]);
			labels[i].position = (double) (labelValue - axisMin) / (double) (axisMax - axisMin);
			labelValue += clumpLength;
		}
System.out.println("Labels are using units: ".concat(getUnits()));
	}
	private int getMaxNumberOfDivisions(final FontMetrics fm, final int axisLength)
	{
		int result;
		if (axis.getAxisOrientation() == Axis.HORIZONTAL)
			result = axisLength / (fm.charWidth('5') * maxCharsPerLabel + minSpaceBetweenLabels);
			// we assume '5' has typical width
		else
			result = axisLength / (fm.getHeight() + minSpaceBetweenLabels);

		// we have at least two divisions
		return Math.max(2, result);
	}

	// @param  minDivisionSpan the fewest number of milliseconds in a division
	// @return the index corresponding to the units we want to use
	private int getUnitIndex(long minDivisionSpan)
	{
		// We double minDivisionSpan because the cutoff we want to use is actually half of the unit length.  Suppose
		// for example our minDivisionSpan is 0.8 minutes, or some value close to a minute.  If we didn't double
		// minDivisionSpan, our units would be seconds, but that would be silly because we could very easily
		// just have one-minute intervals between labels.  Therefore, we want the cutoff for minutes to be half
		// of a minute instead of a full minute so that values for minDivisionSpan as small as 30 seconds will
		// result in a minute units.  Doubling minDivision span has the same effect on the control statements
		// below as halving all of the unit lengths, but is more efficient because it involves only one calculation
		// instead of one per iteration.
		minDivisionSpan *= 2L;

		// i is the index of a trial unit
		int i = 0;

		// j is the index of the largest acceptable unit
		int j = 0;

		while (i < unitLengths.length)
		{
			if (unitLengths[i] == OMIT)
			// we can't inclulde this unit, so...
			{
				// we skip to the next one
				i++;
				continue;
			}
			if (unitLengths[i] < minDivisionSpan)
			// this one is acceptable, so we
			{
				// set j to the acceptable value of i (the value before incrementing), and go to the next iteration
				j = i++;
				continue;
			}

			// if we get to this point, there are no more acceptable indexes, so we will break and return j
			break;
		}

		// return the highest acceptable value
		return j;
	}

	// @param naturalNumberInClump the number of units (not necessarily milliseconds) in a clump
	//                             (we will find the next largest value and return it in milliseconds)
	private long getClumpSize(long naturalNumberInClump)
	// a clump is the number of units per tick mark
	{
		// these are the clump sizes we will try
		final long[] typicalClumps = {1L, 2L, 5L, 10L, 20L, 25L, 50L, 100L, 200L};

		// mult is a scale factor we use to handle clumps independently from unit size and order of magnitude
		long mult = unitLengths[unitIndex];

		// if the natural number in the clump is greater than 100 we want to divide it
		// up such that we have a number within the inclusive range 0:100
		while (naturalNumberInClump > 100L)
		{
			naturalNumberInClump /= 100L;

			// we increase mult to keep track of how we have scaled
			mult *= 100L;
		}
		int clumpIndex = 0;
		while (naturalNumberInClump >= typicalClumps[clumpIndex])
			clumpIndex++;

		// we multiply by mult, se we get the number of milliseconds in a clump
		return typicalClumps[clumpIndex] * mult;
	}
/*
 * end private methods
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * begin coordinate transformation methods
 */
	public double convert(long time)
	{
		final int minL = axis.getMinLocation();
		final int maxL = axis.getMaxLocation();
		final float f = (float) (time - axisMin) / (float) (axisMax - axisMin);
		return minL + f * (maxL - minL);
	}
/*
 * end coordinate transformation methods
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * begin externalization methods
 */
	public void writeExternal(final ObjectOutput out) throws IOException
	{
		out.writeBoolean(useSuggestedRange);
		out.writeObject(unitLengths);
	}
	public void readExternal(final ObjectInput in) throws IOException, ClassNotFoundException
	{
		useSuggestedRange = in.readBoolean();
		unitLengths = (long[]) in.readObject();
	}
/*
 * end externalization methods
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

 /* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * begin members
 */
	/* variables */
	private boolean useSuggestedRange = false;
	private int unitIndex;
	private long dataMin, dataMax;
	private long axisMin, axisMax;
	private AxisLabel[] labels;

	/* constants */
	private final int majorTickLength = 5;
	private final int minSpaceBetweenLabels = 4;
	private final int maxCharsPerLabel = 6;
/*
 * end members
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
}
