package hep.aida.util.comparison;

import hep.aida.ext.IComparisonData;

/**
 *
 * @author The FreeHEP team @ SLAC.
 * Algorithm taken from http://www.ge.infn.it/geant4/analysis/HEPstatistics/
 *
 */
public class KolmogorovSmirnovComparisonAlgorithm extends AbstractComparisonAlgorithm {
    
//    private static double ACCURACY = 0.001;
//    private static double CONVERGENCE = 1.e-8;
    
    private static final String[] names = new String[] {"KolmogorovSmirnov","KS"};
    private static final int dType = ANY_DATA;
    private static final int eType = ANY_NUMBER_OF_EVENTS;
    
    public KolmogorovSmirnovComparisonAlgorithm() {
        super(dType, eType);
    }

    public String[] algorithmNames() {
        return names;
    }
    
    public double quality(IComparisonData d1, IComparisonData d2) {
        
//        if ( d1.type() != IComparisonData.UNBINNED_DATA || d2.type() != IComparisonData.UNBINNED_DATA )
//            throw new IllegalArgumentException("The "+algorithmNames()[0]+" algorithm is meant to be applyed to unbinned data only.");
        
        double distance = evaluateDistance(d1, d2);
        double entries1 = entries(d1);
        double entries2 = entries(d2);
        distance *= Math.sqrt(entries1*entries2/(entries1+entries2));
        double p = probability(distance);
        return p;
    }

    protected double evaluateDistance(IComparisonData d1, IComparisonData d2 ) {

        int nPoints1 = d1.nPoints();
        int nPoints2 = d2.nPoints();
        
        double[] cumulativeWeights1 = getCumulativeArray(d1);
        double[] cumulativeWeights2 = getCumulativeArray(d2);
        
        double D=0;
        double data1=0, data2=0;
        
        double cumulative1=0., cumulative2=0.;
        double d = 0;
        
        int j1=0, j2=0;
        boolean advance1, advance2;
        boolean flag1 = true, flag2 = true;
        
        while(true){
            
            advance1 = false;
            advance2 = false;
            
            data1 = d1.value(j1);
            data2 = d2.value(j2);
            
            if( data1 <= data2 ){
                cumulative1 = cumulativeWeights1[j1];
                advance1 = true;
            }
            if( data1 >= data2 ){
                cumulative2 = cumulativeWeights2[j2];
                advance2 = true;
            }
            
            d = Math.abs( cumulative1 - cumulative2 );
            if( d > D )
                D = d;
            
            if ( j1 == nPoints1 -1 )
                flag1 = false;
            if ( j2 == nPoints2 -1 )
                flag2 = false;
            
            if ( advance1 ) {
                if ( flag1 )
                    j1++;
            } else if ( ! flag2 )
                if ( flag1 )
                    j1++;
            
            if ( advance2 ) {
                if ( flag2 )
                    j2++;
            } else if ( ! flag1 )
                if ( flag2 )
                    j2++;

            
            if ( (!flag1) && (!flag2) )
                break;
        }
        
        return D;
    }
    
    protected double probability(double distance) {

        double prob = distance;
        
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
