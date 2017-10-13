#include <iostream>
#include <fstream>

#include <WBXML/Parser.h>
#include <WBXML/DefaultExtensionHandler.h>
#include <WBXML/DefaultWriter.h>
#include <WBXML/AidaWBXML.h>
#include <BinaryToBinaryHandler.h>

using namespace std;
using namespace WBXML;

int main(int argc, char** argv) {
	if (argc < 3) {
		cerr << "Usage: BinaryToBinary binaryInFile binaryOutFile" << endl;
		return 1;
	}

	ifstream in(argv[1], ifstream::in | ifstream::binary);
	if (in.fail()) {
		cerr << "Cannot open: " << argv[1] << endl;
		return 1;
	}
	ofstream out(argv[2], ofstream::out | ofstream::binary);
	if (out.fail()) {
		cerr << "Cannot open: " << argv[2] << endl;
		return 1;
	}

	AidaWBXML tHandler;
	DefaultWriter writer(out); 
	BinaryToBinaryHandler btobHandler(writer);
	DefaultExtensionHandler eHandler;
	Parser p(tHandler, btobHandler, eHandler, btobHandler);
	if (!p.parse(in)) {
		cerr << "Parsing FAILED: " << argv[1] << endl;
		return 1;
	}
	out.close();
	in.close();

	return 0;
}


