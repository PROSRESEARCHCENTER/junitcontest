// Copyright 2005, FreeHEP.
package hep.graphics.heprep.wbxml;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.kxml2.wap.Wbxml;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;


/**
 * Binary HepRep Parser
 * 
 * @author Mark Donszelmann
 * @version $Id: BHepRepParser.java 8584 2006-08-10 23:06:37Z duns $
 */
public class BHepRepParser extends WbxmlParser {

    protected final static int HEPREP         = 0x05;
    protected final static int ATTDEF         = 0x06;
    protected final static int ATTVALUE       = 0x07;
    protected final static int INSTANCE       = 0x08;
    protected final static int TREEID         = 0x09;
    protected final static int ACTION         = 0x0a;
    protected final static int INSTANCETREE   = 0x0b;
    protected final static int TYPE           = 0x0c;
    protected final static int TYPETREE       = 0x0d;
    protected final static int LAYER          = 0x0e;
    protected final static int POINT          = 0x0f;

    private final static String[] tags = {
        "heprep",              // 0x05          NOTE: same as above
        "attdef",              // 0x06          NOTE: same as above
        "attvalue",            // 0x07          NOTE: same as above 
        "instance",            // 0x08          NOTE: same as above
        "treeid",              // 0x09          NOTE: same as above
        "action",              // 0x0a          NOTE: same as above
        "instancetree",        // 0x0b          NOTE: same as above
        "type",                // 0x0c          NOTE: same as above
        "typetree",            // 0x0d          NOTE: same as above
        "layer",               // 0x0e          NOTE: same as above
        "point"                // 0x0f          NOTE: same as above
    };

    protected final static int VALUE_STRING     = 0x10;
    protected final static int VALUE_COLOR      = 0x11;
    protected final static int VALUE_LONG       = 0x12;
    protected final static int VALUE_INT        = 0x13;
    protected final static int VALUE_BOOLEAN    = 0x14;
    protected final static int VALUE_DOUBLE     = 0x15;
    
    protected final static int EOF              = 0x7f;

    private final static String[] attributes = {
        "version",               // 0x05
        "xmlns",                 // 0x06
        "xmlns:xsi",             // 0x07
        "xsi:schemaLocation",    // 0x08
        null,                    // 0x09
        null,                    // 0x0a
        null,                    // 0x0b
        null,                    // 0x0c
        null,                    // 0x0d
        null,                    // 0x0e
        null,                    // 0x0f
        "valueString",           // 0x10        NOTE: same as above
        "valueColor",            // 0x11        NOTE: same as above
        "valueLong",             // 0x12        NOTE: same as above
        "valueInt",              // 0x13        NOTE: same as above
        "valueBoolean",          // 0x14        NOTE: same as above
        "valueDouble",           // 0x15        NOTE: same as above
        null,                    // 0x16
        null,                    // 0x17
        null,                    // 0x18
        null,                    // 0x19
        null,                    // 0x1a
        null,                    // 0x1b
        null,                    // 0x1c
        null,                    // 0x1d
        null,                    // 0x1e
        null,                    // 0x1f    
        "name",                  // 0x20
        null,                    // 0x21
        "type",                  // 0x22
        "showlabel",             // 0x23
        "desc",                  // 0x24
        "category",              // 0x25
        "extra",                 // 0x26
        "x",                     // 0x27
        "y",                     // 0x28
        "z",                     // 0x29
        "qualifier",             // 0x2a
        "expression",            // 0x2b
        "typetreename",          // 0x2c
        "typetreeversion",       // 0x2d
        "order",                 // 0x2e
        null,                    // 0x2f
        null,                    // 0x30
        null,                    // 0x31
        null,                    // 0x32
        null,                    // 0x33
        null,                    // 0x34
        null,                    // 0x35
        null,                    // 0x36
        null,                    // 0x37
        null,                    // 0x38
        null,                    // 0x39
        null,                    // 0x3a
        null,                    // 0x3b
        null,                    // 0x3c
        null,                    // 0x3d
        null,                    // 0x3e
        null,                    // 0x3f
        null,                    // 0x40
        null,                    // 0x41
        null,                    // 0x42
        null,                    // 0x43
        null,                    // 0x44
        null,                    // 0x45
        null,                    // 0x46
        null,                    // 0x47
        null,                    // 0x48
        null,                    // 0x49
        null,                    // 0x4a
        null,                    // 0x4b
        null,                    // 0x4c
        null,                    // 0x4d
        null,                    // 0x4e
        null,                    // 0x4f
        null,                    // 0x50
        null,                    // 0x51
        null,                    // 0x52
        null,                    // 0x53
        null,                    // 0x54
        null,                    // 0x55
        null,                    // 0x56
        null,                    // 0x57
        null,                    // 0x58
        null,                    // 0x59
        null,                    // 0x5a
        null,                    // 0x5b
        null,                    // 0x5c
        null,                    // 0x5d
        null,                    // 0x5e
        null,                    // 0x5f
        null,                    // 0x60
        null,                    // 0x61
        null,                    // 0x62
        null,                    // 0x63
        null,                    // 0x64
        null,                    // 0x65
        null,                    // 0x66
        null,                    // 0x67
        null,                    // 0x68
        null,                    // 0x69
        null,                    // 0x6a
        null,                    // 0x6b
        null,                    // 0x6c
        null,                    // 0x6d
        null,                    // 0x6e
        null,                    // 0x6f
        null,                    // 0x70
        null,                    // 0x71
        null,                    // 0x72
        null,                    // 0x73
        null,                    // 0x74
        null,                    // 0x75
        null,                    // 0x76
        null,                    // 0x77
        null,                    // 0x78
        null,                    // 0x79
        null,                    // 0x7a
        null,                    // 0x7b
        null,                    // 0x7c
        null,                    // 0x7d
        null,                    // 0x7e
        "eof"                    // 0x7f        NOTE: same as above
    };

