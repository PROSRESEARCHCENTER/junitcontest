package hep.aida.ref.xml.converters;

import hep.aida.ref.xml.ascii.AidaAsciiXMLWriter;
import hep.aida.ref.xml.binary.AidaWBXML;
import hep.aida.ref.xml.binary.AidaWBXMLLookup;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import org.freehep.wbxml.Attributes;
import org.freehep.wbxml.ContentHandler;
import org.freehep.wbxml.EntityResolver;
import org.freehep.wbxml.WBXMLParser;
import org.freehep.wbxml.WBXMLTagWriter;
import org.freehep.wbxml.WBXMLWriter;
import org.xml.sax.SAXException;

public class FromBinaryHandler implements ContentHandler, EntityResolver {

	private WBXMLTagWriter writer;

	public FromBinaryHandler() {
	}

	void convert(InputStream in, OutputStream out, boolean binary)
			throws IOException, SAXException {
		if (binary) {
			writer = new WBXMLWriter(out, AidaWBXML.attributes.length - 1);
		} else {
			writer = new AidaAsciiXMLWriter(new BufferedWriter(new OutputStreamWriter(
					out)));
		}

		WBXMLParser p = new WBXMLParser(this);
		p.setEntityResolver(this);
		p.parse(in);
	}

	public InputStream resolveEntity(String name, String publidId,
			String systemId) throws SAXException, IOException {
		writer.referToDTD(name, systemId);
		return null;
	}

	public void characters(char[] chars, int start, int len)
			throws SAXException {
		try {
			String s = new String(chars);
			writer.print(s.substring(start, start + len));
		} catch (IOException e) {
			throw new SAXException(e);
		}
	}

	public void endDocument() throws SAXException {
		try {
			writer.closeDoc();
			writer.close();
		} catch (IOException e) {
			throw new SAXException(e);
		}
	}

	public void endElement(int tagID) throws SAXException {
		try {
			writer.closeTag();
		} catch (IOException e) {
			throw new SAXException(e);
		}
	}

	public void startDocument() throws SAXException {
		try {
			writer.openDoc();
		} catch (IOException e) {
			throw new SAXException(e);
		}
	}

	public void startElement(int tag, Attributes attr, boolean empty)
			throws SAXException {
		try {
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
}
