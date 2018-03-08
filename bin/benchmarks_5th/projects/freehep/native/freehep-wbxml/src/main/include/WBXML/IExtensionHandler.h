#ifndef WBXML_IEXTENSION_HANDLER_INCLUDE
#define WBXML_IEXTENSION_HANDLER_INCLUDE 1

#include <string>
#include <vector>
#include <iostream>

#include <WBXML/MutableAttributes.h>
#include <WBXML/IReader.h>

namespace WBXML {

class IExtensionHandler {

public:
	virtual ~IExtensionHandler() {;}
	virtual bool extI(unsigned int i, std::wstring readStrI, unsigned int tagID, int attributeID,
			MutableAttributes& atts, std::vector<std::wstring>& value) = 0;

	virtual bool extT(unsigned int i, unsigned int readInt, unsigned int tagID, int attributeID,
			MutableAttributes& atts, std::vector<std::wstring>& value) = 0;

	virtual bool ext(unsigned int i, unsigned int tagID, int attributeID, MutableAttributes& atts,
			std::vector<std::wstring>& value) = 0;

	virtual bool opaque(unsigned int len, IReader& reader, unsigned int tagID, int attributeID,
			MutableAttributes& atts, std::vector<std::wstring>& value) = 0;
};

}

#endif
