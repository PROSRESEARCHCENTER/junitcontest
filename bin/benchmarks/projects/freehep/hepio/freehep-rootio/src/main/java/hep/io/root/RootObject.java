package hep.io.root;

/**
 * A representation of a RootObject
 * @author Tony Johnson (tonyj@slac.stanford.edu)
 * @version $Id: RootObject.java 8584 2006-08-10 23:06:37Z duns $
 */
public interface RootObject
{
   /**
    * Get the class of this object
    * @return The RootClass for this object
    */
   RootClass getRootClass();
}
