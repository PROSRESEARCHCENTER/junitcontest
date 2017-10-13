package hep.aida.ref.event;

/**
 *
 * @author tonyj
 * @version $Id: TreeEvent.java 8584 2006-08-10 23:06:37Z duns $
 */
public class TreeEvent extends java.util.EventObject
{
   private int id;
   private String[] path;
   private Class type;
   private int flags;
   private String[] linkPath = null;
   
   public static final int NODE_ADDED = 1;
   public static final int NODE_DELETED = 2;
   public static final int NODE_RENAMED = 3;
   public static final int NODE_MOVED = 7;
   public static final int CHANGE_DIRECTORY = 4;
   public static final int TREE_CLOSED = 5;
   public static final int LINK_ADDED = 6;
   public static final int NODE_AVAILABLE   = 11;
   public static final int NODE_UNAVAILABLE = 12;
   
   public static final int FOLDER_MASK = 1;
   
   public TreeEvent(Object source, int id, String[] path, Class type, int flags)
   {
      super(source);
      this.id = id;
      this.path = path;
      this.type = type;
      this.flags = flags;
   }

   public TreeEvent(Object source, int id, String[] path, Class type, String[] linkPath)
   {
       this( source, id, path, type, 0);
       this.linkPath = linkPath;
   }
   
   
   public int getFlags()
   {
      return flags;
   }
   public String[] getPath()
   {
      return path;
   }
   public Class getType()
   {
      return type;
   }
   public String getName()
   {
      return path[path.length-1];
   }
   public int getID()
   {
      return id;
   }
   
   public String[] getLinkPath() {
       return linkPath;
   }

   public String[] getOldPath() {
       return linkPath;
   }
   
   public String toString()
   {
      StringBuffer result = new StringBuffer("TreeEvent id=");
      result.append(id);
      result.append(" path=");
      for (int i=0; i<path.length; i++)
      {
         result.append("/");
         result.append(path[i]);
      }
      return result.toString();
   }
}
