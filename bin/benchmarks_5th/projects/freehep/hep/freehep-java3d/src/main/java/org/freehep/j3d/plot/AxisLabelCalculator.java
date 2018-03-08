package org.freehep.j3d.plot;

import java.io.*;
/**
 * @author Joy Kyriakopulos (joyk@fnal.gov)
 * @version $Id: AxisLabelCalculator.java 8584 2006-08-10 23:06:37Z duns $
 */
public class AxisLabelCalculator
{

    /* VARIABLES */
    private double data_min = 0d, data_max = 1d; // actual min/max for the data set
    private double plot_min = 0d, plot_max = 1d; // min/max on the axis itself
    private AxisLabel[] labels;
    private int nDivisions = 0;
    private boolean labelsValid;

    public void createNewLabels(double min, double max)
    {
    	data_min = min;
    	data_max = max;
    	int minNumberOfDivisions = 1;
    	int maxNumberOfDivisions = 10;
    	double log10 = Math.log(10.0);
    	int maxCharsPerLabel = 5;
        labelsValid = true;

        // log_max is the logarithm of the max value of the data
        double log_max = data_max == 0d ? 0d : Math.log(Math.abs(data_max)) / log10;

        // int_log_max is the floored integer equivalent of log_max
        final int int_log_max = (int) Math.floor(log_max);

        // by default, scale_power is 0
        // This number represents the amount by which we scale all labels.  For example,
        // if our range is from 2,000,000,000 to 5,000,000,000 we want scale_power to be
        // 9 so that we get 2.0, 2.5, 3.0, ... or something like that on the labels.
        int scale_power = 0;
        if (int_log_max >= maxCharsPerLabel)
            // we have an order of magnitude that can't be displayed in standard form, so we need to set
            // a value for scale_power
            scale_power = int_log_max;
        else if (int_log_max <= -maxCharsPerLabel)
            // we have an order of magnitude that can't be displayed in standard form, so we need to set
            // a value for scale_power
            scale_power = int_log_max;

        // System.out.println();
        // System.out.println("int_log_max = " + int_log_max + ", maxCharsPerLabel = " + maxCharsPerLabel + ", scale_power = " + scale_power);

        final DoubleNumberFormatter format = new DoubleNumberFormatter(scale_power);
        // the formatter uses scale_power when creating labels

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

        int intMin = round(data_min / conversion, false);
        int intMax = round(data_max / conversion, true);
        // we now have intMin and intMax: the integer pair with a two-digit difference

        plot_min = data_min;
        plot_max = data_max;

        final int naturalNumberOfDivisions = intMax - intMin;
        // this number has precisely two digits

        int nDivisions = 0;

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
            int nLost = (int) ((minLabelValue - inc) / inc);
            minLabelValue -= nLost * inc;
            nDivisions += nLost;
        }
        labels = new AxisLabel[nDivisions + 1];
        this.nDivisions = nDivisions;

        // System.out.println("fractDigits = " + fractDigits + ", minLabelValue = " + minLabelValue + ", inc = " + inc);

        format.setFractionDigits(fractDigits);
        for (int j = 0; j < labels.length; j++)
        {
            final double labelValue = minLabelValue + j * inc;

            labels[j] = new AxisLabel();
            labels[j].text = format.format(labelValue);
            labels[j].position = (labelValue - plot_min) / (plot_max - plot_min);
            // labels[j].logPosition = Math.log(labels[j].position + Math.E);
            if (labels[j].position < 0.) labels[j].position = 0.;
        }
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

    private int round(final double d, final boolean down)
    /*
     * Determines an integer value from a double by rounding intelligently.
     * In the logarithmic case, when determining the order of magnitude
     * of the lowest tick, the "down" parameter is false because
     * we want to round up from the minimum point in the axis (so that
     * the tick shows up on the range of the axis) if we are not very close
     * to an integer value.  However, when we are determining the order
     * of magnitude of the highest tick, we want to round down if we are
     * not very close to an integer so that the tick appears within the
     * range of the axis.  Similarly in the case for the linear axis, we round
     * up to get the smallest tick value and we round down to get the largest
     * tick value.  We do exactly the opposite if wew are using the suggested
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

	public String [] getLabels()
	{
		int len = labels.length;
		String [] lab = new String [len];
		for (int i = 0; i < len; ++i)
			lab[i] = labels[i].text;
		return lab;
	}

	public double [] getPositions()
	{
		int len = labels.length;
		double [] pos = new double [len];
		for (int i = 0; i < len; ++i)
			pos[i] = labels[i].position;
		return pos;
	}

    public void printLabels()
    {
 	 System.out.println("data_min = " + data_min + ", data_max = " + data_max);
	 System.out.println("plot_min = " + plot_min + ", plot_max = " + plot_max);
	 System.out.println("nDivisions = " + nDivisions + ", labelsValid = " + labelsValid);

	 int i;

	 for (i = 0; i < labels.length; ++i)
	 {
	 	System.out.println("label "+ (i+1) + ": " + labels[i].text + "  position: " + labels[i].position);
	 }
    }



   // This class is used when storing information about
   // the labels on the axis.
   private class AxisLabel
   {
	   String text;
	   double position;
           // double logPosition;
   }

   /**
    * Just for testing
    */
   public static void main(String[] argv)
   {
       AxisLabelCalculator t = new AxisLabelCalculator();
       t.createNewLabels(0., 1.);
       t.printLabels();
       t.createNewLabels(0., 100.);
       t.printLabels();
       t.createNewLabels(0.0001, 0.001);
       t.printLabels();
       t.createNewLabels(5., 10.);
       t.printLabels();
       t.createNewLabels(.001, .050);
       t.printLabels();
       t.createNewLabels(.00001, .00005);
       t.printLabels();
       t.createNewLabels(-.001, .00005);
       t.printLabels();
       t.createNewLabels(-.001, .005);
       t.printLabels();
       t.createNewLabels(-2., 5.);
       t.printLabels();
       t.createNewLabels(.05, 10.);
       t.printLabels();

    }

}
