#include <iostream>
#include <fstream>

#include <WBXML/Parser.h>
#include <WBXML/DefaultExtensionHandler.h>
#include <WBXML/DefaultEntityResolver.h>
#include <WBXML/AidaWBXML.h>
#include <TestContentHandler.h>

using namespace std;
using namespace WBXML;

int main(int argc, char** argv) {
	if (argc < 2) {
		cerr << "Usage: TestWBXMLParser filename" << endl;
		return 1;
	}

	ifstream in(argv[1], ifstream::in | ifstream::binary);
	if (in.fail()) {
		cerr << "Cannot open: " << argv[1] << endl;
		return 1;
	}

	AidaWBXML tHandler;
	TestContentHandler cHandler(tHandler);
	DefaultExtensionHandler eHandler;
	DefaultEntityResolver eResolver;
	Parser p(tHandler, cHandler, eHandler, eResolver);
        if (!p.parse(in)) {
                cerr << "Parsing FAILED: " << argv[1] << endl;
                return 1;
        }
	in.close();

	return 0;
}

