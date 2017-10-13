package hep.aida.ref.plotter;

import hep.aida.IBorderStyle;

/**
 *
 * @author The FreeHEP team @ SLAC
 */
public class BorderStyle extends LineStyle implements IBorderStyle {
    
    protected void initializeBaseStyle() {
        super.initializeBaseStyle();
        String[] availableBorderTypes = {"none", "line", "bevelIn", "bevelOut", "ethched", "shadow"};        
        addParameter( new StringStyleParameter( Style.BORDER_TYPE, availableBorderTypes[0], availableBorderTypes ) );        
    }

    public String[] availableBorderTypes() {
        return availableParameterOptions(Style.BORDER_TYPE);
    }

    public String borderType() {
        return ( (StringStyleParameter) deepestSetParameter(Style.BORDER_TYPE) ).value();
    }

    public boolean setBorderType(String newBorderType) {
        return ( (StringStyleParameter) parameter(Style.BORDER_TYPE) ).setValue(newBorderType);
    }
    
}
