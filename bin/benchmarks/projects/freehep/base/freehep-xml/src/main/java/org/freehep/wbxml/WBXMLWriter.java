// Copyright FreeHEP, 2007.
package org.freehep.wbxml;

import java.awt.Color;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Writes out Binary XML see http://www.wapforum.org instead of ASCII XML.
 * 
 * @author Mark Donszelmann
 * @version $Id$
 */
public class WBXMLWriter implements WBXMLTagWriter {

	// outputstream variables
	private DataOutputStream os;
	private String dtd;
	private String version;
	private boolean writtenHeader;

	// Code pages
	private int tagPage;
	private int attributePage;

	// tag attributes
	private boolean hasAttributes;
	private int[] attributeTypes;

	// cache
	private boolean[][] attributeBoolean;
	private int[] attributeBooleanOffset;
	private int[] attributeBooleanLength;

	private byte[][] attributeByte;
	private int[] attributeByteOffset;
	private int[] attributeByteLength;

	private char[][] attributeChar;
	private int[] attributeCharOffset;
	private int[] attributeCharLength;

	private double[][] attributeDouble;
	private int[] attributeDoubleOffset;
	private int[] attributeDoubleLength;

	private float[][] attributeFloat;
	private int[] attributeFloatOffset;
	private int[] attributeFloatLength;

	private int[][] attributeInt;
	private int[] attributeIntOffset;
	private int[] attributeIntLength;

	private long[][] attributeLong;
	private int[] attributeLongOffset;
	private int[] attributeLongLength;

	private short[][] attributeShort;
	private int[] attributeShortOffset;
	private int[] attributeShortLength;

	private String[][] attributeString;
	private int[] attributeStringOffset;
	private int[] attributeStringLength;
	private String encoding;
	private boolean standalone;

	/**
	 * Create a Binary AIDA Writer for given stream
	 * 
	 * @param os
	 *            stream to write to
	 * @param maxAttributeCode largest tagcode for attributes
	 */
	public WBXMLWriter(OutputStream os, int maxAttributeTagCode) throws IOException {
		this.os = (os instanceof DataOutputStream) ? (DataOutputStream) os
				: new DataOutputStream(os);
		tagPage = 0;
		attributePage = 0;

		int len = maxAttributeTagCode+1;
		attributeBoolean = new boolean[len][];
		attributeBooleanOffset = new int[len];
		attributeBooleanLength = new int[len];

		attributeByte = new byte[len][];
		attributeByteOffset = new int[len];
		attributeByteLength = new int[len];

		attributeChar = new char[len][];
		attributeCharOffset = new int[len];
		attributeCharLength = new int[len];

		attributeDouble = new double[len][];
		attributeDoubleOffset = new int[len];
		attributeDoubleLength = new int[len];

		attributeFloat = new float[len][];
		attributeFloatOffset = new int[len];
		attributeFloatLength = new int[len];

		attributeInt = new int[len][];
		attributeIntOffset = new int[len];
		attributeIntLength = new int[len];

		attributeLong = new long[len][];
		attributeLongOffset = new int[len];
		attributeLongLength = new int[len];

		attributeShort = new short[len][];
		attributeShortOffset = new int[len];
		attributeShortLength = new int[len];

		attributeString = new String[len][];
		attributeStringOffset = new int[len];
		attributeStringLength = new int[len];

		attributeTypes = new int[len];
		clearAttributes();
	}

	private void clearAttributes() {
		hasAttributes = false;
		for (int i = 0; i < attributeTypes.length; i++) {
			attributeTypes[i] = -1;
		}
	}

	public void close() throws IOException {
		os.close();
	}

	public void openDoc() throws IOException {
		openDoc("Binary/1.0", "UTF-8", false);
	}

	public void openDoc(String version, String encoding, boolean standalone)
			throws IOException {
		dtd = null;
		writtenHeader = false;
		this.version = version;
		this.encoding = encoding;
		this.standalone = standalone;
	}	
	
	private void writeHeader() throws IOException {
		if (writtenHeader) return;
		
		if (dtd == null) throw new IOException("DTD is missing");
		
		// header
		writeByte(WBXML.WBXML_VERSION);
		writeByte(WBXML.INDEXED_PID);
		writeMultiByteInt(0); // dtd has to be first entry in table.
		writeMultiByteInt(WBXML.UTF8);

		// length string table: add (short) length and (byte) null.
		int len = WBXMLParser.stringUTFLength(dtd) + 2 + 1;
		len += WBXMLParser.stringUTFLength(version) + 2 + 1;
		writeMultiByteInt(len);

		// Binary AIDA Header (as part of the string table)
		writeString(dtd);
		writeString(version);
		
		writtenHeader = true;
	}

	public void closeDoc() throws IOException {
		// ignored
	}

	public void referToDTD(String name, String pid, String ref) {
		// ignored
	}

