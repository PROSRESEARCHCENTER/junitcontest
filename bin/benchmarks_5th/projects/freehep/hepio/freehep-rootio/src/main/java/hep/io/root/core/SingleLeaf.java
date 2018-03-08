package hep.io.root.core;

import hep.io.root.RootClass;
import hep.io.root.interfaces.TLeaf;
import hep.io.root.interfaces.TObjArray;
import java.util.AbstractList;

/**
 *
 * @author tonyj
 * @version $Id: SingleLeaf.java 13617 2009-04-09 22:48:46Z tonyj $
 */
public class SingleLeaf extends AbstractList implements TObjArray
{
   private TLeaf leaf;

   public SingleLeaf(TLeaf leaf)
   {
      this.leaf = leaf;
   }

   public int getBits()
   {
      return 0;
   }

   public Object getElementAt(int index)
   {
      return leaf;
   }

   public int getLast()
   {
      return 1;
   }

   public int getLowerBound()
   {
      return 0;
   }

   public String getName()
   {
      return null;
   }

   public RootClass getRootClass()
   {
      return null;
   }

   public int getSize()
   {
      return 1;
   }

   public String getTitle()
   {
      return null;
   }

   public int getUniqueID()
   {
      return 0;
   }

   public int getUpperBound()
   {
      return 1;
   }

   public Object get(int index)
   {
      return leaf;
   }

   public int size()
   {
      return 1;
   }
}
