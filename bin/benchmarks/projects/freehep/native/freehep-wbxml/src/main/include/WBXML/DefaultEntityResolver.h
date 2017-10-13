#ifndef WBXML_DEFAULT_ENTITY_RESOLVER_INCLUDE
#define WBXML_DEFAULT_IENTITY_RESOLVER_INCLUDE 1

#include <WBXML/IEntityResolver.h>

namespace WBXML {

class DefaultEntityResolver : public virtual IEntityResolver {    

public: 
	virtual std::istream* resolveEntity(std::wstring name, std::wstring publicId, std::wstring systemId) {
		return NULL;
	}
};

}

#endif
