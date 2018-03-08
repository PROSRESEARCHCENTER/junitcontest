/*
 * RemoteCloud1D.java
 *
 * Created on February 17, 2005, 3:14 PM
 */

package hep.aida.ref.remote;

import hep.aida.ICloud1D;
import hep.aida.dev.IDevMutableStore;
import hep.aida.ref.Annotation;
import hep.aida.ref.ReadOnlyException;
import hep.aida.ref.event.HistogramEvent;

/**
 *
 * @author  serbo
 */
public class RemoteCloud1D extends RemoteManagedObject implements ICloud1D {
    
    private RemoteHistogram1D hist;
    private boolean isConverted;
    
    private int entries;
    private int nanEntries = DEFAULT_INT;
    private int maxEntries;
    private double lowerEdge;
    private double upperEdge;
    protected double sumOfWeights;
    
    private double mean;
    private double rms;
    private double[] values;
    private double[] weights;
    
    /** Creates a new instance of RemoteCloud1D */
    public RemoteCloud1D(String name) {
        this(null, name);
    }
    
    public RemoteCloud1D(IDevMutableStore store, String name) {
        this(store, name, name);
    }
    
    public RemoteCloud1D(IDevMutableStore store, String name, String title) {
        super(name);
        aidaType = "ICloud1D";
        this.store = store;
        this.hist = new RemoteHistogram1D(name);
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
    public void setLowerEdge(double d) { lowerEdge = d; }
    public void setUpperEdge(double d) { upperEdge = d; }
    public void setSummOfWeights(double d) { sumOfWeights = d; }
    
    public void setMean(double d) { mean = d; }
    public void setRms(double d) { rms = d; }
    
    public void setValues(double[] a)  { values = a;  }
    public void setWeights(double[] a) { weights = a; } 
    
    
    
    // ICloud1D Methods
    
    
    public hep.aida.IAnnotation annotation() {
        makeSureDataIsValid();
        return hist.annotation();
    }
    
    public int dimension() {
        makeSureDataIsValid();
        return 1;
    }
    
    public int entries() {
        makeSureDataIsValid();
        if (isConverted()) return hist.allEntries();
        return entries;
    }
    
    public hep.aida.IHistogram1D histogram() throws java.lang.RuntimeException {
        makeSureDataIsValid();
        return hist;
    }
    
    public boolean isConverted() {
        makeSureDataIsValid();
        return isConverted;
    }
    
    public double lowerEdge() {
        makeSureDataIsValid();
        return lowerEdge;
    }
    
    public int maxEntries() {
        makeSureDataIsValid();
        return maxEntries;
    }
    
    public double mean() {
        makeSureDataIsValid();
        if (isConverted()) return hist.mean();
        return mean;
    }
    
    public double rms() {
        makeSureDataIsValid();
        if (isConverted()) return hist.rms();
        return rms;
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
    
    public double upperEdge() {
        makeSureDataIsValid();
        return upperEdge;
    }
    
    public int nanEntries() {
        makeSureDataIsValid();
        if (isConverted()) return hist.nanEntries();
        return nanEntries;
    }

    public double value(int param) throws hep.aida.AlreadyConvertedException {
        makeSureDataIsValid();
        if (isConverted()) throw new RuntimeException("Cloud has been converted");
        return values[param];        
    }
    
    public double weight(int param) throws hep.aida.AlreadyConvertedException {
        makeSureDataIsValid();
        if (isConverted()) throw new RuntimeException("Cloud has been converted");
        return weights[param];
    }
    
    public void setConversionParameters(int bins, double le, double ue ) {
        throw new ReadOnlyException();        
    }
    
    public void convert(double[] values) throws hep.aida.AlreadyConvertedException {
        throw new ReadOnlyException();
    }
    
    public void convert(int param, double param1, double param2) throws hep.aida.AlreadyConvertedException {
        throw new ReadOnlyException();
    }
    
    public void convertToHistogram() throws hep.aida.AlreadyConvertedException {
        throw new ReadOnlyException();
    }
    
    public void fill(double param) throws java.lang.IllegalArgumentException {
         throw new ReadOnlyException();
   }
    
    public void fill(double param, double param1) throws java.lang.IllegalArgumentException {
        throw new ReadOnlyException();
    }
    
    public void fillHistogram(hep.aida.IHistogram1D iHistogram1D) throws java.lang.RuntimeException {
         throw new ReadOnlyException();
   }
    
    public void reset() throws java.lang.RuntimeException {
        throw new ReadOnlyException();
    }
    
    public void scale(double param) throws java.lang.IllegalArgumentException {
        throw new ReadOnlyException();
    }
    
}
