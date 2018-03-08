package hep.aida.ref.plotter;

/**
 *
 * @author The FreeHEP team @ SLAC
 */
public class IntegerStyleParameter extends AbstractStyleParameter {
    
    IntegerStyleParameter(String name, int defaultValue) {
        super(name, String.valueOf(defaultValue));
    }
    
    IntegerStyleParameter(String name, int defaultValue, String[] allowedValues) {
        super(name, String.valueOf(defaultValue), allowedValues);
    }
    
    public int value() {
        try {
            return Integer.parseInt(parameterValue());
        } catch (NumberFormatException nfe) {
            return Integer.MAX_VALUE;
        } catch (NullPointerException npe) {
            return Integer.MAX_VALUE;
        }
    }
    
    public boolean setValue(int value) {
        return setParameter(String.valueOf(value));
    }
    
    protected boolean setParameter(String parValue) {
        boolean result = super.setParameter(parValue);
        try {
            int d = Integer.parseInt(parValue);
            return result;
        } catch (NumberFormatException nfe) {
            return false;
        } catch (NullPointerException npe) {
            return false;
        }
    }
    
    public Class type() {
        return Integer.TYPE;
    }
    
}
