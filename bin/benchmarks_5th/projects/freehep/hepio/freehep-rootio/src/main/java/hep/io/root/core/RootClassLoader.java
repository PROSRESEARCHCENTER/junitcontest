package hep.io.root.core;

import hep.io.root.RootClassNotFound;
import hep.io.root.RootFileReader;
import hep.io.root.test.JasminVisitor;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.bcel.classfile.JavaClass;

/**
 * A classloader which can dynamically create classes based on the streamer info
 * objects in a root file.
 * @author tonyj
 */
public class RootClassLoader extends ClassLoader
{
   private final static boolean debugRoot = System.getProperty("debugRoot") != null;
   private Map<Class,GenericRootClass> classMap = new HashMap<Class,GenericRootClass>();
   private Map<String,ClassBuilder> stemMap = new HashMap<String,ClassBuilder>();
   private RootFileReader rfr;
   private final static Object bcelLock = new Object();
   private NameMangler nameMangler = NameMangler.instance();

   RootClassLoader(RootFileReader rfr)
   {
      super(RootClassLoader.class.getClassLoader());
      this.rfr = rfr;
      register(new InterfaceBuilder());
      register(new ProxyBuilder());
      register(new Proxy2Builder());
      register(new ClonesBuilder());
      register(new CloneBuilder());
      register(new Clone2Builder());
   }

   @Override
   public Class findClass(String name) throws ClassNotFoundException
   {
      try
      {
         if (debugRoot)
            System.out.println("RootClassLoader: loading " + name);

         String stem = nameMangler.getStemForJavaClass(name);
         String rootClassName = nameMangler.getClassForJavaClass(name);
         ClassBuilder builder = stemMap.get(stem);
         GenericRootClass gc = (GenericRootClass) rfr.getFactory().create(rootClassName);
         JavaClass jc;
         // BCEL is not thread safe, so class building must be synchronized
         synchronized (bcelLock)
         {
             jc = builder.build(gc);
         }

         if (debugRoot)
         {
            try
            {
               FileOutputStream out = new FileOutputStream(name + ".j");
               new JasminVisitor(jc, out).disassemble();
               out.close();
            }
            catch (IOException x) {}
         }

         byte[] data = jc.getBytes();
         Class result = defineClass(name, data, 0, data.length);
         classMap.put(result, gc);
         return result;
      }
      catch (RootClassNotFound x)
      {
         throw new ClassNotFoundException(name);
      }
   }

   public Class loadSpecial(ClassBuilder builder, String name, GenericRootClass rc)
   {
      if (debugRoot)
         System.out.println("RootClassLoader: loading special " + name);

      JavaClass jc = builder.build(rc);
      if (debugRoot)
      {
         try
         {
            FileOutputStream out = new FileOutputStream(name + ".j");
            new JasminVisitor(jc, out).disassemble();
            out.close();
         }
         catch (IOException x) {}
      }

      byte[] data = jc.getBytes();
      Class result = defineClass(name, data, 0, data.length);
      classMap.put(result, rc);
      return result;
   }

   GenericRootClass getRootClass(Class klass)
   {
      return classMap.get(klass);
   }

   private void register(ClassBuilder builder)
   {
      stemMap.put(builder.getStem(), builder);
   }
}
