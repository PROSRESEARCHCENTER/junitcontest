package hep.aida.web.taglib.jsp20;

import hep.aida.IManagedObject;
import hep.aida.ITree;
import hep.aida.web.taglib.TreeTag;
import hep.aida.web.taglib.TreeTagSupport;

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
public class TreeTagImpl extends SimpleTagSupport implements TreeTag {

    private TreeTagSupport treeTagSupport = new TreeTagSupport();

    public void doTag() throws JspException, IOException {
        JspContext jspContext = getJspContext();

        treeTagSupport.doStartTag();

        // Evaluate any nested tags.
        JspFragment jspBody = getJspBody();
        if (jspBody != null) {
            jspBody.invoke(jspContext.getOut());
        }

        treeTagSupport.doEndTag((PageContext) jspContext);
    }

    /*
     * (non-Javadoc)
     * 
     * @see hep.aida.web.taglib.TreeTag#setStoreName(java.lang.String)
     */
    public void setStoreName(String storeName) {
        treeTagSupport.setStoreName(storeName);
    }

    /*
     * (non-Javadoc)
     * 
     * @see hep.aida.web.taglib.TreeTag#setStoreType(java.lang.String)
     */
    public void setStoreType(String storeType) {
        treeTagSupport.setStoreType(storeType);
    }

    /*
     * (non-Javadoc)
     * 
     * @see hep.aida.web.taglib.TreeTag#setOptions(java.lang.String)
     */
    public void setOptions(String options) {
        treeTagSupport.setOptions(options);
    }
    
}