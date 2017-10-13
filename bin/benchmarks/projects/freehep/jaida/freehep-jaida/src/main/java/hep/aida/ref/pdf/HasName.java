package hep.aida.ref.pdf;

/**
 * Interface to be implemented by classes that have a name.
 *
 * @author The FreeHEP team @ SLAC.
 *
 */
public interface HasName {
    
    /**
     * Get the name.
     * @return the name.
     *
     */
    String name();
    
    /**
     * Set the name.
     * @param name The new name.
     *
     */
    void setName(String name);
    
}
