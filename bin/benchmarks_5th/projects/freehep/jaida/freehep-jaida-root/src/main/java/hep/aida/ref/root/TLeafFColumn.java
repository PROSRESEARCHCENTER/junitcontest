package hep.aida.ref.root;

import hep.io.root.interfaces.TLeafF;

import java.io.IOException;

import org.freehep.util.Value;


/**
 *
 * @author tonyj
 */
class TLeafFColumn extends TLeafColumn
{
   private TLeafF leaf;

   TLeafFColumn(TLeafF leaf)
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
      return Float.TYPE;
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
         float[] array = (float[]) leaf.getWrappedValue(row);
         value.set(array[dim]);
      }
      catch (IOException x)
      {
         throw new RuntimeException("IOException accessing tuple", x);
      }
   } 
}
