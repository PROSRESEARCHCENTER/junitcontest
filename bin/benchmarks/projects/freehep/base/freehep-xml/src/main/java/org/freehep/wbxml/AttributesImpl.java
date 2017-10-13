// Copyright FreeHEP, 2007
package org.freehep.wbxml;

import java.util.List;

/**
 * 
 * @author Mark Donszelmann
 * @version $Id$
 */
public class AttributesImpl implements MutableAttributes {

	// FIXME!
	private static final int MAX = 64;

	private int nIdx = 0;
	private int[] type = new int[MAX];
	private int[] idx = new int[MAX];
	private int[] tags = new int[MAX];

	private int nObject;
	private List[] listValue = new List[MAX];

	private int nDoubleValue, nLongValue, nStringValue;
	private long[] longValue = new long[MAX];
	private double[] doubleValue = new double[MAX];
	private String[] stringValue = new String[MAX];

	private int nObjectArray;
	private Object[] objectArray = new Object[MAX];

	public AttributesImpl() {
	}

	public AttributesImpl(Attributes atts) {
		// make a deep copy of atts
		this();

		int[] tags = atts.getTags();
		for (int i = 0; i < tags.length; i++) {
			int tag = tags[i];
			int type = atts.getType(tag);
			// FIXME, arrays are not copied
			switch (type) {
			case BYTE:
				set(tag, atts.getByteValue(tag));
				break;
			case BOOLEAN:
				set(tag, atts.getBooleanValue(tag));
				break;
			case CHAR:
				set(tag, atts.getCharValue(tag));
				break;
			case DOUBLE:
				set(tag, atts.getDoubleValue(tag));
				break;
			case FLOAT:
				set(tag, atts.getFloatValue(tag));
				break;
			case INT:
				set(tag, atts.getIntValue(tag));
				break;
			case LONG:
				set(tag, atts.getLongValue(tag));
				break;
			case SHORT:
				set(tag, atts.getShortValue(tag));
				break;
			case STRING:
				set(tag, atts.getStringValue(tag));
				break;
			default:
				throw new RuntimeException("Copy of Attributes for type: "+type+" of tag: "+tag+" not handled.");
			}
		}
	}

	public void clear() {
		nIdx = 0;

		nObject = 0;

		nDoubleValue = 0;
		nLongValue = 0;
		nStringValue = 0;

		nObjectArray = 0;
	}

	public void set(int tag, boolean value) {
		int index = getIndex(tag);
		if (index < 0) {
			index = nIdx;
			tags[index] = tag;
			type[index] = BOOLEAN;
			idx[index] = nLongValue;
			nLongValue++;
			nIdx++;
		} else {
			if (type[index] != BOOLEAN)
				throw new NumberFormatException();
		}
		longValue[idx[index]] = value ? 1 : 0;
	}

	public void set(int tag, boolean[] array) {
		int index = getIndex(tag);
		if (index < 0) {
			index = nIdx;
			tags[index] = tag;
			type[index] = BOOLEAN_ARRAY;
			idx[index] = nObjectArray;
			nObjectArray++;
			nIdx++;
		} else {
			if (type[index] != BOOLEAN_ARRAY)
				throw new NumberFormatException();
		}
		objectArray[idx[index]] = array;
	}

	public void set(int tag, byte value) {
		int index = getIndex(tag);
		if (index < 0) {
			index = nIdx;
			tags[index] = tag;
			type[index] = BYTE;
			idx[index] = nLongValue;
			nLongValue++;
			nIdx++;
		} else {
			if (type[index] != BYTE)
				throw new NumberFormatException();
		}
		longValue[idx[index]] = value;
	}

	public void set(int tag, byte[] array) {
		int index = getIndex(tag);
		if (index < 0) {
			index = nIdx;
			tags[index] = tag;
			type[index] = BYTE_ARRAY;
			idx[index] = nObjectArray;
			nObjectArray++;
			nIdx++;
		} else {
			if (type[index] != BYTE_ARRAY)
				throw new NumberFormatException();
		}
		objectArray[idx[index]] = array;
	}

