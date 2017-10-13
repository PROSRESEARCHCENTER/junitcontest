package hep.aida.ref.dataset.binner;


/**
 * This binner is for efficiency-type of data.
 * The bins are re-normalized to be between 0 and 1 by
 * taking the ratio between the bin height and the bin entries.
 *
 * @author  The AIDA team at SLAC
 *
 */
public class EfficiencyBinner extends DefaultBinner {
        
    /**
     * Creates a new instance of Binner.
     * @param bins    The array containing the number of bins per coordinate.
     * @param options The options.
     *
     */
    public EfficiencyBinner(int[] bins, String options) {
        super(bins, options);
        setBinError( new EfficiencyBinError() );
    }

    /**
     * Set at once the content of a bin.
     * @param bin        The array specifying the bin.
     * @param entries    The entries in the bin.
     * @param height     The height of the bin.
     * @param mean       The array with the coordinate means
     * @param rms        The array with the coordinate rmss
     *
     */
    public void setBinContent(int[] bin, int entries, double height, double[] mean, double[] rms) {
        super.setBinContent(bin, entries, height*entries, mean, rms);
    }
    
    public void addContentToBin(int[] bin, int entries, double height, double[] mean, double[] rms) {
        super.addContentToBin(bin, entries, height*entries, mean, rms);
    }

    public void removeContentFromBin(int[] bin, int entries, double height, double[] mean, double[] rms) {
        super.removeContentFromBin(bin, entries, height*entries, mean, rms);
    }
        
    /**
     * Get the height of a bin.
     * @param bin The array specifying the bin.
     *
     */    
    public double height(int[] bin) {
        int iBin = internalBin( bin );
        int e = binStatistics( iBin ).entries();
        if ( e > 0 )
            return binStatistics( iBin ).sumOfWeights()/e;
        return 0;
    }    
}
