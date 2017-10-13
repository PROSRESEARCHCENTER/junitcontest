package org.freehep.math.minuit;

/**
 * Determines the relative floating point arithmetic precision. The
 * setPrecision() method can be used to override Minuit's own determination,
 * when the user knows that the {FCN} function value is not calculated to
 * the nominal machine accuracy.
 * @version $Id: MnMachinePrecision.java 8584 2006-08-10 23:06:37Z duns $
 */
public class MnMachinePrecision
{
   MnMachinePrecision()
   {
      setPrecision(4.0E-7);
      
      double epstry = 0.5;
      double one = 1.0;
      for(int i = 0; i < 100; i++)
      {
         epstry *= 0.5;
         double epsp1 = one + epstry;
         double epsbak = epsp1 - one;
         if(epsbak < epstry)
         {
            setPrecision(8.*epstry);
            break;
         }
      }
   }
   /** eps returns the smallest possible number so that 1.+eps > 1. */
   double eps()
   {
      return theEpsMac;
   }
   
   /** eps2 returns 2*sqrt(eps) */
   double eps2()
   {
      return theEpsMa2;
   }
   
   /** override Minuit's own determination */
   public void setPrecision(double prec)
   {
      theEpsMac = prec;
      theEpsMa2 = 2.*Math.sqrt(theEpsMac);
   }
   
   private double theEpsMac;
   private double theEpsMa2;
}
