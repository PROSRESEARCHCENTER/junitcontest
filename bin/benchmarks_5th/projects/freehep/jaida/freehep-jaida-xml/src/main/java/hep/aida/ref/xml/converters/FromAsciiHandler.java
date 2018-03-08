package hep.aida.ref.xml.converters;

import hep.aida.ref.xml.ascii.AidaAsciiXMLWriter;
import hep.aida.ref.xml.binary.AidaWBXML;
import hep.aida.ref.xml.binary.AidaWBXMLConverter;
import hep.aida.ref.xml.binary.AidaWBXMLLookup;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.freehep.wbxml.WBXMLTagWriter;
import org.freehep.wbxml.WBXMLWriter;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

public class FromAsciiHandler implements ContentHandler, EntityResolver {

	private WBXMLTagWriter writer;

	public FromAsciiHandler() {
	}

	void convert(InputStream in, OutputStream out, boolean binary)
			throws SAXException, ParserConfigurationException, IOException {
		if (binary) {
			writer = new WBXMLWriter(out, AidaWBXML.attributes.length - 1);
		} else {
			writer = new AidaAsciiXMLWriter(new BufferedWriter(new OutputStreamWriter(
					out)));
		}

		SAXParserFactory factory = SAXParserFactory.newInstance();
		factory.setValidating(true);
		factory.setNamespaceAware(false); // the code was generated
		// according
		// DTD
		XMLReader parser = factory.newSAXParser().getXMLReader();
		parser.setContentHandler(this);
		parser.setEntityResolver(this);
		parser.setErrorHandler(new ErrorHandler() {
			public void error(SAXParseException ex) throws SAXException {
				// if (context.isEmpty())
				// System.err.println("Missing DOCTYPE.");
				throw ex;
			}

			public void fatalError(SAXParseException ex) throws SAXException {
				throw ex;
			}

			public void warning(SAXParseException ex) throws SAXException {
				// ignore
			}
		});
		parser.parse(new InputSource(in));
	}

	public InputSource resolveEntity(String publicId, String systemId)
			throws SAXException, IOException {
		// FIXME, get the proper DTD.
		writer.referToDTD("aida", systemId);
		return null;
	}

	public void characters(char[] ch, int start, int length)
			throws SAXException {
		try {
			String content = new String(ch).substring(start, start + length);
			writer.print(content);
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

	public void endElement(String uri, String localName, String name)
			throws SAXException {
		int tag = AidaWBXMLLookup.getTag(name);
		if (tag < 0)
			throw new SAXException("Closing Unknown tag '" + name + "'");
		if (AidaWBXMLLookup.isTagEmpty(tag))
			return;

		try {
			writer.closeTag();
		} catch (IOException e) {
			throw new SAXException(e);
		}
	}

	public void endPrefixMapping(String prefix) throws SAXException {
		// ignored
	}

	public void ignorableWhitespace(char[] ch, int start, int length)
			throws SAXException {
		// ignore
	}

	public void processingInstruction(String target, String data)
			throws SAXException {
		// ignored
	}

	public void setDocumentLocator(Locator locator) {
		// ignored
	}

	public void skippedEntity(String name) throws SAXException {
		// ignored
	}

	public void startDocument() throws SAXException {
		try {
			writer.openDoc();
		} catch (IOException e) {
			throw new SAXException(e);
		}
	}

	public void startElement(String uri, String localName, String name,
			Attributes atts) throws SAXException {
		String attName = null;
		String value = null;
		try {
			int tag = AidaWBXMLLookup.getTag(name);
			if (tag < 0)
				throw new SAXException("Unknown tag '" + name + "'");

			for (int i = 0; i < atts.getLength(); i++) {
				attName = atts.getQName(i);
				value = atts.getValue(i);

				int att = AidaWBXMLLookup.getAttribute(attName);
				if (att == -1)
					throw new SAXException("Unknown attribute '" + attName
							+ "' in tag '" + name + "'");

				int type = -1;
				if (att == -2) {
					// att = "value" --> try to guess type
					try {
						Double.parseDouble(value);
						type = org.freehep.wbxml.Attributes.DOUBLE;
					} catch (NumberFormatException e1) {
						try {
							Integer.parseInt(value);
							type = org.freehep.wbxml.Attributes.INT;
						} catch (NumberFormatException e2) {
							type = org.freehep.wbxml.Attributes.STRING;
						}
					}
				} else {
					type = AidaWBXMLLookup.getAttributeType(att);
				}
				if (type == -1)
					throw new SAXException("Unknown type for attribute '"
							+ attName + "' in tag '" + name + "'");

				switch (type) {
				case org.freehep.wbxml.Attributes.BOOLEAN:
					if (att == -2)
						att = AidaWBXML.VALUE_BOOLEAN;
					writer.setAttribute(att, AidaWBXMLConverter.toBoolean(tag,
							att, value));
					break;
				case org.freehep.wbxml.Attributes.BYTE:
					if (att == -2)
						att = AidaWBXML.VALUE_BYTE;
					writer.setAttribute(att, Byte.parseByte(value));
					break;
				case org.freehep.wbxml.Attributes.CHAR:
					if (att == -2)
						att = AidaWBXML.VALUE_CHAR;
					writer.setAttribute(att, value.charAt(0));
					break;
				case org.freehep.wbxml.Attributes.DOUBLE:
					if (att == -2)
						att = AidaWBXML.VALUE_DOUBLE;
					writer.setAttribute(att, AidaWBXMLConverter.toDouble(tag,
							att, value));
					break;
				case org.freehep.wbxml.Attributes.FLOAT:
					if (att == -2)
						att = AidaWBXML.VALUE_FLOAT;
					writer.setAttribute(att, AidaWBXMLConverter.toFloat(tag,
							att, value));
					break;
				case org.freehep.wbxml.Attributes.INT:
					if (att == -2)
						att = AidaWBXML.VALUE_INT;
					writer.setAttribute(att, AidaWBXMLConverter.toInt(tag, att,
							value));
					break;
				case org.freehep.wbxml.Attributes.LONG:
					if (att == -2)
						att = AidaWBXML.VALUE_LONG;
					writer.setAttribute(att, Long.parseLong(value));
					break;
				case org.freehep.wbxml.Attributes.SHORT:
					if (att == -2)
						att = AidaWBXML.VALUE_SHORT;
					writer.setAttribute(att, Short.parseShort(value));
					break;
				case org.freehep.wbxml.Attributes.STRING:
					if (att == -2)
						att = AidaWBXML.VALUE_STRING;
					writer.setAttribute(att, value);
					break;
				default:
					throw new SAXException("Type '" + type
							+ "' not handled for attribute '" + attName
							+ "' in tag '" + name + "'");
				}
			}

			boolean empty = AidaWBXMLLookup.isTagEmpty(tag);
			if (empty) {
				writer.printTag(tag);
			} else {
				writer.openTag(tag);
			}
		} catch (IOException e) {
			throw new SAXException(e);
		} catch (NumberFormatException e) {
			throw new SAXException("Exception for tag '" + name
					+ "', attribute '" + attName + "' value '" + value + "'", e);
		}
	}

	public void startPrefixMapping(String prefix, String uri)
			throws SAXException {
		// ignored
	}
}
