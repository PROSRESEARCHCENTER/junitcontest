package hep.io.root.core;

import hep.io.root.RootClass;
import hep.io.root.RootClassNotFound;
import hep.io.root.interfaces.TStreamerBasicPointer;
import hep.io.root.interfaces.TStreamerElement;
import hep.io.root.interfaces.TStreamerInfo;
import hep.io.root.interfaces.TStreamerLoop;

import java.util.Iterator;
import java.util.Vector;

import org.apache.bcel.generic.ALOAD;
import org.apache.bcel.generic.ANEWARRAY;
import org.apache.bcel.generic.ASTORE;
import org.apache.bcel.generic.ArrayType;
import org.apache.bcel.generic.BasicType;
import org.apache.bcel.generic.BranchHandle;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.GOTO;
import org.apache.bcel.generic.IFEQ;
import org.apache.bcel.generic.IF_ICMPGE;
import org.apache.bcel.generic.IINC;
import org.apache.bcel.generic.ILOAD;
import org.apache.bcel.generic.ISTORE;
import org.apache.bcel.generic.InstructionConstants;
import org.apache.bcel.generic.InstructionFactory;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.NEWARRAY;
import org.apache.bcel.generic.ObjectType;
import org.apache.bcel.generic.Type;


/**
 * An implementation of StreamerInfo which takes its data from a TStreamerInfo object.
 * @author tonyj
 * @version $Id: StreamerInfoNew.java 13849 2011-07-01 23:49:23Z tonyj $
 */
public class StreamerInfoNew extends StreamerInfo implements org.apache.bcel.Constants
{
   private final static int kBase = 0;
   private final static int kOffsetL = 20;
   private final static int kOffsetP = 40;
   private final static int kCounter = 6;
   private final static int kCharStar = 7;
   private final static int kChar = 1;
   private final static int kShort = 2;
   private final static int kInt = 3;
   private final static int kLong = 4;
   private final static int kFloat = 5;
   private final static int kDouble = 8;
   private final static int kDouble32 = 9;
   private final static int kUChar = 11;
   private final static int kUShort = 12;
   private final static int kUInt = 13;
   private final static int kULong = 14;
   private final static int kBits = 15;
   private final static int kLong64 = 16;
   private final static int kULong64 = 17;
   private final static int kBool    = 18;
   private final static int kObject = 61;
   private final static int kAny = 62;
   private final static int kObjectp = 63;
   private final static int kObjectP = 64;
   private final static int kTString = 65;
   private final static int kTObject = 66;
   private final static int kTNamed = 67;
   private final static int kMissing = 99999;
   private final static int kSkip = 100;
   private final static int kSkipL = 120;
   private final static int kSkipP = 140;
   private final static int kObjectV = 47;
   private final static int kObjectVV = 48;
   private final static int kConv = 200;
   private final static int kConvL = 220;
   private final static int kConvP = 240;
   private final static int kStreamer = 500;
   private final static int kStreamLoop = 501;
   private final static int kSTL = 300;
   private final static int kSTLstring = 365;
   private final static int kSTLvector = 1;
   private final static int kSTLlist = 2;
   private final static int kSTLdeque = 3;
   private final static int kSTLmap = 4;
   private final static int kSTLset = 5;
   private final static int kSTLmultimap = 6;
   private final static int kSTLmultiset = 7;
   private TStreamerInfo streamerInfo;
   private boolean resolved = false;
   private static boolean debug = System.getProperty("debugRoot") != null;

   /** Creates new StreamerInfoNew */
   public StreamerInfoNew(TStreamerInfo streamerInfo)
   {
      this.streamerInfo = streamerInfo;
   }

   public int getCheckSum()
   {
      return streamerInfo.getCheckSum();
   }

   public int getVersion()
   {
      return streamerInfo.getClassVersion();
   }

   public void resolve(RootClassFactory factory) throws RootClassNotFound
   {
      if (!resolved)
      {
         Vector sv = new Vector();
         Vector mv = new Vector();

         for (Iterator i = streamerInfo.getElements().iterator(); i.hasNext();)
         {
            TStreamerElement element = (TStreamerElement) i.next();
            if (element == null)
               continue;

            String typeName = element.getTypeName().toString();

            //System.out.println(element.getName()+" "+typeName);
            if (typeName.equals("BASE")) // base class
            {
               String className = element.getName().toString();
               sv.addElement(factory.create(className));
            }
            else // member
            {
               try
               {
                  mv.addElement(new MemberNew(element, factory));
               }
               catch (RootClassNotFound x)
               {
                  if (debug) System.err.println("Substituting dummy element for "+element.getName()+" of type "+x.getClassName());
                  mv.addElement(new DummyMember(element, factory));
               }

            }
         }
         superClasses = new RootClass[sv.size()];
         sv.copyInto(superClasses);
         members = new BasicMember[mv.size()];
         mv.copyInto(members);
         resolved = true;
      }
   }

   int getBits()
   {
      return streamerInfo.getBits();
   }

