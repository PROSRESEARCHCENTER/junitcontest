package org.freehep.math.minuit;

/**
 *
 * @version $Id: FunctionGradient.java 8584 2006-08-10 23:06:37Z duns $
 */
class FunctionGradient
{
   FunctionGradient(int n)
   {
      theGradient = new MnAlgebraicVector(n);
      theG2ndDerivative = new MnAlgebraicVector(n);
      theGStepSize = new MnAlgebraicVector(n);
   }
   
   FunctionGradient(MnAlgebraicVector grd)
   {
      theGradient = grd;
      theG2ndDerivative = new MnAlgebraicVector(grd.size());
      theGStepSize = new MnAlgebraicVector(grd.size());
      theValid = true;
      theAnalytical = true;
   }
   
   FunctionGradient(MnAlgebraicVector grd, MnAlgebraicVector g2, MnAlgebraicVector gstep)
   {
      theGradient = grd;
      theG2ndDerivative = g2;
      theGStepSize = gstep;
      theValid = true;
      theAnalytical = false;
   }
   
   MnAlgebraicVector grad()
   {
      return theGradient;
   }
   MnAlgebraicVector vec()
   {
      return theGradient;
   }
   boolean isValid()
   {
      return theValid;
   }
   
   boolean isAnalytical()
   {
      return theAnalytical;
   }
   MnAlgebraicVector g2()
   {
      return theG2ndDerivative;
   }
   MnAlgebraicVector gstep()
   {
      return theGStepSize;
   }
   
   private MnAlgebraicVector theGradient;
   private MnAlgebraicVector theG2ndDerivative;
   private MnAlgebraicVector theGStepSize;
   private boolean theValid;
   private boolean theAnalytical;
}
