package hep.aida.web.taglib.jsp20;

import hep.aida.web.taglib.StyleAttributeTag;
import hep.aida.web.taglib.StyleProvider;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;

/**
 * @author The AIDA Team @ SLAC
 *
 */
public class StyleAttributeTagImpl extends SimpleTagSupport implements StyleAttributeTag {
    private String name;

    private String value;

    public void doTag() throws JspException {
        StyleTagImpl styleTag = (StyleTagImpl) findAncestorWithClass(this,
                StyleProvider.class);
        if (styleTag == null) {
            throw new JspException(
                    "a <styleAttribute> tag must be nested inside a <style> tag.");
        }

        styleTag.addAttribute(name, value);
    }

    /*
     * (non-Javadoc)
     * 
     * @see hep.aida.web.taglib.StyleAttributeTag#setName(java.lang.String)
     */
    public void setName(String name) {
        this.name = name;
    }

    /*
     * (non-Javadoc)
     * 
     * @see hep.aida.web.taglib.StyleAttributeTag#setValue(java.lang.String)
     */
    public void setValue(String value) {
        this.value = value;
    }
}