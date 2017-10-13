package hep.aida.web.servlet;

import hep.aida.IPlotter;
import hep.aida.IPlotterFactory;
import hep.aida.ref.plotter.PlotterUtilities;
import hep.aida.web.taglib.util.LogUtils;
import hep.aida.web.taglib.util.PlotUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import hep.aida.web.taglib.PlotterRegistry;
import org.freehep.graphicsbase.util.export.ExportFileType;


/**
 * @author tonyj
 * web.servlet name = "aidaplot" load-on-startup = "1"
 * web.servlet-mapping url-pattern = "/servlet/AidaPlot"
 */
public class PlotServlet extends HttpServlet {
    
    private IPlotterFactory pf;
    
    private static NoPlotPlotter noPlotPlotter = null;
    
    /**
     * Initializes the servlet.
     */
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }
    
    private static String getMimeTypeForFormat(String format) {
        List types = ExportFileType.getExportFileTypes(format);
        if (types == null || types.size() == 0) {
            String message = "Unsupported format: " + format;
            LogUtils.log().warn(message);
            throw new IllegalArgumentException(message);
        }
        ExportFileType fileType = (ExportFileType) types.get(0);
        String[] mimeTypes = fileType.getMIMETypes();
        return ((mimeTypes == null) || (mimeTypes.length == 0)) ? "image/"
                + format : mimeTypes[0];
    }
    
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        
        String id = req.getParameter("name");
        String format = req.getParameter("format");
        String mime = getMimeTypeForFormat(format);

        res.setHeader("Content-Disposition", "attachment; filename=\"" + id +"."+ format + "\"");
        
        ServletOutputStream out = res.getOutputStream();
        res.setContentType(mime);
        printPlotToOutputStream(req, out);
    }

    public static void printPlotToOutputStream(HttpServletRequest req, OutputStream out) throws ServletException, IOException {

        String width = req.getParameter("width");
        String height = req.getParameter("height");
        String id = req.getParameter("name");
        String format = req.getParameter("format");
        LogUtils.log().debug("width=" + width + ", " + "height=" + height + ", " + "id="
                + id + ", " + "format=" + format);

        PlotterRegistry registry = PlotUtils.getPlotterHelper().getPlotterRegistry(req);
        IPlotter plotter = registry.plotter(id);

        if ( plotter == null ) {
            if ( noPlotPlotter == null )
                noPlotPlotter = new NoPlotPlotter();

            plotter = noPlotPlotter;
        }

        Properties options = new Properties();
        options.setProperty("plotWidth", width);
        options.setProperty("plotHeight", height);

        long start = System.currentTimeMillis();
        PlotterUtilities.writeToFile(plotter, out, format, options);
        long stop = System.currentTimeMillis();
        try {
            out.close();
        } catch (Exception e) {
        }
        //req.getSession().removeAttribute(id);
        LogUtils.log().warn("Plot generation took " + (stop - start) + "ms");


    }


}