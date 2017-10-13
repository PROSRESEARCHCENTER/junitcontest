package hep.aida.ref.histogram;

import hep.aida.IAnnotation;
import hep.aida.IDataPoint;
import hep.aida.IDataPointSet;
import hep.aida.IMeasurement;
import hep.aida.ref.AidaUtils;
import hep.aida.ref.Annotation;
import hep.aida.ref.ManagedObject;
import hep.aida.ref.event.AIDAListener;
import hep.aida.ref.event.DataPointSetEvent;
import hep.aida.ref.event.IsObservable;

import java.util.ArrayList;
import java.util.EventObject;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Basic user-level interface class for holding and managing
 * a single set of "data points".
 *
 * @author The AIDA team @ SLAC.
 *
 */
public class DataPointSet extends ManagedObject implements IDataPointSet, IsObservable, AIDAListener {
    
    private int defaultSize = 0;
    private IAnnotation annotation;
    //private String title;
    private int dimension;
    private List points;
    
    // Constructors
    protected DataPointSet() {
        super("");
        initDataPointSet();
        annotation.addItem(Annotation.titleKey,"Title", true);
    }
    
    public DataPointSet(String name, String title, int dimOfPoints) {
        this(name, title, dimOfPoints, 0);
    }
    
    public DataPointSet(String name, String title, int dimOfPoints, int defaultCapacity) {
        this(name, title, dimOfPoints, defaultCapacity, null);
    }
    
    public DataPointSet(String name, String title, int dimOfPoints, String options) {
        this(name, title, dimOfPoints, 0, options);
    }
    
    public DataPointSet(String name, String title, int dimOfPoints, int defaultCapacity, String options) {
        super(name);
        //this.title = title;
        defaultSize = defaultCapacity;
        dimension = dimOfPoints;
        initDataPointSet();
        annotation.addItem(Annotation.titleKey,title, true);
        setOptions(options);
    }
    
    private void initDataPointSet() {
        annotation = new Annotation();
        clear();
        points = new ArrayList(defaultSize);
        for ( int i = 0; i < defaultSize; i++ ) {
            DataPoint point = new DataPoint(dimension);
            points.add( point );
            if (point instanceof IsObservable) {
                ((IsObservable) point).addListener(this);
            }
        }
    }
    
    private void setOptions(String options) {
        if (options == null || options.trim().equals("")) return;
        String[] arr = AidaUtils.parseString(options);
        if (arr == null || arr.length == 0) return;
        ArrayList list = new ArrayList();
        for (int i=0; i<arr.length; i++) {
            String tmp = arr[i];
            if (tmp.length() > 11 && tmp.substring(0, 11).equalsIgnoreCase("annotation.")) {
                String key = tmp.substring(11);
                int index = key.indexOf("=");
                if (index > 0) key = key.substring(0, index);
                list.add(key);
            }
        }
        Map optionMap = AidaUtils.parseOptions( options );
        Iterator it = optionMap.keySet().iterator();
        while (it.hasNext()) {
            String key = (String) it.next();
            for (int i=0; i<list.size(); i++) {
                if (key.equals((String) list.get(i))) {
                    String val = (String) optionMap.get(key);
                    if (val != null) annotation.addItem(key, val, true);
                }
            }
        }
    }
    
    // End of Constructors
    
    protected java.util.EventObject createEvent() {
        return new DataPointSetEvent(this);
    }
    
    
    public IAnnotation annotation() { return annotation; }
    
    public void setAnnotation( IAnnotation annotation ) {this.annotation = annotation; }
    public String title() {
        //return title;
        return annotation.value(Annotation.titleKey);
    }
    
    public void setTitle(String title) throws IllegalArgumentException {
        //this.title = title;
        annotation.setValue(Annotation.titleKey,title);
        if (isValid) fireStateChanged();
    }
    
    public int dimension() { return dimension; }
    
    public void clear() {
        //annotation = new Annotation();
        if (points != null && points.size() > 0) {
            for (int i=0; i<points.size(); i++) {
                removePoint(i);
            }
        }
        points = new ArrayList(defaultSize);
    }
    
    public int size() { return points.size(); }
    
    public IDataPoint point(int index) { return (IDataPoint) points.get(index); }
    
    /**
     * Set the IDataPoint at a give index in the set.
     * This method is not in the IDataSet interface and is here for efficiency reasons
     * @param index The IDataPoint index.
     * @param point The corresponding IDataPoint to be set at the index
     * @throws      IllegalArgumentException If the index is < 0 or >= size().
     *
     */
    public void setPoint(int index, IDataPoint point) throws IllegalArgumentException {
        if (index >= points.size() || index < 0) throw new IllegalArgumentException("Wrong argument in setPoint():  index="+index+", size="+points.size());
        IDataPoint oldPoint = (IDataPoint) points.get(index);
        if (oldPoint instanceof IsObservable) {
            ((IsObservable) oldPoint).removeListener(this);
        }
        points.set(index, point);
        if (point instanceof IsObservable) {
            ((IsObservable) point).addListener(this);
        }
        if (isValid) fireStateChanged();
    }
    
    public IDataPoint addPoint() throws java.lang.RuntimeException {
        IDataPoint point = new DataPoint(dimension);
        addPoint( point );
        return point;
    }
    
