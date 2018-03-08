package hep.aida.web.taglib.jsp20;

import hep.aida.IBaseStyle;
import hep.aida.web.taglib.PlotSetStatus;
import hep.aida.web.taglib.PlotSetTag;
import hep.aida.web.taglib.StyleProvider;
import hep.aida.web.taglib.StyleTag;
import hep.aida.web.taglib.StyleTagSupport;
import hep.aida.web.taglib.util.AidaTLDUtils;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.JspFragment;
import javax.servlet.jsp.tagext.JspTag;
import javax.servlet.jsp.tagext.SimpleTagSupport;

/**
 * @author The AIDA Team @ SLAC
 *
 */
public class StyleTagImpl extends SimpleTagSupport implements StyleTag {

    private StyleTagSupport styleTagSupport = new StyleTagSupport();

    public StyleTagSupport getStyleTagSupport() {
        return styleTagSupport;
    }
    
    public void doTag() throws JspException, IOException {
        // If parent tag is PlotSetTag, evaluate this only once
        JspTag parent = this.getParent();
        if (parent instanceof PlotSetTagImpl) {
            String statusVar = ((PlotSetTagImpl) parent).getStatusvar();
            PlotSetStatus status = (PlotSetStatus) AidaTLDUtils.findObject(statusVar, (PageContext) getJspContext());
            if (status != null) {
                if (status.getIndex() > status.getStartindex()) return;
            }
        }
        
        StyleProvider styleProvider = (StyleProvider) findAncestorWithClass(
                this, StyleProvider.class);
        if (styleProvider == null) {
            throw new JspException(
                    "a <style> tag must be surrounded by a StyleProvider.");
        }

        styleTagSupport.doStartTag(styleProvider);

        // Evaluate any nested tags.
        JspFragment jspBody = getJspBody();
        if (jspBody != null) {
            jspBody.invoke(getJspContext().getOut());
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see hep.aida.web.taglib.StyleTag#setType(java.lang.String)
     */
    public void setType(String type) {
        styleTagSupport.setType(type);
    }

    /**
     * Called by subordinate {@link StyleAttributeTag}tags.
     * 
     * @param name
     *            The name of the AIDA style attibute
     * @param value
     *            The value of the AIDA style attibute
     */
    void addAttribute(String name, String value) throws JspException {
        styleTagSupport.addAttribute(name, value);
    }

    /*
     * (non-Javadoc)
     * 
     * @see hep.aida.web.taglib.util.StyleProvider#getBaseStyle()
     */
    public IBaseStyle getStyle() throws JspException {
        return styleTagSupport.getStyle();
    }

    /*
     * (non-Javadoc)
     * 
     * @see hep.aida.web.taglib.util.StyleProvider#getBaseStyle(java.lang.String)
     */
    public IBaseStyle getStyle(String type) throws JspException {
        return styleTagSupport.getStyle(type);
    }
}