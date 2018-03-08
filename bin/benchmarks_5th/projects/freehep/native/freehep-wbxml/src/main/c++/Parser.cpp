#include <sstream>
#include <string>
#include <vector>

#include <WBXML/WBXML.h>
#include <WBXML/Parser.h>
#include <WBXML/MutableAttributes.h>
#include <WBXML/DefaultReader.h>
#include <WBXML/VerboseReader.h>

/**
 * SAX-like Binary XML Parser. There is NO support for namespaces, attrPrefixValues or attrValues.
 * Code pages are handled internally. Both attribute and tag code indexes start at 0 and run up. 
 * 
 * @author Mark Donszelmann
 * @version $Id: Parser.cpp 12911 2007-07-02 21:33:13Z duns $
 */

using namespace std;
using namespace WBXML;

Parser::Parser(ITagHandler& tHandler, IContentHandler& cHandler, IExtensionHandler& eHandler,
		IEntityResolver& eResolver) :
	tagHandler(tHandler), contentHandler(cHandler), extensionHandler(eHandler), resolver(eResolver) {
}

bool Parser::parse(istream& in, bool verbose) {
	DefaultReader reader(in);
	if (verbose) {
		VerboseReader verboseReader(reader);
		return parse(verboseReader, true);
	} else {
		return parse(reader, false);
	}
}

bool Parser::parse(IReader& reader, bool verbose) {
	wchar_t entityBuf;

	debug = verbose;
	tagPage = 0;
	attributePage = 0;

	if (!reader.readByte(version))
		return false;
	if (version != 0x03)
		return false;
    if (debug) cout << " : Version";

	if (!reader.readMultiByteInt(publicIdentifierId))
		return false;
	if (debug) cout << " : PublicIdentifierId";
	unsigned int dtdIndex = 0;
	if (publicIdentifierId == 0) {
		if (!reader.readMultiByteInt(dtdIndex))
			return false;
		if (debug) cout << " : dtdIndex";
	} else {
		return false;
	}

	if (dtdIndex != 0)
		return false;

	if (!reader.readMultiByteInt(charSet))
		return false;
	if (charSet != 0x6a)
		return false;
	if (debug) cout << " : CharSet";

	unsigned int len;
	if (!reader.readMultiByteInt(len))
		return false;
	if (debug) cout << " : StringTable Len";
	unsigned int offset = 0;
	while (offset < len) {
		wstring s;
		unsigned short utflen;
		if (!reader.readUTF(s, utflen))
			return false;
		unsigned char b;
		if (!reader.readByte(b))
			return false; // skip null termination
		if (debug) cout << " : null termination";
		reader.putStrT(offset, s);
		offset += utflen + 2+ 1;
	}

	if (!contentHandler.startDocument())
		return false;

	if (publicIdentifierId == 0) {
		wstring dtdPair;
		if (!reader.getStrT(dtdIndex, dtdPair))
			return false;
		if (debug) cout << " : dtdPair";
		size_t space = dtdPair.find(' ');
		if (space == string::npos) {
			cout << endl << "DTD specifier does not contain 'space'"<< endl;
			exit(1);
		}
		wstring name = dtdPair.substr(0, space);
		wstring dtd = dtdPair.substr(space+1);

		istream* resolverStream = resolver.resolveEntity(name,L"" , dtd);
		if (resolverStream != NULL) {
			// FIXME, use the resolver stream
			cout << endl << "Entity Resolver NOT Used." << endl;
		}
	}

	unsigned char id;
	while (reader.readByte(id)) {
		switch (id) {
			case WBXML::SWITCH_PAGE:
			if (debug) cout << " : Switch Page";
			if (!reader.readByte(tagPage)) return false;
			if (debug) cout << " : TagPage: " << (unsigned int)tagPage;
			break;

			case WBXML::END: {
				unsigned int tagID;
				if (stack.empty()) return false;
				tagID = stack.top();
				stack.pop();
				if (debug) cout << " : </" << tagHandler.getTag(tagID) << ">";
				if (!contentHandler.endElement(tagID)) return false;
				break;
			}
			case WBXML::ENTITY:
			if (debug) cout << " : Entity";
			if (!reader.readMultiByteInt((unsigned int&)entityBuf)) return false;
			if (debug) cout << " : " << entityBuf;
			if (!contentHandler.characters(&entityBuf, 0, 1)) return false;
			break;

			case WBXML::STR_I: {
				if (debug) cout << " : STR_I";
				wstring s;
				if (!reader.readStrI(s)) return false;
				if (!contentHandler.characters(s, 0, s.length())) return false;
				break;
			}
			case WBXML::EXT_I_0:
			case WBXML::EXT_I_1:
			case WBXML::EXT_I_2:
			case WBXML::EXT_T_0:
			case WBXML::EXT_T_1:
			case WBXML::EXT_T_2:
			case WBXML::EXT_0:
			case WBXML::EXT_1:
			case WBXML::EXT_2:
			case WBXML::OPAQUE: {
				MutableAttributes result;
				vector<wstring> value;
				if (!handleExtensions(reader, id, stack.top(), -1, result, value)) return false;
				break;
			}
			case WBXML::PI:
				if (debug) cout << " : PI";
			return false;
			//				throw new SAXException("PI Not Supported");
			break;

			case WBXML::STR_T: {
				if (debug) cout << " : STR_T";
				wstring s;
				unsigned int pos;
				if (!reader.readStrT(s, pos)) return false;
				if (!contentHandler.characters(s, 0, s.size())) return false;
				break;
			}
			default:
			if (!readElement(reader, id)) return false;
			break;
		}
	}

	if (!stack.empty()) {
		cout << endl << "Stack not empty " << stack.size();
		return false;
		//	throw new SAXException("unclosed elements: " + stack);
	}

	return contentHandler.endDocument();
}

