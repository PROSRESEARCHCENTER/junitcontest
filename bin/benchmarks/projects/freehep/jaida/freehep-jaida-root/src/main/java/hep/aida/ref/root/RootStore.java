package hep.aida.ref.root;

import hep.aida.IManagedObject;
import hep.aida.dev.IDevTree;
import hep.aida.dev.IOnDemandStore;
import hep.io.root.RootClassNotFound;
import hep.io.root.RootFileReader;
import hep.io.root.daemon.RootStreamHandler;
import hep.io.root.interfaces.TDirectory;
import hep.io.root.interfaces.TKey;

import java.io.IOException;
import java.net.URL;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;


/**
 * An implementation of IStore for reading Root files.
 * @author tonyj
 * @version $Id: RootStore.java 12903 2007-06-30 20:51:28Z tonyj $
 */
class RootStore implements IOnDemandStore
{
   private RootFileReader file;
   private boolean recursive;
   private boolean showAllCycles;
   private boolean useProxies;
   
   public boolean isReadOnly()
   {
      return true;
   }
   
   public void close() throws IOException
   {
      if (file != null)
      {
         file.close();
         file = null;
      }
   } 
   
   public void commit(IDevTree tree, Map options) throws IOException
   {
      throw new UnsupportedOperationException();
   }
   
   public void read(IDevTree tree, Map options, boolean readOnly, boolean createNew) throws IOException
   {
      String storeName = tree.storeName();
      
      if (storeName.indexOf("://")>0)
      {
         URL url = null;
         try
         { 
            // First try the default stream handler. This will work inside JAS3 or in
            // any environment where a suitable StreamHandlerFactory has been installed
            url = new URL(null,storeName);
         }
         catch (MalformedURLException x)
         {
            // At the moment uses RootStreamHandler. XRootdStreamHandler would be
            // better, but currently the tests assume root: means old rootd.
            url = new URL(null,storeName,new RootStreamHandler());
         }
         file = new RootFileReader(url,options);
      }
      else
      {
         file = new RootFileReader(storeName);
      }
      useProxies = toBoolean(options,"useProxies");
      recursive = toBoolean(options,"recursive");
      showAllCycles = toBoolean(options,"showallcycles") || toBoolean(options,"showAllCycles");
      
      if (recursive) addEntries(tree, file, "/");
   }
   private boolean toBoolean(Map options, String key)
   {
      Object value = options.get(key);
      if (value == null) return false;
      return Boolean.valueOf(value.toString()).booleanValue();
   }
   public void read(IDevTree tree, String path) throws IllegalArgumentException, IOException
   {
      if (file == null) throw new IOException("Root file is not open.");
      TDirectory folder = file;
      Object obj = null;
      StringTokenizer st = new StringTokenizer(path, "/");
      if (st.countTokens()>0)
      {
         while (st.hasMoreTokens())
         {
            String token = st.nextToken();
            try
            {
               obj = folder.getKey(token).getObject();
            } 
            catch (RootClassNotFound x)
            {
               throw new IOException("Root class not found: "+x.getClassName());
            }
            if (!(obj instanceof TDirectory))
               throw new IllegalArgumentException("Path "+path+" does not point to a directory in the ROOT file "+tree.storeName());
            folder = (TDirectory) obj;
         }
      }
      addEntries(tree, folder, path);
   }
   private void addEntries(IDevTree tree, TDirectory dir, String path) throws IOException
   {
      if (showAllCycles)
      {
         int nKeys = dir.nKeys();
         for (int k = 0; k < nKeys; k++)
         {
            addEntry(tree,path,dir.getKey(k));
         }
      }
      else
      {
         // Find only the latest cycle for each key
         HashMap map = new HashMap();
         int nKeys = dir.nKeys();
         for (int k = 0; k < nKeys; k++)
         {
            TKey key = dir.getKey(k);
            String name = key.getName();
            TKey oldKey = (TKey) map.get(name);
            if (oldKey == null || key.getCycle() > oldKey.getCycle()) map.put(name,key);
         }
         for (Iterator i = map.values().iterator(); i.hasNext(); )
         {
            addEntry(tree,path, (TKey) i.next());
         }
      }
      tree.hasBeenFilled(path);
   }
   private void addEntry(IDevTree tree, String path, TKey key) throws IOException
   {
      try
      {
         if (TDirectory.class.isAssignableFrom(key.getObjectClass().getJavaClass()))
         {
            String newPath;
            if (path.endsWith("/"))
               newPath = path + key.getName();
            else
               newPath = path + "/" + key.getName();
            
            tree.mkdirs(newPath);
            
            if (recursive) addEntries(tree, (TDirectory) key.getObject(), newPath);
         }
         else
         {
            IManagedObject im = Converter.convert(key, getName(key), useProxies);
            if (im != null)
               tree.add(path,im);
         }
      }
      catch (RootClassNotFound x)
      {
         throw new IOException("Root class not found: "+x.getClassName());
      }
   }
   private String getName(TKey key)
   {
      return showAllCycles ?  key.getName() + ";" + key.getCycle() : key.getName();
   }
}
