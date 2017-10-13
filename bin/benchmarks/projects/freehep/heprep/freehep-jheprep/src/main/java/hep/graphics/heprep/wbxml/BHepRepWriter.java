// Copyright FreeHEP, 2005.
package hep.graphics.heprep.wbxml;
                        
import java.awt.Color;
import java.io.*;                        
import java.util.*;
                        
import org.kxml2.wap.Wbxml;

import hep.graphics.heprep.xml.XMLTagWriter;

/**
 * @author Mark Donszelmann
 * @version $Id: BHepRepWriter.java 8584 2006-08-10 23:06:37Z duns $
 */
public class BHepRepWriter implements XMLTagWriter, Wbxml {

    /**
     * WBXML version number
     */
    public static final int WBXML_VERSION    = 0x03;
    /**
     * WBXML Constant for Unknown PID
     */
    public static final int UNKNOWN_PID      = 0x01;
    /**
     * WBXML Constant for UTF 8
     */
    public static final int UTF8             = 0x6a;

    /**
     * Content Mask 
     */
    public static final int CONTENT          = 0x40;
    /**
     * Attribute Mask
     */
    public static final int ATTRIBUTE        = 0x80;

    /**
     * BHepRep String Define code
     */
    public static final int STR_D            = EXT_I_0;
    /**
     * BHepRep String Reference code
     */
    public static final int STR_R            = EXT_T_0;

    private static Map/*<String, String>*/ tags;
    private static Map/*<String, String>*/ attributes;
    private static Map/*<String, String>*/ values;

