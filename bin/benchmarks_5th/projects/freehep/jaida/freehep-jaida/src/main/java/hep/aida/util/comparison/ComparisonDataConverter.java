package hep.aida.util.comparison;

import hep.aida.ICloud1D;
import hep.aida.IHistogram1D;
import hep.aida.ext.IComparisonData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

/**
 *
 * @author The FreeHEP team @ SLAC.
 *
 */
public abstract class ComparisonDataConverter {

    private static IncreasingOrder increasingOrder = new IncreasingOrder();
    
    public static IComparisonData comparisonData(IHistogram1D hist, String testOptions) {
        int bins = hist.axis().bins();
        
        double[] data = new double[bins];
        double[] weights = new double[bins];
        int[] entries = new int[bins];
        
        for ( int i = 0; i < bins; i++ ) {
            data[i] = hist.axis().binLowerEdge(i);
            weights[i] = hist.binHeight(i);
            entries[i] = hist.binEntries(i);
        }
        
        return new ComparisonData(data, weights, entries, IComparisonData.BINNED_DATA);
    }
    
    public static IComparisonData comparisonData(ICloud1D cloud, String testOptions) {
        
        if (cloud.isConverted())
            return comparisonData(cloud.histogram(),testOptions);

        int entries = cloud.entries();
        
        double[] data = new double[entries];
        double[] weights = new double[entries];
        
        ArrayList list = new ArrayList();
        for ( int i = 0; i < entries; i++ )
            list.add(new DataWeightEntry(cloud.value(i), cloud.weight(i)));

        Object[] listArray = list.toArray();
        
        Arrays.sort(listArray, increasingOrder);
        
        for ( int i = 0; i < entries; i++ ) {
            DataWeightEntry d = (DataWeightEntry) listArray[i];
            data[i] = d.data();
            weights[i] = d.weight();
        }
        return new ComparisonData(data, weights, IComparisonData.UNBINNED_DATA);
    }
    
    private static class DataWeightEntry {
        
        private double data;
        private double weight;

        DataWeightEntry(double data, double weight) {
            this.data = data;
            this.weight = weight;
        }
        
        double data() {
            return data;
        }
        
        double weight() {
            return weight;
        }
    }
    
    private static class IncreasingOrder implements Comparator {
        
        public int compare(Object o1, Object o2) {
            DataWeightEntry d1 = (DataWeightEntry)o1;
            DataWeightEntry d2 = (DataWeightEntry)o2;
            if ( d1.data() < d2.data() )
                return -1;
            else if ( d1.data() == d2.data() )
                return 0;
            return 1;
        }        
        
    }
    
}
