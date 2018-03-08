#ifndef WBXML_DUMP_CONTENT_HANDLER_INCLUDE
#define WBXML_DUMP_CONTENT_HANDLER_INCLUDE 1

#include <WBXML/IContentHandler.h>
#include <WBXML/ITagHandler.h>

class DumpContentHandler : public virtual WBXML::IContentHandler {    
	
public: 
	DumpContentHandler(WBXML::ITagHandler& tHandler) : tagHandler(tHandler) {
	}
	
	virtual bool startDocument() {
		return true;
	}
	
	virtual bool endDocument() {
		return true;
	}
    
	virtual bool startElement(unsigned int tagID, WBXML::IAttributes& attr, bool empty) {
		return true;
	}
	
	virtual bool endElement(unsigned int tagID) {
		return true;
	}
    
	virtual bool characters(std::wstring, unsigned int start, unsigned int len) {
		return true;
	}

private:
	WBXML::ITagHandler& tagHandler;
};

#endif
