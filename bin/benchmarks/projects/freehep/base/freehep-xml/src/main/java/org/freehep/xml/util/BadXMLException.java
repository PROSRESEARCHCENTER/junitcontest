package org.freehep.xml.util;

import org.xml.sax.SAXException;

/**
 * A SAXException with an optional nested exception
 * @author tonyj
 * @version $Id: BadXMLException.java 8584 2006-08-10 23:06:37Z duns $
 */

public class BadXMLException extends SAXException
{
   /**
	 * 
	 */
   private static final long serialVersionUID = 1493961676061071756L;
   
   public BadXMLException(String message)
   {
      super(message);
   }
   public BadXMLException(String message, Throwable detail)
   {
      super(message);
      initCause(detail);
   }
}
