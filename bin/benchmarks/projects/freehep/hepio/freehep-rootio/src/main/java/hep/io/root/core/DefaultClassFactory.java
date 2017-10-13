package hep.io.root.core;

import hep.io.root.RootClass;
import hep.io.root.RootClassNotFound;
import hep.io.root.RootFileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Properties;

/*
 * The root class factory is used to create instances
 * of RootClass object.
 * @author Tony Johnson (tonyj@slac.stanford.edu)
 * @version $Id: DefaultClassFactory.java 13617 2009-04-09 22:48:46Z tonyj $
 */
public class DefaultClassFactory implements RootClassFactory
{
   private final static Class[] argc = { String.class, StreamerInfo.class };
   private Hashtable classMap = new Hashtable();
   private RootClassLoader loader;
   private RootFileReader rfr;
   private String[] packageList;

   /**
    * Creates a Root Class factory that looks in hep.io.root.interfaces for classes
    * and uses the default properties files for looking for StreamerInfo
    * and typedefs
    */
   public DefaultClassFactory(RootFileReader rfr)
   {
      loader = new RootClassLoader(rfr);
      try
      {
         Properties typedef = new Properties();
         InputStream in = getClass().getResourceAsStream("Typedef.properties");
         typedef.load(in);
         in.close();

         Properties streamerInfo = new Properties();
         in = getClass().getResourceAsStream("StreamerInfo.properties");
         streamerInfo.load(in);
         in.close();

         //String[] classPackages = { "hep.io.root.reps" };
         init(typedef, streamerInfo); //,classPackages,rfr);
      }
      catch (IOException x)
      {
         throw new RuntimeException("Unable to load default properties",x);
      }
   }

   public RootClassLoader getLoader()
   {
      return loader;
   }

   /**
    * Creates an instance of a RootClass object, by searching
    * for an appropriate class definition.
    */
   public BasicRootClass create(String name) throws RootClassNotFound
   {
      BasicRootClass result = (BasicRootClass) classMap.get(name);
      if (result != null)
         return result;

      // Try to see if there is a class in the package list
      throw new RootClassNotFound(name);
   }

   static RootClass findClass(String name, StreamerInfo info)
   {
      try
      {
         Class c = Class.forName("hep.io.root.classes." + name);
         java.lang.reflect.Constructor cc = c.getConstructor(argc);
         Object[] args = { name, info };
         return (RootClass) cc.newInstance(args);
      }
      catch (Throwable x)
      {
         return new GenericRootClass(name, info);
      }
   }

   private void init(Properties typedef, Properties streamerInfo) // , String[] packageList, RootFileReader rfr)
   {
      //this.packageList = packageList;
      try
      {
         // Convert all the typedefs to Java intrinsic classes
         Enumeration e = typedef.keys();
         while (e.hasMoreElements())
         {
            Object key = e.nextElement();
            Object value = typedef.get(key);
            Class intrinsic = Class.forName("hep.io.root.core." + value);
            if (!IntrinsicRootClass.class.isAssignableFrom(intrinsic))
               throw new RuntimeException("Typedef class is not an intrinsic: " + value);
            classMap.put(key, intrinsic.newInstance());
         }
      }
      catch (Exception x)
      {
         throw new RuntimeException("Error interpreting typedef table",x);
      }
      try
      {
         // Convert all the streamerInfo to Root generic classes
         Enumeration e = streamerInfo.keys();
         while (e.hasMoreElements())
         {
            String key = (String) e.nextElement();
            Object value = streamerInfo.get(key);
            StreamerInfo info = new StreamerInfoString((String) value);

            // Look to see if there is a specific implementation class
            classMap.put(key, findClass(key, info));
         }
      }
      catch (Exception x)
      {
         throw new RuntimeException("Error interpreting typedef table",x);
      }

      // Now make sure we can resolve all the references
      try
      {
         Enumeration e = classMap.elements();
         while (e.hasMoreElements())
         {
            BasicRootClass info = (BasicRootClass) e.nextElement();
            info.resolve(this);
         }
      }
      catch (RootClassNotFound x)
      {
         throw new RuntimeException("Could not resolve class " + x.getClassName(),x);
      }
   }
}
