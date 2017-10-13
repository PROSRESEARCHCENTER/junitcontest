package hep.aida.ref.pdf;

/**
 * Interface to be implemented by classes that have a value.
 *
 * @author The FreeHEP team @ SLAC.
 *
 */
public interface HasValue {
    
    /**
     * Get the current value.
     * @return The current value.
     *
     */
    double value();
    
    /**
     * Set the current value.
     * @param value The new value.
     *
     */
    void setValue(double value);
    
}
