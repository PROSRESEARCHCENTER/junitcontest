#ifndef WBXML_IATTRIBUTES_INCLUDE
#define WBXML_IATTRIBUTES_INCLUDE 1

#include <string>
#include <vector>

#include <WBXML/Types.h>

namespace WBXML {

class IAttributes {

public:
	enum Types {UNDEFINED = -1, BOOLEAN = 0, BYTE = 1, CHAR = 2, DOUBLE = 3, FLOAT = 4, INT = 5, LONG = 6, SHORT = 7, STRING = 8};

	//	enum Arrays { BOOLEAN_ARRAY = 0x10, BYTE_ARRAY = 0x11, CHAR_ARRAY = 0x12 , DOUBLE_ARRAY = 0x13, FLOAT_ARRAY = 0x14, 
	//		          INT_ARRAY = 0x15, LONG_ARRAY = 0x16, SHORT_ARRAY = 0x17, STRING_ARRAY = 0x18
	//	};

	inline std::string getTypeName(Types type) {
		switch (type) {
		case BOOLEAN:
			return "boolean";
		case BYTE:
			return "byte";
		case CHAR:
			return "char";
			//	case COLOR:
			//		return "Color";
		case DOUBLE:
			return "double";
		case FLOAT:
			return "float";
		case INT:
			return "int";
		case LONG:
			return "long";
			//	case OBJECT:
			//		return "Object";
		case SHORT:
			return "short";
		case STRING:
			return "String";
		default:
			return "Unknown";
		}
	}

public:
	virtual ~IAttributes() {
		;
	}
	virtual std::vector<unsigned int>& getTags() = 0;
	virtual Types getType(unsigned int tag) = 0;

	virtual bool getBooleanValue(unsigned int tag, bool def = false) = 0;
	virtual unsigned char getByteValue(unsigned int tag, unsigned char def = 0) = 0;
	virtual wchar_t getCharValue(unsigned int tag, wchar_t def = L' ') = 0;
	virtual double getDoubleValue(unsigned int tag, double def = 0) = 0;
	virtual float getFloatValue(unsigned int tag, float def = 0) = 0;
	virtual int getIntValue(unsigned int tag, int def = 0) = 0;
	virtual int64 getLongValue(unsigned int tag, int64 def = 0) = 0;
	virtual short getShortValue(unsigned int tag, short def = 0) = 0;
	virtual std::wstring getStringValue(unsigned int tag, std::wstring = L"") = 0;
};

}

#endif
