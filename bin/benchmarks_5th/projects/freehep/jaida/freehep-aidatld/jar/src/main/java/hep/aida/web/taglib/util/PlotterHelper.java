package hep.aida.web.taglib.util;

import hep.aida.web.taglib.PlotterRegistry;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;

/**
 *
 * @author The FreeHEP team @ SLAC.
 */
public interface PlotterHelper {
    
    public PlotterRegistry getPlotterRegistry(HttpServletRequest req) ;
    
    public PlotterRegistry getPlotterRegistry(PageContext pageContext);
    
    public void savePlotterRegistry(PageContext pageContext, PlotterRegistry registry);
    
    public int getMaxPlots(PageContext pageContext);

}
