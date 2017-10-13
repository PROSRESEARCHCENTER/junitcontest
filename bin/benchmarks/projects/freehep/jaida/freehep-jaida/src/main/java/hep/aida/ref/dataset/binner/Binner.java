package hep.aida.ref.dataset.binner;


/**
 * A binner is responsible to keep track of the bins statistics for
 * a binned data container in n-dimensions.
 * Internally, the statistics of each bin should be calculating by 
 * using an nDimensional DataStatistics.
 *
 * @author  The FreeHEP team at SLAC
 *
 */
public interface Binner {
    
    /**
     * Fill a bin with a new entry.
     * @param bin    The array specifying the bin.
     * @param x      The coordinate's array
     * @param weight The weight for this entry.
     *
     */
    public void fill( int[] bin, double[] x, double weight);

    /**
     * Set at once the content of a bin.
     * @param bin        The array specifying the bin.
     * @param entries    The entries in the bin.
     * @param height     The height of the bin.
     * @param mean       The array with the coordinate means
     * @param rms        The array with the coordinate rmss
     *
     */
    public void setBinContent(int[] bin, int entries, double height, double[] mean, double[] rms);

    /**
     * Add a set of entries to the existing content of the bin.
     * Notice that this is meant to be used instead of fill ONLY when the number
     * of entries in the set is greater than one.
     * @param bin        The array specifying the bin.
     * @param entries    The entries in the bin.
     * @param height     The height of the bin.
     * @param mean       The array with the coordinate means
     * @param rms        The array with the coordinate rmss
     *
     */
    public void addContentToBin(int[] bin, int entries, double height, double[] mean, double[] rms);

    /**
     * Remove a set of entries from a bin.
     * @param bin        The array specifying the bin.
     * @param entries    The entries in the bin.
     * @param height     The height of the bin.
     * @param mean       The array with the coordinate means
     * @param rms        The array with the coordinate rmss
     *
     */
    public void removeContentFromBin(int[] bin, int entries, double height, double[] mean, double[] rms);

    /**
     * Reset the content of a bin.
     * @param bin The array specifying the bin.
     *
     */
    public void resetBin(int[] bin);
    
    /**
     * Reset the content of the Binner.
     *
     */
    public void reset();
    
    /**
     * Get the number of entries in a bin.
     * @param bin The array specifying the bin.
     *
     */
    public int entries(int[] bin);

    /**
     * Get the height of a bin.
     * @param bin The array specifying the bin.
     *
     */    
    public double height(int[] bin);
    
    /**
     * Get the plus error on a bin.
     * @param bin The array specifying the bin.
     *
     */    
    public double plusError(int[] bin);

    /**
     * Get the minus error on a bin.
     * @param bin The array specifying the bin.
     *
     */    
    public double minusError(int[] bin);

    /**
     * Get the mean of a bin along a given coordinate.
     * @param bin   The array specifying the bin.
     * @param coord The coordinate's index.
     *
     */    
    public double mean(int[] bin, int coord);

    /**
     * Get the rms of a bin along a given coordinate.
     * @param bin   The array specifying the bin.
     * @param coord The coordinate's index.
     *
     */        
    public double rms(int[] bin, int coord);
    
    /**
     * Scale all the bins by a given scale factor.
     * @param scaleFactor The scale factor.
     *
     */
    public void scale(double scaleFactor);
    
}
