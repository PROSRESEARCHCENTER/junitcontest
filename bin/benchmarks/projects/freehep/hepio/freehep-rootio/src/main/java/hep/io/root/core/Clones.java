package hep.io.root.core;


/**
 * A Base class for a set of Clones, read in split mode.
 * @author  tonyj
 */
public abstract class Clones
{
   public abstract void read(RootInput in, int nClones) throws java.io.IOException;
}
