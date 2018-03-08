/*
 * RmiHist1DData.java
 *
 * Created on October 26, 2003, 9:15 PM
 */

package hep.aida.ref.remote.rmi.data;

import java.io.Serializable;

/**
 * This class contains data for IHistogram1D
 *
 * @author  serbo
 */

public class RmiHist1DData implements Serializable {
    
    static final long serialVersionUID = -659964317136800708L;
    private RmiAxis axis;
    private RmiAnnotationItem[] items;
    private double mean;
    private double rms;
   
    private double equivalentBinEntries = 0;
    private int nanEntries   = 0;

    private int[] entries    = null;
    private double[] heights = null;
    private double[] errors  = null;
    private double[] means   = null;
    private double[] rmss    = null;
    
    /** Creates a new instance of RmiHist1DData */
    public RmiHist1DData() {
    }
    
    
    // Setters and getters for the histogram global information
    
    public void setAxis(RmiAxis axis) { this.axis = axis; }
    public void setAnnotationItems(RmiAnnotationItem[] items) { this.items = items; }
    public void setMean(double mean) { this.mean = mean; }
    public void setRms(double rms)   { this.rms = rms; }
    
    public void setEquivalentBinEntries(double d) { this.equivalentBinEntries = d; }
    public double getEquivalentBinEntries() { return equivalentBinEntries; }

    public void setNanEntries(int i) { this.nanEntries = i; }
    public int getNanEntries() { return nanEntries; }

    public RmiAxis getAxis() { return axis; }
    public RmiAnnotationItem[] getAnnotationItems() { return items; }
    public double getMean() { return mean; }
    public double getRms()  { return rms; }
    
    
    
    // Setters and getters for the histogram bin information
    
    public void setBinEntries(int[] entries) { this.entries = entries; }
    public void setBinHeights(double[] heights) { this.heights = heights; }
    public void setBinErrors(double[] errors) { this.errors = errors; }
    public void setBinMeans(double[] means) { this.means = means; }
    public void setBinRmss(double[] rmss) { this.rmss = rmss; }

    public int[] getBinEntries() { return entries; }
    public double[] getBinHeights() { return heights; }
    public double[] getBinErrors() { return errors; }
    public double[] getBinMeans() { return means; }
    public double[] getBinRmss() { return rmss; }
    
}