    static {
        // tags
        tags = new HashMap();
        tags.put("heprep"              , new Integer(0x05));
        tags.put("attdef"              , new Integer(0x06));
        tags.put("attvalue"            , new Integer(0x07));
        tags.put("instance"            , new Integer(0x08));
        tags.put("treeid"              , new Integer(0x09));
        tags.put("action"              , new Integer(0x0a));
        tags.put("instancetree"        , new Integer(0x0b));
        tags.put("type"                , new Integer(0x0c));
        tags.put("typetree"            , new Integer(0x0d));
        tags.put("layer"               , new Integer(0x0e));
        tags.put("point"               , new Integer(0x0f));

        // attribute names
        attributes = new HashMap();
        attributes.put("version"               , new Integer(0x05));
        attributes.put("xmlns"                 , new Integer(0x06));
        attributes.put("xmlns:xsi"             , new Integer(0x07));
        attributes.put("xsi:schemaLocation"    , new Integer(0x08));

        attributes.put("valueString"           , new Integer(0x10));
        attributes.put("valueColor"            , new Integer(0x11));
        attributes.put("valueLong"             , new Integer(0x12));
        attributes.put("valueInt"              , new Integer(0x13));
        attributes.put("valueBoolean"          , new Integer(0x14));
        attributes.put("valueDouble"           , new Integer(0x15));

        attributes.put("name"                  , new Integer(0x20));
        attributes.put("type"                  , new Integer(0x22));
        attributes.put("showlabel"             , new Integer(0x23));
        attributes.put("desc"                  , new Integer(0x24));
        attributes.put("category"              , new Integer(0x25));
        attributes.put("extra"                 , new Integer(0x26));
        attributes.put("x"                     , new Integer(0x27));
        attributes.put("y"                     , new Integer(0x28));
        attributes.put("z"                     , new Integer(0x29));
        attributes.put("qualifier"             , new Integer(0x2a));
        attributes.put("expression"            , new Integer(0x2b));
        attributes.put("typetreename"          , new Integer(0x2c));
        attributes.put("typetreeversion"       , new Integer(0x2d));
        attributes.put("order"                 , new Integer(0x2e));
        
        // for PI
        attributes.put("eof"                   , new Integer(0x7f));
        
        // attribute values        
        values = new HashMap();
        values.put("drawas"                     , new Integer(0x85));
        values.put("drawasoptions"              , new Integer(0x86));
        values.put("visibility"                 , new Integer(0x87));

        values.put("label"                      , new Integer(0x88));

        values.put("fontname"                   , new Integer(0x89));
        values.put("fontstyle"                  , new Integer(0x8a));
        values.put("fontsize"                   , new Integer(0x8b));
        values.put("fontcolor"                  , new Integer(0x8c));
        values.put("fonthasframe"               , new Integer(0x8d));
        values.put("fontframecolor"             , new Integer(0x8e));
        values.put("fontframewidth"             , new Integer(0x8f));
        values.put("fonthasbanner"              , new Integer(0x90));
        values.put("fontbannercolor"            , new Integer(0x91));

        values.put("color"                      , new Integer(0x92));
        values.put("framecolor"                 , new Integer(0x93));
        values.put("layer"                      , new Integer(0x94));
        values.put("markname"                   , new Integer(0x95));
        values.put("marksize"                   , new Integer(0x96));
        values.put("marksizemultiplier"         , new Integer(0x97));
        values.put("marktype"                   , new Integer(0x98));
        values.put("hasframe"                   , new Integer(0x99));
        values.put("framecolor"                 , new Integer(0x9a));
        values.put("framewidth"                 , new Integer(0x9b));

        values.put("linestyle"                  , new Integer(0x9c));
        values.put("linewidth"                  , new Integer(0x9d));
        values.put("linewidthmultiplier"        , new Integer(0x9e));
        values.put("linehasarrow"               , new Integer(0x9f));
        
        values.put("fillcolor"                  , new Integer(0xa0));
        values.put("filltype"                   , new Integer(0xa1));
        values.put("fill"                       , new Integer(0xa2));

        values.put("radius"                     , new Integer(0xa3));
        values.put("phi"                        , new Integer(0xa4));
        values.put("theta"                      , new Integer(0xa5));
        values.put("omega"                      , new Integer(0xa6));
        values.put("radius1"                    , new Integer(0xa7));
        values.put("radius2"                    , new Integer(0xa8));
        values.put("radius3"                    , new Integer(0xa9));
        values.put("curvature"                  , new Integer(0xaa));
        values.put("flylength"                  , new Integer(0xab));
        values.put("faces"                      , new Integer(0xac));

        values.put("text"                       , new Integer(0xad));
        values.put("hpos"                       , new Integer(0xae));
        values.put("vpos"                       , new Integer(0xaf));
        values.put("halign"                     , new Integer(0xb0));
        values.put("valign"                     , new Integer(0xb1));

        values.put("ispickable"                 , new Integer(0xb2));
        values.put("showparentvalues"           , new Integer(0xb3));
        values.put("pickparent"                 , new Integer(0xb4));
        
        // attvalue values
        values.put("false"         , new Integer(0xd0));
        values.put("true"          , new Integer(0xd1));
        
        values.put("point"         , new Integer(0xd2));
        values.put("line"          , new Integer(0xd3));
        values.put("helix"         , new Integer(0xd4));
        values.put("polygon"       , new Integer(0xd5));
        values.put("circle"        , new Integer(0xd6));
        values.put("curve"         , new Integer(0xd7));
        values.put("ellipse"       , new Integer(0xd8));
        values.put("ellipsoid"     , new Integer(0xd9));
        values.put("prism"         , new Integer(0xda));
        values.put("cylinder"      , new Integer(0xdb));
        values.put("ellipseprism"  , new Integer(0xdc));
        values.put("text"          , new Integer(0xdd));

        values.put("nonzero"       , new Integer(0xde));
        values.put("evenodd"       , new Integer(0xdf));
        
        values.put("circle"        , new Integer(0xe0));
        values.put("box"           , new Integer(0xe1));
        values.put("uptriangle"    , new Integer(0xe2));
        values.put("dntriangle"    , new Integer(0xe3));
        values.put("diamond"       , new Integer(0xe4));
        values.put("cross"         , new Integer(0xe5));
        values.put("star"          , new Integer(0xe6));
        values.put("plus"          , new Integer(0xe7));
        values.put("hline"         , new Integer(0xe8));
        values.put("vline"         , new Integer(0xe9));

        values.put("solid"         , new Integer(0xea));
        values.put("dotted"        , new Integer(0xeb));
        values.put("dashed"        , new Integer(0xec));
        values.put("dotdash"       , new Integer(0xed));
        
        values.put("none"          , new Integer(0xee));
        values.put("start"         , new Integer(0xef));
        values.put("end"           , new Integer(0xf0));
        values.put("both"          , new Integer(0xf1));

        values.put("serif"         , new Integer(0xf2));
        values.put("sansserif"     , new Integer(0xf3));
        values.put("monotype"      , new Integer(0xf4));
        values.put("symbol"        , new Integer(0xf5));

        values.put("plain"         , new Integer(0xf6));
        values.put("bold"          , new Integer(0xf7));
        values.put("italic"        , new Integer(0xf8));

        values.put("top"           , new Integer(0xf9));
        values.put("baseline"      , new Integer(0xfa));
        values.put("center"        , new Integer(0xfb));
        values.put("bottom"        , new Integer(0xfc));

        values.put("left"          , new Integer(0xfd));
        values.put("right"         , new Integer(0xfe));

        values.put("default"       , new Integer(0xff));
    }

