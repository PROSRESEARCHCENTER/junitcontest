/*
 * Interface created by InterfaceBuilder. Do not modify.
 *
 * Created on Thu May 10 12:10:41 PDT 2001
 */
package hep.io.root.interfaces;

public interface TLeafObject extends hep.io.root.RootObject, TLeaf
{
   public final static int rootIOVersion = 4;

   Object getValue(long index) throws java.io.IOException;

   /** Support for Virtuality */
   boolean getVirtual();
}
