package hep.io.root.core;

import hep.io.root.RootClassNotFound;
import hep.io.root.RootFileReader;
import hep.io.root.interfaces.TKey;
import hep.io.root.interfaces.TStreamerInfo;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * A class factory used inside a RootFileReader.
 * It first attempts to resolve classes using the StreamerInfo from the file
 * itself, and if that fails delelgates to a default class factory.
 * @author tonyj
 * @version $Id: FileClassFactory.java 13849 2011-07-01 23:49:23Z tonyj $
 */
public class FileClassFactory implements RootClassFactory
{
   private Map classMap = new HashMap();
   private RootClassFactory defaultClassFactory;
   private RootFileReader rfr;

   public FileClassFactory(TKey streamerInfo, RootClassFactory defaultClassFactory, RootFileReader rfr) throws RootClassNotFound, IOException
   {
      this.defaultClassFactory = defaultClassFactory;
      this.rfr = rfr;

      // Loop over all the streamerInfo objects
      List tList = (List) streamerInfo.getObject();

      for (Iterator i = tList.iterator(); i.hasNext();)
      {
         Object element = i.next();
         if (element instanceof TStreamerInfo)
         {
            TStreamerInfo si = (TStreamerInfo) element;
            String key = si.getName();
            StreamerInfo info = new StreamerInfoNew(si);
            classMap.put(key, DefaultClassFactory.findClass(key, info));
         }
      }

      // Now make sure we can resolve all the references
      Iterator i = classMap.values().iterator();
      while (i.hasNext())
      {
         GenericRootClass info = (GenericRootClass) i.next();
         try
         {
            info.resolve(this);
         }
         catch (RootClassNotFound x)
         {
            throw new RuntimeException("Could not resolve class "+x.getClassName()+" referenced from "+info.getClassName(),x);
         }
      }
   }

   public RootClassLoader getLoader()
   {
      return defaultClassFactory.getLoader();
   }

   public BasicRootClass create(String name) throws RootClassNotFound
   {
      //System.out.println("FileClassLoader loading "+name);
      //if (name.startsWith("enum")) name = "Int_t";
      BasicRootClass klass = (BasicRootClass) classMap.get(name);
      if (klass != null)
         return klass;
      return defaultClassFactory.create(name);
   }
}
