package hep.aida.util.comparison;

import hep.aida.ext.IComparisonData;

/**
 *
 * @author The FreeHEP team @ SLAC.
 * Algorithm taken from http://www.ge.infn.it/geant4/analysis/HEPstatistics/
 *
 */
public class FiszCramerVonMisesComparisonAlgorithm extends AbstractComparisonAlgorithm {

    private static final double[] rejectionValues = new double[] {0.1, 0.05, 0.01, 0.001};
    private static final double[] criticalValues = new double[] {0.347, 0.461, 0.743, 1.168};
    
    private static final String[] names = new String[] {"FiszCramerVonMises","FCVM"};
    private static final int dType = ANY_DATA;
    private static final int eType = ANY_NUMBER_OF_EVENTS;
    
    public FiszCramerVonMisesComparisonAlgorithm() {
        super(dType, eType);
    }

    public String[] algorithmNames() {
        return names;
    }
    
    public double quality(IComparisonData d1, IComparisonData d2) {
        
        if ( d1.type() != d2.type() )
            throw new IllegalArgumentException("Incompatible data. One is binned and the other unbinned. Cannot compare.");
        
        double[] cumulativeWeights1 = getCumulativeArray(d1);
        double[] cumulativeWeights2 = getCumulativeArray(d2);
        
        int nPoints1 = d1.nPoints();
        int nPoints2 = d2.nPoints();

        int j1=0, j2=0;
        double data1 = 0., data2 = 0.;
        double cumulative1 = 0., cumulative2 = 0.;
        double t=0.;
        
        double sumOfWeightsSquared1 = 0;
        for ( int i = 0; i < nPoints1; i++ )
            sumOfWeightsSquared1 += Math.pow(d1.entries(i),2);
        
        double sumOfWeightsSquared2 = 0;
        for ( int i = 0; i < nPoints2; i++ )
            sumOfWeightsSquared2 += Math.pow(d2.entries(i),2);
        
        
        
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
            if( data2 <= data1){
                cumulative2 = cumulativeWeights2[j2];
                advance2 = true;
            }
            t += ( cumulative2 - cumulative1) * ( cumulative2 - cumulative1);


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

        // Multiply by two for binned data.
        if ( d1.type() == IComparisonData.BINNED_DATA )
            t *= 2;

        double entries1 = (double)entries(d1);
        double entries2 = (double)entries(d2);

        double entriesProduct = entries1 * entries2;
        double totalentriesSquared = ( entries1 + entries2 ) * ( entries1 + entries2 );
        double r = (entriesProduct / totalentriesSquared);

        
        double s1 = sumOfWeightsSquared1;
        double s2 = sumOfWeightsSquared2;
        
        double s = s1+s2;
        
        double e = entries1+entries2;
        double val = t * r * s / e;
        return val;
    }
    
    public void setRejectionLevel() {
        super.setRejectionLevel();
        double aL = rejectionLevel();
        boolean found = false;
        for ( int i = 0; i < rejectionValues.length; i++ )
            if ( aL == rejectionValues[i]) {
                found = true;
                break;
            }
        if ( ! found ) {
            String levels = "";
            for ( int i = 0; i < rejectionValues.length; i++ )
                levels += rejectionValues[i]+" ";
            System.out.println("Algorithm "+algorithmNames()[0]+" can currently support ONLY the following rejection levels: "+levels);
        }
    }
    
    public double matchUpperBound() {
        double aL = rejectionLevel();
        for ( int i = 0; i < rejectionValues.length; i++ )
            if ( aL == rejectionValues[i])
                return criticalValues[i];
        return criticalValues[1];
    }
    
    public double matchLowerBound() {
        return 0;
    }
}
