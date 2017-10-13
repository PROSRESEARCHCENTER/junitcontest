package jas.plot;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
/**
 * Implements a simple numeric axis, with either linear or logarithmic scale.  Users
 * may set a flag that determines whether the axis may round to close values for simplicity
 * of labels or whether the axis should keep to the given range.  If, for example, the minimum
 * is set to 4.3 and the maximum is set to 95.9, this class may be configured to round the
 * extrema so that the labels are 0, 10, 20, ..., 100.  Use <code>setUseSuggestedRange(boolean)</code>
 * to set whether a pretty range is automatically used.
 *  @see #setUseSuggestedRange(boolean)
 *  @author Jonas Gifford
 */
public final class DoubleAxis extends AxisType implements DoubleCoordinateTransformation
{
/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * begin constructors
 */
	/** Creates linear axis. */
	public DoubleAxis()
	{
		this(false);
	}

	/**
	 * Creates a new numeric axis.
	 *  @param logarithmic whether the axis uses logarithmic (as opposed to liear) scale
	 */
	public DoubleAxis(final boolean logarithmic)
	{
		this.logarithmic = logarithmic;
	}
/*
 * end constructors
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * begin public interface
 */
	public void setLogarithmic(final boolean logarithmic)
	{
		if (this.logarithmic != logarithmic)
		{
			labelsValid = false; // old labels are useless now, so on next layout new ones will be created
			this.logarithmic = logarithmic;
			if (axis != null) axis.revalidate();
		}
	}
	public boolean isLogarithmic()
	{
		return logarithmic;
	}
	public void setUseSuggestedRange(final boolean useSuggestedRange)
	{
		if (this.useSuggestedRange != useSuggestedRange)
		{
			labelsValid = false; // old labels are useless now, so on next layout new ones will be created
			this.useSuggestedRange = useSuggestedRange;
			if (axis != null) axis.revalidate();
		}
	}
	public boolean getUseSuggestedRange()
	{
		return useSuggestedRange;
	}
	public void setMin(final double data_min)
	{
		if (this.data_min != data_min)
		{
			labelsValid = false; // old labels are useless now, so on next layout new ones will be created
			this.data_min = data_min;
			this.plot_min = data_min;
			if (axis != null) axis.revalidate();
		}
	}
	public void setMax(final double data_max)
	{
		if (this.data_max != data_max)
		{
			labelsValid = false; // old labels are useless now, so on next layout new ones will be created
			this.data_max = data_max;
			this.plot_max = data_max;
			if (axis != null) axis.revalidate();
		}
	}

	/** Returns the minimum of the axis range. */
	public double getPlotMin()
	{
		return plot_min;
	}

	/** Returns the maximum of the axis range. */
	public double getPlotMax()
	{
		return plot_max;
	}

	/**
	 * Returns the minimum of the data on the axis, as given
	 * to the method <code>setMin()</code>.
	 *  @see #setMin(double)
	 */
	public double getDataMin()
	{
		return data_min;
	}

