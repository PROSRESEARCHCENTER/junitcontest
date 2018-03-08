package hep.aida.ref.xml.converters;

import hep.aida.ref.xml.ascii.AidaAsciiXMLWriter;
import hep.aida.ref.xml.binary.AidaWBXML;
import hep.aida.ref.xml.binary.AidaWBXMLLookup;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.freehep.wbxml.Attributes;
import org.freehep.wbxml.WBXMLTagWriter;
import org.freehep.wbxml.WBXMLWriter;
import org.xml.sax.SAXException;

import de.schlichtherle.io.File;
import de.schlichtherle.io.FileOutputStream;

public class ToZipHandler {

	protected File zip;
	protected String dtdName;
	protected String dtdSystemId;
	protected WBXMLTagWriter writer;
	protected boolean binary;
	protected int writerID;

	protected List prefixes;
	protected int closeTags;

	protected ToZipHandler() {
	}

	protected void convert(String out, boolean binary) {
		zip = new File(out);
		this.binary = binary;
		prefixes = new ArrayList();
		closeTags = 0;
	}

	public void characters(char[] chars, int start, int len)
			throws SAXException {
		try {
			String content = new String(chars);
			writer.print(content.substring(start, start + len));
		} catch (IOException e) {
			throw new SAXException(e);
		}
	}

	public void startDocument() throws SAXException {
	}

	public void endDocument() throws SAXException {
	}

	public void endElement(int tagID) throws SAXException {
		// ignore any end tag (aida, implementation);
		if (writer == null) {
			prefixes.add(new EndTag(tagID));
			closeTags--;
			return;
		}

		try {
			// current tag
			writer.closeTag();

			if (tagID == writerID) {
				// close all open tags
				for (int i = 0; i < closeTags; i++) {
					writer.closeTag();
				}

				// close file
				writer.closeDoc();
				writer.close();
				writer = null;
				writerID = -1;
			}
		} catch (IOException e) {
			throw new SAXException(e);
		}
	}

	public void startElement(int tag, Attributes attr, boolean empty)
			throws SAXException {

		try {
			if (writer == null) {
				// open new file
				String path = attr.getStringValue(AidaWBXML.PATH, null);
				String name = attr.getStringValue(AidaWBXML.NAME, null);

				if ((path == null) || (name == null)) {
					prefixes.add(new Tag(tag, attr, empty));
					if (!empty)
						closeTags++;
					return;
				}

				File dir = new File(zip, path);
				dir.mkdirs();
				if (binary) {
					writer = new WBXMLWriter(new FileOutputStream(new File(dir,
							escape(name))), AidaWBXML.attributes.length - 1);
				} else {
					writer = new AidaAsciiXMLWriter(new BufferedWriter(
							new FileWriter(new File(dir, escape(name)))));
				}
				System.err.println(new File(dir,
						escape(name)));
				writerID = tag;
				writer.openDoc();
				writer.referToDTD(dtdName, dtdSystemId);
				for (Iterator i = prefixes.iterator(); i.hasNext();) {
					Object o = i.next();
					if (o instanceof EndTag) {
						endElement(-2);
					} else {
						Tag t = (Tag) o;
						startElement(t.getTagID(), t.getAttributes(), t
								.isEmpty());
					}
				}

			}

			int[] atts = attr.getTags();
			for (int i = 0; i < atts.length; i++) {
				int att = atts[i];
				int type = attr.getType(att);
				switch (type) {
				case Attributes.BOOLEAN: {
					boolean value = attr.getBooleanValue(att);
					writer.setAttribute(att, value);
					break;
				}
				case Attributes.BYTE: {
					byte value = attr.getByteValue(att);
					writer.setAttribute(att, value);
					break;
				}
				case Attributes.CHAR: {
					char value = attr.getCharValue(att);
					writer.setAttribute(att, value);
					break;
				}
				case Attributes.DOUBLE: {
					double value = attr.getDoubleValue(att);
					writer.setAttribute(att, value);
					break;
				}
				case Attributes.FLOAT: {
					float value = attr.getFloatValue(att);
					writer.setAttribute(att, value);
					break;
				}
				case Attributes.INT: {
					int value = attr.getIntValue(att);
					writer.setAttribute(att, value);
					break;
				}
				case Attributes.LONG: {
					long value = attr.getLongValue(att);
					writer.setAttribute(att, value);
					break;
				}
				case Attributes.SHORT: {
					short value = attr.getShortValue(att);
					writer.setAttribute(att, value);
					break;
				}
				case Attributes.STRING: {
					String value = attr.getStringValue(att);
					writer.setAttribute(att, value);
					break;
				}
				default:
					throw new SAXException("Type '" + type
							+ "' not handled for attribute '"
							+ AidaWBXMLLookup.getAttributeName(att)
							+ "' in tag '" + AidaWBXMLLookup.getTagName(tag)
							+ "'");
				}
			}

			if (empty) {
				writer.printTag(tag);
			} else {
				writer.openTag(tag);
			}
		} catch (IOException e) {
			throw new SAXException(e);
		}
	}

	protected String escape(String name) {
		StringBuffer s = new StringBuffer();
		for (int i = 0; i < name.length(); i++) {
			switch (name.charAt(i)) {
			case '/':
				s.append("&47;");
				break;
			case '\\':
				s.append("&92;");
				break;
			default:
				s.append(name.charAt(i));
				break;
			}
		}
		return s.toString();
	}

	protected String unescape(String name) {
		StringBuffer s = new StringBuffer();
		for (int i = 0; i < name.length(); i++) {
			switch (name.charAt(i)) {
			case '&':
				if (i + 3 < name.length()) {
					String entity = name.substring(i + 1, 3);
					if (entity.equals("47;")) {
						s.append('/');
						i += 3;
					} else if (entity.equals("92;")) {
						s.append('\\');
						i += 3;
					} else {
						s.append("&");
					}
				} else {
					s.append('&');
				}
				break;
			default:
				s.append(name.charAt(i));
				break;
			}
		}
		return s.toString();
	}
}
