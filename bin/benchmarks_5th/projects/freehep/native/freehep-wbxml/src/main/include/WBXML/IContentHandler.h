#ifndef WBXML_ICONTENT_HANDLER_INCLUDE
#define WBXML_ICONTENT_HANDLER_INCLUDE 1

#include <string>

#include <WBXML/IAttributes.h>

namespace WBXML {

class IContentHandler {    

public: 
    virtual ~IContentHandler() {;}
    virtual bool startDocument() = 0;
    virtual bool endDocument() = 0;
    
    virtual bool startElement(unsigned int tagID, IAttributes& attr, bool empty) = 0;
    virtual bool endElement(unsigned int tagID) = 0;
    
    virtual bool characters(std::wstring, unsigned int start, unsigned int len) = 0;
};

}

#endif
