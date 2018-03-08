package hep.io.root.util;

import hep.io.root.RootClass;
import hep.io.root.RootClassNotFound;
import hep.io.root.interfaces.TDirectory;
import hep.io.root.interfaces.TH1;
import hep.io.root.interfaces.TKey;
import java.io.IOException;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

/**
 * An adaptor that converts a root TDirectory into
 * a TreeModel, allowing any directory structure to
 * be dislayed by a Swing JTree.
 *
 * This version displays only histograms.
 *
 * @author: Tony Johnson (tonyj@slac.stanford.edu)
 * @version: $Id: RootHistogramTreeModel.java 13617 2009-04-09 22:48:46Z tonyj $
 */

class RootHistogramTreeModel implements TreeModel
{
   private TKey top;
   /**
    * Create the tree model
    * @topDir The TDirectory that is to appear as the "root" of the tree
    */
   RootHistogramTreeModel(TDirectory topDir)
   {
      /**
       * In our tree all the nodes correspond to TKeys, so we need to
       * create a "fake" TKey to represent the top of the tree
       */
      this.top = new FakeTKey(topDir);
   }
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
      throw new RuntimeException("RootClassNotFound reading root file",x);
   }
   public Object getChild(Object parent, int index)
   {
      try
      {
         
         TKey node = (TKey) parent;
         Object object = node.getObject();
         if (object instanceof TDirectory)
         {
            TDirectory dir = (TDirectory) object;
            int nKeys = dir.nKeys();
            for (int i=0; i<nKeys; i++)
            {
               TKey key = dir.getKey(i);
               RootClass keyClass = key.getObjectClass();
               Class javaClass = keyClass.getJavaClass();
               if (TDirectory.class.isAssignableFrom(javaClass) || TH1.class.isAssignableFrom(javaClass))
               {
                  if (index-- == 0) return key;
               }
            }
         }
         return null;
      }
      catch (IOException x)
      {
         handleException(x);
         return null;
      }
      catch (RootClassNotFound x)
      {
         handleException(x);
         return null;
      }
   }
   public int getChildCount(Object parent)
   {
      try
      {
         TKey node = (TKey) parent;
         TDirectory dir = (TDirectory) node.getObject();
         int nKeys = dir.nKeys();
         int nChildren = 0;
         for (int i=0; i<nKeys; i++)
         {
            TKey key = dir.getKey(i);
            RootClass keyClass = key.getObjectClass();
            Class javaClass = keyClass.getJavaClass();
            if      (TDirectory.class.isAssignableFrom(javaClass)) nChildren++;
            else if (TH1.class.isAssignableFrom(javaClass)) nChildren++;
         }
         return nChildren;
      }
      catch (IOException x)
      {
         handleException(x);
         return 0;
      }
      catch (RootClassNotFound x)
      {
         handleException(x);
         return 0;
      }
   }
   public int getIndexOfChild(Object parent, Object child)
   {
      try
      {
         TKey node = (TKey) parent;
         TDirectory dir = (TDirectory) node.getObject();
         int nKeys = dir.nKeys();
         int nChildren = 0;
         for (int i=0; i<nKeys; i++)
         {
            TKey key = dir.getKey(i);
            if (key == child) return nChildren;
            RootClass keyClass = key.getObjectClass();
            Class javaClass = keyClass.getJavaClass();
            if      (TDirectory.class.isAssignableFrom(javaClass)) nChildren++;
            else if (TH1.class.isAssignableFrom(javaClass)) nChildren++;
         }
         return -1;
      }
      catch (IOException x)
      {
         handleException(x);
         return -1;
      }
      catch (RootClassNotFound x)
      {
         handleException(x);
         return -1;
      }
   }
   public Object getRoot()
   {
      return top;
   }
   public boolean isLeaf(Object parent)
   {
      try
      {
         TKey key = (TKey) parent;
         RootClass rc = key.getObjectClass();
         Class javaClass = rc.getJavaClass();
         // We need something better here!
         if (TDirectory.class.isAssignableFrom(javaClass)) return false;
         return true;
      }
      catch (IOException x)
      {
         handleException(x);
         return true;
      }
      catch (RootClassNotFound x)
      {
         handleException(x);
         return true;
      }
   }
   //The remaining methods are not implemented since the root tree
   //is assumed for now to be immutable.
   public void removeTreeModelListener(javax.swing.event.TreeModelListener p1)
   {
   }
   public void valueForPathChanged(TreePath p1, Object p2)
   {
   }
   public void addTreeModelListener(javax.swing.event.TreeModelListener p1)
   {
   }
}
