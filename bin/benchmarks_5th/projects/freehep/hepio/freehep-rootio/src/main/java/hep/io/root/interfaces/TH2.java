/*
 * Interface created by InterfaceBuilder. Do not modify.
 *
 * Created on Mon Apr 21 13:58:49 PDT 2003
 */
package hep.io.root.interfaces;

public interface TH2 extends hep.io.root.RootObject, hep.io.root.interfaces.TH1
{
   public final static int rootIOVersion = 3;

   /** Scale factor */
   double getScalefactor();

   /** Total Sum of weight*X*Y */
   double getTsumwxy();

   /** Total Sum of weight*Y */
   double getTsumwy();

   /** Total Sum of weight*Y*Y */
   double getTsumwy2();
}
