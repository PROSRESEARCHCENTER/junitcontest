package hep.io.root.classes;

import hep.io.root.core.GenericRootClass;
import hep.io.root.core.RootType;
import hep.io.root.core.StreamerInfo;

import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.InstructionConstants;
import org.apache.bcel.generic.InstructionFactory;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.NEWARRAY;
import org.apache.bcel.generic.Type;


/**
 * @author Tony Johnson (tonyj@slac.stanford.edu)
 * @version $Id: TArrayI.java 8584 2006-08-10 23:06:37Z duns $
 */
public class TArrayI extends GenericRootClass
{
   final static Type[] arrayArgType = new Type[] { RootType.INTARRAY };

   public TArrayI(String name, StreamerInfo info)
   {
      super(name, info);
   }

   protected String getConvertMethod()
   {
      return "getArray";
   }

   protected Type getJavaTypeForMethod()
   {
      return RootType.INTARRAY;
   }

   protected void generateStreamer(ConstantPoolGen cp, InstructionList il, InstructionFactory factory, String className, boolean hasHeader)
   {
      il.append(InstructionConstants.ALOAD_0);
      il.append(InstructionConstants.ALOAD_1);
      il.append(InstructionConstants.DUP2);
      il.append(factory.createInvoke("hep.io.root.core.RootInput", "readInt", Type.INT, Type.NO_ARGS, INVOKEINTERFACE));
      il.append(InstructionConstants.DUP_X1);
      il.append(factory.createPutField(className, "fN", Type.INT));
      il.append(new NEWARRAY(Type.INT));
      il.append(InstructionConstants.DUP_X1);
      il.append(factory.createInvoke("hep.io.root.core.RootInput", "readFixedArray", Type.VOID, arrayArgType, INVOKEINTERFACE));
      il.append(factory.createPutField(className, "fArray", RootType.INTARRAY));
   }
}
