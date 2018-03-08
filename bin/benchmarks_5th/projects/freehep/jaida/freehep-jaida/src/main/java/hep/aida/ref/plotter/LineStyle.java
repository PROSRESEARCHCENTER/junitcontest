package hep.aida.ref.plotter;

import hep.aida.ILineStyle;

import java.util.Iterator;
import java.util.List;

/**
 *
 * @author The FreeHEP team @ SLAC.
 *
 */
public class LineStyle extends BrushStyle implements ILineStyle {
    
    protected void initializeBaseStyle() {
        super.initializeBaseStyle();
        String[] lineTypes = { "solid", "dotted", "dashed", "dotdash" };
        addParameter( new StringStyleParameter( Style.LINE_TYPE, lineTypes[0], lineTypes) );
        addParameter( new RevolvingStyleParameter( Style.LINE_TYPE_ROTATE, lineTypes[0]) );
        addParameter( new StringStyleParameter( Style.LINE_TYPE_ROTATE_METHOD, Style.ROTATE_METHOD[1], Style.ROTATE_METHOD ) );
        addParameter( new IntegerStyleParameter( Style.LINE_THICKNESS, 2) ); 
        
        String defaultRotation = lineTypes[0];
        for (int i=1; i<lineTypes.length; i++) defaultRotation += ", "+lineTypes[i];
        this.setParameterDefault(Style.LINE_TYPE_ROTATE, defaultRotation);
    }
    
    public String[] availableLineTypes() {
        return availableParameterOptions( Style.LINE_TYPE );
    }
    
    public String lineType() {
        return ( (StringStyleParameter) deepestSetParameter( Style.LINE_TYPE ) ).value();
    }
    
    public String lineType(int globalIndex, int overlayIndex) {
        if (isParameterSet(Style.LINE_TYPE, false)) return parameter(Style.LINE_TYPE).parValue();
        else if (isParameterSet(Style.LINE_TYPE_ROTATE, false)) {
            if (this.parameterValue(Style.LINE_TYPE_ROTATE_METHOD) == Style.ROTATE_METHOD[0]) {
                return ((RevolvingStyleParameter) parameter(Style.LINE_TYPE_ROTATE)).parameterValue(globalIndex);
            } else {
                return ((RevolvingStyleParameter) parameter(Style.LINE_TYPE_ROTATE)).parameterValue(overlayIndex);
            }
        }
        List parents = parentList();
        if (parents != null) {
            Iterator it = parents.iterator();
            while (it.hasNext()) {
                LineStyle pbs = null;
                Object obj = it.next();
                if (obj instanceof LineStyle) pbs = (LineStyle) obj;
                else continue;
                String tmp = pbs.color(globalIndex, overlayIndex);
                if (tmp != null) return tmp;
            }
        }
        return null;
    }
    
    public boolean setLineType(String lineType) {
        return ( (StringStyleParameter) parameter( Style.LINE_TYPE ) ).setValue( lineType );
    }
    
    public boolean setThickness(int lineThickness) {
        return ( (IntegerStyleParameter) parameter( Style.LINE_THICKNESS ) ).setValue( lineThickness );
    }
    
    public int thickness() {
        return ( (IntegerStyleParameter) deepestSetParameter( Style.LINE_THICKNESS ) ).value();
    }
    
}
