package hep.aida.web.taglib.jsp20;

import hep.aida.web.taglib.PlotBrowserTag;
import hep.aida.web.taglib.PlotBrowserTagSupport;
import java.io.IOException;
import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

/**
 * @author The FreeHEP team @ SLAC.
 *
 */
public class PlotBrowserTagImpl extends SimpleTagSupport implements PlotBrowserTag {
    
    private PlotBrowserTagSupport plotBrowserTagSupport = new PlotBrowserTagSupport();
    
    public void doTag() throws JspException, IOException {
        plotBrowserTagSupport.doStartTag();
        JspContext jspContext = getJspContext();
        plotBrowserTagSupport.doEndTag((PageContext) jspContext);
    }

    public void setMaxplots(int maxplots) {
        plotBrowserTagSupport.setMaxplots(maxplots);
    }
    
    public void setNplots(int nplots) {
        plotBrowserTagSupport.setNplots(nplots);
    }
    
    public void setUrl(String url) {
        plotBrowserTagSupport.setUrl(url);
    }
    
    public void setLayoutVar(String var) {
        plotBrowserTagSupport.setLayoutVar(var);
    }
    
    public void setId(String id) {
        plotBrowserTagSupport.setId(id);
    }
    
    public void setLayout(String layoutStr) {
        plotBrowserTagSupport.setLayout(layoutStr);
    }
}