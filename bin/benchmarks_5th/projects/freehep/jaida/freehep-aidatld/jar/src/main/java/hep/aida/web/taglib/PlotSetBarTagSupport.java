package hep.aida.web.taglib;

import hep.aida.web.taglib.util.AidaTLDUtils;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * The implementation class for plotSetBar tag.
 *
 * @author The AIDA Team @ SLAC
 *
 */
public class PlotSetBarTagSupport implements PlotSetBarTag {
    private String url = null;
    private String var = "href";
    
    /**
     * Format the links and create the PlotSetBarStatus object
     * with information about links.
     */
    public void doStartTag(PageContext pageContext, PlotSetStatus status)  throws JspException, IOException {
        String offsetVariable = status.getOffsetvariable();
        int start = status.getStartindex();
        int end = start + status.getPlotsinpage();
        int totalPlots = status.getNplots();
        int maxPagePlots = status.getMaxplots();
        
        String sep = (url.indexOf("?") >= 0) ? "&" : "?";
        
        int nPages      = (int) (totalPlots/maxPagePlots + 1);
        String[] pages  = null;
        String first    = "";
        String previous = "";
        String next     = "";
        String last     = "";
        
        if ( totalPlots > maxPagePlots ) {
            pages = new String[nPages];
            int maxOffset = totalPlots - (totalPlots%maxPagePlots);
            
            if (start > 0) first = url+sep+offsetVariable+"=0";

            int prev = start - maxPagePlots;
            if (start >= maxPagePlots) previous = url+sep+offsetVariable+"="+prev;
            
            int page = 1;
            for ( int i = 0; i < totalPlots; i=i+maxPagePlots ) {
                if ( start != i ) pages[page-1] = url+sep+offsetVariable+"="+i;
                else pages[page-1] = "";
                page++;
            }
            
            if (end <= maxOffset) next = url+sep+offsetVariable+"="+end;
            if (start < maxOffset) last = url+sep+offsetVariable+"="+maxOffset;
        }

        // Setup the Status object
        PlotSetBarStatus barStatus = (PlotSetBarStatus) AidaTLDUtils.findObject(var, pageContext);
        if (barStatus == null) {
            barStatus = new PlotSetBarStatus();
            pageContext.setAttribute(var, barStatus, PageContext.REQUEST_SCOPE);
        }
        barStatus.setPages(pages);
        barStatus.setFirst(first);
        barStatus.setPrevious(previous);
        barStatus.setNext(next);
        barStatus.setLast(last);
        
        String barString = createDefaultBar(pageContext, status);
        barStatus.setDefaultbar(barString);
    }    
    
    /**
     * Write out HTML code for the Navigation Bar
     */
    public void doTag(PageContext pageContext, PlotSetStatus status)  throws JspException, IOException {
        PlotSetBarStatus barStatus = (PlotSetBarStatus) AidaTLDUtils.findObject(var, pageContext);

        Writer writer = pageContext.getOut();
        writer.write(barStatus.getDefaultbar());
    }
    
    /**
     * Create HTML code for the Navigation Bar
     */
    public String createDefaultBar(PageContext pageContext, PlotSetStatus status)  throws JspException, IOException {
        String bar = "";
        
        PlotSetBarStatus barStatus = (PlotSetBarStatus) AidaTLDUtils.findObject(var, pageContext);
        int start = status.getStartindex();
        int end = start + status.getPlotsinpage();
        int totalPlots = status.getNplots();

        if ( barStatus.getNpages() > 0 ) {            
            StringWriter writer = new StringWriter(300);
            writer.write(totalPlots+" plots selected. Showing "+(start+1)+" to "+end+". [ ");
            if (!AidaTLDUtils.isEmpty(barStatus.getFirst()))
                writer.write("<a href=\""+barStatus.getFirst()+"\">");
            writer.write("First");
            if (!AidaTLDUtils.isEmpty(barStatus.getFirst()))
                writer.write("</a> ");
            writer.write("/ ");
            
            if (!AidaTLDUtils.isEmpty(barStatus.getPrevious()))
                writer.write("<a href=\""+barStatus.getPrevious()+"\">");
            writer.write("Previous ");
            if (!AidaTLDUtils.isEmpty(barStatus.getPrevious()))
                writer.write("</a>");
            writer.write("] &nbsp;");
            
            for ( int i = 0; i < barStatus.getNpages(); i++ ) {
                String pageHref = barStatus.getPages()[i];
                if (!AidaTLDUtils.isEmpty(pageHref))
                    writer.write("<a href=\""+pageHref+"\">"+(i+1)+" </a>");
                else
                    writer.write("<font size=+1><b>"+(i+1)+" </b></font>");
                
                if (i+1 < barStatus.getNpages())
                    writer.write(", ");
                else
                    writer.write("&nbsp; ");
            }
            
            writer.write("[ ");
            if (!AidaTLDUtils.isEmpty(barStatus.getNext()))
                writer.write("<a href=\""+barStatus.getNext()+"\">");
            writer.write("Next ");
            if (!AidaTLDUtils.isEmpty(barStatus.getNext()))
                writer.write("</a>");
            writer.write("/ ");
            
            if (!AidaTLDUtils.isEmpty(barStatus.getLast()))
                writer.write("<a href=\""+barStatus.getLast()+"\">");
            writer.write("Last ");
            if (!AidaTLDUtils.isEmpty(barStatus.getLast()))
                writer.write("</a>");
            writer.write("] \n");
            
            bar = writer.toString();
            writer.close();
        }
        return bar;
    }
    
    public void setUrl(String url) {
        this.url = url;
    }
    
    public void setVar(String var) {
        this.var = var;
    }
    
    
}