	public void referToDTD(String name, String system) {
		dtd = name+" "+system;
	}

	public void openTag(int tag) throws IOException {
		writeTag(tag, true);
	}

	public void closeTag() throws IOException {
		writeByte(WBXML.END);
	}

	public void printTag(int tag) throws IOException {
		writeTag(tag, false);
	}

	public void print(String text) throws IOException {
		writeByte(WBXML.STR_I);
		writeString(text);
	}

	public void printComment(String comment) throws IOException {
		// ignored
	}

	private void writeTag(int tag, boolean hasContent) throws IOException {
		if (!writtenHeader) writeHeader();

		// write tag
		int page = tag / WBXML.MAX_CODES;
		if (page != tagPage) {
			writeByte(WBXML.SWITCH_PAGE);
			writeByte(page);
			tagPage = page;
		}
		tag = tag % WBXML.MAX_CODES;
		writeByte((tag + WBXML.RESERVED_CODES)
				| (hasContent ? WBXML.CONTENT : 0x00)
				| (hasAttributes ? WBXML.ATTRIBUTE : 0x00));

		// write attributes
		if (hasAttributes) {
			// write attributes
			for (int i = 0; i < attributeTypes.length; i++) {
				if (attributeTypes[i] >= 0) {
					// write ATTRSTART
					writeAttribute(i);
					switch (attributeTypes[i]) {
					case Attributes.BOOLEAN:
						// write OPAQUE
						writeByte(WBXML.OPAQUE);
						writeMultiByteInt(attributeBooleanLength[i]*1+1);
						writeByte(Attributes.BOOLEAN);
						for (int j = 0; j < attributeBooleanLength[i]; j++) {
							writeByte(attributeBoolean[i][j+attributeBooleanOffset[i]] ? 1 : 0);
						}
						break;
					case Attributes.BYTE:
						// write OPAQUE
						writeByte(WBXML.OPAQUE);
						writeMultiByteInt(attributeByteLength[i]*1+1);
						writeByte(Attributes.BYTE);
						for (int j = 0; j < attributeByteLength[i]; j++) {
							writeByte(attributeByte[i][j+attributeByteOffset[i]]);
						}
						break;
					case Attributes.CHAR:
						// write OPAQUE
						writeByte(WBXML.OPAQUE);
						writeMultiByteInt(attributeCharLength[i]*2+1);
						writeByte(Attributes.CHAR);
						for (int j = 0; j < attributeCharLength[i]; j++) {
							os.writeChar(attributeChar[i][j+attributeCharOffset[i]]);
						}
						break;
					case Attributes.DOUBLE:
						// write OPAQUE
						writeByte(WBXML.OPAQUE);
						writeMultiByteInt(attributeDoubleLength[i]*8+1);
						writeByte(Attributes.DOUBLE);
						for (int j = 0; j < attributeDoubleLength[i]; j++) {
							os.writeDouble(attributeDouble[i][j+attributeDoubleOffset[i]]);
						}
						break;
					case Attributes.FLOAT:
						// write OPAQUE
						writeByte(WBXML.OPAQUE);
						writeMultiByteInt(attributeFloatLength[i]*4+1);
						writeByte(Attributes.FLOAT);
						for (int j = 0; j < attributeFloatLength[i]; j++) {
							os.writeFloat(attributeFloat[i][j+attributeFloatOffset[i]]);
						}
						break;
					case Attributes.INT:
						// write OPAQUE
						writeByte(WBXML.OPAQUE);
						writeMultiByteInt(attributeIntLength[i]*4+1);
						writeByte(Attributes.INT);
						for (int j = 0; j < attributeIntLength[i]; j++) {
							os.writeInt(attributeInt[i][j+attributeIntOffset[i]]);
						}
						break;
					case Attributes.LONG:
						// write OPAQUE
						writeByte(WBXML.OPAQUE);
						writeMultiByteInt(attributeLongLength[i]*8+1);
						writeByte(Attributes.LONG);
						for (int j = 0; j < attributeLongLength[i]; j++) {
							os.writeLong(attributeLong[i][j+attributeLongOffset[i]]);
						}
						break;
					case Attributes.SHORT:
						// write OPAQUE
						writeByte(WBXML.OPAQUE);
						writeMultiByteInt(attributeShortLength[i]*2+1);
						writeByte(Attributes.SHORT);
						for (int j = 0; j < attributeShortLength[i]; j++) {
							os.writeShort(attributeShort[i][j+attributeShortOffset[i]]);
						}
						break;
					case Attributes.STRING:
						// calculate total length
						int length = 0;
						for (int j = 0; j < attributeStringLength[i]; j++) {
							// add (short) length and (byte) null termination
							length += WBXMLParser.stringUTFLength((String)attributeString[i][j+attributeStringOffset[i]]) + 2 + 1;
						}
						
						// write OPAQUE
						writeByte(WBXML.OPAQUE);
						writeMultiByteInt(length+1);
						writeByte(Attributes.STRING);
						
						// write UTF strings
						for (int j = 0; j < attributeStringLength[i]; j++) {
							writeString(attributeString[i][j+attributeStringOffset[i]]);
						}
						break;
					}
				}
			}
			// end of attributes
			writeByte(WBXML.END);
			clearAttributes();
		}
	}

