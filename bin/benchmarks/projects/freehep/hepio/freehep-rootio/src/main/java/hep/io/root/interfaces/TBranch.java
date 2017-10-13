/*
 * Interface created by InterfaceBuilder. Do not modify.
 *
 * Created on Fri Jan 19 12:05:56 PST 2001
 */
package hep.io.root.interfaces;

public interface TBranch extends hep.io.root.RootObject, TNamed
{
   public final static int rootIOVersion = 6;
   public final static int rootCheckSum = 2056727376;

   /** [fMaxBaskets] Length of baskets on file */
   int[] getBasketBytes();

   /** [fMaxBaskets] Table of first entry in eack basket */
   long[] getBasketEntry();

   /** [fMaxBaskets] Addresses of baskets on file */
   long[] getBasketSeek();

   /** Initial Size of  Basket Buffer */
   int getBasketSize();

   /** -> List of baskets of this branch */
   TObjArray getBaskets();

   TBranch getBranchForMangledName(String name);

   TBranch getBranchForName(String name);

   /** -> List of Branches of this branch */
   TObjArray getBranches();

   /** (=1 branch is compressed, 0 otherwise) */
   int getCompress();

   /** Number of entries */
   long getEntries();

   /** Current entry number (last one filled in this branch) */
   long getEntryNumber();

   /** Initial Length of fEntryOffset table in the basket buffers */
   int getEntryOffsetLen();

   /** Name of file where buffers are stored ("" if in same file as Tree header) */
   String getFileName();

   /** -> List of leaves of this branch */
   TObjArray getLeaves();

   /** Maximum number of Baskets so far */
   int getMaxBaskets();

   /** Offset of this branch */
   int getOffset();

   hep.io.root.core.RootInput setPosition(TLeaf leaf, long index) throws java.io.IOException;

   /** Total number of bytes in all leaves before compression */
   long getTotBytes();

   /** Last basket number written */
   int getWriteBasket();

   /** Total number of bytes in all leaves after compression */
   long getZipBytes();
}
