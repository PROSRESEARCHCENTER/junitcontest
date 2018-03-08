package hep.aida.ref.plotter;

import hep.aida.IDataStyle;
import hep.aida.IFillStyle;
import hep.aida.ILineStyle;
import hep.aida.IMarkerStyle;
import hep.aida.ref.AidaUtils;

/**
 *
 * @author The AIDA team @ SLAC.
 *
 */
public class DataStyle extends BaseStyle implements IDataStyle {
    
    protected void initializeBaseStyle() {
        setMarkerStyle(new MarkerStyle());
        setLineStyle(new LineStyle());
        setFillStyle(new FillStyle());
        setErrorBarStyle(new LineStyle());        
        setOutlineStyle(new LineStyle());

        addParameter( new StringStyleParameter("customOverlay", null) );
        addParameter( new StringStyleParameter("timeZone", null) );
        addParameter( new StringStyleParameter(Style.DATA_MODEL, null) );

        String[] normalization = {"none","Entries", "Area", "MaxBin"};
        addParameter( new StringStyleParameter(Style.DATA_NORMALIZATION, normalization[0], normalization) );

        String[] profileErrors = {"spread","errorOnMean"};
        addParameter( new StringStyleParameter("profileErrors",profileErrors[0], profileErrors) );

        addParameter( new BooleanStyleParameter( Style.SHOW_DATA_IN_STATISTICS_BOX, true) );
        addParameter( new BooleanStyleParameter( Style.SHOW_DATA_IN_LEGEND_BOX, true) );
        
        // set the defaults:
        ((BaseStyle)outlineStyle()).setParameterDefault(Style.IS_VISIBLE,"false");
        ((BaseStyle)markerStyle()).setParameterDefault(Style.IS_VISIBLE,"false");
        
    }
        
    public IFillStyle fillStyle() {
        return (IFillStyle) child(Style.DATA_FILL_STYLE);
    }
    
    public ILineStyle lineStyle() {
        return (ILineStyle) child(Style.DATA_LINE_STYLE);
    }
    
    public IMarkerStyle markerStyle() {
        return (IMarkerStyle) child(Style.DATA_MARKER_STYLE);
    }
    
    public ILineStyle errorBarStyle() {
        return (ILineStyle) child(Style.DATA_ERRORBAR_STYLE);
    }
    
    public ILineStyle outlineStyle() {
        return (ILineStyle) child(Style.DATA_OUTLINE_STYLE);
    }

    public boolean setFillStyle(IFillStyle fillStyle) {
        return addBaseStyle( fillStyle,  Style.DATA_FILL_STYLE );
    }
    
    public boolean setLineStyle(ILineStyle lineStyle) {
        return addBaseStyle( lineStyle, Style.DATA_LINE_STYLE );
    }
    
    public boolean setMarkerStyle(IMarkerStyle markerStyle) {
        return addBaseStyle( markerStyle, Style.DATA_MARKER_STYLE );
    }    

    public boolean setErrorBarStyle(ILineStyle errorBarStyle) {
        int index = AidaUtils.findInArray(Style.ERRORBAR_DECORATION, errorBarStyle.availableParameters());
        if (index < 0 && errorBarStyle instanceof LineStyle) 
            ((LineStyle) errorBarStyle).addParameter( new DoubleStyleParameter( Style.ERRORBAR_DECORATION, -1) ); 

        return addBaseStyle( errorBarStyle, Style.DATA_ERRORBAR_STYLE );
        
    }
    public boolean setOutlineStyle(ILineStyle outlineStyle) {
        return addBaseStyle( outlineStyle, Style.DATA_OUTLINE_STYLE );
    }

    /**
     * Set the model accorting to which the data is represented.
     *
     */
    public boolean setModel(String model) {
        return ( (StringStyleParameter) parameter(Style.DATA_MODEL) ).setValue(model);
    }
    
    public String model() {
        return ( (StringStyleParameter) deepestSetParameter(Style.DATA_MODEL) ).value();
    }

    /**
     * Set if this data is to be represented in the statistics box.
     *
     */
    public void showInStatisticsBox(boolean showInStatisticsBox) {
        ( (BooleanStyleParameter) parameter(Style.SHOW_DATA_IN_STATISTICS_BOX) ).setValue(showInStatisticsBox);
    }
    public boolean isShownInStatisticsBox() {
        return ( (BooleanStyleParameter) deepestSetParameter(Style.SHOW_DATA_IN_STATISTICS_BOX) ).value();
    }        


    /**
     * Set if this data is to be represented in the legend box.
     *
     */
    public void showInLegendBox(boolean showInLegendBox) {
        ( (BooleanStyleParameter) parameter(Style.SHOW_DATA_IN_LEGEND_BOX) ).setValue(showInLegendBox);
    }
    public boolean isShownInLegendBox() {
        return ( (BooleanStyleParameter) deepestSetParameter(Style.SHOW_DATA_IN_LEGEND_BOX) ).value();
    }    
}
