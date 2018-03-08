package hep.aida.util;

import hep.aida.IAnalysisFactory;
import hep.aida.ICloud1D;
import hep.aida.IHistogram1D;

/**
 *
 * @author The FreeHEP team @ SLAC.
 * @version $Id: HistUtils.java 13360 2007-10-02 23:13:06Z serbo $
 */
public class HistUtils {
    
    /**
     * Test the distribution of h1 and h2 using the Kolmogorov algorithm.
     * The result is between 0 and 1; 1 is for identical histograms.
     *
     */
    
    public static double kolmogorovTest( ICloud1D c1, ICloud1D c2) {
        return kolmogorovTest(c1, c2, 50);
    }
    
    public static double kolmogorovTest( ICloud1D c1, ICloud1D c2, int nBins) {
        double result = 0;
        if (!c1.isConverted() && !c2.isConverted()) {
        double[] bins1 = null;
        double[] bins2 = null;
            double xMin = c1.lowerEdge();
            double xMax = c1.upperEdge();
            if (c2.lowerEdge() < xMin) xMin = c2.lowerEdge();
            if (c2.upperEdge() > xMax) xMax = c2.upperEdge();
            bins1 = getBins(c1, nBins, xMin, xMax);
            bins2 = getBins(c1, nBins, xMin, xMax);
            result = kolmogorovTest(bins1, bins2);
        } else if (c1.isConverted() && !c2.isConverted()) {
            IHistogram1D h1 = c1.histogram();
            IHistogram1D h2 = IAnalysisFactory.create().createHistogramFactory(null).createHistogram1D(c2.title(), h1.axis().bins(), h1.axis().lowerEdge(), h1.axis().upperEdge());
            result = kolmogorovTest(h1, h2);
        } else if (!c1.isConverted() && c2.isConverted()) {
            IHistogram1D h2 = c2.histogram();
            IHistogram1D h1 = IAnalysisFactory.create().createHistogramFactory(null).createHistogram1D(c1.title(), h2.axis().bins(), h2.axis().lowerEdge(), h2.axis().upperEdge());
            result = kolmogorovTest(h1, h2);
        } else if (c1.isConverted() && c2.isConverted()) {
            result = kolmogorovTest(c1.histogram(), c2.histogram());
        }
        return result;
    }
               
    // Use for unconverted ICloud1D only
    private static double[] getBins(ICloud1D c, int nBins, double xMin, double xMax) {
        if ( c.isConverted() )
            throw new IllegalArgumentException("ICloud1D must be not converted!");
        
        double[] bins = new double[nBins];
        double x = 0;
        double binWidth = (xMax - xMin)/nBins;
        for (int i=0; i<nBins; i++) {
            x = c.value(i);
            if (x >= xMin && x< xMax) {
                int index = (int) Math.floor((x - xMin)/binWidth);
                bins[index] += c.weight(i);
            }
        }
        return bins;
    }
    
    public static double kolmogorovTest( IHistogram1D h1, IHistogram1D h2 ) {
               
        if ( h1 == null || h2 == null )
            throw new IllegalArgumentException("Null Histogram!");
        
        if ( ! h1.axis().equals( h2.axis() ) )
            throw new IllegalArgumentException("The two histograms must have the same binning!");
        
        int nBins = h1.axis().bins();
        
        double[] bins1 = new double[nBins];
        double[] bins2 = new double[nBins];
        
        for ( int i = 0; i < nBins; i++ ) {
            bins1[i] = h1.binHeight(i);
            bins2[i] = h2.binHeight(i);
        }
        return kolmogorovTest(bins1, bins2);        
    }
    
    /**
     * Test the distribution of h1 and h2 using the Kolmogorov algorithm.
     * The result is between 0 and 1; 1 is for identical histograms.
     *
     */
    public static double kolmogorovTest( double[] bins1, double[] bins2 ) {
        
        if ( bins1.length != bins2.length )
            throw new IllegalArgumentException("The two histograms must have the same number of bins!");
        
        int nBins = bins1.length;
        
        double sumOfHeights1 = 0;
        double sumOfHeights2 = 0;
        
        for ( int i = 0; i < nBins; i++ ) {
            sumOfHeights1 += bins1[i];
            sumOfHeights2 += bins2[i];
        }
        
        if ( sumOfHeights1 == 0 || sumOfHeights2 == 0 )
            throw new IllegalArgumentException("The histograms cannot have zero integral!");
        
        double norm1 = 1./sumOfHeights1;
        double norm2 = 1./sumOfHeights2;
        
        // Find largest difference for Kolmogorov Test
        double diff = 0, normSum1 = 0, normSum2 = 0;
        
        for ( int i = 0; i < nBins; i++ ) {
            normSum1 += norm1*bins1[i];
            normSum2 += norm2*bins2[i];
            double tmpDiff = Math.abs( normSum1 - normSum2 );
            if ( tmpDiff > diff ) diff = tmpDiff;
        }
        
        double prob = diff*Math.sqrt(sumOfHeights1*sumOfHeights2/(sumOfHeights1+sumOfHeights2));
        double p = 0;
        if ( prob < 0.2 ) return 1;
        if ( prob > 1 ) {
            // jf2[j] = -2* j**2
            double[] fj2 = {-2. , -8. , -18. , -32. , -50.};
            double s = -2;
            double p2 = prob*prob;
            for ( int i = 0; i < 5; i++ ) {
                s *= -1;
                double c = fj2[i] *p2;
                if (c < -100) return p;
                p += s*Math.exp(c);
            }
            return p;
        }
        
        double[] cons = { -1.233700550136 , -11.10330496 , -30.84251376};
        double sqr2pi = Math.sqrt( 2*Math.PI );
        
        double zinv = 1./prob;
        double a = sqr2pi*zinv;
        double zinv2 = zinv*zinv;
        
        double arg;
        for ( int i =0; i < 3; i++) {
            arg = cons[i]*zinv2;
            if (arg < -30) continue;
            p += Math.exp(arg);
        }
        p = 1 - a*p;
        return p;
        
    }    
}