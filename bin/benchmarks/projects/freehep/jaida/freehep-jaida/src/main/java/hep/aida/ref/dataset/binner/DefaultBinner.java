package hep.aida.ref.dataset.binner;

import hep.aida.ref.dataset.DataStatistics;

/**
 * The default implementation of a Binner.
 *
 * @author  The FreeHEP team at SLAC
 *
 */
public class DefaultBinner implements Binner {
    
    private int[] maxBins;
    private int nBins = 1;
    private int dimension;
    
    private DataStatistics[] binStats;    
    private BinError binError;
    
    /**
     * Creates a new instance of Binner.
     * @param bins    The array containing the number of bins per coordinate.
     * @param options The options.
     *
     */
    public DefaultBinner(int[] bins, String options) {
        this.dimension = bins.length;
        
        maxBins = new int[dimension];
        for ( int j = 0; j < dimension; j++ )
            maxBins[j] = bins[j];
        for ( int i = 0; i < dimension; i++ ) {
            if (bins[i] < 0) throw new IllegalArgumentException("Number of bins cannot be negative!!! "+bins);
            nBins *= bins[i];
        }
        
        setBinError( new GaussianBinError() );

        binStats = new DataStatistics[ nBins ];
        for ( int i = 0; i < nBins; i++ )
            binStats[i] = new DataStatistics(dimension);
    }

    /**
     * Utility method to convert from the bin numbering to the
     * internal bin representation.
     * For a bin represented by int[] {binx, biny, binz} we use
     * the internal numbering: binx + biny*nBinsx + binz*nBinsx*nBinsy
     *
     */
    protected int internalBin(int[] bin) {
        checkDimension(bin);
        int ibin = 0;
        int b = 1;
        for ( int i = 0; i < dimension; i++ ) {
            ibin += bin[i]*b;
            b *= maxBins[i];
        }
        return ibin;
    }
        
    /**
     * Utility method to check the dimension of the bin.
     *
     */
    private void checkDimension( int[] bin ) {
        if ( bin.length != dimension )
            throw new IllegalArgumentException("Illegal dimension "+bin.length+". It must be "+dimension);
    }
    
    /**
     * Utility method to access a bin's statistical information.
     *
     */
    protected DataStatistics binStatistics( int bin ) {
        return binStats[ bin ];
    }
    
    /**
     * Fill a bin with a new entry.
     * @param bin    The array specifying the bin.
     * @param x      The coordinate's array
     * @param weight The weight for this entry.
     *
     */
    public void fill( int[] bin, double[] x, double weight) {
        int iBin = internalBin( bin );
        binStatistics( iBin ).addEntry( x, weight );
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
        int iBin = internalBin( bin );
        resetBin( iBin );
        binStatistics( iBin ).addEntries( mean, rms, height, 0, entries );
    }
    
    public void addContentToBin(int[] bin, int entries, double height, double[] mean, double[] rms) {
        int iBin = internalBin( bin );
        binStatistics( iBin ).addEntries( mean, rms, height, 0, entries );
    }

    public void removeContentFromBin(int[] bin, int entries, double height, double[] mean, double[] rms) {
        int iBin = internalBin( bin );
        binStatistics( iBin ).removeEntries( mean, rms, height, 0, entries );
    }
        
    /**
     * Reset the content of a bin.
     * @param bin The array specifying the bin.
     *
     */
    public void resetBin(int[] bin) {
        int iBin = internalBin( bin );
        resetBin( iBin );
    }
    
    /**
     * Reset the content of the Binner.
     *
     */
    public void reset() {
        for ( int i = 0; i < nBins; i++ )
            resetBin(i);
    }

    /**
     * Reset the content of a bin.
     * @param bin The bin number in the internal representation.
     *
     */
    private void resetBin( int bin ) {
        binStatistics( bin ).reset();
    }

    /**
     * Get the number of entries in a bin.
     * @param bin The array specifying the bin.
     *
     */
    public int entries(int[] bin) {
        int iBin = internalBin( bin );
        return binStatistics( iBin ).entries();
    }        
        
    /**
     * Get the height of a bin.
     * @param bin The array specifying the bin.
     *
     */    
    public double height(int[] bin) {
        int iBin = internalBin( bin );
        return binStatistics( iBin ).sumOfWeights();
    }
    
    /**
     * Get the plus error on a bin.
     * @param bin The array specifying the bin.
     *
     */    
    public double plusError(int[] bin) {
        int iBin = internalBin( bin );
        return binError.plusError(binStatistics( iBin ).entries(), binStatistics( iBin ).sumOfWeights() );
    }

    /**
     * Get the minus error on a bin.
     * @param bin The array specifying the bin.
     *
     */    
    public double minusError(int[] bin) {
        int iBin = internalBin( bin );
        return binError.minusError(binStatistics( iBin ).entries(), binStatistics( iBin ).sumOfWeights() );
    }
    
    /**
     * Get the mean of a bin along a given coordinate.
     * @param bin   The array specifying the bin.
     * @param coord The coordinate's index.
     *
     */    
    public double mean(int[] bin, int coord) {
        int iBin = internalBin( bin );
        return binStatistics( iBin ).mean(coord);
    }

    /**
     * Get the rms of a bin along a given coordinate.
     * @param bin   The array specifying the bin.
     * @param coord The coordinate's index.
     *
     */        
    public double rms(int[] bin, int coord) {
        int iBin = internalBin( bin );
        return binStatistics( iBin ).rms(coord);
    }
    
    /**
     * Scale all the bins by a given scale factor.
     * @param scaleFactor The scale factor.
     *
     */
    public void scale(double scaleFactor) {
        for ( int i = 0; i < nBins; i++ )
            binStatistics( i ).scale(scaleFactor);
    }
    
    /** 
     * Set the BinError with which the plus and minus
     * error on the bin are calculated.
     * @param binError The BinError.
     *
     *
     */
    public void setBinError(BinError binError) {
        this.binError = binError;
    }
    
}
