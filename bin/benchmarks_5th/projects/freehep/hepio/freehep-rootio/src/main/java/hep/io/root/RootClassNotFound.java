package hep.io.root;


/**
 * Exception thrown if a definition of a Root class can not be found
 * @author Tony Johnson (tonyj@slac.stanford.edu)
 * @version $Id: RootClassNotFound.java 8584 2006-08-10 23:06:37Z duns $
 */
public class RootClassNotFound extends Exception
{
   private String name;

   public RootClassNotFound(String name)
   {
      super("Could not find definition for " + name);
      this.name = name;
   }

   public String getClassName()
   {
      return name;
   }
}
