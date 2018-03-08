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

public class RmiHist2DData implements Serializable {
    
    static final long serialVersionUID = 2496218387234007793L;
    private RmiAxis xAxis;
    private RmiAxis yAxis;
    private RmiAnnotationItem[] items;
    
    private double[][] heights = null;
    private double[][] errors = null;
    private int[][] entries = null;
    private double[][] means = null;
    private double[][] rmss = null;

    private double[][] binMeansX = null;
    private double[][] binRmssX = null;
    private double[][] binMeansY = null;
    private double[][] binRmssY = null;
    private double[][] weightedMeansY = null;
    
    private int[] binEntriesX;
    private int[] binEntriesY;
    private double[] binHeightsX;
    private double[] binHeightsY;
    
    private double meanX;
    private double rmsX;
    private double meanY;
    private double rmsY;

    private double equivalentBinEntries;
    private int nanEntries;

    private int inRangeEntries;
    private int extraEntries;
    private int minBinEntries;
    private int maxBinEntries;

    private double inRangeBinHeights;
    private double extraBinHeights;
    private double minBinHeights;
    private double maxBinHeights;
    
    /** Creates a new instance of RmiHist1DData */
    public RmiHist2DData() {
    }
    
    
    // Setters and getters for the histogram global information
    
    public void setAnnotationItems(RmiAnnotationItem[] items) { this.items = items; }
    public RmiAnnotationItem[] getAnnotationItems() { return items; }

    public void setXAxis(RmiAxis axis) { this.xAxis = axis; }
    public void setYAxis(RmiAxis axis) { this.yAxis = axis; }
    public void setMeanX(double mean) { this.meanX = mean; }
    public void setRmsX(double rms)   { this.rmsX = rms; }
    public void setMeanY(double mean) { this.meanY = mean; }
    public void setRmsY(double rms)   { this.rmsY = rms; }
    
    public RmiAxis getXAxis() { return xAxis; }
    public RmiAxis getYAxis() { return yAxis; }
    public double getMeanX() { return meanX; }
    public double getRmsX()  { return rmsX; }
    public double getMeanY() { return meanY; }
    public double getRmsY()  { return rmsY; }
    
    public void setInRangeEntries(int d) { inRangeEntries = d; }
    public void setExtraEntries(int d) { extraEntries = d; }
    public void setMinBinEntries(int d) { minBinEntries = d; }
    public void setMaxBinEntries(int d) { maxBinEntries = d; }
    public void setNanEntries(int i) { this.nanEntries = i; }
    public void setEquivalentBinEntries( double d) { equivalentBinEntries = d; }
    public void setInRangeBinHeights(double d) { inRangeBinHeights = d; }
    public void setExtraBinHeights(double d) { extraBinHeights = d; }
    public void setMinBinHeights(double d) { minBinHeights = d; }
    public void setMaxBinHeights(double d) { maxBinHeights = d; }
    
    public int getNanEntries() { return nanEntries; }
    public double getEquivalentBinEntries() { return equivalentBinEntries; }

    public int getInRangeEntries() { return inRangeEntries; }
    public int getExtraEntries() { return extraEntries; }
    public int getMinBinEntries() { return minBinEntries; }
    public int getMaxBinEntries() { return maxBinEntries; }

    public double getInRangeBinHeights() { return inRangeBinHeights; }
    public double getExtraBinHeights() { return extraBinHeights; }
    public double getMinBinHeights() { return minBinHeights; }
    public double getMaxBinHeights() { return maxBinHeights; }
    
    
    
    // Setters and getters for the histogram bin information
    
    public void setBinEntries(int[][] entries) { this.entries = entries; }
    public void setBinHeights(double[][] heights) { this.heights = heights; }
    public void setBinErrors(double[][] errors) { this.errors = errors; }
    public void setBinMeans(double[][] means) { this.means = means; }
    public void setBinRmss(double[][] rmss) { this.rmss = rmss; }

    public int[][] getBinEntries() { return entries; }
    public double[][] getBinHeights() { return heights; }
    public double[][] getBinErrors() { return errors; }
    public double[][] getBinMeans() { return means; }
    public double[][] getBinRmss() { return rmss; }
    
    public void setBinMeansX(double[][] d) { binMeansX = d; }
    public void setBinRmssX(double[][] d) { binRmssX = d; }
    public void setBinMeansY(double[][] d) { binMeansY = d; }
    public void setBinRmssY(double[][] d) { binRmssY =d ; }
    
    public double[][] getBinMeansX() { return binMeansX; }
    public double[][] getBinRmssX() { return binRmssX; }
    public double[][] getBinMeansY() { return binMeansY; }
    public double[][] getBinRmssY() { return binRmssY; }
    
    public void setBinEntriesX(int[] d) { binEntriesX = d; }
    public void setBinEntriesY(int[] d) { binEntriesY = d; }
    public void setBinHeightsX(double[] d) { binHeightsX = d; }
    public void setBinHeightsY(double[] d) { binHeightsY = d; }

    public int[] getBinEntriesX() { return binEntriesX; }
    public int[] getBinEntriesY() { return binEntriesY; }
    public double[] getBinHeightsX() { return binHeightsX; }
    public double[] getBinHeightsY() { return binHeightsY; }
}
