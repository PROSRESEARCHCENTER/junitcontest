/*
 * JAXPDomParser.java
 *
 * Created on March 30, 2002, 11:51 PM
 */

package jas.util.xml.parserwrappers;
import jas.util.xml.JASDOMParser;

import java.io.IOException;
import java.io.Reader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *
 * @author tonyj
 */
public class JAXPDOMParser extends JASDOMParser
{
   /**
    * Create a DOM document by reading an XML file
    * @param in A reader set up to read an XML file
    * @param fileName The name of the file being read (used in error messages)
    * @return Document The resulting DOM
    * @exception XMLException thrown if there is an error reading the XML
    */
   public Document parse(Reader in, String fileName) throws JASXMLException
   {
		return parse(in,fileName,null);
   }
   
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
   public Document parse(Reader in, String fileName, EntityResolver resolver) throws JASXMLException
   {
      try
      {
         DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
         factory.setValidating(true);
         DocumentBuilder parser = factory.newDocumentBuilder();
         XMLErrorHandler errorHandler = new XMLErrorHandler(fileName);
         parser.setErrorHandler(errorHandler);
         if (resolver != null) parser.setEntityResolver(resolver);
         InputSource is = new InputSource(in);
         is.setSystemId("file:/");
         Document doc = parser.parse(is);
         if (errorHandler.getLevel() > 1) throw new SAXException("Error during XML file parsing");
         return doc;
      }
      catch (SAXException x)
		{
			throw new JASDOMParser.JASXMLException("Syntax error parsing XML file",x);
		}
		catch (IOException x)
		{
			throw new JASDOMParser.JASXMLException("IO error parsing XML file",x);
		}
      catch (ParserConfigurationException x)
      {
         throw new JASDOMParser.JASXMLException("Can not create XML parser",x);
      }
   }
}
