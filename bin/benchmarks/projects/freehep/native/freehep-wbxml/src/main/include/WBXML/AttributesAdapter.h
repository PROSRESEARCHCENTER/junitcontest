#ifndef WBXML_ATTRIBUTES_ADAPTER_INCLUDE
#define WBXML_ATTRIBUTES_ADAPTER_INCLUDE 1

#include <map>
#include <vector>

#include <WBXML/IAttributes.h>
#include <WBXML/ITagHandler.h>

namespace WBXML {

class AttributesAdapter : public virtual IAttributes {

protected:
	std::map<int, std::string> atts;
	std::vector<unsigned int> tags;

	ITagHandler& tagHandler;
	
	AttributesAdapter(ITagHandler& tHandler) : tagHandler(tHandler) {
	}
	
public:
	AttributesAdapter(ITagHandler& tHandler, const char** attributes);
	
	virtual std::vector<unsigned int>& getTags();
	virtual Types getType(unsigned int tag);

	virtual bool getBooleanValue(unsigned int tag, bool def);
	virtual unsigned char getByteValue(unsigned int tag, unsigned char def);
	virtual wchar_t getCharValue(unsigned int tag, wchar_t def);
	virtual double getDoubleValue(unsigned int tag, double def);
	virtual float getFloatValue(unsigned int tag, float def);
	virtual int getIntValue(unsigned int tag, int def);
	virtual int64 getLongValue(unsigned int tag, int64 def);
	virtual short getShortValue(unsigned int tag, short def);
	virtual std::wstring getStringValue(unsigned int tag, std::wstring def);
};

}

#endif
