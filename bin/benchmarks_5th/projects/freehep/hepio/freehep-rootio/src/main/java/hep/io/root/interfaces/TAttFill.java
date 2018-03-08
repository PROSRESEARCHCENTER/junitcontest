/*
 * Interface created by InterfaceBuilder. Do not modify.
 *
 * Created on Mon Jan 15 18:53:38 PST 2001
 */
package hep.io.root.interfaces;

public interface TAttFill extends hep.io.root.RootObject
{
   public final static int rootIOVersion = 1;
   public final static int rootCheckSum = 1204118360;

   /** fill area color */
   short getFillColor();

   /** fill area style */
   short getFillStyle();
}
