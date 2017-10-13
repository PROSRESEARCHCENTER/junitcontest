/*
 * Interface created by InterfaceBuilder. Do not modify.
 *
 * Created on Wed Dec 12 18:41:21 PST 2001
 */
package hep.io.root.interfaces;

public interface TBranchElement extends hep.io.root.RootObject, TBranch
{
   public final static int rootIOVersion = 6;

   /** pointer to primary branchcount branch */
   TBranchElement getBranchCount();

   /** pointer to secondary branchcount branch */
   TBranchElement getBranchCount2();

   /** Class name of referenced object */
   String getClassName();

   /** Version number of class */
   int getClassVersion();

   Class getCloneClass();

   /** Name of class in TClonesArray (if any) */
   String getClonesName();

   /** element serial number in fInfo */
   int getID();

   /** Maximum entries for a TClonesArray or variable array */
   int getMaximum();

   /** Name of parent class */
   String getParentName();

   /** branch streamer type */
   int getStreamerType();

   /** branch type */
   int getType();
}
