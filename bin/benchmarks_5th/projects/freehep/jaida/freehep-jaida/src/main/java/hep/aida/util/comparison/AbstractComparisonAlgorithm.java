package hep.aida.util.comparison;

import hep.aida.ext.IComparisonAlgorithm;
import hep.aida.ext.IComparisonData;
import hep.aida.ext.IComparisonResult;
import hep.aida.ref.AidaUtils;

import java.util.Map;

/**
 *
 * @author The FreeHEP team @ SLAC.
 *
 */
public abstract class AbstractComparisonAlgorithm implements IComparisonAlgorithm {
        
    private Map optionsMap;
    
    public static final int ONLY_BINNED_DATA = 0;
    public static final int ONLY_UNBINNED_DATA = 1;
    public static final int ANY_DATA = 2;
    
    public static final int ONLY_SAME_NUMBER_OF_EVENTS = 0;
    public static final int ANY_NUMBER_OF_EVENTS = 1;
    
    private int dataType;
    private int eventsType;
    
    private double rejectionLevel = 0.05;
    
    AbstractComparisonAlgorithm(int dataType, int eventsType) {
        this.dataType = dataType;
        this.eventsType = eventsType;
    }
    
    public IComparisonResult compare(IComparisonData d1, IComparisonData d2, String options) {
        
        if ( ! canCompare(d1, d2) )
            throw new IllegalArgumentException("This algorithm "+algorithmNames()[0]+" cannot compare the given data sets.");
        
        this.optionsMap = AidaUtils.parseOptions(options); 

        applyOptions();
        
        setRejectionLevel();
        
        ComparisonResult result = new ComparisonResult();
        
        result.setMatchBounds(matchLowerBound(), matchUpperBound());
        
        result.setQuality( quality(d1,d2) );
        
        result.setnDof( nDof(d1,d2) );
        
        return result;

    }
    
    public abstract double quality(IComparisonData d1, IComparisonData d2);
        
    public void applyOptions() {
    }
    
    public int nDof(IComparisonData d1, IComparisonData d2) {
        return d1.nPoints();
    }
    
    public double matchLowerBound() {
        return rejectionLevel();
    }

    public double matchUpperBound() {
        return 1;
    }
    
    public boolean isOptionSet(String option) {
        return optionsMap.containsKey(option);
    }
    
    public String optionValue(String option) {
        return (String)optionsMap.get(option);
    }
    
    public abstract String[] algorithmNames();
    
    public void setRejectionLevel() {
        String rejectionLevelStr = optionValue("rejectionLevel");
        if ( rejectionLevelStr != null )
            rejectionLevel = Double.valueOf(rejectionLevelStr).doubleValue();
        
    }
    
    public double rejectionLevel() {
        return rejectionLevel;
    }
    
    public double[] getCumulativeArray(IComparisonData d) {
        int nPoints = d.nPoints();
        
        double[] cumulativeWeights = new double[nPoints];
        double sumOfWeights = 0;
        
        for ( int i = 0; i < nPoints; i++ ) {
            double weight = d.weight(i);
            if ( weight < 0 )
                weight = 0;
            sumOfWeights += weight;
            cumulativeWeights[i] = sumOfWeights;
        }
        
        if ( sumOfWeights != 0 )
            for( int i=0; i < nPoints; i++ )
                cumulativeWeights[i] /= sumOfWeights;
        
        return cumulativeWeights;
    }
    
    public boolean canCompare(IComparisonData d1, IComparisonData d2) {
        if ( d1.type() != d2.type() )
            throw new IllegalArgumentException("Cannot compare a binned data set with an unbinned one.");
        
        if ( d1.type() == IComparisonData.BINNED_DATA && dataType == ONLY_UNBINNED_DATA )
            return false;

        if ( d1.type() == IComparisonData.UNBINNED_DATA && dataType == ONLY_BINNED_DATA )
            return false;

        if ( d1.type() == IComparisonData.BINNED_DATA )
            if ( ! isBinningCompatible(d1, d2) )
                return false;
        
        if ( eventsType == ONLY_SAME_NUMBER_OF_EVENTS )
            if ( d1.nPoints() != d2.nPoints() )
                return false;
        return true;
    }
    
    private boolean isBinningCompatible(IComparisonData d1, IComparisonData d2 ) {
        int nBins = d1.nPoints();
        if ( nBins != d2.nPoints() )
            return false;
        for ( int i = 0; i < nBins; i++ )
            if ( d1.value(i) != d2.value(i) )
                return false;
        return true;
    }
    
    public double sumOfWeights(IComparisonData d) {
        double sumOfWeights = 0;
        for ( int i = 0; i < d.nPoints(); i++ )
            sumOfWeights += d.weight(i);
        return sumOfWeights;
    }
    
    public int entries(IComparisonData d) {
        int entries = 0;
        for ( int i = 0; i < d.nPoints(); i++ )
            entries += d.entries(i);
        return entries;
    }
}
