package hep.aida.ref.plotter;

import hep.aida.IMarkerStyle;

import java.util.Iterator;
import java.util.List;

/**
 *
 * @author The FreeHEP team @ SLAC.
 *
 */
public class MarkerStyle extends BrushStyle implements IMarkerStyle {
    
    protected void initializeBaseStyle() {
        super.initializeBaseStyle();
        String[] availShapes = {"dot", "box", "triangle", "diamond", "star", "verticalLine", "horizontalLine", "cross", "circle", "square"};
        addParameter( new StringStyleParameter( Style.MARKER_SHAPE, null, availShapes) );
        addParameter( new RevolvingStyleParameter( Style.MARKER_SHAPE_ROTATE, availShapes[0]) );
        addParameter( new StringStyleParameter( Style.MARKER_SHAPE_ROTATE_METHOD, Style.ROTATE_METHOD[1], Style.ROTATE_METHOD ) );
        addParameter( new IntegerStyleParameter(Style.MARKER_SIZE, 6) );
        
        String defaultRotation = "dot, box, triangle, diamond, star, circle, square";
        this.setParameterDefault(Style.MARKER_SHAPE_ROTATE, defaultRotation);
    }
    
    public String[] availableShapes() {
        return availableParameterOptions(Style.MARKER_SHAPE);
    }
    
    public boolean setShape(String markerShape) {
        return ( (StringStyleParameter) parameter(Style.MARKER_SHAPE) ).setValue(markerShape);
    }
    
    public String shape() {
        return ( (StringStyleParameter) deepestSetParameter(Style.MARKER_SHAPE) ).value();
    }    

       public String shape(int globalIndex, int overlayIndex) {
        if (isParameterSet(Style.MARKER_SHAPE, false)) return parameter(Style.MARKER_SHAPE).parValue();
        else if (isParameterSet(Style.MARKER_SHAPE_ROTATE, false)) {
            if (this.parameterValue(Style.MARKER_SHAPE_ROTATE) == Style.ROTATE_METHOD[0]) {
                return ((RevolvingStyleParameter) parameter(Style.MARKER_SHAPE_ROTATE)).parameterValue(globalIndex);
            } else {
                return ((RevolvingStyleParameter) parameter(Style.MARKER_SHAPE_ROTATE)).parameterValue(overlayIndex);
            }
        }
        List parents = parentList();
        if (parents != null) {
            Iterator it = parents.iterator();
            while (it.hasNext()) {
                MarkerStyle pbs = null;
                Object obj = it.next();
                if (obj instanceof MarkerStyle) pbs = (MarkerStyle) obj;
                else continue;
                String tmp = pbs.shape(globalIndex, overlayIndex);
                if (tmp != null) return tmp;
            }
        }
        return null;
    }
 /**
     * Set the marker's size.
     * @param size The marker's size.
     * @return false if the implementation cannot perform the requested change
     *               in the marker's size.
     *
     */
    public boolean setSize(int size) {
        return ( (IntegerStyleParameter) parameter(Style.MARKER_SIZE) ).setValue(size);
    }

    /**
     * Get the marker's size.
     * @return The marker's size.
     *
     */
    public int size() {
        return ( (IntegerStyleParameter) deepestSetParameter(Style.MARKER_SIZE) ).value();
    }
    
}
