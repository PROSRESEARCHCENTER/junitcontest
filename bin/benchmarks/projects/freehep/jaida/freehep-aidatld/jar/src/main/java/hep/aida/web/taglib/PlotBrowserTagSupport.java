package hep.aida.web.taglib;

import hep.aida.web.taglib.util.AidaTLDUtils;
import hep.aida.web.taglib.util.StyleUtils;
import java.io.IOException;
import java.io.Writer;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * @author The FreeHEP team @ SLAC.
 *
 */
public class PlotBrowserTagSupport implements PlotBrowserTag {
    
    private int maxplots = 9, nplots, nh, nw;
    private String url, layoutVar = "layout", layoutStr = null, id = null;
    
    public void doStartTag() throws JspException {
        if (nplots <= 0) {
            throw new JspException("nplots must be > 0");
        }
        if (url == null || url.length() == 0) {
            throw new JspException("url must not be null");
        }
    }
    
    public void doEndTag(JspContext jspContext) throws JspException, IOException {
        HttpServletRequest request = (HttpServletRequest)((PageContext)jspContext).getRequest();
        if ( AidaTLDUtils.isEmpty(id) )
            id = layoutVar;
        String offsetVariable = id+"offset";
        
        String offsetStr = request.getParameter(offsetVariable);
        int offset = 1;
        if ( offsetStr != null ) {
            int tmpOffset = Integer.valueOf(offsetStr).intValue();
            if ( tmpOffset >= 0 )
                offset = tmpOffset;
        }
        
        if ( !AidaTLDUtils.isEmpty(layoutStr) ) {
            nh = Integer.parseInt(layoutStr.substring(0,layoutStr.indexOf("x")).trim());
            nw = Integer.parseInt(layoutStr.substring(layoutStr.indexOf("x")+1).trim());
            maxplots = nh*nw;
        }
        
        int plotsInPage = nplots-(offset-1)*maxplots > maxplots ? maxplots : nplots-(offset-1)*maxplots;
        if ( plotsInPage > maxplots )
            plotsInPage = maxplots;
        
        if ( nplots > maxplots ) {
            
            double ratio = (double)nplots/(double)maxplots;
            int maxOffset = (int)java.lang.Math.floor(ratio);
            if ( ratio - maxOffset > 0 )
                maxOffset += 1;
            
            int start = ((offset-1)*maxplots)+1;
            int end = (offset*maxplots) > nplots ? nplots : (offset*maxplots);
            Writer writer = jspContext.getOut();
            writer.write(nplots+" plots selected. Showing "+start+" to "+end+". [ ");
            if (offset>1)
                writer.write("<a href=\""+url+"?"+offsetVariable+"=1\">");
            writer.write("First ");
            if (offset>1)
                writer.write("</a>");
            writer.write("/ ");
            int previous = offset-1;
            if (offset>1)
                writer.write("<a href=\""+url+"?"+offsetVariable+"="+previous+"\">");
            writer.write("Previous ");
            if (offset>1)
                writer.write("</a>");
            writer.write("] ");
            
            
            start = offset - 4 > 1 ? offset - 4 : 1;
            end = start+8 > maxOffset ? maxOffset : start+8;
            start = end - 8 > 1 ? end - 8 : 1;
            for ( int i = start; i < end +1; i++ ) {
                if ( offset != i )
                    writer.write("<a href=\""+url+"?"+offsetVariable+"="+i+"\">"+i+" </a>");
                else
                    writer.write("<b>"+i+" </b>");
                if ( i != end )
                    writer.write(", ");
            }
            
            writer.write("[ ");
            int next = offset+1;
            if (offset*maxplots < nplots)
                writer.write("<a href=\""+url+"?"+offsetVariable+"="+next+"\">");
            writer.write("Next ");
            if (offset*maxplots < nplots)
                writer.write("</a>");
            writer.write("/ ");
            if (offset*maxplots < nplots)
                writer.write("<a href=\""+url+"?"+offsetVariable+"="+maxOffset+"\">");
            writer.write("Last ");
            if (offset*maxplots < nplots)
                writer.write("</a>");
            writer.write("] \n");
            
        }
        
        int dataCount = (offset-1)*maxplots;
        int dataCountEnd = (offset-1)*maxplots + plotsInPage > nplots ? nplots-1 : (offset-1)*maxplots + plotsInPage-1;
        PageLayoutHelper layout;
        if ( !AidaTLDUtils.isEmpty(layoutStr) )
            layout = new PageLayoutHelper(plotsInPage, dataCount, dataCountEnd,nh,nw);        
        else
            layout = new PageLayoutHelper(plotsInPage, dataCount, dataCountEnd);        
        jspContext.setAttribute(layoutVar, layout);
    }
    
    public void setMaxplots(int maxplots) {
        if ( maxplots <= 0 )
            throw new RuntimeException("maxplots must be greater than zero.");
        this.maxplots = maxplots;
    }
    
    public void setNplots(int nplots) {
        if ( nplots <= 0 )
            throw new RuntimeException("nplots must be greater than zero.");
        this.nplots = nplots;
    }
    
    public void setUrl(String url) {
        this.url = url;
    }
    
    public void setLayoutVar(String var) {
        this.layoutVar = var;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public void setLayout(String layoutStr) {
        this.layoutStr = layoutStr;
    }
}