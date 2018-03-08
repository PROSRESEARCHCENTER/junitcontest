/*
 * EfficiencyBinner1D.java
 *
 * Created on July 22, 2002, 10:19 AM
 */

package hep.aida.ref.histogram.binner;

/**
 *
 * @author  The AIDA team at SLAC.
 *
 */
public class EfficiencyBinner1D extends AbstractBinner1D {
    private double[] error;
    
    /**
     * Creates a new instance of EfficiencyBinner1D.
     */
    
    public EfficiencyBinner1D(int bins) {
        super(bins);
    }
    
    public EfficiencyBinner1D(Binner1D binner) {
        super(binner);
    }

    // Service methodes
    
    protected void createArrays( int n ) {
        super.createArrays(n);
        error = new double[n];
    }
    
    // Binner1d methods
    
    public double plusError(int bin) {
        return EfficiencyBinnerUtils.H95CL(error[bin],entries[bin],1);
    }
    
    public double minusError(int bin) {
        return EfficiencyBinnerUtils.H95CL(error[bin],entries[bin],2);
    }
   
    public void setBinCenter(int bin, double x) {
        super.setBinCenter(bin, x);
        error[bin] = 0;
    }
    
   public void clear() {
       super.clear();
        for (int i=0; i<bins; i++) {
            error[i] = 0;
        }
   }
   
    public void fill( int bin, double x, double weight) {
        super.fill(bin, x, weight);
    }
    
    public void setBinContent(int bin, int entries, double height, double plusError, double minusError, double sumWW, double sumXW, double sumXXW) {
        setBinContent(bin, entries, height, plusError, minusError, sumWW, sumXW, sumXXW);
        
        if ( ! Double.isNaN(height) )
            error[bin] = height*entries;
        else
            error[bin] = entries*entries;
     }
    
 }

