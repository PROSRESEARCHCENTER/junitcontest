package hep.aida.ref.plotter;

/**
 *
 * @author The FreeHEP team @ SLAC
 */
public class StringStyleParameter extends AbstractStyleParameter {
    
    StringStyleParameter(String name, String defaultValue) {
        super(name,defaultValue);
    }
    
    StringStyleParameter(String name, String defaultValue, String[] allowedValues) {
        super(name, defaultValue, allowedValues);
    }
    
    public String value() {
        return parameterValue();
    }
    
    public boolean setValue(String value) {
        return setParameter(value);
    }
    
    public Class type() {
        return String.class;
    }

}
