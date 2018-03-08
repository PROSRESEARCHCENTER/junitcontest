package hep.io.root;

/**
 * An interface representing a RootClass.
 * RootClass objects are created by a RootClassFactory
 * @see hep.io.root.core.RootClassFactory
 * @author Tony Johnson (tonyj@slac.stanford.edu)
 * @version $Id: RootClass.java 8584 2006-08-10 23:06:37Z duns $
 */
public interface RootClass
{
   /**
    * Get the check sum for this Root class
    */
   public int getCheckSum();

   /**
    * Get the Java class corresponding to this Root class
    */
   public Class getJavaClass();

   public RootMember[] getMembers();

   /**
    * Get the version number for this Root class
    */
   public int getVersion();

   /**
    * Get the root class name for this Root class
    */
   String getClassName();

   /**
   * Get the super-classes of this Root class.
   */
   RootClass[] getSuperClasses();

   /**
    * Test if this class is a sub-class of the specified class
    */
   boolean instanceOf(RootClass superClass);
}
