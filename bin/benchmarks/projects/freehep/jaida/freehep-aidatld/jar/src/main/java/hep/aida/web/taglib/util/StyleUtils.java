package hep.aida.web.taglib.util;

import hep.aida.util.comparison.StatisticalComparison;

/**
 *
 * @author The FreeHEP team @ SLAC
 */
public abstract class StyleUtils {
    
    public static String[] statCompareAlgorithmNames() {
        int n = StatisticalComparison.numberOfAvailableComparisonAlgorithm();
        String[] names = new String[n];
        for (int i=0; i<n; i++) {
            String tmp = StatisticalComparison.comparisonAlgorithm(i).algorithmNames()[0];
            names[i] = tmp;
        }
        return names;
    }
     
}
