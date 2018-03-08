// Copyright 2003, FreeHEP.
package hep.aida.ref.xml;

import org.freehep.xml.util.ClassPathEntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *
 * @author The FreeHEP team @ SLAC
 *
 */
public class AIDAEntityResolver extends ClassPathEntityResolver {
    
    private String prefix;
    
    public AIDAEntityResolver(Class root, String DTDPrefix) {
        super(root,DTDPrefix);
        this.prefix = DTDPrefix;
    }
    
    public InputSource resolveEntity(String publicId, String systemId) throws SAXException {
        if ( ! systemId.startsWith( prefix ) )
            systemId = "http://aida.freehep.org/schemas/2.2/aida.dtd";
        return super.resolveEntity(publicId, systemId);
    }
    
}
