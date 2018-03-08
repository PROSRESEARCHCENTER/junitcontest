/*
 * RemoteHistogram1D.java
 *
 * Created on May 28, 2003, 5:38 PM
 */

package hep.aida.ref.remote;

import hep.aida.IAxis;
import hep.aida.IHistogram2D;
import hep.aida.dev.IDevMutableStore;
import hep.aida.ref.Annotation;
import hep.aida.ref.ReadOnlyException;
import hep.aida.ref.event.HistogramEvent;
import hep.aida.ref.histogram.FixedAxis;

/**
 * This is implementation of IHistogram2D that can not be modified
 * by the user. This is a simple implementation that does not guarantee
 * internal consistency. So extra care should be taken when setting
 * data for this class.
 * This Class is designed to work with the IDevMutableStore, but can
 * work with other Stores - overwrite makeSureDataIsValid() method.
 * Note: use "putTitle" method, AIDA's "setTitle" is disabled.
 * Don't forget to call "setDataValid" after you fill new data, as
 * only this method fires events to notify AIDAListeners about change.
 *
 * Also please note that in our current AIDA implementation:
 *  UNDERFLOW_BIN = -2, OVERFLOW_BIN = -1, bins = [0, nBins-1]
 * But we keep them here as:
 *  UNDERFLOW_BIN = 0, OVERFLOW_BIN = nBins+1, bins = [1, nBins]
 * 
 * @author  serbo
 */
public class RemoteHistogram2D extends RemoteManagedObject implements IHistogram2D {
    
    private Annotation annotation = null;
    private IAxis xAxis = null;
    private IAxis yAxis = null;
    
    private double[][] heights = null;
    private double[][] errors = null;
    private int[][] entries = null;

    private double[][] binMeansX = null;
    private double[][] binRmssX = null;
    private double[][] binMeansY = null;
    private double[][] binRmssY = null;
    
    private int[] binEntriesX;
    private int[] binEntriesY;
    private double[] binHeightsX;
    private double[] binHeightsY;
    
    private double meanX = 0;
    private double rmsX = 0;
    private double meanY = 0;
    private double rmsY = 0;

    private double equivalentBinEntries = 0;

    private int inRangeEntries = DEFAULT_INT;
    private int extraEntries = 0;
    private int nanEntries = DEFAULT_INT;
    private int minBinEntries = DEFAULT_INT;
    private int maxBinEntries = DEFAULT_INT;
    
    private double inRangeBinHeights = DEFAULT_DOUBLE;
    private double extraBinHeights = 0;
    private double minBinHeights = DEFAULT_DOUBLE;
    private double maxBinHeights = DEFAULT_DOUBLE;
    
    
    /** Creates a new instance of RemoteHistogram1D */
    public RemoteHistogram2D(String name) {
        this(null, name);
    }
    
    public RemoteHistogram2D(IDevMutableStore store, String name) {
        super(name);
        aidaType = "IHistogram2D";
        this.store = store;
        annotation = new Annotation(); 
        annotation.setFillable(true);
        annotation.addItem(Annotation.titleKey,name,true);
        annotation.setFillable(false);
        dataIsValid = false;
        
        xAxis = new FixedAxis(1, 0, 1);
        yAxis = new FixedAxis(1, 0, 1);
    }
    
    public RemoteHistogram2D(IDevMutableStore store, String name, String title) {
        super(name);
        aidaType = "IHistogram2D";
        this.store = store;
        annotation = new Annotation();
        annotation.setFillable(true);
        annotation.addItem(Annotation.titleKey,title,true);
        annotation.setFillable(false);
        dataIsValid = false;
        xAxis = new FixedAxis(1, 0, 1);
        yAxis = new FixedAxis(1, 0, 1);
    }
    
    
    // AIDAObservable methods
    protected java.util.EventObject createEvent()
    {
       return new HistogramEvent(this);
    }

    
    // Service methods
    
