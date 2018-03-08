#ifndef WBXML_AIDA_ATTRIBUTES_ADAPTER_INCLUDE
#define WBXML_AIDA_ATTRIBUTES_ADAPTER_INCLUDE 1

#include <WBXML/AttributesAdapter.h>

#include <WBXML/AidaWBXML.h>

namespace WBXML {

class AidaAttributesAdapter : public AttributesAdapter {

private:
	enum ExtraType {VALUE = -2};

	AidaWBXML& aidaHandler;

public:
	AidaAttributesAdapter(AidaWBXML& aida, const char** attributes);
	
	virtual std::vector<unsigned int>& getTags();

	virtual int getIntValue(unsigned int tag, int def);
};

}

#endif
