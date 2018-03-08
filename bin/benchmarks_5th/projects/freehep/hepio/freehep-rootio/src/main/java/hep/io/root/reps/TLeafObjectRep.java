package hep.io.root.reps;

import hep.io.root.*;
import hep.io.root.core.*;
import hep.io.root.interfaces.*;

import org.apache.bcel.Constants;
import org.apache.bcel.generic.*;

import java.io.*;


/**
 * @author Tony Johnson
 * @version $Id: TLeafObjectRep.java 8584 2006-08-10 23:06:37Z duns $
 */
public abstract class TLeafObjectRep extends AbstractRootObject implements TLeafObject, Constants
{
   private Class hollowClass;
   private Object lastValue;
   private RootInput rin;
   private TBranchObject branch;
   private long lastValueIndex;

   public void setBranch(TBranch branch)
   {
      this.branch = (TBranchObject) branch;
      lastValueIndex = -1;
   }

   public Object getValue(long index) throws IOException
   {
      try
      {
         if (index == lastValueIndex)
            return lastValue;
         lastValueIndex = index;

         boolean hollow = branch.getEntryNumber() == 0;
         if (!hollow)
         {
            RootInput in = branch.setPosition(this, index);
            String type = in.readString();
            in.readByte();
            return lastValue = in.readObject(type);
         }
         else
         {
            if (hollowClass == null)
            {
               HollowBuilder builder = new HollowBuilder(branch);
               String name = "hep.io.root.hollow." + branch.getClassName();
               RootClassFactory factory = rin.getFactory();
               GenericRootClass gc = (GenericRootClass) factory.create(branch.getClassName());
               hollowClass = factory.getLoader().loadSpecial(builder, name, gc);

               // Populate the leafs.
               builder.populateStatics(hollowClass, factory);
            }

            Hollow h = (Hollow) hollowClass.newInstance();
            h.setHollowIndex(index);
            return lastValue = h;
         }
      }
      catch (IOException x)
      {
         lastValueIndex = -1;
         throw x;
      }
      catch (RootClassNotFound x)
      {
         lastValueIndex = -1;
         throw new IOException("RootClassNotFound " + x.getClassName());
      }
      catch (Throwable x)
      {
         lastValueIndex = -1;
         IOException io = new IOException("Error instantiating hollow object");
         io.initCause(x);
         throw io;
      }
   }

   public Object getWrappedValue(long index) throws IOException
   {
      return getValue(index);
   }

   public void generateReadCode(InstructionList il, InstructionFactory factory, ConstantPoolGen cp, String className)
   {
      String leafClassName = getClass().getName();
      il.append(factory.createInvoke(leafClassName, "getValue", Type.OBJECT, new Type[]
            {
               Type.LONG
            }, INVOKEVIRTUAL));
   }

   public void read(RootInput in) throws IOException
   {
      super.read(in);
      rin = in.getTop();
   }
}
