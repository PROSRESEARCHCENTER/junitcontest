package hep.aida.web.taglib.jsp20;

import hep.aida.ITree;
import hep.aida.web.taglib.DisplayTreeTag;
import hep.aida.web.taglib.DisplayTreeTagSupport;
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
public class DisplayTreeTagImpl extends SimpleTagSupport implements DisplayTreeTag {

    private DisplayTreeTagSupport displayTreeTagSupport = new DisplayTreeTagSupport();

    public void doTag() throws JspException, IOException {
        JspContext jspContext = getJspContext();

        displayTreeTagSupport.doStartTag();

        // Evaluate any nested tags.
        JspFragment jspBody = getJspBody();
        if (jspBody != null) {
            jspBody.invoke(jspContext.getOut());
        }

        displayTreeTagSupport.doEndTag((PageContext) jspContext);
    }

    public void setStoreName(String storeName) {
        displayTreeTagSupport.setStoreName(storeName);
    }
    
    public void setLeafHref(String leafHref) {
        displayTreeTagSupport.setLeafHref(leafHref);
    }
    
    public void setFolderHref(String folderHref) {
        displayTreeTagSupport.setFolderHref(folderHref);
    }
    
    public void setRootVisible(boolean isRootVisible) {
        displayTreeTagSupport.setRootVisible(isRootVisible);
    }

    public void setRootLabel(String rootLabel) {
        displayTreeTagSupport.setRootLabel(rootLabel);
    }
    
    public void setShowItemCount(boolean showItemCount) {
        displayTreeTagSupport.setShowItemCount(showItemCount);
    }

    public void setShowFolderHrefForNodesWithLeavesOnly(boolean show) {
        displayTreeTagSupport.setShowFolderHrefForNodesWithLeavesOnly(show);
    }

}