package hep.aida.ref.plotter;

import hep.aida.IPlotterLayout;

/**
 *
 * @author The FreeHEP team @ SLAC.
 *
 */
public class PlotterLayout implements IPlotterLayout {
    
    private String[] availableParameters;
    
    public String[] availableParameters() {
        return availableParameters;
    }    
    
    public double parameterValue(String str) {
        return Double.NaN;
    }
    
    public void reset() {
    }
    
    public boolean setParameter(String str, double param) {
        return false;
    }
    
}
