package hep.io.root.core;

import hep.io.root.RootClass;
import hep.io.root.RootMember;
import hep.io.root.interfaces.TBranch;
import hep.io.root.interfaces.TLeaf;
import hep.io.root.interfaces.TObjArray;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.bcel.Constants;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.generic.ArrayType;
import org.apache.bcel.generic.BranchHandle;
import org.apache.bcel.generic.ClassGen;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.FieldGen;
import org.apache.bcel.generic.IFNONNULL;
import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.InstructionConstants;
import org.apache.bcel.generic.InstructionFactory;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.MethodGen;
import org.apache.bcel.generic.ObjectType;
import org.apache.bcel.generic.Type;

/**
 * Creates a class which extends Clones2 and which represents a TClonesArray
 * read in split mode.
 * Clones2Builder is used for Root 3.01 and later.
 * @see hep.io.root.core.Clones2
 * @author tonyj
 * @version $Id: Clones2Builder.java 13618 2009-04-10 00:02:54Z tonyj $
 */
public class Clones2Builder implements ClassBuilder, Constants
{
   private static NameMangler nameMangler = NameMangler.instance();
   private Map lMap;
   private TBranch branch;
   private boolean optimize;

   public Clones2Builder(TBranch branch)
   {
      this.branch = branch;
   }

   public String getStem()
   {
      return "hep.io.root.clones2";
   }

   public JavaClass build(GenericRootClass klass)
   {
      optimize = (klass.getStreamerInfo().getBits() & (1 << 12)) == 0;
      //System.out.println("bits=" + Integer.toHexString(klass.getStreamerInfo().getBits()) + " optimize=" + optimize);

      String className = nameMangler.mangleFullClassName(getStem(),klass.getClassName());
      ClassGen cg = new ClassGen(className, "hep/io/root/core/Clones2", "<generated>", ACC_PUBLIC | ACC_SUPER, null);
      ConstantPoolGen cp = cg.getConstantPool();
      InstructionList il = new InstructionList();
      InstructionFactory factory = new InstructionFactory(cg);

      cg.addEmptyConstructor(ACC_PUBLIC);

      // Build the complete list of superclasses
      List sup = new ArrayList();
      RootClass[] superClasses = klass.getSuperClasses();
      iterativelyAdd(sup, superClasses);
      sup.add(klass);

      // Loop over all the leafs, and build a hashtable from name->leaf
      Map nameMap = new HashMap();
      TObjArray branches = branch.getBranches();
      for (Iterator j = branches.iterator(); j.hasNext();)
      {
         TBranch br = (TBranch) j.next();
         TObjArray leaves = br.getLeaves();
branch: for (Iterator i = leaves.iterator(); i.hasNext();)
         {
            Object leaf = i.next();
            String name = ((TLeaf) leaf).getName();
            int pos = name.indexOf('[');
            if (pos > 0)
               name = name.substring(0, pos);
            if (name.endsWith("_"))
               name = name.substring(0, name.length() - 1);
            pos = name.indexOf(".");
            if (pos > 0)
               name = name.substring(pos + 1);
            nameMap.put(name, leaf);
         }
      }
      lMap = new HashMap();

      // Loop over the members and try to find the matching leaf
      for (Iterator j = sup.iterator(); j.hasNext();)
      {
         RootClass k = (RootClass) j.next();
         RootMember[] members = k.getMembers();
         for (int i = 0; i < members.length; i++)
         {
            BasicMember member = (BasicMember) members[i];
            String name = member.getName();
            if (name.endsWith("_"))
               name = name.substring(0, name.length() - 1);

            Object leaf = nameMap.remove(name);
            if (leaf == null)
            {
               System.out.println("Warning: no leaf for " + member.getName());
            }
            else
            {
               lMap.put(member, leaf);
            }
         }
      }

      // Check for any left over leafs
      for (Iterator i = nameMap.keySet().iterator(); i.hasNext();)
      {
         System.out.println("Warning: unused leaf " + i.next());
      }

      // Generate the fields
      for (Iterator i = sup.iterator(); i.hasNext();)
         generateFields((RootClass) i.next(), cp, cg);

      // Generate the static fields
      for (Iterator i = sup.iterator(); i.hasNext();)
         generateStaticFields((RootClass) i.next(), cp, cg);

      // Generate the accessor methods
      for (Iterator i = sup.iterator(); i.hasNext();)
         generateMethods((RootClass) i.next(), cp, il, factory, cg, className);

      // Generate the createClone method
      {
         String cloneName = nameMangler.mangleFullClassName("hep.io.root.clone2",klass.getClassName());
         MethodGen mg = new MethodGen(ACC_PUBLIC, new ObjectType("hep.io.root.core.Clone2"), null, null, "createClone", className, il, cp);
         il.append((Instruction) factory.createNew(new ObjectType(cloneName)));
         il.append(InstructionConstants.DUP);
         il.append(factory.createInvoke(cloneName, "<init>", Type.VOID, Type.NO_ARGS, INVOKESPECIAL));
         il.append(InstructionConstants.ARETURN);
         mg.setMaxStack();
         mg.setMaxLocals();
         cg.addMethod(mg.getMethod());
         il.dispose();
      }

      // Generate the clearCache method
      {
         MethodGen mg = new MethodGen(ACC_PROTECTED, Type.VOID, null, null, "clearCache", className, il, cp);
         for (Iterator j = sup.iterator(); j.hasNext();)
         {
            RootClass k = (RootClass) j.next();
            RootMember[] members = k.getMembers();
            for (int i = 0; i < members.length; i++)
            {
               BasicMember member = (BasicMember) members[i];
               TLeaf leaf = (TLeaf) lMap.get(member);
               Type type = member.getJavaType();
               Type arrayType = new ArrayType(type, 1);

               il.append(InstructionConstants.ALOAD_0);
               il.append(InstructionConstants.ACONST_NULL);
               il.append(factory.createPutField(className, member.getName(), arrayType));
            }
         }
         il.append(InstructionConstants.RETURN);
         mg.setMaxStack();
         mg.setMaxLocals();
         cg.addMethod(mg.getMethod());
      }

      il.dispose();
      return cg.getJavaClass();
   }

