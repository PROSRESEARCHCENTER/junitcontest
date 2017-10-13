package hep.aida.ext;

/**
 *
 * @author The FreeHEP team @ SLAC.
 *
 */
public interface IComparisonAlgorithm {
    
    /**
     * Check if this algorithm can compare two data sets.
     *
     */
    boolean canCompare(IComparisonData d1, IComparisonData d2);
    
    /**
     * Compare two distributions.
     *
     */
    IComparisonResult compare(IComparisonData d1, IComparisonData d2, String options);
    
    /**
     * Get the algorithm name
     *
     */
    String[] algorithmNames();

}
