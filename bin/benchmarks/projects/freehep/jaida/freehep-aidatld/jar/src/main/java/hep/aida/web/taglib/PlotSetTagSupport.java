package hep.aida.web.taglib;

import hep.aida.IManagedObject;
import hep.aida.web.taglib.util.AidaTLDUtils;
import jas.hist.DataSource;

import java.io.IOException;
import java.io.Writer;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.JspFragment;

/**
 * @author The AIDA Team @ SLAC
 *
 */
public class PlotSetTagSupport extends PlotterTagSupport implements PlotSetTag {
    private Object plots = null;
    private int nPlots = -1;
    private int maxPlots = 9;
    private String id = null;
    private String statusVar = "status";
    private String layout = null;
    
    private PlotSetStatus status = null;
    private String offsetVariable = null;
    private boolean heightIsSet = false;
    private boolean widthIsSet  = false;
    private int cellHeight = 200;
    private int cellWidth  = 300;
    
    /**
     * Do initial checks and configure Plotter and Navigation Bar
     */
    public void doStartTag(PageContext pageContext) throws JspException {
        if (nPlots <= 0 && plots == null) {
            throw new JspException("\"nplots\" or \"plots\" must be defined");
        }
        
        // Get array of plot data (can be null if nplots is set)
        Object[] data = getPlotsObject(pageContext);
        
        // Setup the Status object
        status = (PlotSetStatus) AidaTLDUtils.findObject(statusVar, pageContext);
        if (status == null) {
            status = new PlotSetStatus();
            pageContext.setAttribute(statusVar, status, PageContext.REQUEST_SCOPE);
        }
        status.setPlots(data);
        
        // Format plotter and Navigation Bar
        HttpServletRequest request = (HttpServletRequest)(pageContext).getRequest();
        if ( AidaTLDUtils.isEmpty(id) )
            id = statusVar;
        offsetVariable = id+"_offset";
        String offsetStr = request.getParameter(offsetVariable);
        status.setOffsetvariable(offsetVariable);
        
        formatPlotter(offsetStr);
        
        super.doStartTag(pageContext);
    }
    
    /**
     * Process nested tags (if any) and/or emulate them
     */
    public void doBodyTag(JspFragment jspBody, PageContext pageContext) throws JspException, IOException {
        
        // Evaluate any nested tags
        if (jspBody != null) {
            for (int i=0; i<status.getPlotsinpage(); i++) {
                status.setIndex(status.getStartindex()+i);
                jspBody.invoke(pageContext.getOut());
            }
        }
        
        // If no "region" sub-tag is specified, create simple plotter
        // Here we emulate the "region" and "plot" tags
        if (jspBody == null || (jspBody != null && numberOfRegions() == 0)) {
            for (int i=0; i<status.getPlotsinpage(); i++) {
                status.setIndex(status.getStartindex()+i);
                RegionTagSupport regionTag = new RegionTagSupport();
                regionTag.doStartTag(this, pageContext);
                
                PlotTagSupport plotTag = new PlotTagSupport();
                plotTag.doStartTag(regionTag);
                plotTag.setVar(status.getPlots()[status.getStartindex()+i]);
                plotTag.doEndTag(pageContext);
            }
        }
    }
    

    // PlotSetTag methods
    
    public void setPlots(Object plots) {
        this.plots = plots;
    }
    
    public void setNplots(int nPlots) {
        if ( nPlots <= 0 )
            throw new RuntimeException("nplots must be greater than zero, nplots="+nPlots);
        this.nPlots = nPlots;
    }
    
    public void setMaxplots(int maxPlots) {
        if ( maxPlots <= 0 )
            throw new RuntimeException("maxplots must be greater than zero, maxplots="+maxPlots);
        this.maxPlots = maxPlots;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public void setLayout(String layout) {
        this.layout = layout;
    }
    
    
    // PlotterTag Methods overwrite
   
    public void setStatusvar(String statusVar) {
        this.statusVar = statusVar;
    }
    
    public String getStatusvar() {
        return statusVar;
    }
    
    public void setWidth(int width) {
        widthIsSet = true;
        super.setWidth(width);
    }
    
    public void setHeight(int height) {
        heightIsSet = true;
        super.setHeight(height);
    }
    
    
    // Service methods
    
    private void formatPlotter(String offsetStr) {
        int nx = getNx();
        int ny = getNy();
        
        // Re-set total number of plots to browse, if needed
        Object[] data = status.getPlots();
        int tmpNPlots = nPlots;
        if (tmpNPlots > 0){
            if (data != null && data.length < tmpNPlots) tmpNPlots = data.length;
        } else tmpNPlots = data.length;
        status.setNplots(tmpNPlots);
        
        // Re-set max number of plots per page, if layout is defined
        int index = -1;
        int tmpMaxPots = maxPlots;
        if ( !AidaTLDUtils.isEmpty(layout) ) {
            index = layout.indexOf("x");
            nx = Integer.parseInt(layout.substring(0, index).trim());
            ny = Integer.parseInt(layout.substring(index+1).trim());
            tmpMaxPots = nx*ny;
        }
        status.setMaxplots(tmpMaxPots);
        
        // get input offset
        int offset = 0;
        if ( offsetStr != null ) {
            int tmpOffset = Integer.valueOf(offsetStr).intValue();
            if ( tmpOffset >= 0 )
                offset = tmpOffset;
        }
        status.setStratindex(offset);
        
        int plotsInPage = tmpNPlots - offset > tmpMaxPots ? tmpMaxPots : tmpNPlots - offset;
        if ( plotsInPage > tmpMaxPots )
            plotsInPage = tmpMaxPots;
        status.setPlotsinpage(plotsInPage);
        
        // Default format, if layout is not defined
        if ( index < 0 ) {
            if (plotsInPage < 2) {
                nx=1; ny=1;
            } else if (plotsInPage < 3)  {
                nx=1; ny=2;
            } else if (plotsInPage < 3)  {
                nx=1; ny=2;
            } else if (plotsInPage < 5)  {
                nx=2; ny=2;
            } else if (plotsInPage < 7)  {
                nx=2; ny=3;
            } else if (plotsInPage < 10) {
                nx=3; ny=3;
            } else if (10 <= plotsInPage) {
                nx=3;
                ny=(int) plotsInPage/3;
                if (plotsInPage%3 > 0) ny++;
            }
        }
        setNx(nx);
        setNy(ny);
        
        // If Width of Height is not set, set defaults (for multiple plots only)
        if (plotsInPage > 1) {
            if (!heightIsSet) setHeight(cellHeight*ny);
            if (!widthIsSet) setWidth(cellWidth*nx);
        }
    }
    
    private Object[] getPlotsObject(PageContext pageContext) throws JspException {
        if (plots == null) {
            return null;
        }
        
        Object obj = null;
        if (plots instanceof String) {
            // If we were passed a string, then search all JSP scopes for an
            // Object with the name.
            String attributeName = (String) plots;
            obj = AidaTLDUtils.findObject(attributeName, pageContext);
        } else if (plots != null) {
            obj = plots;
        }
        
        if (obj == null) {
            throw new JspException("nothing to plot (plots Object is not found), plots="+plots);
        }
        
        if (obj instanceof Object[]) {
            return (Object[]) obj;
        } else if (obj instanceof List) {
            return ((List) obj).toArray();
        } else if (obj instanceof IManagedObject || obj instanceof DataSource) {
            return new Object[] { obj };
        }
        return null;
    }
    
}