   private class MemberNew extends BasicMember
   {
      private BasicRootClass varClass;
      private String varComment;
      private String varCounter;
      private String varName;
      private int[] maxIndex;
      private int arrayDim;
      private int varType;

      MemberNew(TStreamerElement element, RootClassFactory factory) throws RootClassNotFound
      {
         varName = element.getName();
         varComment = element.getTitle();
         arrayDim = element.getArrayDim();

         String typeName = element.getTypeName();
         varType = element.getType();
         maxIndex = element.getMaxIndex();
         if (typeName.endsWith("*"))
         {
            // This seems dubious, but needed to read CMS moy.root??
            if (varType == kAny)
               varType = kObjectP;
            typeName = typeName.substring(0, typeName.length() - 1);
         }
         if ((varType >= kOffsetP) && (varType <= (kOffsetP + 20)))
            arrayDim++;
         
         // Note: typeName may just refer to a Enumeration, in which case it is 
         // not defined in the root file. This decission should be based on a combination
         // of varType and varClass.
         if (varType == 3) varClass = factory.create("Int_t");
         else varClass = factory.create(typeName);

         if (element instanceof TStreamerBasicPointer)
         {
            varCounter = ((TStreamerBasicPointer) element).getCountName();
         }
         else if (element instanceof TStreamerLoop)
         {
            varCounter = ((TStreamerLoop) element).getCountName();
            arrayDim++;
         }
      }

      public int getArrayDim()
      {
         return arrayDim;
      }

      public String getComment()
      {
         return varComment;
      }

      public Type getJavaType()
      {
         Type t = varClass.getJavaTypeForMethod();
         if (arrayDim > 0)
            t = new ArrayType(t, arrayDim);
         return t;
      }

      public int getMaxIndex(int index)
      {
         return maxIndex[index];
      }

      public String getName()
      {
         return varName;
      }

      public RootClass getType()
      {
         return varClass;
      }
      public String getVarCounter()
      {
         return varCounter;
      }

