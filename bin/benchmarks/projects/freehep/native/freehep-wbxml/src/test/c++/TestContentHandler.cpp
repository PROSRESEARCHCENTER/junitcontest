#include <iostream>
#include <ostream>
#include <iomanip>

#include <WBXML/Types.h>
#include <TestContentHandler.h>

using namespace std;
using namespace WBXML;

bool TestContentHandler::characters(wstring s, unsigned int start, unsigned int len) {
	wcout << "'" << s << "'";
	return true;
}

bool TestContentHandler::endDocument() {
	cout << "END DOCUMENT" << endl;
	return true;
}

bool TestContentHandler::endElement(unsigned int tagID) {
	cout << "</" << tagHandler.getTag(tagID) << ">";
	cout << endl;
	return true;
}

bool TestContentHandler::startDocument() {
	cout << "START DOCUMENT" << endl;
	return true;
}

bool TestContentHandler::startElement(unsigned int tagID, IAttributes& attr, bool empty) {
	cout << "<" << tagHandler.getTag(tagID) << endl;
	vector<unsigned int> tags = attr.getTags();
	for (unsigned int i=0; i<tags.size(); i++) {
		IAttributes::Types type = attr.getType(tags[i]);
		cout << "      " << tagHandler.getAttribute(tags[i]) << "(" << attr.getTypeName(type) << ") = \"";
		switch (attr.getType(tags[i])) {
		case IAttributes::BOOLEAN:
			cout << (attr.getBooleanValue(tags[i]) ? "true" : "false");
			break;
		case IAttributes::BYTE:
			cout << attr.getByteValue(tags[i]);
			break;
		case IAttributes::CHAR:
			cout << attr.getCharValue(tags[i]);
			break;
		case IAttributes::DOUBLE:
			cout << setprecision(16) << attr.getDoubleValue(tags[i]);
			break;
		case IAttributes::FLOAT:
			cout << setprecision(8) << attr.getFloatValue(tags[i]);
			break;
		case IAttributes::INT:
			cout << attr.getIntValue(tags[i]);
			break;
		case IAttributes::LONG:
			// does NOT compile on VC6 due to http://support.microsoft.com/kb/168440
			cout << attr.getLongValue(tags[i]);
			break;
		case IAttributes::SHORT:
			cout << attr.getShortValue(tags[i]);
			break;
		case IAttributes::STRING:
			wcout << attr.getStringValue(tags[i]);
			break;
		default:
			cout << "ERROR - Unknown Value for Unknown Type: " << type;
			break;
		}
		cout << "\"" << endl;
	}
	if (empty) cout << "/";
	cout << ">" << endl;
	return true;
}
