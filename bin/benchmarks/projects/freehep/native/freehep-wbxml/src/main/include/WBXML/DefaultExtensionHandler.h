#ifndef WBXML_DEFAULT_EXTENSION_HANDLER_INCLUDE
#define WBXML_DEFAULT_EXTENSION_HANDLER_INCLUDE 1

#include <WBXML/IExtensionHandler.h>

namespace WBXML {

class DefaultExtensionHandler : public virtual IExtensionHandler {

private:
	bool debug;
	std::vector<std::wstring> strings;

public:
	DefaultExtensionHandler(bool verbose = false) : debug(verbose) {
	}
	virtual bool extI(unsigned int i, std::wstring readStrI, unsigned int tagID, int attributeID,
			MutableAttributes& atts, std::vector<std::wstring>& value);

	virtual bool extT(unsigned int i, unsigned int readInt, unsigned int tagID, int attributeID,
			MutableAttributes& atts, std::vector<std::wstring>& value);

	virtual bool ext(unsigned int i, unsigned int tagID, int attributeID, MutableAttributes& atts,
			std::vector<std::wstring>& value);

	virtual bool opaque(unsigned int len, IReader& reader, unsigned int tagID, int attributeID,
			MutableAttributes& atts, std::vector<std::wstring>& value);
};

}

#endif