unsigned int Parser::getTagId(unsigned char id) {
	return (id & 0x03f) + (tagPage * WBXML_MAX_CODES)- WBXML_RESERVED_CODES;
}

unsigned int Parser::getAttributeId(unsigned char id) {
	return (id & 0x03f) + (attributePage * WBXML_MAX_CODES)
			- WBXML_RESERVED_CODES;
}

bool Parser::readElement(IReader& reader, unsigned char id) {
	unsigned int tagID = getTagId(id & 0x3f);

	if (debug) cout << " : TagID (0x" << hex << tagID << "): <" << tagHandler.getTag(tagID) << ">";

	bool empty;
	if ((id & WBXML_CONTENT) != 0) {
		stack.push(tagID);
		empty = false;
	} else {
		empty = true;
	}

	MutableAttributes attributes;
	if (((id & WBXML_ATTRIBUTE) != 0) && (!readAttr(reader, tagID, attributes)))
		return false;

	return contentHandler.startElement(tagID, attributes, empty);
}

bool Parser::readAttr(IReader& reader, unsigned int tagID, MutableAttributes& atts) {
	int intResult;
	bool hasIntResult = false;
	wchar_t charResult;
	int hasCharResult = false;

	unsigned char id;
	if (!reader.readByte(id))
		return false;
	int attributeID = -1;
	while (id != WBXML::END) {
		// attribute start
		while (id == WBXML::SWITCH_PAGE) {
			if (debug) cout << " : Switch Page";
			if (!reader.readByte(attributePage))
				return false;
			if (debug) cout << " : AttributePage: " << (unsigned int)attributePage;
			if (!reader.readByte(id))
				return false;
		}
		attributeID = getAttributeId(id);

		if (debug) cout << " : AttributeID (0x" << hex << attributeID << "): " << tagHandler.getAttribute(attributeID);

		vector<wstring> value;

		// attribute value(s)
		if (!reader.readByte(id))
			return false;
		while (id > 128|| id == WBXML::SWITCH_PAGE || id == WBXML::ENTITY || id
				== WBXML::STR_I || id == WBXML::STR_T|| (id >= WBXML::EXT_I_0
				&& id <= WBXML::EXT_I_2)|| (id >= WBXML::EXT_T_0 && id
				<= WBXML::EXT_T_2)) {

			switch (id) {
			case WBXML::SWITCH_PAGE:
				if (debug) cout << " : Switch Page";
				if (!reader.readByte(attributePage))
					return false;
				if (debug) cout << " : AttributePage: " << (unsigned int)attributePage;
				break;

			case WBXML::ENTITY:
				if (debug) cout << " : Entity";
				if (!reader.readMultiByteInt((unsigned int&)charResult))
					return false;
				if (debug) cout << " : " << charResult;
				hasCharResult = true;
				value.push_back(&charResult);
				break;

			case WBXML::STR_I: {
				if (debug) cout << " : STR_I";
				wstring s;
				if (!reader.readStrI(s))
					return false;
				value.push_back(s);
				break;
			}
			case WBXML::EXT_I_0:
			case WBXML::EXT_I_1:
			case WBXML::EXT_I_2:
			case WBXML::EXT_T_0:
			case WBXML::EXT_T_1:
			case WBXML::EXT_T_2:
			case WBXML::EXT_0:
			case WBXML::EXT_1:
			case WBXML::EXT_2:
			case WBXML::OPAQUE:
				if (!handleExtensions(reader, id, tagID, attributeID, atts,
						value))
					return false;
				break;

			case WBXML::STR_T: {
				if (debug) cout << " : STR_T";
				wstring s;
				unsigned int pos;
				if (!reader.readStrT(s, pos))
					return false;
				value.push_back(s);
				break;
			}
			default:
				intResult = getAttributeId(id);
				hasIntResult = true;
				wstringstream s;
				s << intResult;
				value.push_back(s.str());
				break;
			}
			if (!reader.readByte(id))
				return false;
		}
		
		switch (value.size()) {
		case 0:
			// already handled
			break;
		case 1:
			if (hasIntResult) {
				atts.set(attributeID, intResult);
			} else if (hasCharResult) {
				atts.set(attributeID, charResult);
			} else {
				atts.set(attributeID, value[0]);
			}
			break;
		default:
			atts.set(attributeID, value);
			break;
		}
		attributeID = -1;
	}
	if (debug) cout << " : AttributeEnd";

	return true;
}