	public void set(int tag, char value) {
		int index = getIndex(tag);
		if (index < 0) {
			index = nIdx;
			tags[index] = tag;
			type[index] = CHAR;
			idx[index] = nLongValue;
			nLongValue++;
			nIdx++;
		} else {
			if (type[index] != CHAR)
				throw new NumberFormatException();
		}
		longValue[idx[index]] = value;
	}

	public void set(int tag, char[] array) {
		int index = getIndex(tag);
		if (index < 0) {
			index = nIdx;
			tags[index] = tag;
			type[index] = CHAR_ARRAY;
			idx[index] = nObjectArray;
			nObjectArray++;
			nIdx++;
		} else {
			if (type[index] != CHAR_ARRAY)
				throw new NumberFormatException();
		}
		objectArray[idx[index]] = array;
	}

	public void set(int tag, double value) {
		int index = getIndex(tag);
		if (index < 0) {
			index = nIdx;
			tags[index] = tag;
			type[index] = DOUBLE;
			idx[index] = nDoubleValue;
			nDoubleValue++;
			nIdx++;
		} else {
			if (type[index] != DOUBLE)
				throw new NumberFormatException();
		}
		doubleValue[idx[index]] = value;
	}

	public void set(int tag, double[] array) {
		int index = getIndex(tag);
		if (index < 0) {
			index = nIdx;
			tags[index] = tag;
			type[index] = DOUBLE_ARRAY;
			idx[index] = nObjectArray;
			nObjectArray++;
			nIdx++;
		} else {
			if (type[index] != DOUBLE_ARRAY)
				throw new NumberFormatException();
		}
		objectArray[idx[index]] = array;
	}

	public void set(int tag, float value) {
		int index = getIndex(tag);
		if (index < 0) {
			index = nIdx;
			tags[index] = tag;
			type[index] = FLOAT;
			idx[index] = nDoubleValue;
			nDoubleValue++;
			nIdx++;
		} else {
			if (type[index] != FLOAT)
				throw new NumberFormatException();
		}
		doubleValue[idx[index]] = value;
	}

	public void set(int tag, float[] array) {
		int index = getIndex(tag);
		if (index < 0) {
			index = nIdx;
			tags[index] = tag;
			type[index] = FLOAT_ARRAY;
			idx[index] = nObjectArray;
			nObjectArray++;
			nIdx++;
		} else {
			if (type[index] != FLOAT_ARRAY)
				throw new NumberFormatException();
		}
		objectArray[idx[index]] = array;
	}

	public void set(int tag, int value) {
		int index = getIndex(tag);
		if (index < 0) {
			index = nIdx;
			tags[index] = tag;
			type[index] = INT;
			idx[index] = nLongValue;
			nLongValue++;
			nIdx++;
		} else {
			if (type[index] != INT)
				throw new NumberFormatException();
		}
		longValue[idx[index]] = value;
	}

	public void set(int tag, int[] array) {
		int index = getIndex(tag);
		if (index < 0) {
			index = nIdx;
			tags[index] = tag;
			type[index] = INT_ARRAY;
			idx[index] = nObjectArray;
			nObjectArray++;
			nIdx++;
		} else {
			if (type[index] != INT_ARRAY)
				throw new NumberFormatException();
		}
		objectArray[idx[index]] = array;
	}

	public void set(int tag, long value) {
		int index = getIndex(tag);
		if (index < 0) {
			index = nIdx;
			tags[index] = tag;
			type[index] = LONG;
			idx[index] = nLongValue;
			nLongValue++;
			nIdx++;
		} else {
			if (type[index] != LONG)
				throw new NumberFormatException();
		}
		longValue[idx[index]] = value;
	}

	public void set(int tag, long[] array) {
		int index = getIndex(tag);
		if (index < 0) {
			index = nIdx;
			tags[index] = tag;
			type[index] = LONG_ARRAY;
			idx[index] = nObjectArray;
			nObjectArray++;
			nIdx++;
		} else {
			if (type[index] != LONG_ARRAY)
				throw new NumberFormatException();
		}
		objectArray[idx[index]] = array;
	}

