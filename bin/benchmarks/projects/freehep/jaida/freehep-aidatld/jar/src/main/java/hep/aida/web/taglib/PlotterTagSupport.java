package hep.aida.web.taglib;

import hep.aida.IAnalysisFactory;
import hep.aida.IBaseStyle;
import hep.aida.IPlotter;
import hep.aida.IPlotterFactory;
import hep.aida.IPlotterRegion;
import hep.aida.ref.plotter.DummyPlotterFactory;
import hep.aida.web.taglib.util.PlotCommand;
import hep.aida.web.taglib.util.PlotUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;

/**
 * The implementation class for all PlotterTag classes.
 * 
 * @author The AIDA Team @ SLAC
 *
 */
public class PlotterTagSupport implements PlotterTag {

    private String name, plotName;

    private int width = 600;

    private int height = 400;

    private int nx = 1;

    private int ny = 1;

    private String format = "png";

    private boolean allowDownload = true;

    private Object plotterObject;

    private IPlotter plotter;

    private boolean nxOrNySet = false;

    private BitSet cellUsed;

    private boolean createImageMap = false;

    private ArrayList regions = new ArrayList();
    
    private static IPlotterFactory plotterFactory = null;

    public synchronized IPlotterFactory getPlotterFactory() {
        if ( plotterFactory == null ) {
            plotterFactory = IAnalysisFactory.create().createPlotterFactory();
        }
        return plotterFactory;
    }

    public void doStartTag(PageContext pageContext) throws JspException {
        // Reset per-invocation state.
        cellUsed = null;
        plotter = null;
        
        if (isNxOrNySet()) {
            setCellUsed(new BitSet(getNx() * getNy()));
        }

        // If we were passed a plotter, then try to make it an IPlotter.
        if (plotterObject != null) {
            if (plotterObject instanceof IPlotter) {
                plotter = (IPlotter) plotterObject;
            } else if (plotterObject instanceof String) {
                // If plotter is a string, then search all JSP scopes for an
                // IPlotter with that name.
                String attributeName = (String) plotterObject;
                plotter = findPlotter(attributeName, pageContext);
            } else {
                // We don't know how to handle objects of this type.
                throw new JspException("don't know how to handle plotter "
                        + plotterObject);
            }
        }
    }

    public void doEndTag(PageContext pageContext) throws JspException {

        PlotterRegistry registry = plotterRegistry(pageContext);
        String innername = registry.addPlotter(plotter);

        if ( name != null )
            pageContext.setAttribute(name, innername, PageContext.SESSION_SCOPE);

        PlotCommand plotCommand = new PlotCommand();
        plotCommand.setName(innername);
        plotCommand.setWidth(getWidth());
        plotCommand.setHeight(getHeight());
        plotCommand.setFormat(getFormat());
        plotCommand.setAllowDownload(isAllowDownload());
        plotCommand.setCreateImageMap(getCreateImageMap());

        HttpServletRequest request = (HttpServletRequest) pageContext
                .getRequest();
        try {
            pageContext.getOut().println(
                    PlotUtils.createHtml(request, plotCommand, this));
        } catch (IOException e) {
            throw new JspException(e);
        }
    }

    public IPlotterRegion createRegion(double x, double y, double width,
            double height, int rowSpan, int colSpan,
            RegionTagSupport regionTagSupport) {

        regions.add(regionTagSupport);
        if (isNxOrNySet()) {
            BitSet cellUsed = getCellUsed();
            int nx = getNx();
            int ny = getNy();

            int next = cellUsed.nextClearBit(0);
            int ix = next % nx;
            int iy = next / nx;
            for (int i = ix; i < ix + colSpan; i++) {
                for (int j = iy; j < iy + rowSpan; j++) {
                    cellUsed.set(i + nx * j);
                }
            }
            double w = 1. / nx;
            double h = 1. / ny;
            x = ix * w;
            y = iy * h;
            width = colSpan * w;
            height = rowSpan * h;

            regionTagSupport.setX(x);
            regionTagSupport.setY(y);
            regionTagSupport.setWidth(width);
            regionTagSupport.setHeight(height);
        }
        return getPlotter().createRegion(x, y, width, height);
    }

    private PlotterRegistry plotterRegistry(PageContext pageContext) {                
        Object obj = PlotUtils.getPlotterHelper().getPlotterRegistry(pageContext);        
        int plotsPerSession = PlotUtils.getPlotterHelper().getMaxPlots(pageContext);
        if ( obj == null ) {
            obj = new PlotterRegistry(plotsPerSession);
            PlotUtils.getPlotterHelper().savePlotterRegistry(pageContext,(PlotterRegistry)obj);
        }
        return (PlotterRegistry) obj;
    }
        
