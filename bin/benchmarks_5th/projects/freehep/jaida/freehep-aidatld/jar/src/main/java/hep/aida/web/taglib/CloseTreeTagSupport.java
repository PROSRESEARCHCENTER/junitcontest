package hep.aida.web.taglib;

import hep.aida.web.taglib.util.TreeUtils;
import java.io.IOException;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * The implementation class for all CloseTreeTag classes.
 * 
 * @author The AIDA Team @ SLAC
 *
 */
public class CloseTreeTagSupport implements CloseTreeTag {

    private String storeName;

    public void doStartTag() throws JspException {
        if (storeName == null || storeName.length() == 0) {
            throw new JspException("var must not be null");
        }
    }

    public void doEndTag(PageContext pageContext) throws JspException {
        try {
            TreeUtils.closeTree(getStoreName(), pageContext.getSession().getId());
        } catch (IOException e) {
            throw new JspException(e);
        }
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getStoreName() {
        return storeName;
    }

}