    // outputstream variables
    private DataOutputStream os;
    private boolean singlePrecision;

    // document variables            
    private Map/*<String, Integer>*/ stringValues;

    // tag variables
    Map/*<String, String>*/ stringAttributes;
    Map/*<String, Color>*/ colorAttributes;
    Map/*<String, Long>*/ longAttributes;
    Map/*<String, Integer>*/ intAttributes;
    Map/*<String, Boolean>*/ booleanAttributes;
    Map/*<String, Double>*/ doubleAttributes;
    
    // point array
    List/*<Double>*/ points;

    /**
     * Create a Binary HepRep Writer for given stream
     * @param os stream to write to
     */
    public BHepRepWriter(OutputStream os) {
        this.os = (os instanceof DataOutputStream) ? (DataOutputStream)os : new DataOutputStream(os);
        singlePrecision = true;         
        stringValues = new HashMap();

        stringAttributes = new HashMap();
        colorAttributes = new HashMap();
        longAttributes = new HashMap();
        intAttributes = new HashMap();
        booleanAttributes = new HashMap();
        doubleAttributes = new HashMap();

        points = new ArrayList();                          
    }

    public void openTag(String ns, String name) throws IOException {
        openTag(ns.equals("heprep") ? name : ns+":"+name);
    }
    
    public void printTag(String ns, String name) throws IOException {
        openTag(ns.equals("heprep") ? name : ns+":"+name);
    }

    public void setAttribute(String ns, String name, String value) {
        setAttribute(ns.equals("heprep") ? name : ns+":"+name, value);
    }
    
    public void setAttribute(String ns, String name, double value) {
        setAttribute(ns.equals("heprep") ? name : ns+":"+name, value);
    }

    public void close() throws IOException {
    }
    
    public void openDoc() throws IOException {
        openDoc("BinaryHepRep/1.0", "UTF-8", false);
    }
    
    public void openDoc(String version, String encoding, boolean standalone) throws IOException {
        stringValues.clear();
        
        // header
        writeByte(WBXML_VERSION);
        writeMultiByteInt(UNKNOWN_PID);
        writeMultiByteInt(UTF8);        
        
        version = "BinaryHepRep/1.0"; 
       
        // string table
        writeMultiByteInt(version.length()+1);
        
        // BHepRep Header (as part of the string table)
        writeString(version);
        
    }
    
    public void closeDoc() throws IOException {
        writeByte(PI);
        writeByte((Integer)attributes.get("eof"));
        writeByte(END);
    }

    public void openTag(String name) throws IOException {
        writeTag(name, true);
    }
    
    public void closeTag() throws IOException {
        writePoints();
        writeByte(END);
    }
    
    public void printTag(String name) throws IOException {
        writeTag(name, false);
    }
    
