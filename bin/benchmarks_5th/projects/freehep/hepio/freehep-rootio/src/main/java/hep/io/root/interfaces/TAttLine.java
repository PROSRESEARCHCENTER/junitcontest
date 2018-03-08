/*
 * Interface created by InterfaceBuilder. Do not modify.
 *
 * Created on Mon Jan 15 18:53:32 PST 2001
 */
package hep.io.root.interfaces;

public interface TAttLine extends hep.io.root.RootObject
{
   public final static int rootIOVersion = 1;
   public final static int rootCheckSum = 1369587346;

   /** line color */
   short getLineColor();

   /** line style */
   short getLineStyle();

   /** line width */
   short getLineWidth();
}
