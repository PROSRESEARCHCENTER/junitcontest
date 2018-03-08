#include <iostream>
#include <istream>

#include <WBXML/DefaultExtensionHandler.h>

using namespace std;
using namespace WBXML;

bool DefaultExtensionHandler::ext(unsigned int i, unsigned int tagID,
		int attributeID, MutableAttributes& atts, vector<wstring>& value) {
	if (debug) cout << endl << "Ext "<< i << " tagID: "<< tagID<< " attributeID: "
			<< attributeID;
	return true;
}

bool DefaultExtensionHandler::extI(unsigned int i, wstring s,
		unsigned int tagID, int attributeID, MutableAttributes& atts,
		vector<wstring>& value) {
	if (debug) wcout << endl << "ExtI "<< i << " "<< s << " tagID: "<< tagID
			<< " attributeID: "<< attributeID;
	if (i == 0) {
		atts.set(attributeID, s);
		strings.push_back(s);
	}
	return true;
}

bool DefaultExtensionHandler::extT(unsigned int i, unsigned int index,
		unsigned int tagID, int attributeID, MutableAttributes& atts,
		vector<wstring>& value) {
	if (debug) cout << endl << "ExtT "<< i << " "<< index << " tagID: "<< tagID
			<< " attributeID: "<< attributeID;
	if (i==0) {
		atts.set(attributeID, strings[index]);
	}
	return true;
}

// FIXME, no handling of arrays yet
bool DefaultExtensionHandler::opaque(unsigned int len, IReader& reader,
		unsigned int tagID, int attributeID, MutableAttributes& atts,
		vector<wstring>& value) {
	// read type
	unsigned char type;
	if (!reader.readByte(type))
		return false;
	if (debug) cout << " : Type: "<< atts.getTypeName((IAttributes::Types)type);
	len--;

	switch (type) {
	case IAttributes::BOOLEAN:
		if (len == 1) {
			bool b;
			if (!reader.readBoolean(b))
				return false;
			atts.set(attributeID, b);
			len--;
		} else {
			//			boolean[] array = new boolean[len];
			//			for (int i=0; i<array.length; i++) {
			//				array[i] = in.readByte() != 0;
			//				len--;
			//			}
			//			atts.set(attributeID, array);
		}
		break;
	case IAttributes::BYTE:
		if (len == 1) {
			unsigned char b;
			if (!reader.readByte(b))
				return false;
			atts.set(attributeID, b);
			len--;
		} else {
			//			byte[] array = new byte[len];
			//			for (int i=0; i<array.length; i++) {
			//				array[i] = in.readByte();
			//				len--;
			//			}
			//			atts.set(attributeID, array);
		}
		break;
	case IAttributes::CHAR:
		if (len == 2) {
			wchar_t c;
			if (!reader.readChar(c))
				return false;
			atts.set(attributeID, c);
			len-=2;
		} else {
			//			char[] array = new char[len/2];
			//			for (int i=0; i<array.length; i++) {
			//				array[i] = in.readChar();
			//				len-=2;
			//			}
			//			atts.set(attributeID, array);
		}
		break;
	case IAttributes::DOUBLE:
		if (len == 8) {
			double d;
			if (!reader.readDouble(d))
				return false;
			atts.set(attributeID, d);
			len -= 8;
		} else {
			//			double[] array = new double[len/8];
			//			for (int i=0; i<array.length; i++) {
			//				array[i] = in.readDouble();
			//				len-=8;
			//			}
			//			atts.set(attributeID, array);
		}
		break;
	case IAttributes::FLOAT:
		if (len == 4) {
			float f;
			if (!reader.readFloat(f))
				return false;
			atts.set(attributeID, f);
			len -= 4;
		} else {
			//			float[] array = new float[len/4];
			//			for (int i=0; i<array.length; i++) {
			//				array[i] = in.readFloat();
			//				len-=4;
			//			}
			//			atts.set(attributeID, array);
		}
		break;
	case IAttributes::INT:
		if (len == 4) {
			int i;
			if (!reader.readInt(i))
				return false;
			atts.set(attributeID, i);
			len -= 4;
		} else {
			//			int[] array = new int[len/4];
			//			for (int i=0; i<array.length; i++) {
			//				array[i] = in.readInt();
			//				len-=4;
			//			}
			//			atts.set(attributeID, array);
		}
		break;
	case IAttributes::LONG:
		if (len == 8) {
			int64 l;
			if (!reader.readLong(l))
				return false;
			atts.set(attributeID, l);
			len -= 8;
		} else {
			//			long[] array = new long[len/8];
			//			for (int i=0; i<array.length; i++) {
			//				array[i] = in.readLong();
			//				len-=8;
			//			}
			//			atts.set(attributeID, array);
		}
		break;
	case IAttributes::SHORT:
		if (len == 2) {
			short s;
			if (!reader.readShort(s))
				return false;
			atts.set(attributeID, s);
			len -= 2;
		} else {
			//			short[] array = new short[len/2];
			//			for (int i=0; i<array.length; i++) {
			//				array[i] = in.readShort();
			//				len-=2;
			//			}
			//			atts.set(attributeID, array);
		}
		break;
	case IAttributes::STRING: {
		vector<wstring> vs;
		while (len != 0) {
			wstring s;
			unsigned short utflen;
			if (!reader.readUTF(s, utflen))
				return false;
			unsigned char b;
			if (!reader.readByte(b))
				return false; // skip NULL termination
			vs.push_back(s);
			len -= utflen + 2+ 1;
		}
		if (vs.size() == 1) {
			atts.set(attributeID, vs[0]);
		} else {
			//			String[] array = new String[vs.size()];
			//			array = (String[])vs.toArray(array);
			//			atts.set(attributeID, array);
		}
	}
		break;
	default:
		cerr << endl << "ERROR No opaque handler for attributeID: "<< attributeID
				<< " part of tagID: "<< tagID << endl;
		return false;
	}
	if (debug) cout << " : Value";
	
	if (len != 0) {
		cerr << endl << "Skipping "<< len << " unused OPAQUE bytes..."<< endl;
		while (len != 0) {
			unsigned char b;
			if (!reader.readByte(b))
				return false;
			len--;
		}
	}
	return true;
}
