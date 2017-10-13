package hep.io.root.interfaces;

import hep.io.root.*;
import java.io.IOException;


/**
 * @author Tony Johnson (tonyj@slac.stanford.edu)
 * @version $Id: TKey.java 8584 2006-08-10 23:06:37Z duns $
 */
public interface TKey extends hep.io.root.RootObject, TNamed
{
   /**
    * Get the cycle number for this key
    */
   short getCycle();

   /**
    * Get the object associated with this key.
    */
   RootObject getObject() throws RootClassNotFound, IOException;

   /**
    * Get the class of the object associated with this key.
    */
   RootClass getObjectClass() throws RootClassNotFound, IOException;
}