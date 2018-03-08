package hep.aida.ref.root;

import hep.io.root.interfaces.TLeafD;

import java.io.IOException;

import org.freehep.util.Value;


/**
 *
 * @author tonyj
 */
class TLeafDColumn extends TLeafColumn
{
   private TLeafD leaf;

   TLeafDColumn(TLeafD leaf)
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
      return Double.TYPE;
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
         double[] array = (double[]) leaf.getWrappedValue(row);
         value.set(array[dim]);
      }
      catch (IOException x)
      {
         throw new RuntimeException("IOException accessing tuple", x);
      }
   }  
}
