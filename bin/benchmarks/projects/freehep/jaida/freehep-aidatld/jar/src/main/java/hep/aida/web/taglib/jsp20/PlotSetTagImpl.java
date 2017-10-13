package hep.aida.web.taglib.jsp20;

import hep.aida.web.taglib.PlotSetTag;
import hep.aida.web.taglib.PlotSetTagSupport;

import java.io.IOException;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.JspFragment;

/**
 * @author The AIDA Team @ SLAC
 *
 */
public class PlotSetTagImpl extends PlotterTagImpl implements PlotSetTag {
    
    public PlotSetTagImpl() {
        super();
        plotterTagSupport = new PlotSetTagSupport();
    }
    
    public void doTag() throws JspException, IOException {
        PageContext pageContext = (PageContext) getJspContext();
        
        // Do initial checks and configure Plotter and Navigation Bar
        plotterTagSupport.doStartTag(pageContext);
        
        // Evaluate any nested tags
        JspFragment jspBody = getJspBody();
        ((PlotSetTagSupport) plotterTagSupport).doBodyTag(jspBody, pageContext);
        
        // Finish - create image and generate html
        plotterTagSupport.doEndTag(pageContext);
    }
    
    
    // PlotSetTag methods
    
    public void setPlots(Object plots) {
        ((PlotSetTagSupport) plotterTagSupport).setPlots(plots);
    }
    
    public void setNplots(int nPlots) {
        ((PlotSetTagSupport) plotterTagSupport).setNplots(nPlots);
    }
    
    public void setMaxplots(int maxPlots) {
        ((PlotSetTagSupport) plotterTagSupport).setMaxplots(maxPlots);
    }
    
    public void setStatusvar(String var) {
        ((PlotSetTagSupport) plotterTagSupport).setStatusvar(var);
    }
    public String getStatusvar() {
        return ((PlotSetTagSupport) plotterTagSupport).getStatusvar();
    }
    
    public void setId(String id) {
        ((PlotSetTagSupport) plotterTagSupport).setId(id);
    }
    
    public void setLayout(String layout) {
        ((PlotSetTagSupport) plotterTagSupport).setLayout(layout);
    }
    
}