package hep.aida.ref.plotter;

/**
 *
 * @author The FreeHEP team @ SLAC
 */
public class DoubleStyleParameter extends AbstractStyleParameter {
    
    private boolean hasLimits = false;
    private double min;
    private double max;
    
    DoubleStyleParameter(String name, double defaultValue) {
        super(name, String.valueOf(defaultValue));
    }
    
    DoubleStyleParameter(String name, double defaultValue, double min, double max) {
        super(name, String.valueOf(defaultValue));
        if ( min >= max ) throw new IllegalArgumentException("Min value: "+min+" cannot be greater or equal to Max value: "+max);
        this.hasLimits = true;
        this.min = min;
        this.max = max;
    }
    
    public boolean hasLimits() {
        return hasLimits;
    }
    
    public double min() {
        return min;
    }

    public double max() {
        return max;
    }

    public double value() {
        try {
            return Double.parseDouble(parameterValue());
        } catch (NumberFormatException nfe) {
            return Double.NaN;
        } catch (NullPointerException npe) {
            return Double.NaN;
        }
    }
    
    public boolean setValue(double value) {
        return setParameter(String.valueOf(value));
    }
    
    protected boolean setParameter(String parValue) {
        boolean result = super.setParameter(parValue);
        try {
            double d = Double.parseDouble(parValue);
            if ( ! result || ! hasLimits() )
                return result;
            return d < max && d > min;
        } catch (NumberFormatException nfe) {
            return false;
        } catch (NullPointerException npe) {
            return false;
        }
    }
    
    public Class type() {
        return Double.TYPE;
    }    
}
