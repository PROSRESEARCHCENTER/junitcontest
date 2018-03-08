package hep.aida.ref.plotter;

import hep.aida.IBoxStyle;
import hep.aida.IStatisticsBoxStyle;
import hep.aida.ITextStyle;


/**
 *
 * @author The FreeHEP team @ SLAC
 */
public class StatisticsBoxStyle extends BaseStyle implements IStatisticsBoxStyle {

    protected void initializeBaseStyle() {
        setBoxStyle(new BoxStyle());
        setTextStyle(new TextStyle());
        addParameter( new StringStyleParameter(Style.VISIBLE_STATISTICS,null) );
    }

    
    public IBoxStyle boxStyle() {
        return (IBoxStyle) child(Style.STATISTICS_BOX_STYLE);
    }

    public boolean setBoxStyle(IBoxStyle boxStyle) {
        return addBaseStyle(boxStyle, Style.STATISTICS_BOX_STYLE);
    }

    public boolean setTextStyle(ITextStyle textStyle) {
        return addBaseStyle(textStyle, Style.STATISTICS_TEXT_STYLE);
    }

    public ITextStyle textStyle() {
        return (ITextStyle) child(Style.STATISTICS_TEXT_STYLE);
    }
    
    public boolean setVisibileStatistics(String visibleStat) {
        return ( (StringStyleParameter) parameter(Style.VISIBLE_STATISTICS) ).setValue(visibleStat);
    }

    public String visibleStatistics() {
        return ( (StringStyleParameter) deepestSetParameter(Style.VISIBLE_STATISTICS) ).value();
    }
    
    
}