    public void setTreeFolder(String treeFolder) {
        super.setTreeFolder(treeFolder);
        
        boolean flbl = annotation.isFillable();
        if (!flbl) annotation.setFillable(true);
        if (annotation.hasKey(Annotation.fullPathKey)) {
            annotation.setValue(Annotation.fullPathKey, treePath);
        } else {
            annotation.addItem(Annotation.fullPathKey, treePath, true);
        }
        if (!flbl) annotation.setFillable(false);
    }
    
    private int convertAIDAIndex(IAxis axis, int index) {
        int mi;
        if ( index == IAxis.UNDERFLOW_BIN )
            mi = 0;
        else if ( index == IAxis.OVERFLOW_BIN )
            mi = axis.bins()+1;
        else
            mi = index + 1;
        return mi;
    }
    
    public void setAnnotation(Annotation a) { annotation = a; }
    
    public void setXAxis(int bins, double min, double max) {
        xAxis = new FixedAxis(bins, min, max);
    }
    public void setYAxis(int bins, double min, double max) {
        yAxis = new FixedAxis(bins, min, max);
    }

    public void setRmsX(double rms) {
        this.rmsX = rms;
    }
    public void setRmsY(double rms) {
        this.rmsY = rms;
    }
    
    public void setMeanX(double mean) {
        this.meanX = mean;
    }
    public void setMeanY(double mean) {
        this.meanY = mean;
    }
    
    
    public void setHeights(double[][] h) {
        if (h == null || h.length == 0) {
            heights = null;
            inRangeBinHeights = DEFAULT_DOUBLE;
            extraBinHeights = DEFAULT_DOUBLE;
            minBinHeights = DEFAULT_DOUBLE;
            maxBinHeights = DEFAULT_DOUBLE;
            return;
        }
        heights = h;
    }

    public void setEntries(int[][] h) {
        if (h == null || h.length == 0) {
            entries = null;
            inRangeEntries = DEFAULT_INT;
            extraEntries = DEFAULT_INT;
            equivalentBinEntries = DEFAULT_DOUBLE;
            return;
        }
        entries = h;
    }

    public void setErrors(double[][] h) {
        if (h == null || h.length == 0) {
            errors = null;
            return;
        }
        errors = h;
    }

    public void setBinMeansX(double[][] h) {
        if (h == null || h.length == 0) {
            binMeansX = null;
            return;
        }
        binMeansX = h;
    }

    public void setBinRmssX(double[][] h) {
        if (h == null || h.length == 0) {
            binRmssX = null;
            return;
        }
        binRmssX = h;
    }

    public void setBinMeansY(double[][] h) {
        if (h == null || h.length == 0) {
            binMeansY = null;
            return;
        }
        binMeansY = h;
    }

    public void setBinRmssY(double[][] h) {
        if (h == null || h.length == 0) {
            binRmssY = null;
            return;
        }
        binRmssY = h;
    }

    public void setBinEntriesX(int[] h) {
        if (h == null || h.length == 0) {
            binEntriesX = null;
            return;
        }
        binEntriesX = h;
    }
    public void setBinEntriesY(int[] h) {
        if (h == null || h.length == 0) {
            binEntriesY = null;
            return;
        }
        binEntriesY = h;
    }
    public void setBinHeightsX(double[] h) {
        if (h == null || h.length == 0) {
            binHeightsX = null;
            return;
        }
        binHeightsX = h;
    }
    public void setBinHeightsY(double[] h) {
        if (h == null || h.length == 0) {
            binHeightsY = null;
            return;
        }
        binHeightsY = h;
    }
    
    public void setEquivalentBinEntries( double d) { equivalentBinEntries = d; }
    public void setNanEntries(int d) { nanEntries = d;  }

    public void setInRangeEntries(int d) { inRangeEntries = d; }
    public void setExtraEntries(int d) { extraEntries = d; }
    public void setMinBinEntries(int d) { minBinEntries = d; }
    public void setMaxBinEntries(int d) { maxBinEntries = d; }
    
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
    
    public int[][] getBinEntries() { return entries; }
    public double[][] getBinHeights() { return heights; }
    public double[][] getBinErrors() { return errors; }
    
