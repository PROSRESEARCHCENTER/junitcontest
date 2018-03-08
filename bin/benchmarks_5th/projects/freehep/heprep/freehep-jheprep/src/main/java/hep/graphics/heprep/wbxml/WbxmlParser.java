/* Copyright (c) 2002,2003,2004 Stefan Haustein, Oberhausen, Rhld., Germany
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or
 * sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The  above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE. */

// Contributors: Bjorn Aadland, Chris Bartley, Nicola Fankhauser, 
//               Victor Havin,  Christian Kurzke, Bogdan Onoiu,
//               Jain Sanjay, David Santoro.

/* CHANGES by FREEHEP 2005 (Mark.Donszelmann@slac.stanford.edu)
- package name and import of Wbxml.
- readByte, readInt, readStrI and readStrT protected.
- added parsing of WapExtension for attributes.
- parseWapExtension renamed into parseExtension which now returns extensionData and does NOT set type anymore.
- added parseOpaque.
- parseExtension and parseOblique have params set when they are called for attributes (tag and name).
- added hex number to undefined printout in resolveId.
- added typename to resolveId, to have better reporting.
- added getWapExtension.
- fixed problem with codepages in selectPage.
- added readFloat, readDouble, readInt32 and readInt64.
- made internal stream a DataInputStream.
- redid attributes to allow Objects to be stored.
- added getTag and tagId.
- redid elements to allow tagId to be stored.
*/


// FREEHEP
//package org.kxml2.wap;
package hep.graphics.heprep.wbxml;
import org.kxml2.wap.Wbxml;

import java.io.*;
import java.util.Vector;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Stack;

import org.xmlpull.v1.*;


/**
 * Binary XML Parser
 * 
 * @author duns
 * @version $Id: WbxmlParser.java 8584 2006-08-10 23:06:37Z duns $
 */
public class WbxmlParser implements XmlPullParser {

    class Attribute {
        int attId;
        String prefix;
        String namespace;
        String name;
        Object value;
        
        Attribute(int attId, String prefix, String namespace, String name, Object value) {
            this.attId = attId;
            this.prefix = prefix;
            this.namespace = namespace;
            this.name = name;
            this.value = (value instanceof List) && (((List)value).size() == 1) ? ((List)value).get(0) : value;
        }
    }    

    class Tag {
        int tagId;
        String prefix;
        String namespace;
        String name;
        
        Tag(int tagId, String prefix, String namespace, String name) {
            this.tagId = tagId;
            this.prefix = prefix;
            this.namespace = namespace;
            this.name = name;
        }
    }

    /**
     * WBXML Extension code
     */
    public static final int EXTENSION = 64;
    /**
     * WBXML Opaque code
     */
    public static final int OPAQUE = 65;

//    static final private String UNEXPECTED_EOF =
//        "Unexpected EOF";
    static final private String ILLEGAL_TYPE =
        "Wrong event type";

    private DataInputStream in;

	private int TAG_TABLE = 0;
	private int ATTR_START_TABLE = 1;
	private int ATTR_VALUE_TABLE = 2;

    protected String[] attrStartTable;
    protected String[] attrValueTable;
    protected String[] tagTable;
    private String stringTable;
    private boolean processNsp;

    private int depth;
    private String[] nspStack = new String[8];
    private int[] nspCounts = new int[4];

    private List/*<Attribute>*/ attributes = new ArrayList();
    private Stack/*<Tag>*/ tags = new Stack();

	private int nextId = -2;

	private Vector tables = new Vector();

    int version;
    int publicIdentifierId;
    int charSet;

    private int tagId;
    private String prefix;
    private String namespace;
    private String name;
    private String text;

    //	private String encoding;
    private Object wapExtensionData;
//    private int wapExtensionCode;

    protected int type;
//	private int codePage;

    private boolean degenerated;
    private boolean isWhitespace;

    public boolean getFeature(String feature) {
        if (XmlPullParser
            .FEATURE_PROCESS_NAMESPACES
            .equals(feature))
            return processNsp;
        else
            return false;
    }

    public String getInputEncoding() {
        // should return someting depending on charSet here!!!!!
        return null;
    }

    public void defineEntityReplacementText(
        String entity,
        String value)
        throws XmlPullParserException {

        // just ignore, has no effect
    }

