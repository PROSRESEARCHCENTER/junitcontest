package hep.aida.ref.hbook;

import hep.aida.IManagedObject;
import hep.aida.dev.IDevTree;
import hep.aida.dev.IOnDemandStore;
import hep.io.hbook.CompositeHbookObject;
import hep.io.hbook.HbookFile;
import hep.io.hbook.HbookObject;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * An implementation of IStore for reading HBook files.
 * @author tonyj
 * @version $Id: HBookStore.java 8584 2006-08-10 23:06:37Z duns $
 */
class HBookStore implements IOnDemandStore
{
   private HbookFile file;
   private boolean recursive;
   
   /**
    * If "true", then Hbook 1D and 2D histogram ID (number) is used for 
    * AIDA histogram object name. Be careful when setting it to  "false": 
    * Hbook histogram ID only is unique, histogram name does not have to be
    */
   private boolean useIdForName = true;

   public boolean isReadOnly()
   {
      return true;
   }
   
   public void read(IDevTree tree, Map options, boolean readOnly, boolean createNew) throws IOException
   {
      int recordSize = 0;
      Object size = options.get("recordsize");
      if (size != null) recordSize = Integer.parseInt(size.toString());
//      String storeName = tree.storeName();
      file = new HbookFile(tree.storeName(),recordSize);
      Enumeration e = file.getTopLevelDirectory().getChildren();
      recursive = options.containsKey("recursive");
      if (options.containsKey("useidforname")) {
          String val = (String) options.get("useidforname");
          if (val != null && val.equalsIgnoreCase("false")) {
              useIdForName = false;
          }
      }
      Converter.setUseIdForName(useIdForName);
      addEntries(tree,e,"/");
   }

   public void read(IDevTree tree, String path) throws IllegalArgumentException, IOException {
       if (file == null) throw new IOException("HbookFile is not open.");
       HbookObject folder = (HbookObject) file.getTopLevelDirectory();
       StringTokenizer st = new StringTokenizer(path, "/");
       if (st.countTokens()>0) {
	   while (st.hasMoreTokens()) {
	       String token = st.nextToken();
	       folder = ((CompositeHbookObject) folder).getChild(token);
	       if (!(folder instanceof CompositeHbookObject))
		   throw new IllegalArgumentException("Path "+path+" does not point to a directory in the HBOOK file "+tree.storeName());
	   }
       }
      Enumeration e = ((CompositeHbookObject) folder).getChildren();
      Converter.setUseIdForName(useIdForName);
      addEntries(tree,e,path);
    }

    /**
     * Add elements of e to the Tree
     * First check if directory exists in the tree and create it
     * if needed.
     */
   private void addEntries(IDevTree tree,Enumeration e, String path)
   {
      while (e.hasMoreElements())
      {
         Object o = e.nextElement();
         IManagedObject im = Converter.convert(o);
         if (im != null) {
                tree.add(path,im);
         } else if (o instanceof CompositeHbookObject) {
            CompositeHbookObject c = (CompositeHbookObject) o;
            String newPath;
            if (path.endsWith("/")) newPath = path+c.getName();
            else newPath = path + "/" + c.getName();
	    
	    tree.mkdirs(newPath);
            if (recursive) addEntries(tree,c.getChildren(),newPath);
         }
      }
      tree.hasBeenFilled(path);
   }
   
   public void commit(IDevTree tree, Map options) throws IOException
   {
      throw new UnsupportedOperationException();
   }
   
   public void close() throws IOException
   {
      if (file != null)
      {
         file.close();
         file = null;
      }
   }
}
