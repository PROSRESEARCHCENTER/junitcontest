package hep.aida.ref.root;

import hep.io.root.interfaces.TLeafL;
import java.io.IOException;
import org.freehep.util.Value;


/**
 *
 * @author tonyj
 */
class TLeafLColumn extends TLeafColumn
{
   private TLeafL leaf;

   TLeafLColumn(TLeafL leaf)
   {
      this.leaf = leaf;
   }

   public void defaultValue(Value value)
   {
      value.set((long) 0);
   }

   public String name()
   {
      return leaf.getName();
   }

   public Class type()
   {
      return Long.TYPE;
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
         long[] array = (long[]) leaf.getWrappedValue(row);
         value.set(array[dim]);
      }
      catch (IOException x)
      {
         throw new RuntimeException("IOException accessing tuple", x);
      }
   }  
}
