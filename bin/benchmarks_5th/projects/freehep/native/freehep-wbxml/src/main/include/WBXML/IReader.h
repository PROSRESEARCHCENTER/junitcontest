#ifndef WBXML_IREADER_INCLUDE
#define WBXML_IREADER_INCLUDE 1

#include <string>

#include <WBXML/Types.h>

namespace WBXML {

class IReader {

protected:
	struct primitive {
		union {
			double d;
			float f;
			int64 l;
			int i;
			short s;
			unsigned short us;
			wchar_t ch;
			char c[sizeof(double)];
		};
	};

public:
	virtual ~IReader() {;}

	virtual bool readUTF(std::wstring& s, unsigned short& utflen) = 0;

	virtual bool readByte(unsigned char& c) = 0;

	virtual bool readBoolean(bool& b) = 0;
	
	virtual bool readChar(wchar_t& c) = 0;

	virtual bool readDouble(double& d) = 0;
	
	virtual bool readFloat(float& f) = 0;

	virtual bool readInt(int& i) = 0;

	virtual bool readLong(int64& l) = 0;

	virtual bool readShort(short& s) = 0;

	virtual bool readUnsignedShort(unsigned short& us) = 0;

	virtual bool readMultiByteInt(unsigned int& ui) = 0;

	virtual bool readStrI(std::wstring& s) = 0;

  virtual bool readStrT(std::wstring& s, unsigned int &pos) = 0;

	virtual void putStrT(unsigned int pos, std::wstring& s) = 0;

	virtual bool getStrT(unsigned int pos, std::wstring& s) = 0;
};

}

#endif
