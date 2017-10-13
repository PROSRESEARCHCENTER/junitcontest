// Copyright FreeHEP, 2007
package org.freehep.wbxml;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.xml.sax.SAXException;

/**
 * SAX-like Binary XML Parser. There is NO support form namespaces, attrPrefixValues or attrValues.
 * Code pages are handled internally. Both attribute and tag code indexes start at 0 and run up. 
 * 
 * @author Mark Donszelmann
 * @version $Id: WbxmlParser.java 8584 2006-08-10 23:06:37Z duns $
 */
public class WBXMLParser implements WBXML {

	private DataInputStream in;
	private ContentHandler contentHandler;
	private ExtensionHandler extensionHandler;
	private Map/*<Integer, String>*/ stringTable;
	private int version;
	private int publicIdentifierId;
	private int charSet;
	private int tagPage;
	private int attributePage;
	private Stack stack = new Stack();
	private EntityResolver resolver;

	public WBXMLParser(ContentHandler contentHandler) {
		this(contentHandler, new DefaultExtensionHandler());
	}
	
	public WBXMLParser(ContentHandler contentHandler, ExtensionHandler extensionHandler) {
		this.contentHandler = contentHandler;
		this.extensionHandler = extensionHandler;
	}
	
	public void setEntityResolver(EntityResolver resolver) {
		this.resolver = resolver;
	}
		
	public int getCharSet() {
		return charSet;
	}
	
	public int getVersion() {
		return version;
	}

	public void parse(InputStream in) throws SAXException, IOException {
		this.in = in instanceof DataInputStream ? (DataInputStream) in
				: new DataInputStream(in);
		char entityBuf[] = new char[1];
		tagPage = 0;
		attributePage = 0;
		
		version = readByte();
		publicIdentifierId = readInt();
		int dtdIndex = 0;
		if (publicIdentifierId == 0) {
			dtdIndex = readInt();
		}
		charSet = readInt();

		stringTable = new HashMap();
		int len = readInt();
		int offset = 0;
		while (offset < len) {
			String s = this.in.readUTF();
			int sLen = stringUTFLength(s);
			stringTable.put(new Integer(offset), s);
			in.read(); // skip NULL termination
			// len (short) + null (byte)
			offset += sLen + 2 + 1;
		}

		contentHandler.startDocument();
		
		if (publicIdentifierId == 0) {
			String[] dtdPair = ((String)stringTable.get(new Integer(dtdIndex))).split(" ", 2);
			
			if (resolver != null) {
				// FIXME, use the resolver stream
				resolver.resolveEntity(dtdPair[0], null, dtdPair[1]);
			}
		}
		
		while (true) {
			int id = in.read();
			if (id == -1) {
				break;
			}
			switch (id) {
			case SWITCH_PAGE:
				tagPage = readByte();
				break;

			case END:
				contentHandler.endElement(((Integer) stack.pop()).intValue());
				break;

			case ENTITY:
				entityBuf[0] = (char) readInt();
				contentHandler.characters(entityBuf, 0, 1);
				break;

			case STR_I:
				String s = readStrI();
				contentHandler.characters(s.toCharArray(), 0, s.length());
				break;

			case EXT_I_0:
			case EXT_I_1:
			case EXT_I_2:
			case EXT_T_0:
			case EXT_T_1:
			case EXT_T_2:
			case EXT_0:
			case EXT_1:
			case EXT_2:
			case OPAQUE:
				int tagID = ((Integer) stack.peek()).intValue();
				handleExtensions(id, tagID, -1, null, null);
				break;

			case PI:
				throw new SAXException("PI Not Supported");

			case STR_T:
				String str = readStrT();
				contentHandler.characters(str.toCharArray(), 0, str.length());
				break;

			default:
				readElement(id);
			}
		}

		if (stack.size() != 0) {
			throw new SAXException("unclosed elements: " + stack);
		}

		contentHandler.endDocument();
	}

	private void handleExtensions(int id, int tagID, int attributeID,
			MutableAttributes atts, List value) throws SAXException,
			IOException {
		switch (id) {
		case EXT_I_0:
		case EXT_I_1:
		case EXT_I_2:
			extensionHandler.extI(id - EXT_I_0, readStrI(), tagID, attributeID,
					atts, value);
			break;

		case EXT_T_0:
		case EXT_T_1:
		case EXT_T_2:
			extensionHandler.extT(id - EXT_T_0, readInt(), tagID, attributeID,
					atts, value);
			break;

		case EXT_0:
		case EXT_1:
		case EXT_2:
			extensionHandler.ext(id - EXT_0, tagID, attributeID, atts, value);
			break;

		case OPAQUE:
			int len = readInt();
			extensionHandler.opaque(len, in, tagID, attributeID, atts, value);
			break;
		}
	}

