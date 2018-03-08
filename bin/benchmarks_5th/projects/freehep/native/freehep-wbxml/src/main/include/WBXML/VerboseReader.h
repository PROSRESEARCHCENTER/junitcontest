#ifndef WBXML_VERBOSE_READER_INCLUDE
#define WBXML_VERBSOE_READER_INCLUDE 1

#include <iomanip>

#include <WBXML/IReader.h>
#include <WBXML/DefaultWriter.h>

namespace WBXML {

class VerboseReader : public virtual IReader {

private:
	IReader& reader;
	unsigned int address;
	
  unsigned int multiByteIntLength(unsigned int ui) {
		unsigned int idx = 0;

		do {
			idx++;
			ui = ui >> 7;
		} while (ui != 0);
	  return idx;
  }
	
public:
	VerboseReader(IReader& r) : reader(r), address(0) {
	}

	inline bool readUTF(std::wstring& s, unsigned short& utflen) {
		bool r = reader.readUTF(s, utflen);
		std::cout << std::endl << "0x" << std::setfill('0') << std::setw(4) << std::hex << address;
		std::wcout << ": UTFString : \"" << s << "\"" ;
		address += utflen + 2;
		return r;
	}

	inline bool readByte(unsigned char& c) {
		bool r = reader.readByte(c);
		if (r) std::cout << std::endl << "0x" << std::setfill('0') << std::setw(4) << std::hex << address << ": byte      :                      0x" 
			                                    << std::setfill('0') << std::setw(2) << std::hex << (int)c ;
	  address += 1;
		return r;
	}

	inline bool readBoolean(bool& b) {
		bool r = reader.readBoolean(b);
		std::cout << std::endl << "0x" << std::setfill('0') << std::setw(4) << std::hex << address <<  ": boolean   : " << std::setfill(' ') << std::setw(25) << (b ? "true" : "false") ;
    address += 1;
		return r;
	} 
	
	inline bool readChar(wchar_t& c) {
		bool r = reader.readChar(c);
		std::cout << std::endl << "0x" << std::setfill('0') << std::setw(4) << std::hex << address << ": char      : " << std::setfill(' ') << std::setw(25) << (char)c ;
    address += 2;
		return r;
	}

	inline bool readDouble(double& d) {
		bool r = reader.readDouble(d);
		std::cout << std::endl << "0x" << std::setfill('0') << std::setw(4) << std::hex << address << ": double    : " << std::setfill(' ') << std::setw(25) << std::setprecision(16) << d ;
    address += 8;
		return r;
	}

	inline bool readFloat(float& f) {
		bool r = reader.readFloat(f);
		std::cout << std::endl << "0x" << std::setfill('0') << std::setw(4) << std::hex << address << ": float     : " << std::setfill(' ') << std::setw(25) << std::setprecision(8) << f ;
		address += 4;
		return r;
	}

	inline bool readInt(int& i) {
		bool r = reader.readInt(i);
		std::cout << std::endl << "0x" << std::setfill('0') << std::setw(4) << std::hex << address << ": int       : " << std::setfill(' ') << std::setw(25) << std::dec << i ;
		address += 4;
		return r;
	}

	inline bool readLong(int64& l) {
		bool r = reader.readLong(l);
		std::cout << std::endl << "0x" << std::setfill('0') << std::setw(4) << std::hex << address << ": long      : " << std::setfill(' ') << std::setw(25) << std::dec << l ;
		address += 8;
		return r;
	}

	inline bool readShort(short& s) {
		bool r = reader.readShort(s);
		std::cout << std::endl << "0x" << std::setfill('0') << std::setw(4) << std::hex << address << ": short     : " << std::setfill(' ') << std::setw(25) << std::dec << (int)s ;
		address += 2;
		return r;
	}

	inline bool readUnsignedShort(unsigned short& us) {
		bool r = reader.readUnsignedShort(us);
		std::cout << std::endl << "0x" << std::setfill('0') << std::setw(4) << std::hex << address << ": ushort    : " << std::setfill(' ') << std::setw(25) << std::dec << (unsigned int)us ;
		address += 2;
		return r;
	}

	inline bool readMultiByteInt(unsigned int& ui) {
		bool r = reader.readMultiByteInt(ui);
		std::cout << std::endl << "0x" << std::setfill('0') << std::setw(4) << std::hex << address << ": mb uint   : " << std::setfill(' ') << std::setw(25) << std::dec << ui ;
		address += multiByteIntLength(ui);
		return r;
	}

	inline bool readStrI(std::wstring& s) {
		bool r = reader.readStrI(s);
		std::cout << std::endl << "0x" << std::setfill('0') << std::setw(4) << std::hex << address;
		std::wcout << ": String I  : \"" << s << "\"" ;		
		address += DefaultWriter::stringUTFLength(s) + 2 + 1;
		return r;
 	}

  inline bool readStrT(std::wstring& s, unsigned int& pos) {
		bool r = reader.readStrT(s, pos);
		std::cout << std::endl << "0x" << std::setfill('0') << std::setw(4) << std::hex << address;
		std::wcout << ": String T  : \"" << s << "\"" ;		
	  address += multiByteIntLength(pos);	
		return r;
  }

	inline void putStrT(unsigned int pos, std::wstring& s) {
		reader.putStrT(pos, s);
		std::cout << std::endl << "0x" << std::setfill('0') << std::setw(4) << std::hex << address;
		std::wcout << ": put       : (" << std::dec << pos << ", \"" << s << "\")" ;
	  address += 0;
	}

	inline bool getStrT(unsigned int pos, std::wstring& s) {
		bool r = reader.getStrT(pos, s);
		std::cout << std::endl << "0x" << std::setfill('0') << std::setw(4) << std::hex << address;
		std::wcout << ": get       : (" << std::dec << pos << "): \"" << s << "\"" ;
		address += 0;
		return r;
	}
};

}

#endif
