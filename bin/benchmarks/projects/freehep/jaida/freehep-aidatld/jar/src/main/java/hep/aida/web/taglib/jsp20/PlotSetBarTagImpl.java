package hep.aida.web.taglib.jsp20;

import hep.aida.web.taglib.PlotSetBarTag;
import hep.aida.web.taglib.PlotSetBarTagSupport;
import hep.aida.web.taglib.PlotSetStatus;
import hep.aida.web.taglib.util.AidaTLDUtils;

import java.io.IOException;

import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.JspFragment;
import javax.servlet.jsp.tagext.JspTag;
import javax.servlet.jsp.tagext.SimpleTagSupport;

/**
 * This tag is nested in the plotSet tag and servs two goals:
 * 1. Make sure that the body of this tag is processed only once per request.
 * 2. Generate the default Navigation Bar if no body is defined
 *
 * @author The AIDA Team @ SLAC
 */
public class PlotSetBarTagImpl extends SimpleTagSupport implements PlotSetBarTag {
    
    private PlotSetBarTagSupport plotSetBarTagSupport = new PlotSetBarTagSupport();
    
    public void doTag() throws JspException, IOException {
        // If parent tag is PlotSetTag, evaluate this only once
        JspTag parent = this.getParent();
        System.out.println("PlotSetBarTagImpl.doTag :: parent="+parent);
        if (parent instanceof PlotSetTagImpl) {
            String statusVar = ((PlotSetTagImpl) parent).getStatusvar();
            PlotSetStatus status = (PlotSetStatus) AidaTLDUtils.findObject(statusVar, (PageContext) getJspContext());
            if (status.getIndex() > status.getStartindex()) return;
            
            PageContext pageContext = (PageContext) getJspContext();
            
            // Format the links and create the PlotSetBarStatus object
            // with information about links
            plotSetBarTagSupport.doStartTag(pageContext, status);
            
            // Evaluate any nested tags
            JspFragment jspBody = getJspBody();
            if (jspBody != null) {
                jspBody.invoke(pageContext.getOut());
            } else {
                // Generate default Navigation Bar
                plotSetBarTagSupport.doTag(pageContext, status);
            }
        } else {
            throw new JspException(
                    "a <plotSetBar> tag must be nested in a <plotSet> tag");
        }
    }
    
    public void setUrl(String url) {
        plotSetBarTagSupport.setUrl(url);
    }
    
    public void setVar(String var) {
        plotSetBarTagSupport.setVar(var);
    }
    
}