package org.freehep.util.parameterdatabase.types;

import java.util.StringTokenizer;

/**
 * This class represents a continuous range of double values which includes the
 * given endpoints.
 */
public class DoubleRange {

    private double minimum;

    private double maximum;

    /**
     * Create a DoubleRange which represents a continuous range of double values
     * from the given minimum to the given maximum. The endpoints are included
     * in this range.
     */
    public DoubleRange(double minimum, double maximum) {
        this.minimum = minimum;
        this.maximum = maximum;
    }

    /**
     * This creates a DoubleRange from a String with the following format:
     * [min,max], where min and max are valid double strings. If min or max is
     * an empty string, then it represents the smallest or largest double,
     * respectively. The brackets must be included.
     */
    public DoubleRange(String range) {

        // Split the given string into two parts, dividing at the comma
        // between the minimum and maximum.
        StringTokenizer st = new StringTokenizer(range.trim(), ",");
        if (st.countTokens() != 2)
            throw new RangeFormatException();

        // Retrieve the two halves.
        String firstHalf = st.nextToken();
        String lastHalf = st.nextToken();

        // Get the number strings.
        String minString = firstHalf.substring(1).trim();
        String maxString = lastHalf.substring(0, lastHalf.length() - 1).trim();

        // Get the value of the minimum and maximum.
        if (!minString.equals("")) {
            minimum = Double.parseDouble(minString);
        } else {
            minimum = -Double.MAX_VALUE;
        }

        if (!maxString.equals("")) {
            maximum = Double.parseDouble(maxString);
        } else {
            maximum = Double.MAX_VALUE;
        }
    }

    /**
     * Writes out the range in the format [min,max]. If min or max is the empty
     * string, then it represents the smallest or largest double value,
     * respectively.
     */
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("[");
        if (minimum != (-Double.MAX_VALUE)) {
            buffer.append(Double.toString(minimum));
        }
        buffer.append(",");
        if (maximum != Double.MAX_VALUE) {
            buffer.append(Double.toString(maximum));
        }
        buffer.append("]");

        return buffer.toString();
    }

    /**
     * Return the minimum end of the range.
     */
    public double getMinimum() {
        return minimum;
    }

    /**
     * Return the maximum end of the range.
     */
    public double getMaximum() {
        return maximum;
    }

    /**
     * Check to see if the given double is in the range represented by this
     * object.
     */
    public boolean inRange(double d) {
        return (d >= minimum && d <= maximum);
    }

    /**
     * Return the double value d clipped to the range represented by this
     * object.
     */
    public double forceInRange(double d) {
        return Math.max(minimum, Math.min(maximum, d));
    }

    /**
     * This exception will be thrown if the String describing the range is
     * invalid for any reason.
     */
    protected class RangeFormatException extends RuntimeException {

        public RangeFormatException() {
            super();
        }

        public RangeFormatException(String s) {
            super(s);
        }
    }

}
