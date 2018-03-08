package hep.aida.web.taglib;

import hep.aida.IPlotter;

/**
 * A top level tag which generates an image containing one or more plots.
 *
 * @author The AIDA Team @ SLAC
 *
 */
public interface PlotSetTag extends PlotterTag {
    
    /**
     * Set objects to plot. Can be an array of plottable objects,
     * a single plottable object, or a String name that refferes to
     * such object.
     * It is required if "nplots" attribute is not set.
     */
    public void setPlots(Object plots);
    
    /**
     * Total number of plots to browse.
     * It is required if "plots" attribute is not set.
     */
    public void setNplots(int nPlots);
    
    /**
     * The maximum number of plots in a page.
     */
    public void setMaxplots(int maxplots);
    
    /**
     * Set the name of the variable that is going to keep the
     * information about the current status of the PlotSetTag
     * processing.
     */
    public void setStatusvar(String var);
    
    /**
     * The id for the browsing when multiple plotSet tags
     * are present in a page.
     */
    public void setId(String id);
    
    /**
     * The layout of the plot page. Must be in a form of "nHxnW",
     * "3x4" means 3 plots in Height direction, 4 plots in Width direction.
     */
    public void setLayout(String layout);
    
}