	/**
	 * Returns the maximum of the data on the axis, as given
	 * to the method <code>setMax()</code>.
	 *  @see #setMax(double)
	 */
	public double getDataMax()
	{
		return data_max;
	}
/*
 * end public interface
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * begin private methods
 */
	private int stringWidth(final FontMetrics fm, final String s)
	{
		if (s.startsWith("e"))
			return fm.stringWidth(s) + fm.stringWidth("10") - fm.charWidth('e');
		else
			return fm.stringWidth(s);
	}
	private int round(final double d, final boolean down)
	/*
	 * Determines an integer value from a double by rounding intelligently.
	 * In the logarithmic case, when determining the order of magnitude
	 * of the lowest tick, the "down" parameter is false because
	 * we want to round up from the minimum point in the axis (so that
	 * the tick shows un on the range of the axis) if we are not very close
	 * to an integer value.  However, when we are determining the order
	 * of magnitude of the highest tick, we want to round down if we are
	 * not very close to an integer so that the tick appears within the
	 * range of the axis.  Similarly in the case for the linear axis, we round
	 * up to get the smallest tick value and we round down to get the largest
	 * tick value.  We do exactly the opposite if we are using the suggested
	 * range.  In that case, we round down to get the minimum and up to get
	 * the maximum.
	 */
	{
		final double minProximity = 0.0001;
		/*
		 * A parameter's proximity to the nearest integer must be this
		 * fraction of its size in order to be considered that value.
		 */
		final double round = Math.round(d); // the closest integer value
		if (d == round || Math.abs(d - round) < (d != 0.0 ? minProximity * Math.abs(d) : 0.000001))
		{
			// we assume here that d is close enough to be an integer, so we round and return
			return (int) round;
		}
		else
		{
			// d is not close enough to be an integer, so we return the
			// floor if we were supposed to round down and ceil
			// otherwise
			return down ? (int) Math.floor(d) : (int) Math.ceil(d);
		}
	}
	private boolean areEqual(double d1, double d2)
	{
		// returns whether they are close enough to be considered equal
		return d1 == d2 || Math.abs(d2 - d1) < Math.abs(d1 != 0.0 ? 0.0001 * d1 : 0.000001);
	}
	private int charsReq(int pow)
	/*
	 * Returns the number of characters required (not using scientific
	 * notation) to represent a decade of the given order of magnitude.
	 */
	{
		if (pow < 0)
			/*
			 * If the power is less than 0, we need one space for the
			 * leading zero, one for the decimal, and then -pow spaces
			 * for each of the following characters.  For example, if
			 * pow = -2 then the result would be 4, because there are
			 * four characters in the string "0.01".
			 */
			return -pow + 2;
		else
			/*
			 * If the power is 0 or greater, we need one character for
			 * the character '1' and one '0' for each order of magnitude.
			 */
			return  pow + 1;
	}
	private void createNewLabels(final int maxNumberOfDivisions)
	{
		labelsValid = true;

		// log_max is the logarithm of the max value of the data
		double log_max = data_max == 0d ? 0d : Math.log(Math.abs(data_max)) / log10;

		// int_log_max is the floored integer equivalent of log_max
		final int int_log_max = (int) Math.floor(log_max);

		// by default, scale_power is 0
		// This number represents the amount by which we scale all labels.  For example,
		// if our range is from 2,000,000,000 to 5,000,000,000 we want scale_power to be
		// 9 so that we get 2.0, 2.5, 3.0, ... or something like that on the labels.
		scale_power = 0;
		if (int_log_max >= maxCharsPerLabel)
			// we have an order of magnitude that can't be displayed in standard form, so we need to set
			// a value for scale_power
			scale_power = int_log_max;
		else if (int_log_max <= -maxCharsPerLabel)
			// we have an order of magnitude that can't be displayed in standard form, so we need to set
			// a value for scale_power
			scale_power = int_log_max;

		final DoubleNumberFormatter format = new DoubleNumberFormatter(scale_power);
		// the formatter uses scale_power when creating labels

		if (logarithmic)
		{
			// log_min is the logarithm of the min value of the data
			double log_min = data_min == 0d ? 0d : Math.log(Math.abs(data_min)) / log10;

			int min_int = round(log_min, useSuggestedRange);
			// min_int is the order of magnitude of the smallest major tick
			// If min is a multiple of 10, min_int will be the order of
			// magnitude of data_min.  Otherwise, it will be the next highest
			// order of magnitude.  However,  if we are using the
			// suggested range, we want the next lowest, not the next highest.

			int max_int = round(log_max, !useSuggestedRange);
			// max_int is the order of magnitude of the largest major tick.
			// If we are using the suggested range, we want the next highest,
			// not the next lowest

			final int nDecades = max_int - min_int + 1;
			// nDecades is the number of major ticks we have on the plot

			if (nDecades < (useSuggestedRange ? 3 : 2))
			// if we can't get at least two decade marks in the range we put
			// some ugly (but correct) labels on the axis spaced approximately evenly
			// we require at least 3 if the suggested range is used because it
			// rounds such that there are at least two by default and we want at least
			// one more
			{
				tryForNewLabelsOnExpansion = true;
				// when we resize, we may want new labels

				minorTickPositions = null; // no minor ticks
				final int pow = (int) Math.floor(Math.log(data_max - data_min) / log10) - (maxNumberOfDivisions > 40 ? 2 : 1);
				// we will divide numbers by this many factors of 10, round using
				// Math.floor(), and multiply again by this many factors of ten, thus
				// getting a series of numbers truncated to a fixed point

				if (scale_power > 0 || pow < 0)
					format.setFractionDigits(scale_power - pow);
				else
					// we don't need any fraction digits in out result
					format.setFractionDigits(0);

				final double mag = Math.pow(10.0, pow);
				// mag is the scaling factor (this is just to keep from having to recalculate each time)

				nDivisions = Math.max(maxNumberOfDivisions, 2);

				if (useSuggestedRange)
				{
					plot_min = Math.floor(data_min / mag) * mag;
					// we set a value for the plot_min which is rounded down from our data_min at the precision specified by mag

					plot_max = Math.ceil(data_max / mag) * mag;
					// we set a value for the plot_max which is rounded up from our data_max at the precision specified by mag

					// We want log_min and log_max to reflect extrema on the axis range, not for the data.  We have to update
					// these values to reflect a new range
					log_min = Math.log(plot_min) / log10;
					log_max = Math.log(plot_max) / log10;

					// create a new label array
					labels = new AxisLabel[nDivisions];
				}
				else
				{
					// because we're not using the suggested range, plot_min and plot_max are the same as data_min and data_max
					// (note that log_min and log_max are already correct)
					plot_min = data_min;
					plot_max = data_max;

					// lastLabel is a potential label value that we will include if it fits
					final double lastLabel = Math.ceil(Math.pow(10.0, log_max) / mag) * mag;
					if (lastLabel < data_max || areEqual(lastLabel, data_max))
						// if the last label is safely within the data range, we create an array
						// that will include that label
						labels = new AxisLabel[nDivisions];
					else
						// we create an array that will not include that last label
						labels = new AxisLabel[nDivisions - 1];
				}

				for (int i = 0; i < labels.length; i++)
				{
					double labelValue = Math.ceil(Math.pow(10.0, i * (log_max - log_min) / (nDivisions - 1) + log_min) / mag) * mag;
					// we multiply the number by mag, floor, then divide by mag

					labels[i] = new AxisLabel();
					labels[i].text = format.format(labelValue);
					labels[i].position = (Math.log(labelValue) / log10 - log_min) / (log_max - log_min);
				}
			}
			else
			{
				scale_power = 0; // reset any settings because we don't scale logs
				int nLabels = nDecades; // we will initially have exactly one label per decade
				                        // (we may decrease this if skip, defined below, increases)
				int skip = 1;
				// skip is the number of orders of magnitude between tick marks

				if (useSuggestedRange)
				{
					// max_int and min_int are the powers of ten for the axis range, so once cast to double, they
					// will do just fine as log_min and log_max, which are supposed to be the logs of the axis boundaries
					log_min = min_int;
					log_max = max_int;

					// the plot range is different from the data range:
					plot_min = Math.pow(10.0, min_int);
					plot_max = Math.pow(10.0, max_int);
				}
				else
				{
					// the plot range is equal to the data range
					plot_min = data_min;
					plot_max = data_max;
				}

				if (nDecades > maxNumberOfDivisions && maxNumberOfDivisions > 1)
				// we have a problem: we have more decades than we have room for ticks
				// time to set a good value for skip
				{
					// if the axis is extended, we may want new labels (i.e., to reflect a smaller skip) so...
					tryForNewLabelsOnExpansion = true;

					while (skip + 1 < nDecades && nDecades / skip >= maxNumberOfDivisions - 1)
						// we just increement skip until we find something that works
						skip++;
					if (skip != 1)
					{
						// we now have fewer labels
						nLabels = nDecades / skip;

						if (useSuggestedRange)
						// we have to round things to make the labels land exactly on the ends
						{
							// mod represents how much the axis min currently goes past the currently highest multiple of skip
							final int mod = (max_int - min_int) % skip;
							if (mod != 0)
							// we need to move the max up so that it is a multiple of skip
							{
								max_int += skip - mod;

								// now plot_max and log_max must be increased to reflect rounding up the max
								plot_max = Math.pow(10.0, max_int);
								log_max = max_int;
							}
							// by using the suggested range, we guarantee that there will be a label exactly on either end
							// this involved having (nDecades + 1) labels, so...
							nLabels++;
						}

						// For various reasons, we might not have enough labels, so...
						if (min_int + nLabels * skip <= max_int)
							// if we don't have enough, we add another
							nLabels++;
					}
				}
				else
					// We have no skip, and therefore no way of inserting new labels between the ones we have, so there is
					// no point in calculating new labels if we resize.  Therefore, ...
					tryForNewLabelsOnExpansion = false;

				nDivisions = nLabels;
				labels = new AxisLabel[nLabels];
				if (charsReq(min_int) > maxCharsPerLabel || charsReq(max_int) > maxCharsPerLabel)
				// if we can't fit the full label in, we use scientific notation
				{
					int power = min_int;
					for (int i = 0; i < nLabels; i++)
					{
						labels[i] = new AxisLabel();

						// the label text for 10^6 is "e6"
						// we therefore concatenate the power as a string to the string "e"
						labels[i].text = "e".concat(String.valueOf(power));
						labels[i].position = (power - log_min) / (log_max - log_min);
						power += skip;
					}
				}
				else
				// we are going to display the whole labels
				{
					if (min_int >= 0)
					// all our numbers are greater than or equal to 1, so all
					// we have to do is add zeros to one string buffer
					{
						// empty the buffer
						b.setLength(0);

						b.append('1');
						for (int j = 0; j < min_int; j++)
							b.append('0');
						// we now have a buffer initialized for the first label

						for (int i = 0; i < nLabels; i++)
						{
							labels[i] = new AxisLabel();
							labels[i].text = b.toString();
							labels[i].position = (i * skip + min_int - log_min) / (log_max - log_min);

							// now, we set it up for the next label
							for (int j = 0; j < skip; j++)
								b.append('0');
						}
					}
					else if (max_int < 0)
					// all of our labels are less than or equal to 0.1, so we can just insert '0' characters into a single buffer
					{
						// empty the buffer
						b.setLength(0);

						// initialize the buffer
						b.append("0.1");
						for (int i = max_int; i < -1; i++)
							b.insert(2, '0'); // for each order less than -1 we insert a '0' after the '.'

						for (int i = nLabels - 1; i >= 0; i--)
						{
							labels[i] = new AxisLabel();
							labels[i].text = b.toString();
							labels[i].position = (i * skip + min_int - log_min) / (log_max - log_min);

							// set up for the next label
							for (int j = 0; j < skip; j++)
								b.insert(2, '0'); // insert another '0' for each skip
						}
					}
					else
					// this isn't so simple because we can't just use one string
					// buffer for all of the labels
					{
						int power = min_int;
						// this is the order of magnitude of the current label
						for (int i = 0; i < nLabels; i++)
						{
							b.setLength(0);
							if (power < 0)
							// we have to create a decimal number
							{
								b.append("0.");
								for (int j = -1; j > power; j--)
									b.append('0');
								// for every order of magnitude less than -1 we
								// add a zero

								b.append('1');
							}
							else
							// our number is 1 or greater
							{
								b.append('1');
								for (int j = 0; j < power; j++)
									b.append('0');
								// for every order of magnitude we add a zero
							}

							labels[i] = new AxisLabel();
							labels[i].text = b.toString();
							labels[i].position = (power - log_min) / (log_max - log_min);

							// increment power for the next label
							power += skip;
						}
					}
				}

				// create minor ticks
				if (skip == 1)
				// our task is to create 8 minor ticks between each decade mark
				{
					double minorTickBase = areEqual(min_int, log_min) ? plot_min : Math.pow(10.0, min_int - 1);
					// tickMarkBase is the multiple of ten that the minor ticks themselves are multiples of.
					// Between two decades marks, the eight minor ticks are at:
					//   2 * tickMarkBase, 3 * tickMarkBase, 4 * tickMarkBase, 5 * tickMarkBase,
					//   6 * tickMarkBase, 7 * tickMarkBase, 8 * tickMarkBase, 9 * tickMarkBase
					// and tickMarkBase is the value of the smaller of the surrounding decade marks.
					// Therefore, between the marks 100 and 1000, we use the eight multiples above times
					// the smaller decade (100) to get the following minor tick values:
					//   200, 300, 400, 500, 600, 700, 800, 900
					// We calculate the initial value of minorTickBase by taking 10 to the power of (min_int - 1)
					// if the min_int is not equal to the log_min (i.e., if the axis minimum is not a multiple of 10)
					// and plot_min is the axis minimum is a multiple of 10.  When we go to the next decade to put the
					// marks there, we will just multiply minorTickBase by 10 to get the next value.

					final double initialIndex = plot_min / minorTickBase;
					final double initialIndex_ceil = Math.ceil(initialIndex);
					int index = initialIndex_ceil > initialIndex ? (int) initialIndex_ceil : (int) initialIndex_ceil + 1;
					// The code above merely calculates the initial value for index.  index is the multiple of minorTick
					// base for a given minor tick.  It rolls within the inclusive range 2:9.

					int nMinorTicks = (max_int - min_int) * 8;
					// so far we have 8 minor ticks for each decade interval

					if (! useSuggestedRange)
					// we may have extras because there is space after the last decade and befer the first
					// where maybe we will need minor ticks
					{
						// increase nMinorTicks by the number that are needed below the first decade mark
						nMinorTicks += (int) ((Math.pow(10.0, min_int) - data_min) / Math.pow(10.0, min_int - 1));

						// increase nMinorTicks by the number that are needed after the last decade mark
						nMinorTicks += (int) ((data_max - Math.pow(10.0, max_int)) / Math.pow(10.0, max_int));
					}
					minorTickPositions = new double[nMinorTicks];
					for (int i = 0; i < nMinorTicks; i++)
					{
						if (index > 9)
						// we have come to the end of one decade interval, so...
						{
							// we restart index at 2 and...
							index = 2;

							// increase minorTickBase for the new decade
							minorTickBase *= 10.0;
						}
						// we assign the position of this minor tick
						minorTickPositions[i] = (Math.log(index * minorTickBase) / log10 - log_min) / (log_max - log_min);

						// we increment index for the next tick
						index++;
					}
				}
				else
				// We have a skip other than 1, so we will try to put minor ticks on decade evenly spaced
				// between the labels (major ticks).
				{
					minorTickPositions = null; // initially null; we'll see if we can get a nice pattern
					for (int nMinorDivisions = Math.min(6 /* max */, skip); nMinorDivisions > 1; nMinorDivisions--)
					{
						if (skip % nMinorDivisions != 0) // this number won't work, so we...
							continue;

						// By this point we know we have a number that will work.  We assign an array to
						// minorTickPositions (which was previously null) and we break from the loop.

						// minorSkip is the number of decades between minor ticks
						final int minorSkip = skip / nMinorDivisions;

						// nMinorTicks is the number of minor ticks
						int nMinorTicks = (nMinorDivisions - 1) * (max_int - min_int) / skip;

						// tickPower is the power of ten for the current tick
						// we have set it here to the value for the first tick
						int tickPower = min_int + minorSkip;

						minorTickPositions = new double[nMinorTicks];
						for (int i = 0; i < nMinorTicks; i++)
						{
							if ((tickPower - min_int) % skip == 0)
								// we have actually landed on a major tick, so we skip over to the next minorTick value
								tickPower += minorSkip;
							minorTickPositions[i] = (tickPower - log_min) / (log_max - log_min);
							tickPower += minorSkip;
						}
						break;
					}
				}
			}
		}
		else
		{
			/*
			 * Our strategy here is based on the observation that plotting the range
			 * 0.2 to 40 is very similar to the task of plotting the ranges 2 to 400,
			 * or 0.02 to 4.  We scale the min and max by an order of ten such that when
			 * converted to integers (by a process not quite like trucation) the difference
			 * between those two integers is a number greater than or equal to 10 and less
			 * than (but not equal to) 100.  In other words, there will be a difference of
			 * exactly two digits, which is a sensible precision to see in variation between
			 * axis labels.  For example, the ranges listed above would all yield the integer
			 * pair 1 to 40 (because the difference between those integers has two digits).
			 * Given a pair of integers, the function proceeds to calculate appropriate labels.
			 * If we are using the suggested range, we just round the min down to the next nice
			 * label and we round the max up to the next nice label (unless the max and min are
			 * already on nice labels).
			 */
			tryForNewLabelsOnExpansion = true;
			minorTickPositions = null; // no minor ticks

			final double difference = data_max - data_min;
			final double pow = Math.floor(Math.log(difference) / log10) - 1.0;
			// pow is the power of 10 used to get integers of the appropriate range

			int fractDigits = 0;
			if (scale_power > 0)
				fractDigits = scale_power - (int) pow;
			else if (pow < -0.5)
				// we use -0.5 instead of 0.0 in case a Math.floor() returns something that should be 0.0
				// but is really just barely under 0.0
				fractDigits = scale_power - (int) pow;

			final double conversion = Math.pow(10.0, pow);
			// this is the actual conversion factor we used, stored once to keep from
			// having to recalculate it

			int intMin = round(data_min / conversion, useSuggestedRange);
			int intMax = round(data_max / conversion, !useSuggestedRange);
			// we now have intMin and intMax: the integer pair with a two-digit difference
			
			if (useSuggestedRange)
			{
				plot_min = intMin * conversion;
				plot_max = intMax * conversion;
			}
			else
			{
				plot_min = data_min;
				plot_max = data_max;
			}

			final int naturalNumberOfDivisions = intMax - intMin;
			// this number has precisely two digits

			int nDivisions;

			final float idealMinFraction = 0.5f;
			// we will allow as few as this fraction of the maximum number of labels if it is convenient

			int nUnits = 1;
			// this number can represent two things:
			//  a) if naturalNumberOfDivisions < maxNumberOfDivisions
			//     it represents the number of divisions (units) between the natural divisions
			//  b) if naturalNumberOfDivisions > maxNumberOfDivisions
			//     it represents the number of units between divisions (the number to skip between divisions)
			// 1 is the default value, but we will see if a different value might be better

			if (naturalNumberOfDivisions < maxNumberOfDivisions)
			// we might like to put in some new divisions
			{
				final float proximity = (float) naturalNumberOfDivisions / (float) maxNumberOfDivisions;
				// this number measures how close the natural number is to the maximum

				boolean niceDivisionFound = false;
				if (proximity < idealMinFraction)
				// we want to do something because the number we have is below the range we want
				{
					final int[] divisions = {2, 4, 5, 10, 20};
					// These are the numbers of subdivisions we might want to place between natural divisions.
					// The array only goes up to 20 because we would need a plot with at least 200 labels before
					// needing to go any higher.
					for (int i = 0; i < divisions.length; i++)
					{
						final int candidate = divisions[i];
						if (proximity * candidate <= 1.0)
						// this might work, because the number is small enough that we could fit
						// this many divisions on the axis
						{
							niceDivisionFound = true;
							nUnits = candidate;

							// the next iteration might be even better, so we...
							continue;
						}

						// if we didn't execute continue, it was because we can't fit this many
						// divisions on the axis, and so there's no point in trying the next
						// iteration either because it's even bigger, so we break out of the loop
						break;
					}
				}
				if (niceDivisionFound)
				{
					if (useSuggestedRange)
					{
						nDivisions = naturalNumberOfDivisions * nUnits;
					}
					else
					{
						nDivisions = naturalNumberOfDivisions * nUnits + (int) ((plot_max / conversion - intMax) * nUnits);
						/*
						 * The first term in the expression isn't tough: If nUnits is 2 then we need twice as many
						 * divisions on the axis.  The second term isn't so obvious.  Suppose our axis goes from 0
						 * to 12.7 and we decide to set nUnits to 2.  We therefore get labels 0.0, 0.5, 1.0, ... , 12.0
						 * but we won't get 12.5 on there.  There will instead be empty space where the 12.5 should go.
						 * The last term accounts for this, by taking the difference between the top label and the
						 * axis max (in this case 12.7 - 12.0 = 0.7), multiplying bn nUnits (to get 1.4 in this case)
						 * and truncating (to get 1 in this case).  The result (1 in this case) is the number of labels
						 * extra we need.
						 */
					}

					if (pow < 0.5 || scale_power > 0)
					// we use 0.5 instead of 1.0 in case a Math.floor() returns something
					// that should be 1.0 but is really just barely under 1.0
						fractDigits++;
						// we've gone down to one lover decimal level so we have to tell the formatter

					if ((nUnits == 4 || nUnits == 20) && (pow < 1.5 || scale_power > 0)) // we've actually gone down two decimal levels, so...
						// we use 1.5 instead of 2.0 in case a Math.floor() returns something
						// that should be 2.0 but is really just barely under 2.0
						fractDigits++; // we add another
				}
				else
				// we give up and keep the natural number, even though it's smaller that what we'd like
				{
					nDivisions = Math.max(naturalNumberOfDivisions, minNumberOfDivisions);
				}
			}
			else if (naturalNumberOfDivisions > maxNumberOfDivisions)
			// the natural number is larger than what we'd like, so we have to skip over some
			// (typically this is the more common problem)
			{
				nDivisions = 1;
				// we supply this initialization to make the compiler happy, but in the algorithm
				// requires that this initial value change

				final int[] skips = {2, 5, 10, 20, 25, 50};
				// These are the numbers of natural divisions we're going to try skipping.

				for (int i = 0; i < skips.length; i++)
				{
					final int nDivisionsThisTry = naturalNumberOfDivisions / skips[i];
					if (nDivisionsThisTry > maxNumberOfDivisions)
					{
						// this many skips isn't big enough, so we'll try the next one
						continue;
					}

					// we now assign to nUnits the number of natural divisions to skip over
					nUnits = skips[i];
					nDivisions = nDivisionsThisTry;
					if (useSuggestedRange)
					{
						// changed is a flag that indicates if the number of divisions has changed
						boolean changed = false;

						if (intMin % nUnits != 0)
						// intMin is not a multiple of nUnits, so we...
						{
							// decrease it so that it is a multiple,
							intMin -= intMin % nUnits;

							// adjust plot_min accordingly, and
							plot_min = intMin * conversion;

							// set the flag that the range has changed
							changed = true;
						}
						if (intMax % nUnits != 0)
						// intMax is not a multiple of nUnits, so we...
						{
							// increase it so that it is a multiple,
							intMax += nUnits - intMax % nUnits;

							// adjust plot_max accordingly, and
							plot_max = intMax * conversion;

							// set the flag that the range has changed
							changed = true;
						}
						if (changed)
						// we need to recalculate the number of divisions
						{
							nDivisions = (intMax - intMin) / nUnits;
						}
					}

					if (nUnits >= 10 && nUnits != 25 && fractDigits > 0)
						// we're skipping at least an order of 10, and we're not in quarters, so...
						fractDigits--; // we can get rid of one fraction digit

					/*
					 * We may calculate a new value for the intMin.  Consider, for example,
					 * the range 3 to 17.  If we decide that our skip will be 2, we will get
					 * labels like 3, 5, 7, 9, etc.  This will look dumb because we would much rather
					 * have the first label a nice multiple of our skip (i.e., we would rather
					 * have 2, 4, 6, 8, etc., or for multiples of 5 we would rather have 20,
					 * 25, 30, 35, etc. over 18, 23, 28, 33, etc.)  Therefore, if the intMin is not
					 * a nice multiple of that skip then we increase the intMin, and we may have
					 * to decrement nDivisions because of a lost label.
					 */
					if (intMin % nUnits != 0)
					// only true if we are not using the suggested range
					{
						// increase is the amount we will increase intMin by to make it a nice multiple of nUnits
						final int increase = intMin > 0 ? nUnits - intMin % nUnits : -intMin % nUnits;

						if (increase > intMax - (intMax - intMin) / nUnits * nUnits - intMin)
							// we have put the last label over the limit, so...
							nDivisions--; // we drop the last label

						intMin += increase;
					}

					// We are happy with what we've got because it gives us an acceptable
					// number of divisions.  We don't want to go any higher because that
					// will just make for fewer divisions, so we...
					break;
				}
			}
			else // hey! they're exactly equal
				nDivisions = Math.max(naturalNumberOfDivisions, minNumberOfDivisions);

			double minLabelValue = intMin * conversion;
			final double inc = naturalNumberOfDivisions < maxNumberOfDivisions ? conversion / nUnits : conversion * nUnits;
			if (naturalNumberOfDivisions < maxNumberOfDivisions && minLabelValue - inc >= plot_min)
			// this happens if we are dividing up divisions, and we get divisions below intMin * conversion
			{
				int nLost = (int) ((minLabelValue - plot_min) / inc);
				minLabelValue -= nLost * inc;
				nDivisions += nLost;
			}
                        if (nDivisions < 0) nDivisions = 0;
                        if (nDivisions > maxNumberOfDivisions) nDivisions = maxNumberOfDivisions;
			labels = new AxisLabel[nDivisions + 1];
			this.nDivisions = nDivisions;
			format.setFractionDigits(fractDigits);
			for (int j = 0; j < labels.length; j++)
			{
				final double labelValue = minLabelValue + j * inc;
				// this method of finding labelValue is more expensive than the method in the
				// previous version (i.e., the last version checked in to source safe) but this method
				// avoids some miniscule rounding problems we had in the previous version

				labels[j] = new AxisLabel();
				labels[j].text = format.format(labelValue);
				labels[j].position = (labelValue - plot_min) / (plot_max - plot_min);
			}
		}
	}
	private int calculateMaxNDivisions(final FontMetrics fm, final int axisLength)
	{
		int result;
		if (axis.getAxisOrientation() == Axis.HORIZONTAL)
			result = axisLength / (fm.charWidth('5') * maxCharsPerLabel + minSpaceBetweenLabels);
			// assume '5' has typical width
		else
			result = axisLength / (fm.getHeight() + minSpaceBetweenLabels);
		return Math.max(minNumberOfDivisions, result);
	}
	private int getPowerOffset(final FontMetrics fm)
	// returns how far the term indicating power is below the regular labels (horizontal axis only)
	{
		// kind of an arbitrary return value (we just want some number that relates to font size)
		return fm.getAscent() / 2;
	}
/*
 * end private methods
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * begin AxisType methods
 */
	void assumeAxisLength(final int axisLength)
	{
      Font font = axis.getFont();
		final FontMetrics fm = axis.getToolkit().getFontMetrics(font);
		final int maxNumberOfDivisions = calculateMaxNDivisions(fm, axisLength);

		if (!labelsValid || labels == null || nDivisions < 2 || nDivisions > maxNumberOfDivisions ||
			tryForNewLabelsOnExpansion && nDivisions < maxNumberOfDivisions / 2)
		{
			createNewLabels(maxNumberOfDivisions);
		}

		// we are going to indicate that the minor ticks should be hidden if there are minor ticks and
		// the number of divisions is high enough that the ticks would be crammed too close together
		hideMinorTicks = minorTickPositions == null || nDivisions > maxNumberOfDivisions / 3;

		final int lastLabelLocation = (int) ((1.0 - labels[labels.length - 1].position) * axisLength);

		if (axis.getAxisOrientation() == Axis.VERTICAL)
		{
			spaceRequirements.width = longestStringLength(fm,labels) + Axis.padFromAxis;
			spaceRequirements.flowPastEnd = fm.getMaxAscent() - fm.getAscent() / 2 - lastLabelLocation;
			if (logarithmic && labels[0].text.startsWith("e"))
			{
				spaceRequirements.width += fm.stringWidth("10") - fm.charWidth('e');
				spaceRequirements.flowPastEnd += fm.getHeight() / 2;
			}
			if (scale_power != 0)
			{
				spaceRequirements.width = Math.max(fm.stringWidth("x10") + fm.stringWidth(String.valueOf(scale_power)),
					spaceRequirements.width);
				spaceRequirements.flowPastEnd += minSpaceBetweenLabels;
				if (spaceRequirements.flowPastEnd < 0) spaceRequirements.flowPastEnd = 0;
				spaceRequirements.flowPastEnd += fm.getAscent();
			}
			else if (spaceRequirements.flowPastEnd < 0)
				spaceRequirements.flowPastEnd = 0;
			final int lineHeight = isLogarithmic() && labels[0].text.startsWith("e") ?
				fm.getHeight() / 2 + fm.getAscent() : fm.getAscent();
			spaceRequirements.height = Math.max(lineHeight / 2 - (int) (labels[0].position * axisLength), 0);
		}
		else
		{
			spaceRequirements.width = Math.max(stringWidth(fm, labels[0].text) / 2 - (int) (labels[0].position * axisLength), 0);
			spaceRequirements.flowPastEnd = stringWidth(fm, labels[labels.length - 1].text) / 2 - lastLabelLocation;
			final int lineHeight = isLogarithmic() && labels[0].text.startsWith("e") ?
				fm.getHeight() / 2 + fm.getMaxAscent() + fm.getDescent() : fm.getMaxAscent() + fm.getDescent();
			spaceRequirements.height = lineHeight + Axis.padFromAxis;
			if (scale_power != 0)
			{
				spaceRequirements.height = Math.max(fm.getHeight() / 2 + fm.getAscent() + getPowerOffset(fm),
					spaceRequirements.height);
				spaceRequirements.flowPastEnd += minSpaceBetweenLabels;
				if (spaceRequirements.flowPastEnd < 0) spaceRequirements.flowPastEnd = 0;
				spaceRequirements.flowPastEnd += fm.stringWidth("x10") + fm.stringWidth(String.valueOf(scale_power));
			}
			else if (spaceRequirements.flowPastEnd < 0)
				spaceRequirements.flowPastEnd = 0;
		}
	}

