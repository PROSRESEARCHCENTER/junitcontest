package org.freehep.util.commanddispatcher;

/**
 * Thrown when a command invoked via reflection throws an exception.
 * The original exception is available by callng getTargetException()
 *
 * @author Tony Johnson (tonyj@slac.stanford.edu)
 * @version $Id:
 */
public class CommandInvocationException extends Exception
{
   private Throwable m_target;

   CommandInvocationException(Throwable t)
   {
      super("Error during command invocation");
      m_target = t;
   }

   public Throwable getTargetException()
   {
      return m_target;
   }
}
