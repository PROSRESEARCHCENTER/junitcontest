/*
 * ReadOnlyException.java
 *
 * Created on November 1, 2002, 12:12 PM
 */

package hep.aida.ref;

import hep.aida.IManagedObject;

/**
 * An exception thrown when an attempt is made to modify a readOnly AIDA object.
 * @author tonyj
 * @version $Id: ReadOnlyException.java 10701 2007-04-19 21:16:10Z serbo $
 */
public class ReadOnlyException extends RuntimeException
{
   public ReadOnlyException()
   {
      super("Attempt to modify readonly AIDA object");
   }
   
   public ReadOnlyException(IManagedObject mo)
   {
      super("Attempt to modify readonly AIDA object: name="+mo.name()+", type="+mo.type());
   }
}