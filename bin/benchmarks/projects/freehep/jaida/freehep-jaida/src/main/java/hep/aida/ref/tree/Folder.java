package hep.aida.ref.tree;
import hep.aida.IManagedObject;
import hep.aida.ref.AidaUtils;

import java.util.Iterator;
import java.util.Map;

/**
 * A Folder is a managed object representing a directory in a tree.
 * It is used only within the Tree implementation, and is never exposed
 * to the user.
 * @author  The AIDA team @ SLAC.
 * @version $Id: Folder.java 8584 2006-08-10 23:06:37Z duns $
 */
public class Folder extends hep.aida.ref.ManagedObject
{
   Folder(String name)
   {
      super(name);
      hasBeenFilled = false;
   }
   void add(IManagedObject object)
   {
      map.put(AidaUtils.modifyName(object.name()),object);
   }
   void remove(IManagedObject child)
   {
      map.remove(AidaUtils.modifyName(child.name()));
   }
   IManagedObject getChild(String name)
   {
      return (IManagedObject) map.get(name);
   }
   int getChildCount()
   {
      return map.size();
   }
   int getIndexOfChild(IManagedObject child)
   {
      Iterator iter = map.values().iterator();
      for (int i=0; iter.hasNext(); i++)
      {
         if (iter.next().equals(child)) return i;
      }
      return -1;
   }
   IManagedObject getChild(int index)
   {
      Iterator iter = map.values().iterator();
      for (int i=0; i<index; i++) iter.next();
      return (IManagedObject) iter.next();
   }
   public String type()
   {
      return "dir";
   }
   boolean isBeingWatched()
   {
      return isBeingWatched;
   }
   void setIsBeingWatched(boolean value)
   {
      isBeingWatched = value;
   }

   // Is set to True by Tree when the folder is filled by the Store
   void setFilled(boolean value)
   {
       hasBeenFilled = value;
   }
   boolean isFilled()
   {
       return hasBeenFilled;
   }
   private Map map = new java.util.TreeMap();
   private boolean isBeingWatched = false;
   private boolean hasBeenFilled;
}
