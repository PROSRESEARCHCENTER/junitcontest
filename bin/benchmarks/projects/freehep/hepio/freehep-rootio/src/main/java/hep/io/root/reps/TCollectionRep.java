package hep.io.root.reps;

import hep.io.root.RootObject;
import hep.io.root.core.AbstractRootObject;
import hep.io.root.core.RootInput;
import hep.io.root.core.TListIterator;
import hep.io.root.interfaces.TObject;

import java.io.IOException;


/**
 * @author Tony Johnson (tonyj@slac.stanford.edu)
 * @version $Id: TCollectionRep.java 8584 2006-08-10 23:06:37Z duns $
 */
public abstract class TCollectionRep extends AbstractRootObject implements hep.io.root.interfaces.TCollection
{
   private String fName;
   private TObject fObject;
   private RootObject[] fArray;
   private int fNobjects;
   private int fSize;

   public Object getElementAt(int index)
   {
      return fArray[index];
   }

   public boolean isEmpty()
   {
      return fSize == 0;
   }

   public int getLast()
   {
      return fNobjects;
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
      fObject = (TObject) in.readObject("TObject");
      fName = in.readObject("TString").toString();
      fNobjects = in.readInt();
      fArray = new RootObject[fNobjects];
      fSize = 0;
      for (int i = 0; i < fNobjects; i++)
      {
         fArray[i] = in.readObjectRef();
         if (fArray[i] != null)
            fSize = i + 1;
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

   public Object set(int index, Object obj)
   {
      //We allow this so that new entries can be added to the list of Baskets in a Branch
      //throw new UnsupportedOperationException();
      Object old = fArray[index];
      fArray[index] = (RootObject) obj;
      return old;
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

   void setElementAt(int index, RootObject value)
   {
      fArray[index] = value;
   }

   RootObject elementAt(int index)
   {
      return fArray[index];
   }
}
