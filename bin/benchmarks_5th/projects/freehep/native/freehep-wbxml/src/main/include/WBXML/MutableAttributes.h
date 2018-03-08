#ifndef WBXML_MUTABLE_ATTRIBUTES_INCLUDE
#define WBXML_MUTABLE_ATTRIBUTES_INCLUDE 1

#include <map>

#include <WBXML/IAttributes.h>

namespace WBXML {

class MutableAttributes : public virtual IAttributes {

private:
	std::vector<unsigned int> attrTags;
	std::map<unsigned int, Types> attrTypes;

	std::map<unsigned int, bool> booleanAtts;
	std::map<unsigned int, double> doubleAtts;
	std::map<unsigned int, int64> longAtts;
	std::map<unsigned int, std::wstring> stringAtts;

public:
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

	virtual void clear();
	virtual void set(unsigned int tag, bool value);
//	virtual void set(unsigned int tag, std::vector<bool> value);

	virtual void set(unsigned int tag, unsigned char value);
//	virtual void set(unsigned int tag, std::vector<unsigned char> value);

	virtual void set(unsigned int tag, wchar_t value);
//	virtual void set(unsigned int tag, std::vector<wchar_t> value);

	virtual void set(unsigned int tag, double value);
//	virtual void set(unsigned int tag, std::vector<double> value);

	virtual void set(unsigned int tag, float value);
//	virtual void set(unsigned int tag, std::vector<float> value);

	virtual void set(unsigned int tag, int value);
//	virtual void set(unsigned int tag, std::vector<int> value);

	virtual void set(unsigned int tag, int64 value);
//	virtual void set(unsigned int tag, std::vector<int64> value);

	virtual void set(unsigned int tag, short value);
//	virtual void set(unsigned int tag, std::vector<short> value);

	virtual void set(unsigned int tag, std::wstring value);
	virtual void set(unsigned int tag, std::vector<std::wstring> value);
};

}

#endif

