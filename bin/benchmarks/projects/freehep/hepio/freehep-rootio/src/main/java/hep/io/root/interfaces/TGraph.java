/*
 * Interface created by InterfaceBuilder. Do not modify.
 *
 * Created on Mon Apr 21 13:58:49 PDT 2003
 */
package hep.io.root.interfaces;

public interface TGraph extends hep.io.root.RootObject, hep.io.root.interfaces.TNamed, hep.io.root.interfaces.TAttLine, hep.io.root.interfaces.TAttFill, hep.io.root.interfaces.TAttMarker
{
   public final static int rootIOVersion = 3;

   /** Pointer to list of functions (fits and user) */
   hep.io.root.interfaces.TList getFunctions();

   /** Pointer to histogram used for drawing axis */
   hep.io.root.interfaces.TH1F getHistogram();

   /** Maximum value for plotting along y */
   double getMaximum();

   /** Minimum value for plotting along y */
   double getMinimum();

   /** Number of points */
   int getNpoints();

   /** [fNpoints] array of X points */
   double[] getX();

   /** [fNpoints] array of Y points */
   double[] getY();
}
