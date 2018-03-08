// Copyright 2000-2005, FreeHEP.

package hep.graphics.heprep1.xml;

import hep.graphics.heprep1.HepRep;
import hep.graphics.heprep1.HepRepAttDef;
import hep.graphics.heprep1.HepRepAttValue;
import hep.graphics.heprep1.HepRepAttribute;
import hep.graphics.heprep1.HepRepInstance;
import hep.graphics.heprep1.HepRepPoint;
import hep.graphics.heprep1.HepRepPrimitive;
import hep.graphics.heprep1.HepRepType;

import java.io.IOException;
import java.io.Writer;
import java.util.Enumeration;

import org.freehep.util.ScientificFormat;
import org.freehep.xml.util.XMLWriter;

/**
 *
 * @author M.Donszelmann
 *
 * @version $Id: XMLHepRepWriter.java 8584 2006-08-10 23:06:37Z duns $
 */

public class XMLHepRepWriter extends XMLWriter {

    private ScientificFormat scientific;

    /**
     * Create a HepRep Writer for writer
     * @param out writer
     */
    public XMLHepRepWriter(Writer out) {
        super(out);
        scientific = new ScientificFormat();
        openDoc();
    }

    public void close() throws IOException {
        closeDoc();
        super.close();
    }

    /**
     * Write heprep
     * @param root heprep
     */
    public void write(HepRep root) {
        setAttribute("xmlns:heprep", "http://www.freehep.org/HepRep");
        setAttribute("xmlns:xsi", "http://www.w3.org/1999/XMLSchema-instance");
        setAttribute("xsi:schemaLocation", "HepRep.xsd");
        openTag("heprep:heprep");
        write((HepRepAttribute)root);
        for (Enumeration e=root.getTypes(); e.hasMoreElements(); ) {
            HepRepType type = (HepRepType)e.nextElement();
            write(type);
        }
        closeTag();
    }

    /**
     * Write HepRep Type
     * @param type type
     */
    public void write(HepRepType type) {
        setAttribute("name", type.getName());
        setAttribute("version", type.getVersion());
        openTag("heprep:type");
        write((HepRepAttribute)type);
        for (Enumeration e=type.getTypes(); e.hasMoreElements(); ) {
            HepRepType subType = (HepRepType)e.nextElement();
            write(subType);
        }
        for (Enumeration e=type.getInstances(); e.hasMoreElements(); ) {
            HepRepInstance instance = (HepRepInstance)e.nextElement();
            write(instance);
        }
        closeTag();
    }

    /**
     * Write Instance   
     * @param instance instance
     */
    public void write(HepRepInstance instance) {
        openTag("heprep:instance");
        write((HepRepAttribute)instance);
        for (Enumeration e=instance.getTypes(); e.hasMoreElements(); ) {
            HepRepType type = (HepRepType)e.nextElement();
            write(type);
        }
        for (Enumeration e=instance.getPrimitives(); e.hasMoreElements(); ) {
            HepRepPrimitive primitive = (HepRepPrimitive)e.nextElement();
            write(primitive);
        }
        closeTag();
    }

    /**
     * Write Primitive
     * @param primitive primitive
     */
    public void write(HepRepPrimitive primitive) {
        openTag("heprep:primitive");
        write((HepRepAttribute)primitive);
        for (Enumeration e=primitive.getPoints(); e.hasMoreElements(); ) {
            HepRepPoint point = (HepRepPoint)e.nextElement();
            write(point);
        }
        closeTag();
    }

    /**
     * Write HepRep Point
     * @param point point
     */
    public void write(HepRepPoint point) {
        setAttribute("x", scientific.format(point.getX()));
        setAttribute("y", scientific.format(point.getY()));
        setAttribute("z", scientific.format(point.getZ()));
        openTag("heprep:point");
        write((HepRepAttribute)point);
        closeTag();
    }

    private String labelStrings[] = {"NAME", "DESC", "VALUE", "EXTRA"};

// FIXME: maybe write the bitflags as hex?
    /**
     * Write attribute value
     * @param attValue attribute value
     */
    public void write(HepRepAttValue attValue) {
        setAttribute("name", attValue.getName());
// FIXME: will not work since there is not type in the attValue.
//        if (attValue.getType() == HepRepAttValue.TYPE_DOUBLE) {
//            setAttribute("value", scientific.format(attValue.getDouble()));            
//        } else {
            setAttribute("value", attValue.getString());
//        }
        String label = null;
        int showLabel = attValue.showLabel();
        if (showLabel == HepRepAttValue.SHOW_NONE) {
            label = "NONE";
        } else {
            for (int i=0; i<16; i++) {
                if (((showLabel >> i) & 0x0001) == 0x0001) {
                    if (label == null) {
                        label = "";
                    } else {
                        label += ", ";
                    }
                    if (i < labelStrings.length) {
                        label += labelStrings[i];
                    } else {
                        label += "0x"+Integer.toHexString(0x0001 << i);
                    }
                }
            }
        }
        setAttribute("showLabel", label);
        printTag("heprep:attvalue");
    }

    /**
     * Write attribute definition
     * @param attDef attribute definition
     */
    public void write(HepRepAttDef attDef) {
        setAttribute("name", attDef.getName());
        setAttribute("desc", attDef.getDescription());
        setAttribute("type", attDef.getType());
        setAttribute("extra", attDef.getExtra());
        printTag("heprep:attdef");
    }

    /**
     * Write Attribute
     * @param attribute attribute
     */
    public void write(HepRepAttribute attribute) {
        for (Enumeration e=attribute.getAttValues(); e.hasMoreElements(); ) {
            HepRepAttValue attValue = (HepRepAttValue)e.nextElement();
            write(attValue);
        }
        for (Enumeration e=attribute.getAttDefs(); e.hasMoreElements(); ) {
            HepRepAttDef attDef = (HepRepAttDef)e.nextElement();
            write(attDef);
        }
    }

}

