#ifndef WBXML_IENTITY_RESOLVER_INCLUDE
#define WBXML_IENTITY_RESOLVER_INCLUDE 1

#include <iostream>
#include <istream>
#include <string>

namespace WBXML {

class IEntityResolver {    

public: 
	virtual ~IEntityResolver() {;}
	virtual std::istream* resolveEntity(std::wstring name, std::wstring publicId, std::wstring systemId) = 0;
};

}

#endif
