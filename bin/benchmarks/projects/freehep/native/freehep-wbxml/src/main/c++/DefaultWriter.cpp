#include <sstream>

#include <WBXML/IAttributes.h>
#include <WBXML/WBXML.h>
#include <WBXML/DefaultWriter.h>

using namespace std;
using namespace WBXML;

DefaultWriter::DefaultWriter(ostream& out) :
	os(out) {

	union {
		short s;
		char c[sizeof(short)];
	} un;
	un.s = 0x0102;
	lowEndian = un.c[0] == 2&& un.c[1] == 1;

	tagPage = 0;
	attributePage = 0;
	clearAttributes();
}

void DefaultWriter::openDoc(wstring _version, string _encoding, bool _standalone) {
	//		stringValues.clear();
	dtd =L"";
	writtenHeader = false;
	version = _version;
	encoding = _encoding;
	standalone = _standalone;
}

void DefaultWriter::close() {
	//	os.close();
}

void DefaultWriter::closeDoc() {
	// ignored
}

void DefaultWriter::referToDTD(wstring name, wstring pid, wstring ref) {
	// ignored
}

void DefaultWriter::referToDTD(wstring name, wstring system) {
	wstringstream s;
	s << name << " "<< system;
	dtd = s.str();
}

void DefaultWriter::writeHeader() {
	if (writtenHeader)
		return;

	if (dtd ==L"" ) {
		cerr << "DTD is missing" << endl;
		exit(1);
	}

	// header
	writeByte(WBXML_VERSION);
	writeByte(WBXML_INDEXED_PID);
	writeMultiByteInt(0); // dtd has to be first entry in table.
	writeMultiByteInt(WBXML_UTF8);

	// length string table: add (short) length and (byte) null.
	int len = stringUTFLength(dtd) + 2 + 1;
	len += stringUTFLength(version) + 2 + 1;
	writeMultiByteInt(len);

	// Binary AIDA Header (as part of the string table)
	writeString(dtd);
	writeString(version);

	writtenHeader = true;
}

void DefaultWriter::openTag(unsigned int tag) {
	writeTag(tag, true);
}

void DefaultWriter::closeTag() {
	writeByte(WBXML::END);
}

void DefaultWriter::printTag(unsigned int tag) {
	writeTag(tag, false);
}

void DefaultWriter::print(wstring text, unsigned int start, unsigned int len) {
	writeByte(WBXML::STR_I);
	writeString(text, start, len);
}

void DefaultWriter::printComment(wstring comment) {
	// ignored
}

void DefaultWriter::clearAttributes() {
	hasAttributes = false;
	attributeTypes.clear();
	attributeByte.clear();
	attributeBoolean.clear();
	attributeChar.clear();
	attributeDouble.clear();
	attributeFloat.clear();
	attributeInt.clear();
	attributeLong.clear();
	attributeShort.clear();
	attributeString.clear();
}

