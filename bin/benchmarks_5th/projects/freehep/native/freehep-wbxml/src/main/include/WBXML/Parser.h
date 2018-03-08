#ifndef WBXML_PARSER_INCLUDE
#define WBXML_PARSER_INCLUDE 1

#include <istream>
#include <stack>
#include <vector>

#include <WBXML/IAttributes.h>
#include <WBXML/IContentHandler.h>
#include <WBXML/IExtensionHandler.h>
#include <WBXML/IEntityResolver.h>
#include <WBXML/IReader.h>
#include <WBXML/ITagHandler.h>
#include <WBXML/MutableAttributes.h>

namespace WBXML {

class Parser {

private:	
	bool debug;
	unsigned char tagPage;
	unsigned char attributePage;
	unsigned char version;
	unsigned int publicIdentifierId;
	unsigned int charSet;
	std::stack<unsigned int> stack;
	ITagHandler& tagHandler;
    IContentHandler& contentHandler;
	IExtensionHandler& extensionHandler;
	IEntityResolver& resolver;

	unsigned int getTagId(unsigned char id);
	unsigned int getAttributeId(unsigned char id);

	bool readElement(IReader& reader, unsigned char id);
	bool readAttr(IReader& reader, unsigned int tagID, MutableAttributes& atts);

	bool handleExtensions(IReader& reader, unsigned int id, unsigned int tagID, 
			int attributeID, MutableAttributes& atts,
			std::vector<std::wstring>& value);

	unsigned int getCharSet();
	unsigned int getVersion();

	bool parse(IReader& reader, bool verbose = false);
	
public:
	Parser(ITagHandler& tHandler, IContentHandler& cHandler, IExtensionHandler& eHandler, IEntityResolver& eResolver);
	bool parse(std::istream& in, bool verbose = false);
};

}

#endif
