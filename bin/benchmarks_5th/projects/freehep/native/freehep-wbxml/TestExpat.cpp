#include <iostream>
#include <fstream>
#include <map>

#include <WBXML/AidaWBXML.h>
#include <WBXML/AidaAttributesAdapter.h>

#include <TestContentHandler.h>

#include <expat.h>
#define BUFFSIZE        8192
char Buff[BUFFSIZE];

using namespace std;
using namespace WBXML;

int Depth;
AidaWBXML aida;
TestContentHandler contentHandler(aida);

static void XMLCALL startElement(void *data, const char *el, const char **attr) {

	// lookup tag
	int tag = aida.getTag(el);

	if (tag < 0) {
		cerr << "Unknow tag: " << el << endl;
		return;
	}

	// create adapter
	AidaAttributesAdapter attributes(aida, attr);

	// is empty
	bool empty = aida.isTagEmpty(tag);

	// handle element
	bool ok = contentHandler.startElement(tag, attributes, empty);

	// handle return
	if (!ok) cerr << "Element: " << el << " not handled." << endl;
}

static void XMLCALL endElement(void *data, const char *el) {
	// lookup tag
	int tag = aida.getTag(el);

	if (tag < 0) {
		cerr << "Unknow tag: " << el << endl;
		return;
	}

	// handle end element
	bool ok = contentHandler.endElement(tag);

	// handle return
	if (!ok) cerr << "Element: " << el << " not handled." << endl;
}

static void XMLCALL characters(void *userData, const char *s, int len) {
	string value(s, len);

	// Make room for characters
	wstring wvalue(value.length(), L' ');

	// Copy string to wstring
	copy(value.begin(), value.end(), wvalue.begin());

	// handle characters
	bool ok = contentHandler.characters(wvalue, 0, wvalue.size());

	// handle return
	if (!ok) cerr << "Characters: \"" << value << "\" not handled." << endl;
}

static void XMLCALL startDoctype(void *userData,
		const char *doctypeName,
		const char *sysid,
		const char *pubid,
		int has_internal_subset) {
	cerr << "DOCTYPE " << doctypeName << " " << sysid << " " << pubid << endl;
}

static void XMLCALL endDoctype(void *userData) {
	cerr << "ENDDOCTYPE" << endl;
}

int main(int argc, char **argv) {
	if (argc < 2) {
		cerr << "Usage: TestExpat filename"<< endl;
		return 1;
	}

	ifstream in(argv[1], ifstream::in | ifstream::binary);
	if (in.fail()) {
		cerr << "Cannot open: "<< argv[1]<< endl;
		return 1;
	}

	XML_Parser p = XML_ParserCreate(NULL);
	if (!p) {
		cerr << "Couldn't allocate memory for parser"<< endl;
		return -1;
	}

	XML_SetElementHandler(p, startElement, endElement);
	XML_SetCharacterDataHandler(p, characters);
	XML_SetDoctypeDeclHandler(p, startDoctype, endDoctype);
	
	for (;;) {
		int done;
		int len;

		in.read(Buff, BUFFSIZE);
		len = in.gcount();
		if (in.bad()) {
			cerr << "Read error" << endl;
			return -1;
		}
		done = in.eof();

		if (XML_Parse(p, Buff, len, done) == XML_STATUS_ERROR) {
			cerr << "Parse error at line " <<XML_GetCurrentLineNumber(p) << endl;
			cerr << XML_ErrorString(XML_GetErrorCode(p)) << endl;
			return -1;
		}

		if (done)
		break;
	}

	XML_ParserFree(p);
	return 0;
}
