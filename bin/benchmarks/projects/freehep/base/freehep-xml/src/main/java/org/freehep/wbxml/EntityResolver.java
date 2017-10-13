package org.freehep.wbxml;

import java.io.IOException;
import java.io.InputStream;

import org.xml.sax.SAXException;

public interface EntityResolver {
	public InputStream resolveEntity(String name, String publicId, String systemId)
			throws SAXException, IOException;
}
