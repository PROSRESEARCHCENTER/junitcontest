package hep.aida.ref.root;

import hep.io.root.interfaces.TLeaf;

import java.io.IOException;

import org.freehep.util.Value;


/**
 *
 * @author tonyj
 */
class TLeafObjectColumn extends TLeafColumn
{
   private TLeaf leaf;

   TLeafObjectColumn(TLeaf leaf)
   {
      this.leaf = leaf;
   }

   public void defaultValue(Value value)
   {
      value.set((Object) null);
   }

   public String name()
   {
      return leaf.getName();
   }

   public Class type()
   {
      return Object.class;
   }

   void getValue(int row, Value value)
   {
      try
      {
         value.set(leaf.getWrappedValue(row));
      }
      catch (IOException x)
      {
         throw new RuntimeException("IOException accessing tuple", x);
      }
   }
   void getArrayValue(int row, int dim, Value value)
   {
      try
      {
         Object[] array = (Object[]) leaf.getWrappedValue(row);
         value.set(array[dim]);
      }
      catch (IOException x)
      {
         throw new RuntimeException("IOException accessing tuple", x);
      }
   } 
}
