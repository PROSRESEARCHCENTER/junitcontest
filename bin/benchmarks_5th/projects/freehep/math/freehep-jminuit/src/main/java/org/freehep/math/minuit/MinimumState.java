package org.freehep.math.minuit;

/** MinimumState keeps the information (position, gradient, 2nd deriv, etc)
 * after one minimization step (usually in MinimumBuilder).
 * @version $Id: MinimumState.java 8584 2006-08-10 23:06:37Z duns $
 */
class MinimumState
{
   MinimumState(int n)
   {
      theParameters = new MinimumParameters(n);
      theError = new MinimumError(n);
      theGradient = new FunctionGradient(n);
   }
   MinimumState(MinimumParameters states, MinimumError err, FunctionGradient grad, double edm, int nfcn)
   {
      theParameters = states;
      theError = err;
      theGradient = grad;
      theEDM = edm;
      theNFcn = nfcn;
   }
   
   MinimumState(MinimumParameters states, double edm, int nfcn)
   {
      theParameters = states;
      theError = new MinimumError(states.vec().size());
      theGradient = new FunctionGradient(states.vec().size());
      theEDM = edm;
      theNFcn = nfcn;
   }
   
   MinimumParameters parameters()
   {
      return theParameters;
   }
   MnAlgebraicVector vec()
   {
      return theParameters.vec();
   }
   int size()
   {
      return theParameters.vec().size();
   }
   
   MinimumError error()
   {
      return theError;
   }
   FunctionGradient gradient()
   {
      return theGradient;
   }
   double fval()
   {
      return theParameters.fval();
   }
   double edm()
   {
      return theEDM;
   }
   int nfcn()
   {
      return theNFcn;
   }
   
   boolean isValid()
   {
      if(hasParameters() && hasCovariance())
         return parameters().isValid() && error().isValid();
      else if(hasParameters()) return parameters().isValid();
      else return false;
   }
   boolean hasParameters()
   {
      return theParameters.isValid();
   }
   boolean hasCovariance()
   {
      return theError.isAvailable();
   }
   
   private MinimumParameters theParameters;
   private MinimumError theError;
   private FunctionGradient theGradient;
   private double theEDM;
   private int theNFcn;
}