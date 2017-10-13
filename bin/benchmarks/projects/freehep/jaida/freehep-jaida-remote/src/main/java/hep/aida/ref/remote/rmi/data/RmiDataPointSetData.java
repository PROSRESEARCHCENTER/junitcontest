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

public class RmiDataPointSetData implements Serializable {
    
    static final long serialVersionUID = -6142149214530165034L;
    private RmiAnnotationItem[] items;
    private int dimension;
    private String xAxisType = null;
    private String yAxisType = null;
    private long timeOfLastUpdate = 0;
    private double[] upperExtent;
    private double[] lowerExtent;
    private double[] values;
    private double[] plusErrors;
    private double[] minusErrors;
    
    /** Creates a new instance of RmiDataPointSetData */
    public RmiDataPointSetData() {
    }
    
    
    // Setters and getters
    
    public void setAnnotationItems(RmiAnnotationItem[] items) { this.items = items; }
    public RmiAnnotationItem[] getAnnotationItems() { return items; }
    
    public void setXAxisType(String type) { xAxisType = type; }
    public void setYAxisType(String type) { yAxisType = type; }
    public void setTimeOfLastUpdate(long t) { timeOfLastUpdate = t; }
    public String getXAxisType() { return xAxisType; }
    public String getYAxisType() { return yAxisType; }
    public long getTimeOfLastUpdate() { return timeOfLastUpdate; }
   
    public void setDimension(int d)        { this.dimension = d; }
    public void setUpperExtent(double[] d) { this.upperExtent = d; }
    public void setLowerExtent(double[] d) { this.lowerExtent = d; }
    public void setValues(double[] d)      { this.values = d; }
    public void setPlusErrors(double[] d)  { this.plusErrors = d; }
    public void setMinusErrors(double[] d) { this.minusErrors = d; }
    
    
    public int getDimension()        { return dimension; }
    public double[] getUpperExtent() { return upperExtent; }
    public double[] getLowerExtent() { return lowerExtent; }
    public double[] getValues()      { return values; }
    public double[] getPlusErrors()  { return plusErrors; }
    public double[] getMinusErrors() { return minusErrors; }
    
}
