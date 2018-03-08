package hep.aida.web.taglib.jsp20;

import hep.aida.web.taglib.ManagedObjectsTag;
import hep.aida.web.taglib.ManagedObjectsTagSupport;
import java.io.IOException;
import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

/**
 * @author The AIDA Team @ SLAC
 *
 */
public class ManagedObjectsTagImpl extends SimpleTagSupport implements ManagedObjectsTag {

    private ManagedObjectsTagSupport managedObjectsTagSupport = new ManagedObjectsTagSupport();

    public void doTag() throws JspException, IOException {
        managedObjectsTagSupport.doStartTag();
        JspContext jspContext = getJspContext();
        managedObjectsTagSupport.doEndTag((PageContext) jspContext);
    }

    public void setStoreName(String storeName) {
        managedObjectsTagSupport.setStoreName(storeName);
    }
    
    public void setVar(String var) {
        managedObjectsTagSupport.setVar(var);
    }

    public void setScope(String scope) {
        managedObjectsTagSupport.setScope(scope);
    }

    public void setPath(String path) {
        managedObjectsTagSupport.setPath(path);
    }

}