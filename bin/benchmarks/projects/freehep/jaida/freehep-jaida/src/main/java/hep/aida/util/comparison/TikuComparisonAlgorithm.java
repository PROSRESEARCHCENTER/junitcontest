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
public class TikuComparisonAlgorithm extends AbstractComparisonAlgorithm {
    
    private static final double precision = 1.0e-10;
    private FiszCramerVonMisesComparisonAlgorithm alg = new FiszCramerVonMisesComparisonAlgorithm();
    
    private static final String[] names = new String[] {"Tiku"};
    
    private static final int dType = ANY_DATA;
    private static final int eType = ANY_NUMBER_OF_EVENTS;
    
    public TikuComparisonAlgorithm() {
        super(dType, eType);
    }
    
    public String[] algorithmNames() {
        return names;
    }
    
    public double quality(IComparisonData d1, IComparisonData d2) {
        double quality = alg.quality(d1,d2);
        
        int entries = entries(d1);
        
        double c = (4*entries-3);
        double n2 = entries*entries;
        
        double bNum = (32*n2 - 61*entries+30);
        double bDen = 84*entries*c;
        double b = bNum / bDen;
        
        double aNum = ( 336*n2 - 959*entries +609);
        double aDen = 210*bNum;
        double a = aNum / aDen;
        
        double distance = Math.abs((quality-a)/b);

        double nFreedom =(98*(entries)*Math.pow(c,3))/(5*Math.pow(bNum,2));

        ChiSquaredDistribution chi2Distribution = DistributionFactory.newInstance().createChiSquareDistribution(nFreedom);
        double prob = Double.NaN;
        try {
            prob = chi2Distribution.cumulativeProbability(distance);
        } catch ( org.apache.commons.math.MathException me) {
            throw new RuntimeException("Problems evaluating probability ",me);
        }
        return prob;
    }
}