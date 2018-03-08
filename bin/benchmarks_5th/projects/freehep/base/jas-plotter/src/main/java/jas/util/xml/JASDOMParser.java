package jas.util.xml;

import jas.util.NestedException;
import jas.util.NestedRuntimeException;

import java.io.Reader;

import org.w3c.dom.Document;
import org.xml.sax.EntityResolver;

/**
 * Ideally wouldnt need this interface, except that the DOM specification
 * only specifies how to extract information from a DOM, not how to create
 * a DOM from an XML file. The JASDOMParser interface is meant to make up
 * for that deficiency.
 */
public abstract class JASDOMParser 
{
	/**
	 * Create a DOM document by reading an XML file
	 * @param in A reader set up to read an XML file
	 * @param fileName The name of the file being read (used in error messages)
	 * @return Document The resulting DOM
	 * @exception XMLException thrown if there is an error reading the XML
	 */
	public abstract Document parse(Reader in, String fileName) throws JASXMLException;
	
	/**
	 * Create a DOM document by reading an XML file with an explicit entity resolver.
	 * An entity resolver is typically used to specify where to find the DTD for the XML
	 * document.
	 * @param in A reader set up to read an XML file
	 * @param fileName The name of the file being read (used in error messages)
	 * @param resolver An entity resolver to use when reading the XML file
	 * @return Document The resulting DOM
	 * @exception XMLException thrown if there is an error reading the XML
	 */
	public abstract Document parse(Reader in, String fileName, EntityResolver resolver) throws JASXMLException;

	/**
	 * An exception that gets thrown if there is an error reading an XML file.
	 */
	public static class JASXMLException extends NestedException
	{
		public JASXMLException(String message)
		{
			super(message,null);
		}
		public JASXMLException(String message, Throwable detail)
		{
			super(message,detail);
		}
	}
	/**
	 * Creates a default instance of a JASDOMParser
	 * @return A JASDOMParser
	 */
	public static JASDOMParser instance()
	{
		try
		{
			return (JASDOMParser) Class.forName("jas.util.xml.parserwrappers.JAXPDOMParser").newInstance();
		}
		catch (Throwable x)
		{
			throw new NestedRuntimeException("Unable to create default JASDomParser",x);
		}
	}
}
