package hep.aida.ref.plotter;

import hep.aida.IBoxStyle;
import hep.aida.ITextStyle;
import hep.aida.ITitleStyle;

/**
 *
 * @author The FreeHEP team @ SLAC.
 *
 */
public class TitleStyle extends BaseStyle implements ITitleStyle {
        
    protected void initializeBaseStyle() {
        setTextStyle( new TextStyle() );
        setBoxStyle( new BoxStyle() );
    }
    
    public boolean setTextStyle(ITextStyle textStyle) {
        return addBaseStyle( textStyle, Style.TITLE_TEXT_STYLE );
    }
    
    public ITextStyle textStyle() {
        return (ITextStyle) child(Style.TITLE_TEXT_STYLE);
    }    

    public boolean setBoxStyle(IBoxStyle boxStyle) {
        return addBaseStyle(boxStyle,Style.TITLE_BOX_STYLE);
    }

    public IBoxStyle boxStyle() {
        return (IBoxStyle) child(Style.TITLE_BOX_STYLE);
    }
}
