/*
 * Interface created by InterfaceBuilder. Do not modify.
 *
 * Created on Sat May 05 18:29:49 PDT 2001
 */
package hep.io.root.interfaces;

public interface TLeafF extends hep.io.root.RootObject, TLeaf
{
   public final static int rootIOVersion = 1;
   public final static int rootCheckSum = 1366318032;

   /** Maximum value if leaf range is specified */
   float getMaximum();

   /** Minimum value if leaf range is specified */
   float getMinimum();

   float getValue(long index) throws java.io.IOException;
}
