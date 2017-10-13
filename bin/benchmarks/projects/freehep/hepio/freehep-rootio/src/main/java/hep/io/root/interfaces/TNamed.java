/*
 * Interface created by InterfaceBuilder. Do not modify.
 *
 * Created on Wed Jan 10 15:21:38 PST 2001
 */
package hep.io.root.interfaces;

public interface TNamed extends hep.io.root.RootObject, TObject
{
   public final static int rootIOVersion = 1;

   /** object identifier */
   String getName();

   /** object title */
   String getTitle();
}