    public Object getProperty(String property) {
        return null;
    }

    public int getNamespaceCount(int depth) {
        if (depth > this.depth)
            throw new IndexOutOfBoundsException();
        return nspCounts[depth];
    }

    public String getNamespacePrefix(int pos) {
        return nspStack[pos << 1];
    }

    public String getNamespaceUri(int pos) {
        return nspStack[(pos << 1) + 1];
    }

    public String getNamespace(String prefix) {

        if ("xml".equals(prefix))
            return "http://www.w3.org/XML/1998/namespace";
        if ("xmlns".equals(prefix))
            return "http://www.w3.org/2000/xmlns/";

        for (int i = (getNamespaceCount(depth) << 1) - 2; i >= 0; i -= 2) {
            if (prefix == null) {
                if (nspStack[i] == null)
                    return nspStack[i + 1];
            }
            else if (prefix.equals(nspStack[i]))
                return nspStack[i + 1];
        }
        return null;
    }

    public int getDepth() {
        return tags.size();
    }

    public String getPositionDescription() {

        StringBuffer buf =
            new StringBuffer(
                type < TYPES.length ? TYPES[type] : "unknown type "+type);
        buf.append(' ');

        if (type == START_TAG || type == END_TAG) {
            if (degenerated)
                buf.append("(empty) ");
            buf.append('<');
            if (type == END_TAG)
                buf.append('/');

            if (prefix != null)
                buf.append("{" + namespace + "}" + prefix + ":");
            buf.append(name);

            for (int i = 0; i < attributes.size(); i++) {
                buf.append(' ');
                if (((Attribute)attributes.get(i)).namespace != null)
                    buf.append(
                        "{"
                            + ((Attribute)attributes.get(i)).prefix
                            + "}"
                            + ((Attribute)attributes.get(i)).namespace
                            + ":");
                buf.append(
                    ((Attribute)attributes.get(i)).name
                        + "='"
                        + getAttributeValue(i)
                        + "'");
            }

            buf.append('>');
        }
        else if (type == IGNORABLE_WHITESPACE);
        else if (type != TEXT)
            buf.append(getText());
        else if (isWhitespace)
            buf.append("(whitespace)");
        else {
            String text = getText();
            if (text.length() > 16)
                text = text.substring(0, 16) + "...";
            buf.append(text);
        }

        return buf.toString();
    }

    public int getLineNumber() {
        return -1;
    }

    public int getColumnNumber() {
        return -1;
    }

    public boolean isWhitespace()
        throws XmlPullParserException {
        if (type != TEXT
            && type != IGNORABLE_WHITESPACE
            && type != CDSECT)
            exception(ILLEGAL_TYPE);
        return isWhitespace;
    }

    public String getText() {
        return text;
    }

    public char[] getTextCharacters(int[] poslen) {
        if (type >= TEXT) {
            poslen[0] = 0;
            poslen[1] = text.length();
            char[] buf = new char[text.length()];
            text.getChars(0, text.length(), buf, 0);
            return buf;
        }

        poslen[0] = -1;
        poslen[1] = -1;
        return null;
    }

    public String getNamespace() {
        return namespace;
    }

    /**
     * @return current tagid
     */
    public int getTag() {
        return tagId;
    }

    public String getName() {
        return name;
    }

    public String getPrefix() {
        return prefix;
    }

    public boolean isEmptyElementTag()
        throws XmlPullParserException {
        if (type != START_TAG)
            exception(ILLEGAL_TYPE);
        return degenerated;
    }

    protected void addAttribute(int attId, String prefix, String namespace, String name, Object value) {
        attributes.add(new Attribute(attId, prefix, namespace, name, value));
    }

    public int getAttributeCount() {
        return attributes.size();
    }

    public String getAttributeType(int index) {
        return "CDATA";
    }

    public boolean isAttributeDefault(int index) {
        return false;
    }

    public String getAttributeNamespace(int index) {
        if (index >= getAttributeCount())
            throw new IndexOutOfBoundsException();
        return ((Attribute)attributes.get(index)).namespace;
    }

    public String getAttributeName(int index) {
        if (index >= getAttributeCount())
            throw new IndexOutOfBoundsException();
        return ((Attribute)attributes.get(index)).name;
    }

