package hep.aida.ref.plotter;

import hep.aida.IGridStyle;

/**
 *
 * @author The FreeHEP team @ SLAC
 */
public class GridStyle extends LineStyle implements IGridStyle {
    
    protected void initializeBaseStyle() {
        super.initializeBaseStyle();
        addParameter( new DoubleStyleParameter(Style.GRID_SIZE, Double.NaN) );
        String[] units = {"pixels", "percent", "dataValue" };
        addParameter( new IntegerStyleParameter( Style.GRID_UNITS, 0, units) );
    }
    
    public double cellSize() {
        return ( (DoubleStyleParameter) deepestSetParameter(Style.GRID_SIZE) ).value();
    }

    public boolean setCellSize(double size) {
        return ( (DoubleStyleParameter) parameter(Style.GRID_SIZE) ).setValue(size);
    }

    public boolean setUnits(int unitsDefinition) {
        return ( (IntegerStyleParameter) parameter(Style.GRID_UNITS) ).setValue(unitsDefinition);
    }

    public int units() {
        return ( (IntegerStyleParameter) deepestSetParameter(Style.GRID_UNITS) ).value();
    }
    
    
}
