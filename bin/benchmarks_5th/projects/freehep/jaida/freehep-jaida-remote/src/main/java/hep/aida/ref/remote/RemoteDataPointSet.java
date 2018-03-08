/*
 * RemoteDataPointSet.java
 *
 * Created on June 16, 2003, 6:57 PM
 */

package hep.aida.ref.remote;

import hep.aida.IAnnotation;
import hep.aida.IDataPoint;
import hep.aida.IDataPointSet;
import hep.aida.IMeasurement;
import hep.aida.dev.IDevMutableStore;
import hep.aida.ref.Annotation;
import hep.aida.ref.ReadOnlyException;
import hep.aida.ref.event.DataPointSetEvent;

/**
 * This is implementation of IDataPointSet that can not be modified
 * by user. This is a simple implementation that does not guarantee
 * internal consistency. So extra care should be taken when setting
 * data for this class.
 * For more information look into RemoteHistogram1D comments.
 *
 * @author  serbo
 */
public class RemoteDataPointSet extends RemoteManagedObject implements IDataPointSet {
    protected Annotation annotation = null;
    protected int dimension;
    protected double[] upperExtent;
    protected double[] lowerExtent;
    protected double[] values;
    protected double[] plusErrors;
    protected double[] minusErrors;
    protected double[] weights;

    /** Creates a new instance of RemoteDataPointSet */
    public RemoteDataPointSet(String name) {
        this(null, name);
    }
    
    public RemoteDataPointSet(IDevMutableStore store, String name) {
        this(store, name, name, 2);
    }
    