    private void writeTag(String tagName, boolean hasContent) throws IOException {
        String s = tagName.toLowerCase();
        
        // find tag
        Integer tag = (Integer)tags.get(s);
        if (tag == null) {
            throw new IOException("Cannot find tag '" + s + "' in tags table.");
        }
        
        // write tag
        boolean isPoint = s.equals("point");
        boolean hasAttributes = (stringAttributes.size() > 0) || (doubleAttributes.size() > (isPoint ? 3 : 0));
        
        if (!hasAttributes && isPoint) {
            // store the point for the future
            points.add((Double)doubleAttributes.get("x"));
            points.add((Double)doubleAttributes.get("y"));
            points.add((Double)doubleAttributes.get("z"));
            return;
        }

        writePoints();
        writeByte(tag.intValue() | ((hasContent || isPoint) ? CONTENT : 0x00) | (hasAttributes ? ATTRIBUTE : 0x00));        
            
        // write attributes
        if (hasAttributes) {
            // write string attributes
	        for (Iterator i = stringAttributes.keySet().iterator(); i.hasNext(); ) {
    		    String name = (String)i.next();
    		    String value = (String)stringAttributes.get(name);

                // write ATTRSTART
                writeByte((Integer)attributes.get(name));
                String v = value.toLowerCase();
                if (values.get(v) != null) {
                    // write ATTRVALUE
                    writeByte((Integer)values.get(v));
                } else {
                    if (stringValues.get(value) == null) {
                        // define this new string
                        writeStringDefine(value);
                        int index = stringValues.size();
                        stringValues.put(value, new Integer(index));
                    } else {
                        // write string ref
                        writeByte(STR_R);
                        writeMultiByteInt((Integer)stringValues.get(value));    
                    }
                }
            }
    	    stringAttributes.clear();   	     

            // write color attributes
	        for (Iterator i = colorAttributes.keySet().iterator(); i.hasNext(); ) {
    		    String name = (String)i.next();
    		    Color value = (Color)colorAttributes.get(name);
                // write ATTRSTART
                writeByte((Integer)attributes.get(name));
                // write OPAQUE
                writeByte(OPAQUE);
                writeMultiByteInt(value.getAlpha() < 0xFF ? 4 : 3);
                writeByte(value.getRed());
                writeByte(value.getGreen());
                writeByte(value.getBlue());
                if (value.getAlpha() < 0xFF) writeByte(value.getAlpha());
            }
    	    colorAttributes.clear();
    	    
            // write long attributes
	        for (Iterator i = longAttributes.keySet().iterator(); i.hasNext(); ) {
    		    String name = (String)i.next();
    		    Long value = (Long)longAttributes.get(name);
                 // write ATTRSTART
                writeByte((Integer)attributes.get(name));
                // write OPAQUE
                writeByte(OPAQUE);
                writeMultiByteInt(8);
                writeLong(value);
            }
    	    longAttributes.clear();
    	    
            // write int attributes
	        for (Iterator i = intAttributes.keySet().iterator(); i.hasNext(); ) {
    		    String name = (String)i.next();
    		    Integer value = (Integer)intAttributes.get(name);
                // write ATTRSTART
                writeByte((Integer)attributes.get(name));
                // write OPAQUE
                writeByte(OPAQUE);
                writeMultiByteInt(4);
                writeInt(value);
            }
    	    intAttributes.clear();
    	    
            // write boolean attributes
	        for (Iterator i = booleanAttributes.keySet().iterator(); i.hasNext(); ) {
    		    String name = (String)i.next();
    		    Boolean value = (Boolean)booleanAttributes.get(name);
                // write ATTRSTART
                writeByte((Integer)attributes.get(name));
                // write ATTRVALUE
                writeByte(value.booleanValue() ? (Integer)values.get("true") : (Integer)values.get("false"));
            }
    	    booleanAttributes.clear();
    	    
            // write double attributes
	        for (Iterator i = doubleAttributes.keySet().iterator(); i.hasNext(); ) {
    		    String name = (String)i.next();
    		    Double value = (Double)doubleAttributes.get(name);
    		    if (!isPoint && !name.equals("x") && !name.equals("y") && !name.equals("z")) {
                    // write ATTRSTART
                    writeByte((Integer)attributes.get(name));
                    // write OPAQUE
                    writeByte(OPAQUE);
                    writeMultiByteInt(singlePrecision ? 4 : 8);
                    writeReal(value);
                }        
            }
    	    doubleAttributes.clear();
    	    
    	    // end of attributes
    	    writeByte(END);   	     
	    }
        
        if (s == "point") {
            writeByte(OPAQUE);
            writeMultiByteInt(singlePrecision ?  12 : 24);
            writeReal((Double)doubleAttributes.get("x"));
            writeReal((Double)doubleAttributes.get("y"));
            writeReal((Double)doubleAttributes.get("z"));
        }
        
        if (isPoint && !hasContent) {
            // end this tag
            writeByte(END);
        }    
    }
    