	public void set(int tag, short value) {
		int index = getIndex(tag);
		if (index < 0) {
			index = nIdx;
			tags[index] = tag;
			type[index] = SHORT;
			idx[index] = nLongValue;
			nLongValue++;
			nIdx++;
		} else {
			if (type[index] != SHORT)
				throw new NumberFormatException();
		}
		longValue[idx[index]] = value;
	}

	public void set(int tag, short[] array) {
		int index = getIndex(tag);
		if (index < 0) {
			index = nIdx;
			tags[index] = tag;
			type[index] = SHORT_ARRAY;
			idx[index] = nObjectArray;
			nObjectArray++;
			nIdx++;
		} else {
			if (type[index] != SHORT_ARRAY)
				throw new NumberFormatException();
		}
		objectArray[idx[index]] = array;
	}

	public void set(int tag, String value) {
		int index = getIndex(tag);
		if (index < 0) {
			index = nIdx;
			tags[index] = tag;
			type[index] = STRING;
			idx[index] = nStringValue;
			nStringValue++;
			nIdx++;
		} else {
			if (type[index] != STRING)
				throw new NumberFormatException(
						"Found String while expecting: "
								+ getTypeName(type[index]));
		}
		stringValue[idx[index]] = value;
	}

	public void set(int tag, String[] array) {
		int index = getIndex(tag);
		if (index < 0) {
			index = nIdx;
			tags[index] = tag;
			type[index] = STRING_ARRAY;
			idx[index] = nObjectArray;
			nObjectArray++;
			nIdx++;
		} else {
			if (type[index] != STRING_ARRAY)
				throw new NumberFormatException();
		}
		objectArray[idx[index]] = array;
	}

	public void set(int tag, List value) {
		int index = getIndex(tag);
		if (index < 0) {
			index = nIdx;
			tags[index] = tag;
			type[index] = OBJECT;
			idx[index] = nObject;
			nStringValue++;
			nIdx++;
		} else {
			if (type[index] != OBJECT)
				throw new NumberFormatException();
		}
		listValue[idx[index]] = value;
	}

	private int getIndex(int tag) {
		for (int i = 0; i < nIdx; i++) {
			if (tag == tags[i]) {
				return i;
			}
		}
		return -1;
	}

	public int getType(int tag) {
		int index = getIndex(tag);
		return index < 0 ? -1 : type[index];
	}

	public int[] getTags() {
		int[] result = new int[nIdx];
		System.arraycopy(tags, 0, result, 0, result.length);
		return result;
	}

	public boolean getBooleanValue(int tag, boolean def) {
		int index = getIndex(tag);
		return index < 0 ? def
				: type[index] == BOOLEAN ? longValue[idx[index]] != 0 : def;
	}

	public boolean getBooleanValue(int tag) {
		int index = getIndex(tag);
		if (type[index] != BOOLEAN)
			throw new NumberFormatException();
		return longValue[idx[index]] != 0;
	}

	public boolean[] getBooleanArray(int tag) {
		int index = getIndex(tag);
		if (type[index] != BOOLEAN_ARRAY)
			throw new NumberFormatException();
		return (boolean[]) objectArray[idx[index]];
	}

	public byte getByteValue(int tag, byte def) {
		int index = getIndex(tag);
		return index < 0 ? def
				: type[index] == BYTE ? (byte) longValue[idx[index]] : def;
	}

	public byte getByteValue(int tag) {
		int index = getIndex(tag);
		if (type[index] != BYTE)
			throw new NumberFormatException();
		return (byte) longValue[idx[index]];
	}

	public byte[] getByteArray(int tag) {
		int index = getIndex(tag);
		if (type[index] != BYTE_ARRAY)
			throw new NumberFormatException();
		return (byte[]) objectArray[idx[index]];
	}

	public char getCharValue(int tag, char def) {
		int index = getIndex(tag);
		return index < 0 ? def
				: type[index] == CHAR ? (char) longValue[idx[index]] : def;
	}

	public char getCharValue(int tag) {
		int index = getIndex(tag);
		if (type[index] != CHAR)
			throw new NumberFormatException();
		return (char) longValue[idx[index]];
	}

	public char[] getCharArray(int tag) {
		int index = getIndex(tag);
		if (type[index] != CHAR_ARRAY)
			throw new NumberFormatException();
		return (char[]) objectArray[idx[index]];
	}

