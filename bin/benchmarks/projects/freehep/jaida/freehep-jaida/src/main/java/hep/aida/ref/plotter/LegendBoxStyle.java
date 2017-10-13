package hep.aida.ref.plotter;

import hep.aida.IBoxStyle;
import hep.aida.ILegendBoxStyle;
import hep.aida.ITextStyle;

/**
 *
 * @author The FreeHEP team @ SLAC
 */
public class LegendBoxStyle extends BaseStyle implements ILegendBoxStyle {
    
    protected void initializeBaseStyle() {
        setBoxStyle(new BoxStyle());
        setTextStyle(new TextStyle());
    }

    
    public IBoxStyle boxStyle() {
        return (IBoxStyle) child(Style.LEGEND_BOX_STYLE);
    }

    public boolean setBoxStyle(IBoxStyle boxStyle) {
        return addBaseStyle(boxStyle, Style.LEGEND_BOX_STYLE);
    }

    public boolean setTextStyle(ITextStyle textStyle) {
        return addBaseStyle(textStyle, Style.LEGEND_TEXT_STYLE);
    }

    public ITextStyle textStyle() {
        return (ITextStyle) child(Style.LEGEND_TEXT_STYLE);
    }
    
    
}
