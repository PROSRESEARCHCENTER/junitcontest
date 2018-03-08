#ifndef WBXML_ITAG_HANDLER_INCLUDE
#define WBXML_ITAG_HANDLER_INCLUDE 1

#include <string>

#include <WBXML/IAttributes.h>

namespace WBXML {

class ITagHandler {    

public: 	
	virtual ~ITagHandler() {;}
    virtual std::string getTag(unsigned int tag) = 0;
    virtual int getTag(std::string name) = 0;
    virtual bool isTagEmpty(unsigned int tag) = 0;
    virtual std::string getAttribute(unsigned int tag) = 0;
    virtual int getAttribute(std::string name) = 0;
    virtual IAttributes::Types getAttributeType(unsigned int tag) = 0;
};

}

#endif