	private Attributes readAttr(int tagID) throws SAXException, IOException {
		AttributesImpl result = new AttributesImpl();

		int id = readByte();
		int attributeID = -1;
		while (id != END) {
			// attribute start
			while (id == SWITCH_PAGE) {
				attributePage = readByte();
				id = readByte();
			}
			attributeID = getAttributeId(id);

			List value = new ArrayList();

			// attribute value(s)
			id = readByte();
			while (id > 128 || id == SWITCH_PAGE || id == ENTITY || id == STR_I || id == STR_T
					|| (id >= EXT_I_0 && id <= EXT_I_2)
					|| (id >= EXT_T_0 && id <= EXT_T_2)) {

				switch (id) {
				case SWITCH_PAGE:
					attributePage = readByte();
					break;

				case ENTITY:
					value.add(new Character((char) readInt()));
					break;

				case STR_I:
					value.add(readStrI());
					break;

				case EXT_I_0:
				case EXT_I_1:
				case EXT_I_2:
				case EXT_T_0:
				case EXT_T_1:
				case EXT_T_2:
				case EXT_0:
				case EXT_1:
				case EXT_2:
				case OPAQUE:
					handleExtensions(id, tagID, attributeID, result,
							value);
					break;
				case STR_T:
					value.add(readStrT());
					break;
				default:
					value.add(new Integer(getAttributeId(id)));
				}
				id = readByte();
			}
			
			switch (value.size()) {
			case 0:
				// already handled
				break;
			case 1:
				Object o = value.get(0);
				if (o instanceof Integer) {
					result.set(attributeID, ((Integer) o).intValue());
				} else if (o instanceof Character) {
					result.set(attributeID, ((Character) o).charValue());
				} else if (o instanceof String) {
					result.set(attributeID, (String) o);
				} else {
					throw new IOException(getClass() + ": Type " + o.getClass()
							+ " not properly handled.");
				}
				break;
			default:
				result.set(attributeID, value);
				break;
			}
			attributeID = -1;
		}
		return result;
	}

	private int getTagId(int id) {
		return (id & 0x03f) + (tagPage * MAX_CODES) - RESERVED_CODES;
	}

	private int getAttributeId(int id) {
		return (id & 0x03f) + (attributePage * MAX_CODES) - RESERVED_CODES;
	}

	private void readElement(int id) throws IOException, SAXException {
		int tagID = getTagId(id & 0x3f);

		boolean empty;
		if ((id & CONTENT) != 0) {
			stack.add(new Integer(tagID));
			empty = false;
		} else {
			empty = true;
		}

		contentHandler.startElement(tagID, ((id & ATTRIBUTE) != 0) ? readAttr(tagID)
				: new AttributesImpl(), empty);

	}

	protected int readByte() throws IOException, SAXException {
		int i = in.read();
		if (i == -1) {
			throw new SAXException("Unexpected EOF");
		}
		return i;
	}

	protected int readInt() throws SAXException, IOException {
		int result = 0;
		int i;
		do {
			i = readByte();
			result = (result << 7) | (i & 0x7f);
		} while ((i & 0x80) != 0);
		return result;
	}

	protected String readStrI() throws IOException, SAXException {
		String s = in.readUTF();
		in.read(); // skip NULL Termination
		return s;
	}

	protected String readStrT() throws IOException, SAXException {
		Integer pos = new Integer(readInt());
		return (String)stringTable.get(pos);
	}

	public static int stringUTFLength(String s) {
		int bytesNeeded = 0;
		for (int i = 0; i < s.length(); i++) {
			if (s.charAt(i) < 0x80) {
				++bytesNeeded;
			} else if (s.charAt(i) < 0x0800) {
				bytesNeeded += 2;
			} else if (s.charAt(i) < 0x10000) {
				bytesNeeded += 3;
			} else {
				bytesNeeded += 4;
			}
		}
		return bytesNeeded;
	}	

	public static void main(String[] args) throws Exception {
		if (args.length < 1) {
			System.err.println("Usage: WBXMLParser filename");
			System.exit(1);
		}

		ContentHandler contentHandler = new ContentHandler() {

			public void characters(char[] chars, int start, int len)
					throws SAXException {
				System.err.print("'" + String.valueOf(chars) + "'");
			}

			public void endDocument() throws SAXException {
				System.err.println("END DOCUMENT");
			}

			public void endElement(int tagID) throws SAXException {
				System.err.println("</" + tagID + ">");
			}

			public void startDocument() throws SAXException {
				System.err.println("START DOCUMENT");
			}

			public void startElement(int tagID, Attributes attr, boolean empty)
					throws SAXException {
				System.err.println("<" + tagID);
				System.err.println(attr.getTags().length);
				if (empty) System.err.print("/");
				System.err.println(">");
			}

		};

	    WBXMLParser p = new WBXMLParser(contentHandler);
		p.parse(new FileInputStream(args[0]));
	}
}
