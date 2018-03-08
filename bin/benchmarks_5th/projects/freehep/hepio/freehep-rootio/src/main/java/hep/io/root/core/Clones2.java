package hep.io.root.core;

import hep.io.root.RootClass;
import hep.io.root.interfaces.TClonesArray;
import java.util.AbstractList;
import java.util.ArrayList;


/**
 * A Clones2 is used to represent a split TClonesArray.
 * In Root 3.00 there was a special TBranchClones class to represent this,
 * in later versions of Root this is just handled like everything else,
 * by TBranchElement.
 */
public abstract class Clones2 extends AbstractList implements TClonesArray
{
   protected ArrayList cloneCache;
   protected long hollowIndex;
   protected int size;

   public void setData(int size, long hollowIndex)
   {
      this.size = size;
      this.hollowIndex = hollowIndex;
      clearCache();
      if (cloneCache == null)
         cloneCache = new ArrayList(size);
      else
         cloneCache.ensureCapacity(size);
      for (int i = cloneCache.size(); i < size; i++)
      {
         Clone2 result = createClone();
         result.setData(i, this);
         cloneCache.add(result);
      }
   }

   public Object getElementAt(int index)
   {
      return get(index);
   }

   public int getLast()
   {
      return size;
   }

   public int getLowerBound()
   {
      return 0;
   }

   public RootClass getRootClass()
   {
      Class klass = getClass();
      RootClassLoader loader = (RootClassLoader) klass.getClassLoader();
      return loader.getRootClass(klass);
   }

   public int getUpperBound()
   {
      return size - 1;
   }

   public Object get(int index)
   {
      return cloneCache.get(index);
   }

   public int size()
   {
      return size;
   }

   protected abstract void clearCache();

   protected abstract Clone2 createClone();
}
