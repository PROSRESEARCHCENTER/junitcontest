package hep.aida.ref.dataset.binner;

/**
 * Classes implementing this interfaces calculate the plus and minus error on a bin
 * given its entries and height.
 *
 * @author The FreeHEP team @ SLAC.
 * 
 */
public interface BinError {
    
    /**
     * Get the plus error on a bin.
     * @param entries The entries in the bin.
     * @param height  The height of the bin.
     * @return The plus error.
     *
     */
    public double plusError( int entries, double height );
    
    /**
     * Get the minus error on a bin.
     * @param entries The entries in the bin.
     * @param height  The height of the bin.
     * @return The minus error.
     *
     */
    public double minusError( int entries, double height );

}
