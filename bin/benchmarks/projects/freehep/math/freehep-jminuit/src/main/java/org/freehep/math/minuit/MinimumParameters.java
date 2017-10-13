package org.freehep.math.minuit;
/**
 *
 * @version $Id: MinimumParameters.java 8584 2006-08-10 23:06:37Z duns $
 */
class MinimumParameters
{
   MinimumParameters(int n)
   {
      theParameters =  new MnAlgebraicVector(n);
      theStepSize = new MnAlgebraicVector(n);
   }
   MinimumParameters(MnAlgebraicVector avec, double fval)
   {
      theParameters = avec;
      theStepSize = new MnAlgebraicVector(avec.size());
      theFVal = fval;
      theValid = true;
   }
   
   MinimumParameters(MnAlgebraicVector avec, MnAlgebraicVector dirin, double fval)
   {
      theParameters = avec;
      theStepSize = dirin;
      theFVal = fval;
      theValid = true;
      theHasStep = true;
   }
   
   MnAlgebraicVector vec()
   {
      return theParameters;
   }
   MnAlgebraicVector dirin()
   {
      return theStepSize;
   }
   double fval()
   {
      return theFVal;
   }
   boolean isValid()
   {
      return theValid;
   }
   boolean hasStepSize()
   {
      return theHasStep;
   }
   
   private MnAlgebraicVector theParameters;
   private MnAlgebraicVector theStepSize;
   private double theFVal;
   private boolean theValid;
   private boolean theHasStep;
}
