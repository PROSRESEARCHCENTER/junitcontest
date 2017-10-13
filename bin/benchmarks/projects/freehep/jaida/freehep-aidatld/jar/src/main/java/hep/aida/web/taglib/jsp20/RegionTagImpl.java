package hep.aida.web.taglib.jsp20;

import hep.aida.IBaseStyle;
import hep.aida.IPlotterRegion;
import hep.aida.web.taglib.RegionTag;
import hep.aida.web.taglib.RegionTagSupport;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.JspFragment;
import javax.servlet.jsp.tagext.SimpleTagSupport;

/**
 * @author The AIDA Team @ SLAC
 *
 */
public class RegionTagImpl extends SimpleTagSupport implements RegionTag {
    
    private RegionTagSupport regionTagSupport = new RegionTagSupport();
    
    RegionTagSupport getRegionTagSupport() {
        return regionTagSupport;
    }
    
    public void doTag() throws JspException, IOException {
        PageContext pageContext = (PageContext) getJspContext();
        PlotterTagImpl plotterTag = (PlotterTagImpl) findAncestorWithClass(
                this, PlotterTagImpl.class);
        if (plotterTag == null) {
            throw new JspException(
                    "a <region> tag must be nested in a <plotter> tag");
        }
        
        // The RegionTagSupport needs to call back on the PlotterTagSupport in
        // order to create an IPlotterRegion. This is because nested <plot> tags
        // within a <region> tag need access to the <region> so that they can
        // render their plot in it.
        regionTagSupport.doStartTag(plotterTag.getPlotterTagSupport(), pageContext);
        
        // Evaluate any nested tags.
        JspFragment jspBody = getJspBody();
        if (jspBody != null) {
            jspBody.invoke(getJspContext().getOut());
        }
    }
    
    /**
     * Create and return an AIDA plotter region from the IPlotter managed by our
     * parent ELPlotterTagImpl.
     *
     * @return An AIDA plotter region
     */
    IPlotterRegion getPlotterRegion() {
        return regionTagSupport.getPlotterRegion();
    }
    
    /*
     * (non-Javadoc)
     *
     * @see hep.aida.web.taglib.RegionTag#setX(double)
     */
    public void setX(double x) {
        regionTagSupport.setX(x);
    }
    
    /*
     * (non-Javadoc)
     *
     * @see hep.aida.web.taglib.RegionTag#setY(double)
     */
    public void setY(double y) {
        regionTagSupport.setY(y);
    }
    
    /*
     * (non-Javadoc)
     *
     * @see hep.aida.web.taglib.RegionTag#setWidth(double)
     */
    public void setWidth(double width) {
        regionTagSupport.setWidth(width);
    }
    
    /*
     * (non-Javadoc)
     *
     * @see hep.aida.web.taglib.RegionTag#setHeight(double)
     */
    public void setHeight(double height) {
        regionTagSupport.setHeight(height);
    }
    
    /*
     * (non-Javadoc)
     *
     * @see hep.aida.web.taglib.RegionTag#setRowSpan(int)
     */
    public void setRowSpan(int rowSpan) {
        regionTagSupport.setRowSpan(rowSpan);
    }
    
    /*
     * (non-Javadoc)
     *
     * @see hep.aida.web.taglib.RegionTag#setColSpan(int)
     */
    public void setColSpan(int colSpan) {
        regionTagSupport.setColSpan(colSpan);
    }
    
    /*
     * (non-Javadoc)
     *
     * @see hep.aida.web.taglib.util.StyleProvider#getStyle()
     */
    public IBaseStyle getStyle() throws JspException {
        return regionTagSupport.getStyle();
    }
    
    /*
     * (non-Javadoc)
     *
     * @see hep.aida.web.taglib.util.StyleProvider#getStyle(java.lang.String)
     */
    public IBaseStyle getStyle(String type) throws JspException {
        return regionTagSupport.getStyle(type);
    }
    
    /*
     * (non-Javadoc)
     *
     * @see hep.aida.web.taglib.RegionTag#setHref(java.lang.String)
     */
    public void setHref(String href) {
        regionTagSupport.setHref(href);
    }
    
    public String getHref() {
        return regionTagSupport.getHref();
    }
    
    /*
     * (non-Javadoc)
     *
     * @see hep.aida.web.taglib.RegionTag#setTitle(java.lang.String)
     */
    public void setTitle(String title) {
        regionTagSupport.setTitle(title);
    }
    
    public String getTitle() {
        return regionTagSupport.getTitle();
    }
    
    public void setVar(String var) {
        regionTagSupport.setVar(var);
    }
    
    public String getVar() {
        return regionTagSupport.getVar();
    }
    
    public void setScope(String scope) {
        regionTagSupport.setScope(scope);
    }
    
}