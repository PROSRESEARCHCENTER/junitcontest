package hep.io.root.util;

import hep.io.root.RootClass;
import hep.io.root.RootClassNotFound;
import hep.io.root.RootObject;
import hep.io.root.interfaces.TBranch;
import hep.io.root.interfaces.TBranchClones;
import hep.io.root.interfaces.TDirectory;
import hep.io.root.interfaces.TKey;
import hep.io.root.interfaces.TLeaf;
import hep.io.root.interfaces.TNamed;
import hep.io.root.interfaces.TTree;
import java.io.IOException;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;


/**
 * An adaptor that converts a root TDirectory into
 * a TreeModel, allowing any directory structure to
 * be dislayed by a Swing JTree.
 * <p>
 * This model will also drill down into TTree's, showing the branch and
 * leaf structure inside.
 *
 * @author Tony Johnson (tonyj@slac.stanford.edu)
 * @version $Id: RootDirectoryTreeModel.java 13617 2009-04-09 22:48:46Z tonyj $
 */

/*
 * Implementation notes:
 *
 * Nodes in the tree may be:
 *    TKey's corresponding to entries in Directories
 *       (a fake TKey is created for the top level directory)
 *    TBranch a branch of a tree
 *    TBranchClones
 *    BranchEntry -- representing an occurence of a Leaf
 */
public class RootDirectoryTreeModel implements TreeModel
{
   private TKey top;

   /**
    * Create the tree model
    * @param topDir The TDirectory that is to appear as the "root" of the tree
    */
   public RootDirectoryTreeModel(TDirectory topDir)
   {
      /**
       * In our tree all the nodes correspond to TKeys, so we need to
       * create a "fake" TKey to represent the top of the tree
       */
      this.top = new FakeTKey(topDir);
   }

   public Object getChild(Object parent, int index)
   {
      try
      {
         if (parent instanceof TKey)
         {
            TKey node = (TKey) parent;
            Object object = node.getObject();
            if (object instanceof TDirectory)
            {
               TDirectory dir = (TDirectory) object;
               return dir.getKey(index);
            }
            else // if (object instanceof TTree)
            {
               TTree tree = (TTree) object;
               return tree.getBranch(index);
            }
         }
         else // if (node instanceof TBranch)
         {
            TBranch branch = (TBranch) parent;
            int n = branch.getBranches().size();
            if (index < n)
            {
               // These are the sub-branches
               Object p = branch.getBranches().get(index);
               if (p instanceof TBranch)
                  return p;
               if (p instanceof TBranchClones)
                  return p;
               return new BranchEntry("????" + p.getClass(), index);
            }
             // Otherwise this is the data for the branch
            else
            {
               TLeaf leaf = (TLeaf) branch.getLeaves().get(0); // TODO: more leaves
               return new BranchEntry(leaf, index, n);
            }
         }
      }
      catch (RootClassNotFound x)
      {
         handleException(x);
         return new BranchEntry(x, index);
      }
      catch (IOException x)
      {
         handleException(x);
         return new BranchEntry(x, index);
      }
   }

   public int getChildCount(Object parent)
   {
      try
      {
         if (parent instanceof TKey)
         {
            TKey node = (TKey) parent;
            Object object = node.getObject();
            if (object instanceof TDirectory)
            {
               TDirectory dir = (TDirectory) object;
               return dir.nKeys();
            }
            else // if (object instanceof TTree)
            {
               TTree tree = (TTree) object;
               return tree.getNBranches();
            }
         }
         else // if (object instanceof TBranch)
         {
            TBranch branch = (TBranch) parent;
            long n = branch.getBranches().size();
            n += branch.getEntries();
            return (int) n;
         }
      }
      catch (RootClassNotFound x)
      {
         handleException(x);
         return -1;
      }
      catch (IOException x)
      {
         handleException(x);
         return 0;
      }
   }

