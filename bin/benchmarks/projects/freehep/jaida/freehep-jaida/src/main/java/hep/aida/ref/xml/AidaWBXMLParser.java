// Copyright FreeHEP, 2007.
package hep.aida.ref.xml;

import hep.aida.ref.xml.binary.AidaWBXML;
import hep.aida.ref.xml.binary.AidaWBXMLLookup;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Stack;

import org.freehep.wbxml.Attributes;
import org.freehep.wbxml.ContentHandler;
import org.freehep.wbxml.WBXMLParser;
import org.xml.sax.SAXException;

/**
 * This class reads Binary XML.
 * 
 * @author Mark Donszelmann
 * @version $Id$
 */
public class AidaWBXMLParser implements ContentHandler {

	private StringBuffer buffer;
	private AidaBinaryHandler handler;
	private Stack context;

	/**
	 * Creates a parser instance.
	 * 
	 * @param handler
	 *            handler interface implementation (never <code>null</code>
	 * @param resolver
	 *            SAX entity resolver implementation or <code>null</code>. It
	 *            is recommended that it could be able to resolve at least the
	 *            DTD.
	 */
	public AidaWBXMLParser(final AidaBinaryHandler handler) {
		this.handler = handler;
		buffer = new StringBuffer(111);
		context = new Stack();
	}

	public void startDocument() throws SAXException {
	}

	public void endDocument() throws SAXException {
	}

	public void startElement(int tagID, Attributes attrs, boolean empty) throws SAXException {
		dispatch(true);
		// FIXME: maybe a copy
		context.push(new Object[] { new Integer(tagID), attrs });

		switch (tagID) {
		case AidaWBXML.ITEM:
			handler.handle_item(attrs);
			break;
		case AidaWBXML.ENTRIES_1D:
			handler.start_entries1d(attrs);
			break;
		case AidaWBXML.ARGUMENT:
			handler.start_argument(attrs);
			break;
		case AidaWBXML.DATA_1D:
			handler.start_data1d(attrs);
			break;
		case AidaWBXML.BIN_1D:
			handler.handle_bin1d(attrs);
			break;
		case AidaWBXML.ENTRY_ITUPLE:
			handler.start_entryITuple(attrs);
			break;
		case AidaWBXML.COLUMN:
			handler.handle_column(attrs);
			break;
		case AidaWBXML.TUPLE:
			handler.start_tuple(attrs);
			break;
		case AidaWBXML.FUNCTION:
			handler.start_function(attrs);
			break;
		case AidaWBXML.COLUMNS:
			handler.start_columns(attrs);
			break;
		case AidaWBXML.DATA_POINT_SET:
			handler.start_dataPointSet(attrs);
			break;
		case AidaWBXML.DATA_POINT:
			handler.start_dataPoint(attrs);
			break;
		case AidaWBXML.IMPLEMENTATION:
			handler.handle_implementation(attrs);
			break;
		case AidaWBXML.ENTRY_1D:
			handler.handle_entry1d(attrs);
			break;
		case AidaWBXML.CLOUD_1D:
			handler.start_cloud1d(attrs);
			break;
		case AidaWBXML.ROW:
			handler.start_row(attrs);
			break;
		case AidaWBXML.ANNOTATION:
			handler.start_annotation(attrs);
			break;
		case AidaWBXML.ENTRY_3D:
			handler.handle_entry3d(attrs);
			break;
		case AidaWBXML.HISTOGRAM_3D:
			handler.start_histogram3d(attrs);
			break;
		case AidaWBXML.AIDA:
			handler.start_aida(attrs);
			break;
		case AidaWBXML.ENTRY:
			handler.handle_entry(attrs);
			break;
		case AidaWBXML.DATA_2D:
			handler.start_data2d(attrs);
			break;
		case AidaWBXML.BIN_3D:
			handler.handle_bin3d(attrs);
			break;
		case AidaWBXML.PARAMETER:
			handler.handle_parameter(attrs);
			break;
		case AidaWBXML.ARGUMENTS:
			handler.start_arguments(attrs);
			break;
		case AidaWBXML.PARAMETERS:
			handler.start_parameters(attrs);
			break;
		case AidaWBXML.ROWS:
			handler.start_rows(attrs);
			break;
		case AidaWBXML.HISTOGRAM_1D:
			handler.start_histogram1d(attrs);
			break;
		case AidaWBXML.AXIS:
			handler.start_axis(attrs);
			break;
		case AidaWBXML.CLOUD_3D:
			handler.start_cloud3d(attrs);
			break;
		case AidaWBXML.BIN_BORDER:
			handler.handle_binBorder(attrs);
			break;
		case AidaWBXML.RANGE:
			handler.handle_range(attrs);
			break;
		case AidaWBXML.MEASUREMENT:
			handler.handle_measurement(attrs);
			break;
		case AidaWBXML.DATA_3D:
			handler.start_data3d(attrs);
			break;
		case AidaWBXML.ENTRY_2D:
			handler.handle_entry2d(attrs);
			break;
		case AidaWBXML.PROFILE_1D:
			handler.start_profile1d(attrs);
			break;
		case AidaWBXML.ENTRIES_2D:
			handler.start_entries2d(attrs);
			break;
		case AidaWBXML.ENTRIES_3D:
			handler.start_entries3d(attrs);
			break;
		case AidaWBXML.BIN_2D:
			handler.handle_bin2d(attrs);
			break;
		case AidaWBXML.STATISTICS:
			handler.start_statistics(attrs);
			break;
		case AidaWBXML.PROFILE_2D:
			handler.start_profile2d(attrs);
			break;
		case AidaWBXML.CLOUD_2D:
			handler.start_cloud2d(attrs);
			break;
		case AidaWBXML.STATISTIC:
			handler.handle_statistic(attrs);
			break;
		case AidaWBXML.HISTOGRAM_2D:
			handler.start_histogram2d(attrs);
			break;
		}
		
		if (empty) {
			dispatch(false);
			context.pop();
		}
	}

