#ifndef WBXML_TEST_CONTENT_HANDLER_INCLUDE
#define WBXML_TEST_CONTENT_HANDLER_INCLUDE 1

#include <WBXML/IContentHandler.h>
#include <WBXML/ITagHandler.h>

class TestContentHandler : public virtual WBXML::IContentHandler {    
	
public:
	TestContentHandler(WBXML::ITagHandler& tHandler) : tagHandler(tHandler) {
	}
	virtual bool startDocument();
	virtual bool endDocument();
    
	virtual bool startElement(unsigned int tagID, WBXML::IAttributes& attr, bool empty);
	virtual bool endElement(unsigned int tagID);
    
	virtual bool characters(std::wstring, unsigned int start, unsigned int len);

private:
	WBXML::ITagHandler& tagHandler;

};

#endif
