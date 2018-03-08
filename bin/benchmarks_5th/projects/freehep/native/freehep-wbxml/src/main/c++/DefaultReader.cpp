#include <WBXML/DefaultReader.h>

using namespace std;
using namespace WBXML;

DefaultReader::DefaultReader(istream& in) :
	is(in) {
	union {
		short s;
		char c[sizeof(short)];
	} un;
	un.s = 0x0102;
	lowEndian = un.c[0] == 2&& un.c[1] == 1;
}

bool DefaultReader::readUTF(wstring& s, unsigned short& utflen) {
	if (!readUnsignedShort(utflen))
		return false;

	char *bytearr = new char[utflen];
	wchar_t *chararr = new wchar_t[utflen];
	
	int c, char2, char3;
	unsigned int count = 0;
	unsigned int chararr_count=0;

	is.read(bytearr, utflen);
	bool result = is.good();

	while (result && (count < utflen)) {
		c = (int) bytearr[count] & 0xff;
		if (c > 127)
			break;
		count++;
		chararr[chararr_count++]=(wchar_t)c;
	}

	while (result && (count < utflen)) {
		c = (int) bytearr[count] & 0xff;
		switch (c >> 4) {
		case 0:
		case 1:
		case 2:
		case 3:
		case 4:
		case 5:
		case 6:
		case 7:
			/* 0xxxxxxx*/
			count++;
			chararr[chararr_count++]=(wchar_t)c;
			break;
		case 12:
		case 13:
			/* 110x xxxx   10xx xxxx*/
			count += 2;
			if (count > utflen) {
				cerr << "malformed input: partial character at end"<< endl;
				result = false;
				continue;
			}
			char2 = (int) bytearr[count-1];
			if ((char2 & 0xC0) != 0x80) {
				cerr << "malformed input around byte " + count << endl;
				result = false;
				continue;
			}
			chararr[chararr_count++]=(wchar_t)(((c & 0x1F) << 6)
					|(char2 & 0x3F));
			break;
		case 14:
			/* 1110 xxxx  10xx xxxx  10xx xxxx */
			count += 3;
			if (count > utflen) {
				cerr << "malformed input: partial character at end"<< endl;
				result = false;
				continue;
			}
			char2 = (int) bytearr[count-2];
			char3 = (int) bytearr[count-1];
			if (((char2 & 0xC0) != 0x80) || ((char3 & 0xC0) != 0x80)) {
				cerr << "malformed input around byte "<< (count-1)<< endl;
				result = false;
				continue;
			}
			chararr[chararr_count++]=(wchar_t)(((c & 0x0F) << 12) |((char2
					& 0x3F) << 6)|((char3 & 0x3F) << 0));
			break;
		default:
			/* 10xx xxxx,  1111 xxxx */
			cerr << "malformed input around byte "<< count << endl;
			result = false;
			continue;
		}
	}
	
	if (result) {
		// NOTE: when compiled in DEBUG mode on Windows (vc++ 2005 14.00.50727.762) 
		// we tend to see a crash in the following statement. Not sure why. 
		// The number of chars produced may be less than utflen
		s = wstring(chararr, 0, chararr_count);
	}
	
	delete [] bytearr;
	delete [] chararr;

	return result;
}
