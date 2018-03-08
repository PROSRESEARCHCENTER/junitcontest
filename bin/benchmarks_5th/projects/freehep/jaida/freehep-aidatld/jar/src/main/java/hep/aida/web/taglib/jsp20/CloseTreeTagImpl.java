package hep.aida.web.taglib.jsp20;

import hep.aida.web.taglib.CloseTreeTag;
import hep.aida.web.taglib.CloseTreeTagSupport;
import java.io.IOException;
import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

/**
 * @author The AIDA Team @ SLAC
 *
 */
public class CloseTreeTagImpl extends SimpleTagSupport implements CloseTreeTag {

    private CloseTreeTagSupport closeTreeTagSupport = new CloseTreeTagSupport();

    public CloseTreeTagSupport getCloseTreeTagSupport() {
        return closeTreeTagSupport;
    }
    
    public void doTag() throws JspException, IOException {
        closeTreeTagSupport.doStartTag();
        JspContext jspContext = getJspContext();
        closeTreeTagSupport.doEndTag((PageContext) jspContext);
    }


    /*
     * (non-Javadoc)
     * 
     * @see hep.aida.web.taglib.TreeTag#setStoreName(java.lang.String)
     */
    public void setStoreName(String storeName) {
        closeTreeTagSupport.setStoreName(storeName);
    }
    
}