package hep.io.root.core;

import hep.io.root.RootClass;
import hep.io.root.RootClassNotFound;
import hep.io.root.RootMember;
import hep.io.root.RootObject;

import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.InstructionFactory;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.LDC;
import org.apache.bcel.generic.ObjectType;
import org.apache.bcel.generic.Type;


/**
 * A RootClass based on a StreamerInfo object
 * @author Tony Johnson (tonyj@slac.stanford.edu)
 * @version $Id: GenericRootClass.java 13618 2009-04-10 00:02:54Z tonyj $
 */
public class GenericRootClass extends BasicRootClass
{
   private final static Type rootObjectType = new ObjectType("hep.io.root.RootObject");
   private static NameMangler nameMangler = NameMangler.instance();
   private Class javaClass;
   private Class proxyClass;
   private RootClassFactory factory;
   private StreamerInfo streamerInfo;
   private String name;

   public GenericRootClass(String name, StreamerInfo info)
   {
      streamerInfo = info;
      this.name = name;
   }

   public int getCheckSum()
   {
      return streamerInfo.getCheckSum();
   }

   public String getClassName()
   {
      return name;
   }

   public Class getJavaClass()
   {
      if (javaClass == null)
         javaClass = getClass(nameMangler.mangleInterfaceName(name));
      return javaClass;
   }

   public Type getJavaType()
   {
      return new ObjectType(nameMangler.mangleInterfaceName(name));
   }

   public RootMember[] getMembers()
   {
      return streamerInfo.getMembers();
   }

   public RootClass[] getSuperClasses()
   {
      return streamerInfo.getSuperClasses();
   }

   public int getVersion()
   {
      return streamerInfo.getVersion();
   }

   public boolean instanceOf(RootClass superClass)
   {
      if (superClass == this)
         return true;

      RootClass[] classes = getSuperClasses();
      for (int i = 0; i < classes.length; i++)
      {
         if (classes[i].instanceOf(superClass))
            return true;
      }
      return false;
   }

   public AbstractRootObject newInstance()
   {
      try
      {
         return (AbstractRootObject) getProxyClass().newInstance();
      }
      catch (Exception t)
      {
         throw new RuntimeException("Error while instantiating " + name,t);
      }
   }

   StreamerInfo getStreamerInfo()
   {
      return streamerInfo;
   }

   void generateReadCode(InstructionList il, InstructionFactory factory, ConstantPoolGen cp)
   {
      il.append(new LDC(cp.addString(name)));
      il.append(factory.createInvoke("hep.io.root.core.RootInput", "readObject", rootObjectType, new Type[]
            {
               Type.STRING
            }, INVOKEINTERFACE));
      il.append(factory.createCast(rootObjectType, getJavaType()));
   }

   void generateReadPointerCode(InstructionList il, InstructionFactory factory, ConstantPoolGen cp)
   {
      il.append(factory.createInvoke("hep.io.root.core.RootInput", "readObjectRef", rootObjectType, Type.NO_ARGS, INVOKEINTERFACE));
      il.append(factory.createCast(rootObjectType, getJavaType()));
   }

   void resolve(RootClassFactory factory) throws RootClassNotFound
   {
      this.factory = factory;
      streamerInfo.resolve(factory);
   }

   /**
    * This should return the Java class for the interface corresponding
    * to this class
    */
   private Class getClass(String fullName)
   {
      try
      {
         RootClassLoader loader = factory.getLoader();
         Class result = loader.loadClass(fullName);

         // Some sanity checks
         if (!RootObject.class.isAssignableFrom(result))
            throw new RuntimeException("Invalid class " + name);
         return result;
      }
      catch (ClassNotFoundException x)
      {
         throw new RuntimeException("Error loading " + name,x);
      }
   }

   private Class getProxyClass()
   {
      if (proxyClass == null)
         proxyClass = getClass(nameMangler.mangleFullClassName("hep.io.root.proxy",name));
      return proxyClass;
   }
}
