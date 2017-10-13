package hep.aida.util.comparison;

import hep.aida.ext.IComparisonData;

import org.apache.commons.math.distribution.ChiSquaredDistribution;
import org.apache.commons.math.distribution.DistributionFactory;

/**
 *
 * @author The FreeHEP team @ SLAC.
 * Algorithm taken from http://www.ge.infn.it/geant4/analysis/HEPstatistics/
 *
 */
public class GoodmanComparisonAlgorithm extends KolmogorovSmirnovComparisonAlgorithm {
    
    private static final String[] names = new String[] {"Goodman","KolmogorovSmirnovChi2Approx","KSChi2Approx"};
    private static final ChiSquaredDistribution chi2Distribution = DistributionFactory.newInstance().createChiSquareDistribution(3);

    public String[] algorithmNames() {
        return names;
    }
    
    public double quality(IComparisonData d1, IComparisonData d2) {
        
        double distance = evaluateDistance(d1,d2);
        
        double entries1 = entries(d1);
        double entries2 = entries(d2);
        double n = entries1 * entries2 / (entries1 + entries2);

	double chi2 = 4 * distance * distance * n;
        double prob = Double.NaN;
        try {
            prob = chi2Distribution.cumulativeProbability(chi2);
        } catch ( org.apache.commons.math.MathException me) {
            throw new RuntimeException("Problems evaluating probability ",me);
        }
        return prob;
    }
}
