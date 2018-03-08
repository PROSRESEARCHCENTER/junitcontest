package hep.aida.ref.dataset.binner;

/**
 * This class calculates the error on the bin assuming a Poisson distribution.
 * The error is calculated using the approximation ~ 1 + sqrt( h + 0.75 ) where 
 * h is the height of the bin.
 *
 * @author The FreeHEP team @ SLAC.
 *
 */
public class PoissonBinError implements BinError {
    
    public PoissonBinError() {
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
        return 1. + Math.sqrt( height + 0.75 );
    }
    
}
