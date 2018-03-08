package hep.aida.util;

import hep.aida.IFitResult;
import hep.aida.IManagedObject;
import hep.aida.ITree;
import hep.aida.dev.IAddable;
import hep.aida.dev.IDevTree;
import hep.aida.ref.AidaUtils;
import hep.aida.ref.ContainerManagedObject;
import hep.aida.ref.tree.Tree;
import hep.aida.ref.xml.AIDAEntityResolver;
import hep.aida.ref.xml.AidaHandlerImpl;
import hep.aida.ref.xml.AidaParser;
import hep.aida.ref.xml.AidaXMLStore;
import hep.aida.ref.xml.AidaXMLWriter;
import java.io.ByteArrayInputStream;
import de.schlichtherle.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

/**
 *
 * @author The FreeHEP team @ SLAC.
 * @version $Id: XMLUtils.java 13343 2007-09-18 01:24:09Z serbo $
 */
public abstract class XMLUtils {
    
    /**
     *  Write AIDA Tree to an XML file
     *
     * Possible options and option values:
     *  compress :  zip, gzip, false
     *  binary :    true, false
     *  skip :      list of AIDA Type to skip when writing to file,
     *              Example:  skip=\"ITuple, ICloud2D\"
     */
    public static void writeToFile(ITree tree, String fileName, String options) throws IOException {
        Map optionsMap = AidaUtils.parseOptions(options);
        AidaXMLStore xmlStore = new AidaXMLStore();
        
        File file = new File(fileName);
        if (file.exists()) {
            throw new IOException("Output file already exists: "
                    + file.getAbsolutePath());
        }
        String cString = (String) optionsMap.get("compress");
        boolean zip = cString != null && cString.equalsIgnoreCase("zip");
        boolean compress = (cString == null) || cString.equalsIgnoreCase("yes")
        || cString.equalsIgnoreCase("true")
        || cString.equalsIgnoreCase("gzip");
        String bString = (String) optionsMap.get("binary");
        boolean binary = (bString != null)
        && (bString.equalsIgnoreCase("yes") || bString
                .equalsIgnoreCase("true"));
        String[] skip = null;
        if (optionsMap.get("skip") != null)
            skip = AidaUtils.parseString((String) optionsMap.get("skip"));
        
        xmlStore.commit(tree, file, skip, zip, compress, binary);
    }
    
    public static String createXMLString(IManagedObject mo) throws IOException {
        return createXMLString(mo, "");
    }
    
    public static String createXMLString(IManagedObject mo, ITree tree) throws IOException {
        return createXMLString(mo, tree.findPath(mo));
    }
    
    public static String createXMLString(IManagedObject mo, String path) throws IOException {
        return toXMLString(mo, path);
    }
    
    public static IManagedObject createManagedObject(String xmlString) throws IOException {
        Addable addable = new Addable();
        parse(xmlString, addable, null);
        return addable.object();
    }
    
    public static void addToTree(String xmlString, ITree tree) throws IOException {
        String systemID = tree.storeName();
        if (tree instanceof IDevTree) parse(xmlString, (IDevTree) tree, systemID);
        else
            throw new IOException("Wrong Tree type: "+tree);
    }
    
    
    // IFitResult
    
    public static String createXMLString(IFitResult fitResult) throws IOException {
        return createXMLString(fitResult, "/");
    }
    
    public static String createXMLString(IFitResult fitResult, String path) throws IOException {
        return toXMLString(fitResult, path);
    }
    
    public static IFitResult createFitResult(String xmlString) throws IOException {
        Addable addable = new Addable();
        IFitResult fitResult = null;
        try {
        parse(xmlString, addable, null);
        IManagedObject mo = addable.object();
        if (mo instanceof IFitResult) fitResult = (IFitResult) mo;
        else if (mo instanceof ContainerManagedObject) fitResult = (IFitResult) ((ContainerManagedObject) mo).getObject();
        } catch (Exception e) { e.printStackTrace(); }
        return fitResult;
    }

    
    // Service methods
    
    private static String toXMLString(Object obj, String path) throws IOException {
        AidaXMLWriter axw = null;
        StringWriter writer = new StringWriter();
        axw = new AidaXMLWriter(writer);
        if (obj instanceof IManagedObject) {
            axw.toXML((IManagedObject) obj, path);
        } else if (obj instanceof IFitResult) {
            axw.toXML((IFitResult) obj, path);
        }
        if (axw != null) {
            axw.close();
        }
        return writer.toString();
    }

    private static void parse(String xmlString, IAddable tree, String systemID) throws IOException {
        AidaHandlerImpl handler = new AidaHandlerImpl(tree, false);
        EntityResolver er = new AIDAEntityResolver(AidaParser.class,
                "http://aida.freehep.org/");
        AidaParser parser = new AidaParser(handler, er);
        parser.setValidate(false);
        
        ByteArrayInputStream in = new ByteArrayInputStream(xmlString.getBytes());        
        InputSource is = new InputSource(in);
        is.setSystemId(systemID);
        try {
            parser.parse(is);
        } catch (IOException ioe) {
            throw ioe;
        } catch (Exception e) {
            IOException ioe2 = new IOException();
            ioe2.initCause(e);
            throw ioe2;
        }
    }
    /*
    private static class Addable implements IAddable {
        private IManagedObject mo;
        private String path;
        
        private Addable() {
        }
        
        public void add(String path, IManagedObject mo) throws IllegalArgumentException {
            this.path = path;
            this.mo = mo;
        }
        
        public void hasBeenFilled(String path) throws IllegalArgumentException {
        }
        
        public void mkdirs(String path) throws IllegalArgumentException {
        }
        
        public String path() { return path; }
        
        public IManagedObject object() { return mo; }
    }
    */
}

