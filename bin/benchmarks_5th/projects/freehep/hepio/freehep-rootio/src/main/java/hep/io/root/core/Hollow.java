package hep.io.root.core;

import hep.io.root.RootClass;
import hep.io.root.RootObject;


public abstract class Hollow implements RootObject, hep.io.root.interfaces.TObject
{
   protected long index;
   protected int subIndex; // implemented as array, only int supported?

   public void setHollowIndex(long index)
   {
      this.index = index;
   }

   public RootClass getRootClass()
   {
      Class klass = getClass();
      RootClassLoader loader = (RootClassLoader) klass.getClassLoader();
      return loader.getRootClass(klass);
   }

   public void setSubIndex(long index)
   {
      this.subIndex = (int) index;
   }
}
