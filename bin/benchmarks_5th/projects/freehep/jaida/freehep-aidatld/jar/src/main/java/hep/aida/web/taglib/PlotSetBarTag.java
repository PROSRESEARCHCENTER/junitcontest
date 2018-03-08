package hep.aida.web.taglib;

import hep.aida.IPlotter;

/**
 * A top level tag which generates an image containing one or more plots.
 *
 * @author The AIDA Team @ SLAC
 *
 */
public interface PlotSetBarTag {
    
    /**
     * The page to link the navigation bar to.
     */
    public void setUrl(String url);
        
    /**
     * Variable where the current Navigation Bar links are stored.
     */
    public void setVar(String var);
        
}