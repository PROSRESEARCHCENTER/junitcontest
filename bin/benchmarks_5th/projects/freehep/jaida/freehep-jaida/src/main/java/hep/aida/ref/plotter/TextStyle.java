package hep.aida.ref.plotter;

import hep.aida.ITextStyle;

/**
 *
 * @author The FreeHEP team @ SLAC.
 *
 */
public class TextStyle extends BrushStyle implements ITextStyle {
    
    protected void initializeBaseStyle() {
        super.initializeBaseStyle();
        addParameter( new FontStyleParameter( Style.TEXT_FONT, "SansSerif") );
        addParameter( new DoubleStyleParameter(Style.TEXT_FONT_SIZE, 12) );
        addParameter( new BooleanStyleParameter(Style.TEXT_BOLD, false) );
        addParameter( new BooleanStyleParameter(Style.TEXT_ITALIC, false) );
        addParameter( new BooleanStyleParameter(Style.TEXT_UNDERLINED, false) );
    }
    
    public String[] availableFonts() {
        return availableParameterOptions( Style.TEXT_FONT );
    }
    
    public String font() {
        return ( (FontStyleParameter) deepestSetParameter( Style.TEXT_FONT ) ).value();
    }
    
    public double fontSize() {
        return ( ( DoubleStyleParameter ) deepestSetParameter( Style.TEXT_FONT_SIZE ) ).value();
    }
    
    public boolean isBold() {
        return ( ( BooleanStyleParameter ) deepestSetParameter(Style.TEXT_BOLD) ).value();
    }
    
    public boolean isItalic() {
        return ( ( BooleanStyleParameter ) deepestSetParameter(Style.TEXT_ITALIC) ).value();
    }
    
    public boolean isUnderlined() {
        return ( ( BooleanStyleParameter ) deepestSetParameter(Style.TEXT_UNDERLINED) ).value();
    }
    
    public boolean setBold() {
        return ( ( BooleanStyleParameter ) parameter(Style.TEXT_BOLD) ).setParameter();
    }
    
    public boolean setBold(boolean isBold) {
        return ( ( BooleanStyleParameter ) parameter(Style.TEXT_BOLD) ).setValue(isBold);
    }
    
    public boolean setFont(String font) {
        return ( ( FontStyleParameter ) parameter(Style.TEXT_FONT) ).setValue(font);
    }
    
    public boolean setFontSize(double fontSize) {
        return ( ( DoubleStyleParameter ) parameter(Style.TEXT_FONT_SIZE) ).setValue(fontSize);
    }
    
    public boolean setItalic() {
        return ( ( BooleanStyleParameter ) parameter(Style.TEXT_ITALIC) ).setParameter();
    }
    
    public boolean setItalic(boolean isItalic) {
        return ( ( BooleanStyleParameter ) parameter(Style.TEXT_ITALIC) ).setValue(isItalic);
    }
    
    public boolean setUnderlined() {
        return ( ( BooleanStyleParameter ) parameter(Style.TEXT_UNDERLINED) ).setParameter();
    }
    
    public boolean setUnderlined(boolean isUnderlined) {
        return ( ( BooleanStyleParameter ) parameter(Style.TEXT_UNDERLINED) ).setValue(isUnderlined);
    }
}
