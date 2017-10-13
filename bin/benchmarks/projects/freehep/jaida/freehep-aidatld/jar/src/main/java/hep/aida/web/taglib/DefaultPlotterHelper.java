package hep.aida.web.taglib;

import hep.aida.web.taglib.util.PlotterHelper;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;

/**
 *
 * @author The FreeHEP team @ SLAC.
 */
public class DefaultPlotterHelper implements PlotterHelper {
    
    public PlotterRegistry getPlotterRegistry(HttpServletRequest req) {
        return (PlotterRegistry) req.getSession().getAttribute(PlotterRegistry.REGISTRY_SESSION_NAME);
    }    
    
    public PlotterRegistry getPlotterRegistry(PageContext pageContext) {
        Object obj = pageContext.getAttribute(PlotterRegistry.REGISTRY_SESSION_NAME,pageContext.SESSION_SCOPE);
        if ( obj != null )
            return (PlotterRegistry) obj;
        return null;
    }
    
    public void savePlotterRegistry(PageContext pageContext, PlotterRegistry registry) {
        pageContext.setAttribute(PlotterRegistry.REGISTRY_SESSION_NAME,registry,PageContext.SESSION_SCOPE);        
    }
    
    public int getMaxPlots(PageContext pageContext) {
        int plotsPerSession = 20;
        Object o = pageContext.getAttribute("aida.max.plots.per.session",pageContext.SESSION_SCOPE);
        if ( o != null )
            plotsPerSession = Integer.parseInt(o.toString());
        return plotsPerSession;
    }

}
