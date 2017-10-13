package hep.aida.ref.plotter;

import java.awt.Font;
import java.awt.GraphicsEnvironment;

/**
 *
 * @author The FreeHEP team @ SLAC
 */
public class FontStyleParameter extends StringStyleParameter {
    
    private static String[] defaultValues;
    
    static {
        try {
            defaultValues = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        } catch (InternalError ie) {
            // No display
            defaultValues = null;
        }
    }
    
    FontStyleParameter(String name, String defaultValue) {
        super(name, defaultValue, defaultValues);
    }
    
    public Class type() {
        return Font.class;
    }
    
    protected String[] allowedValues() {
        //This method is overwritten for when there is no display.
        //defaultValues is null so that any value is allowed, but when allowedValues is invoked, we return something 
        //even when there is no display.
        if ( defaultValues == null )
            return new String[] {"Serif"};
        return super.allowedValues();
    }
    
    
}
