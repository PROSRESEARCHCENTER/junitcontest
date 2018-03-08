package hep.io.root.reps;

import hep.io.root.core.AbstractRootObject;
import hep.io.root.core.RootInput;
import hep.io.root.interfaces.TBranch;
import hep.io.root.interfaces.TLeafB;
import hep.io.root.interfaces.TLeafI;

import java.io.IOException;

import org.apache.bcel.Constants;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.InstructionFactory;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.Type;


/**
 * @author Tony Johnson
 * @version $Id: TLeafBRep.java 8584 2006-08-10 23:06:37Z duns $
 */
public abstract class TLeafBRep extends AbstractRootObject implements TLeafB, Constants
{
   private Object lastValue;
   private TBranch branch;
   private byte lastByte;
   private long lastByteIndex;
   private long lastValueIndex;
   
   public void setBranch(TBranch branch)
   {
      this.branch = branch;
      lastValueIndex = -1;
      lastByteIndex = -1;
   }
   
   public byte getValue(long index) throws IOException
   {
      try
      {
         if (index == lastByteIndex)
            return lastByte;

         RootInput in = branch.setPosition(this, lastByteIndex = index);
         return lastByte = in.readByte();
      }
      catch (IOException x)
      {
         lastByteIndex = -1;
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
            return lastValue = new Byte(in.readByte());
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
