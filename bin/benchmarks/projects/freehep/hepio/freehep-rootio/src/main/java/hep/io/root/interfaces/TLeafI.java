/*
 * Interface created by InterfaceBuilder. Do not modify.
 *
 * Created on Sat May 05 18:29:49 PDT 2001
 */
package hep.io.root.interfaces;

public interface TLeafI extends hep.io.root.RootObject, TLeaf
{
   public final static int rootIOVersion = 1;

   /** Maximum value if leaf range is specified */
   int getMaximum();

   /** Minimum value if leaf range is specified */
   int getMinimum();

   int getValue(long index) throws java.io.IOException;
}