void DefaultWriter::writeTag(unsigned int tag, bool hasContent) {
	if (!writtenHeader)
		writeHeader();

	// write tag
	int page = tag / WBXML_MAX_CODES;
	if (page != tagPage) {
		writeByte(WBXML::SWITCH_PAGE);
		writeByte(page);
		tagPage = page;
	}
	tag = tag % WBXML_MAX_CODES;
	writeByte((tag + WBXML_RESERVED_CODES)| (hasContent ? WBXML_CONTENT : 0x00)
			| (hasAttributes ? WBXML_ATTRIBUTE : 0x00));

	// write attributes
	if (hasAttributes) {
		// write attributes
		for (map<unsigned int,int>::iterator i=attributeTypes.begin(); i
				!= attributeTypes.end(); i++) {
			int att = (*i).first;
			int type = (*i).second;
			// write ATTRSTART
			writeAttribute(att);
			switch (type) {
			case IAttributes::BOOLEAN:
				// write OPAQUE
				writeByte(WBXML::OPAQUE);
				writeMultiByteInt(1+1);
				writeByte(IAttributes::BOOLEAN);
				writeBoolean(attributeBoolean[att]);
				break;
			case IAttributes::BYTE:
				// write OPAQUE
				writeByte(WBXML::OPAQUE);
				writeMultiByteInt(1+1);
				writeByte(IAttributes::BYTE);
				writeByte(attributeByte[att]);
				break;
			case IAttributes::CHAR:
				// write OPAQUE
				writeByte(WBXML::OPAQUE);
				writeMultiByteInt(2+1);
				writeByte(IAttributes::CHAR);
				writeChar(attributeChar[att]);
				break;
			case IAttributes::DOUBLE:
				// write OPAQUE
				writeByte(WBXML::OPAQUE);
				writeMultiByteInt(8+1);
				writeByte(IAttributes::DOUBLE);
				writeDouble(attributeDouble[att]);
				break;
			case IAttributes::FLOAT:
				// write OPAQUE
				writeByte(WBXML::OPAQUE);
				writeMultiByteInt(4+1);
				writeByte(IAttributes::FLOAT);
				writeFloat(attributeFloat[att]);
				break;
			case IAttributes::INT:
				// write OPAQUE
				writeByte(WBXML::OPAQUE);
				writeMultiByteInt(4+1);
				writeByte(IAttributes::INT);
				writeInt(attributeInt[att]);
				break;
			case IAttributes::LONG:
				// write OPAQUE
				writeByte(WBXML::OPAQUE);
				writeMultiByteInt(8+1);
				writeByte(IAttributes::LONG);
				writeLong(attributeLong[att]);
				break;
			case IAttributes::SHORT:
				// write OPAQUE
				writeByte(WBXML::OPAQUE);
				writeMultiByteInt(2+1);
				writeByte(IAttributes::SHORT);
				writeShort(attributeShort[att]);
				break;
			case IAttributes::STRING:
				// calculate total length
				int length = 0;
				// add (short) length and (byte) null termination
				length += stringUTFLength(attributeString[att])+ 2+ 1;

				// write OPAQUE
				writeByte(WBXML::OPAQUE);
				writeMultiByteInt(length+1);
				writeByte(IAttributes::STRING);

				// write UTF strings
				writeString(attributeString[att]);
				break;

			}
		}
		// end of attributes
		writeByte(WBXML::END);
		clearAttributes();
	}
}

void DefaultWriter::writeAttribute(unsigned int tag) {
	unsigned int page = tag / WBXML_MAX_CODES;
	tag = tag % WBXML_MAX_CODES;
	if (page != attributePage) {
		writeByte(WBXML::SWITCH_PAGE);
		writeByte(page);
		attributePage = page;
	}
	writeByte(tag + WBXML_RESERVED_CODES);
}

void DefaultWriter::setAttribute(unsigned int tag, wstring value) {
	hasAttributes = true;
	attributeString[tag] = value;
	attributeTypes[tag] = IAttributes::STRING;
}

/*
 public void setAttribute(unsigned int tag, String[] value, int offset, int length) {
 hasAttributes = true;
 attributeString[tag] = value;
 attributeStringOffset[tag] = offset;
 attributeStringLength[tag] = length;
 attributeTypes[tag] = IAttributes.STRING;
 }
 */

void DefaultWriter::setAttribute(unsigned int tag, unsigned char value) {
	hasAttributes = true;
	attributeByte[tag] = value;
	attributeTypes[tag] = IAttributes::BYTE;
}

/*
 public void setAttribute(unsigned int tag, byte[] value, int offset, int length) {
 hasAttributes = true;
 attributeByte[tag] = value;
 attributeByteOffset[tag] = offset;
 attributeByteLength[tag] = length;
 attributeTypes[tag] = IAttributes.BYTE;
 }
 */

void DefaultWriter::setAttribute(unsigned int tag, int64 value) {
	hasAttributes = true;
	attributeLong[tag] = value;
	attributeTypes[tag] = IAttributes::LONG;
}

/*
 public void setAttribute(unsigned int tag, int64[] value, int offset, int length) {
 hasAttributes = true;
 attributeLong[tag] = value;
 attributeLongOffset[tag] = offset;
 attributeLongLength[tag] = length;
 attributeTypes[tag] = IAttributes.LONG;
 }
 */

void DefaultWriter::setAttribute(unsigned int tag, int value) {
	hasAttributes = true;
	attributeInt[tag] = value;
	attributeTypes[tag] = IAttributes::INT;
}

/*
 public void setAttribute(unsigned int tag, int[] value, int offset, int length) {
 hasAttributes = true;
 attributeInt[tag] = value;
 attributeIntOffset[tag] = offset;
 attributeIntLength[tag] = length;
 attributeTypes[tag] = IAttributes.INT;
 }
 */

void DefaultWriter::setAttribute(unsigned int tag, bool value) {
	hasAttributes = true;
	attributeBoolean[tag] = value;
	attributeTypes[tag] = IAttributes::BOOLEAN;
}

/*
 public void setAttribute(unsigned int tag, boolean[] value, int offset, int length) {
 hasAttributes = true;
 attributeBoolean[tag] = value;
 attributeBooleanOffset[tag] = offset;
 attributeBooleanLength[tag] = length;
 attributeTypes[tag] = IAttributes.BOOLEAN;
 }
 */

