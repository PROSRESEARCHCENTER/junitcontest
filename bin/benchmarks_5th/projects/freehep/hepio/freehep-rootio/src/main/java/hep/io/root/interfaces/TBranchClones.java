/*
 * Interface created by InterfaceBuilder. Do not modify.
 *
 * Created on Fri Jan 19 17:38:30 PST 2001
 */
package hep.io.root.interfaces;

// Note, the version generated from the StreamerInfo does not extend TBranch.
// This file has been modified by hand
public interface TBranchClones extends hep.io.root.RootObject, TNamed, TBranch
{
   TBranch getBranchCount();

   String getClassName();

   int getEntryOffset();

   Class getObjectClass();
}
