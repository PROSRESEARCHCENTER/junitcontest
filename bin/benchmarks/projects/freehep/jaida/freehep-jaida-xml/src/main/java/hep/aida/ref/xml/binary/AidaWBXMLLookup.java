// Copyright FreeHEP, 2007.
package hep.aida.ref.xml.binary;

import org.freehep.wbxml.Attributes;

/**
 * Aida Tag IDs, Attribute IDs and Attribute Types, used both for Binary and ASCII XML
 * 
 * @author Mark Donszelmann
 * @version $Id$
 */
public class AidaWBXMLLookup implements AidaWBXML {

	public static String getTagName(int tag) {
		String name = tags[tag];
		if (name == null)
			throw new RuntimeException("No TagName available for " + tag);
		return name;
	}

	public static int getTag(String name) {
		for (int i = 0; i < tags.length; i++) {
			if (tags[i].equals(name))
				return i;
		}
		return -1;
	}

	public static boolean isTagEmpty(int tag) {
		return tagIsEmpty[tag];
	}
	
	public static String getAttributeName(int tag) {
		if (tag == -2) return "value";
		String name = attributes[tag];
		if (name == null)
			throw new RuntimeException("No AttributeName available for " + tag);
		return name;
	}

	public static int getAttributeType(int tag) {
		if (tag < 0 || tag >= attributes.length) return Attributes.UNDEFINED;
		return attributeType[tag];
	}

	public static int getAttribute(String name) {
		if ("value".equals(name)) return -2;
		for (int i = 0; i < attributes.length; i++) {
			if (attributes[i].equals(name))
				return i;
		}
		return -1;
	}
	
}
