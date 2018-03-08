// Copyright 2007, FreeHEP.
package hep.aida.ref.xml.ascii;

import hep.aida.ref.xml.binary.AidaWBXML;
import hep.aida.ref.xml.binary.AidaWBXMLLookup;

import java.awt.Color;
import java.io.IOException;
import java.io.Writer;

import org.freehep.wbxml.WBXMLTagWriter;
import org.freehep.xml.util.XMLTagWriter;
import org.freehep.xml.util.XMLWriter;

/**
 * Delegates the writing of tags to an ASCII XML Writer
 * 
 * @author Mark Donszelmann
 * @version $Id$
 */		
public class AidaAsciiXMLWriter implements WBXMLTagWriter {

	private XMLTagWriter xml;
	
	public AidaAsciiXMLWriter(Writer writer) {
		xml = new XMLWriter(writer);
	}
	
	public void close() throws IOException {
		xml.close();
	}

	public void closeDoc() throws IOException {
		xml.closeDoc();
	}

	public void closeTag() throws IOException {
		xml.closeTag();
	}

	public void openDoc() throws IOException {
		xml.openDoc();
	}

	public void openDoc(String version, String encoding, boolean standalone)
			throws IOException {
		xml.openDoc(version, encoding, standalone);
	}

	public void openTag(int tag) throws IOException {
		xml.openTag(AidaWBXMLLookup.getTagName(tag));
	}

	public void print(String text) throws IOException {
		xml.print(text);
	}

	public void printComment(String comment) throws IOException {
		xml.printComment(comment);
	}

	public void printTag(int tag) throws IOException {
		xml.printTag(AidaWBXMLLookup.getTagName(tag));
	}

	public void referToDTD(String name, String system) {
		xml.referToDTD(name, system);
	}

	public void referToDTD(String name, String pid, String ref) {
		xml.referToDTD(name, pid, ref);
	}

	private String getAttributeName(int tag) {
		switch(tag) {
		case AidaWBXML.VALUE_BOOLEAN:
		case AidaWBXML.VALUE_BYTE:
		case AidaWBXML.VALUE_CHAR:
		case AidaWBXML.VALUE_DOUBLE:
		case AidaWBXML.VALUE_FLOAT:
		case AidaWBXML.VALUE_INT:
		case AidaWBXML.VALUE_LONG:
		case AidaWBXML.VALUE_SHORT:
		case AidaWBXML.VALUE_STRING:
			return "value";
		default:
			return AidaWBXMLLookup.getAttributeName(tag);
		}
	}
	
	public void setAttribute(int tag, String value) {
		xml.setAttribute(getAttributeName(tag), value);
	}
	
	public void setAttribute(int tag, String[] value, int offset, int length) {
		throw new RuntimeException("setAttribute ASCII XML (tag:"+tag+") not implemented for arrays.");
	}

	public void setAttribute(int tag, Color value) {
		xml.setAttribute(getAttributeName(tag), value);
	}

	public void setAttribute(int tag, Color[] value, int offset, int length) {
		throw new RuntimeException("setAttribute ASCII XML (tag:"+tag+") not implemented for arrays.");
	}

	public void setAttribute(int tag, byte value) {
		xml.setAttribute(getAttributeName(tag), value);
	}

	public void setAttribute(int tag, byte[] value, int offset, int length) {
		throw new RuntimeException("setAttribute ASCII XML (tag:"+tag+") not implemented for arrays.");
	}

	public void setAttribute(int tag, char value) {
		xml.setAttribute(getAttributeName(tag), value);
	}

	public void setAttribute(int tag, char[] value, int offset, int length) {
		throw new RuntimeException("setAttribute ASCII XML (tag:"+tag+") not implemented for arrays.");
	}

	public void setAttribute(int tag, long value) {
		xml.setAttribute(getAttributeName(tag), value);
	}

	public void setAttribute(int tag, long[] value, int offset, int length) {
		throw new RuntimeException("setAttribute ASCII XML (tag:"+tag+") not implemented for arrays.");
	}

	public void setAttribute(int tag, int value) {
		String name = getAttributeName(tag);
		switch(tag) {
/*	
        case AidaWBXML.DIRECTION:
			switch(value) {
			case 0:
			default:
				xml.setAttribute(name, "x");
				break;
			case 1:
				xml.setAttribute(name, "y");
				break;
			case 2:
				xml.setAttribute(name, "z");
				break;
			}
			break;
*/
		case AidaWBXML.BIN_NUM:
		case AidaWBXML.BIN_NUM_X:
		case AidaWBXML.BIN_NUM_Y:
		case AidaWBXML.BIN_NUM_Z:
			switch(value) {
			case -2:
				xml.setAttribute(name, "UNDERFLOW");
				break;
			case -1:
				xml.setAttribute(name, "OVERFLOW");
				break;
			default:
				xml.setAttribute(name, value);
				break;			
			}
			break;
		default:
			xml.setAttribute(name, value);
			break;
		}
	}

	public void setAttribute(int tag, int[] value, int offset, int length) {
		throw new RuntimeException("setAttribute ASCII XML (tag:"+tag+") not implemented for arrays.");
	}

	public void setAttribute(int tag, short value) {
		xml.setAttribute(getAttributeName(tag), value);
	}

	public void setAttribute(int tag, short[] value, int offset, int length) {
		throw new RuntimeException("setAttribute ASCII XML (tag:"+tag+") not implemented for arrays.");
	}

	public void setAttribute(int tag, boolean value) {
		xml.setAttribute(getAttributeName(tag), value);
	}

	public void setAttribute(int tag, boolean[] value, int offset, int length) {
		throw new RuntimeException("setAttribute ASCII XML (tag:"+tag+") not implemented for arrays.");
	}

	public void setAttribute(int tag, float value) {
		xml.setAttribute(getAttributeName(tag), value);
	}

	public void setAttribute(int tag, float[] value, int offset, int length) {
		throw new RuntimeException("setAttribute ASCII XML (tag:"+tag+") not implemented for arrays.");
	}

	public void setAttribute(int tag, double value) {
		xml.setAttribute(getAttributeName(tag), value);
	}

	public void setAttribute(int tag, double[] value, int offset, int length) {
		throw new RuntimeException("setAttribute ASCII XML (tag:"+tag+") not implemented for arrays.");
	}
}