	/**
	 * This SAX interface method is implemented by the parser.
	 * 
	 */
	public void endElement(int tagID) throws SAXException {
		dispatch(false);
		context.pop();

		switch (tagID) {
		case AidaWBXML.ENTRIES_1D:
			handler.end_entries1d();
			break;
		case AidaWBXML.ARGUMENT:
			handler.end_argument();
			break;
		case AidaWBXML.DATA_1D:
			handler.end_data1d();
			break;
		case AidaWBXML.ENTRY_ITUPLE:
			handler.end_entryITuple();
			break;
		case AidaWBXML.TUPLE:
			handler.end_tuple();
			break;
		case AidaWBXML.FUNCTION:
			handler.end_function();
			break;
		case AidaWBXML.COLUMNS:
			handler.end_columns();
			break;
		case AidaWBXML.DATA_POINT_SET:
			handler.end_dataPointSet();
			break;
		case AidaWBXML.DATA_POINT:
			handler.end_dataPoint();
			break;
		case AidaWBXML.CLOUD_1D:
			handler.end_cloud1d();
			break;
		case AidaWBXML.ROW:
			handler.end_row();
			break;
		case AidaWBXML.ANNOTATION:
			handler.end_annotation();
			break;
		case AidaWBXML.HISTOGRAM_3D:
			handler.end_histogram3d();
			break;
		case AidaWBXML.AIDA:
			handler.end_aida();
			break;
		case AidaWBXML.DATA_2D:
			handler.end_data2d();
			break;
		case AidaWBXML.ARGUMENTS:
			handler.end_arguments();
			break;
		case AidaWBXML.PARAMETERS:
			handler.end_parameters();
			break;
		case AidaWBXML.ROWS:
			handler.end_rows();
			break;
		case AidaWBXML.HISTOGRAM_1D:
			handler.end_histogram1d();
			break;
		case AidaWBXML.AXIS:
			handler.end_axis();
			break;
		case AidaWBXML.CLOUD_3D:
			handler.end_cloud3d();
			break;
		case AidaWBXML.DATA_3D:
			handler.end_data3d();
			break;
		case AidaWBXML.PROFILE_1D:
			handler.end_profile1d();
			break;
		case AidaWBXML.ENTRIES_2D:
			handler.end_entries2d();
			break;
		case AidaWBXML.ENTRIES_3D:
			handler.end_entries3d();
			break;
		case AidaWBXML.STATISTICS:
			handler.end_statistics();
			break;
		case AidaWBXML.PROFILE_2D:
			handler.end_profile2d();
			break;
		case AidaWBXML.CLOUD_2D:
			handler.end_cloud2d();
			break;
		case AidaWBXML.HISTOGRAM_2D:
			handler.end_histogram2d();
			break;
		}
	}

