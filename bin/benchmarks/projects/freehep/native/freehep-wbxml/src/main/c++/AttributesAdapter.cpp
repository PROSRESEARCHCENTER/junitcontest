#include <cstdlib>
#include <string>
#include <cctype>
#include <algorithm>
#include <iostream>

#include <WBXML/AttributesAdapter.h>

using namespace WBXML;
using namespace std;

AttributesAdapter::AttributesAdapter(ITagHandler& tHandler, const char** attributes) : tagHandler(tHandler) {
	// fill map of attributes
	for (int i = 0; attributes[i]; i += 2) {
		// handle "value" mapping to artificial VALUE tag.
		string key = attributes[i];
		string value = attributes[i+1];
		int tag = tagHandler.getAttribute(key);
		if (tag < 0) {
			cerr << "Unknown attribute: \""<< key << "\" with value: \""
					<< value << "\""<< endl;
			continue;
		}
		atts[tag] = value;
	}
}

vector<unsigned int>& AttributesAdapter::getTags() {
	if (tags.size() != atts.size()) {
		map<int, string>::iterator i;
		for (i = atts.begin(); i != atts.end(); i++) {
			int tag = (*i).first;
			string value = (*i).second;

			// add tag
			tags.push_back(tag);
		}
	}
	return tags;
}

IAttributes::Types AttributesAdapter::getType(unsigned int tag) {
	// check if tag is defined in current list
	map<int, string>::iterator i = atts.find((int)tag);
	if (i == atts.end()) {
		return UNDEFINED;
	}

	// return predefined type
	return tagHandler.getAttributeType(tag);
}

bool AttributesAdapter::getBooleanValue(unsigned int tag, bool def) {
	map<int, string>::iterator i = atts.find(tag);
	if (i != atts.end()) {
		string value = (*i).second;
		transform(value.begin(), value.end(), value.begin(), (int(*)(int)) tolower);
		return value == "true";
	}
	return def;
}

unsigned char AttributesAdapter::getByteValue(unsigned int tag,
		unsigned char def) {
	map<int, string>::iterator i = atts.find(tag);
	if (i != atts.end()) {
		return atoi((*i).second.c_str());
	}
	return def;
}

wchar_t AttributesAdapter::getCharValue(unsigned int tag, wchar_t def) {
	map<int, string>::iterator i = atts.find(tag);
	if (i != atts.end()) {
		return ((*i).second[0]);
	}
	return def;
}

double AttributesAdapter::getDoubleValue(unsigned int tag, double def) {
	map<int, string>::iterator i = atts.find(tag);
	if (i != atts.end()) {
		string value = (*i).second;
		transform(value.begin(), value.end(), value.begin(), (int(*)(int)) tolower);
		if (value == "nan")
			value = "NaN";
		return (float)atof(value.c_str());
	}
	return def;
}

float AttributesAdapter::getFloatValue(unsigned int tag, float def) {
	map<int, string>::iterator i = atts.find(tag);
	if (i != atts.end()) {
		string value = (*i).second;
		transform(value.begin(), value.end(), value.begin(), (int(*)(int)) tolower);
		if (value == "nan")
			value = "NaN";
		return atof(value.c_str());
	}
	return def;
}

int AttributesAdapter::getIntValue(unsigned int tag, int def) {
	map<int, string>::iterator i = atts.find(tag);
	if (i != atts.end()) {
		string value = (*i).second;
		return atoi(value.c_str());
	}
	return def;
}

int64 AttributesAdapter::getLongValue(unsigned int tag, int64 def) {
	map<int, string>::iterator i = atts.find(tag);
	if (i != atts.end()) {
		return atol((*i).second.c_str());
	}
	return def;
}

short AttributesAdapter::getShortValue(unsigned int tag, short def) {
	map<int, string>::iterator i = atts.find(tag);
	if (i != atts.end()) {
		return (short)atoi((*i).second.c_str());
	}
	return def;
}

wstring AttributesAdapter::getStringValue(unsigned int tag, wstring def) {
	map<int, string>::iterator i = atts.find(tag);
	if (i != atts.end()) {
		string value = (*i).second;
		// Make room for characters
		wstring wvalue(value.length(), L' ');

		// Copy string to wstring
		copy(value.begin(), value.end(), wvalue.begin());
		return wvalue;
	}
	return def;
}
