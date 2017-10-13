package org.freehep.application.studio.pluginmanager;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.freehep.application.studio.PluginInfo;
import org.freehep.application.studio.Studio;
import org.freehep.xml.util.ClassPathEntityResolver;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

/**
 * SPI and default implementation for downloading a list of available plugins.
 * 
 * If an alternative way of downloading the plugin list needs to be used, a customized
 * subclass must be passed to the {@link PluginManager#setPluginListHandler}.
 * 
 * @author onoprien
 */
public class PluginListHandler {
    
    /**
     * Called by {@link PluginManager} to download a list of available plugins.
     * The provided implementation posts HTTP request to the supplied URL and uses JDOM to parse
     * the XML response conforming to "plugin.dtd".
     * 
     * @param url
     * @param logger
     * @param application
     * @return 
     */
    public List<PluginInfo> getAvailablePlugins(URL url, Logger logger, Studio application) {
        
        ArrayList<PluginInfo> availablePlugins = new ArrayList<PluginInfo>();
        
        try {
            // We do an http post to the plugin URL, so we can send extra info
            // which may be useful to the receiver
            URLConnection connection = url.openConnection();
            if (connection instanceof HttpURLConnection) {
//                try {
                    Properties prop = application.getAppProperties();

                    ByteArrayOutputStream byteStream = new ByteArrayOutputStream(512);
                    PrintWriter out = new PrintWriter(byteStream, true);
                    out.print("app.name=" + urlEncode(prop.getProperty("appName")));
                    out.print("&app.version=" + urlEncode(prop.getProperty("version")));
                    out.print("&os.name=" + urlEncode(System.getProperty("os.name")));
                    out.print("&os.arch=" + urlEncode(System.getProperty("os.arch")));
                    out.print("&os.version=" + urlEncode(System.getProperty("os.version")));
                    out.print("&java.version=" + urlEncode(System.getProperty("java.version")));
                    out.print("&java.vendor=" + urlEncode(System.getProperty("java.vendor")));
                    out.close();

                    String lengthString = String.valueOf(byteStream.size());
                    HttpURLConnection http = (HttpURLConnection) connection;
                    http.setRequestMethod("POST");
                    http.setUseCaches(false);
                    http.setDoOutput(true);
                    http.setRequestProperty("Content-Length", lengthString);
                    http.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    byteStream.writeTo(connection.getOutputStream());
//                } catch (UnsupportedEncodingException x) {
//                    if (logger != null) logger.log(Level.WARNING, "Unable to request plugin list", x);
//                }
            }

            // For the moment at least we will use JDOM here
            SAXBuilder builder = new SAXBuilder(true);
            builder.setEntityResolver(new ClassPathEntityResolver("plugin.dtd", Studio.class));
            java.io.InputStream in = connection.getInputStream();
            try {
                Document doc = builder.build(in);
                List<Element> root = doc.getRootElement().getChildren();
                for (Element node : root) {
                    PluginInfo plugin = new PluginInfo(node);
                    availablePlugins.add(plugin);
                }
            } finally {
                in.close();
            }
        } catch (Exception x) {
            if (logger == null) {
                application.error("Unable to retrieve the list of available plugins", x);
            } else {
                logger.log(Level.WARNING, "Unable to retrieve the list of available plugins", x);
            }
        }
        
        availablePlugins.trimToSize();
        return availablePlugins;
    }
    
    /** Encodes URL. */
    protected String urlEncode(String in) throws UnsupportedEncodingException {
        return URLEncoder.encode(in, "UTF-8");
    }
    
}
