package hep.aida.ref.root;

import hep.io.root.interfaces.TLeafB;

import java.io.IOException;

import org.freehep.util.Value;


/**
 *
 * @author tonyj
 */
class TLeafBColumn extends TLeafColumn
{
   private TLeafB leaf;

   TLeafBColumn(TLeafB leaf)
   {
      this.leaf = leaf;
   }

   public void defaultValue(Value value)
   {
   }

   public String name()
   {
      return leaf.getName();
   }

   public Class type()
   {
      return Byte.TYPE;
   }

   void getValue(int row, Value value)
   {
      try
      {
         value.set(leaf.getValue(row));
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
         byte[] array = (byte[]) leaf.getWrappedValue(row);
         value.set(array[dim]);
      }
      catch (IOException x)
      {
         throw new RuntimeException("IOException accessing tuple", x);
      }
   }   
}
