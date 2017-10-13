package hep.aida.ref.plotter.style.registry;

/**
 * This object encapsulates information about relevant
 * IPlotterRegion, object, and actions.
 * It is used for obtaining implicit IPlotterStyle
 */

import java.util.Map;

public interface IPlotterState {

    static String ATTRIBUTE_KEY_PREFIX = "IPlotterState";
            
    Object getObject();    
    String getObjectPath();
    
    int getOverlayIndex();
    int getOverlayTotal();
    
    int getRegionIndex();
    int getRegionTotal();
    
    String getAttribute(String key);
    
    Map getAttributes();    
    
    void clear();
}
