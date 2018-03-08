// Copyright 2000-2005, FreeHEP.
package hep.graphics.heprep.xml;

import hep.graphics.heprep.HepRep;
import hep.graphics.heprep.HepRepAction;
import hep.graphics.heprep.HepRepAttDef;
import hep.graphics.heprep.HepRepAttValue;
import hep.graphics.heprep.HepRepAttribute;
import hep.graphics.heprep.HepRepConstants;
import hep.graphics.heprep.HepRepDefinition;
import hep.graphics.heprep.HepRepInstance;
import hep.graphics.heprep.HepRepInstanceTree;
import hep.graphics.heprep.HepRepPoint;
import hep.graphics.heprep.HepRepTreeID;
import hep.graphics.heprep.HepRepType;
import hep.graphics.heprep.HepRepTypeTree;
import hep.graphics.heprep.HepRepWriter;
import hep.graphics.heprep.util.HepRepUtil;
import hep.graphics.heprep.wbxml.BHepRepWriter;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.freehep.util.io.NoCloseOutputStream;

/**
 *
 * @author M.Donszelmann
 *
 * @version $Id: XMLHepRepWriter.java 8584 2006-08-10 23:06:37Z duns $
 */

public class XMLHepRepWriter implements HepRepWriter {

    private static final String nameSpace = "heprep";
        
    // JHEPREP-17: deprecated now that we have a binary format.
    private boolean bitEncoding;        // flags if the doubles should be HexBit encoded
    
    private OutputStream cos;            // closeable stream
    private ZipOutputStream zip;        // zip stream if applicable
    private OutputStream out;           // pointer to top-most stream (zip, gzip or os)
    private XMLTagWriter xml;
    private long fileNumber;
    protected Map/*<String, String>*/ properties;


    XMLHepRepWriter(OutputStream os, boolean randomAccess, boolean compress) throws IOException {
        super();

        if (randomAccess) {
            zip = new ZipOutputStream(os);
            zip.setMethod(compress ? ZipOutputStream.DEFLATED : ZipOutputStream.STORED);
            cos = zip;
        } else if (compress) {
            cos = new GZIPOutputStream(os);
        } else {
            cos = os;
        }
        out = new NoCloseOutputStream(cos);   
        
        bitEncoding = false;
        properties = new HashMap();
        fileNumber = 1;
    }
/*
    XMLHepRepWriter(Writer out) {
        super();
        init(out);
    }
*/

    /**
     * Switches bitencoding
     * @param bitEncoding set bitencoding on
     * @deprecated use binary heprep (bheprep) instead.
     */
    public void setBitEncoding(boolean bitEncoding) {
        this.bitEncoding = bitEncoding;
        if (bitEncoding) System.err.println("setBitEncoding is deprecated, use binary heprep files instead.");
    }

    public void addProperty(String key, String value) throws IOException {
        properties.put(key, value);
    }

    public void close() throws IOException {
        if (zip != null) {
            zip.putNextEntry(new ZipEntry("heprep.properties"));
            PrintStream ps = new PrintStream(zip);
            for (Iterator i=properties.keySet().iterator(); i.hasNext(); ) {
                String key = (String)i.next();
                ps.println(key+"="+(String)properties.get(key));
            }
            zip.closeEntry();
            zip.close();
        }
        cos.close();
    }

    /**
     * Write any known type of HepRep Object. HepRep themselves are written as numbered bheprep files with name: File-#.bheprep
     * @param object anby HepRep object
     * @throws IOException in case of an I/O error
     */
    public void write(Object object) throws IOException {
        if (object instanceof HepRep) {
            write((HepRep)object, "File-"+fileNumber+".bheprep");
            fileNumber++;
        } else if (object instanceof HepRepTreeID) {
            write((HepRepTreeID)object);
        } else if (object instanceof List/*<String>*/) {
            write((List/*<String>*/)object);
        } else if (object instanceof HepRepAction) {
            write((HepRepAction)object);
        } else if (object instanceof HepRepTypeTree) {
            write((HepRepTypeTree)object);
        } else if (object instanceof HepRepType) {
            write((HepRepType)object);
        } else if (object instanceof HepRepInstanceTree) {
            write((HepRepInstanceTree)object);
        } else if (object instanceof HepRepInstance) {
            write((HepRepInstance)object);
        } else if (object instanceof HepRepPoint) {
            write((HepRepPoint)object);
        } else {
            System.err.println("XMLHepRepWriter: do not know how to write object of type: "+object.getClass());
        }
    }