    private final static String[] values = {
        // attvalue keys
        "drawas",                     // 0x85
        "drawasoptions",              // 0x86
        "visibility",                 // 0x87
    
        "label",                      // 0x88
    
        "fontname",                   // 0x89
        "fontstyle",                  // 0x8a
        "fontsize",                   // 0x8b
        "fontcolor",                  // 0x8c
        "fonthasframe",               // 0x8d
        "fontframecolor",             // 0x8e
        "fontframewidth",             // 0x8f
        "fonthasbanner",              // 0x90
        "fontbannercolor",            // 0x91
    
        "color",                      // 0x92
        "framecolor",                 // 0x93
        "layer",                      // 0x94
        "markname",                   // 0x95
        "marksize",                   // 0x96
        "marksizemultiplier",         // 0x97
        "marktype",                   // 0x98
        "hasframe",                   // 0x99
        "framecolor",                 // 0x9a
        "framewidth",                 // 0x9b
    
        "linestyle",                  // 0x9c
        "linewidth",                  // 0x9d
        "linewidthmultiplier",        // 0x9e
        "linehasarrow",               // 0x9f
            
        "fillcolor",                  // 0xa0
        "filltype",                   // 0xa1
        "fill",                       // 0xa2
    
        "radius",                     // 0xa3
        "phi",                        // 0xa4
        "theta",                      // 0xa5
        "omega",                      // 0xa6
        "radius1",                    // 0xa7
        "radius2",                    // 0xa8
        "radius3",                    // 0xa9
        "curvature",                  // 0xaa
        "flylength",                  // 0xab
        "faces",                      // 0xac
    
        "text",                       // 0xad
        "hpos",                       // 0xae
        "vpos",                       // 0xaf
        "halign",                     // 0xb0
        "valign",                     // 0xb1
    
        "ispickable",                 // 0xb2
        "showparentvalues",           // 0xb3
        "pickparent",                 // 0xb4
    
        null,                   // 0xb5
        null,                   // 0xb6
        null,                   // 0xb7
        null,                   // 0xb8
        null,                   // 0xb9
        null,                   // 0xba
        null,                   // 0xbb
        null,                   // 0xbc
        null,                   // 0xbd
        null,                   // 0xbe
        null,                   // 0xbf
        null,                   // 0xc0
        null,                   // 0xc1
        null,                   // 0xc2
        null,                   // 0xc3
        null,                   // 0xc4
        null,                   // 0xc5
        null,                   // 0xc6
        null,                   // 0xc7
        null,                   // 0xc8
        null,                   // 0xc9
        null,                   // 0xca
        null,                   // 0xcb
        null,                   // 0xcc
        null,                   // 0xcd
        null,                   // 0xce
        null,                   // 0xcf
            
        // attvalue values
        "false",         // 0xd0
        "true",          // 0xd1
            
        "point",         // 0xd2
        "line",          // 0xd3
        "helix",         // 0xd4
        "polygon",       // 0xd5
        "circle",        // 0xd6
        "curve",         // 0xd7
        "ellipse",       // 0xd8
        "ellipsoid",     // 0xd9
        "prism",         // 0xda
        "cylinder",      // 0xdb
        "ellipseprism",  // 0xdc
        "text",          // 0xdd
    
        "nonzero",       // 0xde
        "evenodd",       // 0xdf
            
        "circle",        // 0xe0
        "box",           // 0xe1
        "uptriangle",    // 0xe2
        "dntriangle",    // 0xe3
        "diamond",       // 0xe4
        "cross",         // 0xe5
        "star",          // 0xe6
        "plus",          // 0xe7
        "hline",         // 0xe8
        "vline",         // 0xe9
    
        "solid",         // 0xea
        "dotted",        // 0xeb
        "dashed",        // 0xec
        "dotdash",       // 0xed
            
        "none",          // 0xee
        "start",         // 0xef
        "end",           // 0xf0
        "both",          // 0xf1
    
        "serif",         // 0xf2
        "sansserif",     // 0xf3
        "monotype",      // 0xf4
        "symbol",        // 0xf5
    
        "plain",         // 0xf6
        "bold",          // 0xf7
        "italic",        // 0xf8
    
        "top",           // 0xf9
        "baseline",      // 0xfa
        "center",        // 0xfb
        "bottom",        // 0xfc
    
        "left",          // 0xfd
        "right",         // 0xfe
    
        "default",       // 0xff
    };
 
