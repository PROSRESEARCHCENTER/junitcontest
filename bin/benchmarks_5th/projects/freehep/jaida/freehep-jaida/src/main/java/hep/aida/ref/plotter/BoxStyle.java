package hep.aida.ref.plotter;

import hep.aida.IBorderStyle;
import hep.aida.IBoxStyle;
import hep.aida.IFillStyle;

/**
 *
 * @author The FreeHEP team @ SLAC
 */
public class BoxStyle extends BaseStyle implements IBoxStyle {
    
    protected void initializeBaseStyle() {
        setForegroundStyle(new FillStyle());
        setBackgroundStyle(new FillStyle());
        setBorderStyle(new BorderStyle());
        addParameter( new DoubleStyleParameter( Style.BOX_X_COORDINATE, Double.NaN ) );
        addParameter( new DoubleStyleParameter( Style.BOX_Y_COORDINATE, Double.NaN ) );
        addParameter( new DoubleStyleParameter( Style.BOX_HEIGHT, Double.NaN ) );
        addParameter( new DoubleStyleParameter( Style.BOX_WIDTH, Double.NaN ) );
        String[] units = {"pixels", "percent", "dataValue" };
        addParameter( new IntegerStyleParameter( Style.BOX_UNITS, 0, units ) );
        String[] placement = {"bottomLeft", "middleLeft", "topLeft", "topCenter", "topRight", "middleRight", "bottomRight", "bottomCenter", "center" };
        addParameter( new IntegerStyleParameter( Style.BOX_PLACEMENT, 3, placement ) );
    }
    
    /**
     * Get the IFillStyle that controls the background of the scene part.
     * @return The background IFillStyle.
     *
     */
    public IFillStyle backgroundStyle() {
        return (IFillStyle) child(Style.BOX_BACKGROUND_STYLE);
    }
    
    public boolean setBackgroundStyle(IFillStyle fillStyle) {
        return addBaseStyle(fillStyle,Style.BOX_BACKGROUND_STYLE);
    }
    
    /**
     * Get the IFillStyle that controls the foreground of the scene part.
     * The foreground controls the color of what appears in front unless otherwise
     * specified by other styles. (Is this really needed?)
     * @return The foreground IFillStyle.
     *
     */
    public IFillStyle foregroundStyle() {
        return (IFillStyle) child(Style.BOX_FOREGROUND_STYLE);
    }
    
    public boolean setForegroundStyle(IFillStyle fillStyle) {
        return addBaseStyle(fillStyle,Style.BOX_FOREGROUND_STYLE);
    }
    
    /**
     * Get the border style of the scene part.
     * @return The border style.
     *
     */
    public IBorderStyle borderStyle() {
        return (IBorderStyle) child(Style.BOX_BORDER_STYLE);
    }
    
    public boolean setBorderStyle(IBorderStyle borderStyle) {
        return addBaseStyle(borderStyle, Style.BOX_BORDER_STYLE);
    }
    
    /**
     * Set and get the x and y coordinate of the box's origin.
     *
     */
    public boolean setX(double x) {
        return ((DoubleStyleParameter) parameter(Style.BOX_X_COORDINATE) ).setValue(x);
    }
    
    public boolean setY(double y) {
        return ((DoubleStyleParameter) parameter(Style.BOX_Y_COORDINATE) ).setValue(y);
    }
    
    public double x() {
        return ((DoubleStyleParameter) deepestSetParameter(Style.BOX_X_COORDINATE) ).value();
    }
    
    public double y() {
        return ((DoubleStyleParameter) deepestSetParameter(Style.BOX_Y_COORDINATE) ).value();
    }
    
    /**
     * Set and get the height and the width of the box.
     *
     */
    public boolean setHeight(double height) {
        return ((DoubleStyleParameter) parameter(Style.BOX_HEIGHT) ).setValue(height);
    }
    
    public boolean setWidth(double width) {
        return ((DoubleStyleParameter) parameter(Style.BOX_WIDTH) ).setValue(width);
    }
    
    public double height() {
        return ((DoubleStyleParameter) deepestSetParameter(Style.BOX_HEIGHT) ).value();
    }
    
    public double width() {
        return ((DoubleStyleParameter) deepestSetParameter(Style.BOX_WIDTH) ).value();
    }
    
    /**
     * Get and specify the origin's definition.
     * The default is BOTTOM_LEFT.
     *
     */
    public boolean setPlacement(int originDefinition) {
        return ((IntegerStyleParameter) parameter(Style.BOX_PLACEMENT) ).setValue(originDefinition);
    }
    
    public int placement() {
        return ((IntegerStyleParameter) deepestSetParameter(Style.BOX_PLACEMENT) ).value();
    }
    
    /**
     * Get and specify the definition of the units used for the size and origin's position.
     * The default is PIXELS.
     *
     */
    public boolean setUnits(int unitsDefinition) {
        return ((IntegerStyleParameter) parameter(Style.BOX_UNITS) ).setValue(unitsDefinition);
    }
    
    public int units() {
        return ((IntegerStyleParameter) deepestSetParameter(Style.BOX_UNITS) ).value();
    }
    
}
