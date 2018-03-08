package hep.io.root.core;

import hep.io.root.RootClass;
import hep.io.root.interfaces.TBranch;
import hep.io.root.interfaces.TBranchClones;
import hep.io.root.interfaces.TLeaf;
import hep.io.root.interfaces.TLeafObject;
import org.apache.bcel.Constants;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.InstructionFactory;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.Type;

/**
 *
 * @author tonyj
 * @version $Id: CloneLeaf.java 13617 2009-04-09 22:48:46Z tonyj $
 */
public class CloneLeaf implements TLeafObject, Constants
{
   private TBranchClones branch;

   public int getArrayDim()
   {
      return 0;
   }

   public int getBits()
   {
      return 0;
   }

   public void setBranch(TBranch branch)
   {
      this.branch = (TBranchClones) branch;
   }

   public boolean getIsRange()
   {
      return false;
   }

   public boolean getIsUnsigned()
   {
      return false;
   }

   public TLeaf getLeafCount()
   {
      return null;
   }

   public int getLen()
   {
      return 1;
   }

   public int getLenType()
   {
      return -1;
   }

   public int[] getMaxIndex()
   {
      return null;
   }

   /** object identifier  */
   public String getName()
   {
      return branch.getName();
   }

   public int getOffset()
   {
      return branch.getOffset();
   }

   /**
    * Get the class of this object
    * @return The RootClass for this object
    */
   public RootClass getRootClass()
   {
      return null;
   }

   public String getTitle()
   {
      return branch.getTitle();
   }

   public int getUniqueID()
   {
      return 0;
   }

   public Object getValue(long index) throws java.io.IOException
   {
      return new HollowArray(index, branch);
   }

   public boolean getVirtual()
   {
      return false;
   }

   public Object getWrappedValue(long index) throws java.io.IOException
   {
      return getValue(index);
   }

   public void generateReadCode(InstructionList il, InstructionFactory factory, ConstantPoolGen cp, String className)
   {
      String leafClassName = getClass().getName();
      il.append(factory.createInvoke(leafClassName, "getValue", Type.OBJECT, new Type[]
            {
               Type.LONG
            }, INVOKEVIRTUAL));
   }
}
