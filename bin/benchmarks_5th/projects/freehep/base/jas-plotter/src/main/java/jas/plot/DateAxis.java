package jas.plot;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.Vector;
/**
 * This class implements a simple date axis, where values on the axis are particular
 * instances in time.  The dates are impemented with <code>long</code> values, representing
 * the number of milliseconds after midnight on Jan 1, 1970 GMT.
 *  @author Jonas Gifford
 */
public final class DateAxis extends AxisType implements DateCoordinateTransformation
{
/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * begin AxisType methods
 */
	CoordinateTransformation getCoordinateTransformation()
	{
		return this;
	}
	void assumeAxisLength(final int axisLength)
	{
      Font font = axis.getFont();
		final FontMetrics fm = axis.getToolkit().getFontMetrics(font);
		final int maxNumberOfDivisions = getMaxNumberOfDivisions(fm,axisLength);
		if (!labelsValid || labels == null || labels.length > maxNumberOfDivisions || labels.length < maxNumberOfDivisions / 2)
			labels = getAxisLabels(maxNumberOfDivisions);
		final DateLabel first = labels[0];
		final DateLabel lastLabel = labels[labels.length - 1];

		if (axis.getAxisOrientation() == Axis.HORIZONTAL)
		{
			spaceRequirements.height = fm.getMaxAscent() + fm.getMaxDescent() + Axis.padFromAxis;
			spaceRequirements.width = Math.max(fm.stringWidth(first.text) / 2 -
				(int) (first.position * axisLength), 0);
			if (first.subtext != null)
			{
				spaceRequirements.height += fm.getHeight(); // add room for a second line
				spaceRequirements.width = Math.max(fm.stringWidth(first.subtext) -
					(int) (first.position * axisLength),
					spaceRequirements.width);
			}
			int lastLabelWidth = fm.stringWidth(lastLabel.text);
			if (lastLabel.subtext != null)
				lastLabelWidth = Math.max(fm.stringWidth(lastLabel.subtext), lastLabelWidth);
			spaceRequirements.flowPastEnd = Math.max(lastLabelWidth / 2 +
				(int) (lastLabel.position * axisLength) - axisLength, 0);
		}
		else
		{
			int longest = 0;
			for (int i = 0; i < labels.length; i++)
				longest = Math.max(fm.stringWidth(labels[i].text), longest);
			spaceRequirements.width = longest + Axis.padFromAxis;
			spaceRequirements.height = Math.max(fm.getAscent() / 2 + fm.getMaxDescent() -
				(int) (first.position * axisLength), 0);
			spaceRequirements.flowPastEnd =  Math.max(fm.getMaxAscent() - fm.getAscent() / 2 +
				(int) (lastLabel.position * axisLength) - axisLength, 0);
		}
	}
	void paintAxis(final PlotGraphics g, final double originX, final double originY, 
		final double length,
		final Color textColor, final Color majorTickColor, final Color minorTickColor)
	{
		final FontMetrics fm = g.getFontMetrics();
		if (axis.getAxisOrientation() == Axis.HORIZONTAL)
		{
			final double y = originY + fm.getMaxAscent() + Axis.padFromAxis;
			for (int i = 0; i < labels.length; i++)
			{
				final String text = labels[i].text;
				final double x = originX + labels[i].position * length;
				g.setColor(textColor);
				g.drawString(text, x - fm.stringWidth(text) / 2, y);
				final String subtext = labels[i].subtext;
				if (subtext != null)
					g.drawString(subtext, i != 0 ? x - fm.stringWidth(subtext) / 2 :
						x - fm.stringWidth(subtext), y + fm.getHeight());
				g.setColor(majorTickColor);
				g.drawLine(x, originY + majorTickLength, x, originY - majorTickLength);
			}
		}
		else
		{
			final double x = axis.onLeftSide ? originX - Axis.padFromAxis : originX + Axis.padFromAxis;
			final double lineOffset = fm.getAscent() / 2;
			for (int i = 0; i < labels.length; i++)
			{
				final String text = labels[i].text;
				final double y = originY - labels[i].position * length;
				g.setColor(majorTickColor);
				g.drawLine(originX - majorTickLength, y, originX + majorTickLength, y);
				g.setColor(textColor);
				g.drawString(text, axis.onLeftSide ? x - fm.stringWidth(text) : x, y + lineOffset);
			}
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
 * begin coordinate transformation methods
 */
	public double convert(final long d)
	{
		return timeToPixel(d);
	}
	public long map(final double i)
	{
		final int minL = axis.getMinLocation();
		final int maxL = axis.getMaxLocation();
		final double d = (i - minL) / (maxL - minL);
		return axis_min + (long) (d * (axis_max - axis_min));
	}
/*
 * end coordinate transformation methods
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * begin private methods
 */
	private int getMaxNumberOfDivisions(final FontMetrics fm, final int axisLength)
	{
		return Math.max(minNumberOfDivisions, axis.getAxisOrientation() == Axis.HORIZONTAL ?
			axisLength / (fm.charWidth('5') * maxCharsPerLabel + minSpaceBetweenLabels) : // assume '5' has typical width
			axisLength / (fm.getMaxAscent() + fm.getMaxDescent() + fm.getHeight() +
				minSpaceBetweenLabels));
			// getMaxAscent() covers height of first line
			// getMaxDescent() + getHeight() covers second line
	}

	/**
	 * Returns the index corresponding to the units we will use on the axis.
	 * As an example, if the argument represents a number greater than or equal to 30 seconds
	 * and less than 30 minutes, minutes will be the major units on the axis.  The
	 * algorithm finds the units such that the minDivisionSpan given is
	 * greater than or equal to half of one of the units it returns but less than
	 * half of one of the next larger units (if they exist).
	 *  @param minDivisionSpan the minimum number of milliseconds in a division
	 */
	private int getScaleIndex(final long minDivisionSpan)
	{
		final long minTime = minDivisionSpan * 2L;
		int scaleIndex = scaleFactors.length;
		while (--scaleIndex > 0 && minTime < scaleFactors[scaleIndex]);
		return scaleIndex;
	}
	private boolean setToZero(final int scaleIndex)
	{
		boolean allMin = true;
		for (int i = 0; i < scaleIndex; i++)
		{
			int field = calendarFields[i];
			final int min = calendar.getMinimum(field);
			final boolean isMin = calendar.get(field) == min;
			allMin = allMin && isMin;
			if (! isMin) calendar.set(field, min);
		}
		return allMin;
	}
	private DateLabel[] getAxisLabels(final int maxNumberOfDivisions)
	{
		// some initialization:
		calendar.setTimeInMillis(data_min); // may go forward or backward
		calendar.setTimeZone(timeZone);
		format.setTimeZone(timeZone);
		axis_min = data_min;
		axis_max = data_max;
		labelsValid = true;

		final long difference = data_max - data_min;
		final int scaleIndex = getScaleIndex(difference / (long) maxNumberOfDivisions);
		// scaleIndex represents the units that will appear on the axis
		// we may clump several of these units into one division, but we won't divide them up
		int unitsPerDivision = 1; // 1 is default, we may increase this
		if ((int) (difference / scaleFactors[scaleIndex]) > maxNumberOfDivisions)
		{
			final int naturalNumberInClump = (int) (difference /
				(long) maxNumberOfDivisions / scaleFactors[scaleIndex]) + 1;
			int[] acceptableClumps;
			if (scaleIndex == DAYS)
			{
				final int[] a = {2, 5, 10, 15};
				acceptableClumps = a;
			}
			else if (scaleIndex == YEARS)
			{
				if (naturalNumberInClump > 100)
				{
					int i = naturalNumberInClump;
					int mult = 1;
					while (i > 100)
					{
						mult *= 100;
						i /= 100;
					}
					final int[] a = {2 * mult, 5 * mult, 10 * mult, 25 * mult, 50 * mult, 100 * mult};
					acceptableClumps = a;
				}
				else
				{
					final int[] a = {2, 5, 10, 25, 50, 100};
					acceptableClumps = a;
				}
			}
			else if (scaleIndex == MILLISECONDS)
			{
				final int[] a = {2, 5, 10, 25, 50, 100, 200, 250, 500};
				acceptableClumps = a;
			}
			else if (scaleIndex == MONTHS)
			{
				final int[] a = {2, 3, 4, 6};
				acceptableClumps = a;
			}
			else if (scaleIndex == HOURS)
			{
				final int[] a = {2, 3, 4, 6, 12};
				acceptableClumps = a;
			}
			else // we're in seconds or minutes
			{
				final int[] a = {2, 5, 10, 15, 20, 30};
				acceptableClumps = a;
			}
			int i = 0;
			while (i - 1 < acceptableClumps.length && naturalNumberInClump > acceptableClumps[i])
				i++;
			unitsPerDivision = acceptableClumps[i];
		}

		final boolean allMinAtMin = setToZero(scaleIndex);

		if (useSuggestedRange)
		{
			int fieldValue;
			int mod;

			// set the min
			fieldValue = calendar.get(calendarFields[scaleIndex]);
			mod = (scaleIndex == DAYS && unitsPerDivision == 2 ? fieldValue - 1 : fieldValue)
				% unitsPerDivision;
			if (scaleIndex == DAYS && mod != 0 && fieldValue - mod +
				unitsPerDivision > monthLengths[calendar.get(Calendar.MONTH)])
				calendar.add(calendarFields[scaleIndex], -mod - unitsPerDivision);
			else if (mod != 0)
			{
				if (scaleIndex != DAYS || fieldValue > unitsPerDivision)
					calendar.add(calendarFields[scaleIndex], -mod);
				else
					calendar.set(Calendar.DATE, 1);
			}
			axis_min = calendar.getTimeInMillis();

			// temporarily set the calendar to the maximum: here we determine how to round up and set the max
			calendar.setTimeInMillis(data_max);
			fieldValue = calendar.get(calendarFields[scaleIndex]);
			mod = (scaleIndex == DAYS && unitsPerDivision == 2 ? fieldValue - 1 : fieldValue)
				% unitsPerDivision;
			if (!setToZero(scaleIndex) || mod != 0)
			{
				calendar.add(calendarFields[scaleIndex], unitsPerDivision - mod);
			}
			axis_max = calendar.getTimeInMillis();

			// restore to min
			calendar.setTimeInMillis(axis_min);
		}
		else if (! allMinAtMin)
		{
			if (scaleIndex == DAYS && calendar.get(Calendar.DATE) % unitsPerDivision != 0 &&
				calendar.get(Calendar.DATE) + unitsPerDivision >= monthLengths[calendar.get(Calendar.MONTH)])
			{
				calendar.set(Calendar.DATE, 1);
				calendar.add(Calendar.MONTH, 1);
			}
			else if (scaleIndex == DAYS && calendar.get(Calendar.DATE) < unitsPerDivision)
			{
				calendar.set(Calendar.DATE, 1);
			}
			else
			{
				calendar.add(calendarFields[scaleIndex], unitsPerDivision -
					calendar.get(calendarFields[scaleIndex]) % unitsPerDivision);
			}
		}

		final String normalLine = normalTimeFormats[scaleIndex];
		int lastValueOfNextHigherField = -1;
		final boolean isHorizontal = axis.getAxisOrientation() == Axis.HORIZONTAL;
		boolean first = true;
		while (true)
		{
			DateLabel newLabel = new DateLabel();
			labelVector.addElement(newLabel);

			format.applyPattern(first && !isHorizontal ? verticalAxisFirstEntryTimeFormats[scaleIndex] :
				normalLine);
			newLabel.text = format.format(calendar.getTime());
			newLabel.position = timeToDouble(calendar.getTimeInMillis());
			if (first)
			{
				first = false;
				if (isHorizontal)
				{
					final String pattern = horizontalAxisSecondLineFirstEntryTimeFormats[scaleIndex];
					if (pattern != null)
					{
						format.applyPattern(pattern);
						newLabel.subtext = format.format(calendar.getTime());
					}
				}
				if (scaleIndex + 1 < calendarFields.length)
					lastValueOfNextHigherField =
						calendar.get(calendarFields[getIndexForNextHighestField(scaleIndex)]);
			}
			else if (scaleIndex + 1 < calendarFields.length)
			{
				int currentValueOfNextHigherField =
					calendar.get(calendarFields[getIndexForNextHighestField(scaleIndex)]);
				if (currentValueOfNextHigherField != lastValueOfNextHigherField)
				{
					if (isHorizontal)
					{
						format.applyPattern(horizontalAxisSecondLineSubsequentEntryTimeFormats[scaleIndex]);
						newLabel.subtext = format.format(calendar.getTime());
					}
					else
					{
						format.applyPattern(verticalAxisChangedUnitsTimeFormatsPrefix[scaleIndex]);
						newLabel.text = format.format(calendar.getTime()).concat(newLabel.text);
					}
					lastValueOfNextHigherField = currentValueOfNextHigherField;
				}
			}
			if (scaleIndex == DAYS && unitsPerDivision != 1)
			{
				final int day = calendar.get(Calendar.DAY_OF_MONTH);
				int nextLabel = day + unitsPerDivision;
				if (day == 1 && unitsPerDivision != 2)
					nextLabel--;
				if (nextLabel + unitsPerDivision / 2 > monthLengths[calendar.get(Calendar.MONTH)])
				{
					calendar.set(Calendar.DAY_OF_MONTH, 1);
					calendar.add(Calendar.MONTH, 1);
					if (calendar.getTimeInMillis() > axis_max &&
						nextLabel <= monthLengths[calendar.get(Calendar.MONTH)])
					{
						calendar.add(Calendar.MONTH, -1);
						calendar.set(Calendar.DAY_OF_MONTH, nextLabel);
					}
				}
				else
					calendar.set(Calendar.DAY_OF_MONTH, nextLabel);
			}
			else
				calendar.add(calendarFields[scaleIndex], unitsPerDivision);

			if (calendar.getTimeInMillis() <= axis_max)
				// we're set up for the next label, so we...
				continue;

			// we're done, so we...
			break;
		}
		DateLabel[] result = new DateLabel[labelVector.size()];
		labelVector.copyInto(result);
		labelVector.removeAllElements();
		return result;
	}
	private int getIndexForNextHighestField(final int scaleIndex)
	{
		switch (scaleIndex)
		{
		case MILLISECONDS:
			 // When our units are in milliseconds, we will display seconds so we only
			 // need a subtext update when the minute changes.
			return MINUTES;
		case MINUTES:
			 // When our units are in minutes, we will display hours so we only
			 // need a subtext update when the day changes.
			return DAYS;
		default:
			 // For all other cases, we need an update when the next higher
			 // field has changed.
			return scaleIndex + 1;
		}
	}
	private double timeToPixel(long time)
	{
		final int minL = axis.getMinLocation();
		final int maxL = axis.getMaxLocation();
		return minL + timeToDouble(time) * (maxL - minL);
	}
	private double timeToDouble(long time)
	{
		return ((double) (time - axis_min)) / ((double) (axis_max - axis_min));
	}
/*
 * end private methods
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * begin public interface
 */
	public void setMin(final long min)
	{
		if (data_min != min)
		{
			labelsValid = false;
			data_min = min;
                        axis_min = min;
			if (axis != null) axis.revalidate();
		}
	}
	public void setMax(final long max)
	{
		if (data_max != max)
		{
			labelsValid = false;
			data_max = max;
                        axis_max = max;
			if (axis != null) axis.revalidate();
		}
	}
	public long getDataMin()
	{
		return data_min;
	}
	public long getDataMax()
	{
		return data_max;
	}
	public long getAxisMin()
	{
		return axis_min;
	}
	public long getAxisMax()
	{
		return axis_max;
	}
	public void setTimeZone(final TimeZone z)
	{
		if (timeZone != z)
		{
			labelsValid = false;
			timeZone = z;
		}
	}
	public TimeZone getTimeZone()
	{
		return timeZone;
	}
	public void setUseSuggestedRange(boolean useSuggestedRange)
	{
		if (this.useSuggestedRange != useSuggestedRange)
		{
			labelsValid = false;
			this.useSuggestedRange = useSuggestedRange;
			if (axis != null) axis.revalidate();
		}
	}
	public boolean getUseSuggestedRange()
	{
		return useSuggestedRange;
	}
/*
 * end public interface
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * begin externalization methods
 */
	public void writeExternal(final ObjectOutput out) throws IOException
	{
		out.writeBoolean(useSuggestedRange);
		out.writeObject(timeZone);
	}
	public void readExternal(final ObjectInput in) throws IOException, ClassNotFoundException
	{
		useSuggestedRange = in.readBoolean();
		timeZone = (TimeZone) in.readObject();
	}
/*
 * end externalization methods
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
	private TimeZone timeZone = TimeZone.getDefault();
	private final int majorTickLength = 5;
	private final int maxCharsPerLabel = 6;
	private final int minSpaceBetweenLabels = 3;
	private final int minNumberOfDivisions = 1;
	private long data_min=0, data_max=3600000; // actual min/max for the data set
	private long axis_min=0, axis_max=3600000; // min/max of the axis
	private DateLabel[] labels;
	private final JASCalendar calendar = new JASCalendar();
	private final SimpleDateFormat format = new SimpleDateFormat();
	private final Vector labelVector = new Vector();

	boolean useSuggestedRange = false;

	// scale index values
	private final int MILLISECONDS = 0;
	private final int SECONDS      = 1;
	private final int MINUTES      = 2;
	private final int HOURS        = 3;
	private final int DAYS         = 4;
	private final int MONTHS       = 5;
	private final int YEARS        = 6;
	final private static long[] scaleFactors = // used to determine an approximate scale
	{
		1L,                                  // index 0: milliseconds
		1000L,                               // index 1: seconds
		1000L * 60L,                         // index 2: minutes
		1000L * 60L * 60L,                   // index 3: hours
		1000L * 60L * 60L * 24L,             // index 4: days
		1000L * 60L * 60L * 24L * 30L,       // index 5: months
		1000L * 60L * 60L * 24L * 30L * 12L, // index 6: years
	};
	final private static int[] calendarFields = 
	{
		Calendar.MILLISECOND,
		Calendar.SECOND,
		Calendar.MINUTE,
		Calendar.HOUR_OF_DAY,
		Calendar.DATE,
		Calendar.MONTH,
		Calendar.YEAR
	};
	final private String[] normalTimeFormats = 
	{
		"s.SSS", // millisecond
		"s", // second
		"H:mm", // minute
		"H:mm", // hour
		"d", // day
		"MMM", // month
		"yyyy" // year
	};
	final private String[] horizontalAxisSecondLineFirstEntryTimeFormats = 
	{
		"MMM d, yyyy, H:mm", // millisecond
		"MMM d, yyyy, H:mm", // second
		"MMM d, yyyy", // minute
		"MMM d, yyyy", // hour
		"MMM, yyyy", // day
		"yyyy", // month
		null
	};
	final private String[] horizontalAxisSecondLineSubsequentEntryTimeFormats = 
	{
		"H:mm", // millisecond
		"H:mm", // second
		"MMM d", // minute
		"MMM d", // hour
		"MMM", // day
		"yyyy", // month
		null
	};
	final private String[] verticalAxisFirstEntryTimeFormats = 
	{
		"MMM d, yyyy, H:mm:ss.SSS", // millisecond
		"MMM d, yyyy, H:mm:ss", // second
		"MMM d, yyyy, H:mm", // minute
		"MMM d, yyyy, H:mm", // hour
		"MMM d, yyyy", // day
		"MMM, yyyy", // month
		"yyyy" // year
	};
	final private String[] verticalAxisChangedUnitsTimeFormatsPrefix = 
	// for labels on vertical axes, the normal formats are concatenated to these formats when the units change
	{
		"(H:mm) ", // millisecond
		"(H:mm) ", // second
		"MMM d, ", // minute
		"MMM d, ", // hour
		"MMM ", // day
		"(yyyy) ", // month
		null
	};
	final private int[] monthLengths =
	{
		31, // Jan
		28, // Feb
		31, // Mar
		30, // Apr
		31, // May
		30, // Jun
		31, // Jul
		31, // Aug
		30, // Sep
		31, // Oct
		30, // Nov
		31  // Dec
	};
	private final static class DateLabel extends AxisLabel
	{
		String subtext = null;
	}
	private final static class JASCalendar extends GregorianCalendar
	{
		// we convert a protected method to public
		public long getTimeInMillis()
		{
			return super.getTimeInMillis();
		}
		// we convert a protected method to public
		public void setTimeInMillis(final long time)
		{
			super.setTimeInMillis(time);
		}
	}
}
