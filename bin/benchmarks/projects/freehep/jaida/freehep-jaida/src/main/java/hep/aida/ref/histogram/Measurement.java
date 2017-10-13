package hep.aida.ref.histogram;

import hep.aida.IMeasurement;
import hep.aida.ref.event.AIDAObservable;
import hep.aida.ref.event.IsObservable;

/**
 * Basic user-level interface class for holding a single "measurement"
 * with positive and negative errors (to allow for asymmetric errors).
 * "IMeasurement" = "value" + "errorPlus" - "errorMinus"
 *
 * @author The AIDA team
 *
 */
public class  Measurement extends AIDAObservable implements IMeasurement, IsObservable {

    private double value;
    private double errorPlus;
    private double errorMinus;

    // Constructors 
    public Measurement() {
        super();
        setIsValidAfterNotify(true);
	value = Double.NaN;
	errorPlus = Double.NaN;
	errorMinus = Double.NaN;
    }

    public Measurement(IMeasurement m) {
        super();
        setIsValidAfterNotify(true);
	value = m.value();
	errorPlus = m.errorPlus();
	errorMinus = m.errorMinus();
    }

    public Measurement(double val) {
        super();
        setIsValidAfterNotify(true);
	value = val;
	errorPlus = Double.NaN;
	errorMinus = Double.NaN;
    }

    public Measurement(double val, double err) {
        super();
        setIsValidAfterNotify(true);
	value = val;
	errorPlus = err;
	errorMinus = err;
    }

    public Measurement(double val, double errMinus, double errPlus) {
        super();
        setIsValidAfterNotify(true);
	value = val;
	errorPlus = errMinus;
	errorMinus = errPlus;
    }
    // End of Constructors

    public double value() { return value; }

    public double errorPlus() { 
        if ( Double.isNaN(errorPlus) ) return 0.;
        return errorPlus; 
    }

    public double errorMinus() { 
        if ( Double.isNaN(errorMinus) ) return errorPlus();
        return errorMinus;
    }

    public void setValue(double value) throws IllegalArgumentException {
	this.value = value;
        if (isValid) fireStateChanged();
    }

    public void setErrorPlus(double errorPlus) throws IllegalArgumentException {
	if (errorPlus < 0.) 
	    throw new IllegalArgumentException("Attempt to set negative value for \"errorPlus\" in Measurement");
	this.errorPlus = errorPlus;
        if (isValid) fireStateChanged();
    }

    public void setErrorMinus(double errorMinus) throws IllegalArgumentException {
	if (errorMinus < 0.) 
	    throw new IllegalArgumentException("Attempt to set negative value for \"errorMinus\" in Measurement");
	this.errorMinus = errorMinus;
        if (isValid) fireStateChanged();
    }


} // class or interface
