package hep.io.root.reps;

import hep.io.root.*;
import hep.io.root.core.*;

import java.io.*;


/**
 * @author Tony Johnson (tonyj@slac.stanford.edu)
 * @version $Id: TListRep.java 8584 2006-08-10 23:06:37Z duns $
 */
public abstract class TListRep extends AbstractRootObject implements hep.io.root.interfaces.TList
{
   private String fName;
   private RootObject[] fArray;
   private int fSize;

   public int getBits()
   {
      return 0;
   }

   public boolean isEmpty()
   {
      return fSize == 0;
   }

   /** name of the collection  */
   public String getName()
   {
      return fName;
   }

   /** number of elements in collection  */
   public int getSize()
   {
      return fSize;
   }

   public int getUniqueID()
   {
      return 0;
   }

   public void add(int param, Object obj)
   {
      throw new UnsupportedOperationException();
   }

   public boolean add(Object obj)
   {
      throw new UnsupportedOperationException();
   }

   public boolean addAll(java.util.Collection collection)
   {
      throw new UnsupportedOperationException();
   }

   public boolean addAll(int param, java.util.Collection collection)
   {
      throw new UnsupportedOperationException();
   }

   public void clear()
   {
      throw new UnsupportedOperationException();
   }

   public boolean contains(Object obj)
   {
      for (int i = 0; i < fSize; i++)
         if ((obj == null) ? (fArray[i] == null) : obj.equals(fArray[i]))
            return true;
      return false;
   }

   public boolean containsAll(java.util.Collection collection)
   {
      java.util.Iterator i = collection.iterator();
      while (i.hasNext())
         if (!contains(i.next()))
            return false;
      return true;
   }

   public Object get(int param)
   {
      return fArray[param];
   }

   public int indexOf(Object obj)
   {
      for (int i = 0; i < fSize; i++)
         if ((obj == null) ? (fArray[i] == null) : obj.equals(fArray[i]))
            return i;
      return -1;
   }

   public java.util.Iterator iterator()
   {
      return new TListIterator(fArray, fSize, 0);
   }

   public int lastIndexOf(Object obj)
   {
      for (int i = fSize - 1; i >= 0; i--)
         if ((obj == null) ? (fArray[i] == null) : obj.equals(fArray[i]))
            return i;
      return -1;
   }

   public java.util.ListIterator listIterator(int start)
   {
      return new TListIterator(fArray, fSize, start);
   }

   public java.util.ListIterator listIterator()
   {
      return new TListIterator(fArray, fSize, 0);
   }

   public void readMembers(RootInput in) throws IOException
   {
      int v = in.readVersion(this);

      if (v > 2)
      {
         in.readObject("TObject");
      }
      if (v > 1)
      {
         fName = in.readObject("TString").toString();
      }
      fSize = in.readInt();
      fArray = new RootObject[fSize];
      for (int i = 0; i < fSize; i++)
      {
         RootObject obj = in.readObjectRef();
         if (v > 3)
         {
            int l = in.readByte();
            if (l > 0)
            {
               byte[] data = new byte[l];
               for (int j = 0; j < l; i++)
                  data[j] = in.readByte();

               String opt = new String(data);

               // Note we just toss away the option for now, should do something better I guess.
            }
            else
               fArray[i] = obj;
         }
         else
            fArray[i] = obj;
      }
      in.checkLength(this);
   }

   public Object remove(int param)
   {
      throw new UnsupportedOperationException();
   }

   public boolean remove(Object obj)
   {
      throw new UnsupportedOperationException();
   }

   public boolean removeAll(java.util.Collection collection)
   {
      throw new UnsupportedOperationException();
   }

   public boolean retainAll(java.util.Collection collection)
   {
      throw new UnsupportedOperationException();
   }

   public Object set(int param, Object obj)
   {
      throw new UnsupportedOperationException();
   }

   public int size()
   {
      return fSize;
   }

   public java.util.List subList(int param, int param1)
   {
      // I'm too lazy to implement this
      throw new UnsupportedOperationException();
   }

   public Object[] toArray(Object[] obj)
   {
      int l = obj.length;
      if (l < fSize)
         obj = (Object[]) java.lang.reflect.Array.newInstance(obj.getClass(), 0);
      System.arraycopy(fArray, 0, obj, 0, fSize);
      return obj;
   }

   public Object[] toArray()
   {
      Object[] obj = new Object[fSize];
      System.arraycopy(fArray, 0, obj, 0, fSize);
      return obj;
   }
}
