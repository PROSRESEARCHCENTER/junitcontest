/*
 * BinNormalizer.java
 *
 * Created on January 24, 2001, 12:12 PM
 */

package jas.hist.normalization;
import jas.hist.DataSource;
import jas.hist.Rebinnable1DHistogramData;

/**
 * Calculates a normalization factor base on a specific bin.
 * @author tonyj
 * @version $Id: BinNormalizer.java 11553 2007-06-05 22:06:23Z duns $
 */
public class BinNormalizer extends DataSourceNormalizer
{
/** Create a new BinNormalizer
 * @param data The data source
 * @param bin The bin number.
 */    
    public BinNormalizer(DataSource data, int bin) 
    {
        super(data);
        this.bin = bin;
        init();
    }
    protected double calculateNormalization()
    {
        if (source instanceof Rebinnable1DHistogramData)
        {
            Rebinnable1DHistogramData data = (Rebinnable1DHistogramData) source;
            int nBins = data.getBins();
            double xMin = data.getMin();
            double xMax = data.getMax();
            double[][] bins = data.rebin(nBins,xMin,xMax,false,hurry);
            double[] y = bins[0];
            return y[bin] > 0 ? y[bin] : 1;
        }
        else return 1;
    }
    private int bin;
}