    /*
     * (non-Javadoc)
     * 
     * @see hep.aida.web.taglib.ELPlotterTagImpl#setName(java.lang.String)
     */
    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return "plot";
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see hep.aida.web.taglib.ELPlotterTagImpl#setWidth(int)
     */
    public void setWidth(int width) {
        this.width = width;
    }

    public int getWidth() {
        return width;
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see hep.aida.web.taglib.ELPlotterTagImpl#setHeight(int)
     */
    public void setHeight(int height) {
        this.height = height;
    }

    public int getHeight() {
        return height;
    }

    /*
     * (non-Javadoc)
     * 
     * @see hep.aida.web.taglib.ELPlotterTagImpl#setNx(int)
     */
    public void setNx(int nx) {
        this.nx = nx;
        setNxOrNySet(true);
    }

    public int getNx() {
        return nx;
    }

    /*
     * (non-Javadoc)
     * 
     * @see hep.aida.web.taglib.ELPlotterTagImpl#setNy(int)
     */
    public void setNy(int ny) {
        this.ny = ny;
        setNxOrNySet(true);
    }

    public int getNy() {
        return ny;
    }

    /*
     * (non-Javadoc)
     * 
     * @see hep.aida.web.taglib.ELPlotterTagImpl#setFormat(java.lang.String)
     */
    public void setFormat(String format) {
        this.format = format;
    }

    public String getFormat() {
        return format;
    }

    /*
     * (non-Javadoc)
     * 
     * @see hep.aida.web.taglib.ELPlotterTagImpl#setAllowDownload(boolean)
     */
    public void setAllowDownload(boolean allowDownload) {
        this.allowDownload = allowDownload;
    }

    public boolean isAllowDownload() {
        return allowDownload;
    }

    /*
     * (non-Javadoc)
     * 
     * @see hep.aida.web.taglib.PlotterTagSupport#setCreateImageMap(boolean)
     */
    public void setCreateImageMap(boolean createImageMap) {
        this.createImageMap = createImageMap;
    }

    public boolean getCreateImageMap() {
        return createImageMap;
    }

    /*
     * (non-Javadoc)
     * 
     * @see hep.aida.web.taglib.ELPlotterTagImpl#setPlotter(hep.aida.IPlotter)
     */
    public void setVar(Object plotter) {
        this.plotterObject = plotter;
    }

    public Object getVar() {
        return plotterObject;
    }

    public void setPlotNameVar(String plotName) {
        this.plotName = plotName;
    }

    public String getPlotNameVar() {
        return plotName;
    }

    public IPlotter getPlotter() {
        if (plotter == null) {
            plotter = getPlotterFactory().create("DefaultPlotter");
        }

        return plotter;
    }

    public IBaseStyle getStyle() throws JspException {
        return (IBaseStyle)getPlotter().style();
    }

    public IBaseStyle getStyle(String type) throws JspException {
        throw new JspException(
                "If you see this you have a logic error: type = " + type);
    }

    public boolean isNxOrNySet() {
        return nxOrNySet;
    }

    private void setNxOrNySet(boolean nxOrNySet) {
        this.nxOrNySet = nxOrNySet;
    }

    private BitSet getCellUsed() {
        return cellUsed;
    }

    private void setCellUsed(BitSet cellUsed) {
        this.cellUsed = cellUsed;
    }

    /**
     * Find an {@link IPlotter}in a JSP scope under the given attribute name.
     * If nothing is found then return null.
     * 
     * @param attributeName
     *            the name of the {@link IPlotter}in a JSP scope
     * @return the {@link IPlotter}if it is found, otherwise null
     */
    private IPlotter findPlotter(String attributeName, PageContext pageContext) {
        IPlotter plotter = null;

        // There is a bug in ColdFusion MX 6.1 on JRun4 whereby a
        // request scope attribute exists but its value is always null.
        // Therefore, we simply search the scopes ourselves.
        // managedObject = (IManagedObject)
        // pageContext.findAttribute(attributeName);
        int[] scope = { PageContext.PAGE_SCOPE, PageContext.REQUEST_SCOPE,
                PageContext.SESSION_SCOPE, PageContext.APPLICATION_SCOPE };
        for (int i = 0; i < scope.length; ++i) {
            plotter = (IPlotter) pageContext.getAttribute(attributeName,
                    scope[i]);
            if (plotter != null) {
                break;
            }
        }

        return plotter;
    }

    /**
     * The the ith RegionTagSupport
     */
    public RegionTagSupport regionTagSupport(int i) {
        return (RegionTagSupport) regions.get(i);
    }

    /**
     * Get the number of regions
     *  
     */
    public int numberOfRegions() {
        return regions.size();
    }
}