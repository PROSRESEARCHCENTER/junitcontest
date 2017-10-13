package hep.aida.ref.root;

import hep.aida.ref.tuple.FTupleColumn;

import org.freehep.util.Value;


/**
 *
 * @author tonyj
 */
abstract class TLeafColumn implements FTupleColumn
{
   public void maxValue(Value value)
   {
      value.set(Double.NaN);
   }

   public void meanValue(Value value)
   {
      value.set(Double.NaN);
   }

   public void minValue(Value value)
   {
      value.set(Double.NaN);
   }

   public void rmsValue(Value value)
   {
      value.set(Double.NaN);
   }

   abstract void getValue(int row, Value value);
   abstract void getArrayValue(int row, int dim, Value value);
   
   public boolean hasDefaultValue() {
       return false;
   }
}
