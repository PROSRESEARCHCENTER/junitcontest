package hep.aida.ref.plotter.style.registry;

import hep.aida.IAnalysisFactory;
import hep.aida.IBaseStyle;
import hep.aida.IPlotterFactory;
import hep.aida.IPlotterStyle;
import hep.aida.ref.plotter.BaseStyle;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import org.freehep.xml.util.XMLWriter;

public class StyleStoreXMLWriter extends XMLWriter {
    
    public static void writeToFile(String fileName, IStyleStore store) throws IOException {
        writeToFile(fileName, store, false);
    }
    public static void writeToFile(String fileName, IStyleStore store, boolean writeAllParameters) throws IOException {
        File file = new File(fileName);
        if (file.exists()) file.delete();
        
        Writer writer = new BufferedWriter(new FileWriter(file));
        
        StyleStoreXMLWriter xmlWriter = new StyleStoreXMLWriter(writer);
        xmlWriter.writeXMLStyleStore(store, writeAllParameters);
        
        xmlWriter.close();
    }
    public StyleStoreXMLWriter(Writer writer) throws IOException {
        super(writer);
        openDoc("1.0", "ISO-8859-1", false);
        
        //referToDTD("aidaStyleStore", "http://java.freehep.org/schemas/jaida/1.0/StyleStoreSchema.xsd");
        referToDTD("aidaStyleStore", "http://java.freehep.org/schemas/jaida/1.0/StyleStoreDTD.dtd");
    }
    
    public void close() throws IOException {
        super.close();
    }
    
    
    // Writing IStyleStore to XML
    public void writeXMLStyleStore(IStyleStore store) {
        writeXMLStyleStore(store, false);
    }
    public void writeXMLStyleStore(IStyleStore store, boolean writeAllParameters) {
        setAttribute("storeName",  store.getStoreName());
        setAttribute("storeType",  store.getStoreType());
        setAttribute("isReadOnly", store.isReadOnly());
        openTag("aidaStyleStore");
        
        String[] names = store.getAllStyleNames();
        
        if (store instanceof BaseStyleStore) {
            for (int i=0; i<names.length; i++) {
                StyleStoreEntry entry = ((BaseStyleStore) store).getStoreEntry(names[i]);
                storeEntryToXML(names[i], entry.getPreviewType().getName(), store.getStyle(names[i]), entry.getRule());
            }
        } else {
            for (int i=0; i<names.length; i++) {
                storeEntryToXML(names[i], store.getStyle(names[i]), store.getRuleForStyle(names[i]));
            }
        }
        closeTag();
    }
    
    
    // Writing StyleStore Entry to XML
    public void storeEntryToXML(String name, IPlotterStyle style, IStyleRule rule) {
        String previewType = StyleStoreEntry.DEFAULT_ENTRY_TYPE;
        storeEntryToXML(name, previewType, style, rule);
    }
    public void storeEntryToXML(String name, String type, IPlotterStyle style, IStyleRule rule) {
        setAttribute("entryName", name);
        if (type != null) setAttribute("entryType", type);
        openTag("aidaStyleStoreEntry");
        
        styleRuleToXML(rule);
        plotterStyleToXML(style);
        
        closeTag();
        
    }
    
    
    // Writing IStyleRule to XML
    public void styleRuleToXML(IStyleRule rule) {
        setAttribute("ruleValue", rule.getDescription());
        if (rule instanceof JELRule && ((JELRule) rule).getType() != null) {
            setAttribute("ruleType", ((JELRule) rule).getType());
        }
        printTag("aidaStyleRule");
    }
    
    
    
    // Writing IPlotterStyle to XML
    
    public void plotterStyleToXML(IPlotterStyle style) {
        plotterStyleToXML(style, false);
    }
    
    public void plotterStyleToXML(IPlotterStyle style, boolean writeAllParameters) {
        
        if (isStyleEmpty(style, writeAllParameters)) return;
        
        openTag("aidaPlotterStyle");
        
        IBaseStyle[] children = style.children();
        if (children != null) {
            for (int i=0; i<children.length; i++) {
                styleToXML(children[i], writeAllParameters);
            }
        }
        
        styleParametersToXML(style, writeAllParameters);
        
        closeTag();
        
    }
    
    void styleToXML(IBaseStyle style) {
        styleToXML(style, false);
    }
    
    void styleToXML(IBaseStyle style, boolean writeAllParameters) {
        if (isStyleEmpty(style, writeAllParameters)) return;
        
        setAttribute("type", style.name());
        openTag("aidaStyle");
        
        IBaseStyle[] children = style.children();
        if (children != null) {
            for (int i=0; i<children.length; i++) {
                styleToXML(children[i], writeAllParameters);
            }
        }
        
        styleParametersToXML(style, writeAllParameters);
        
        closeTag();
    }
    
    void styleParametersToXML(IBaseStyle style, boolean writeAllParameters) {
        String[] parNames = style.availableParameters();
        if (parNames != null) {
            for (int i=0; i<parNames.length; i++) {
                String name = parNames[i];
                String value = style.parameterValue(name);
                if (value == null) value = "null";
                
                boolean isSet = true;
                String type = String.class.getName();
                String[] possibleValues = null;
                if (style instanceof BaseStyle) {
                    isSet = ((BaseStyle) style).isParameterSet(name);
                    type = ((BaseStyle) style).parameter(name).type().getName();
                    possibleValues = ((BaseStyle) style).availableParameterOptions(name);
                }
                String options = null;
                if (possibleValues != null && possibleValues.length > 0) {
                    options = "\""+possibleValues[0]+"\"";
                    for (int o=1; o<possibleValues.length; o++) options += ", \""+possibleValues[o]+"\"";
                }
                if (isSet || writeAllParameters) {
                    setAttribute("attributeName", name);
                    setAttribute("attributeValue", value);
                    if (options != null) setAttribute("attributeOptions", options);
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
    
    
    public static void main(String[] args) throws Exception {
        XMLStyleStore store = new XMLStyleStore("XML Store 2", XMLStyleStore.TYPE, false);
        
        IPlotterFactory pf = IAnalysisFactory.create().createPlotterFactory();
        IPlotterStyle style1 = pf.createPlotterStyle();
        style1.dataBoxStyle().setVisible(true);
        style1.dataBoxStyle().backgroundStyle().setPattern("hatched");
        JELRule rule1 = new JELRule("OverlayNumber==1");
        store.addStyle("Style-1", style1, rule1);
        
        IPlotterStyle style2 = pf.createPlotterStyle();
        style2.dataBoxStyle().setVisible(false);
        style2.dataBoxStyle().backgroundStyle().setColor("White");
        JELRule rule2 = new JELRule("RegionNumber>=2");
        store.addStyle("Style-2", style2, rule2);
        
        String fileName = "C:/TEMP/xmlStore.xml";
        StyleStoreXMLWriter.writeToFile(fileName, store);
    }
    
}