   public int getIndexOfChild(Object parent, Object child)
   {
      try
      {
         if (parent instanceof TKey)
         {
            TKey node = (TKey) parent;
            Object object = node.getObject();
            if (object instanceof TDirectory)
            {
               TDirectory dir = (TDirectory) object;
               int n = dir.nKeys();
               for (int i = 0; i < n; i++)
                  if (dir.getKey(i).equals(child))
                     return i;
               throw new IOException("Could not find " + child + " in " + dir);
            }
            else // if (object instanceof TTree)
            {
               TTree tree = (TTree) object;
               for (int i = 0; i < tree.getNBranches(); i++)
                  if (tree.getBranch(i).equals(child))
                     return i;
               throw new IOException("Could not find " + child + " in " + tree);
            }
         }
         else
         {
            if (child instanceof BranchEntry)
            {
               BranchEntry entry = (BranchEntry) child;
               return entry.getIndex();
            }
            else
            {
               TBranch branch = (TBranch) parent;
               int index = branch.getBranches().indexOf(child);
               if (index < 0)
                  System.out.println("Illegal index " + index + " " + child + " " + branch);
               return index;
            }
         }
      }
      catch (RootClassNotFound x)
      {
         handleException(x);
         return -1;
      }
      catch (IOException x)
      {
         handleException(x);
         return -1;
      }
   }

   public boolean isLeaf(Object parent)
   {
      try
      {
         if (parent instanceof TKey)
         {
            TKey key = (TKey) parent;
            RootClass rc = key.getObjectClass();
            Class jc = rc.getJavaClass();

            // We need something better here!
            if (TDirectory.class.isAssignableFrom(jc))
               return false;
            if (TTree.class.isAssignableFrom(jc))
               return false;
            return true;
         }
         else if (parent instanceof TBranch)
            return false;
         else
            return true;
      }
      catch (IOException x)
      {
         handleException(x);
         return true;
      }
      catch (Throwable t)
      {
         t.printStackTrace();
         return true;
      }
   }

   public Object getRoot()
   {
      return top;
   }

   public void addTreeModelListener(javax.swing.event.TreeModelListener p1) {}

   //The remaining methods are not implemented since the root tree
   //is assumed for now to be immutable.
   public void removeTreeModelListener(javax.swing.event.TreeModelListener p1) {}

   public void valueForPathChanged(TreePath p1, Object p2) {}

   /**
    * Handle IOExceptions when reading the root file.
    * Can be overriden in order to handle IOExceptions
    * encountered when reading objects from the root file.
    * The default implementation throws a RuntimeException
    */
   protected void handleException(IOException x)
   {
      throw new RuntimeException("IOException reading root file",x);
   }

   protected void handleException(RootClassNotFound x)
   {
      throw new RuntimeException("Root class not found reading root file: " + x.getClassName(),x);
   }
}


class FakeTKey implements TKey
{
   private TNamed target;

   FakeTKey(TNamed target)
   {
      this.target = target;

      //System.out.println("Created fakeTKey for "+target.getName());
   }

   public int getBits()
   {
      return 0;
   }

   public short getCycle()
   {
      return 1;
   }

   public String getName()
   {
      return target.getName();
   }

   /**
    * Returns the proxy for the object
    */
   public RootObject getObject() throws IOException
   {
      return target;
   }

   public RootClass getObjectClass()
   {
      return target.getRootClass();
   }

   public RootClass getRootClass()
   {
      return target.getRootClass();
   }

   public String getTitle()
   {
      return target.getTitle();
   }

   public int getUniqueID()
   {
      return 0;
   }

   public boolean equals(Object other)
   {
      if (other instanceof FakeTKey)
      {
         return this.target.equals(((FakeTKey) other).target);
      }
      return false;
   }

   public int hashCode()
   {
      return target.hashCode();
   }

   public RootObject readObject()
   {
      return target;
   }
}


/**
 * Represents an element in an array.
 * We cannot use the value of the array element itself
 * as the tree node since the same object may be referenced
 * by multiple elements of the array, and since the value
 * may be null.
 */
class BranchEntry
{
   private TLeaf leaf;
   private int index;
   private int offset;

   BranchEntry(Object entry, int index)
   {
      this(null, index, 0);
   }

   BranchEntry(TLeaf leaf, int index, int offset)
   {
      this.leaf = leaf;
      this.index = index;
      this.offset = offset;
   }

   public String toString()
   {
      String name;
      try
      {
         Object o = getValue();
         if (o == null)
            name = "null";
         else if (o.getClass().isArray())
            name = "Array"; // Too bad we dont know this without calling getValue
         else if (o instanceof java.util.List)
            name = "List";
         else
            name = o.toString();
      }
      catch (IOException x)
      {
         name = "????";
      }
      return "[" + (index - offset) + "] " + name;
   }

   int getIndex()
   {
      return index;
   }

   Object getValue() throws IOException
   {
      return leaf.getWrappedValue(index - offset);
   }
}
