/*
 * FitParameterSettings.java
 *
 * Created on August 21, 2002, 12:44 PM
 */

package hep.aida.ref.optimizer;
import hep.aida.ext.IVariableSettings;

/**
 *
 * @author The AIDA team @ SLAC.
 *
 */
public class VariableSettings implements IVariableSettings {

    private String name;
    private double stepSize;
    private double upperBound;
    private double lowerBound;
    private boolean isFixed;
    private double currentValue;
    
    private final double  stepSizeDef = 1.0;
    private final boolean isFixedDef = false;
    private final double upperBoundDef = Double.POSITIVE_INFINITY;
    private final double lowerBoundDef = Double.NEGATIVE_INFINITY;
    private final double defValue = Double.NaN;
    
    public VariableSettings(String name) {
        reset();
        this.name = name;
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
        if ( ! Double.isNaN( value() ) )
            if ( value() < lo || value() > up ) throw new IllegalArgumentException("The value "+value()+" is outside of the range "+lo+" "+up);
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

    public void reset() {
        setStepSize(stepSizeDef);
        setFixed(isFixedDef);
        setBounds(lowerBoundDef,upperBoundDef);
        setValue(defValue);
    }
        
    public double value() {
        return currentValue;
    }
    public void setValue(double value) {
        if ( value < lowerBound() || value > upperBound() ) throw new IllegalArgumentException("The value "+value+" is outside of the bounds "+lowerBound()+" "+upperBound());
        this.currentValue = value;
    }
    
}
