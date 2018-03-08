package hep.aida.ref.tree;

/**
 * A Link is a managed object representing a symbolic link.
 * It is used only within the Tree implementation, and is never exposed
 * to the user.
 * @author  The AIDA team @ SLAC.
 * @version $Id: Link.java 8584 2006-08-10 23:06:37Z duns $
 */
public class Link extends hep.aida.ref.ManagedObject {

    private Path path;
    private boolean isBroken = false;

    Link(String name, Path path) {
      super(name);
      this.path = path;
    }
    
    Path path() {
        return path;
    }
    
    boolean isBroken() {
        return isBroken;
    }
    
    private void setIsBroken( boolean isBroken ) {
        this.isBroken = isBroken;
    }    
    
    public String type()
    {
        return "lnk";
    }
    
}