    public void write(HepRep heprep, String name) throws IOException {
        if (zip != null) {
            zip.putNextEntry(new ZipEntry(name));
        }
        if (name.endsWith(".bheprep")) {
            xml = new BHepRepWriter(new BufferedOutputStream(out));
            bitEncoding = false;
        } else {
            xml = new ASCIIHepRepWriter(new BufferedWriter(new OutputStreamWriter(out)), "  ", nameSpace);
        }
        
        xml.openDoc();
        xml.setAttribute("version", "2.0");
        String schemaLocation = "http://java.freehep.org/schemas/heprep/2.0";
        xml.setAttribute("xmlns", schemaLocation);
        xml.setAttribute("xmlns", "xsi", "http://www.w3.org/2001/XMLSchema-instance");
        xml.setAttribute("xsi", "schemaLocation", schemaLocation+" "+schemaLocation+"/HepRep.xsd");
        xml.openTag(nameSpace, "heprep");
        write(heprep.getLayerOrder());
        for (Iterator i=heprep.getTypeTreeList().iterator(); i.hasNext(); ) {
            write((HepRepTypeTree)i.next());
        }
        for (Iterator i=heprep.getInstanceTreeList().iterator(); i.hasNext(); ) {
            write((HepRepInstanceTree)i.next());
        }
        xml.closeTag();
        xml.closeDoc();
        xml.close();
        
        if (zip != null) {
            zip.closeEntry();
        }
    }

    public void write(List/*<String>*/ layers) throws IOException {
        StringBuffer layerOrder = new StringBuffer();
        String layerName = null;
        for (Iterator i=layers.iterator(); i.hasNext(); ) {
            if (layerName != null) layerOrder.append(", ");
            layerName = (String)i.next();
            layerOrder.append(layerName);
        }
        xml.setAttribute("order", layerOrder.toString());
        xml.printTag(nameSpace, "layer");
    }

    public void write(HepRepTypeTree typeTree) throws IOException {
        xml.setAttribute("name", typeTree.getName());
        xml.setAttribute("version", typeTree.getVersion());
        xml.openTag(nameSpace, "typetree");
        for (Iterator i=typeTree.getTypeList().iterator(); i.hasNext(); ) {
            write((HepRepType)i.next());
        }
        xml.closeTag();
    }

    public void write(HepRepType type) throws IOException {
        if (type == null) return;
        xml.setAttribute("name", type.getName());
        xml.openTag(nameSpace, "type");
        write((HepRepDefinition)type);
        write((HepRepAttribute)type);
        for (Iterator i=type.getTypeList().iterator(); i.hasNext(); ) {
            write((HepRepType)i.next());
        }
        xml.closeTag();
    }

    public void write(HepRepTreeID treeID) throws IOException {
        xml.setAttribute("qualifier", treeID.getQualifier());
        xml.setAttribute("name", treeID.getName());
        xml.setAttribute("version", treeID.getVersion());
        xml.printTag(nameSpace, "treeid");
    }

    public void write(HepRepAction action) throws IOException {
        xml.setAttribute("name", action.getName());
        xml.setAttribute("expression", action.getExpression());
        xml.printTag(nameSpace, "action");
    }

    public void write(HepRepInstanceTree instanceTree) throws IOException {
        xml.setAttribute("name", instanceTree.getName());
        xml.setAttribute("version", instanceTree.getVersion());
        xml.setAttribute("typetreename", instanceTree.getTypeTree().getName());
        xml.setAttribute("typetreeversion", instanceTree.getTypeTree().getVersion());
        xml.openTag(nameSpace, "instancetree");

        // refs
        for (Iterator i=instanceTree.getInstanceTreeList().iterator(); i.hasNext(); ) {
            write((HepRepTreeID)i.next());
        }

        // instances
        for (Iterator i=instanceTree.getInstances().iterator(); i.hasNext(); ) {
            write((HepRepInstance)i.next());
        }
        xml.closeTag();
    }