   public void populateStatics(Class hollowClass, RootClassFactory factory)
   {
      try
      {
         // Loop over the leaves
         for (Iterator i = lMap.entrySet().iterator(); i.hasNext();)
         {
            Map.Entry entry = (Map.Entry) i.next();
            BasicMember member = (BasicMember) entry.getKey();
            Object l = entry.getValue();
            java.lang.reflect.Field field = hollowClass.getField(member.getName() + "Leaf");
            field.set(null, l);
         }
      }
      catch (Exception x)
      {
         throw new RuntimeException("Error populating statics ",x);
      }
   }

   private void generateFields(RootClass k, ConstantPoolGen cp, ClassGen cg)
   {
      if (k.getClassName().equals("TObject") && optimize)
         return;

      RootMember[] members = k.getMembers();
      for (int i = 0; i < members.length; i++)
      {
         BasicMember member = (BasicMember) members[i];

         Type type = ((BasicMember) members[i]).getJavaType();
         type = new ArrayType(type, 1);

         FieldGen fg = new FieldGen(ACC_PRIVATE, type, members[i].getName(), cp);
         cg.addField(fg.getField());
      }
   }

   private void generateMethods(RootClass k, ConstantPoolGen cp, InstructionList il, InstructionFactory factory, ClassGen cg, String className)
   {
      RootMember[] members = k.getMembers();
      for (int i = 0; i < members.length; i++)
      {
         BasicMember member = (BasicMember) members[i];
         TLeaf leaf = (TLeaf) lMap.get(member);
         if (leaf == null) continue;
         String leafClassName = leaf.getClass().getName();

         Type type = member.getJavaType();
         Type arrayType = new ArrayType(type, 1);
         MethodGen mg = new MethodGen(ACC_PUBLIC, type, new Type[] { Type.INT }, new String[]
            {
               "index"
            }, nameMangler.mangleMember(member.getName()), className, il, cp);
         il.append(InstructionConstants.ALOAD_0);
         il.append(factory.createGetField(className, member.getName(), arrayType));
         il.append(InstructionFactory.DUP);

         BranchHandle bh = il.append(new IFNONNULL(null));
         il.append(InstructionFactory.POP);
         il.append(factory.createGetStatic(className, member.getName() + "Leaf", new ObjectType(leafClassName)));
         il.append(InstructionConstants.ALOAD_0);
         il.append(factory.createGetField("hep.io.root.core.Clones2", "hollowIndex", Type.LONG));

         //    BasicRootClass varClass = (BasicRootClass) member.getType();
         il.append(factory.createInvoke(leafClassName, "setPosition", new ObjectType("hep.io.root.core.RootInput"), new Type[]
               {
                  Type.LONG
               }, INVOKEVIRTUAL));
         il.append(InstructionConstants.ALOAD_0);
         il.append(factory.createGetField("hep.io.root.core.Clones2", "size", Type.INT));
         il.append((Instruction) factory.createNewArray(type, (short) 1));
         il.append(InstructionConstants.DUP_X1);
         il.append(factory.createInvoke("hep.io.root.core.RootInput", "readFixedArray", Type.VOID, new Type[]
               {
                  arrayType
               }, INVOKEINTERFACE));

         il.append(InstructionConstants.DUP);
         il.append(InstructionConstants.ALOAD_0);
         il.append(InstructionConstants.SWAP);
         il.append(factory.createPutField(className, member.getName(), arrayType));

         bh.setTarget(il.append(InstructionConstants.ILOAD_1));
         il.append(InstructionFactory.createArrayLoad(type));
         il.append(InstructionFactory.createReturn(type));
         mg.setMaxStack();
         mg.setMaxLocals();
         cg.addMethod(mg.getMethod());
         il.dispose();
      }
   }

   private void generateStaticFields(RootClass k, ConstantPoolGen cp, ClassGen cg)
   {
      RootMember[] members = k.getMembers();
      for (int i = 0; i < members.length; i++)
      {
         BasicMember member = (BasicMember) members[i];
         TLeaf leaf = (TLeaf) lMap.get(member);
         if (leaf != null)
         {
            Type type = new ObjectType(leaf.getClass().getName());
            FieldGen fg = new FieldGen(ACC_PUBLIC | ACC_STATIC, type, member.getName() + "Leaf", cp);
            cg.addField(fg.getField());
         }
      }
   }

   private void iterativelyAdd(List list, RootClass[] superClasses)
   {
      for (int i = 0; i < superClasses.length; i++)
      {
         RootClass[] supsup = superClasses[i].getSuperClasses();
         iterativelyAdd(list, supsup);
         list.add(superClasses[i]);
      }
   }
}
