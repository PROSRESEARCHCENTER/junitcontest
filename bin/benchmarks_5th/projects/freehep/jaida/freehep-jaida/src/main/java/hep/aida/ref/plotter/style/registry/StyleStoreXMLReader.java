package hep.aida.ref.plotter.style.registry;

import hep.aida.IAnalysisFactory;
import hep.aida.IBaseStyle;
import hep.aida.IPlotterFactory;
import hep.aida.IPlotterStyle;
import hep.aida.ref.AidaUtils;
import hep.aida.ref.plotter.BaseStyle;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Method;
import java.net.URL;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Iterator;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

public class StyleStoreXMLReader {
    private Element rootElement;
    private IPlotterFactory plotterFactory;
    
    // Convenience static method for restoring IStyleStore directly from XML file on disk or on the net
    
    public static IStyleStore restoreFromFile(String fileName) throws IOException, JDOMException  {
        boolean readOnly = false;
        String normalFileName = new String(fileName);
        String protocol = fileName.substring(0, 4);
        if (protocol.equalsIgnoreCase("http") || protocol.equalsIgnoreCase("ftp:")) {
            readOnly = true;
            normalFileName = null;
        } else if (protocol.equalsIgnoreCase("file")) {
            normalFileName = fileName.substring(5);
            File file = new File(normalFileName);
            if (!file.canWrite()) readOnly = true;
        } else {
            File file = new File(normalFileName);
            if (!file.canWrite()) readOnly = true;
            fileName = "file:"+fileName;
        }
        URL source = new URL(fileName);
        BufferedReader reader = new BufferedReader(new InputStreamReader(source.openStream()));
        StyleStoreXMLReader xmlReader = new StyleStoreXMLReader(reader);
        
        XMLStyleStore store = xmlReader.createXMLStyleStore();
        
        // Set Store as "Read-Only" if file is on the web, or is not writable
        //System.out.println("XMLStyleStore :: isReadOnly="+readOnly+", file="+normalFileName);
        //store.setReadOnly(readOnly);
        store.setCommitFileName(normalFileName);
        return store;
    }
    
    public static IStyleStore restoreFromDB(String url, String user, String pass, String table, String column) throws Exception  {
        boolean readOnly = false;
        String normalFileName = new String(url);
        
        System.out.println("fileName="+url+", user="+user+", pass="+pass);
        
        Class.forName("oracle.jdbc.driver.OracleDriver");
        Connection connection = DriverManager.getConnection(url, user, pass);
        
        Statement stRead = connection.createStatement();
        ResultSet rs = stRead.executeQuery("SELECT * FROM "+table);
        rs.next();
        Clob clob = rs.getClob(column);
        
        Reader reader = clob.getCharacterStream();
        StyleStoreXMLReader xmlReader = new StyleStoreXMLReader(reader);
        
        XMLStyleStore store = xmlReader.createXMLStyleStore();
        
        store.setCommitFileName(normalFileName);
        return store;
    }
    
    public static IStyleStore restoreFromStream(InputStream stream) throws IOException, JDOMException  {
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        StyleStoreXMLReader xmlReader = new StyleStoreXMLReader(reader);
        
        XMLStyleStore store = xmlReader.createXMLStyleStore();
        
        return store;
    }
    
    
    
    public StyleStoreXMLReader(Reader reader) throws IOException, JDOMException {
        SAXBuilder builder  = new SAXBuilder();
        builder.setValidation(false);
        
        EntityResolver resolver = new EntityResolver() {
            public InputSource resolveEntity(String publicId, String systemId) {
                InputStream in = getClass().getResourceAsStream(
                        "/hep/aida/ref/xml/StyleStoreDTD.dtd"
                        );
                return new InputSource( in );
            }
        };
        
        builder.setEntityResolver(resolver);
        //builder.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        Document   doc      = builder.build(reader);
        rootElement = doc.getRootElement();
        plotterFactory = IAnalysisFactory.create().createPlotterFactory();
    }
    
    XMLStyleStore createXMLStyleStore() {
        XMLStyleStore store = null;
        String storeName = rootElement.getAttributeValue("storeName");
        String storeType = rootElement.getAttributeValue("storeType");
        boolean isReadOnly  = true;
        String tmp = rootElement.getAttributeValue("isReadOnly");
        if (tmp.equalsIgnoreCase("false")) isReadOnly = false;
        
        store = new XMLStyleStore(storeName, storeType, isReadOnly);
        
        List children = rootElement.getChildren();
        Iterator   it = children.iterator();
        while (it.hasNext()) {
            Element childElement = (Element) it.next();
            String name = childElement.getName();
            
            if (name.equals("aidaStyleStoreEntry")) {
                try {
                    StyleStoreEntry entry = createStoreEntry(childElement);
                    store.addStoreEntry(entry);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return store;
    }
    
    StyleStoreEntry createStoreEntry(Element el) {
        StyleStoreEntry entry = null;
        if (!el.getName().equals("aidaStyleStoreEntry"))
            throw new IllegalArgumentException("StyleStoreXMLReader.createStoreEntry :: wrong element name: "+el.getName()+" ... do nothing here");
        
        String name = el.getAttributeValue("entryName");
        String type = el.getAttributeValue("entryType");
        if (type == null || type.trim().equals("")) type = StyleStoreEntry.DEFAULT_ENTRY_TYPE;
        IStyleRule rule = createRule(el.getChild("aidaStyleRule"));
        IPlotterStyle style = createStyle(el.getChild("aidaPlotterStyle"));
        entry = new StyleStoreEntry(name, style, rule);
        Class previewType = null;
        try {
            previewType = Class.forName(type);
        } catch (Exception e) { e.printStackTrace(); }
        if (previewType != null) entry.setPreviewType(previewType);
        return entry;
    }
    
    IStyleRule createRule(Element el) {
        IStyleRule rule = null;
        if (!el.getName().equals("aidaStyleRule"))
            throw new IllegalArgumentException("StyleStoreXMLReader.createRule :: wrong element name: "+el.getName()+" ... do nothing here");
        
        String value = el.getAttributeValue("ruleValue");
        String type = el.getAttributeValue("ruleType");
        
        rule = new JELRule(value);
        ((JELRule) rule).setType(type);
        return rule;
    }
    
    IPlotterStyle createStyle(Element el) {
        IPlotterStyle style = plotterFactory.createPlotterStyle();
        setStyle(style, el);
        return style;
    }
    
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
        String optionsString = el.getAttributeValue("attributeOptions");
        if (value != null && value.equalsIgnoreCase("null")) value = null;
        
        if (optionsString == null || optionsString.trim().equals("") || !(style instanceof BaseStyle))
            style.setParameter(name, value);
        else {
            String[] options = AidaUtils.parseString(optionsString);
            ((BaseStyle) style).setParameter(name, value, options);
        }
    }
    
}