    public void write(HepRepInstance instance) throws IOException {
        // FIXME JHEPREP-11
        xml.setAttribute("type", instance.getType().getFullName());
        xml.openTag(nameSpace, "instance");
        write((HepRepAttribute)instance);
        for (Iterator i=instance.getPoints().iterator(); i.hasNext(); ) {
            write((HepRepPoint)i.next());
        }
        for (Iterator i=instance.getInstances().iterator(); i.hasNext(); ) {
            write((HepRepInstance)i.next());
        }
        xml.closeTag();
    }

    public void write(HepRepPoint point) throws IOException {
        if (bitEncoding) {
            xml.setAttribute("x", encodeNumber(point.getX()));
            xml.setAttribute("y", encodeNumber(point.getY()));
            xml.setAttribute("z", encodeNumber(point.getZ()));
        } else {
            xml.setAttribute("x", point.getX());
            xml.setAttribute("y", point.getY());
            xml.setAttribute("z", point.getZ());
        }
        if (point.getAttValuesFromNode().iterator().hasNext()) {
            xml.openTag(nameSpace, "point");
            write((HepRepAttribute)point);
            xml.closeTag();
        } else {
            xml.printTag(nameSpace, "point");
        }
    }

    public void write(HepRepAttribute attribute) throws IOException {
        for (Iterator i = attribute.getAttValuesFromNode().iterator(); i.hasNext(); ) {
            HepRepAttValue attValue = (HepRepAttValue)i.next();
            write(attValue);
        }
    }

    public void write(HepRepDefinition definition) throws IOException {
        for (Iterator i = definition.getAttDefsFromNode().iterator(); i.hasNext(); ) {
            HepRepAttDef attDef = (HepRepAttDef)i.next();
            write(attDef);
        }
    }

    private String encodeNumber(double d) {
        return "0ds"+HepRepUtil.encodeSpecial(Double.doubleToLongBits(d));
//        return "0d0x"+Long.toHexString(Double.doubleToLongBits(d));
//        return "0d"+Double.doubleToLongBits(d);
    }

    public void write(HepRepAttValue attValue) throws IOException {
        String name = attValue.getName();
        xml.setAttribute("name", name);

        switch(attValue.getType()) {
            default:
                xml.setAttribute("value", attValue.getAsString());
                break;
            case HepRepAttValue.TYPE_STRING:
                xml.setAttribute("value", attValue.getString());
                break;
            case HepRepAttValue.TYPE_LONG:
                xml.setAttribute("value", attValue.getLong());
                break;
            case HepRepAttValue.TYPE_INT:
                xml.setAttribute("value", attValue.getInteger());
                break;
            case HepRepAttValue.TYPE_DOUBLE:
                if (bitEncoding) {
                    xml.setAttribute("value", encodeNumber(attValue.getDouble()));
                } else {
                    xml.setAttribute("value", attValue.getDouble());
                }
                break;
            case HepRepAttValue.TYPE_BOOLEAN:
                xml.setAttribute("value", attValue.getBoolean());
                break;
            case HepRepAttValue.TYPE_COLOR:
                xml.setAttribute("value", attValue.getColor());
                break;
        }
        
        if (attValue.showLabel() != HepRepConstants.SHOW_NONE) {
            xml.setAttribute("showlabel", attValue.showLabel());
        }
        xml.printTag(nameSpace, "attvalue");
    }

    public void write(HepRepAttDef attDef) throws IOException {
        xml.setAttribute("name", attDef.getName());
        xml.setAttribute("desc", attDef.getDescription());
        xml.setAttribute("category", attDef.getCategory());
        xml.setAttribute("extra", attDef.getExtra());
        xml.printTag(nameSpace, "attdef");
    }
}

