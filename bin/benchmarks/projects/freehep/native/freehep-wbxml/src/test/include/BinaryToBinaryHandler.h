#ifndef WBXML_BINARY_TO_BINARY_HANDLER_INCLUDE
#define WBXML_BINARY_TO_BINARY_HANDLER_INCLUDE 1

#include <iostream>
#include <istream>
#include <ostream>

#include <WBXML/IContentHandler.h>
#include <WBXML/IEntityResolver.h>
#include <WBXML/IWriter.h>

class BinaryToBinaryHandler : public virtual WBXML::IContentHandler, public virtual WBXML::IEntityResolver {    

private:
	WBXML::IWriter& writer;
	
public:
	BinaryToBinaryHandler(WBXML::IWriter& writer);
	~BinaryToBinaryHandler();
	
	virtual bool startDocument();
	virtual bool endDocument();
    
	virtual bool startElement(unsigned int tagID, WBXML::IAttributes& attr, bool empty);
	virtual bool endElement(unsigned int tagID);
    
	virtual bool characters(std::wstring, unsigned int start, unsigned int len);
	
	virtual std::istream* resolveEntity(std::wstring name, std::wstring publicId, std::wstring systemId);
};

#endif
