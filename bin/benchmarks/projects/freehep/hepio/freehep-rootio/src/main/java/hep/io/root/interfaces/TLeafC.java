/*
 * Interface created by InterfaceBuilder. Do not modify.
 *
 * Created on Sat May 05 18:29:49 PDT 2001
 */
package hep.io.root.interfaces;

public interface TLeafC extends hep.io.root.RootObject, TLeaf
{
   public final static int rootIOVersion = 1;
   public final static int rootCheckSum = 202902899;

   /** Maximum value if leaf range is specified */
   int getMaximum();

   /** Minimum value if leaf range is specified */
   int getMinimum();

   String getValue(long index) throws java.io.IOException;
}
