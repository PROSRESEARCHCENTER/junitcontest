package hep.io.root.reps;

import hep.io.root.core.AbstractRootObject;
import hep.io.root.core.RootInput;
import hep.io.root.interfaces.TBranch;
import hep.io.root.interfaces.TLeafI;
import hep.io.root.interfaces.TLeafO;

import java.io.IOException;

import org.apache.bcel.Constants;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.InstructionFactory;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.Type;


/**
 * @author Tony Johnson
 * @version $Id: TLeafORep.java 10712 2007-04-25 21:42:28Z tonyj $
 */
public abstract class TLeafORep extends AbstractRootObject implements TLeafO, Constants
{
   private Object lastValue;
   private TBranch branch;
   private boolean lastBoolean;
   private long lastBooleanIndex;
   private long lastValueIndex;

   public void setBranch(TBranch branch)
   {
      this.branch = branch;
      lastValueIndex = -1;
      lastBooleanIndex = -1;
   }

   public boolean getValue(long index) throws IOException
   {
      try
      {
         if (index == lastBooleanIndex)
            return lastBoolean;

         RootInput in = branch.setPosition(this, lastBooleanIndex = index);
         return lastBoolean = in.readByte() != 0;
      }
      catch (IOException x)
      {
         lastBooleanIndex = -1;
         throw x;
      }
   }

   public Object getWrappedValue(long index) throws IOException
   {
      try
      {
         if (index == lastValueIndex)
            return lastValue;
         lastValueIndex = index;

         RootInput in = branch.setPosition(this, index);
         int arrayDim = getArrayDim();
         if (arrayDim == 0)
            return lastValue = Boolean.valueOf(in.readByte() != 0);
         else if (arrayDim == 1)
         {
            TLeafI count = (TLeafI) getLeafCount();
            int len = (count == null) ? getLen() : count.getValue(index);
            byte[] array = new byte[len];
            in.readFixedArray(array);
            return lastValue = array;
         }
         else
         {
            return lastValue = readMultiArray(in, Byte.TYPE, index);
         }
      }
      catch (IOException x)
      {
         lastValueIndex = -1;
         throw x;
      }
   }

   public void generateReadCode(InstructionList il, InstructionFactory factory, ConstantPoolGen cp, String className)
   {
      String leafClassName = getClass().getName();
      int arrayDim = getArrayDim();
      if (arrayDim == 0)
         il.append(factory.createInvoke(leafClassName, "getValue", Type.BYTE, new Type[]
               {
                  Type.LONG
               }, INVOKEVIRTUAL));
      else
         il.append(factory.createInvoke(leafClassName, "getWrappedValue", Type.OBJECT, new Type[]
               {
                  Type.LONG
               }, INVOKEVIRTUAL));
   }

   abstract Object[] readMultiArray(RootInput in, Class type, long index);
}