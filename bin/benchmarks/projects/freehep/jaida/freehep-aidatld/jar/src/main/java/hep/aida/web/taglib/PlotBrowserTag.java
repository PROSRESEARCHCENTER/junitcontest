package hep.aida.web.taglib;

import java.io.IOException;
import java.io.Writer;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

/**
 * @author The FreeHEP team @ SLAC.
 *
 */
public interface PlotBrowserTag {
    
    public void setMaxplots(int maxplots);
    public void setNplots(int nplots);
    public void setUrl(String url);
    public void setLayoutVar(String var);
    public void setId(String id);
    public void setLayout(String layoutStr);

}