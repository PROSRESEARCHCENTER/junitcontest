/*
 * IRange.java
 *
 * Created on May 17, 2002, 3:09 PM
 */

package hep.aida.ext;

/**
 *
 *  Interface for a Range.
 *
 * @author Tony Johnson, Victor Serbo, Max Turri
 *
 */
public interface IRange {
    
    /**
     * The IRange types. It determines if the boundaries are inside
     * or outside the range.
     *
     */
    public final static int INCLUDE_BOUNDS = 0;
    public final static int INCLUDE_UPPER_BOUND = 1;
    public final static int INCLUDE_LOWER_BOUND = 2;
    public final static int EXCLUDE_BOUNDS = 3;

    
    /**
     * Get the IRange type.
     * @return The IRange type.
     *
     */
    int type();
    
    /**
     * Set the IRange type.
     * @param type The IRange type.
     * @return <code>true</code> if the type was set succesfully
     *         <code>false</code> otherwise
     *
     */
    boolean setType( int type );
    
    /**
     * Get the lower bound.
     * @return The lower bound.
     *
     */
    double lowerBound();
    
    /**
     * Get the upper bound.
     * @return The upper bound.
     *
     */
    double upperBound();
    
    /**
     * Set the lower bound.
     * @param lowerBound The lower bound.
     * @return <code>true</code> if the lower bound was set succesfully
     *         <code>false</code> if lowerBound is greater than the upper bound.
     *
     */
    boolean setLowerBound( double lowerBound );
    
    /**
     * Set the upper bound.
     * @param upperBound The upper bound.
     * @return <code>true</code> if the upper bound was set succesfully
     *         <code>false</code> if upperBound is smaller than the lower bound.
     *
     */
    boolean setUpperBound( double upperBound );
    
    /**
     * Check if a value is within the IRange.
     * @param value The value to check.
     * @return <code>true</code> if the value is within the range.
     *         <code>false</code> otherwise.
     *
     */
    boolean isInRange( double value );

    
}
