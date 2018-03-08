package hep.aida.ref.plotter;

import hep.aida.IAxisStyle;
import hep.aida.IBoxStyle;
import hep.aida.IDataStyle;
import hep.aida.IGridStyle;
import hep.aida.IInfoStyle;
import hep.aida.ILegendBoxStyle;
import hep.aida.IPlotterStyle;
import hep.aida.IStatisticsBoxStyle;
import hep.aida.ITitleStyle;

import java.util.List;

/**
 *
 * @author The AIDA team @ SLAC.
 *
 */
public class PlotterStyle extends BaseStyle implements IPlotterStyle {
    
    protected PlotterStyle() {
        super();
    }
    
    protected PlotterStyle( PlotterStyle style ) {
        super( style );
    }

    public void print(String pref) {
        System.out.println(pref+"***** PlotterStyle :: "+parameterValue("plotterStyleName"));

        System.out.println(pref+"\t  Parents ");
        for (int i=0; i<parentList().size(); i++) {
            String name = "";
            Object obj = parentList().get(i);
            if (obj instanceof IPlotterStyle) name = ((IPlotterStyle) obj).parameterValue("plotterStyleName");
            System.out.println(pref+"\t\t"+obj+"  "+name);
            if (obj instanceof PlotterStyle) ((PlotterStyle) obj).print(pref+"\t\t");
        }

        System.out.println(pref+"\t  Listeners ");
        for (int i=0; i<listeners.size(); i++) {
            String name = "";
            Object obj = listeners.get(i);
            if (obj instanceof IPlotterStyle) name = ((IPlotterStyle) obj).parameterValue("plotterStyleName");
            System.out.println(pref+"\t\t"+obj+"  "+name);
            //if (obj instanceof PlotterStyle) ((PlotterStyle) obj).print(pref+"\t\t");
        }
    }
    
    protected void initializeBaseStyle() {
        addParameter( new StringStyleParameter(Style.PLOTTER_STYLE_NAME, null) );
        addParameter( new IntegerStyleParameter(Style.PLOTTER_STYLE_INDEX, -1) );
        setDataStyle( new DataStyle() );
        setInfoStyle( new InfoStyle() );
        setTitleStyle( new TitleStyle() );
        setAxisStyleX( new AxisStyle() );
        
        AxisStyle yStyle = new AxisStyle();
        String[] yAxisValues = {"Y0", "Y1"};
        yStyle.addParameter( new StringStyleParameter( "yAxis", yAxisValues[0], yAxisValues ) );
        setAxisStyleY( yStyle );

        setAxisStyleZ( new AxisStyle() );
        setLegendBoxStyle( new LegendBoxStyle() );
        setStatisticsBoxStyle( new StatisticsBoxStyle() );
        setGridStyle( new GridStyle() );
        setRegionBoxStyle( new BoxStyle() );
        setDataBoxStyle( new BoxStyle() );
                
        String[] hist2dStyle = {"box", "ellipse", "colorMap"};
        addParameter( new StringStyleParameter("hist2DStyle", null, hist2dStyle) );
        addParameter( new BooleanStyleParameter("showAsScatterPlot",true ) );
        
        addParameter( new StringStyleParameter("xAxisLowerLimit", null) );
        addParameter( new StringStyleParameter("xAxisUpperLimit", null) );
        addParameter( new StringStyleParameter("yAxisLowerLimit", null) );
        addParameter( new StringStyleParameter("yAxisUpperLimit", null) );
        
    }
    
    // Deprecate limit parameters here
    
    public boolean setParameter(String pn, String parValue, String[] parAllowedValues) {
        if (pn.equals("xAxisLowerLimit") || pn.equals("xAxisUpperLimit") ||
            pn.equals("yAxisLowerLimit") || pn.equals("yAxisUpperLimit") ) {
            System.err.println("Parameter \""+pn+"\" works, but has been deprecated. " +
                    "Please use \""+Style.AXIS_LOWER_LIMIT+"\" or \""+Style.AXIS_UPPER_LIMIT+"\" parameters of the " +
                    "relevant IAxisStyle.");
        }
        return super.setParameter(pn, parValue, parAllowedValues);
    }
    
    
    // IPlotterStyle methods here
    