    public String getAttributePrefix(int index) {
        if (index >= getAttributeCount())
            throw new IndexOutOfBoundsException();
        return ((Attribute)attributes.get(index)).prefix;
    }

    public String getAttributeValue(int index) {
        if (index >= getAttributeCount())
            throw new IndexOutOfBoundsException();
        Object object = getAttributeObject(index);
        if (object instanceof List) {
            StringBuffer s = new StringBuffer();
            for (Iterator i=((List)object).iterator(); i.hasNext(); ) {
                s.append(i.next());
            }
            return s.toString();
        } else {
            return object.toString();
        }
    }

    /**
     * Looks up Attribute value for index
     * @param index attribute index
     * @return attribute value
     */
    public Object getAttributeObject(int index) {
        if (index >= getAttributeCount())
            throw new IndexOutOfBoundsException();
        return ((Attribute)attributes.get(index)).value;
    }

    /**
     * Looks up Attribute string for name
     * @param name attribute name
     * @return attribute value
     */
    public String getAttributeValue(String name) {
        return getAttributeValue(null, name);
    }

    public String getAttributeValue(
        String namespace,
        String name) {

        for (int i = 0; i < attributes.size(); i++) {
            if (((Attribute)attributes.get(i)).name.equals(name) && 
                (namespace == null || ((Attribute)attributes.get(i)).namespace.equals(namespace)))
                return getAttributeValue(i);
        }

        return null;
    }

    /**
     * Looks up Attribute object for name
     * @param name attribute name
     * @return attribute value
     */
    public Object getAttributeObject(String name) {
        return getAttributeObject(null, name);
    }

    /**
     * Looks up Attribute object for name
     * @param namespace attribute namespace
     * @param name attribute name
     * @return attribute value
     */
    public Object getAttributeObject(String namespace, String name) {
        for (int i = 0; i < attributes.size(); i++) {
            if (((Attribute)attributes.get(i)).name.equals(name) && 
                (namespace == null || ((Attribute)attributes.get(i)).namespace.equals(namespace)))
                return getAttributeObject(i);
        }

        return null;
    }

    public int getEventType() throws XmlPullParserException {
        return type;
    }

    public int next() throws XmlPullParserException, IOException {

        isWhitespace = true;
        int minType = 9999;

        while (true) {

			String save = text;

            nextImpl();

            if (type < minType)
                minType = type;

			if (minType > CDSECT) continue; // no "real" event so far

			if (minType >= TEXT) {  // text, see if accumulate
				
				if (save != null) text = text == null ? save : save + text;
				
				switch(peekId()) {
					case Wbxml.ENTITY:
					case Wbxml.STR_I:
					case Wbxml.LITERAL:
					case Wbxml.LITERAL_C:
					case Wbxml.LITERAL_A:
					case Wbxml.LITERAL_AC: continue;
				}
			}
				
            break; 
        }

        type = minType;

        if (type > TEXT)
            type = TEXT;

        return type;
    }


    public int nextToken() throws XmlPullParserException, IOException {

        isWhitespace = true;
        nextImpl();
        return type;
    }



    public int nextTag() throws XmlPullParserException, IOException {

        next();
        if (type == TEXT && isWhitespace)
            next();

        if (type != END_TAG && type != START_TAG)
            exception("unexpected type");

        return type;
    }


    public String nextText() throws XmlPullParserException, IOException {
        if (type != START_TAG)
            exception("precondition: START_TAG");

        next();

        String result;

        if (type == TEXT) {
            result = getText();
            next();
        }
        else
            result = "";

        if (type != END_TAG)
            exception("END_TAG expected");

        return result;
    }


    public void require(int type, String namespace, String name)
        throws XmlPullParserException, IOException {

        if (type != this.type
            || (namespace != null && !namespace.equals(getNamespace()))
            || (name != null && !name.equals(getName())))
            exception(
                "expected: " + TYPES[type] + " {" + namespace + "}" + name);
    }


	public void setInput(Reader reader) throws XmlPullParserException {
		exception("InputStream required");
	}

    protected int readVersion() throws IOException {
        return readByte();
    }
    
    protected int readPublicIdentifierId() throws IOException {
        int id = readInt();

        if (id == 0)
            readInt();
        
        return id;
    }

