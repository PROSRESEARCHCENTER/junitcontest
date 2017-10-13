/*
 * RemoteCloud2D.java
 *
 * Created on February 18, 2005, 3:24 PM
 */

package hep.aida.ref.remote;

import hep.aida.ICloud2D;
import hep.aida.dev.IDevMutableStore;
import hep.aida.ref.Annotation;
import hep.aida.ref.ReadOnlyException;
import hep.aida.ref.event.HistogramEvent;

/**
 *
 * @author  serbo
 */
public class RemoteCloud2D extends RemoteManagedObject implements ICloud2D {
    
    private RemoteHistogram2D hist;
    private boolean isConverted;
    
    private int entries;
    private int nanEntries;
    private int maxEntries;
    protected double sumOfWeights;
    private double lowerEdgeX;
    private double lowerEdgeY;
    private double upperEdgeX;
    private double upperEdgeY;
    
    private double meanX;
    private double meanY;
    private double rmsX;
    private double rmsY;
    private double[] valuesX;
    private double[] valuesY;
    private double[] weights;
    

    /** Creates a new instance of RemoteCloud2D */
    public RemoteCloud2D(String name) {
        this(null, name);
    }
    
    public RemoteCloud2D(IDevMutableStore store, String name) {
        this(store, name, name);
    }
    
    public RemoteCloud2D(IDevMutableStore store, String name, String title) {
        super(name);
        aidaType = "ICloud2D";
        this.store = store;
        this.hist = new RemoteHistogram2D(name);
        this.hist.setFillable(true);
        this.hist.setDataValid(true);
        if (!name.equals(title)) hist.setTitle(title);
        dataIsValid = false;
    }
    
    
    // AIDAObservable methods
    protected java.util.EventObject createEvent()
    {
       return new HistogramEvent(this);
    }

    
    
    
    // Service Methods
    
    public void setTreeFolder(String treeFolder) {
        super.setTreeFolder(treeFolder);
        
        boolean flbl = !(hist.annotation() instanceof Annotation) || ((hist.annotation() instanceof Annotation) && ((Annotation) hist.annotation()).isFillable());
        if (!flbl) ((Annotation) hist.annotation()).setFillable(true);
        if (hist.annotation().hasKey(Annotation.fullPathKey)) {
            hist.annotation().setValue(Annotation.fullPathKey, treePath);
        } else {
            hist.annotation().addItem(Annotation.fullPathKey, treePath, true);
        }
        if (!flbl) ((Annotation) hist.annotation()).setFillable(false);
    }
    
    public void setConverted(boolean b) { isConverted = b; }
    public void setMaxEntries(int i) { maxEntries = i; }
    public void setNanEntries(int i) { nanEntries = i;  }
    
    public void setEntries(int i) { entries = i; }
    public void setLowerEdgeX(double d) { lowerEdgeX = d; }
    public void setLowerEdgeY(double d) { lowerEdgeY = d; }
    public void setUpperEdgeX(double d) { upperEdgeX = d; }
    public void setUpperEdgeY(double d) { upperEdgeY = d; }
    public void setSummOfWeights(double d) { sumOfWeights = d; }
    
    public void setMeanX(double d) { meanX = d; }
    public void setMeanY(double d) { meanY = d; }
    public void setRmsX(double d) { rmsX = d; }
    public void setRmsY(double d) { rmsY = d; }
    
    public void setValuesX(double[] a)  { valuesX = a;  }
    public void setValuesY(double[] a)  { valuesY = a;  }
    public void setWeights(double[] a) { weights = a; } 
    
    
    
    // ICloud1D Methods
        
    public hep.aida.IAnnotation annotation() {
        makeSureDataIsValid();
        return hist.annotation();
    }
    
    public int dimension() {
        makeSureDataIsValid();
        return 2;
    }
    
    public int entries() {
        makeSureDataIsValid();
        if (isConverted()) return hist.allEntries();
        return entries;
    }
    
    public int nanEntries() {
        makeSureDataIsValid();
        if (isConverted()) return hist.nanEntries();
        return nanEntries;
    }

    public hep.aida.IHistogram2D histogram() throws java.lang.RuntimeException {
        makeSureDataIsValid();
        return hist;
    }
    
    public boolean isConverted() {
        makeSureDataIsValid();
        return isConverted;
    }
    
    public double lowerEdgeX() {
        makeSureDataIsValid();
        return lowerEdgeX;
    }
    
    public double lowerEdgeY() {
        makeSureDataIsValid();
        return lowerEdgeY;
    }
    
    public int maxEntries() {
        makeSureDataIsValid();
        return maxEntries;
    }
    
    public double meanX() {
        makeSureDataIsValid();
        if (isConverted()) return hist.meanX();
        return meanX;
    }
    
    public double meanY() {
        makeSureDataIsValid();
        if (isConverted()) return hist.meanY();
        return meanY;
    }
    
    public double rmsX() {
        makeSureDataIsValid();
        if (isConverted()) return hist.rmsX();
        return rmsX;
    }
    
    public double rmsY() {
        makeSureDataIsValid();
        if (isConverted()) return hist.rmsY();
        return rmsY;
    }
    
    public void setTitle(String str) throws java.lang.IllegalArgumentException {
        hist.setTitle(str);
    }
    
    public double sumOfWeights() {
        makeSureDataIsValid();
        if (isConverted()) return hist.sumAllBinHeights();
        return sumOfWeights;
    }
    
    public String title() {
        //makeSureDataIsValid();
        return hist.title();
    }
    
    public double upperEdgeX() {
        makeSureDataIsValid();
        return upperEdgeX;
    }
    
    public double upperEdgeY() {
        makeSureDataIsValid();
        return upperEdgeY;
    }
    
    public double valueX(int param) throws hep.aida.AlreadyConvertedException {
        makeSureDataIsValid();
        if (isConverted()) throw new RuntimeException("Cloud has been converted");
        return valuesX[param];        
    }
    
    public double valueY(int param) throws hep.aida.AlreadyConvertedException {
        makeSureDataIsValid();
        if (isConverted()) throw new RuntimeException("Cloud has been converted");
        return valuesY[param];        
    }
    
    public double weight(int param) throws hep.aida.AlreadyConvertedException {
        makeSureDataIsValid();
        if (isConverted()) throw new RuntimeException("Cloud has been converted");
        return weights[param];        
    }
    
    public void setConversionParameters(int binsx, double lex, double uex,int binsy, double ley, double uey ) {
        throw new ReadOnlyException();        
    }
    
    public void convert(double[] values, double[] values1) throws hep.aida.AlreadyConvertedException {
         throw new ReadOnlyException();
    }
    
    public void convert(int param, double param1, double param2, int param3, double param4, double param5) throws hep.aida.AlreadyConvertedException {
         throw new ReadOnlyException();
    }
    
    public void convertToHistogram() throws hep.aida.AlreadyConvertedException {
         throw new ReadOnlyException();
    }
    
    public void fill(double param, double param1) throws java.lang.IllegalArgumentException {
         throw new ReadOnlyException();
    }
    
    public void fill(double param, double param1, double param2) throws java.lang.IllegalArgumentException {
         throw new ReadOnlyException();
    }
    
    public void fillHistogram(hep.aida.IHistogram2D iHistogram2D) throws java.lang.RuntimeException {
         throw new ReadOnlyException();
    }
       
    public void reset() throws java.lang.RuntimeException {
         throw new ReadOnlyException();
    }
    
    public void scale(double param) throws java.lang.IllegalArgumentException {
         throw new ReadOnlyException();
    }
    
}