	private void writeAttribute(int tag) throws IOException {
		int page = tag / WBXML.MAX_CODES;
		tag = tag % WBXML.MAX_CODES;
		if (page != attributePage) {
			writeByte(WBXML.SWITCH_PAGE);
			writeByte(page);
			attributePage = page;
		}
		writeByte(tag + WBXML.RESERVED_CODES);
	}

	public void setAttribute(int tag, String value) {
		setAttribute(tag, new String[] { value }, 0, 1);
	}

	public void setAttribute(int tag, String[] value, int offset, int length) {
		hasAttributes = true;
		attributeString[tag] = value;
		attributeStringOffset[tag] = offset;
		attributeStringLength[tag] = length;
		attributeTypes[tag] = Attributes.STRING;
	}

	public void setAttribute(int tag, byte value) {
		setAttribute(tag, new byte[] { value }, 0, 1);
	}

	public void setAttribute(int tag, byte[] value, int offset, int length) {
		hasAttributes = true;
		attributeByte[tag] = value;
		attributeByteOffset[tag] = offset;
		attributeByteLength[tag] = length;
		attributeTypes[tag] = Attributes.BYTE;
	}

	public void setAttribute(int tag, long value) {
		setAttribute(tag, new long[] { value }, 0, 1);
	}

	public void setAttribute(int tag, long[] value, int offset, int length) {
		hasAttributes = true;
		attributeLong[tag] = value;
		attributeLongOffset[tag] = offset;
		attributeLongLength[tag] = length;
		attributeTypes[tag] = Attributes.LONG;
	}

	public void setAttribute(int tag, int value) {
		setAttribute(tag, new int[] { value }, 0, 1);
	}

	public void setAttribute(int tag, int[] value, int offset, int length) {
		hasAttributes = true;
		attributeInt[tag] = value;
		attributeIntOffset[tag] = offset;
		attributeIntLength[tag] = length;
		attributeTypes[tag] = Attributes.INT;
	}

	public void setAttribute(int tag, boolean value) {
		setAttribute(tag, new boolean[] { value }, 0, 1);
	}

	public void setAttribute(int tag, boolean[] value, int offset, int length) {
		hasAttributes = true;
		attributeBoolean[tag] = value;
		attributeBooleanOffset[tag] = offset;
		attributeBooleanLength[tag] = length;
		attributeTypes[tag] = Attributes.BOOLEAN;
	}

	public void setAttribute(int tag, float value) {
		setAttribute(tag, new float[] { value }, 0, 1);
	}

	public void setAttribute(int tag, float[] value, int offset, int length) {
		hasAttributes = true;
		attributeFloat[tag] = value;
		attributeFloatOffset[tag] = offset;
		attributeFloatLength[tag] = length;
		attributeTypes[tag] = Attributes.FLOAT;
	}

	public void setAttribute(int tag, double value) {
		setAttribute(tag, new double[] { value }, 0, 1);
	}

	public void setAttribute(int tag, double[] value, int offset, int length) {
		hasAttributes = true;
		attributeDouble[tag] = value;
		attributeDoubleOffset[tag] = offset;
		attributeDoubleLength[tag] = length;
		attributeTypes[tag] = Attributes.DOUBLE;
	}

	public void setAttribute(int tag, Color value) {
		setAttribute(tag, new Color[] { value }, 0, 1);
	}

	public void setAttribute(int tag, Color[] value, int offset, int length) {
		// ignored
	}

	public void setAttribute(int tag, char value) {
		setAttribute(tag, new char[] { value }, 0, 1);
	}

	public void setAttribute(int tag, char[] value, int offset, int length) {
		hasAttributes = true;
		attributeChar[tag] = value;
		attributeCharOffset[tag] = offset;
		attributeCharLength[tag] = length;
		attributeTypes[tag] = Attributes.CHAR;
	}

	public void setAttribute(int tag, short value) {
		setAttribute(tag, new short[] { value }, 0, 1);
	}

	public void setAttribute(int tag, short[] value, int offset, int length) {
		hasAttributes = true;
		attributeShort[tag] = value;
		attributeShortOffset[tag] = offset;
		attributeShortLength[tag] = length;
		attributeTypes[tag] = Attributes.SHORT;
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

	private void writeByte(int b) throws IOException {
		os.writeByte(b);
	}

	private void writeString(String s) throws IOException {
		os.writeUTF(s);
		os.writeByte(0);
	}

}