    protected int readCharSet() throws IOException {
        return readInt();
    }

    protected String readStringTable() throws IOException {
        int strTabSize = readInt();
    
        StringBuffer buf = new StringBuffer(strTabSize);
    
        for (int i = 0; i < strTabSize; i++)
            buf.append((char) readByte());

        return buf.toString();
    }

    public void setInput(InputStream in, String enc)
        throws XmlPullParserException {

        this.in = (in instanceof DataInputStream) ? (DataInputStream)in : new DataInputStream(in);
        
        try {
            readVersion();
            publicIdentifierId = readPublicIdentifierId();

            charSet = readCharSet(); // skip charset

	        stringTable = readStringTable();
	        	        
	        selectPage(0, true);
			selectPage(0, false);
        }
        catch (IOException e) {
            exception("Illegal input format");
        }
    }

    public void setFeature(String feature, boolean value)
        throws XmlPullParserException {
        if (XmlPullParser.FEATURE_PROCESS_NAMESPACES.equals(feature))
            processNsp = value;
        else
            exception("unsupported feature: " + feature);
    }

    public void setProperty(String property, Object value)
        throws XmlPullParserException {
        throw new XmlPullParserException("unsupported property: " + property);
    }

    // ---------------------- private / internal methods
/*
    private final boolean adjustNsp()
        throws XmlPullParserException {

        boolean any = false;

        for (int i = 0; i < attributes.size(); i++) {

            String attrName = ((Attribute)attributes.get(i)).name;
            int cut = attrName.indexOf(':');
            String prefix;

            if (cut != -1) {
                prefix = attrName.substring(0, cut);
                attrName = attrName.substring(cut + 1);
            }
            else if (attrName.equals("xmlns")) {
                prefix = attrName;
                attrName = null;
            }
            else
                continue;

            if (!prefix.equals("xmlns")) {
                any = true;
            }
            else {
                int j = (nspCounts[depth]++) << 1;

                nspStack = ensureCapacity(nspStack, j + 2);
                nspStack[j] = attrName;
                nspStack[j + 1] = ((Attribute)attributes.get(i)).namespace;

                if (attrName != null
                    && ((Attribute)attributes.get(i).namespace.equals(""))
                    exception("illegal empty namespace");

                //  prefixMap = new PrefixMap (prefixMap, attrName, attr.getValue ());

                //System.out.println (prefixMap);

                System.arraycopy(
                    attributes,
                    i + 4,
                    attributes,
                    i,
                    ((--attributeCount) << 2) - i);

                i -= 4;
            }
        }

        if (any) {
            for (int i = (attributeCount << 2) - 4;
                i >= 0;
                i -= 4) {

                String attrName = attributes[i + 2];
                int cut = attrName.indexOf(':');

                if (cut == 0)
                    throw new RuntimeException(
                        "illegal attribute name: "
                            + attrName
                            + " at "
                            + this);

                else if (cut != -1) {
                    String attrPrefix =
                        attrName.substring(0, cut);

                    attrName = attrName.substring(cut + 1);

                    String attrNs = getNamespace(attrPrefix);

                    if (attrNs == null)
                        throw new RuntimeException(
                            "Undefined Prefix: "
                                + attrPrefix
                                + " in "
                                + this);

                    attributes[i] = attrNs;
                    attributes[i + 1] = attrPrefix;
                    attributes[i + 2] = attrName;

                    for (int j = (attributeCount << 2) - 4;
                        j > i;
                        j -= 4)
                        if (attrName.equals(attributes[j + 2])
                            && attrNs.equals(attributes[j]))
                            exception(
                                "Duplicate Attribute: {"
                                    + attrNs
                                    + "}"
                                    + attrName);
                }
            }
        }

        int cut = name.indexOf(':');

        if (cut == 0)
            exception("illegal tag name: " + name);
        else if (cut != -1) {
            prefix = name.substring(0, cut);
            name = name.substring(cut + 1);
        }

        this.namespace = getNamespace(prefix);

        if (this.namespace == null) {
            if (prefix != null)
                exception("undefined prefix: " + prefix);
            this.namespace = NO_NAMESPACE;
        }

        return any;
    }
*/
	private final void setTable(int page, int type, String[] table) {
		if(stringTable != null){
			throw new RuntimeException("setXxxTable must be called before setInput!");
		}
		while(tables.size() < 3*page +3){
			tables.addElement(null);
		}
		tables.setElementAt(table, page*3+type);
	}
		
		



