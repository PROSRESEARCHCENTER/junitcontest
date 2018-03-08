// Copyright FreeHEP, 2007.
package org.freehep.wbxml;

import org.xml.sax.SAXException;

/**
 * 
 * @author Mark Donszelmann
 * @version $Id$
 */
public interface ContentHandler {

    public void startDocument() throws SAXException;
    public void endDocument() throws SAXException;
    
    public void startElement(int tagID, Attributes attr, boolean empty) throws SAXException;
    public void endElement(int tagID) throws SAXException;
    
    public void characters(char[] chars, int start, int len) throws SAXException;
}
