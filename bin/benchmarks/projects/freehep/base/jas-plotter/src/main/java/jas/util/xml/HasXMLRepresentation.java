package jas.util.xml;

/**
 * An interface to be implemented by Objects which can 
 * provide their own XML representation
 */
public interface HasXMLRepresentation
{
	void writeAsXML(XMLWriter pw);
}