    private final void exception(String desc)
        throws XmlPullParserException {
        throw new XmlPullParserException(desc, this, null);
    }


	protected void selectPage(int nr, boolean tags) throws XmlPullParserException {
		if (tables.size() == 0 && nr == 0) return;
		
		// FREEHEP
		if ((tags && (nr*3 + TAG_TABLE > tables.size())) || 
		    (!tags && (nr*3 + ATTR_VALUE_TABLE > tables.size())))
			exception("Code Page "+nr+" undefined for "+(tags ? "tags" : "attributes")+".");
		
		if (tags) {
			tagTable = (String[]) tables.elementAt(nr * 3 + TAG_TABLE);
		} else {
			attrStartTable = (String[]) tables.elementAt(nr * 3 + ATTR_START_TABLE);
			attrValueTable = (String[]) tables.elementAt(nr * 3 + ATTR_VALUE_TABLE);
		}
	}

    private final void nextImpl()
        throws IOException, XmlPullParserException {

        if (type == END_TAG) {
            depth--;
            Tag tag = (Tag)tags.pop();
            endTag(tag);
        }

        if (degenerated) {
            type = XmlPullParser.END_TAG;
            degenerated = false;
            return;
        }

        text = null;
        prefix = null;
        tagId = 0;
        name = null;

        int id = peekId ();
        while(id == Wbxml.SWITCH_PAGE){
        	nextId = -2;
			selectPage(readByte(), true);
			id = peekId();        	
        }
        nextId = -2;

        switch (id) {
            case -1 :
                type = XmlPullParser.END_DOCUMENT;
                break;

            case Wbxml.END :
                {
                    type = END_TAG;
                    Tag peek = (Tag)tags.peek(); 
                    tagId = peek.tagId;  
                    namespace = peek.namespace;
                    prefix = peek.prefix;
                    name = peek.name;
                }
                break;

            case Wbxml.ENTITY :
                {
                    type = ENTITY_REF;
                    char c = (char) readInt();
                    text = "" + c;
                    name = "#" + ((int) c);
                }

                break;

            case Wbxml.STR_I :
                type = TEXT;
                text = readStrI();
                break;

            case Wbxml.EXT_I_0 :
            case Wbxml.EXT_I_1 :
            case Wbxml.EXT_I_2 :
            case Wbxml.EXT_T_0 :
            case Wbxml.EXT_T_1 :
            case Wbxml.EXT_T_2 :
            case Wbxml.EXT_0 :
            case Wbxml.EXT_1 :
            case Wbxml.EXT_2 :
                type = EXTENSION;
                parseExtension(id, 0, 0);
                break;
            
            case Wbxml.OPAQUE :
                // FREEHEP
                type = OPAQUE;
                int len = readInt();
                parseOpaque(len, 0, 0);
                break;

            case Wbxml.PI :
                processInstruction();
                break;

            case Wbxml.STR_T :
                {
                    type = TEXT;
                    int pos = readInt();
                    int end = stringTable.indexOf('\0', pos);
                    text = stringTable.substring(pos, end);
                }
                break;

            default :
                parseElement(id);
        }
    }

    protected void endAttributes() throws IOException, XmlPullParserException {
    }

    protected void endTag(Tag tag) throws IOException, XmlPullParserException {
    }

    protected void processInstruction() throws IOException, XmlPullParserException  {
        readAttr(0);
    }
    
// FREEHEP
    protected Object parseOpaque(int len, int tagId, int attId) 
        throws IOException, XmlPullParserException {

        byte[] buf = new byte[len];

        for (int i = 0;
            i < len;
            i++) // enhance with blockread!
            buf[i] = (byte) readByte();

        wapExtensionData = buf;
        
        return buf;
    }


