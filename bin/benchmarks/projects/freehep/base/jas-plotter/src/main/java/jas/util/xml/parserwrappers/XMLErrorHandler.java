/*
 * XMLErrorHandler.java
 *
 * Created on March 31, 2002, 12:08 AM
 */

package jas.util.xml.parserwrappers;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 *
 * @author tonyj
 */
class XMLErrorHandler implements ErrorHandler
{
   private int level = 0;
   private String fileName;
   XMLErrorHandler(String fileName)
   {
      this.fileName = fileName;
   }
   int getLevel()
   {
      return level;
   }
   public void warning(SAXParseException exception)
   {
      //System.err.println(fileName+": Warning at line "+exception.getLineNumber()+": "+exception);
      if (level < 1) level = 1;
   }
   public void error(SAXParseException exception)
   {
      System.err.println(fileName+": Error at line "+exception.getLineNumber()+": "+exception);
      if (level < 2) level = 2;
   }
   public void fatalError(SAXParseException exception) throws SAXException
   {
      System.err.println(fileName+": Fatal error at line "+exception.getLineNumber()+": "+exception);
      if (level < 3) level = 3;
      throw exception;
   }
}
