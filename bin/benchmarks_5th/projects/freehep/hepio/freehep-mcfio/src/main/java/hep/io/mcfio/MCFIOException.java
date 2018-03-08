package hep.io.mcfio;

import java.io.IOException;


/**
 * An exception thrown for certain MCFIO specific errors.
 *
 * @author Tony Johnson (tonyj@slac.stanford.edu)
 * @version $Id: MCFIOException.java 8584 2006-08-10 23:06:37Z duns $
 */
public class MCFIOException extends IOException
{
   MCFIOException(String message)
   {
      super(message);
   }
}
