/*
 * Interface created by InterfaceBuilder. Do not modify.
 *
 * Created on Sat May 05 18:29:49 PDT 2001
 */
package hep.io.root.interfaces;

public interface TAttAxis extends hep.io.root.RootObject
{
   public final static int rootIOVersion = 4;
   public final static int rootCheckSum = 1395276684;

   /** color of the line axis */
   short getAxisColor();

   /** color of labels */
   short getLabelColor();

   /** font for labels */
   short getLabelFont();

   /** offset of labels */
   float getLabelOffset();

   /** size of labels */
   float getLabelSize();

   /** Number of divisions(10000*n3 + 100*n2 + n1) */
   int getNdivisions();

   /** length of tick marks */
   float getTickLength();

   /** color of axis title */
   short getTitleColor();

   /** font for axis title */
   short getTitleFont();

   /** offset of axis title */
   float getTitleOffset();

   /** size of axis title */
   float getTitleSize();
}