      public void generateReadCode(InstructionList il, InstructionFactory factory, ConstantPoolGen cp, String className)
      {
         try
         {
            switch (varType)
            {
            case kChar:
            case kShort:
            case kInt:
            case kLong:
            case kFloat:
            case kDouble:
            case kDouble32:
            case kCounter:
            case kUChar:
            case kUShort:
            case kUInt:
            case kULong:
            case kBits:
            case kObject:
            case kObjectp:
            case kTString:
            case kTObject:
            case kTNamed:
            case kAny:
            case kLong64:
	    case kULong64:
            case kBool:
	       varClass.generateReadCode(il, factory, cp);
               break;

            case kCharStar:
               il.append(InstructionConstants.DUP);
               il.append(factory.createInvoke("hep.io.root.core.RootInput", "readInt", Type.INT, Type.NO_ARGS, INVOKEINTERFACE));
               il.append(factory.createInvoke("hep.io.root.core.RootInput", "skipBytes", Type.INT, new Type[]
                     {
                        Type.INT
                     }, INVOKEINTERFACE));
               il.append(InstructionConstants.POP);
               il.append(InstructionConstants.ICONST_0);
               break;

            case kOffsetP + kChar:
            case kOffsetP + kShort:
            case kOffsetP + kInt:
            case kOffsetP + kLong:
            case kOffsetP + kFloat:
            case kOffsetP + kDouble:
            case kOffsetP + kDouble32:
            case kOffsetP + kUChar:
            case kOffsetP + kUShort:
            case kOffsetP + kUInt:
            case kOffsetP + kULong:
	    case kOffsetP + kLong64:
	    case kOffsetP + kULong64:
            case kOffsetP + kBool:
               il.append(InstructionConstants.DUP);
               il.append(factory.createInvoke("hep.io.root.core.RootInput", "readByte", Type.BYTE, Type.NO_ARGS, INVOKEINTERFACE));

               BranchHandle bh = il.append(new IFEQ(null));
               il.append(InstructionConstants.ALOAD_0);
               il.append(factory.createInvoke(className, nameMangler.mangleMember(varCounter), Type.INT, Type.NO_ARGS, INVOKESPECIAL));

               BasicType type = (BasicType) varClass.getJavaType();
               il.append(new NEWARRAY(type));
               il.append(InstructionConstants.DUP_X1);

               Type[] arrayArgType = new Type[] { new ArrayType(type, 1) };
               il.append(factory.createInvoke("hep.io.root.core.RootInput", "readFixedArray", Type.VOID, arrayArgType, INVOKEINTERFACE));

               BranchHandle bh2 = il.append(new GOTO(null));
               bh.setTarget(il.append(InstructionConstants.POP));
               il.append(InstructionConstants.ACONST_NULL);
               bh2.setTarget(il.append(InstructionConstants.NOP));
               break;

            case kOffsetL + kChar:
            case kOffsetL + kShort:
            case kOffsetL + kInt:
            case kOffsetL + kLong:
            case kOffsetL + kFloat:
            case kOffsetL + kDouble:
            case kOffsetL + kDouble32:
            case kOffsetL + kUChar:
            case kOffsetL + kUShort:
            case kOffsetL + kUInt:
            case kOffsetL + kULong:
	    case kOffsetL + kLong64:
	    case kOffsetL + kULong64:
	    case kOffsetL + kBool: 
               IntrinsicRootClass intrinsic = (IntrinsicRootClass) varClass;
               intrinsic.generateReadArrayCode(il, factory, cp, arrayDim, maxIndex);
               break;

            case kObjectP:
               ((GenericRootClass) varClass).generateReadPointerCode(il, factory, cp);
               break;

            case kStreamLoop:
               if (!varClass.getClassName().equals("TString"))
               {
                  //TODO: Fixme
                  System.err.println("Warning: Generating dummy read for "+varName);
                  il.append(factory.createInvoke("hep.io.root.core.RootInput", "dump", Type.VOID, Type.NO_ARGS, INVOKEINTERFACE));
                  il.append(InstructionConstants.ACONST_NULL);
               }
               else
               {
                  il.append(factory.createInvoke("hep.io.root.core.RootInput", "readVersion", Type.INT, Type.NO_ARGS, INVOKEINTERFACE));
                  il.append(InstructionConstants.POP);

                  BasicMember varMember = getMember(varCounter);
                  if (varMember == null) throw new RuntimeException("Cannot find variable counter "+varCounter);
                  Type varMemberType = varMember.getJavaType();
            
                  il.append(InstructionConstants.DUP); //ALOAD_0
                  il.append(factory.createInvoke(className, nameMangler.mangleMember(varCounter), varMemberType, Type.NO_ARGS, INVOKESPECIAL));
                  if (varMemberType != Type.INT) il.append(factory.createCast(varMemberType, Type.INT));
                  il.append(InstructionConstants.DUP);
                  il.append(InstructionConstants.ISTORE_2);

                  ObjectType xxxType = (ObjectType) varClass.getJavaTypeForMethod();
                  il.append(new ANEWARRAY(cp.addClass(xxxType)));
                  il.append(new ASTORE(3));

                  il.append(InstructionConstants.ICONST_0);
                  il.append(new ISTORE(4));
                  InstructionHandle ih = il.append(new ILOAD(4));
                  il.append(InstructionConstants.ILOAD_2);
                  bh = il.append(new IF_ICMPGE(null));

                  il.append(new ALOAD(3));
                  il.append(new ILOAD(4));
                  il.append(InstructionConstants.ALOAD_1);
                  varClass.generateReadCode(il, factory, cp);
                  if (varClass.getConvertMethod() != null && arrayDim == 1)
                  {
                     il.append(factory.createInvoke("hep.io.root.interfaces." + varClass.getClassName(), varClass.getConvertMethod(), varClass.getJavaTypeForMethod(), Type.NO_ARGS, INVOKEINTERFACE));
                  }
                  il.append(InstructionConstants.AASTORE);
                  il.append(new IINC(4,1));
                  il.append(new GOTO(ih));
                  bh.setTarget(il.append(InstructionConstants.NOP));
                  il.append(new ALOAD(3));
               }
               break;               

            default:
               throw new RuntimeException("Unable to decode varType " + varType + " in class "+className);
            }
            if (varClass.getConvertMethod() != null && arrayDim == 0)
            {
               il.append(factory.createInvoke("hep.io.root.interfaces." + varClass.getClassName(), varClass.getConvertMethod(), varClass.getJavaTypeForMethod(), Type.NO_ARGS, INVOKEINTERFACE));
            }
         }
         catch (RuntimeException x)
         {
            System.err.println("Error reading member " + varName + " of type " + varClass.getClassName());
            throw x;
         }
      }
   }
   private class DummyMember extends BasicMember
   {
      private String varComment;
      private String varName;
      private int[] maxIndex;
      private int arrayDim;
      private int varType;

      DummyMember(TStreamerElement element, RootClassFactory factory)
      {
         varName = element.getName();
         varComment = element.getTitle();
         arrayDim = element.getArrayDim();
         varType = element.getType();
         maxIndex = element.getMaxIndex();
      }

      public int getArrayDim()
      {
         return arrayDim;
      }

      public String getComment()
      {
         return varComment;
      }

      public Type getJavaType()
      {
         return Type.STRING;
      }

      public int getMaxIndex(int index)
      {
         return maxIndex[index];
      }

      public String getName()
      {
         return varName;
      }

      public RootClass getType()
      {
         return null; // Fixme:
      }

      public void generateReadCode(InstructionList il, InstructionFactory factory, ConstantPoolGen cp, String className)
      {
          System.err.println("Warnng: Generating dummy read for "+varName);
          il.append(factory.createInvoke("hep.io.root.core.RootInput", "skipObject", Type.VOID, Type.NO_ARGS, INVOKEINTERFACE));
      }
   }
}
