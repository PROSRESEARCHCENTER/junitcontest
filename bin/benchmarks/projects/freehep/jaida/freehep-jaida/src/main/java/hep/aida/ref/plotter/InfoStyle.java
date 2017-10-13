package hep.aida.ref.plotter;

import hep.aida.IInfoStyle;
import hep.aida.ITextStyle;

/**
 *
 * @author The FreeHEP team @ SLAC.
 *
 */
public class InfoStyle extends BaseStyle implements IInfoStyle {
    
    protected void initializeBaseStyle() {
        setTextStyle(new TextStyle());
    }
    
    public boolean setTextStyle(ITextStyle textStyle) {
        return addBaseStyle(textStyle, Style.INFO_TEXT_STYLE);
    }
    
    public ITextStyle textStyle() {
        return (ITextStyle) child(Style.INFO_TEXT_STYLE);
    }    
        
}
