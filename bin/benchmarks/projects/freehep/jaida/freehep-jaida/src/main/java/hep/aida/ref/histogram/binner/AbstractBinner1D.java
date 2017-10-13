/*
 * AbstractBinner1D.java
 *
 * This class handles everything, except the errors
 */

package hep.aida.ref.histogram.binner;

import hep.aida.ref.histogram.HistUtils;

/**
 *
 * @author  The AIDA team at SLAC
 *
 */
public abstract class AbstractBinner1D implements Binner1D{
    
    protected int[] entries;
    protected double[] binCenter;
    protected double[] sumW;
    protected double[] sumWW;
    protected double[] sumXW;
    protected double[] sumXXW;
    
    protected int bins;
    
    /**
     * Creates a new instance of OneDBinner.
     *
     */
    public AbstractBinner1D(int bins) {
        if (bins < 0) throw new IllegalArgumentException("Number of bins cannot be negative!!! "+bins);
        setNumberOfBins(bins);
    }
    
    public AbstractBinner1D(Binner1D binner) {
        if (binner.bins() < 0) throw new IllegalArgumentException("Number of bins cannot be negative!!! "+bins);
        initBinner(binner);
    }
    
    // Service methods
    
    public void initBinner(Binner1D binner) {
        setNumberOfBins(binner.bins());
        int e = 0;
        double h = 0;
        double er = 0;
        double m = 0;
        double w = 0;
        double r = 0;
        for (int i=0; i<bins; i++) {
            setBinCenter(i, binner.binCenter(i));
            e = binner.entries(i);
            h = binner.height(i);
            w = binner.sumWW(i);
            m = binner.sumXW(i);
            r = binner.sumXXW(i);
            entries[i] = e;
            if ( HistUtils.isValidDouble(h) ) sumW[i]   = h;
            if ( HistUtils.isValidDouble(w) ) sumWW[i]  = w;
            if ( HistUtils.isValidDouble(m) ) sumXW[i]  = m;
            if ( HistUtils.isValidDouble(r) ) sumXXW[i] = r;
        }
    }
    
    protected void createArrays( int n ) {
        entries = new int[n];
        binCenter = new double[n];
        sumW   = new double[n];
        sumWW  = new double[n];
        sumXW  = new double[n];
        sumXXW = new double[n];
    }

    
    private void setNumberOfBins( int bins ) {
        this.bins = bins;
        createArrays(bins);
    }
    
    public String toString() {
        String tmp = "";
        for (int i=0; i<bins; i++) {
            tmp += i+"  b="+binCenter(i)+", e="+entries(i)+",  h="+height(i)+",  m="+mean(i)+",  r="+rms(i)+"\n";
        }
        tmp += "\n";
        return tmp;
    }
    
    
    // Binner1D methods
    
    /**
     * This method sets new center of the bin and 
     * initializes other bin data
     */
    public void setBinCenter(int bin, double x) {
        binCenter[bin] = x;
        entries[bin] = 0;
        sumW[bin]   = 0;
        sumWW[bin]  = 0;
        sumXW[bin]  = 0;
        sumXXW[bin] = 0;
    }
    
    public double binCenter(int bin) {
        return binCenter[bin];
    }
    
    public void clear() {
        for (int i=0; i<bins; i++) {
            binCenter[i] = 0;
            entries[i] = 0;
            sumW[i]   = 0;
            sumWW[i]  = 0;
            sumXW[i]  = 0;
            sumXXW[i] = 0;
        }
    }
    
    public int bins() { return bins; }
    
    public void fill( int bin, double x, double weight) {
        double delta = x - binCenter[bin];
        entries[bin]++;
        sumW[bin]       += weight;
        sumWW[bin]      += weight*weight;        
        sumXW[bin]      += delta*weight;
        sumXXW[bin]     += delta*delta*weight;
    }
    
    public int entries(int bin) {
        return entries[bin];
    }
    
    public double height(int bin) {
        return sumW[bin];
    }
    
    public double mean(int bin) {
        double h = height(bin);
        if ( h != 0 ) return sumXW[bin]/h;
        return Double.NaN;
    }
    
    public double rms(int bin) {
        double h = height(bin);
        double m = sumXW[bin];
        double r = sumXXW[bin];
        if ( h != 0 ) return Math.sqrt( Math.abs( (r - m*m/h)/h ) );
        return Double.NaN;
    }
    
    public void scale( double scaleFactor ) {
        for ( int bin = 0; bin < bins; bin++ ) {
            sumW[bin]       *= scaleFactor;
            sumWW[bin]      *= scaleFactor*scaleFactor;
            sumXW[bin]      *= scaleFactor;
            sumXXW[bin]      *= scaleFactor;
        }
    }
    
    public double sumWW(int bin) {
        return sumWW[bin];
    }
    
    public double sumXW(int bin) {
        return sumXW[bin];
    }

    public double sumXXW(int bin) {
        return sumXXW[bin];
    }
    
    public void setBinContent(int bin, double binCenter, int entries, double height, double plusError, double minusError, double sWW, double sXW, double sXXW) {
        this.entries[bin] = entries;
        
        if ( ! Double.isNaN(height) )
            sumW[bin] = height;
        else
            sumW[bin] = entries;
        
        if ( ! Double.isNaN(sWW) )
            this.sumWW[bin] = sWW;
        else if ( ! Double.isNaN(plusError) )
            this.sumWW[bin] = plusError*plusError;
        else
            this.sumWW[bin] = entries;
        
        double d = binCenter - this.binCenter(bin);
        if (d != 0) {
            sXXW = sXXW + d*(2*sXW + d*sumW[bin]);
            sXW = sXW + d*sumW[bin];
        }
        sumXW[bin]  = sXW;
        sumXXW[bin] = sXXW;
    }
    
}