    public void addPoint(IDataPoint point) throws IllegalArgumentException {
        if (point.dimension() == dimension) points.add(point);
        else throw new IllegalArgumentException("Wrong dimension in addPoint(): DataPointSet.dimension()="+dimension+
        ", DataPoint.dimension()="+point.dimension());
        if (point instanceof IsObservable) {
            ((IsObservable) point).addListener(this);
        }
        if (isValid) fireStateChanged();
    }
    
    public void removePoint(int index) throws IllegalArgumentException {
        if (index >= points.size() || index < 0) throw new IllegalArgumentException("Wrong argument in removePoint()  index="+index+", size="+points.size());
        IDataPoint point = (IDataPoint) points.remove(index);
        if (point instanceof IsObservable) ((IsObservable) point).removeListener(this);
        if (point instanceof DataPoint) ((DataPoint) point).clear();
        
        if (isValid) fireStateChanged();
    }
    
    public double lowerExtent(int coord) throws IllegalArgumentException {
        if (coord<0 || coord>=dimension || points.size()==0)
            throw new IllegalArgumentException("Can not calculate lowerExtent: coord="+coord+", size="+points.size());
        double lowerExtent = Double.NaN;
        for (int i=0; i<points.size(); i++) {
            double lower = ((DataPoint) points.get(i)).lowerExtent(coord);
            if (Double.isNaN(lowerExtent) || lowerExtent > lower) lowerExtent = lower;
        }
        return lowerExtent;
    }
    
    public double upperExtent(int coord) throws IllegalArgumentException {
        if (coord<0 || coord>=dimension || points.size()==0)
            throw new IllegalArgumentException("Can not calculate upperExtent: coord="+coord+", size="+points.size());
        double upperExtent = Double.NaN;
        for (int i=0; i<points.size(); i++) {
            double upper = ((DataPoint) points.get(i)).upperExtent(coord);
            if (Double.isNaN(upperExtent) || upper > upperExtent ) upperExtent = upper;
        }
        return upperExtent;
    }
    
    public void scale(double scaleFactor) throws IllegalArgumentException {
        if (scaleFactor<=0)
            throw new IllegalArgumentException("Illegal scale factor: scaleFactor="+scaleFactor);
        for (int i=0; i<points.size(); i++) {
            IDataPoint p = (IDataPoint) points.get(i);
            for (int j=0; j<dimension; j++) {
                IMeasurement meas = p.coordinate(j);
                meas.setValue(meas.value() * scaleFactor);
                meas.setErrorMinus(meas.errorMinus() * scaleFactor);
                meas.setErrorPlus(meas.errorPlus() * scaleFactor);
            }
        }
        if (isValid) fireStateChanged();
    }
    
    public void scaleValues(double scaleFactor) throws IllegalArgumentException {
        if (scaleFactor<=0)
            throw new IllegalArgumentException("Illegal scale factor: scaleFactor="+scaleFactor);
        for (int i=0; i<points.size(); i++) {
            IDataPoint p = (IDataPoint) points.get(i);
            for (int j=0; j<dimension; j++) {
                IMeasurement meas = p.coordinate(j);
                meas.setValue(meas.value() * scaleFactor);
            }
        }
        if (isValid) fireStateChanged();
    }
    
    public void scaleErrors(double scaleFactor) throws IllegalArgumentException {
        if (scaleFactor<=0)
            throw new IllegalArgumentException("Illegal scale factor: scaleFactor="+scaleFactor);
        for (int i=0; i<points.size(); i++) {
            IDataPoint p = (IDataPoint) points.get(i);
            for (int j=0; j<dimension; j++) {
                IMeasurement meas = p.coordinate(j);
                meas.setErrorMinus(meas.errorMinus() * scaleFactor);
                meas.setErrorPlus(meas.errorPlus() * scaleFactor);
            }
        }
        if (isValid) fireStateChanged();
    }
    
    public void setCoordinate(int coord, double[] values, double[] errors) throws IllegalArgumentException {
        setCoordinate(coord, values, errors, errors);
    }
    
    public void setCoordinate(int coord, double[] values, double[] errp, double[] errm) throws IllegalArgumentException {
        if ( coord < 0 || coord >= dimension() ) throw new IllegalArgumentException("Illegal coordinate "+coord+"!! It has to be 0 <= coord < "+dimension());
        int nPoints = values.length;
        if ( nPoints != errp.length ) throw new IllegalArgumentException("Incompatible array sizes! Value's size "+nPoints+" while error's size is "+errp.length);
        if ( nPoints != errm.length ) throw new IllegalArgumentException("Incompatible array sizes! Value's size "+nPoints+" while error's size is "+errm.length);
        if ( size() == 0 )
            addNPoints(nPoints);
        if ( nPoints != size() ) throw new IllegalArgumentException("Array size "+nPoints+" is incompatible with the number of points "+size());
        
        for ( int i = 0; i < nPoints; i++ ) {
            IMeasurement meas = point(i).coordinate(coord);
            meas.setValue( values[i] );
            meas.setErrorMinus( errm[i] );
            meas.setErrorPlus( errp[i] );
        }
    }
    
    /**
     * Non-AIDA methods.
     *
     */
    protected void setDimension( int dimOfPoints ) {
        dimension = dimOfPoints;
    }
    
    private void addNPoints( int n ) {
        for( int i = 0; i < n; i++ )
            addPoint();
    }
    
    protected void finalize() throws Throwable {
        defaultSize = 0;
        clear();
    }
    
    // AIDAListener interface
    public void stateChanged(EventObject e) {
        //System.out.println("DataPointSet.stateChanged  isValid="+isValid);
        if (isValid) fireStateChanged();
    }
    
} // class or interface