    private List strings = new ArrayList();
    
	/**
	 * Create a Binary HepRep Parser
	 */
	public BHepRepParser() {
	    super();
	 
	    // wbxml settings   		
        setTagTable(0, tags);
        setAttrStartTable(0, attributes);
        setAttrValueTable(0, values);
    }

    protected void processInstruction() throws IOException, XmlPullParserException  {
        super.processInstruction();
        if (getAttributeValue("eof") != null) {
            type = XmlPullParser.END_DOCUMENT;
        }
    }
	
    protected Object parseExtension(int id, int tagId, int attId) throws IOException, XmlPullParserException {
        
        Object param = super.parseExtension(id, tagId, attId);
        
        switch(id) {
            case Wbxml.EXT_I_0:  // STR_D
                strings.add(param.toString());
                break;
            case Wbxml.EXT_T_0:  // STR_R
                param = strings.get(((Integer)param).intValue());
                break;
/*
            case Wbxml.EXT_0:    // END_BHEPREP;
                param = null;
                break;
*/
            default:
                // ignored
                break;
        }
        
        return param;
    }

    private double[] points = new double[32];

    protected Object parseOpaque(int len, int tagId, int attId) throws IOException, XmlPullParserException {
        switch (tagId) {
            case 0:
                // multiple of 3 floating point coordinates
                if ((len % 12) == 0) {
                    // CONTENT of point (NOTE tagId not set at top-level)
                    // NOTE: single precision only
                    int n = len/4;
                    if (n > points.length) {
                        points = new double[Math.max(points.length*2, n) ];
                    }
                    for (int i=0; i<n; i++) {
                        points[i] = readFloat();
                    }
                    return points;    
                }
                break;

            case ATTVALUE:
                switch(attId) {
                    case VALUE_STRING:
                        // NOTE: string encoded as STR_R or ATTRVALUE
                        break;
                    case VALUE_COLOR:
                        int r = readByte();
                        int g = readByte();
                        int b = readByte();
                        int a = (len == 4) ? readByte() : 0xff;
                        return new Color(r, g, b, a);
                    case VALUE_LONG:
                        return new Long(readInt64());
                    case VALUE_INT:
                        return new Integer(readInt32());
                    case VALUE_BOOLEAN: 
                        // NOTE: boolean encoded as ATTRVALUE
                        break;
                    case VALUE_DOUBLE:
                        return new Double(readFloat());
                    default:
                        break;
                }
                break;
                
            default:
                break;
        }
        
        // failover
        byte[] buf = new byte[len];

        for (int i = 0; i < len; i++) // enhance with blockread!
            buf[i] = (byte) readByte();
        
        System.out.println("WARNING: Unknown OPAQUE with length: "+len);
        return buf;
    }    
}