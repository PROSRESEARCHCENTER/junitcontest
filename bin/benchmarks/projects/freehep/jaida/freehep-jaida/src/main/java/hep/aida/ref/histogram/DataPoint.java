package hep.aida.ref.histogram;

import hep.aida.IDataPoint;
import hep.aida.IMeasurement;
import hep.aida.ref.event.AIDAListener;
import hep.aida.ref.event.AIDAObservable;
import hep.aida.ref.event.IsObservable;

/** 
 * Basic user-level interface class for holding and managing
 * a single set of "measurements".
 * 
 * @author The AIDA team
 *
 */
public class DataPoint extends AIDAObservable implements IDataPoint, IsObservable, AIDAListener {

    private int dimension;
    private IMeasurement[] measurements;

    // Constructors 
    public DataPoint(int dim) {
        super();
        setIsValidAfterNotify(true);
	dimension = dim;
	measurements = new Measurement[dim];
	for (int i=0; i<dimension; i++) {
            Measurement meas = new Measurement();
            meas.addListener(this);
            measurements[i] = meas;
        }
    }

    public DataPoint(IDataPoint p) {
        super();
        setIsValidAfterNotify(true);
	dimension = p.dimension();
	measurements = new Measurement[dimension];
	for (int i=0; i<dimension; i++) {
            Measurement meas = new Measurement(p.coordinate(i));
            meas.addListener(this);
            measurements[i] = meas;
        }
    }

    public DataPoint(IMeasurement[] meas) {
        super();
        setIsValidAfterNotify(true);
	dimension = meas.length;
	measurements = meas;
	for (int i=0; i<meas.length; i++) {
            if (measurements[i] instanceof IsObservable) ((IsObservable) measurements[i]).addListener(this);
        }
    }

    public DataPoint(double[] val) {
        super();
        setIsValidAfterNotify(true);
	dimension = val.length;
	measurements = new Measurement[dimension];
	for (int i=0; i<dimension; i++) {
            Measurement meas = new Measurement(val[i]);
            meas.addListener(this);
            measurements[i] = meas;
        }
    }

    public DataPoint(double[] val, double[] err) {
        super();
        setIsValidAfterNotify(true);
	if (val.length != err.length)
	    throw new IllegalArgumentException("DataPoint Constructor: Value and Error arrays are not the same size");
	dimension = val.length;
	measurements = new Measurement[dimension];
	for (int i=0; i<dimension; i++) {
            Measurement meas = new Measurement(val[i], err[i]);
            meas.addListener(this);
            measurements[i] = meas;
        }
    }

    public DataPoint(double[] val, double[] errMinus, double[] errPlus) {
        super();
        setIsValidAfterNotify(true);
	if (val.length != errPlus.length)
	    throw new IllegalArgumentException("DataPoint Constructor: Value and Error arrays are not the same size");
	if (val.length != errMinus.length)
	    throw new IllegalArgumentException("DataPoint Constructor: Value and Error arrays are not the same size");
	dimension = val.length;
	measurements = new Measurement[dimension];
	for (int i=0; i<dimension; i++) {
            Measurement meas = new Measurement(val[i], errMinus[i], errPlus[i]);
            meas.addListener(this);
            measurements[i] = meas;
        }
    }
    // End of Constructors

    public int dimension() { return dimension; }

    public IMeasurement coordinate(int coord) { return measurements[coord]; }


    /**
     * Get the lower value for a give axis.
     * This method is not in the IDataPoint interface and is here for efficiency reasons
     * @param coord The coordinate of the axis.
     * @return      The lower edge of the corresponding axis.
     * @throws      IllegalArgumentException if coord < 0 or coord >= dimension() or if the set is empty.
     *
     */
    public double lowerExtent(int coord) throws IllegalArgumentException { 
        double le = measurements[coord].value();
        if ( ! Double.isNaN( measurements[coord].errorMinus() ) )
            le -= measurements[coord].errorMinus();
	return  le;
    }

    /**
     * Get the upper value for a give axis.
     * This method is not in the IDataPoint interface and is here for efficiency reasons
     * @param coord The coordinate of the axis.
     * @return      The upper edge of the corresponding axis.
     * @throws      IllegalArgumentException if coord < 0 or coord >= dimension() or if the set is empty.
     *
     */
    public double upperExtent(int coord) throws IllegalArgumentException { 
        double ue = measurements[coord].value();
        if ( ! Double.isNaN( measurements[coord].errorPlus() ) )
            ue += measurements[coord].errorPlus();
	return ue;
    }

    
    // AIDAListener methods
    public void stateChanged(java.util.EventObject e) {
        //System.out.println("DataPoint.stateChanged, isValid="+isValid);
        fireStateChanged();
    }    
    
    
    // Service methods
    
    public void clear() {
        if (measurements != null && measurements.length > 0) {
            for (int i=0; i<measurements.length; i++) {
                if (measurements[i] instanceof IsObservable) ((IsObservable) measurements[i]).removeListener(this);                
            }
        }
        
    }
    
    protected void finalize() throws Throwable {
        clear();
    }
    
} // class or interface
