/*
 * RmiHist1DData.java
 *
 * Created on October 26, 2003, 9:15 PM
 */

package hep.aida.ref.remote.rmi.data;

import hep.aida.IAnalysisFactory;
import hep.aida.ICloud1D;
import hep.aida.ITree;
import hep.aida.ref.remote.RemoteCloud1D;
import hep.aida.ref.remote.rmi.converters.RmiCloud1DConverter;

import java.io.Serializable;

/**
 * This class contains data for IHistogram1D
 *
 * @author  serbo
 */

public class RmiCloud1DData implements Serializable {
    
    static final long serialVersionUID = 3521807348319671978L;
    private RmiHist1DData hist = null;
    private boolean isConverted = false;
    private int maxEntries = 0;
    private int entries    = 0;
    private int nanEntries    = 0;
    private double sumOfWeights = 0;
    
    private double lowerEdge = Double.NaN;
    private double upperEdge = Double.NaN;
    private double equivalentBinEntries;
    private double mean = Double.NaN;
    private double rms  = Double.NaN;
   
    private double[] values  = null;
    private double[] weights = null;
    
    /** Creates a new instance of RmiHist1DData */
    public RmiCloud1DData() {
    }
    
    
    // Setters and getters for the global information
    
    public void setEquivalentBinEntries(double d) { this.equivalentBinEntries = d; }
    public double getEquivalentBinEntries() { return equivalentBinEntries; }

    public void setHist(RmiHist1DData hist) { this.hist = hist; }
    public RmiHist1DData getHist() { return hist; }

    public void setConverted(boolean b) { this.isConverted = b; }
    public void setMaxEntries(int i) { this.maxEntries = i; }
    public void setEntries(int i) { this.entries = i; }
    public void setNanEntries(int i) { this.nanEntries = i; }
    public void setSumOfWeights(double d) { this.sumOfWeights = d; }
    
    public void setLowerEdge(double d) { this.lowerEdge = d; }
    public void setUpperEdge(double d) { this.upperEdge = d; }
    public void setMean(double d) { this.mean = d; }
    public void setRms(double d)  { this.rms = d; }
    
    public boolean getConverted() { return isConverted; }
    public int getMaxEntries() { return maxEntries; }
    public int getEntries() { return entries; }
    public int getNanEntries() { return nanEntries; }
    public double getSumOfWeights() { return sumOfWeights; }
    
    public double getLowerEdge() { return lowerEdge; }
    public double getUpperEdge() { return upperEdge; }    
    public double getMean() { return mean; }
    public double getRms()  { return rms; }
    
    
    
    // Setters and getters for the bin information
    
    public void setValues(double[] values)   { this.values = values; }
    public void setWeights(double[] weights) { this.weights = weights; }

    public double[] getValues()  { return values; }
    public double[] getWeights() { return weights; }

    
    
    public static void main(String[] args) throws Exception {
        
        IAnalysisFactory af = IAnalysisFactory.create();
        ITree tree = af.createTreeFactory().create();
        ICloud1D c1 = af.createHistogramFactory(tree).createCloud1D("Cloud-1D");
        
        java.util.Random rand = new java.util.Random();
        for (int i=0; i<1000; i++) c1.fill(rand.nextGaussian());
        
        RmiCloud1DConverter converter = RmiCloud1DConverter.getInstance();
        RmiCloud1DData data = (RmiCloud1DData) converter.extractData(c1);
        
        RemoteCloud1D rc1 = (RemoteCloud1D) converter.createAidaObject("Cloud-1D");
        
        converter.updateAidaObject(rc1, data);
        
        System.out.println("Entries="+rc1.entries());
    }
}
