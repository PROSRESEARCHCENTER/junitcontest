// Charles A. Loomis, Jr., and University of California, Santa Cruz,
// Copyright (c) 2000
package org.freehep.swing.graphics;

import java.awt.geom.GeneralPath;

/**
 * This class contains static methods which are useful for the
 * ScaleBorder class and any potential subclasses. 
 *
 * @author Charles Loomis
 * @version $Id: Scale.java 8584 2006-08-10 23:06:37Z duns $ */
public class Scale {

    private static int[] p = {2,2,5,5,5,10,10,10,20,20,50,50};
    private static int[] ps = {2,10,5,10,20,10,20,50,20,100,50,100};

    final static public int LEFT_TICKS = 0;
    final static public int RIGHT_TICKS = 1;
    final static public int BOTH_TICKS = 2;

    /**
     * The size in pixels of the primary tick marks.  This is a global
     * property for all scales. */
    protected static float primaryTickSize = 8.f;

    /**
     * The size in pixels of the secondary tick marks.  This is a
     * global property for all scales. */
    protected static float secondaryTickSize = 5.f;

    /**
     * Set the tick sizes (in pixels) for the primary and secondary
     * tick marks. */
    static public void setTickSizes(float primaryTickSize,
                                    float secondaryTickSize) {
        Scale.primaryTickSize = primaryTickSize;
        Scale.secondaryTickSize = secondaryTickSize;
    }

    /**
     * Get the primary tick size (in pixels). */
    static public float getPrimaryTickSize() {
        return Scale.primaryTickSize;
    }

    /**
     * Get the secondary tick size (in pixels). */
    static public float getSecondaryTickSize() {
        return Scale.secondaryTickSize;
    }

    /**
     * A utility method to add a tick mark to the given GeneralPath at
     * the given position. */
    static private void addTickMark(GeneralPath gp, 
                                    int location,
                                    float tickPosition,
                                    float tickLength) {

        switch (location) {

        case (Scale.RIGHT_TICKS): 
            gp.moveTo(tickPosition,0.f);
            gp.lineTo(tickPosition,tickLength);
            break;
        case (Scale.LEFT_TICKS):
            gp.moveTo(tickPosition,0.f);
            gp.lineTo(tickPosition,-tickLength);
            break;
        case (Scale.BOTH_TICKS):
            gp.moveTo(tickPosition,-tickLength);
            gp.lineTo(tickPosition,tickLength);
            break;
        }
    }

    static public void drawLinearScale(double value0,
                                       double value1,
                                       int scaleSize,
                                       int minPrimary,
                                       int minSeparation,
                                       int location,
                                       GeneralPath primaryTicks,
                                       GeneralPath secondaryTicks,
                                       String[] label,
                                       double[] position) {

        // First calculate the range of the scale.
        double range = Math.abs(value1-value0);

        // Calculate the smallest order of magnitude which will NOT
        // fit in the range. 
        double m = Math.pow(10.,Math.ceil(Math.log(range)/Math.log(10.)));

        // Get the size of the window.
        double w = scaleSize;

        // Calculate the minimum value of np (number of primary tick
        // marks in m).
        double minNP = m*minPrimary/range;

        // Calculate the maximum value of np*ns.
        double maxNPNS = w*m/(range*minSeparation);

        // Find the optimal combination of primary and secondary tick
        // marks. 
        int optimal = -1;
        int oldPS = -1;
        int oldP = Integer.MAX_VALUE;
        for (int i=0; i<p.length; i++) {
            if (p[i]>minNP && ps[i]<maxNPNS) {
                if (p[i]<=oldP && ps[i]>oldPS) {
                    optimal = i;
                    oldP = p[i];
                    oldPS = ps[i];
                }
            }
        }

        // Setup the two paths which will contain the primary and
        // secondary tick marks.
        primaryTicks.reset();
        secondaryTicks.reset();
        primaryTicks.moveTo(0.f,0.f);
        primaryTicks.lineTo((float) w,0.f);
        secondaryTicks.moveTo(0.f,0.f);
        secondaryTicks.lineTo((float) w,0.f);

        // Only do something if a solution was found.
        if (optimal>0) {

            // Calculate the size of the secondary tick marks.
            double size = m/ps[optimal];

            // Calculate the limits for the tick marks.  Make sure
            // that we take the integers which are on the interior of
            // the interval!
            int average = (int) (0.5*(value0+value1)/size);
            int limit0 = (int) (value0/size-average) + average;
            int limit1 = (int) (value1/size-average) + average;

            // Now make the secondary tick marks.
            for (int iv= Math.min(limit0,limit1); 
                 iv <= Math.max(limit0,limit1);
                 iv++) {

                double val = iv*size;
                float tickPosition = interpolate(val,w,value0,value1);

                addTickMark(secondaryTicks,location,
                            tickPosition,secondaryTickSize);
            }

            // Calculate the size of the primary tick marks.
            size = m/p[optimal];

            // Again calculate the limits for the tick marks on the
            // interior of the interval.
            average = (int) (0.5*(value0+value1)/size);
            limit0 = (int) (value0/size-average) + average;
            limit1 = (int) (value1/size-average) + average;

            // Make the primary tick marks.
            for (int iv = Math.min(limit0,limit1);
                 iv <= Math.max(limit0,limit1);
                 iv++) {

                double val = iv*size;
                float tickPosition = interpolate(val,w,value0,value1);

                addTickMark(primaryTicks,location,
                            tickPosition,primaryTickSize);
            }

            // Now the labels with the correct precision must be made.
            // First calculate the necessary precision.
            int ndigits = (int) (Math.floor(Math.log(size)/Math.log(10.)));
            ndigits = -Math.min(ndigits,0);
            
            // Now make the labels.
            int iv = Math.min(limit0,limit1);
            int zero = iv;
            double val = iv*size;
            float tickPosition = interpolate(val,w,value0,value1);
            label[0] = fixedPrecision(val, ndigits);
            position[0] = (double) tickPosition;

            iv = Math.max(limit0,limit1);
            zero *= iv;
            val = iv*size;
            tickPosition = interpolate(val,w,value0,value1);
            label[1] = fixedPrecision(val, ndigits);
            position[1] = (double) tickPosition;

            // Check to see if zero is inside the interval.
            if (zero<=0) {
                tickPosition = interpolate(0.,w,value0,value1);
                label[2] = fixedPrecision(0., ndigits);
                position[2] = (double) tickPosition;
            } else {
                label[2] = null;
                position[2] = position[1];
            }
        }
    }

    static private float interpolate(double value, double size,
                              double value0, double value1) {
        return (float) (size*(value-value0)/(value1-value0));
    }

    public static String fixedPrecision(double d, int ndigits) {
        String dstring = Double.toString(d);
        StringBuffer buffer = new StringBuffer(dstring);
        int index = dstring.lastIndexOf(".");
        if (index<0) {
            buffer.append(".");
            index = buffer.length()-1;
        }
        buffer.setLength(index+ndigits+1);
        for (int i=0; i<buffer.length(); i++) {
            if (buffer.charAt(i)=='\u0000') buffer.setCharAt(i,'0');
        }
        return buffer.toString();
    }
}