    public double[][] getBinMeansX() { return binMeansX; }
    public double[][] getBinRmssX() { return binRmssX; }
    public double[][] getBinMeansY() { return binMeansY; }
    public double[][] getBinRmssY() { return binRmssY; }
    
    public int[] getBinEntriesX() { return binEntriesX; }
    public int[] getBinEntriesY() { return binEntriesY; }
    public double[] getBinHeightsX() { return binHeightsX; }
    public double[] getBinHeightsY() { return binHeightsY; }   

    
    // IBaseHistogram methods
    
    public hep.aida.IAnnotation annotation() {
        makeSureDataIsValid();
        return annotation;
    }
    
    public int dimension() {
        //makeSureDataIsValid();
        return 2;
    }
    
    public int entries() {
        makeSureDataIsValid();
        if (entries == null && heights != null) return (int) inRangeBinHeights;
        return inRangeEntries;
    }
    
    public void reset() throws java.lang.RuntimeException {
        throw new ReadOnlyException();
    }
    
    public String title() {
        //makeSureDataIsValid();
        return annotation.value(Annotation.titleKey);
    }
    
    public void setTitle(String title) throws java.lang.IllegalArgumentException {
        if (!fillable) throw new ReadOnlyException();
        annotation.setFillable(true);
        annotation.setValue(Annotation.titleKey,title);        
        annotation.setFillable(false);
    }
        

    
    // IHistogram methods
    
    public double equivalentBinEntries() {
        makeSureDataIsValid();
        if (entries == null) return DEFAULT_INT;
        return equivalentBinEntries;
    }
    
    public int allEntries() {
        makeSureDataIsValid();
        if (entries == null) {
            if (heights != null) return (int) sumAllBinHeights();
            else return DEFAULT_INT;
        }
        return (entries() + extraEntries());
    }
    
    public int extraEntries() {
        makeSureDataIsValid();
        if (entries == null) {
            if (heights != null) return (int) extraBinHeights;
            else return DEFAULT_INT;
        }
        return extraEntries;
    }
    
    public int nanEntries() {
        makeSureDataIsValid();
        return nanEntries;
    }
    
    public void scale(double param) throws java.lang.IllegalArgumentException {
        throw new ReadOnlyException();
    }
    
    public double maxBinHeight() {
        makeSureDataIsValid();
        if ( heights == null && entries != null) return (double) maxBinEntries;
        return maxBinHeights;
    }
    
    public double minBinHeight() {
        makeSureDataIsValid();
        if ( heights == null && entries != null) return (double) minBinEntries;
        return minBinHeights;
    }

    public double sumAllBinHeights() {
        makeSureDataIsValid();
        if (heights == null) {
            if (entries != null) return (double) allEntries();
            else return DEFAULT_DOUBLE;
        }
        return (sumBinHeights() + sumExtraBinHeights());
    }
    
    public double sumBinHeights() {
        makeSureDataIsValid();
        if ( heights == null && entries != null) return (double) inRangeEntries;
        return inRangeBinHeights;
    }
    
    public double sumExtraBinHeights() {
        makeSureDataIsValid();
        if (heights == null) {
            if (entries != null) return (double) extraEntries;
            else return DEFAULT_DOUBLE;
        }
        return extraBinHeights;
    }

    
    // IHistogram2D methods
    
    public void add(hep.aida.IHistogram2D iHistogram2D) throws java.lang.IllegalArgumentException {
        throw new ReadOnlyException();
    }
    
    public int binEntries(int param1, int param2) throws java.lang.IllegalArgumentException {
        makeSureDataIsValid();
        if (entries == null) {
            if (heights != null) return (int) binHeight(param1, param2);
            else return DEFAULT_INT;
        }
        return entries[convertAIDAIndex(xAxis, param1)][convertAIDAIndex(yAxis, param2)];
    }
    
