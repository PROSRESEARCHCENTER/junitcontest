package hep.aida.ref.histogram;

/**
 * Exception thrown if an IHistogram is unfillable.
 * @author The AIDA Team at SLAC.
 *
 */
public class UnfillableHistogramException extends RuntimeException {
    
    public UnfillableHistogramException() {
        super("This Histogram cannot be filled");
    }

}
