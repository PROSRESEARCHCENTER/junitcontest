/*
 * Interface created by InterfaceBuilder. Do not modify.
 *
 * Created on Fri Jan 19 12:59:21 PST 2001
 */
package hep.io.root.interfaces;

public interface TBranchObject extends hep.io.root.RootObject, TBranch
{
   public final static int rootIOVersion = 1;

   /** Class name of referenced object */
   String getClassName();
}
