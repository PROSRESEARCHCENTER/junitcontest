package hep.io.root.core;

/**
 * @author tonyj
 * @version $Id: TListIterator.java 13617 2009-04-09 22:48:46Z tonyj $
 */
public class TListIterator implements java.util.ListIterator
{
   private Object[] fArray;
   private int fSize;
   private int index;

   public TListIterator(Object[] array, int size, int start)
   {
      index = start;
      fSize = size;
      fArray = array;
   }

   public void add(Object obj)
   {
      throw new UnsupportedOperationException();
   }

   public boolean hasNext()
   {
      return index < fSize;
   }

   public boolean hasPrevious()
   {
      return index > 0;
   }

   public Object next()
   {
      return fArray[index++];
   }

   public int nextIndex()
   {
      return index;
   }

   public Object previous()
   {
      return fArray[--index];
   }

   public int previousIndex()
   {
      return index - 1;
   }

   public void remove()
   {
      throw new UnsupportedOperationException();
   }

   public void set(Object obj)
   {
      throw new UnsupportedOperationException();
   }
}
