#include <cstdlib>
#include <string>
#include <cctype>
#include <algorithm>
#include <iostream>

#include <WBXML/AidaAttributesAdapter.h>

using namespace WBXML;
using namespace std;

AidaAttributesAdapter::AidaAttributesAdapter(AidaWBXML& aida,
		const char** attributes) :
	AttributesAdapter(aida), aidaHandler(aida) {
	// fill map of attributes
	for (int i = 0; attributes[i]; i += 2) {
		// handle "value" mapping to artificial VALUE tag.
		string key = attributes[i];
		string value = attributes[i+1];
		int tag = (key == "value") ? VALUE : aidaHandler.getAttribute(key);
		if ((tag < 0) && (tag != VALUE)) {
			cerr << "Unknown attribute: \""<< key << "\" with value: \""
					<< value << "\""<< endl;
			continue;
		}
		atts[tag] = value;
	}
}

vector<unsigned int>& AidaAttributesAdapter::getTags() {
	// this vector may contain VALUE tags, for which you have to lookup the type
	if (tags.size() != atts.size()) {
		map<int, string>::iterator i;
		for (i = atts.begin(); i != atts.end(); i++) {
			int tag = (*i).first;
			string value = (*i).second;
			//
			// NOTE: guess type if tag is VALUE (double, int or string)
			//
			// FIXME: This is not working very well, and incomplete. The problem here
			// is not so important, since this method is only used if you
			// want to iterate over all the attributes in a tag.
			//
			// Typically an AIDA implementation will expect a "VALUE" to be of a certain
			// type and call one of getFloatValue, getIntValue, getStringValue directly.
			//
			if (tag == VALUE) {
				// remove VALUE tag
				atts.erase(tag);

				double d = atof(value.c_str()); // returns 0.0 if failure, unless value was 0.0
				if ((d != 0.0) || (value == "0.0")) {
					// this was a double
					tag = aidaHandler.VALUE_DOUBLE;
				} else {
					int j = atoi(value.c_str()); // returns 0 if failure, unless value was 0
					if ((j != 0) || (value == "0")) {
						// this was an int
						tag = aidaHandler.VALUE_INT;
					} else {
						// so, ... this must be a string
						tag = aidaHandler.VALUE_STRING;
					}
				}
			}

			// add tag
			atts[tag] = value;
			tags.push_back(tag);
		}
	}
	return tags;
}

int AidaAttributesAdapter::getIntValue(unsigned int tag, int def) {
	map<int, string>::iterator i = atts.find(tag);
	if (i != atts.end()) {
		string value = (*i).second;
		switch (tag) {
		case AidaWBXML::BIN_NUM:
		case AidaWBXML::BIN_NUM_X:
		case AidaWBXML::BIN_NUM_Y:
		case AidaWBXML::BIN_NUM_Z:
			if (value == "UNDERFLOW")
				return -2;
			if (value == "OVERFLOW")
				return -1;
			break;
		default:
			break;
		}
		return atoi(value.c_str());
	}
	return def;
}