    public RemoteDataPointSet(IDevMutableStore store, String name, String title, int dimOfPoint) {
        super(name);
        aidaType = "IDataPointSet";
        this.store = store;
        annotation = new Annotation(); 
        annotation.setFillable(true);
        annotation.addItem(Annotation.titleKey,title,true);
        annotation.setFillable(false);
        init(dimOfPoint);
    }
    
    
    // AIDAObservable methods
    protected java.util.EventObject createEvent()
    {
       return new DataPointSetEvent(this);
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
    
    protected void init(int dimOfPoint) {
	dimension = dimOfPoint;
        upperExtent = new double[dimension];
        lowerExtent = new double[dimension];
        dataIsValid = false;
    }
    
    public void setValues(double[] values) {
        this.values = values;
    }
    
    public void setPlusErrors(double[] plusErrors) {
        this.plusErrors = plusErrors;
    }
    
    public void setWeights(double[] w) {
        this.weights = w;
    }
    
    public void setMinusErrors(double[] minusErrors) {
        this.minusErrors = minusErrors;
    }
    
    public void setUpperExtent(double[] upperExtent) {
        this.upperExtent = upperExtent;
    }
    
    public void setLowerExtent(double[] lowerExtent) {
        this.lowerExtent = lowerExtent;
    }
    
    public void setDimension(int dimension) {
        this.dimension = dimension;
    }
    
    public void print() {
        System.out.println("\nRemoteDataPointSet  name="+name()+", title="+title()+
                           ", dimension="+dimension()+", size="+size());
        
        for (int dim=0; dim<dimension(); dim++) {
            System.out.println("\tDimension="+dim+"  lowerExtent="+lowerExtent(dim)+"  upperExtent="+upperExtent(dim));
        }
        for (int ip=0; ip<size(); ip++) {
            IDataPoint p = this.point(ip);
            System.out.print("\n\t"+ip);
            for (int dim=0; dim<dimension(); dim++) {
                IMeasurement m = p.coordinate(dim);
                System.out.print("  dim="+dim+"  v="+m.value()+" er="+m.errorPlus()+" em="+m.errorMinus()+"; ");
            }
        }
        System.out.print("\n\n");
    }
    
    
    // Local methods to use with RemoteAIDADataPointSetAdapter
    
    double errorMinus(int i) {
        makeSureDataIsValid();
        int index = dimension*(i+1)-1;
        if (minusErrors != null) return  minusErrors[index];        
        else if (plusErrors != null) return  plusErrors[index];        
        return DEFAULT_DOUBLE;
    }
    
    double errorPlus(int i) {
        makeSureDataIsValid();
        int index = dimension*(i+1)-1;
        if (plusErrors != null) return plusErrors[index]; 
        return DEFAULT_DOUBLE; 
    }
    
    double getX(int i) {
        makeSureDataIsValid();
        int index = dimension*i;
        if (values != null) return values[index]; 
        return DEFAULT_DOUBLE;
    }
    
    double getY(int i) {
        makeSureDataIsValid();
        int index = dimension*(i+1)-1;
        if (values != null) return values[index];
        return DEFAULT_DOUBLE;    
    }
    
    // IDataPointSet methods
    
    public IAnnotation annotation() {
        makeSureDataIsValid();
        return annotation;
    }
    
    public int dimension() {
        makeSureDataIsValid();
        return dimension;
    }
    
    public int size() {
        makeSureDataIsValid();
        if (values == null || dimension() == 0) return 0;
        return (int) values.length/dimension();
    }
    
    public void setTitle(String title) throws java.lang.IllegalArgumentException {
        //makeSureDataIsValid();
        if (!fillable) throw new ReadOnlyException();
        annotation.setFillable(true);
        annotation.setValue(Annotation.titleKey,title);        
        annotation.setFillable(false);
    }

    public String title() {
        //makeSureDataIsValid();
        return annotation.value(Annotation.titleKey);
    }
    
    public double upperExtent(int coordinate) throws java.lang.IllegalArgumentException {
        makeSureDataIsValid();
        
        if (upperExtent == null || dimension() == 0 || size() == 0) return lowerExtent(coordinate)+1.;
        return  upperExtent[coordinate];
    }

    public double lowerExtent(int coordinate) throws java.lang.IllegalArgumentException {
        makeSureDataIsValid();
        if (lowerExtent == null || dimension() == 0 || size() == 0) return DEFAULT_DOUBLE;
        return  lowerExtent[coordinate];
    }
    
    public hep.aida.IDataPoint point(int index) {
        makeSureDataIsValid();
        return new RemoteDataPoint(index);
    }
    
    public void removePoint(int index) throws java.lang.IllegalArgumentException {
        throw new ReadOnlyException();
    }
    
    public void clear() {
        throw new ReadOnlyException();
    }
    
    public IDataPoint addPoint() throws java.lang.RuntimeException {
        throw new ReadOnlyException();
    }
    
    public void addPoint(hep.aida.IDataPoint iDataPoint) throws java.lang.IllegalArgumentException {
        throw new ReadOnlyException();
    }
    
    public void scale(double param) throws java.lang.IllegalArgumentException {
        throw new ReadOnlyException();
    }
    
    public void scaleErrors(double param) throws java.lang.IllegalArgumentException {
        throw new ReadOnlyException();
    }
    
    public void scaleValues(double param) throws java.lang.IllegalArgumentException {
        throw new ReadOnlyException();
    }
        
    public void setCoordinate(int param, double[] values, double[] values2) throws java.lang.IllegalArgumentException {
        throw new ReadOnlyException();
    }    
    
    public void setCoordinate(int param, double[] values, double[] values2, double[] values3) throws java.lang.IllegalArgumentException {
        throw new ReadOnlyException();
    }
    
    public class RemoteDataPoint implements IDataPoint {
        
        private int startIndex;  // index into main arrays
        private int stopIndex;
        
        public RemoteDataPoint(int index) {
            this.startIndex = dimension*index;
            this.stopIndex = startIndex + dimension - 1;
        }
        
        public hep.aida.IMeasurement coordinate(int param) {
            if (param < 0 || param >= dimension)
                throw new IllegalArgumentException("Not valid value of coordinate index: "+param);
            return new RemoteMeasurement(startIndex+param);
        }
        
        public int dimension() {
            return dimension;
        }
        
    }
    
    public class RemoteMeasurement implements IMeasurement {
        
        private int index; // index into main arrays
        
        // Here index is not coordinate, but coordinate + offset in the main array
        public RemoteMeasurement(int index) {
            this.index = index;
        }
        public double value() {
	    if (values == null) return DEFAULT_DOUBLE;
            return values[index];
        }
        
        public double errorMinus() {
	    if (minusErrors == null) return errorPlus();
            return minusErrors[index];
        }
        
        public double errorPlus() {
	    if (plusErrors == null) return 0.0;
            return plusErrors[index];
        }
        
        public void setErrorMinus(double param) throws java.lang.IllegalArgumentException {
            throw new ReadOnlyException();
        }
        
        public void setErrorPlus(double param) throws java.lang.IllegalArgumentException {
            throw new ReadOnlyException();
        }
        
        public void setValue(double param) throws java.lang.IllegalArgumentException {
            throw new ReadOnlyException();
        }  
        
        // Extra method
        public double weight() {
	    if (weights == null) return DEFAULT_DOUBLE;
            return weights[index];
        }
        
        
    }
}
