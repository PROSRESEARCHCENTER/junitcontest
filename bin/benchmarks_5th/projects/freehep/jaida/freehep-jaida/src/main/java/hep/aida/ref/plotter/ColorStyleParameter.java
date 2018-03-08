package hep.aida.ref.plotter;

import java.awt.Color;

import org.freehep.swing.ColorConverter;

/**
 *
 * @author The FreeHEP team @ SLAC
 */
public class ColorStyleParameter extends AbstractStyleParameter {
    
    ColorStyleParameter(String name) {
        super(name, (String) null);
    }

    ColorStyleParameter(String name, String defaultColor) {
        super(name, defaultColor);
    }
    
    public Color color() {
        String value = parameterValue();
        if ( value == null )
            return null;
        try {
            Color color = ColorConverter.get( value );
            return color;
        } catch ( Exception e ) {
            throw new RuntimeException("Problem converting string "+value+" to a Color");
        }
    }
    
    public String stringValue() {
        return parameterValue();
    }
    
    public boolean setColor(Color color) {
        return setParameter( ColorConverter.get( color ) );
    }
    
    public boolean setStringValue(String color) {
        return setParameter(color);
    }
    
    protected boolean setParameter(String parValue) {
        boolean result = super.setParameter(parValue);
        try {
            Color c = ColorConverter.get(parValue);
            return result;
        } catch (Exception cce) {
            return false;
        }
    }
    
    public Class type() {
        return Color.class;
    }
    
    
}
