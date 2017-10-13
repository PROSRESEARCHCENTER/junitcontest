package hep.io.root.core;

import hep.io.root.RootClass;
import hep.io.root.interfaces.TBranchClones;
import hep.io.root.interfaces.TClonesArray;
import hep.io.root.interfaces.TLeafI;
import java.util.AbstractList;

/**
 * A HollowArray is used to represent a split TClonesArray.
 * In Root 3.00 there was a special TBranchClones class to represent this,
 * in later versions of Root this is just handled like everything else,
 * by TBranchElement.
 */
public class HollowArray extends AbstractList implements TClonesArray
{
   private Class objectClass;
   private long hollowIndex;
   private int size;

   //private TBranchClones branch;
   HollowArray(long hollowIndex, TBranchClones branch) throws java.io.IOException
   {
      this.size = ((TLeafI) branch.getBranchCount().getLeaves().get(0)).getValue(hollowIndex);
      this.hollowIndex = hollowIndex;

      //this.branch = branch;
      this.objectClass = branch.getObjectClass();
   }

   public int getBits()
   {
      return 0;
   }

   public Object getElementAt(int index)
   {
      return get(index);
   }

   public int getLast()
   {
      return size;
   }

   public int getLowerBound()
   {
      return 0;
   }

   public String getName()
   {
      return null;
   }

   /**
    * Get the class of this object
    * @return The RootClass for this object
    */
   public RootClass getRootClass()
   {
      return null;
   }

   public int getSize()
   {
      return size;
   }

   public String getTitle()
   {
      return null;
   }

   public int getUniqueID()
   {
      return 0;
   }

   public int getUpperBound()
   {
      return size - 1;
   }

   public Object get(int cloneIndex)
   {
      try
      {
         // We have two indeces to deal with, the hollowIndex and the cloneIndex
         Hollow ho = (Hollow) objectClass.newInstance();
         ho.setHollowIndex(hollowIndex);
         ho.setSubIndex(cloneIndex);
         return ho;
      }
      catch (Throwable x)
      {
         throw new RuntimeException("Error instantiating TBranchClones element ",x);
      }
   }

   public int size()
   {
      return size;
   }
}