	public void characters(char[] chars, int start, int len)
			throws SAXException {
		buffer.append(chars, start, len);
	}

	/*
	 * public final void ignorableWhitespace(char[] chars, int start, int len)
	 * throws SAXException { }
	 * 
	 * public final void processingInstruction(java.lang.String target,
	 * java.lang.String data) throws SAXException { }
	 * 
	 * public final void skippedEntity(java.lang.String name) throws
	 * SAXException { }
	 */
	private void dispatch(final boolean fireOnlyIfMixed) throws SAXException {
		if (fireOnlyIfMixed && buffer.length() == 0)
			return; // skip it

		Object[] ctx = (Object[]) context.peek();
		int here = ((Integer) ctx[0]).intValue();
		Attributes attrs = (Attributes) ctx[1];
		if (AidaWBXML.CODELET == here) {
			if (fireOnlyIfMixed)
				throw new IllegalStateException(
						"Unexpected characters() event!");
			handler.handle_codelet(buffer.length() == 0 ? null : buffer
					.toString(), attrs);
		} else {
			// do not care
		}
		buffer.delete(0, buffer.length());
	}

	public void parse(InputStream in) throws SAXException, IOException {
		WBXMLParser p = new WBXMLParser(this);
		p.parse(in);
	}

	public static void main(String[] args) throws Exception {
		if (args.length < 1) {
			System.err.println("Usage: AidaWBXMLParser filename");
			System.exit(1);
		}

		AidaWBXMLParser handler = new AidaWBXMLParser(null) {

			public void characters(char[] chars, int start, int len)
					throws SAXException {
				System.err.print("'" + String.valueOf(chars) + "'");
			}

			public void endDocument() throws SAXException {
				System.err.println("END DOCUMENT");
			}

			public void endElement(int tagID) throws SAXException {
				System.err.println("</" + AidaWBXMLLookup.getTagName(tagID)
						+ ">");
			}

			public void startDocument() throws SAXException {
				System.err.println("START DOCUMENT");
			}

			public void startElement(int tagID, Attributes attr, boolean empty)
					throws SAXException {
				System.err.println("<" + AidaWBXMLLookup.getTagName(tagID));
				int[] tags = attr.getTags();
				for (int i = 0; i < tags.length; i++) {
					switch (attr.getType(tags[i])) {
					case Attributes.BOOLEAN:
						System.err.println("  "
								+ AidaWBXMLLookup.getAttributeName(tags[i])
								+ "=" + attr.getBooleanValue(tags[i]));
						break;
					case Attributes.BYTE:
						System.err.println("  "
								+ AidaWBXMLLookup.getAttributeName(tags[i])
								+ "=" + attr.getByteValue(tags[i]));
						break;
					case Attributes.CHAR:
						System.err.println("  "
								+ AidaWBXMLLookup.getAttributeName(tags[i])
								+ "=" + attr.getCharValue(tags[i]));
						break;
					case Attributes.DOUBLE:
						System.err.println("  "
								+ AidaWBXMLLookup.getAttributeName(tags[i])
								+ "=" + attr.getDoubleValue(tags[i]));
						break;
					case Attributes.FLOAT:
						System.err.println("  "
								+ AidaWBXMLLookup.getAttributeName(tags[i])
								+ "=" + attr.getFloatValue(tags[i]));
						break;
					case Attributes.INT:
						System.err.println("  "
								+ AidaWBXMLLookup.getAttributeName(tags[i])
								+ "=" + attr.getIntValue(tags[i]));
						break;
					case Attributes.LONG:
						System.err.println("  "
								+ AidaWBXMLLookup.getAttributeName(tags[i])
								+ "=" + attr.getLongValue(tags[i]));
						break;
					case Attributes.SHORT:
						System.err.println("  "
								+ AidaWBXMLLookup.getAttributeName(tags[i])
								+ "=" + attr.getShortValue(tags[i]));
						break;
					case Attributes.STRING:
						System.err.println("  "
								+ AidaWBXMLLookup.getAttributeName(tags[i])
								+ "=\"" + attr.getStringValue(tags[i]) + "\"");
						break;
					default:
						System.err.println("Unhandled attribute type: "
								+ attr.getType(tags[i]));
						break;
					}
				}
				if (empty) System.err.print("/");
				System.err.println(">");
			}

		};
		WBXMLParser p = new WBXMLParser(handler);
		p.parse(new FileInputStream(args[0]));
	}
}
