package hep.io.root.core;

import hep.io.root.RootClass;
import hep.io.root.RootMember;
import org.apache.bcel.Constants;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.generic.ClassGen;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.FieldGen;
import org.apache.bcel.generic.MethodGen;
import org.apache.bcel.generic.Type;

/**
 * Creates a Java interface with accessor methods for each root member
 * @author tonyj
 */
public class InterfaceBuilder implements ClassBuilder, Constants
{
   private NameMangler nameMangler = NameMangler.instance();

   public String getStem()
   {
      return "hep.io.root.interfaces";
   }

   public JavaClass build(GenericRootClass klass)
   {
      RootClass[] superClasses = klass.getSuperClasses();
      String[] superClassNames = new String[superClasses.length + 1];
      superClassNames[0] = "hep.io.root.RootObject";
      for (int i = 0; i < superClasses.length; i++)
         superClassNames[i + 1] = nameMangler.mangleInterfaceName(superClasses[i].getClassName());

      ClassGen cg = new ClassGen(nameMangler.mangleInterfaceName(klass.getClassName()), "java/lang/Object", "<generated>", ACC_PUBLIC | ACC_INTERFACE | ACC_ABSTRACT, superClassNames);
      ConstantPoolGen cp = cg.getConstantPool();

      RootMember[] members = klass.getMembers();
      for (int i = 0; i < members.length; i++)
      {
         RootClass rootClass =  members[i].getType();
         if (rootClass instanceof BasicRootClass)
         {
            Type type = ((BasicRootClass) rootClass).getJavaType();
            MethodGen mg = new MethodGen(ACC_PUBLIC | ACC_ABSTRACT, type, null, null, nameMangler.mangleMember(members[i].getName()), null, null, cp);
            cg.addMethod(mg.getMethod());
         }
      }

      int version = klass.getVersion();
      if (version > 0)
      {
         FieldGen fg = new FieldGen(ACC_PUBLIC | ACC_STATIC | ACC_FINAL, Type.INT, "rootIOVersion", cp);
         fg.setInitValue(version);
         cg.addField(fg.getField());
      }

      int checkSum = klass.getCheckSum();
      if (checkSum > 0)
      {
         FieldGen fg = new FieldGen(ACC_PUBLIC | ACC_STATIC | ACC_FINAL, Type.INT, "rootCheckSum", cp);
         fg.setInitValue(checkSum);
         cg.addField(fg.getField());
      }
      return cg.getJavaClass();
   }
}