void DefaultWriter::setAttribute(unsigned int tag, float value) {
	hasAttributes = true;
	attributeFloat[tag] = value;
	attributeTypes[tag] = IAttributes::FLOAT;
}

/*
 public void setAttribute(unsigned int tag, float[] value, int offset, int length) {
 hasAttributes = true;
 attributeFloat[tag] = value;
 attributeFloatOffset[tag] = offset;
 attributeFloatLength[tag] = length;
 attributeTypes[tag] = IAttributes.FLOAT;
 }
 */

void DefaultWriter::setAttribute(unsigned int tag, double value) {
	hasAttributes = true;
	attributeDouble[tag] = value;
	attributeTypes[tag] = IAttributes::DOUBLE;
}

/*
 public void setAttribute(unsigned int tag, double[] value, int offset, int length) {
 hasAttributes = true;
 attributeDouble[tag] = value;
 attributeDoubleOffset[tag] = offset;
 attributeDoubleLength[tag] = length;
 attributeTypes[tag] = IAttributes.DOUBLE;
 }
 */

void DefaultWriter::setAttribute(unsigned int tag, wchar_t value) {
	hasAttributes = true;
	attributeChar[tag] = value;
	attributeTypes[tag] = IAttributes::CHAR;
}
/*
 public void setAttribute(unsigned int tag, char[] value, int offset, int length) {
 hasAttributes = true;
 attributeChar[tag] = value;
 attributeCharOffset[tag] = offset;
 attributeCharLength[tag] = length;
 attributeTypes[tag] = IAttributes.CHAR;
 }
 */

void DefaultWriter::setAttribute(unsigned int tag, short value) {
	hasAttributes = true;
	attributeShort[tag] = value;
	attributeTypes[tag] = IAttributes::SHORT;
}
/*
 public void setAttribute(unsigned int tag, short[] value, int offset, int length) {
 hasAttributes = true;
 attributeShort[tag] = value;
 attributeShortOffset[tag] = offset;
 attributeShortLength[tag] = length;
 attributeTypes[tag] = IAttributes.SHORT;
 }
 */

void DefaultWriter::writeMultiByteInt(unsigned int ui) {
	int buf[5];
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

int DefaultWriter::stringUTFLength(std::wstring str, unsigned int start,
		unsigned int len) {
	unsigned int strlen = len == 0 ? str.size() : len;
	unsigned int utflen = 0;

	/* use charAt instead of copying String to char array */
	for (unsigned int i = start; i < start+strlen; i++) {
		wchar_t c = str[i];
		if ((c >= 0x0001) && (c <= 0x007F)) {
			utflen++;
		} else if (c > 0x07FF) {
			utflen += 3;
		} else {
			utflen += 2;
		}
	}

	return utflen;
}

int DefaultWriter::writeUTF(wstring str, unsigned int start, unsigned int len) {
	unsigned int strlen = len == 0 ? str.size() : len;
	unsigned int utflen = 0;
	unsigned int count = 0;
	wchar_t c;

	utflen = stringUTFLength(str, start, len);

	if (utflen > 65535) {
		cerr << "encoded string too long: "<< utflen << " bytes"<< endl;
		return 0;
	}

	char *bytearr = new char[utflen+2];

	bytearr[count++] = (char) ((utflen >> (lowEndian ? 8 : 0)) & 0xFF);
	bytearr[count++] = (char) ((utflen >> (lowEndian ? 0 : 8)) & 0xFF);

	unsigned int i=0;
	for (i=start; i<start+strlen; i++) {
		c = str[i];
		if (!((c >= 0x0001) && (c <= 0x007F)))
			break;
		bytearr[count++] = (char)c;
	}

	for (; i < start+strlen; i++) {
		c = str[i];
		if ((c >= 0x0001) && (c <= 0x007F)) {
			bytearr[count++] = (char)c;

		} else if (c > 0x07FF) {
			bytearr[count++] = (char) (0xE0 | ((c >> 12) & 0x0F));
			bytearr[count++] = (char) (0x80 | ((c >> 6) & 0x3F));
			bytearr[count++] = (char) (0x80 | ((c >> 0) & 0x3F));
		} else {
			bytearr[count++] = (char) (0xC0 | ((c >> 6) & 0x1F));
			bytearr[count++] = (char) (0x80 | ((c >> 0) & 0x3F));
		}
	}
	os.write(bytearr, utflen+2);

	delete [] bytearr;

	return utflen;
}

