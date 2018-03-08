package hep.aida.ref.pdf;
import hep.aida.ext.IRange;

/**
 *
 * Implementation of IRange.
 *
 * @author The FreeHEP team @ SLAC.
 *
 */
public class Range implements IRange {
    
    private double upperBound = Double.POSITIVE_INFINITY;
    private double lowerBound = Double.NEGATIVE_INFINITY;
    private int rangeType;
    
    /**
     * Creates a new instance of Range.
     */
    public Range() {
        this(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, INCLUDE_BOUNDS );
    }
    
    /**
     * Creates a new instance of Range with type INCLUDE_BOUNDS.
     * @param lowerBound The lower bound.
     * @param upperBound The upper bound.
     *
     */
    public Range( double lowerBound, double upperBound ) {
        this( lowerBound, upperBound, INCLUDE_BOUNDS );
    }
    
    /**
     * Creates a new instance of Range.
     * @param lowerBound The lower bound.
     * @param upperBound The upper bound.
     * @param type The Range type.
     *
     */
    public Range( double lowerBound, double upperBound, int type ) {
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
        setType( type );
    }
    
    /**
     * Get the IRange type.
     * @return The IRange type.
     *
     */
    public int type() {
        return rangeType;
    }
    
    /**
     * Set the IRange type.
     * @param type The IRange type.
     * @return <code>true</code> if the type was set succesfully
     *         <code>false</code> otherwise
     *
     */
    public boolean setType( int type ) {
        this.rangeType = type;
        return true;
        /*
        switch ( type ) {
            case INCLUDE_BOUNDS :
                if ( lowerBound != Double.NEGATIVE_INFINITY && upperBound != Double.POSITIVE_INFINITY ) {
                    rangeType = type;
                    return true;
                }
                break;
            case INCLUDE_UPPER_BOUND :
                if ( upperBound != Double.POSITIVE_INFINITY ) {
                    rangeType = type;
                    return true;
                }
                break;
            case INCLUDE_LOWER_BOUND :
                if ( lowerBound != Double.NEGATIVE_INFINITY ) {
                    rangeType = type;
                    return true;
                }
                break;
            case EXCLUDE_BOUNDS :
                rangeType = type;
                return true;
            default :
                System.out.println("Unknown type "+type+" for a Range!!");
        }
        throw new RuntimeException("Could not set the range type");
//        return false;
         */
    }
    
    /**
     * Get the lower bound.
     * @return The lower bound.
     *
     */
    public double lowerBound() {
        return lowerBound;
    }
    
    /**
     * Get the upper bound.
     * @return The upper bound.
     *
     */
    public double upperBound() {
        return upperBound;
    }
    
    /**
     * Set the lower bound.
     * @param lowerBound The lower bound.
     * @return <code>true</code> if the lower bound was set succesfully
     *         <code>false</code> if lowerBound is greater than the upper bound.
     *
     */
    public boolean setLowerBound( double lowerBound ) {
        if ( lowerBound < upperBound ) {
            this.lowerBound = lowerBound;
            return true;
        }
        return false;
    }
    
    /**
     * Set the upper bound.
     * @param upperBound The upper bound.
     * @return <code>true</code> if the upper bound was set succesfully
     *         <code>false</code> if upperBound is smaller than the lower bound.
     *
     */
    public boolean setUpperBound( double upperBound ) {
        if ( upperBound > lowerBound ) {
            this.upperBound = upperBound;
            return true;
        }
        return false;
    }
    
    /**
     * Check if a value is within the IRange.
     * @param value The value to check.
     * @return <code>true</code> if the value is within the range.
     *         <code>false</code> otherwise.
     *
     */
    public boolean isInRange( double value ) {
        if ( value < upperBound && value > lowerBound ) return true;
        if( value > upperBound || value < lowerBound ) return false;
        if ( value == upperBound ) {
            if ( rangeType == INCLUDE_BOUNDS || rangeType == INCLUDE_UPPER_BOUND ) return true;
        }
        else {
            if ( rangeType == INCLUDE_BOUNDS || rangeType == INCLUDE_LOWER_BOUND ) return true;
        }
        return false;
    }
}
