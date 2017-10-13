package hep.aida.ref.pdf;

/**
 * Interface implemented by classes with Units.
 *
 * @author The FreeHEP team @ SLAC.
 *
 */
public interface HasUnits {
    
    /**
     * Get the Units of the object.
     * @return The object's Units.
     *
     */
    Units units();
    
    /**
     * Set the object's Units.
     * @param units The new Units.
     *
     */
    void setUnits(Units units);
    
}
