package hep.io.root.core;

/**
 *
 * @author tonyj
 * @version $Id: WrongLengthException.java 13617 2009-04-09 22:48:46Z tonyj $
 */
public class WrongLengthException extends java.io.IOException
{
   public WrongLengthException(long offset, String className)
   {
      super("Unexpected Length for class " + className + " (offset " + offset + ")");
   }
}
