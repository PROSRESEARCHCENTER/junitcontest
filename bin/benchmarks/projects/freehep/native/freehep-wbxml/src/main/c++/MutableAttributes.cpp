
#include <iostream>

#include <WBXML/MutableAttributes.h>

using namespace std;
using namespace WBXML;

vector<unsigned int>& MutableAttributes::getTags() {
	return attrTags;
}

IAttributes::Types MutableAttributes::getType(unsigned int tag) {
	std::map<unsigned int, Types>::iterator i = attrTypes.find(tag);
	return (i != attrTypes.end()) ? (*i).second : UNDEFINED;
}

bool MutableAttributes::getBooleanValue(unsigned int tag, bool def) {
	std::map<unsigned int, bool>::iterator i = booleanAtts.find(tag);
	return (i != booleanAtts.end()) ? (*i).second : def;
}

unsigned char MutableAttributes::getByteValue(unsigned int tag, unsigned char def) {	
	std::map<unsigned int, int64>::iterator i = longAtts.find(tag);
	return (i != longAtts.end()) ? (unsigned char)(*i).second : def;
}

wchar_t MutableAttributes::getCharValue(unsigned int tag, wchar_t def) {
	std::map<unsigned int, int64>::iterator i = longAtts.find(tag);
	return (i != longAtts.end()) ? (wchar_t)(*i).second : def;
}

double MutableAttributes::getDoubleValue(unsigned int tag, double def) {
	std::map<unsigned int, double>::iterator i = doubleAtts.find(tag);
	return (i != doubleAtts.end()) ? (*i).second : def;
}

float MutableAttributes::getFloatValue(unsigned int tag, float def) {
	std::map<unsigned int, double>::iterator i = doubleAtts.find(tag);
	return (i != doubleAtts.end()) ? (float)(*i).second : def;
}

int MutableAttributes::getIntValue(unsigned int tag, int def) {
	std::map<unsigned int, int64>::iterator i = longAtts.find(tag);
	return (i != longAtts.end()) ? (int)(*i).second : def;
}

int64 MutableAttributes::getLongValue(unsigned int tag, int64 def) {
	std::map<unsigned int, int64>::iterator i = longAtts.find(tag);
	return (i != longAtts.end()) ? (*i).second : def;
}

short MutableAttributes::getShortValue(unsigned int tag, short def) {
	std::map<unsigned int, int64>::iterator i = longAtts.find(tag);
	return (i != longAtts.end()) ? (short)(*i).second : def;
}

wstring MutableAttributes::getStringValue(unsigned int tag, wstring def) {
	std::map<unsigned int, wstring>::iterator i = stringAtts.find(tag);
	return (i != stringAtts.end()) ? (*i).second : def;
}

void MutableAttributes::clear() {
	attrTags.clear();
	attrTypes.clear();
	booleanAtts.clear();
	doubleAtts.clear();
	longAtts.clear();
	stringAtts.clear();
}

void MutableAttributes::set(unsigned int tag, bool value) {
	attrTags.push_back(tag);
	attrTypes[tag] = BOOLEAN;
	booleanAtts[tag] = value;
}

void MutableAttributes::set(unsigned int tag, unsigned char value) {
	attrTags.push_back(tag);
	attrTypes[tag] = BYTE;
	longAtts[tag] = value;
}

void MutableAttributes::set(unsigned int tag, wchar_t value) {
	attrTags.push_back(tag);
	attrTypes[tag] = CHAR;
	longAtts[tag] = value;
}

void MutableAttributes::set(unsigned int tag, double value) {
	attrTags.push_back(tag);
	attrTypes[tag] = DOUBLE;
	doubleAtts[tag] = value;
}

void MutableAttributes::set(unsigned int tag, float value) {
	attrTags.push_back(tag);
	attrTypes[tag] = FLOAT;
	doubleAtts[tag] = value;
}

void MutableAttributes::set(unsigned int tag, int value) {
	attrTags.push_back(tag);
	attrTypes[tag] = INT;
	longAtts[tag] = value;
}

void MutableAttributes::set(unsigned int tag, int64 value) {
	attrTags.push_back(tag);
	attrTypes[tag] = LONG;
	longAtts[tag] = value;
}

void MutableAttributes::set(unsigned int tag, short value) {
	attrTags.push_back(tag);
	attrTypes[tag] = SHORT;
	longAtts[tag] = value;
}

void MutableAttributes::set(unsigned int tag, wstring value) {
	attrTags.push_back(tag);
	attrTypes[tag] = STRING;
	stringAtts[tag] = value;
}

void MutableAttributes::set(unsigned int tag, vector<wstring> value) {
	// FIXME, no array handling yet
	cerr << "MutableAttributes::set(unsigned int tag, vector<wstring> value): NOT IMPLEMENTED" << endl;
}