    public int binEntriesX(int param) throws java.lang.IllegalArgumentException {
        makeSureDataIsValid();
        if (binEntriesX == null) {
            if (binHeightsX != null) return (int) binHeightX(param);
            else return DEFAULT_INT;
        }
        return binEntriesX[convertAIDAIndex(xAxis, param)];
    }
    
    public int binEntriesY(int param) throws java.lang.IllegalArgumentException {
        makeSureDataIsValid();
        if (binEntriesY == null) {
            if (binHeightsY != null) return (int) binHeightY(param);
            else return DEFAULT_INT;
        }
        return binEntriesX[convertAIDAIndex(xAxis, param)];
    }
    
    public double binError(int param1, int param2) throws java.lang.IllegalArgumentException {
        makeSureDataIsValid();
        if (errors == null) return Math.sqrt(binHeight(param1, param2));
        return errors[convertAIDAIndex(xAxis, param1)][convertAIDAIndex(yAxis, param2)];
    }
    
    public double binHeight(int param1, int param2) throws java.lang.IllegalArgumentException {
        makeSureDataIsValid();
        if (heights == null) {
            if (entries != null) return (double) binEntries(param1, param2);
            else return DEFAULT_DOUBLE;
        }
        return heights[convertAIDAIndex(xAxis, param1)][convertAIDAIndex(yAxis, param2)];
    }
    
    public double binHeightX(int param) throws java.lang.IllegalArgumentException {
        makeSureDataIsValid();
        if (binHeightsX == null) {
            if (binEntriesX != null) return (double) binEntriesX(param);
            else return DEFAULT_DOUBLE;
        }
        return binHeightsX[convertAIDAIndex(xAxis, param)];
    }
    
    public double binHeightY(int param) throws java.lang.IllegalArgumentException {
        makeSureDataIsValid();
        if (binHeightsY == null) {
            if (binEntriesY != null) return (double) binEntriesY(param);
            else return DEFAULT_DOUBLE;
        }
         return binHeightsY[convertAIDAIndex(yAxis, param)];
    }
    
    public double binMeanX(int param1, int param2) throws java.lang.IllegalArgumentException {
        makeSureDataIsValid();
        double d = DEFAULT_DOUBLE;
        if (binMeansX == null) {
            if (param1 == IAxis.UNDERFLOW_BIN) d = Double.NEGATIVE_INFINITY;
            else if (param1 == IAxis.OVERFLOW_BIN) d = Double.POSITIVE_INFINITY;
            else d = xAxis.binCenter(param1);
        } else
            d = binMeansX[convertAIDAIndex(xAxis, param1)][convertAIDAIndex(yAxis, param2)];
        return d;
    }
    
    public double binMeanY(int param1, int param2) throws java.lang.IllegalArgumentException {
        makeSureDataIsValid();
        double d = DEFAULT_DOUBLE;
        if (binMeansY == null) {
            if (param2 == IAxis.UNDERFLOW_BIN) d = Double.NEGATIVE_INFINITY;
            else if (param2 == IAxis.OVERFLOW_BIN) d = Double.POSITIVE_INFINITY;
            else d = yAxis.binCenter(param2);
        } else
            d = binMeansY[convertAIDAIndex(xAxis, param1)][convertAIDAIndex(yAxis, param2)];
        return d;
    }
    
    public double binRmsX(int param1, int param2) throws java.lang.IllegalArgumentException {
        makeSureDataIsValid();
        double d = DEFAULT_DOUBLE;
        if (binRmssX == null) {
            if (param1 == IAxis.UNDERFLOW_BIN) d = DEFAULT_DOUBLE;
            else if (param1 == IAxis.OVERFLOW_BIN) d = DEFAULT_DOUBLE;
            else d = xAxis.binWidth(param1)/rmsFactor;
        } else
            d = binRmssX[convertAIDAIndex(xAxis, param1)][convertAIDAIndex(yAxis, param2)];
        return d;
    }
    
