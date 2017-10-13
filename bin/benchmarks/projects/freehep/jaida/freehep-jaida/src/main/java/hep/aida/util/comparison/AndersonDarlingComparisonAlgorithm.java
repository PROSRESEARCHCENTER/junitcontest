package hep.aida.util.comparison;

import hep.aida.ext.IComparisonData;

/**
 *
 * @author The FreeHEP team @ SLAC.
 * Algorithm taken from http://www.ge.infn.it/geant4/analysis/HEPstatistics/
 *
 */
public class AndersonDarlingComparisonAlgorithm extends AbstractComparisonAlgorithm {

    private static final String[] names = new String[] {"AndersonDarling","AD"};
    private static final int dType = ANY_DATA;
    private static final int eType = ANY_NUMBER_OF_EVENTS;

    private static final double[] rejectionValues = new double[] {0.1, 0.05, 0.01, 0.001};
    private static final double[] criticalValues = new double[] {1.933, 2.492, 3.857, 4.356};
    
    public AndersonDarlingComparisonAlgorithm() {
        super(dType, eType);
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
    
    public double matchLowerBound() {
        return 0;
    }
    
    public double matchUpperBound() {
        double aL = rejectionLevel();
        for ( int i = 0; i < rejectionValues.length; i++ )
            if ( aL == rejectionValues[i])
                return criticalValues[i];
        return criticalValues[1];
    }
    
    public double quality(IComparisonData d1, IComparisonData d2) {
        
        if ( d1.type() != d2.type() )
            throw new IllegalArgumentException("Incompatible data sets: one is binned the other is unbinned. Cannot perform test");

        int nPoints1 = d1.nPoints();
        int nPoints2 = d2.nPoints();
        
        // Calculate the sum of weights
        double sumOfWeights1=0.,sumOfWeights2=0.;
        for(int i=0; i<nPoints1; i++)
            sumOfWeights1 += d1.entries(i);
        for(int i=0; i<nPoints2; i++)
            sumOfWeights2 += d2.entries(i);
        
        // The total sum of weights:
        int entries1 = entries(d1);
        int entries2 = entries(d2);
        double totalEntries = entries1 + entries2;
        double totalEntriesSquared= totalEntries*totalEntries;

        double equivalentEntries = (totalEntries -1)/totalEntriesSquared;
        
        double dt1=0., dt1_2=0., dt1_add1=0., dt1_add2=0.;
        double dt2=0., dt2_2=0., dt2_add2=0., dt2_add1=0.;        
        double A2=0.;
        double data1=0.,data2=0;
        double add1=0.,add2=0;
        double ratio1=0.,ratio2=0;
        double den=0.;

        int j1=0,j2=0;
        double sum1=0., sum2=0., sumH=0.;
        double frequencies1=0.,frequencies2=0., frequenciesL1=0., frequenciesL2=0.;
        double frequenciesTot1=0., frequenciesTot2=0., H=0., h=0.;

        boolean advance1, advance2;
        boolean flag1 = true, flag2 = true;
        
        while( true ) {
            
            advance1 = false;
            advance2 = false;
            
            data1 = d1.value(j1);
            data2 = d2.value(j2);
            
            if( data1 < data2 ){
                frequencies1 = d1.entries(j1);
                sum1 += frequencies1;
                frequenciesTot1 = ( (frequencies1 / 2) + sum1 - frequencies1 );
                advance1 = true;
                frequencies2=0;
                frequenciesTot2 = sum2;
            }
            else if (data1 == data2) {
                frequencies1 = d1.entries(j1);
                frequencies2 = d2.entries(j2);
                sum1 += frequencies1;
                sum2 += frequencies2;
                frequenciesTot1 = (frequencies1 / 2 + sum1 - frequencies1);
                frequenciesTot2 = (frequencies2 / 2 + sum2 - frequencies2);
                advance1 = true;
                advance2 = true;
            }
            else {
                frequencies2 = d2.entries(j2);
                sum2 += frequencies2;
                frequenciesTot2 = (frequencies2 / 2 + sum2 - frequencies2);
                advance2 = true;
                frequencies1=0;
                frequenciesTot1 = sum1;
            }
            
            h = frequencies1 + frequencies2;
            sumH += frequencies1 + frequencies2;
            H = ( h / 2 ) + sumH - h;
            
            if (H != 0 && H != 1){
            dt1_add1 = ( sumOfWeights1 + sumOfWeights2 ) * frequenciesTot1;
                dt1_add2 = ( sumOfWeights1 * H );
                dt1 = dt1_add1 - dt1_add2;
                dt1_2 = dt1 * dt1;
                
                dt2_add1 = ( sumOfWeights1 + sumOfWeights2 ) * frequenciesTot2;
                dt2_add2 = ( sumOfWeights2 * H );
                dt2 = dt2_add1 - dt2_add2;
                dt2_2 = dt2 * dt2;
                
                den = ( H * ( sumOfWeights1 + sumOfWeights2 - H ) - ( (sumOfWeights1 + sumOfWeights2) * h / 4));
                if ( den != 0 ) {
                    ratio1 +=  h * dt1_2  / den;
                    ratio2 += h * dt2_2 / den;
                }
            }
            
            
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
        
        add1 = ratio1 / sumOfWeights1;
        add2 = ratio2 / sumOfWeights2;
        
        
        A2 = equivalentEntries * ( add1 + add2);
        
        return A2;
    }
    
    public String[] algorithmNames() {
        return names;
    }
    
}
