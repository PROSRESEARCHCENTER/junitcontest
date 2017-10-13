/*
 * Interface created by InterfaceBuilder. Do not modify.
 *
 * Created on Sat May 05 18:29:49 PDT 2001
 */
package hep.io.root.interfaces;

public interface TCollection extends hep.io.root.RootObject, TObject
{
   public final static int rootIOVersion = 3;

   Object getElementAt(int index);

   int getLast();

   /** name of the collection */
   String getName();

   /** number of elements in collection */
   int getSize();
}
