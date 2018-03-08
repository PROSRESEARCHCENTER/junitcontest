package hep.aida.ref.xml;

import hep.aida.IAnalysisFactory;
import hep.aida.IBaseStyle;
import hep.aida.IPlotterStyle;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

/**
 * Restore IPlotterStyle from XML 
 * @author  The AIDA team @ SLAC.
 */
public class AidaStyleXMLReader {    
    private Element rootElement;
    
    // Convenience static method for restoring IPlotterStyle directly from XML file on disk
    public static IPlotterStyle restoreFromFile(String fileName) throws IOException, JDOMException  {
        File file = new File(fileName);
        FileReader reader = new FileReader(file);
        AidaStyleXMLReader xmlReader = new AidaStyleXMLReader(reader);
        
        IPlotterStyle style = IAnalysisFactory.create().createPlotterFactory().createPlotterStyle();        
        xmlReader.setStyle(style);  
        
        return style;
    }
    
    public static void setStyleFromFile(IPlotterStyle style, String fileName) throws IOException, JDOMException  {
        File file = new File(fileName);
        FileReader reader = new FileReader(file);
        AidaStyleXMLReader xmlReader = new AidaStyleXMLReader(reader);
        
        xmlReader.setStyle(style);  
        
        return;
    }
    
    public static void setStyleFromFile(IPlotterStyle style, URL fileURL) throws IOException, JDOMException  {
        InputStream is = fileURL.openStream();
        Reader reader = new InputStreamReader(is);
        AidaStyleXMLReader xmlReader = new AidaStyleXMLReader(reader);
        
        xmlReader.setStyle(style);  
        
        return;
    }
    
    
    /** Creates a new instance of AidaStyleXMLReader */
    public AidaStyleXMLReader(Reader reader) throws IOException, JDOMException {
        SAXBuilder builder  = new SAXBuilder();
        Document   doc      = builder.build(reader);
        rootElement = doc.getRootElement();         
    }
    
    
    /*
     * To create new IPlotterStyle
     */
    public IPlotterStyle createStyle() {
        IPlotterStyle style = IAnalysisFactory.create().createPlotterFactory().createPlotterStyle();        
        setStyle(style, rootElement);
        return style;
    }
    
    /*
     * To set parameters in existing IPlotterStyle
     */
    public void setStyle(IPlotterStyle style) {
        setStyle(style, rootElement);
    }
    
    void setStyle(IBaseStyle style, Element el) {
        if (!el.getName().equals("aidaStyle") && !el.getName().equals("aidaPlotterStyle")) {
            System.out.println("***** AidaStyleXMLReader.setParameter: wrong element name: "+el.getName()+" ... do nothing here");
            return;
        }
        
        List children = el.getChildren(); 
        Iterator   it = children.iterator();
        while (it.hasNext()) {
            Element childElement = (Element) it.next();
            String name = childElement.getName();
            
            if (name.equals("aidaStyle")) {
                String styleName = childElement.getAttributeValue("type");
                IBaseStyle subStyle = null;
                Method method = null;
                try {
                    method = style.getClass().getMethod(styleName, (Class[]) null);
                    subStyle = (IBaseStyle) method.invoke(style, (Object[]) null);
                    setStyle(subStyle, childElement);
                } catch (Exception e) {
                    System.out.println("\t***** Invalid method: "+((method == null) ? "null" : method.getName())+" for: "+styleName+"... do nothing here");
                }
            } else if (name.equals("aidaStyleAttribute")) {
                setParameter(style, childElement);
            }
        }
    }
    
    void setParameter(IBaseStyle style, Element el) {
        if (!el.getName().equals("aidaStyleAttribute")) {
            System.out.println("AidaStyleXMLReader.setParameter: wrong element name: "+el.getName()+" ... do nothing here");
            return;
        }
        String name = el.getAttributeValue("attributeName");
        String value = el.getAttributeValue("attributeValue");
        if (value != null && value.equalsIgnoreCase("null")) value = null;
        style.setParameter(name, value);
    }
    
 
    // Shows how to use AidaStyleXMLReader by restoring IPlotterStyle from
    // XML file on disk and then writing it out again for comparison
    public static void main(String[] args) throws IOException, JDOMException {
        
        // Restoring IPlotterStyle from XML file
        /*
        String xmlFileName = "C:/work/Projects/Tests/PlotterStyle.xml";
        IPlotterStyle style = AidaStyleXMLReader.restoreStyleFromFile(xmlFileName);
        
         */

            String tmp = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\" ?>\n";
            tmp += "<!DOCTYPE aidaPlotterStyle SYSTEM \"http://java.freehep.org/schemas/jaida/1.0/aidaPlotterStyle.dtd\">\n";
            tmp += "<aidaPlotterStyle>\n";
        tmp += "<aidaStyle type=\"dataStyle\">\n";
        tmp += "<aidaStyle type=\"markerStyle\">\n";
        tmp += "<aidaStyleAttribute attributeName=\"color\" attributeValue=\"red\"/>\n";
        tmp += "</aidaStyle>\n";
        tmp += "</aidaStyle>\n";
            tmp += "</aidaPlotterStyle>\n";
    
        Reader reader = new StringReader(tmp);
        AidaStyleXMLReader xmlReader = new AidaStyleXMLReader(reader); 
        
        IPlotterStyle plotterStyle = IAnalysisFactory.create().createPlotterFactory().createPlotterStyle();        
        xmlReader.setStyle(plotterStyle);  

        // Writing IPlotterStyle to different XML file
        boolean writeAllParameters = false;
        String newXmlFileName = "C:/work/Projects/Tests/PlotterStyle-test2.xml";
        AidaStyleXMLWriter.writeToFile(newXmlFileName, plotterStyle, writeAllParameters);
    }
    
}
