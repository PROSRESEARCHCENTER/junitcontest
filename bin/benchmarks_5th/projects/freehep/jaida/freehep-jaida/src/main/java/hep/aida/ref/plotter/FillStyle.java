package hep.aida.ref.plotter;

import hep.aida.IFillStyle;

/**
 *
 * @author The FreeHEP team @ SLAC
 *
 */
public class FillStyle extends BrushStyle implements IFillStyle {
    
    protected void initializeBaseStyle() {
        super.initializeBaseStyle();
        String[] colorMapScheme = {"none","warm", "cool", "thermal", "rainbow", "grayscale"};
        addParameter( new StringStyleParameter("colorMapScheme", colorMapScheme[0], colorMapScheme) );
        addParameter( new ColorStyleParameter("startColor") );
        addParameter( new ColorStyleParameter("endColor") );
        addParameter( new StringStyleParameter(Style.FILL_PATTERN, null) );
        addParameter( new BooleanStyleParameter(Style.FILL_SHOW_ZERO_HEIGHT_BINS, true) );
    }
    
    public String[] availablePatterns() {
        return availableParameterOptions( Style.FILL_PATTERN );
    }
    
    public String pattern() {
        return ( (StringStyleParameter) deepestSetParameter( Style.FILL_PATTERN ) ).value();
    }
    
    public boolean setPattern(String pattern) {
        return ( (StringStyleParameter) parameter( Style.FILL_PATTERN ) ).setValue( pattern );
    }    
}
