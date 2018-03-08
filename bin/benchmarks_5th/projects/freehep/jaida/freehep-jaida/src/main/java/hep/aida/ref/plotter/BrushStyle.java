package hep.aida.ref.plotter;

import hep.aida.IBrushStyle;

import java.util.Iterator;
import java.util.List;

/**
 *
 * @author The FreeHEP team @ SLAC.
 *
 */
abstract class BrushStyle extends BaseStyle implements IBrushStyle {
    
    protected void initializeBaseStyle() {        
        addParameter( new ColorStyleParameter( Style.BRUSH_COLOR) );
        addParameter( new RevolvingColorStyleParameter( Style.BRUSH_COLOR_ROTATE) );
        addParameter( new StringStyleParameter( Style.BRUSH_COLOR_ROTATE_METHOD, Style.ROTATE_METHOD[0], Style.ROTATE_METHOD ) );
        addParameter( new DoubleStyleParameter( Style.BRUSH_OPACITY, 1, 0, 1) );
        
        String defaultRotation = "fuchsia, green, orange, cyan, blue, red";
        this.setParameterDefault(Style.BRUSH_COLOR_ROTATE, defaultRotation);
    }
    
    public String[] availableColors() {
        return availableParameterOptions( Style.BRUSH_COLOR );
    }   
    
    public String color() {
        return ( (ColorStyleParameter) deepestSetParameter( Style.BRUSH_COLOR ) ).stringValue();
    }
    
    public String color(int gi, int oi) {
        if (isParameterSet(Style.BRUSH_COLOR, false)) {
            return parameter(Style.BRUSH_COLOR).parValue();
        } else if (isParameterSet(Style.BRUSH_COLOR_ROTATE, false)) {
            if (parameterValue(Style.BRUSH_COLOR_ROTATE_METHOD).trim().equalsIgnoreCase(Style.ROTATE_METHOD[0])) {
                return ((RevolvingColorStyleParameter) parameter(Style.BRUSH_COLOR_ROTATE)).stringValue(gi);
            } else {
                return ((RevolvingColorStyleParameter) parameter(Style.BRUSH_COLOR_ROTATE)).stringValue(oi);
            }
        }
        List parents = parentList();
        if (parents != null) {
            Iterator it = parents.iterator();
            while (it.hasNext()) {
                BrushStyle pbs = null;
                Object obj = it.next();
                if (obj instanceof BrushStyle) pbs = (BrushStyle) obj;
                else continue;
                String tmp = pbs.color(gi, oi);
                if (tmp != null) return tmp;
            }
        }
        return null;
    }
    
    /**
     * returns -1 if it not set
     */
    public double opacity() {
        return ( (DoubleStyleParameter) deepestSetParameter( Style.BRUSH_OPACITY ) ).value();
    }
    
    public boolean setColor(String color) {
        return this.setParameter(Style.BRUSH_COLOR, color);
    }
    
    public boolean setOpacity(double opacity) {
        return ( (DoubleStyleParameter) parameter( Style.BRUSH_OPACITY ) ).setValue(opacity);
    }    
}
