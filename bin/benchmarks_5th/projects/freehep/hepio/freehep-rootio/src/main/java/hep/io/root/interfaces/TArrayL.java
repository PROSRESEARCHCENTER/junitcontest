/*
 * Interface created by InterfaceBuilder. Do not modify.
 *
 * Created on Sat May 05 18:29:49 PDT 2001
 */
package hep.io.root.interfaces;

public interface TArrayL extends hep.io.root.RootObject, TArray
{
   public final static int rootIOVersion = 1;
   public final static int rootCheckSum = 1191246382;

   /** [fN] Array of fN longs */
   long[] getArray();
}
