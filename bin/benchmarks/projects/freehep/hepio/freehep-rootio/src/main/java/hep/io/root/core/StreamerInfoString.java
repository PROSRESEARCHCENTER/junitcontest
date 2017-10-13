package hep.io.root.core;

import hep.io.root.RootClass;
import hep.io.root.RootClassNotFound;

import java.util.StringTokenizer;
import java.util.Vector;

import org.apache.bcel.generic.ArrayType;
import org.apache.bcel.generic.BasicType;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.InstructionConstants;
import org.apache.bcel.generic.InstructionFactory;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.NEWARRAY;
import org.apache.bcel.generic.Type;


/**
 * An implementation of StreamerInfo which takes info from a String
 * @author Tony Johnson (tonyj@slac.stanford.edu)
 * @version $Id: StreamerInfoString.java 8584 2006-08-10 23:06:37Z duns $
 */
public class StreamerInfoString extends StreamerInfo
{
   private String description;

   /**
    * Builds a StreamerInfo class from a string. The string
    * must be of the form defined by the root StreamerInfo
    * class. A ClassFactory must also be given, and is used to
    * interpret the types.
    */
   public StreamerInfoString(String description)
   {
      //we delay resolving the referenced classes until the
      //resolve method is called, so that we can wait until the
      //class factory is fully loaded.
      this.description = description;
   }

   int getBits()
   {
      return 0;
   }

   int getCheckSum()
   {
      return 0;
   }

   int getVersion()
   {
      return 0;
   }

   void resolve(RootClassFactory factory) throws RootClassNotFound
   {
      if (description != null)
      {
         Vector sv = new Vector();
         Vector mv = new Vector();
         StringTokenizer tokenizer = new StringTokenizer(description, ";");
         while (tokenizer.hasMoreTokens())
         {
            String token = tokenizer.nextToken();
            StringTokenizer t2 = new StringTokenizer(token, " ");
            if (t2.countTokens() == 1)
            {
               String className = t2.nextToken();

               // This must be a super class
               RootClass superClass = factory.create(className);
               sv.addElement(superClass);
            }
            else
            {
               String index = null;
               String type = t2.nextToken();
               String name = t2.nextToken();
               boolean pointer = type.endsWith("*");
               if (pointer)
                  type = type.substring(0, type.length() - 1);

               int pos = type.indexOf('[');
               if (pos > 0)
               {
                  int l = type.length();
                  index = type.substring(pos + 1, l - 1);
                  type = type.substring(0, pos);
               }

               BasicRootClass memberClass = factory.create(type);
               mv.addElement(new MemberString(memberClass, name, pointer, index));
            }
         }
         superClasses = new RootClass[sv.size()];
         sv.copyInto(superClasses);
         members = new MemberString[mv.size()];
         mv.copyInto(members);
         description = null; // Not needed anymore
      }
   }

   private class MemberString extends BasicMember
   {
      private BasicRootClass varClass;
      private String name;
      private boolean pointer;
      private int dim;
      private int index;
      private String varCounter;

      MemberString(BasicRootClass type, String name, boolean pointer, String index)
      {
         this.varClass = type;
         this.name = name;
         this.pointer = pointer;
         this.dim = (index == null) ? 0 : 1;
         try
         {
            this.index = Integer.parseInt(index); 
         }
         catch (NumberFormatException x)
         {
             varCounter = index;
         }
      }

      public int getArrayDim()
      {
         return dim;
      }
      
      public String getVarCounter()
      {
         return varCounter;
      }

      public String getComment()
      {
         return null;
      }

      public Type getJavaType()
      {
         Type t = ((BasicRootClass) varClass).getJavaTypeForMethod();
         if (dim > 0) t = new ArrayType(t, dim);
         return t;
      }

      public int getMaxIndex(int index)
      {
         return index == 0 ? this.index : 1;
      }

      public String getName()
      {
         return name;
      }

      public RootClass getType()
      {
         return varClass;
      }

      public void generateReadCode(InstructionList il, InstructionFactory factory, ConstantPoolGen cp, String className)
      {
         if (pointer)
         {
            ((GenericRootClass) varClass).generateReadPointerCode(il, factory, cp);
         }
         else if (dim == 0)
         {
            varClass.generateReadCode(il, factory, cp);
         }
         else if (varCounter == null)
         {
            ((IntrinsicRootClass) varClass).generateReadArrayCode(il, factory, cp, 1, new int[]{index});
         }
         else 
         {
            BasicMember varMember = getMember(varCounter);
            if (varMember == null) throw new RuntimeException("Cannot find variable counter "+varCounter);
            Type varMemberType = varMember.getJavaType();
            il.append(InstructionConstants.ALOAD_0);
            il.append(factory.createInvoke(className, nameMangler.mangleMember(varCounter), varMemberType, Type.NO_ARGS, INVOKESPECIAL));
            if (varMemberType != Type.INT) il.append(factory.createCast(varMemberType, Type.INT));

            BasicType type = (BasicType) varClass.getJavaType();
            il.append(new NEWARRAY(type));
            il.append(InstructionConstants.DUP_X1);

            Type[] arrayArgType = new Type[] { new ArrayType(type, 1) };
            il.append(factory.createInvoke("hep.io.root.core.RootInput", "readFixedArray", Type.VOID, arrayArgType, INVOKEINTERFACE));
         }
         if (varClass.getConvertMethod() != null)
         {
            il.append(factory.createInvoke("hep.io.root.interfaces." + varClass.getClassName(), varClass.getConvertMethod(), varClass.getJavaTypeForMethod(), Type.NO_ARGS, INVOKEINTERFACE));
         }
      }

      boolean isPointer()
      {
         return pointer;
      }
   }
}