package hep.aida.ref.plotter;

/**
 *
 * @author The FreeHEP team @ SLAC
 */
public class BooleanStyleParameter extends AbstractStyleParameter {
    
    private static String[] parValues = {"true","false"};
    
    BooleanStyleParameter(String name, boolean defaultValue) {
        super(name, String.valueOf(defaultValue), parValues);
    }
    
    public boolean value() {
        return Boolean.valueOf( parameterValue() ).booleanValue();
    }
    
    public boolean setValue(boolean value) {
        return setParameter(String.valueOf(value));
    }    

    public Class type() {
        return Boolean.TYPE;
    }

}
