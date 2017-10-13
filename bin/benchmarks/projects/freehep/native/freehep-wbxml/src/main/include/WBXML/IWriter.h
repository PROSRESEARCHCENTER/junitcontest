#ifndef WBXML_IWRITER_INCLUDE
#define WBXML_IWRITER_INCLUDE 1

#include <string>

#include <WBXML/Types.h>

namespace WBXML {

class IWriter {
	
public:
	virtual ~IWriter() {;}

	virtual void openDoc(std::wstring version = L"BinaryAIDA/1.0",
			std::string encoding = "UTF-8", bool standalone = false) = 0;
	virtual void closeDoc() = 0;
	virtual void close() = 0;
	virtual void referToDTD(std::wstring name, std::wstring pid, std::wstring ref) = 0;
	virtual void referToDTD(std::wstring name, std::wstring system) = 0;

	virtual void openTag(unsigned int tag) = 0;
	virtual void closeTag() = 0;
	virtual void printTag(unsigned int tag) = 0;
	virtual void print(std::wstring text, unsigned int start, unsigned int len) = 0;
	virtual void printComment(std::wstring comment) = 0;
	
	virtual void setAttribute(unsigned int tag, unsigned char value) = 0;
	virtual void setAttribute(unsigned int tag, bool value) = 0;
	virtual void setAttribute(unsigned int tag, wchar_t value) = 0;
	virtual void setAttribute(unsigned int tag, double value) = 0;
	virtual void setAttribute(unsigned int tag, float value) = 0;
	virtual void setAttribute(unsigned int tag, int value) = 0;
	virtual void setAttribute(unsigned int tag, int64 value) = 0;
	virtual void setAttribute(unsigned int tag, short value) = 0;
	virtual void setAttribute(unsigned int tag, std::wstring value) = 0;
};

}

#endif