    public double binRmsY(int param1, int param2) throws java.lang.IllegalArgumentException {
        makeSureDataIsValid();
        double d = DEFAULT_DOUBLE;
        if (binRmssY == null) {
            if (param2 == IAxis.UNDERFLOW_BIN) d = DEFAULT_DOUBLE;
            else if (param2 == IAxis.OVERFLOW_BIN) d = DEFAULT_DOUBLE;
            else d = yAxis.binWidth(param2)/rmsFactor;
        } else
            d = binRmssY[convertAIDAIndex(xAxis, param1)][convertAIDAIndex(yAxis, param2)];
        return d;
    }
    
    public int coordToIndexX(double param) {
        makeSureDataIsValid();
        return xAxis.coordToIndex(param);
    }
    
    public int coordToIndexY(double param) {
        makeSureDataIsValid();
        return xAxis.coordToIndex(param);
    }
    
    public void fill(double param, double param1) throws java.lang.IllegalArgumentException {
        throw new ReadOnlyException();
    }
    
    public void fill(double param, double param1, double param2) throws java.lang.IllegalArgumentException {
        throw new ReadOnlyException();
    }
    
    public double meanX() {
        makeSureDataIsValid();
        return meanX;
    }
    
    public double meanY() {
        makeSureDataIsValid();
        return meanY;
    }
    
    public double rmsX() {
        makeSureDataIsValid();
        return rmsX;
    }
    
    public double rmsY() {
        makeSureDataIsValid();
        return rmsY;
    }
    
    public hep.aida.IAxis xAxis() {
        makeSureDataIsValid();
        return xAxis;
    }
    
    public hep.aida.IAxis yAxis() {
        makeSureDataIsValid();
        return yAxis;
    }
    
    
    // Do some simple tests here
    public static void main(String[] args) throws Exception {

        hep.aida.IAnalysisFactory af = hep.aida.IAnalysisFactory.create();
        hep.aida.ITreeFactory tf = af.createTreeFactory();
        hep.aida.IHistogramFactory hf = af.createHistogramFactory(null);    
        IHistogram2D h21 = hf.createHistogram2D("Histogram 2D-1", 300, -3.0, 3.0, 300, -3.0, 3.0);
        
        RemoteHistogram2D rh2 = new RemoteHistogram2D(null, "Histogram 2D");
        rh2.setConnected(false);
        
        java.util.Random r = new java.util.Random();
        
        for (int i = 0; i < 100000; i++) {
            double xVal = r.nextGaussian();
            double yVal = r.nextGaussian();
            double w = r.nextDouble();
            h21.fill(xVal, yVal, w);
        }

        hep.aida.ref.remote.rmi.data.RmiHist2DData data1 = null;
        hep.aida.ref.remote.rmi.data.RmiHist2DData data2 = null;
        hep.aida.ref.remote.rmi.converters.RmiHist2DConverter converter = hep.aida.ref.remote.rmi.converters.RmiHist2DConverter.getInstance();

        
        for (int k=0; k<10; k++) {
            
            for (int i = 0; i < 10000; i++) {
                double xVal = r.nextGaussian();
                double yVal = r.nextGaussian();
                double w = r.nextDouble();
                h21.fill(xVal, yVal, w);
            }
            
            // extract data from Histogram2D
            long t0 = System.currentTimeMillis();
            data1 = (hep.aida.ref.remote.rmi.data.RmiHist2DData) converter.extractData(h21);
            
            // set data to RemoteHistogram2D 
            long t1 = System.currentTimeMillis();
            converter.updateAidaObject(rh2, data1);
            
            // extract data from RemoteHistogram2D 
            long t2 = System.currentTimeMillis();
            data2 = (hep.aida.ref.remote.rmi.data.RmiHist2DData) converter.extractData(rh2);
            
            long t3 = System.currentTimeMillis();
            
            long tExtract1  = (t1 - t0);
            long tSet1      = (t2 - t1);
            long tRExtract1 = (t3 - t2);
            
            System.out.println(k+" Extract data: "+tExtract1+" msec, Set data: "+tSet1+" msec, Extract from Remote: "+tRExtract1+" msec :: "+data2.getEquivalentBinEntries());
            
            Thread.sleep(2000);
        }
    }
    
}
