package hep.aida.web.taglib.util;

import hep.aida.web.taglib.PlotterRegistry;
import hep.aida.web.taglib.PlotterTagSupport;
import hep.aida.web.taglib.RegionTagSupport;
import java.io.InputStream;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.PageContext;
import org.freehep.graphicsbase.util.export.ExportFileType;
import org.freehep.graphicsbase.util.export.ExportFileTypeGroups;


/**
 * Various utility functions for the tags.
 *
 * @author The AIDA Team @ SLAC
 *
 */
public abstract class PlotUtils {
    
    private static int mapCounter = 0;
    
    private static Map scopeMap = new HashMap();
    
    private static PlotterHelper plotterHelper = null;
    private static String propertiesFile = "/freehepWebapp.properties";
        
    static {
        scopeMap.put("page", new Integer(PageContext.PAGE_SCOPE));
        scopeMap.put("request", new Integer(PageContext.REQUEST_SCOPE));
        scopeMap.put("session", new Integer(PageContext.SESSION_SCOPE));
        scopeMap.put("application", new Integer(PageContext.APPLICATION_SCOPE));
    }
    
    public static PlotterHelper getPlotterHelper() {
        if ( plotterHelper == null ) {
            Properties props = new Properties();
    
            InputStream input = null;
            try {
                input = PlotUtils.class.getResourceAsStream(propertiesFile);
                if (input != null)
                    props.load(input);
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                try {
                    if (input!= null) {
                        input.close();
                    }
                } catch (java.io.IOException ex) {
                    ex.printStackTrace();
                }
            }
            
            String plotterHelperClass = props.getProperty("hep.aida.web.plotterhelper","hep.aida.web.taglib.DefaultPlotterHelper");
            try {
                plotterHelper = (PlotterHelper) Class.forName(plotterHelperClass).getConstructor(new Class[0]).newInstance(new Object[0]);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return plotterHelper;
    }
    
    public static int getScope(String scopeName) {
        return ((Integer) scopeMap.get(scopeName)).intValue();
    }
    
    public static String createHtml(HttpServletRequest request,
            PlotCommand plotCommand, PlotterTagSupport plotterTagSupport) {
        StringBuffer buffer = new StringBuffer(createImageTag(request,
                plotCommand, plotterTagSupport));
        if (plotCommand.isAllowDownload()) {
            buffer.append(createDownloadLinks(request, plotCommand));
        }
        
        return buffer.toString();
    }
    
    private static String createImageTag(HttpServletRequest request,
            PlotCommand plotCommand, PlotterTagSupport plotterTagSupport) {
        StringBuffer buffer;
        
        buffer = new StringBuffer("<img width=\"");
        buffer.append(plotCommand.getWidth());
        buffer.append("\" height=\"");
        buffer.append(plotCommand.getHeight());
        buffer.append("\" src=\"");
        buffer.append(urlEncode(request, plotCommand)+"\"");
        
        String mapName = "gridMap"+mapCounter;
        if ( plotCommand.createImageMap() ) {
            mapCounter = mapCounter > 999 ? 0 : mapCounter + 1;
            buffer.append(" usemap=\"#"+mapName+"\"");
        }
        
        buffer.append("/>");
        
        
        if ( plotCommand.createImageMap() ) {
            
            buffer.append("\n<map name=\""+mapName+"\">");
            
            int nRegions = plotterTagSupport.numberOfRegions();
            int width = plotterTagSupport.getWidth();
            int height = plotterTagSupport.getHeight();
            for ( int i = 0; i < nRegions; i++ ) {
                RegionTagSupport regionTagSupport = plotterTagSupport.regionTagSupport(i);
                String href = regionTagSupport.getHref();
                if ( href != null && ! href.equals("")) {
                    int xUpperCorner = (int)(regionTagSupport.getX()*width);
                    int yUpperCorner = (int)(regionTagSupport.getY()*height);
                    int xLowerCorner = xUpperCorner + (int)(regionTagSupport.getWidth()*width);
                    int yLowerCorner = yUpperCorner + (int)(regionTagSupport.getHeight()*height);
                    buffer.append("\n<area shape=\"rect\" coords=\""+xUpperCorner+","+yUpperCorner+","+xLowerCorner+","+yLowerCorner+"\" href=\""+href+"\"/>");
                }
            }
            buffer.append("\n</map>\n");
        }
        String img = buffer.toString();
        return img;
    }
    
    private static String createDownloadLinks(HttpServletRequest request,
            PlotCommand plotCommandTemplate) {
        
        PlotCommand plotCommand = new PlotCommand();
        plotCommand.setName(plotCommandTemplate.getName());
        plotCommand.setWidth(plotCommandTemplate.getWidth());
        plotCommand.setHeight(plotCommandTemplate.getHeight());
        plotCommand.setAllowDownload(true);
        
        StringBuffer buffer = new StringBuffer("\n<p>Download:");
        
        ExportFileTypeGroups groups = new ExportFileTypeGroups(ExportFileType.getExportFileTypes());
        String[] groupNames = { ExportFileTypeGroups.VECTOR, ExportFileTypeGroups.BITMAP };
        for (int i=0; i<groupNames.length; i++) {
            if (i != 0) buffer.append(", ");
            buffer.append("\n(<i>");
            buffer.append(groupNames[i]);
            buffer.append("</i>)\n");
            List types = groups.getExportFileTypes(groupNames[i]);
            Iterator iterator = types.iterator();
            while (iterator.hasNext()) {
                ExportFileType fileType = (ExportFileType) iterator.next();
                String format = fileType.getExtensions()[0];
                
                if ( format.equals("bmp") || format.equals("wbmp") || format.equals("raw") )
                    continue;
                
                plotCommand.setFormat(format);
                
                buffer.append("\n");
                buffer.append(" <a href=\"");
                buffer.append(urlEncode(request, plotCommand));
                buffer.append("\">");
                buffer.append(format);
                buffer.append("</a>\n");
            }
        }
        buffer.append("</p>\n");
        
        return buffer.toString();
    }
    
    private static String urlEncode(HttpServletRequest request,
            PlotCommand plotCommand) {
        StringBuffer buffer;
        
        buffer = new StringBuffer(request.getContextPath());
        buffer.append("/aida_plot.jsp?name=");
        buffer.append(plotCommand.getName());
        buffer.append("&width=");
        buffer.append(plotCommand.getWidth());
        buffer.append("&height=");
        buffer.append(plotCommand.getHeight());
        buffer.append("&format=");
        buffer.append(plotCommand.getFormat());
        String url = buffer.toString();
        
        return url;
    }
    
    public static String printPlotRegistry(HttpSession session) {
        System.out.println("*** Printing plot reigstry for session "+session.getId());
        Object obj = session.getAttribute(PlotterRegistry.REGISTRY_SESSION_NAME);
        if ( obj != null )
            return ((PlotterRegistry)obj).printPlotRegistry();
        return "no Plotter Registry found";
    }
    
    public static void clearPlotRegistry(HttpSession session) {
        System.out.println("*** Clearing plot reigstry for session "+session.getId());
        Object obj = session.getAttribute(PlotterRegistry.REGISTRY_SESSION_NAME);
        if ( obj != null )
            ((PlotterRegistry)obj).clear();
    }
    
}