    private void writePoints() throws IOException {
        if (points.size() <= 0) return;
        
        writeByte(((Integer)tags.get("point")).intValue() | CONTENT);                
        writeByte(OPAQUE);
        writeMultiByteInt(points.size()*(singlePrecision ? 4 : 8));
        for (Iterator i = points.iterator(); i.hasNext(); ) {
            writeReal((Double)i.next());
        }
        writeByte(END);
        
        points.clear();
    }
        
    public void setAttribute(String name, String value) {
        if (name.equals("value")) name += "String";
                
        // make sure the attribute name is defined
        if (attributes.get(name) == null) {
            throw new RuntimeException("Cannot find attribute name '" + name + "' in attributes table.");
        }
                    
        stringAttributes.put(name, value);
    }

    public void setAttribute(String name, Color value) {
        if (name.equals("value")) name += "Color";

        // make sure the attribute name is defined
        if (attributes.get(name) == null) {
            throw new RuntimeException("Cannot find attribute name '" + name + "' in attributes table.");
        }
                    
        colorAttributes.put(name, value);
    }
    
    public void setAttribute(String name, long value) {
        if (name.equals("value")) name += "Long";

        // make sure the attribute name is defined
        if (attributes.get(name) == null) {
            throw new RuntimeException("Cannot find attribute name '" + name + "' in attributes table.");
        }
                    
        longAttributes.put(name, new Long(value));
    }
    
    public void setAttribute(String name, int value) {
        if (name.equals("value")) name += "Int";

        // make sure the attribute name is defined
        if (attributes.get(name) == null) {
            throw new RuntimeException("Cannot find attribute name '" + name + "' in attributes table.");
        }
                    
        intAttributes.put(name, new Integer(value));
    }
    
    public void setAttribute(String name, boolean value) {
        if (name.equals("value")) name += "Boolean";

        // make sure the attribute name is defined
        if (attributes.get(name) == null) {
            throw new RuntimeException("Cannot find attribute name '" + name + "' in attributes table.");
        }
                    
        booleanAttributes.put(name, new Boolean(value));
    }
    
    public void setAttribute(String name, double value) {
        if (name.equals("value")) name += "Double";

        // make sure the attribute name is defined
        if (attributes.get(name) == null) {
            throw new RuntimeException("Cannot find attribute name '" + name + "' in attributes table.");
        }
                    
        doubleAttributes.put(name, new Double(value));
    }

    private void writeStringDefine(String s) throws IOException {
        writeByte(STR_D);
        writeString(s);
    }

    private void writeMultiByteInt(Number n) throws IOException {
        writeMultiByteInt(n.longValue());
    }
    
    private void writeMultiByteInt(long ui) throws IOException {
        int buf[] = new int[5];
        int idx = 0;
        
        do {
            buf[idx++] = (int) (ui & 0x7f);
            ui = ui >> 7;
        } while (ui != 0);

        while (idx > 1) {
            writeByte(buf[--idx] | 0x80);
        }
        writeByte(buf[0]);
    }

    private void writeReal(Double d) throws IOException {
        if (singlePrecision) {
            os.writeFloat(d.floatValue());
        } else {
            os.writeDouble(d.doubleValue());
        }
    }

    private void writeLong(Long i) throws IOException {        
        os.writeLong(i.longValue());
    }    
    
    private void writeInt(Integer i) throws IOException {
        os.writeInt(i.intValue());
    }
    
    private void writeByte(Integer b) throws IOException {
        writeByte(b.intValue());
    }
    
    private void writeByte(int b) throws IOException {
        os.writeByte(b);
    }
    
    private void writeString(String s) throws IOException {
        os.writeBytes(s);
        os.writeByte(0);
    }
}
