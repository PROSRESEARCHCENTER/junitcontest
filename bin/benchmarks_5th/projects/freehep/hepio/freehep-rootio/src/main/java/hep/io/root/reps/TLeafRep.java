package hep.io.root.reps;

import hep.io.root.core.AbstractRootObject;
import hep.io.root.core.RootInput;
import hep.io.root.interfaces.TLeaf;
import hep.io.root.interfaces.TLeafI;

import java.io.IOException;


/**
 * @author Tony Johnson
 * @version $Id: TLeafRep.java 8584 2006-08-10 23:06:37Z duns $
 */
public abstract class TLeafRep extends AbstractRootObject implements TLeaf
{
   private int[] maxIndex;
   private int arrayDim;

   public int getArrayDim()
   {
      return arrayDim;
   }

   public int[] getMaxIndex()
   {
      return maxIndex;
   }

   public void read(RootInput in) throws IOException
   {
      super.read(in);

      // There should be a better way of doing this!
      // If we could reliably associate a TStreamerInfo with each leaf it would
      // be much better.
      arrayDim = 0;
      maxIndex = new int[5];

      String title = getTitle() + getName(); // both may contain array decls
      int p1 = 0;
      int p2 = 0;
      for (; title != null; arrayDim++)
      {
         p1 = title.indexOf('[', p2);
         p2 = title.indexOf(']', p1);
         if ((p1 < 0) || (p2 <= p1))
            break;

         try
         {
            maxIndex[arrayDim] = Integer.parseInt(title.substring(p1 + 1, p2));
         }
         catch (NumberFormatException x)
         {
            maxIndex[arrayDim] = -1;
         }
      }
   }

   Object[] readMultiArray(RootInput in, Class type, long index) throws IOException
   {
      TLeafI count = (TLeafI) getLeafCount();
      int[] maxIndex = getMaxIndex();
      int[] dims = new int[arrayDim];
      for (int i = 0; i < arrayDim; i++)
      {
         dims[i] = (maxIndex[i] >= 0) ? maxIndex[i] : count.getValue(index);
      }

      Object[] array = (Object[]) java.lang.reflect.Array.newInstance(type, dims);
      in.readMultiArray(array);
      return array;
   }
}
