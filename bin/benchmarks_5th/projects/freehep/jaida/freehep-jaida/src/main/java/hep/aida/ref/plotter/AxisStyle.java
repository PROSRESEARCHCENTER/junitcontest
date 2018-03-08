package hep.aida.ref.plotter;

import hep.aida.IAxisStyle;
import hep.aida.ILineStyle;
import hep.aida.ITextStyle;

/**
 *
 * @author The AIDA team @ SLAC.
 *
 */
public class AxisStyle extends BaseStyle implements IAxisStyle {
    
    protected void initializeBaseStyle() {
        String[] scaleOptions = {"lin", "linear", "log", "logarithmic"};
        addParameter( new StringStyleParameter( Style.AXIS_SCALE, scaleOptions[0], scaleOptions ) );
        
        String[] typeOptions = {"double", "int", "date", "string"};
        addParameter( new StringStyleParameter( Style.AXIS_TYPE, typeOptions[0], typeOptions ) );
        
        addParameter( new StringStyleParameter( Style.AXIS_LABEL, "" ) );        
        addParameter( new BooleanStyleParameter( Style.AXIS_VERTICAL_LABEL, false ) );
        addParameter( new BooleanStyleParameter( Style.AXIS_ALLOW_ZERO_SUPPRESSION, true ) );
        addParameter( new StringStyleParameter(Style.AXIS_LOWER_LIMIT, null) );
        addParameter( new StringStyleParameter(Style.AXIS_UPPER_LIMIT, null) );
          
        setTickLabelStyle(new TextStyle());
        setLabelStyle(new TextStyle());
        setLineStyle(new LineStyle());
    }
    
    // JAIDA-only methods
    
    public boolean setLowerLimit(String val) {
        return ( (StringStyleParameter) parameter( Style.AXIS_LOWER_LIMIT ) ).setValue( val );
    }
    
    public String lowerLimit() {
        return ( (StringStyleParameter) deepestSetParameter( Style.AXIS_LOWER_LIMIT ) ).value();
    }
    
    public boolean setUpperLimit(String val) {
        return ( (StringStyleParameter) parameter( Style.AXIS_UPPER_LIMIT ) ).setValue( val );
    }
    
    public String upperLimit() {
        return ( (StringStyleParameter) deepestSetParameter( Style.AXIS_UPPER_LIMIT ) ).value();
    }
    
    // IAxisStyle methods
    
    public boolean setLabel(String label) {
        return ( (StringStyleParameter) parameter( Style.AXIS_LABEL ) ).setValue( label );
    }
    
    public String label() {
        return ( (StringStyleParameter) deepestSetParameter( Style.AXIS_LABEL ) ).value();
    }
    
    /**
     * Set the axis scaling (at least "log" and "linear").
     */
    public boolean setScaling(String scaling) {
        return ( (StringStyleParameter) parameter( Style.AXIS_SCALE ) ).setValue(scaling);
    }
    
    /**
     * Get the axis scaling.
     * return The axis scaling.
     */
    public String scaling() {
        return ( (StringStyleParameter) deepestSetParameter( Style.AXIS_SCALE ) ).value();
    }
    
    public ITextStyle labelStyle() {
        return (ITextStyle) child(Style.AXIS_LABEL_STYLE);
    }
    
    public ILineStyle lineStyle() {
        return (ILineStyle) child(Style.AXIS_LINE_STYLE);
    }
    
    public ITextStyle tickLabelStyle() {
        return (ITextStyle) child(Style.AXIS_TICK_LABEL_STYLE);
    }
    
    public boolean setLabelStyle(ITextStyle labelStyle) {
        return addBaseStyle( labelStyle, Style.AXIS_LABEL_STYLE );
    }
    
    public boolean setTickLabelStyle(ITextStyle tickLabelStyle) {
        return addBaseStyle( tickLabelStyle, Style.AXIS_TICK_LABEL_STYLE );
    }
    
    /**
     * @deprecated Use setLineStyle instead.
     */
    public boolean setlineStyle(ILineStyle lineStyle) {
        return setLineStyle(lineStyle);
    }
    
    public boolean setLineStyle(ILineStyle lineStyle) {
        return addBaseStyle( lineStyle, Style.AXIS_LINE_STYLE );
    }
}