    protected Object parseExtension(int id, int tagId, int attId)
        throws IOException, XmlPullParserException {

// FREEHEP
//        type = WAP_EXTENSION;
//        wapExtensionCode = id;

        switch (id) {
            case Wbxml.EXT_I_0 :
            case Wbxml.EXT_I_1 :
            case Wbxml.EXT_I_2 :
                wapExtensionData = readStrI();
                break;

            case Wbxml.EXT_T_0 :
            case Wbxml.EXT_T_1 :
            case Wbxml.EXT_T_2 :
                wapExtensionData = new Integer(readInt());
                break;

            case Wbxml.EXT_0 :
            case Wbxml.EXT_1 :
            case Wbxml.EXT_2 :
                wapExtensionData = null;
                break;
            	
            default:
                exception("illegal extension id: "+id+"("+Integer.toHexString(id)+")");
        } // SWITCH
        
        // FREEHEP
        return wapExtensionData;
    }


    /**
     * Read attribute for specified tag id
     * @param tagId tag id
     * @throws IOException if stream cannot be read
     * @throws XmlPullParserException if format is incorrect
     */
    public void readAttr(int tagId) throws IOException, XmlPullParserException {

        int id = readByte();
        while (id != 1) {

        	while(id == Wbxml.SWITCH_PAGE){
                selectPage(readByte(), false);
                id = readByte();
            } 
        	
        	int attId = id;
            String name = resolveId("ATTRSTART", attrStartTable, id);
            List value = new ArrayList();

            int cut = name.indexOf('=');

            if (cut != -1) {
                value.add(name.substring(cut + 1));
                name = name.substring(0, cut);
            }

            id = readByte();
            while (id > 128
            	|| id == Wbxml.SWITCH_PAGE
                || id == Wbxml.ENTITY
                || id == Wbxml.STR_I
                || id == Wbxml.STR_T
                || (id >= Wbxml.EXT_I_0 && id <= Wbxml.EXT_I_2)
                || (id >= Wbxml.EXT_T_0 && id <= Wbxml.EXT_T_2)) {

                switch (id) {
					case Wbxml.SWITCH_PAGE :
						selectPage(readByte(), false);
						break;
                	
                    case Wbxml.ENTITY :
                        value.add(new Integer(readInt()));
                        break;

                    case Wbxml.STR_I :
                        value.add(readStrI());
                        break;

                    case Wbxml.EXT_I_0 :
                    case Wbxml.EXT_I_1 :
                    case Wbxml.EXT_I_2 :
                    case Wbxml.EXT_T_0 :
                    case Wbxml.EXT_T_1 :
                    case Wbxml.EXT_T_2 :
                    case Wbxml.EXT_0 :
                    case Wbxml.EXT_1 :
                    case Wbxml.EXT_2 :
                        // FREEHEP, re-added
                        value.add(parseExtension(id, tagId, attId));
                        break;

                    case Wbxml.OPAQUE :
                        // FREEHEP, added
                        int len = readInt();
                        value.add(parseOpaque(len, tagId, attId));
                        break;

                    case Wbxml.STR_T :
                        value.add(readStrT());
                        break;

                    default :
                        value.add(resolveId("ATTRVALUE", attrValueTable, id));
                }

                id = readByte();
            }

            addAttribute(attId, "", null, name, value);
        }
        endAttributes();
    }

	private int peekId () throws IOException {
		if (nextId == -2) {
			nextId = in.read ();
		}
		return nextId;
	}
		
    // FREEHEP
    protected String resolveId(String type, String[] tab, int id) throws IOException {
        int idx = (id & 0x07f) - 5;
        if (idx == -1)
            return readStrT();
        if (idx < 0
            || tab == null
            || idx >= tab.length
            || tab[idx] == null)
            // FREEHEP
            throw new IOException(type+" id " + id + " (0x"+Integer.toHexString(id)+") undefined.");

        return tab[idx];
    }