    public IDataStyle dataStyle() {
        return (IDataStyle) child(Style.PLOTTER_DATA_STYLE);
    }
    
    /**
     * @deprecated
     */
    public IInfoStyle infoStyle() {
        return (IInfoStyle) child(Style.PLOTTER_INFO_STYLE);
    }
    
    public ITitleStyle titleStyle() {
        return (ITitleStyle) child(Style.PLOTTER_TITLE_STYLE);
    }
    
    public IAxisStyle xAxisStyle() {
        return (IAxisStyle) child(Style.PLOTTER_XAXIS_STYLE);
    }
    
    public IAxisStyle yAxisStyle() {
        return (IAxisStyle) child(Style.PLOTTER_YAXIS_STYLE);
    }
    
    public IAxisStyle zAxisStyle() {
        return (IAxisStyle) child(Style.PLOTTER_ZAXIS_STYLE);
    }
    
    public ILegendBoxStyle legendBoxStyle() {
        return (ILegendBoxStyle) child(Style.PLOTTER_LEGEND_BOX_STYLE);
    }
    
    public IStatisticsBoxStyle statisticsBoxStyle() {
        return (IStatisticsBoxStyle) child(Style.PLOTTER_STATISTICS_BOX_STYLE);
    }
    
    public IGridStyle gridStyle() {
        return (IGridStyle) child(Style.PLOTTER_GRID_STYLE);
    }
    
    public IBoxStyle regionBoxStyle() {
        return (IBoxStyle) child(Style.PLOTTER_REGION_BOX_STYLE);
    }
    
    public IBoxStyle dataBoxStyle() {
        return (IBoxStyle) child(Style.PLOTTER_DATA_BOX_STYLE);
    }
    
    public boolean setAxisStyleX(IAxisStyle xAxisStyle) {
        return addBaseStyle( xAxisStyle, Style.PLOTTER_XAXIS_STYLE );
    }
    
    public boolean setAxisStyleY(IAxisStyle yAxisStyle) {
        return addBaseStyle( yAxisStyle, Style.PLOTTER_YAXIS_STYLE );
    }
    
    public boolean setAxisStyleZ(IAxisStyle zAxisStyle) {
        return addBaseStyle( zAxisStyle, Style.PLOTTER_ZAXIS_STYLE );
    }
    
    public boolean setDataStyle(IDataStyle dataStyle) {
        return addBaseStyle( dataStyle, Style.PLOTTER_DATA_STYLE );
    }
    
    public boolean setInfoStyle(IInfoStyle infoStyle) {
        return addBaseStyle( infoStyle, Style.PLOTTER_INFO_STYLE );
    }
    
    public boolean setTitleStyle(ITitleStyle titleStyle) {
        return addBaseStyle( titleStyle, Style.PLOTTER_TITLE_STYLE );
    }
    
    public boolean setLegendBoxStyle(ILegendBoxStyle legendBoxStyle) {
        return addBaseStyle( legendBoxStyle, Style.PLOTTER_LEGEND_BOX_STYLE );
    }
    
    public boolean setStatisticsBoxStyle(IStatisticsBoxStyle statisticsBoxStyle) {
        return addBaseStyle( statisticsBoxStyle, Style.PLOTTER_STATISTICS_BOX_STYLE );
    }
    
    public boolean setGridStyle(IGridStyle gridStyle) {
        return addBaseStyle( gridStyle, Style.PLOTTER_GRID_STYLE );
    }
    
    public boolean setRegionBoxStyle(IBoxStyle regionBoxStyle) {
        return addBaseStyle( regionBoxStyle, Style.PLOTTER_REGION_BOX_STYLE );
    }
    
    public boolean setDataBoxStyle(IBoxStyle dataBoxStyle) {
        return addBaseStyle( dataBoxStyle, Style.PLOTTER_DATA_BOX_STYLE );
    }
    
}
