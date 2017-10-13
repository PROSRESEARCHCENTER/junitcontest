// Copyright FreeHEP, 2007
package org.freehep.wbxml;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.xml.sax.SAXException;

public class DefaultExtensionHandler implements ExtensionHandler {

	private List strings = new ArrayList();

	public void ext(int i, int tagID, int attributeID,
			MutableAttributes atts, List value) throws SAXException {
		throw new SAXException("Unknown extension: Ext " + i + " tagID: " + tagID
				+ " attributeID: " + attributeID);
	}

	public void extI(int i, String s, int tagID,
			int attributeID, MutableAttributes atts, List value) throws SAXException {
		if (i==0) {
			atts.set(attributeID, s);
			strings.add(s);
		} else {
			throw new SAXException("Unknown extension: ExtI " + i + " " + s + " tagID: "
				+ tagID + " attributeID: " + attributeID);
		}
	}

	public void extT(int i, int index, int tagID, int attributeID,
			MutableAttributes atts, List value) throws SAXException {
		if (i==0) {
			atts.set(attributeID, (String)strings.get(index));
		} else {
			throw new SAXException("Unknown extension: ExtT " + i + " " + index + " tagID: "
				+ tagID + " attributeID: " + attributeID);
		}
	}

	public void opaque(int len, DataInputStream in, int tagID,
			int attributeID, MutableAttributes atts, List value)
			throws IOException, SAXException {
		int type = in.readByte();
		len--;
		switch(type) {
		case Attributes.BOOLEAN:
			if (len == 1) {
				atts.set(attributeID, in.readByte() != 0);
				len--;
			} else {
				boolean[] array = new boolean[len];
				for (int i=0; i<array.length; i++) {
					array[i] = in.readByte() != 0;
					len--;
				}
				atts.set(attributeID, array);
			}
			break;
		case Attributes.BYTE:
			if (len == 1) {
				atts.set(attributeID, in.readByte());
				len--;
			} else {
				byte[] array = new byte[len];
				for (int i=0; i<array.length; i++) {
					array[i] = in.readByte();
					len--;
				}
				atts.set(attributeID, array);
			}
			break;
		case Attributes.CHAR:
			if (len == 2) {
				atts.set(attributeID, in.readChar());
				len-=2;
			} else {
				char[] array = new char[len/2];
				for (int i=0; i<array.length; i++) {
					array[i] = in.readChar();
					len-=2;
				}
				atts.set(attributeID, array);
			}
			break;
		case Attributes.DOUBLE:
			if (len == 8) {
				atts.set(attributeID, in.readDouble());
				len -= 8;
			} else {
				double[] array = new double[len/8];
				for (int i=0; i<array.length; i++) {
					array[i] = in.readDouble();
					len-=8;
				}
				atts.set(attributeID, array);
			}
			break;
		case Attributes.FLOAT:
			if (len == 4) {
				atts.set(attributeID, in.readFloat());
				len -= 4;
			} else {
				float[] array = new float[len/4];
				for (int i=0; i<array.length; i++) {
					array[i] = in.readFloat();
					len-=4;
				}
				atts.set(attributeID, array);
			}
			break;
		case Attributes.INT:
			if (len == 4) {
				atts.set(attributeID, in.readInt());
				len -= 4;
			} else {
				int[] array = new int[len/4];
				for (int i=0; i<array.length; i++) {
					array[i] = in.readInt();
					len-=4;
				}
				atts.set(attributeID, array);
			}
			break;
		case Attributes.LONG:
			if (len == 8) {
				atts.set(attributeID, in.readLong());
				len -= 8;
			} else {
				long[] array = new long[len/8];
				for (int i=0; i<array.length; i++) {
					array[i] = in.readLong();
					len-=8;
				}
				atts.set(attributeID, array);
			}
			break;
		case Attributes.SHORT:
			if (len == 2) {
				atts.set(attributeID, in.readShort());
				len -= 2;
			} else {
				short[] array = new short[len/2];
				for (int i=0; i<array.length; i++) {
					array[i] = in.readShort();
					len-=2;
				}
				atts.set(attributeID, array);
			}
			break;
		case Attributes.STRING:
			List strings = new ArrayList();
			while(len > 0) {
				String s = in.readUTF();
				in.readByte(); // skip NULL termination
				strings.add(s);
				// len (short) + null (byte)
				len -= WBXMLParser.stringUTFLength(s) + 2 + 1;
			}
			if (strings.size() == 1) {
				atts.set(attributeID, (String)strings.get(0));
			} else {
				String[] array = new String[strings.size()];
				array = (String[])strings.toArray(array);
				atts.set(attributeID, array);
			}
			break;
		default:
			System.err.println("Unknown extension: Opaque " + len + " tagID: " + tagID
					+ " attributeID: " + attributeID);
		}
		if (len > 0) {
			System.err.println("Skipping "+len+" unused OPAQUE bytes...");
			while (len > 0) {
				in.readByte();
				len--;
			}
		}
	}

}
