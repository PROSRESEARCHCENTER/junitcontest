// Copyright FreeHEP, 2007.
package org.freehep.wbxml;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.List;

import org.xml.sax.SAXException;

/**
 * 
 * @author Mark Donszelmann
 * @version $Id$
 */
public interface ExtensionHandler {

	public void extI(int i, String readStrI, int tagID, int attributeID, MutableAttributes atts, List value) throws SAXException;

	public void extT(int i, int readInt, int tagID, int attributeID, MutableAttributes atts, List value) throws SAXException;

	public void ext(int i, int tagID, int attributeID, MutableAttributes atts, List value) throws SAXException;

	public void opaque(int len, DataInputStream in, int tagID, int attributeID, MutableAttributes atts, List value) throws IOException, SAXException;
}