	/**
	 * Returns an instance of <code>DoubleCoordinateTransformation</code>.
	 *  @see DoubleCoordinateTransformation
	 */
	public CoordinateTransformation getCoordinateTransformation()
	{
		return this;
	}
	void paintAxis(final PlotGraphics g, final double originX, final double originY,
		final double length, final Color textColor,
		final Color majorTickColor, final Color minorTickColor)
	{
		final boolean isHorizontal = axis.getAxisOrientation() == Axis.HORIZONTAL;
		final boolean onLeft = (!isHorizontal) && axis.onLeftSide;
		final double minL = isHorizontal ? originX : originY;
		final double maxL = isHorizontal ? originX + length : originY - length;
		final FontMetrics fm = g.getFontMetrics();
		final int lineHeight = fm.getHeight();
		final int ascent = fm.getAscent();
		final int maxAscent = fm.getMaxAscent();
                double lastLabel = 0;
		if (labels == null) return;
		if (logarithmic)
		{
			final boolean isInExp = labels[0].text.startsWith("e");
			for (int i = 0; i < labels.length; i++)
			{
				String text = labels[i].text;
				String pow = null;
				double textLength;
				if (isInExp)
				{
					pow = text.substring(1);
					text = "10";
					textLength = fm.stringWidth(text) + fm.stringWidth(pow);
				}
				else
				{
					textLength = fm.stringWidth(text);
				}
				double pos = minL + (int) (labels[i].position * (maxL - minL));
				if (isHorizontal)
				{
					g.setColor(majorTickColor);
					g.drawLine(pos, originY + majorTickLength, pos, originY - majorTickLength);
					double y = originY + maxAscent + Axis.padFromAxis;
					pos -= textLength / 2;
					g.setColor(textColor);
					if (isInExp)
					{
						g.drawString(pow, pos + fm.stringWidth(text), y);
						y += lineHeight / 2;
					}
					g.drawString(text, pos, y);
				}
				else
				{
					g.setColor(majorTickColor);
					g.drawLine(originX - majorTickLength, pos, originX + majorTickLength, pos);
					final double x = onLeft ? originX - textLength - Axis.padFromAxis : originX + Axis.padFromAxis;
					pos += ascent / 2;
					g.setColor(textColor);
					if (isInExp)
					{
						// the "10" is centered in the tick mark
						// the exponent is higher by lineHeight / 2
						g.drawString(pow, x + fm.stringWidth(text), pos - lineHeight / 2);
					}
					g.drawString(text, x, pos);
				}
			}
		}
		else
		{
			for (int i = 0; i < labels.length; i++)
			{
				final String text = labels[i].text;
				final double pos = minL + labels[i].position * (maxL - minL);

				if (isHorizontal)
				{
					g.setColor(majorTickColor);
					g.drawLine(pos, originY + majorTickLength, pos, originY - majorTickLength);
					final double y = originY + maxAscent + Axis.padFromAxis;
					g.setColor(textColor);
					g.drawString(text, pos - fm.stringWidth(text) / 2, y);
				}
				else
				{
					g.setColor(majorTickColor);
					g.drawLine(originX - majorTickLength, pos, originX + majorTickLength, pos);
					final double x = onLeft ? originX - fm.stringWidth(text) - Axis.padFromAxis : originX + Axis.padFromAxis;
					g.setColor(textColor);
					g.drawString(text, x, pos + ascent/2);
				}
			}
                        lastLabel = minL + labels[labels.length-1].position*(maxL-minL);
		}

		if (scale_power != 0)
		{
			g.setColor(textColor);
			final String s = String.valueOf(scale_power);
			if (isHorizontal)
                        {                            
				final double y = originY + ascent + getPowerOffset(fm) + 2;
				final double x = axis.getSize().width - Axis.padAroundEdge - fm.stringWidth(s) + spaceRequirements.flowPastEnd;
				g.drawString("x10", x - fm.stringWidth("x10"), y + lineHeight / 2);
				g.drawString(s, x, y + lineHeight / 5);
//				g.drawString(s, x, y);
			}
			else
			{
                            
//				final double y = ascent + Axis.padAroundEdge;
//                            final double y1 = originY - length - lineHeight;
                            final double y = lineHeight/2;
				final double x = onLeft ? originX - fm.stringWidth(s) :  originX;
				g.drawString("x10", onLeft ? x - fm.stringWidth("x10") : x, y + lineHeight / 2);
				g.drawString(s, onLeft ? x : x + fm.stringWidth("x10"), y + lineHeight / 5);
//				g.drawString(s, onLeft ? x : x + fm.stringWidth("x10"), y);
                        }
                            
		}
		if (!hideMinorTicks && minorTickPositions != null && minorTickPositions.length != 0)
		{
			g.setColor(minorTickColor);
			if (isHorizontal)
			{
				for (int i = 0; i < minorTickPositions.length; i++)
				{
					final double x = minL + (int) (minorTickPositions[i] * (maxL - minL));
					g.drawLine(x, originY + minorTickLength, x, originY - minorTickLength);
				}
			}
			else
			{
				for (int i = 0; i < minorTickPositions.length; i++)
				{
					final double y = minL + (int) (minorTickPositions[i] * (maxL - minL));
					g.drawLine(originX - minorTickLength, y, originX + minorTickLength,y);
				}
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
	// Convert between axis coordinate (double) and drawing coordinates (pixels);
	public double convert(double d)
	{
		final int minL = axis.getMinLocation();
		final int maxL = axis.getMaxLocation();
		if (logarithmic)
		{
			final double min = Math.log(plot_min) / log10;
			final double max = Math.log(plot_max) / log10;
			
			if(d > 0)
				d = Math.log(d) / log10;
			else
				d = min;
			
			final double f = (d - min) / (max - min);
			return minL + f*(maxL - minL);
		}
		else
		{
			final double f = (d - plot_min) / (plot_max - plot_min);
			return minL + f*(maxL - minL);
		}
	}
	public double unConvert(double d)
	{
		final int minL = axis.getMinLocation();
		final int maxL = axis.getMaxLocation();
		final double f = (d - minL) / (maxL - minL);
		if (logarithmic)
		{
			final double min = Math.log(plot_min) / log10;
			final double max = Math.log(plot_max) / log10;

			return plot_min + f*(max - min);
		}
		else
		{
			return plot_min + f*(plot_max - plot_min);
		}
	}
	public double getIntersection()
	{
		return plot_min;
	}
/*
 * end coordinate transformation methods
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * begin externalization methods
 */
	public void writeExternal(final ObjectOutput out) throws IOException
	{
		out.writeBoolean(logarithmic);
		out.writeBoolean(useSuggestedRange);
	}
	public void readExternal(final ObjectInput in) throws IOException
	{
		logarithmic = in.readBoolean();
		useSuggestedRange = in.readBoolean();
	}
/*
 * end externalization methods
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * begin private member fields
 */
	/* CONSTANTS */
	private final StringBuffer b = new StringBuffer();
	private final double log10 = Math.log(10.0);
	private final int majorTickLength = 5;
	private final int minorTickLength = 3;
	private final int maxCharsPerLabel = 5;
	private final int minSpaceBetweenLabels = 3;
	private final int minNumberOfDivisions = 1;

	/* VARIABLES */
	private int nDivisions = 0;
	private double data_min = 0d, data_max = 1d; // actual min/max for the data set
	private double plot_min = 0d, plot_max = 1d; // min/max on the axis itself
	private AxisLabel[] labels;
	private double[] minorTickPositions;
	private boolean logarithmic;
	private int scale_power;
	private boolean useSuggestedRange = false;
	private boolean tryForNewLabelsOnExpansion; // This will only be false if we have a logarithmic axis where all the decades are showing.
											    // Otherwise, such an axis will recalculate new labels each time it is expanded.
	private boolean hideMinorTicks = false;
/*
 * end private member fields
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
}
