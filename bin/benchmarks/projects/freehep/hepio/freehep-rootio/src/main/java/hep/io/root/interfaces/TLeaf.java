/*
 * Interface created by InterfaceBuilder. Do not modify.
 *
 * Created on Wed Jan 10 15:17:52 PST 2001
 */
package hep.io.root.interfaces;

import org.apache.bcel.generic.*;


public interface TLeaf extends hep.io.root.RootObject, TNamed
{
   public final static int rootIOVersion = 2;
   public final static int rootCheckSum = 727988519;

   int getArrayDim();

   void setBranch(TBranch branch);

   /** (=kTRUE if leaf has a range, kFALSE otherwise) */
   boolean getIsRange();

   /** (=kTRUE if unsigned, kFALSE otherwise) */
   boolean getIsUnsigned();

   /** Pointer to Leaf count if variable length */
   TLeaf getLeafCount();

   /** Number of fixed length elements */
   int getLen();

   /** Number of bytes for this data type */
   int getLenType();

   int[] getMaxIndex();

   /** Offset in ClonesArray object (if one) */
   int getOffset();

   Object getWrappedValue(long index) throws java.io.IOException;

   void generateReadCode(InstructionList il, InstructionFactory factory, ConstantPoolGen cp, String className);
}
