/*
 * FitParameterSettings.java
 *
 * Created on August 21, 2002, 12:44 PM
 */

package hep.aida.ref.fitter;
import hep.aida.IFitParameterSettings;

/**
 *
 * @author The AIDA team @ SLAC.
 *
 */
public class FitParameterSettings implements IFitParameterSettings {

    private String name;
    private double stepSize = Double.NaN;
    private double upperBound;
    private double lowerBound;
    private boolean isFixed;
    
    private final boolean isFixedDef = false;
    private final double upperBoundDef = Double.POSITIVE_INFINITY;
    private final double lowerBoundDef = Double.NEGATIVE_INFINITY;
    
    /**
     * Creates a new instance of FitParameterSettings.
     * @param name The name of the FitParameterSettings. It corresponds
     *             to the name of the parameter to which the FitParameterSettings
     *             refers to.
     *
     */
    public FitParameterSettings( String name ) {
        this.name = name;
        reset();
    }
    
    public String name() {
        return name;
    }

    public double stepSize() {
        return stepSize;
    }

    public double upperBound() {
        return upperBound;
    }

    public double lowerBound() {
        return lowerBound;
    }
    
    public boolean isBound() {
        if ( upperBound == Double.POSITIVE_INFINITY && lowerBound == Double.NEGATIVE_INFINITY ) return false;
        return true;
    }

    public boolean isFixed() {
        return isFixed;
    }

    public void setStepSize(double step) {
        if ( step > 0 ) stepSize = step;
        else throw new IllegalArgumentException("Illegal step size "+step+" it has to be positive!");
    }

    public void setBounds(double lo, double up) {
        if ( up < lo ) throw new IllegalArgumentException("Lower bound cannot be less than upper bound : "+lo+" > "+up);
        upperBound = up;
        lowerBound = lo;
    }

    public void removeBounds() {
        upperBound = upperBoundDef;
        lowerBound = lowerBoundDef;
    }

    public void setFixed(boolean isFixed) {
        this.isFixed = isFixed;
    }

    public void setUpperBound(double upperBound) {
        this.upperBound = upperBound;
    }

    public void setLowerBound(double lowerBound) {
        this.lowerBound = lowerBound;
    }

    public void reset() {
        this.stepSize = Double.NaN;
        setFixed(isFixedDef);
        setBounds(lowerBoundDef,upperBoundDef);
    }
    
}
