#ifndef WBXML_DEFAULT_READER_INCLUDE
#define WBXML_DEFAULT_READER_INCLUDE 1

#include <iostream>
#include <istream>
#include <ostream>
#include <map>

#include <WBXML/IReader.h>

namespace WBXML {

class DefaultReader : public virtual IReader {

private:
	bool lowEndian;
	std::istream& is;
	std::map<unsigned int, std::wstring> stringTable;

	inline bool read(primitive& p, unsigned int n) {
		for (unsigned int i=0; i<n; i++) {
			p.c[lowEndian ? n - 1 - i : i] = is.get();
			if (!is.good()) return false;
		}
		return true;
	}

public:
	DefaultReader(std::istream& in);

	bool readUTF(std::wstring& s, unsigned short& utflen);

	inline bool readByte(unsigned char& c) {
		int d = is.get();
		c = d;
		return is.good();
	}

	inline bool readBoolean(bool& b) {
		b = is.get() != 0;
		return is.good();
	}
	
	inline bool readChar(wchar_t& c) {
		primitive p;
		if (!read(p, 2)) return false;
		c = p.ch;
		return true;
	}

	inline bool readDouble(double& d) {
		primitive p;
		if (!read(p, 8)) return false;
		d = p.d;
		return true;
	}

	inline bool readFloat(float& f) {
		primitive p;
		if (!read(p, 4)) return false;
		f = p.f;
		return true;
	}

	inline bool readInt(int& i) {
		primitive p;
		if (!read(p, 4)) return false;
		i = p.i;
		return true;
	}

	inline bool readLong(int64& l) {
		primitive p;
		if (!read(p, 8)) return false;
		l = p.l;
		return true;
	}

	inline bool readShort(short& s) {
		primitive p;
		if (!read(p, 2)) return false;
		s = p.s;
		return true;
	}

	inline bool readUnsignedShort(unsigned short& us) {
		primitive p;
		if (!read(p, 2)) return false;
		us = p.us;
		return true;
	}

	inline bool readMultiByteInt(unsigned int& ui) {
        	ui = 0;
        	int r = 0;      
        	do {
                	r = is.get();
                	if (!is.good()) return false;
                	ui = (ui << 7) | (r & 0x7f);
        	} while ((r & 0x80) != 0);
        	return true;
	}

	inline bool readStrI(std::wstring& s) {
        	unsigned short len;
		if (!readUTF(s, len)) return false;
        	is.get(); // skip null termination
        	return is.good();              
	}

        inline bool readStrT(std::wstring& s, unsigned int& pos) {
                if (!readMultiByteInt(pos)) return false;
                s = stringTable[pos];
                return true;
        }

	inline void putStrT(unsigned int pos, std::wstring& s) {
		stringTable[pos] = s;
	}

	inline bool getStrT(unsigned int pos, std::wstring& s) {
		std::map<unsigned int, std::wstring>::iterator iterator = stringTable.find(pos);
		if (iterator != stringTable.end()) {
			s = stringTable[pos];
    			return true;
		}
		return false;
	}
};

}

#endif
