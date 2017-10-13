/*
 * XMLIOFile.java
 */

package org.freehep.xml.io;
import org.jdom.Element;
import java.lang.String;

/**
 * This is a dummy class. It saves the name of a file,
 * and restores it registering it with the XMLIOManager
 *
 * @author  turri
 * @version 1.0
 */

public class XMLIOFile implements XMLIO
{
    private String fileName;

    /**
     * The default constructor does nothing
     */
    public XMLIOFile() {}

    /**
     * The constructor only loads the name of the file
     * @param fileName the name of the file
     */
    public XMLIOFile( String fileName ) {
	this.fileName = fileName;
    }

    public void save(XMLIOManager xmlioManager, Element el) {
	el.setAttribute( "fName", fileName );
    }

    public void restore(XMLIOManager xmlioManager, Element nodeEl) {
	fileName = (String) nodeEl.getAttributeValue( "fName" );
    }
    
}

