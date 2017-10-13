package hep.aida.web.taglib;

import hep.aida.IBaseStyle;
import hep.aida.IPlotterRegion;
import hep.aida.web.taglib.util.LogUtils;
import hep.aida.web.taglib.util.PlotUtils;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * The implementation class for all RegionTag classes.
 * 
 * @author The AIDA Team @ SLAC
 *
 */
public class RegionTagSupport implements RegionTag {

    private double x = 0;

    private double y = 0;

    private double width = 1.0;

    private double height = 1.0;

    private int rowSpan = 1;

    private int colSpan = 1;

    private String href = null;

    private String title = null;
    private String var = null, scope = "page";

    private IPlotterRegion plotterRegion;

    private PlotterTagSupport plotterTagSupport;

    PlotterTagSupport getPlotterTagSupport() {
        return plotterTagSupport;
    }

    public void doStartTag(PlotterTagSupport plotterTagSupport, PageContext pageContext)
            throws JspException {
        // Reset per-invocation state.
        plotterRegion = null;

        this.plotterTagSupport = plotterTagSupport;

        if (title != null)
            getPlotterRegion().setTitle(title);
        String outputVar = getVar();
        if ( outputVar != null )
            pageContext.setAttribute(outputVar, getPlotterRegion(), PlotUtils.getScope(scope));
    }

    
    
    public IPlotterRegion getPlotterRegion() {
        if (plotterRegion == null) {
                String message = "create region with x = "+x+", y = "+y+", width = "+width+
                        ", height = "+height+", rowSpan = "+rowSpan+", colSpan = "+colSpan;
            if (LogUtils.log().isDebugEnabled()) {
                LogUtils.log().debug(message);
            }

        System.out.println("RegionTagSupport :: "+message);
        
            plotterRegion = getPlotterTagSupport().createRegion(x, y, width,
                    height, rowSpan, colSpan, this);
        }

        return plotterRegion;
    }

    /*
     * (non-Javadoc)
     * 
     * @see hep.aida.web.taglib.RegionTag#setX(double)
     */
    public void setX(double x) {
        this.x = x;
    }

    public double getX() {
        return x;
    }

    /*
     * (non-Javadoc)
     * 
     * @see hep.aida.web.taglib.RegionTag#setY(double)
     */
    public void setY(double y) {
        this.y = y;
    }

    public double getY() {
        return y;
    }

    /*
     * (non-Javadoc)
     * 
     * @see hep.aida.web.taglib.RegionTag#setWidth(double)
     */
    public void setWidth(double width) {
        this.width = width;
    }

    public double getWidth() {
        return width;
    }

    /*
     * (non-Javadoc)
     * 
     * @see hep.aida.web.taglib.RegionTag#setHeight(double)
     */
    public void setHeight(double height) {
        this.height = height;
    }

    public double getHeight() {
        return height;
    }

    /*
     * (non-Javadoc)
     * 
     * @see hep.aida.web.taglib.RegionTag#setRowSpan(int)
     */
    public void setRowSpan(int rowSpan) {
        this.rowSpan = rowSpan;
    }

    public int getRowSpan() {
        return rowSpan;
    }

    /*
     * (non-Javadoc)
     * 
     * @see hep.aida.web.taglib.RegionTag#setColSpan(int)
     */
    public void setColSpan(int colSpan) {
        this.colSpan = colSpan;
    }

    public int getColSpan() {
        return colSpan;
    }

    /*
     * (non-Javadoc)
     * 
     * @see hep.aida.web.taglib.RegionTag#setHref(java.lang.String)
     */
    public void setHref(String href) {
        this.href = href;
    }

    public String getHref() {
        return href;
    }

    /*
     * (non-Javadoc)
     * 
     * @see hep.aida.web.taglib.RegionTag#setTitle(java.lang.String)
     */
    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setVar(String var) {
        this.var = var;
    }

    public String getVar() {
        return var;
    }

    public IBaseStyle getStyle() throws JspException {
        IBaseStyle style = getPlotterRegion().style();
        LogUtils.log().debug(style);
        return style;
    }

    public IBaseStyle getStyle(String type) throws JspException {
        throw new JspException(
                "If you see this you have a logic error: type = " + type);
    }

    public void setScope(String scope) {
        this.scope = scope;
    }
    
    
}