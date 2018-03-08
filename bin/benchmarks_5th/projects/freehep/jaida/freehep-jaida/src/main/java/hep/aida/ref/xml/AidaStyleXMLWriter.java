package hep.aida.ref.xml;

import hep.aida.IAnalysisFactory;
import hep.aida.IBaseStyle;
import hep.aida.IPlotterStyle;
import hep.aida.ref.plotter.BaseStyle;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import org.freehep.xml.util.XMLWriter;


/**
 * Convert AIDA objects to XML.
 * @author The AIDA team @ SLAC.
 * @version $Id: AidaStyleXMLWriter.java 8704 2006-08-26 00:19:06Z serbo $
 */
public class AidaStyleXMLWriter extends XMLWriter {
    
    // Convenience statice methods for writing IPlotterStyle directly to XML file on disk
    
    public static void writeToFile(String fileName, IPlotterStyle style) throws IOException {
        writeToFile(fileName, style, false);
    }
    
    public static void writeToFile(String fileName, IPlotterStyle style, boolean writeAllParameters) throws IOException {
        File file = new File(fileName);
        if (file.exists()) file.delete();
        
        Writer writer = new BufferedWriter(new FileWriter(file));
        
        AidaStyleXMLWriter xmlWriter = new AidaStyleXMLWriter(writer);
        xmlWriter.plotterStyleToXML(style, writeAllParameters);
        
        xmlWriter.close();
    }
    
    
    public AidaStyleXMLWriter(Writer writer) throws IOException {
        super(writer);
        openDoc("1.0", "ISO-8859-1", false);
        
        referToDTD("aidaPlotterStyle", "http://java.freehep.org/schemas/jaida/1.0/aidaPlotterStyle.dtd");
    }
    
    public void close() throws IOException {
        super.close();
    }
    
    
    public void plotterStyleToXML(IPlotterStyle style) {
        plotterStyleToXML(style, false);
    }    
    public void plotterStyleToXML(IPlotterStyle style, boolean writeAllParameters) {
        
        if (isStyleEmpty(style, writeAllParameters)) return;

        openTag("aidaPlotterStyle");
        
        IBaseStyle[] children = style.children();
        if (children != null) {
            for (int i=0; i<children.length; i++) {
                toXML(children[i], writeAllParameters);
            }
        }
        
        parametersToXML(style, writeAllParameters);
        
        closeTag();
        
    }
    
    void toXML(IBaseStyle style) {
        toXML(style, false);
    }
    
    void toXML(IBaseStyle style, boolean writeAllParameters) {
        if (isStyleEmpty(style, writeAllParameters)) return;
        
        setAttribute("type", style.name());
        openTag("aidaStyle");
        
        IBaseStyle[] children = style.children();        
        if (children != null) {
            for (int i=0; i<children.length; i++) {
                toXML(children[i], writeAllParameters);
            }
        }
        
        parametersToXML(style, writeAllParameters);
        
        closeTag();
    }
    
    void parametersToXML(IBaseStyle style, boolean writeAllParameters) {
        String[] parNames = style.availableParameters();
        if (parNames != null) {
            for (int i=0; i<parNames.length; i++) {
                String name = parNames[i];
                String value = style.parameterValue(name);                
                if (value == null) value = "null";
                
                boolean isSet = true;
                String type = String.class.getName();
                if (style instanceof BaseStyle) {
                    isSet = ((BaseStyle) style).isParameterSet(name);
                    type = ((BaseStyle) style).parameter(name).type().getName();
                }
                
                if (isSet || writeAllParameters) {
                    setAttribute("attributeName", name);
                    setAttribute("attributeValue", value);
                    printTag("aidaStyleAttribute");
                }
            }
        }        
    }
    
    boolean isStyleEmpty(IBaseStyle style) {
        return isStyleEmpty(style, false);
    }
    boolean isStyleEmpty(IBaseStyle style, boolean writeAllParameters) {
        boolean isEmpty = true;
        
        // Check if style has any valid parameters
        String[] parNames = style.availableParameters();
        if (parNames != null) {
            for (int i=0; i<parNames.length; i++) {
                String name = parNames[i];
                boolean isSet = true;
                if (style instanceof BaseStyle) isSet = ((BaseStyle) style).isParameterSet(name);
                
                if (isSet || writeAllParameters) {
                    return false;
                }
            }
        }
        
        // Check if children styles are empty
        IBaseStyle[] children = style.children();
        if (children != null) {
            for (int i=0; i<children.length; i++) {
                if (!isStyleEmpty(children[i])) return false;
            }
        }
        
        return isEmpty;
    }
    
    public void printStyle(IBaseStyle style) {
        printStyle(style, false);
    }    
    public static void printStyle(IBaseStyle style, boolean writeAllParameters) {
        
        System.out.println("<"+style.name()+" type="+style.type().getName()+" >");
        IBaseStyle[] children = style.children();
        if (children != null) {
            for (int i=0; i<children.length; i++) {
                printStyle(children[i], writeAllParameters);
            }
        }
        
        String[] parNames = style.availableParameters();
        if (parNames != null) {
            for (int i=0; i<parNames.length; i++) {
                String name = parNames[i];
                String value = style.parameterValue(name);
                boolean isSet = true;
                if (style instanceof BaseStyle) isSet = ((BaseStyle) style).isParameterSet(name);
                
                if (isSet || writeAllParameters) {
                    System.out.println("\t\t<parameter name="+name+" value="+value+" />");
                }
            }
        }
        System.out.println("</"+style.name()+">");        
    }
    
 
    // Shows how to use AidaStyleXMLWriter by writing IPlotterStyle to XML file
    public static void main(String[] args) throws IOException {
        
        // Initial setup
        String fileName = "C:/work/Projects/Tests/PlotterStyle.xml";
        boolean writeAllParameters = true;
        
        
        // Create File and Writer
        File file = new File(fileName);
        if (file.exists()) file.delete();
        
        Writer writer = new BufferedWriter(new FileWriter(file));
        AidaStyleXMLWriter xmlWriter = new AidaStyleXMLWriter(writer);
        
        // Create AIDA IPlotterStyle and set several parameters        
        IPlotterStyle style = IAnalysisFactory.create().createPlotterFactory().createPlotterStyle();
        
        style.xAxisStyle().setLabel("xxxxxAxisLabel");
        style.dataStyle().markerStyle().setShape("box");
        style.yAxisStyle().setLabel("yyyyyAxisLabel");
        
        
        
        //AidaStyleXMLWriter.printStyle(style.dataStyle().lineStyle(), true);
        AidaStyleXMLWriter.printStyle(style.xAxisStyle(), true);
        
        // Write it ot XML file
        xmlWriter.plotterStyleToXML(style, writeAllParameters);        
        xmlWriter.close();                
    }
}