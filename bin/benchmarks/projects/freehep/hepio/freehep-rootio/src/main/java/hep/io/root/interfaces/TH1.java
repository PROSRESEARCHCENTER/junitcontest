/*
 * Interface created by InterfaceBuilder. Do not modify.
 *
 * Created on Mon Apr 21 13:58:49 PDT 2003
 */
package hep.io.root.interfaces;

public interface TH1 extends hep.io.root.RootObject, hep.io.root.interfaces.TNamed, hep.io.root.interfaces.TAttLine, hep.io.root.interfaces.TAttFill, hep.io.root.interfaces.TAttMarker
{
   public final static int rootIOVersion = 3;

   /** (1000*offset) for bar charts or legos */
   short getBarOffset();

   /** (1000*width) for bar charts or legos */
   short getBarWidth();

   /** Array to display contour levels */
   double[] getContour();

   /** Number of entries */
   double getEntries();

   /** ->Pointer to list of functions (fits and user) */
   hep.io.root.interfaces.TList getFunctions();

   /** Maximum value for plotting */
   double getMaximum();

   /** Minimum value for plotting */
   double getMinimum();

   /** number of bins(1D), cells (2D) +U/Overflows */
   int getNcells();

   /** Normalization factor */
   double getNormFactor();

   /** histogram options */
   String getOption();

   /** Array of sum of squares of weights */
   double[] getSumw2();

   /** Total Sum of weights */
   double getTsumw();

   /** Total Sum of squares of weights */
   double getTsumw2();

   /** Total Sum of weight*X */
   double getTsumwx();

   /** Total Sum of weight*X*X */
   double getTsumwx2();

   /** X axis descriptor */
   hep.io.root.interfaces.TAxis getXaxis();

   /** Y axis descriptor */
   hep.io.root.interfaces.TAxis getYaxis();

   /** Z axis descriptor */
   hep.io.root.interfaces.TAxis getZaxis();
}
