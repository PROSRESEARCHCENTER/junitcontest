package hep.io.root.core;

import hep.io.root.RootClass;
import hep.io.root.RootMember;

import org.apache.bcel.generic.ArrayType;
import org.apache.bcel.generic.BasicType;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.InstructionFactory;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.PUSH;
import org.apache.bcel.generic.Type;


/**
 * @author Tony Johnson (tonyj@slac.stanford.edu)
 * @version $Id: IntrinsicRootClass.java 8584 2006-08-10 23:06:37Z duns $
 */
abstract class IntrinsicRootClass extends BasicRootClass
{
   private static final Type[] objectArrayArgType =
   {
      new ArrayType(Type.OBJECT, 1)
   };
   private static final RootClass[] none = {  };
   private static final RootMember[] noMembers = {  };
   private BasicType type;
   private Class javaClass;
   private String name;
   private String readMethod;
   private Type[] arrayArgType;

   IntrinsicRootClass(String name, BasicType type, Class javaClass, String readMethod)
   {
      this.name = name;
      this.type = type;
      this.javaClass = javaClass;
      this.readMethod = readMethod;
      this.arrayArgType = new Type[] { new ArrayType(type, 1) };
   }

   public int getCheckSum()
   {
      return 0;
   }

   public String getClassName()
   {
      return name;
   }

   public Class getJavaClass()
   {
      return javaClass;
   }

   public Type getJavaType()
   {
      return type;
   }

   public RootMember[] getMembers()
   {
      return noMembers;
   }

   public RootClass[] getSuperClasses()
   {
      return none;
   }

   public int getVersion()
   {
      return 0;
   }

   public void generateReadArrayCode(InstructionList il, InstructionFactory factory, ConstantPoolGen cp, int dim, int[] maxIndex)
   {
      for (int i = 0; i < dim; i++)
         il.append(new PUSH(cp, maxIndex[i]));
      il.append((Instruction) factory.createNewArray(type, (short) dim));
      il.append(InstructionFactory.DUP_X1);
      if (dim == 1)
         il.append(factory.createInvoke("hep.io.root.core.RootInput", "readFixedArray", Type.VOID, arrayArgType, INVOKEINTERFACE));
      else
         il.append(factory.createInvoke("hep.io.root.core.RootInput", "readMultiArray", Type.VOID, objectArrayArgType, INVOKEINTERFACE));
   }

   public void generateReadCode(InstructionList il, InstructionFactory factory, ConstantPoolGen cp)
   {
      il.append(factory.createInvoke("hep.io.root.core.RootInput", readMethod, type, Type.NO_ARGS, INVOKEINTERFACE));
   }

   public boolean instanceOf(RootClass superClass)
   {
      return superClass == this;
   }

   public void resolve(RootClassFactory factory) {}
}


class IntegerClass extends IntrinsicRootClass
{
   IntegerClass()
   {
      super("Integer", Type.INT, Integer.TYPE, "readInt");
   }
}

class LongClass extends IntrinsicRootClass
{
   LongClass()
   {
      super("Integer", Type.LONG, Long.TYPE, "readLong");
   }
}

class DoubleClass extends IntrinsicRootClass
{
   DoubleClass()
   {
      super("Double", Type.DOUBLE, Double.TYPE, "readDouble");
   }
}


class FloatClass extends IntrinsicRootClass
{
   FloatClass()
   {
      super("Float", Type.FLOAT, Float.TYPE, "readFloat");
   }
}


class ShortClass extends IntrinsicRootClass
{
   ShortClass()
   {
      super("Short", Type.SHORT, Short.TYPE, "readShort");
   }
}


class ByteClass extends IntrinsicRootClass
{
   ByteClass()
   {
      super("Byte", Type.BYTE, Byte.TYPE, "readByte");
   }
}


class BooleanClass extends IntrinsicRootClass
{
   BooleanClass()
   {
      super("Boolean", Type.BOOLEAN, Boolean.TYPE, "readBoolean");
   }
}
