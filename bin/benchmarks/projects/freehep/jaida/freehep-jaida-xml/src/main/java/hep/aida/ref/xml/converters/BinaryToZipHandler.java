// Copyright FreeHEP, 2007
package hep.aida.ref.xml.converters;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.freehep.wbxml.ContentHandler;
import org.freehep.wbxml.EntityResolver;
import org.freehep.wbxml.WBXMLParser;
import org.xml.sax.SAXException;

/**
 * 
 * @author Mark Donszelmann
 * @version $Id$
 */
public class BinaryToZipHandler extends ToZipHandler implements ContentHandler, EntityResolver {

	public BinaryToZipHandler() {
	}

    void convert(String in, String out, boolean binary) throws IOException, SAXException {
		super.convert(out, binary);
		WBXMLParser p = new WBXMLParser(this);
		p.setEntityResolver(this);
		p.parse(new FileInputStream(in));
	}

	public InputStream resolveEntity(String name, String publidId,
			String systemId) throws SAXException, IOException {
		dtdName = name;
		dtdSystemId = systemId;
		return null;
	}
}
