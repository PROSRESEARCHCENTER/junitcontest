package hep.aida.web.taglib;

import hep.aida.IBaseHistogram;
import hep.aida.IBaseStyle;
import hep.aida.IDataPointSet;
import hep.aida.IDataStyle;
import hep.aida.IFunction;
import hep.aida.IManagedObject;
import hep.aida.IPlotterRegion;
import hep.aida.IPlotterStyle;
import hep.aida.ref.plotter.PlotterRegion;
import hep.aida.web.taglib.util.AidaTLDUtils;
import hep.aida.web.taglib.util.LogUtils;
import jas.hist.DataSource;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * The implementation class for all PlotTag classes.
 *
 * @author The AIDA Team @ SLAC
 *
 */
public class PlotTagSupport implements PlotTag {
    
    private Object var;
    private IBaseStyle style = null;
    
    private RegionTagSupport regionTagSupport;
    
    RegionTagSupport getRegionTagSupport() {
        return regionTagSupport;
    }
    
    public void doStartTag(RegionTagSupport regionTagSupport)
    throws JspException {
        this.regionTagSupport = regionTagSupport;
    }
    
    public void doEndTag(PageContext pageContext) throws JspException {
        Object plotObject = getPlotObject(pageContext);
        
        // Why don't we get the IPlotterStyle from our region's parent?
        IPlotterStyle plotterStyle = null;
        if (style instanceof IPlotterStyle) plotterStyle = (IPlotterStyle) style;
        else {
            plotterStyle = regionTagSupport.getPlotterTagSupport()
            .getPlotterFactory().createPlotterStyle();
            plotterStyle.setDataStyle(getDataStyle());
        }
        
        IPlotterRegion plotterRegion = regionTagSupport.getPlotterRegion();
        if (plotObject instanceof IBaseHistogram) {
            plotterRegion.plot((IBaseHistogram) plotObject, plotterStyle);
        } else if (plotObject instanceof IDataPointSet) {
            plotterRegion.plot((IDataPointSet) plotObject, plotterStyle);
        } else if (plotObject instanceof IFunction) {
            plotterRegion.plot((IFunction) plotObject, plotterStyle);
        } else {
            ((PlotterRegion) plotterRegion).add(plotObject, plotterStyle, 1);
        }
    }
    
    /*
     * (non-Javadoc)
     *
     * @see hep.aida.web.taglib.PlotTag#setPlotObject(java.lang.Object)
     */
    public void setVar(Object plotObject) {
        this.var = plotObject;
    }
    
    public Object getVar() {
        return var;
    }
    
    public IBaseStyle getStyle() throws JspException {
        return getDataStyle();
    }
    
    public IBaseStyle getStyle(String type) throws JspException {
        if (type == null) 
            return getDataStyle();
        else if (type.equalsIgnoreCase(StyleProvider.plotterStyle))
            return getPlotterStyle();
        throw new JspException(
                "If you see this you have a logic error: type = " + type);
    }
    
    private IDataStyle getDataStyle() {
        if (style == null) {
            if ( LogUtils.log().isDebugEnabled() )
                LogUtils.log().debug("create a default data style");
            style = getRegionTagSupport().getPlotterTagSupport()
            .getPlotterFactory().createDataStyle();
        }
        return (IDataStyle) style;
    }
    
    private IPlotterStyle getPlotterStyle() {
        if (style == null) {
            if ( LogUtils.log().isDebugEnabled() )
                LogUtils.log().debug("create a default plotter style");
            style = getRegionTagSupport().getPlotterTagSupport()
            .getPlotterFactory().createPlotterStyle();
        }
        return (IPlotterStyle) style;
    }
    
    private Object getPlotObject(PageContext pageContext) throws JspException {
        if (var == null) {
            throw new JspException("nothing to plot (var is null)");
        }
        
        // First, see if we were passed an IManagedObject.
        Object plotObject = null;
        if (var instanceof IManagedObject || var instanceof DataSource) {
            plotObject = (Object) var;
            return plotObject;
        }
        
        // If we were passed a string, then search all JSP scopes for an
        // IManagedObject with the name.
        if (var instanceof String) {
            String attributeName = (String) var;
            plotObject = findPlotObject(attributeName, pageContext);
        }
        
        if (plotObject == null) {
            throw new JspException("nothing to plot (plotObject is null)");
        }
        
        return plotObject;
    }
    
    /**
     * Find an IManagedObject in a JSP scope under the given attribute name. If
     * nothing is found then return null.
     *
     * @param attributeName
     *            the name of the IManagedObject in a JSP scope
     * @return the IManagedObject if it is found, otherwise null
     */
    private Object findPlotObject(String attributeName, PageContext pageContext) {
        Object plotObject = null;
        
        // There is a bug in ColdFusion MX 6.1 on JRun4 whereby a
        // request scope attribute exists but its value is always null.
        // Therefore, we simply search the scopes ourselves.
        // plotObject = (IManagedObject)
        // pageContext.findAttribute(attributeName);
        int[] scope = { PageContext.PAGE_SCOPE, PageContext.REQUEST_SCOPE,
        PageContext.SESSION_SCOPE, PageContext.APPLICATION_SCOPE };
        for (int i = 0; i < scope.length; ++i) {
            plotObject = (Object) pageContext.getAttribute(attributeName,
                    scope[i]);
            if (plotObject != null) {
                break;
            }
        }
        
        return plotObject;
    }
}