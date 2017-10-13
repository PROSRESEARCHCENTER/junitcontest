// Copyright FreeHEP, 2007
package hep.aida.ref.xml.converters;

import hep.aida.ref.xml.binary.AidaWBXML;
import hep.aida.ref.xml.binary.AidaWBXMLConverter;
import hep.aida.ref.xml.binary.AidaWBXMLLookup;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.freehep.wbxml.AttributesImpl;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;

/**
 * 
 * @author Mark Donszelmann
 * @version $Id$
 */
public class AsciiToZipHandler extends ToZipHandler implements ContentHandler, EntityResolver {

	public AsciiToZipHandler() {
	}

	void convert(String in, String out, boolean binary) throws IOException, ParserConfigurationException, SAXException {
		super.convert(out, binary);
		
		SAXParserFactory factory = SAXParserFactory.newInstance();
		factory.setValidating(true);
		factory.setNamespaceAware(false); // the code was generated according
		// DTD
		XMLReader parser = factory.newSAXParser().getXMLReader();
		parser.setContentHandler(this);
		parser.setEntityResolver(this);
		parser.setErrorHandler(new ErrorHandler() {
			public void error(SAXParseException ex) throws SAXException {
//				if (context.isEmpty())
//					System.err.println("Missing DOCTYPE.");
				throw ex;
			}

			public void fatalError(SAXParseException ex) throws SAXException {
				throw ex;
			}

			public void warning(SAXParseException ex) throws SAXException {
				// ignore
			}
		});
		parser.parse(in);
	}

	public InputSource resolveEntity(String publicId, String systemId)
			throws SAXException, IOException {
		// FIXME, hardcoded
		dtdName = "aida";
		dtdSystemId = systemId;
		return null;
	}

	public void endElement(String uri, String localName, String name)
			throws SAXException {
		int tag = AidaWBXMLLookup.getTag(name);
		if (tag < 0)
			throw new SAXException("Closing Unknown tag '" + name + "'");
		if (AidaWBXMLLookup.isTagEmpty(tag))
			return;

		endElement(tag);
	}

	public void startElement(String uri, String localName, String tagName,
			Attributes attr) throws SAXException {

		int tag = AidaWBXMLLookup.getTag(tagName);
		if (tag < 0)
			throw new SAXException("Unknown tag '" + tagName + "'");

		String attName = null;
		String value = null;
		
		try {
			// convert SAX Attributes to WBXML Attributes
			org.freehep.wbxml.MutableAttributes atts = new AttributesImpl();

			for (int i = 0; i < attr.getLength(); i++) {
				attName = attr.getQName(i);
				value = attr.getValue(i);

				int attID = AidaWBXMLLookup.getAttribute(attName);
				if (attID == -1)
					throw new SAXException("Unknown attribute '" + attName
							+ "' in tag '" + tagName + "'");

				int type = -1;
				if (attID == -2) {
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
					type = AidaWBXMLLookup.getAttributeType(attID);
				}
				if (type == -1)
					throw new SAXException("Unknown type for attribute '"
							+ attName + "' in tag '" + tagName + "'");

				switch (type) {
				case org.freehep.wbxml.Attributes.BOOLEAN:
					if (attID == -2)
						attID = AidaWBXML.VALUE_BOOLEAN;
					atts.set(attID, AidaWBXMLConverter.toBoolean(tag,
							attID, value));
					break;

				case org.freehep.wbxml.Attributes.BYTE:
					if (attID == -2)
						attID = AidaWBXML.VALUE_BYTE;
					atts.set(attID, Byte.parseByte(value));
					break;
				case org.freehep.wbxml.Attributes.CHAR:
					if (attID == -2)
						attID = AidaWBXML.VALUE_CHAR;
					atts.set(attID, value.charAt(0));
					break;
				case org.freehep.wbxml.Attributes.DOUBLE:
					if (attID == -2)
						attID = AidaWBXML.VALUE_DOUBLE;
					atts.set(attID, AidaWBXMLConverter.toDouble(tag,
							attID, value));
					break;
				case org.freehep.wbxml.Attributes.FLOAT:
					if (attID == -2)
						attID = AidaWBXML.VALUE_FLOAT;
					atts.set(attID, AidaWBXMLConverter.toFloat(tag,
							attID, value));
					break;
				case org.freehep.wbxml.Attributes.INT:
					if (attID == -2)
						attID = AidaWBXML.VALUE_INT;
					atts.set(attID, AidaWBXMLConverter.toInt(tag, attID,
							value));
					break;
				case org.freehep.wbxml.Attributes.LONG:
					if (attID == -2)
						attID = AidaWBXML.VALUE_LONG;
					atts.set(attID, Long.parseLong(value));
					break;
				case org.freehep.wbxml.Attributes.SHORT:
					if (attID == -2)
						attID = AidaWBXML.VALUE_SHORT;
					atts.set(attID, Short.parseShort(value));
					break;
				case org.freehep.wbxml.Attributes.STRING:
					if (attID == -2)
						attID = AidaWBXML.VALUE_STRING;
					atts.set(attID, value);
					break;
				default:
					throw new SAXException("Type '" + type
							+ "' not handled for attribute '" + attName
							+ "' in tag '" + tagName + "'");
				}
			}

			boolean empty = AidaWBXMLLookup.isTagEmpty(tag);
			
			// handled by Binary version
			startElement(tag, atts, empty);
		} catch (NumberFormatException e) {
			throw new SAXException("Exception for tag '" + tagName
					+ "', attribute '" + attName + "' value '" + value + "'", e);
		}
	}

	public void endPrefixMapping(String prefix) throws SAXException {
	}

	public void ignorableWhitespace(char[] ch, int start, int length)
			throws SAXException {
	}

	public void processingInstruction(String target, String data)
			throws SAXException {
	}

	public void setDocumentLocator(Locator locator) {
	}

	public void skippedEntity(String name) throws SAXException {
	}

	public void startPrefixMapping(String prefix, String uri)
			throws SAXException {
	}

}
