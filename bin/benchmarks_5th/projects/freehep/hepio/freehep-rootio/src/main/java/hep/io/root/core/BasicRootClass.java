package hep.io.root.core;

import hep.io.root.RootClass;
import hep.io.root.RootClassNotFound;
import hep.io.root.RootMember;
import org.apache.bcel.generic.BranchHandle;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.GOTO;
import org.apache.bcel.generic.IFEQ;
import org.apache.bcel.generic.InstructionConstants;
import org.apache.bcel.generic.InstructionFactory;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.LCONST;
import org.apache.bcel.generic.PUSH;
import org.apache.bcel.generic.Type;

/**
 *
 * @author tonyj
 * @version $Id: BasicRootClass.java 13616 2009-04-09 21:25:45Z tonyj $
 */
public abstract class BasicRootClass implements RootClass, org.apache.bcel.Constants
{
   private static boolean debugRoot = System.getProperty("debugRoot") != null;
   
   /**
    * Get the Java type corresponding to this class.
    */
   public abstract Type getJavaType();
   
   public String toString()
   {
      return "RootClass: " + getClassName();
   }
   
   /**
    * The method used to convert the object to its method type.
    */
   protected String getConvertMethod()
   {
      return null;
   }
   
   /**
    * The type that will be used when this class is stored as a member, or as a return
    * type from a method.
    */
   protected Type getJavaTypeForMethod()
   {
      return getJavaType();
   }
   
   protected void generateStreamer(ConstantPoolGen cp, InstructionList il, InstructionFactory factory, String className, boolean hasHeader)
   {
      if (hasHeader)
      {
         if (debugRoot)
         {
            // Each class starts with a version field!
            il.append(InstructionConstants.ALOAD_1); // Copy the inputstream
            il.append(factory.createInvoke("hep.io.root.core.RootInput", "readUnsignedShort", Type.INT, Type.NO_ARGS, INVOKEINTERFACE));
            il.append(InstructionConstants.DUP);
            il.append(new PUSH(cp, 16384));
            il.append(InstructionConstants.IAND);
            
            BranchHandle bh = il.append(new IFEQ(null));
            il.append(new PUSH(cp, 16383));
            il.append(InstructionConstants.IAND);
            il.append(new PUSH(cp, 16));
            il.append(InstructionConstants.ISHL);
            il.append(InstructionConstants.ALOAD_1);
            il.append(factory.createInvoke("hep.io.root.core.RootInput", "readUnsignedShort", Type.INT, Type.NO_ARGS, INVOKEINTERFACE));
            il.append(InstructionConstants.IADD);
            il.append(InstructionConstants.I2L);
            il.append(InstructionConstants.ALOAD_1);
            il.append(factory.createInvoke("hep.io.root.core.RootInput", "getPosition", Type.LONG, Type.NO_ARGS, INVOKEINTERFACE));
            il.append(InstructionConstants.LADD);
            il.append(InstructionConstants.ALOAD_1);
            il.append(factory.createInvoke("hep.io.root.core.RootInput", "readUnsignedShort", Type.INT, Type.NO_ARGS, INVOKEINTERFACE));
            il.append(InstructionConstants.POP);
            
            BranchHandle bh2 = il.append(new GOTO(null));
            bh.setTarget(il.append(InstructionConstants.POP));
            il.append(new PUSH(cp, 0L));
            bh2.setTarget(il.append(InstructionConstants.NOP));
         }
         else
         {
            il.append(InstructionConstants.ALOAD_1); // Copy the inputstream
            il.append(factory.createInvoke("hep.io.root.core.RootInput", "readUnsignedShort", Type.INT, Type.NO_ARGS, INVOKEINTERFACE));
            il.append(new PUSH(cp, 16384));
            il.append(InstructionConstants.IAND);
            
            BranchHandle bh = il.append(new IFEQ(null));
            il.append(InstructionConstants.ALOAD_1);
            il.append(factory.createInvoke("hep.io.root.core.RootInput", "readInt", Type.INT, Type.NO_ARGS, INVOKEINTERFACE));
            il.append(InstructionConstants.POP);
            bh.setTarget(il.append(InstructionConstants.NOP));
            
            //il.append(new ALOAD(1));
            //il.append(new PUSH(cp,6));
            //il.append(factory.createInvoke("hep.io.root.core.RootInput","skipBytes",Type.INT,new Type[]{ Type.INT },INVOKEINTERFACE));
            //il.append(new POP());
         }
      }
      
      // Now create streamers for any super classes
      RootClass[] superClasses = getSuperClasses();
      for (int i = 0; i < superClasses.length; i++)
         ((BasicRootClass) superClasses[i]).generateStreamer(cp, il, factory, className, hasHeader);
      
      RootMember[] members = getMembers();
      for (int i = 0; i < members.length; i++)
      {
         if (((BasicMember) members[i]).getType() != null)
         {
            il.append(InstructionConstants.ALOAD_0);
            il.append(InstructionConstants.ALOAD_1);
            ((BasicMember) members[i]).generateReadCode(il, factory, cp, className);
            il.append(factory.createPutField(className, members[i].getName(),((BasicMember) members[i]).getJavaType()));
         }
         else
         {
            il.append(InstructionConstants.ALOAD_1);
            ((BasicMember) members[i]).generateReadCode(il, factory, cp, className);            
         }
      }
      if (debugRoot && hasHeader)
      {
         // Check the length
         il.append(InstructionConstants.DUP2);
         il.append(new LCONST(0L));
         il.append(InstructionConstants.LCMP);
         
         BranchHandle bh = il.append(new IFEQ(null));
         il.append(InstructionConstants.ALOAD_1);
         il.append(factory.createInvoke("hep.io.root.core.RootInput", "getPosition", Type.LONG, Type.NO_ARGS, INVOKEINTERFACE));
         il.append(InstructionConstants.LSUB);
         il.append(InstructionConstants.DUP2);
         il.append(new LCONST(0L));
         il.append(InstructionConstants.LCMP);
         
         BranchHandle bh2 = il.append(new IFEQ(null));
         il.append(factory.createNew("hep.io.root.core.WrongLengthException"));
         il.append(InstructionConstants.DUP_X2);
         il.append(InstructionConstants.DUP_X2);
         il.append(InstructionConstants.POP);
         il.append(new PUSH(cp, className));
         
         Type[] args = { Type.LONG, Type.STRING };
         il.append(factory.createInvoke("hep.io.root.core.WrongLengthException", "<init>", Type.VOID, args, INVOKESPECIAL));
         il.append(InstructionConstants.ATHROW);
         
         InstructionHandle ih = il.append(InstructionConstants.POP2);
         bh.setTarget(ih);
         bh2.setTarget(ih);
      }
   }
   
   abstract void generateReadCode(InstructionList il, InstructionFactory factory, ConstantPoolGen cp);
   
   abstract void resolve(RootClassFactory factory) throws RootClassNotFound;
}
