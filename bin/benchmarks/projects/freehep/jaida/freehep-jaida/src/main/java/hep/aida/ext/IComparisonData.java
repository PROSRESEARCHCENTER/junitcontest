package hep.aida.ext;

/**
 *
 * @author The FreeHEP team @ SLAC.
 *
 */
public interface IComparisonData {
    
    public static int BINNED_DATA = 0;
    public static int UNBINNED_DATA = 1;
    
    /**
     * IMPORTANT: The data has to be ordered in ascending order!
     *
     */
    
    /**
     * Get the data Type: binned or unbinned.
     *
     */
    int type();
    
    /**
     * Get the number of points.
     *
     */
    int nPoints();
    
    /**
     * Get the ith value.
     *
     */
    double value(int i);
    
    /**
     * Get the ith weight.
     *
     */
    double weight(int i);
    
    /**
     * Get the ith number of entries.
     *
     */
    int entries(int i);
    
}