	public double getDoubleValue(int tag, double def) {
		int index = getIndex(tag);
		return index < 0 ? def
				: type[index] == DOUBLE ? doubleValue[idx[index]] : def;
	}

	public double getDoubleValue(int tag) {
		int index = getIndex(tag);
		if (type[index] != DOUBLE)
			throw new NumberFormatException();
		return doubleValue[idx[index]];
	}

	public double[] getDoubleArray(int tag) {
		int index = getIndex(tag);
		if (type[index] != DOUBLE_ARRAY)
			throw new NumberFormatException();
		return (double[]) objectArray[idx[index]];
	}

	public float getFloatValue(int tag, float def) {
		int index = getIndex(tag);
		return index < 0 ? def
				: type[index] == FLOAT ? (float) doubleValue[idx[index]] : def;
	}

	public float getFloatValue(int tag) {
		int index = getIndex(tag);
		if (type[index] != FLOAT)
			throw new NumberFormatException();
		return (float) doubleValue[idx[index]];
	}

	public float[] getFloatArray(int tag) {
		int index = getIndex(tag);
		if (type[index] != FLOAT_ARRAY)
			throw new NumberFormatException();
		return (float[]) objectArray[idx[index]];
	}

	public int getIntValue(int tag, int def) {
		int index = getIndex(tag);
		return index < 0 ? def
				: type[index] == INT ? (int) longValue[idx[index]] : def;
	}

	public int getIntValue(int tag) {
		int index = getIndex(tag);
		if (type[index] != INT)
			throw new NumberFormatException();
		return (int) longValue[idx[index]];
	}

	public int[] getIntArray(int tag) {
		int index = getIndex(tag);
		if (type[index] != INT_ARRAY)
			throw new NumberFormatException();
		return (int[]) objectArray[idx[index]];
	}

	public long getLongValue(int tag, long def) {
		int index = getIndex(tag);
		return index < 0 ? def : type[index] == LONG ? longValue[idx[index]]
				: def;
	}

	public long getLongValue(int tag) {
		int index = getIndex(tag);
		if (type[index] != LONG)
			throw new NumberFormatException();
		return longValue[idx[index]];
	}

	public long[] getLongArray(int tag) {
		int index = getIndex(tag);
		if (type[index] != LONG_ARRAY)
			throw new NumberFormatException();
		return (long[]) objectArray[idx[index]];
	}

	public short getShortValue(int tag, short def) {
		int index = getIndex(tag);
		return index < 0 ? def
				: type[index] == SHORT ? (short) longValue[idx[index]] : def;
	}

	public short getShortValue(int tag) {
		int index = getIndex(tag);
		if (type[index] != SHORT)
			throw new NumberFormatException();
		return (short) longValue[idx[index]];
	}

	public short[] getShortArray(int tag) {
		int index = getIndex(tag);
		if (type[index] != SHORT_ARRAY)
			throw new NumberFormatException();
		return (short[]) objectArray[idx[index]];
	}

	public String getStringValue(int tag, String def) {
		int index = getIndex(tag);
		return index < 0 ? def
				: type[index] == STRING ? stringValue[idx[index]] : def;
	}

	public String getStringValue(int tag) {
		int index = getIndex(tag);
		// NOTE: exceptional, we return null if not found
		if (index < 0)
			return null;
		if (type[index] != STRING)
			throw new NumberFormatException();
		return stringValue[idx[index]];
	}

	public String[] getStringArray(int tag) {
		int index = getIndex(tag);
		if (type[index] != STRING_ARRAY)
			throw new NumberFormatException();
		return (String[]) objectArray[idx[index]];
	}

	private String getTypeName(int type) {
		switch (type) {
		case BOOLEAN:
			return "boolean";
		case BYTE:
			return "byte";
		case CHAR:
			return "char";
		case COLOR:
			return "Color";
		case DOUBLE:
			return "double";
		case FLOAT:
			return "float";
		case INT:
			return "int";
		case LONG:
			return "long";
		case OBJECT:
			return "Object";
		case SHORT:
			return "short";
		case STRING:
			return "String";
		default:
			return "Unknown";
		}
	}
}