bool Parser::handleExtensions(IReader& reader, unsigned int id,
		unsigned int tagID, int attributeID, MutableAttributes& atts,
		vector<wstring>& value) {

	switch (id) {
	case WBXML::EXT_I_0:
	case WBXML::EXT_I_1:
	case WBXML::EXT_I_2: {
		if (debug) cout << " : EXT_I_x";
		wstring s;
		if (!reader.readStrI(s))
			return false;
		if (!extensionHandler.extI(id - WBXML::EXT_I_0, s, tagID, attributeID,
				atts, value))
			return false;
		break;
	}
	case WBXML::EXT_T_0:
	case WBXML::EXT_T_1:
	case WBXML::EXT_T_2: {
		if (debug) cout << " : EXT_T_x";
		unsigned int i;
		if (!reader.readMultiByteInt(i))
			return false;
		if (!extensionHandler.extT(id - WBXML::EXT_T_0, i, tagID, attributeID,
				atts, value))
			return false;
		break;
	}
	case WBXML::EXT_0:
	case WBXML::EXT_1:
	case WBXML::EXT_2:
		if (debug) cout << " : EXT_x";
		if (!extensionHandler.ext(id - WBXML::EXT_0, tagID, attributeID, atts,
				value))
			return false;
		break;

	case WBXML::OPAQUE: {
		if (debug) cout << " : OPAQUE";
		unsigned int len;
		if (!reader.readMultiByteInt(len))
			return false;
		if (debug) cout << " : Len";
		if (!extensionHandler.opaque(len, reader, tagID, attributeID, atts,
				value))
			return false;
		break;
	}
	}
	return true;
}

unsigned int Parser::getCharSet() {
	return charSet;
}

unsigned int Parser::getVersion() {
	return version;
}

