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

public class RmiCloud2DData implements Serializable {
    
    static final long serialVersionUID = 5957778805011393373L;
    private RmiHist2DData hist = null;
    private boolean isConverted = false;
    private int maxEntries = 0;
    private int entries    = 0;
    private int nanEntries    = 0;
    private double sumOfWeights = 0;
    
    private double lowerEdgeX = Double.NaN;
    private double lowerEdgeY = Double.NaN;
    private double upperEdgeX = Double.NaN;
    private double upperEdgeY = Double.NaN;
    private double meanX = Double.NaN;
    private double meanY = Double.NaN;
    private double rmsX  = Double.NaN;
    private double rmsY  = Double.NaN;
   
    private double[] valuesX  = null;
    private double[] valuesY  = null;
    private double[] weights  = null;
    
    /** Creates a new instance of RmiHist1DData */
    public RmiCloud2DData() {
    }
    
    
    // Setters and getters for the global information
    
    public void setHist(RmiHist2DData hist) { this.hist = hist; }
    public RmiHist2DData getHist() { return hist; }

    public void setConverted(boolean b) { this.isConverted = b; }
    public void setMaxEntries(int i) { this.maxEntries = i; }
    public void setEntries(int i) { this.entries = i; }
    public void setNanEntries(int i) { this.nanEntries = i; }
    public void setSumOfWeights(double d) { this.sumOfWeights = d; }
    
    public void setLowerEdgeX(double d) { this.lowerEdgeX = d; }
    public void setLowerEdgeY(double d) { this.lowerEdgeY = d; }
    public void setUpperEdgeX(double d) { this.upperEdgeX = d; }
    public void setUpperEdgeY(double d) { this.upperEdgeY = d; }
    public void setMeanX(double d) { this.meanX = d; }
    public void setMeanY(double d) { this.meanY = d; }
    public void setRmsX(double d)  { this.rmsX = d; }
    public void setRmsY(double d)  { this.rmsY = d; }
    
    public boolean getConverted() { return isConverted; }
    public int getMaxEntries() { return maxEntries; }
    public int getEntries() { return entries; }
    public int getNanEntries() { return nanEntries; }
    public double getSumOfWeights() { return sumOfWeights; }
    
    public double getLowerEdgeX() { return lowerEdgeX; }
    public double getLowerEdgeY() { return lowerEdgeY; }
    public double getUpperEdgeX() { return upperEdgeX; }    
    public double getUpperEdgeY() { return upperEdgeY; }    
    public double getMeanX() { return meanX; }
    public double getMeanY() { return meanY; }
    public double getRmsX()  { return rmsX; }
    public double getRmsY()  { return rmsY; }
    
    
    
    // Setters and getters for the bin information
    
    public void setValuesX(double[] values)   { this.valuesX = values; }
    public void setValuesY(double[] values)   { this.valuesY = values; }
    public void setWeights(double[] weights) { this.weights = weights; }

    public double[] getValuesX()  { return valuesX; }
    public double[] getValuesY()  { return valuesY; }
    public double[] getWeights() { return weights; }

}
