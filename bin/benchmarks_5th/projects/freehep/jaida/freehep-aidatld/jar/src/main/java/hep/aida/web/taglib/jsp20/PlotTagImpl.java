package hep.aida.web.taglib.jsp20;

import hep.aida.IBaseStyle;
import hep.aida.web.taglib.PlotTag;
import hep.aida.web.taglib.PlotTagSupport;

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
public class PlotTagImpl extends SimpleTagSupport implements PlotTag {

    private PlotTagSupport plotTagSupport = new PlotTagSupport();

    public PlotTagSupport getPlotTagSupport() {
        return plotTagSupport;
    }
    
    public void doTag() throws JspException, IOException {
        RegionTagImpl regionTag = (RegionTagImpl) findAncestorWithClass(this,
                RegionTagImpl.class);
        if (regionTag == null) {
            throw new JspException(
                    "a <plot> tag must be nested in a <region> tag");
        }

        plotTagSupport.doStartTag(regionTag.getRegionTagSupport());

        // Evaluate any nested tags.
        JspFragment jspBody = getJspBody();
        JspContext jspContext = getJspContext();
        if (jspBody != null) {
            jspBody.invoke(jspContext.getOut());
        }

        plotTagSupport.doEndTag((PageContext) jspContext);
    }

    /*
     * (non-Javadoc)
     * 
     * @see hep.aida.web.taglib.PlotTag#setPlotObject(java.lang.Object)
     */
    public void setVar(Object var) {
        plotTagSupport.setVar(var);
    }

    /*
     * (non-Javadoc)
     * 
     * @see hep.aida.web.taglib.util.StyleProvider#getStyle()
     */
    public IBaseStyle getStyle() throws JspException {
        return plotTagSupport.getStyle();
    }

    /*
     * (non-Javadoc)
     * 
     * @see hep.aida.web.taglib.util.StyleProvider#getStyle(java.lang.String)
     */
    public IBaseStyle getStyle(String type) throws JspException {
        return plotTagSupport.getStyle(type);
    }
}