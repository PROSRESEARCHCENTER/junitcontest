/*
 * MaxBinNormalizer.java
 *
 * Created on January 23, 2001, 3:14 PM
 */

package jas.hist.normalization;
import jas.hist.DataSource;
import jas.hist.Rebinnable1DHistogramData;

/**
 * Calculates a normalization factor based on the bin with the largest value.
 * @author  tonyj
 * @version $Id: MaxBinNormalizer.java 11553 2007-06-05 22:06:23Z duns $ 
 */
public class MaxBinNormalizer extends DataSourceNormalizer
{
    /** Creates new MaxBinNormalizer 
     * @param data The data source
     */
    public MaxBinNormalizer(DataSource data) 
    {
        super(data);
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
            double max = 0;
            for (int i=0; i<y.length; i++)
            {
                if (max < y[i]) max = y[i];
            }
            return max > 0 ? max : 1;
        }
        else return 1;
    }
}