    protected void parseElement(int id)
        throws IOException, XmlPullParserException {

		type = START_TAG;
		tagId = id & 0x03f;
        name = resolveId("TAG", tagTable, tagId);

		attributes.clear();
        if ((id & 128) != 0) {
            readAttr(tagId);
        }

        degenerated = (id & 64) == 0;

        depth++;

        // transfer to element stack
        if (depth >= nspCounts.length) {
             int[] bigger = new int[depth + 4];
             System.arraycopy(nspCounts, 0, bigger, 0, nspCounts.length);
             nspCounts = bigger;
        }
        
        nspCounts[depth] = nspCounts[depth - 1]; 

/* 
        for (int i = attributeCount - 1; i > 0; i--) {
            for (int j = 0; j < i; j++) {
                if (getAttributeName(i)
                    .equals(getAttributeName(j)))
                    exception(
                        "Duplicate Attribute: "
                            + getAttributeName(i));
            }
        }
*/
/*
        if (processNsp)
            adjustNsp();
        else
*/
            namespace = "";
        tags.push(new Tag(tagId, prefix, namespace, name));
    }
    
/*    private final String[] ensureCapacity(
        String[] arr,
        int required) {
        if (arr.length >= required)
            return arr;
        String[] bigger = new String[required + 16];
        System.arraycopy(arr, 0, bigger, 0, arr.length);
        return bigger;
    }
*/
    protected int readByte() throws IOException {
        int i = in.read();
        if (i == -1)
            throw new IOException("Unexpected EOF");
        return i;
    }

    protected int readInt() throws IOException {
        int result = 0;
        int i;

        do {
            i = readByte();
            result = (result << 7) | (i & 0x7f);
        }
        while ((i & 0x80) != 0);

        return result;
    }

    // FREEHEP
    protected float readFloat() throws IOException {
        return in.readFloat();
    }

    // FREEHEP
    protected double readDouble() throws IOException {
        return in.readDouble();
    }

    // FREEHEP
    protected int readInt32() throws IOException {
        return in.readInt();
    }

    // FREEHEP
    protected long readInt64() throws IOException {
        return in.readLong();
    }

    

    // FREEHEP
    protected String readStrI() throws IOException {
        StringBuffer buf = new StringBuffer();
        boolean wsp = true;
        while (true) {
            int i = in.read();
            if (i == -1)
                throw new IOException("Unexpected EOF");
            if (i == 0)
                break;
            if (i > 32)
                wsp = false;
            buf.append((char) i);
        }
        isWhitespace = wsp;
        return buf.toString();
    }

    protected String readStrT() throws IOException {
        int pos = readInt();
        int end = stringTable.indexOf('\0', pos);

        return stringTable.substring(pos, end);
    }

    /** 
     * Sets the tag table for a given page.
     * The first string in the array defines tag 5, the second tag 6 etc.
     * @param page page number
     * @param table table to use
     */
    public void setTagTable(int page, String[] table) {
    	setTable(page, TAG_TABLE, table);
    	
//        this.tagTable = tagTable;
  //      if (page != 0)
    //        throw new RuntimeException("code pages curr. not supp.");
    }

    /** 
     * Sets the attribute start Table for a given page.
     *	The first string in the array defines attribute 
     *  5, the second attribute 6 etc.
     *  Currently, only page 0 is supported. Please use the 
     *  character '=' (without quote!) as delimiter 
     *  between the attribute name and the (start of the) value 
     * @param page page number
     * @param table table to use
     */
    public void setAttrStartTable(
        int page,
        String[] table) {

		setTable(page, ATTR_START_TABLE, table);
    }

    /** Sets the attribute value Table for a given page.
     *	The first string in the array defines attribute value 0x85, 
     *  the second attribute value 0x86 etc.
     *  Currently, only page 0 is supported.
     * @param page page number
     * @param table table to use
     */
    public void setAttrValueTable(
        int page,
        String[] table) {

		setTable(page, ATTR_VALUE_TABLE, table);
    }

    /**
     * Return WAP extension string for id
     * @param id wap extension id
     * @return wap extension string
     */
    // FREEHEP
    public static String getWapExtension(int id) {
        switch(id) {
            case Wbxml.EXT_I_0 : return "EXT_I_0";
            case Wbxml.EXT_I_1 : return "EXT_I_1";
            case Wbxml.EXT_I_2 : return "EXT_I_2";
            case Wbxml.EXT_T_0 : return "EXT_T_0";
            case Wbxml.EXT_T_1 : return "EXT_T_1";
            case Wbxml.EXT_T_2 : return "EXT_T_2";
            case Wbxml.EXT_0 :   return "EXT_0";
            case Wbxml.EXT_1 :   return "EXT_1";
            case Wbxml.EXT_2 :   return "EXT_2";
            case Wbxml.OPAQUE :  return "OPAQUE";
            
            default:
                return "Unknown token: "+id+"("+Integer.toHexString(id)+")";
        }
    }
}
