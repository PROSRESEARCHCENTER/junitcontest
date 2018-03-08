package hep.aida.util.comparison;

import hep.aida.ext.IComparisonData;

/**
 *
 * @author The FreeHEP team @ SLAC.
 *
 */
public class ComparisonData implements IComparisonData {
    
    private double[] data;
    private double[] weight;
    private int type;
    private int[] entries;
    
    public ComparisonData(double[] data, double[] weight, int type) {
        this(data,weight, null,type);
    }

    public ComparisonData(double[] data, double[] weight, int[] entries, int type) {
        if ( data.length != weight.length || (entries != null && data.length != entries.length) )
            throw new IllegalArgumentException("Incompatible lengths! Data and weights have different lengths.");
        this.data = data;
        this.weight = weight;
        this.type = type;
        this.entries = entries;
    }
    
    public int nPoints() {
        return data.length;
    }    
    
    public int type() {
        return type;
    }
    
    public double value(int i) {
        return data[i];
    }
    
    public double weight(int i) {
        return weight[i];
    }
    
    public int entries(int i) {
        if ( entries == null )
            return 1;
        return entries[i];
    }
    
}
