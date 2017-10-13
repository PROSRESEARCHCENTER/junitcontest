package hep.aida.web.taglib;

/**
 * A plot region within a <code>&lt;plotter&gt;</code>.
 * 
 * @author The AIDA Team @ SLAC
 *
 */
public interface RegionTag extends StyleProvider {

    /**
     * Set the relative horizontal position of the region within the plotter (a
     * number between 0 and 1).
     * 
     * @param x
     *            relative x position of the region within the plotter
     */
    public void setX(double x);

    /**
     * Set the relative vertical position of the region within the plotter (a
     * number between 0 and 1).
     * 
     * @param y
     *            relative y position of the region within the plotter
     */
    public void setY(double y);

    /**
     * Set the relative width of the region within the plotter (a number between
     * 0 and 1).
     * 
     * @param width
     *            relative width of the region within the plotter
     */
    public void setWidth(double width);

    /**
     * Set the relative height of the region within the plotter (a number
     * between 0 and 1).
     * 
     * @param height
     *            relative height of the region within the plotter
     */
    public void setHeight(double height);

    /**
     * Set the number of horizontal cells spanned in the region grid.
     * 
     * @param rowSpan
     *            number of horizontal cells spanned in the region grid
     */
    public void setRowSpan(int rowSpan);

    /**
     * Set the number of vertical cells spanned in the region grid.
     * 
     * @param colSpan
     *            number of vertical cells spanned in the region grid
     */
    public void setColSpan(int colSpan);

    /**
     * Specify an href to the region. If the plotter has an image map and the
     * region regerence is specified, then it is clickable.
     *  
     */
    public void setHref(String href);

    /**
     * Set the title of the plot in the current region.
     *  
     */
    public void setTitle(String title);
    
    public void setVar(String var);
}