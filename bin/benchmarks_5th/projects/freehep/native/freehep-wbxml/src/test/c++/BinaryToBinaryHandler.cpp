
#include <iostream>
#include <ostream>

#include <BinaryToBinaryHandler.h>

using namespace std;
using namespace WBXML;

BinaryToBinaryHandler::BinaryToBinaryHandler(IWriter& writer) : writer(writer) {
}

BinaryToBinaryHandler::~BinaryToBinaryHandler() {
}

istream* BinaryToBinaryHandler::resolveEntity(wstring name, wstring publicId, wstring systemId) {
	writer.referToDTD(name, systemId);
	return NULL;
}

bool BinaryToBinaryHandler::characters(wstring s, unsigned int start, unsigned int len) {
	writer.print(s, start, len);
	return true;
}

bool BinaryToBinaryHandler::endDocument() {
	writer.closeDoc();
	writer.close();
	return true;
}

bool BinaryToBinaryHandler::endElement(unsigned int tagID) {
	writer.closeTag();
	return true;
}

bool BinaryToBinaryHandler::startDocument() {
	writer.openDoc();
	return true;
}

bool BinaryToBinaryHandler::startElement(unsigned int tagID, IAttributes& attr, bool empty) {
	vector<unsigned int> tags = attr.getTags();
	for (unsigned int i=0; i<tags.size(); i++) {
		IAttributes::Types type = attr.getType(tags[i]);
		switch (type) {
		case IAttributes::BOOLEAN:
			writer.setAttribute(tags[i], attr.getBooleanValue(tags[i]));
			break;
		case IAttributes::BYTE:
			writer.setAttribute(tags[i], attr.getByteValue(tags[i]));
			break;
		case IAttributes::CHAR:
			writer.setAttribute(tags[i], attr.getCharValue(tags[i]));
			break;
		case IAttributes::DOUBLE:
			writer.setAttribute(tags[i], attr.getDoubleValue(tags[i]));
			break;
		case IAttributes::FLOAT:
			writer.setAttribute(tags[i], attr.getFloatValue(tags[i]));
			break;
		case IAttributes::INT:
			writer.setAttribute(tags[i], attr.getIntValue(tags[i]));
			break;
		case IAttributes::LONG:
			writer.setAttribute(tags[i], attr.getLongValue(tags[i]));
			break;
		case IAttributes::SHORT:
			writer.setAttribute(tags[i], attr.getShortValue(tags[i]));
			break;
		case IAttributes::STRING:
			writer.setAttribute(tags[i], attr.getStringValue(tags[i]));
			break;
		default:
			cout << "ERROR - Unknown Value for Unknown Type: " << type;
			break;
		}
	}
	if (empty) {
		writer.printTag(tagID);
	} else {
		writer.openTag(tagID);
	}
	return true;
}

