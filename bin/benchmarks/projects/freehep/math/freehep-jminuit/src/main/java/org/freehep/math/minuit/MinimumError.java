package org.freehep.math.minuit;

import java.util.logging.Logger;

/** MinimumError keeps the inverse 2nd derivative (inverse Hessian) used for
 * calculating the parameter step size (-V*g) and for the covariance update
 * (ErrorUpdator). The covariance matrix is equal to twice the inverse Hessian.
 * @version $Id: MinimumError.java 16142 2014-09-05 02:52:34Z tonyj $
 */
class MinimumError
{
   MinimumError(int n)
   {
      theMatrix = new MnAlgebraicSymMatrix(n);
      theDCovar = 1.;
   }
   MinimumError(MnAlgebraicSymMatrix mat, double dcov)
   {
      theMatrix = mat;
      theDCovar = dcov;
      theValid = true;
      thePosDef = true;
      theAvailable = true;
   }
   MinimumError(MnAlgebraicSymMatrix mat, MnHesseFailed x)
   {
      theMatrix = mat;
      theDCovar = 1;
      theValid = false;
      thePosDef = false;
      theMadePosDef = false;
      theHesseFailed= true;
      theInvertFailed = false;
      theAvailable = true;
   }
   MinimumError(MnAlgebraicSymMatrix mat, MnMadePosDef x)
   {
      theMatrix = mat;
      theDCovar = 1.;
      theValid = false;
      thePosDef = false;
      theMadePosDef = true;
      theHesseFailed = false;
      theInvertFailed = false;
      theAvailable = true;
   }
   
   MinimumError(MnAlgebraicSymMatrix mat, MnInvertFailed x)
   {
      theMatrix = mat;
      theDCovar = 1.;
      theValid = false;
      thePosDef = true;
      theMadePosDef = false;
      theHesseFailed = false;
      theInvertFailed = true;
      theAvailable = true;
   }
   
   MinimumError(MnAlgebraicSymMatrix mat, MnNotPosDef x )
   {
      theMatrix = mat;
      theDCovar = 1.;
      theValid = false;
      thePosDef = false;
      theMadePosDef = false;
      theHesseFailed = false;
      theInvertFailed = false;
      theAvailable = true;
   }
   
   
     MnAlgebraicSymMatrix matrix()
     {
        return MnUtils.mul(theMatrix,2);
     }
   
   MnAlgebraicSymMatrix invHessian()
   {
      return theMatrix;
   }
   
   MnAlgebraicSymMatrix hessian()
   {
      try
      {
         MnAlgebraicSymMatrix tmp = theMatrix.clone();
         tmp.invert();
         return tmp;
      }
      catch (MatrixInversionException x)
      {
         
         logger.info("BasicMinimumError inversion fails; return diagonal matrix.");
         MnAlgebraicSymMatrix tmp = new MnAlgebraicSymMatrix(theMatrix.nrow());
         for(int i = 0; i < theMatrix.nrow(); i++)
         {
            tmp.set(i,i, 1./theMatrix.get(i,i));
         }
         return tmp;
      }
   }
   
   double dcovar()
   {
      return theDCovar;
   }
   boolean isAccurate()
   {
      return theDCovar < 0.1;
   }
   boolean isValid()
   {
      return theValid;
   }
   boolean isPosDef()
   {
      return thePosDef;
   }
   boolean isMadePosDef()
   {
      return theMadePosDef;
   }
   boolean hesseFailed()
   {
      return theHesseFailed;
   }
   boolean invertFailed()
   {
      return theInvertFailed;
   }
   boolean isAvailable()
   {
      return theAvailable;
   }
   
   private final MnAlgebraicSymMatrix theMatrix;
   private final double theDCovar;
   private boolean theValid;
   private boolean thePosDef;
   private boolean theMadePosDef;
   private boolean theHesseFailed;
   private boolean theInvertFailed;
   private boolean theAvailable;
   
   static class MnNotPosDef {};
   static class MnMadePosDef {};
   static class MnHesseFailed {};
   static class MnInvertFailed {};
   
   private static final Logger logger = Logger.getLogger(MinimumError.class.getName());
}
