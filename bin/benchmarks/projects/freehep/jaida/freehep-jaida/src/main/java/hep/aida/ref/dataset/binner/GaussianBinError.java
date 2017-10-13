package hep.aida.ref.dataset.binner;

/**
 * This class calculates the error on the bin assuming a gaussian distribution.
 *
 * @author The FreeHEP team @ SLAC.
 *
 */
public class GaussianBinError implements BinError {
    
    public GaussianBinError() {
    }
    
    /** Get the minus error on a bin.
     * @param entries The entries in the bin.
     * @param height  The height of the bin.
     * @return The minus error.
     *
     *
     */
    public double minusError(int entries, double height) {
        return plusError(entries, height);
    }
    
    /** Get the plus error on a bin.
     * @param entries The entries in the bin.
     * @param height  The height of the bin.
     * @return The plus error.
     *
     *
     */
    public double plusError(int entries, double height) {
        return Math.sqrt( height );
    }
    
}
