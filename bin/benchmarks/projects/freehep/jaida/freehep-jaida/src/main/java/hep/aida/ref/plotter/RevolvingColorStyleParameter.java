package hep.aida.ref.plotter;

import java.awt.Color;

import org.freehep.swing.ColorConverter;

/**
 *
 * @author The FreeHEP team @ SLAC
 */
public class RevolvingColorStyleParameter extends RevolvingStyleParameter {
    
    RevolvingColorStyleParameter(String name) {
        this(name, null);
    }
    
    RevolvingColorStyleParameter(String name, String defaultValue) {
        super(name, defaultValue);
    }
    
    public Color color() {
        String value = parameterValue();
        if ( value == null )
            return null;
        try {
            Color color = ColorConverter.get( value );
            return color;
        } catch ( Exception e ) {
            throw new RuntimeException("Problem converting string "+value+" to a Color", e);
        }
    }
    
    public Color color(int index) {
        String value = parameterValue(index);
        if ( value == null )
            return null;
        try {
            Color color = ColorConverter.get( value );
            return color;
        } catch ( Exception e ) {
            throw new RuntimeException("Problem converting string "+value+" to a Color", e);
        }
    }
    
    public String stringValue() {
        return parameterValue();
    }
    
    public String stringValue(int index) {
        return parameterValue(index);
    }

    public Class type() {
        return RevolvingColorStyleParameter.class;
    }
    
    
}
