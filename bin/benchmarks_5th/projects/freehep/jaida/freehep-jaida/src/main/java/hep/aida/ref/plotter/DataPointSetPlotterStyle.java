package hep.aida.ref.plotter;

/**
 *
 * @author The FreeeHEP team @ SLAC.
 *
 */
public class DataPointSetPlotterStyle extends PlotterStyle {
    
    public DataPointSetPlotterStyle() {
        ((BaseStyle)dataStyle().outlineStyle()).setParameterDefault(Style.IS_VISIBLE,"false");
        ((BaseStyle)dataStyle().lineStyle()).setParameterDefault(Style.IS_VISIBLE,"false");
        ((BaseStyle)dataStyle().fillStyle()).setParameterDefault(Style.IS_VISIBLE,"false");
        ((BaseStyle)dataStyle().errorBarStyle()).setParameterDefault(Style.IS_VISIBLE,"true");
        ((BaseStyle)dataStyle().markerStyle()).setParameterDefault(Style.IS_VISIBLE,"true");
        ((BaseStyle)dataStyle().errorBarStyle()).setParameterDefault(Style.BRUSH_COLOR,dataStyle().markerStyle().parameterValue(Style.BRUSH_COLOR));
    }
    
}
