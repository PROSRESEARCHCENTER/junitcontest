package hep.aida.web.taglib.jsp20;

import hep.aida.IBaseStyle;
import hep.aida.IPlotterRegion;
import hep.aida.web.taglib.PlotterTag;
import hep.aida.web.taglib.PlotterTagSupport;
import hep.aida.web.taglib.RegionTagSupport;

import java.io.IOException;

import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.JspFragment;
import javax.servlet.jsp.tagext.SimpleTagSupport;

/**
 * @author The AIDA Team @ SLAC
 *
 */
public class PlotterTagImpl extends SimpleTagSupport implements PlotterTag {

    protected PlotterTagSupport plotterTagSupport = new PlotterTagSupport();

    PlotterTagSupport getPlotterTagSupport() {
        return plotterTagSupport;
    }

    public void doTag() throws JspException, IOException {
        JspContext jspContext = getJspContext();

        plotterTagSupport.doStartTag((PageContext) jspContext);

        // Evaluate any nested tags.
        JspFragment jspBody = getJspBody();
        if (jspBody != null) {
            jspBody.invoke(jspContext.getOut());
        }

        plotterTagSupport.doEndTag((PageContext) jspContext);
    }

    IPlotterRegion createRegion(double x, double y, double width,
            double height, int rowSpan, int colSpan,
            RegionTagSupport regionTagSupport) {
        return plotterTagSupport.createRegion(x, y, width, height, rowSpan,
                colSpan, regionTagSupport);
    }

    /*
     * (non-Javadoc)
     * 
     * @see hep.aida.web.taglib.PlotterTag#setName(java.lang.String)
     */
    public void setName(String name) {
        plotterTagSupport.setName(name);
    }

    /*
     * (non-Javadoc)
     * 
     * @see hep.aida.web.taglib.PlotterTag#setWidth(int)
     */
    public void setWidth(int width) {
        plotterTagSupport.setWidth(width);
    }

    /*
     * (non-Javadoc)
     * 
     * @see hep.aida.web.taglib.PlotterTag#setHeight(int)
     */
    public void setHeight(int height) {
        plotterTagSupport.setHeight(height);
    }

    /*
     * (non-Javadoc)
     * 
     * @see hep.aida.web.taglib.PlotterTag#setNx(int)
     */
    public void setNx(int nx) {
        plotterTagSupport.setNx(nx);
    }

    /*
     * (non-Javadoc)
     * 
     * @see hep.aida.web.taglib.PlotterTag#setNy(int)
     */
    public void setNy(int ny) {
        plotterTagSupport.setNy(ny);
    }

    /*
     * (non-Javadoc)
     * 
     * @see hep.aida.web.taglib.PlotterTag#setFormat(java.lang.String)
     */
    public void setFormat(String format) {
        plotterTagSupport.setFormat(format);
    }

    /*
     * (non-Javadoc)
     * 
     * @see hep.aida.web.taglib.PlotterTag#setAllowDownload(boolean)
     */
    public void setAllowDownload(boolean allowDownload) {
        plotterTagSupport.setAllowDownload(allowDownload);
    }

    /*
     * (non-Javadoc)
     * 
     * @see hep.aida.web.taglib.PlotterTag#setPlotter(java.lang.Object)
     */
    public void setVar(Object plotter) {
        plotterTagSupport.setVar(plotter);
    }

    /*
     * (non-Javadoc)
     * 
     * @see hep.aida.web.taglib.util.StyleProvider#getStyle()
     */
    public IBaseStyle getStyle() throws JspException {
        return plotterTagSupport.getStyle();
    }

    /*
     * (non-Javadoc)
     * 
     * @see hep.aida.web.taglib.util.StyleProvider#getStyle(java.lang.String)
     */
    public IBaseStyle getStyle(String type) throws JspException {
        return plotterTagSupport.getStyle(type);
    }

    /*
     * (non-Javadoc)
     * 
     * @see hep.aida.web.taglib.PlotterTagSupport#setCreateImageMap(boolean)
     */
    public void setCreateImageMap(boolean createImageMap) {
        plotterTagSupport.setCreateImageMap(createImageMap);
    }

    public boolean getCreateImageMap() {
        return plotterTagSupport.getCreateImageMap();
    }
}