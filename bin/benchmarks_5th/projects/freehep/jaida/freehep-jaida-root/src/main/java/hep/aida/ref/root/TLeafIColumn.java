package hep.aida.ref.root;

import hep.io.root.interfaces.TLeafI;

import java.io.IOException;

import org.freehep.util.Value;


/**
 *
 * @author tonyj
 */
class TLeafIColumn extends TLeafColumn
{
   private TLeafI leaf;

   TLeafIColumn(TLeafI leaf)
   {
      this.leaf = leaf;
   }

   public void defaultValue(Value value)
   {
      value.set((int) 0);
   }

   public String name()
   {
      return leaf.getName();
   }

   public Class type()
   {
      return Integer.TYPE;
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
         int[] array = (int[]) leaf.getWrappedValue(row);
         value.set(array[dim]);
      }
      catch (IOException x)
      {
         throw new RuntimeException("IOException accessing tuple", x);
      }
   }  
}
