package hep.aida.util.comparison;

import hep.aida.ext.IComparisonData;

/**
 *
 * @author The FreeHEP team @ SLAC.
 * Algorithm taken from http://www.ge.infn.it/geant4/analysis/HEPstatistics/
 *
 */
public class KuiperComparisonAlgorithm extends AbstractComparisonAlgorithm {
    
    private static final double ACCURACY = 0.001;
    private static final double CONVERGENCE = 1.e-8;
    
    private static final String[] names = new String[] {"Kuiper"};
    private static final int dType = ONLY_UNBINNED_DATA;
    private static final int eType = ANY_NUMBER_OF_EVENTS;
    
    public KuiperComparisonAlgorithm() {
        super(dType, eType);
    }
    
    public String[] algorithmNames() {
        return names;
    }
    
    public double quality(IComparisonData d1, IComparisonData d2) {
        
        if ( d1.type() != IComparisonData.UNBINNED_DATA || d2.type() != IComparisonData.UNBINNED_DATA )
            throw new IllegalArgumentException("The "+algorithmNames()[0]+" comparison can only be applyed to unbinned data.");
        
        double[] cumulatived1 = getCumulativeArray(d1);
        double[] cumulatived2 = getCumulativeArray(d2);
        
        double dPlus=0;
        double dMinus=0;
        
        double data1=0.,data2=0.;
        double cumulative1=0.,cumulative2=0.;
        
        int j1=0,j2=0;
        
        int nPoints1 = d1.nPoints();
        int nPoints2 = d2.nPoints();
        
        boolean advance1, advance2;
        boolean flag1 = true, flag2 = true;
        
        while(true){
            
            advance1 = false;
            advance2 = false;
            
            data1 = d1.value(j1);
            data2 = d2.value(j2);
            if( data1 <= data2 ){
                cumulative1 = cumulatived1[j1];
                advance1 = true;
            }
            if(data2 <= data1){
                cumulative2 = cumulatived2[j2];
                advance2 = true;
            }
            
            double delta = (cumulative2 - cumulative1);
            if( delta > dPlus) dPlus = delta;
            if( -1*delta > dMinus) dMinus = -1*delta;

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
                double distance = dPlus + dMinus;
        
        double entries1 = entries(d1);
        double entries2 = entries(d2);
        double eventProduct = entries1*entries2;
        double eventSum = entries1+entries2;
        double rootEvt = Math.sqrt(eventProduct/eventSum);
        
        double arg = (rootEvt + 0.155 + 0.24/rootEvt) * distance;
        
        double arg2 = -2.*arg*arg;
        
        double factor = 2.;
        double factor2 = 0.;
        double product = 0.;
        double term = 0;
        double soFar = 0;
        double sum = 0;
        double argument = 0;
        
        for ( int i = 1; i < 100; i++ ) {
            argument = arg2 * i * i;
            term = factor * Math.exp( argument );
            factor2 = -2 * arg2 * i * i -1;
            product = factor2 * term;
            sum += product;
            if ( Math.abs(term) <= soFar * ACCURACY ||
            Math.abs(term) <= sum * CONVERGENCE ) return sum;
            soFar = Math.abs(term);
        }
        return 1.;
        
    }
    
}

