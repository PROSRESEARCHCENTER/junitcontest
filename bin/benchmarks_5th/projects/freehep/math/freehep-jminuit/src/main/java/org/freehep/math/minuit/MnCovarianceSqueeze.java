package org.freehep.math.minuit;

import java.util.logging.Logger;

/**
 *
 * @version $Id: MnCovarianceSqueeze.java 16142 2014-09-05 02:52:34Z tonyj $
 */
abstract class MnCovarianceSqueeze
{
   
   static MnUserCovariance squeeze(MnUserCovariance cov, int n)
   {
      assert(cov.nrow() > 0);
      assert(n < cov.nrow());
      
      MnAlgebraicSymMatrix hess = new MnAlgebraicSymMatrix(cov.nrow());
      for(int i = 0; i < cov.nrow(); i++)
      {
         for(int j = i; j < cov.nrow(); j++)
         {
            hess.set(i,j,cov.get(i,j));
         }
      }
      
      try
      {
         hess.invert();
      }
      catch (MatrixInversionException x)
      {
         logger.info("MnUserCovariance inversion failed; return diagonal matrix;");
         MnUserCovariance result = new MnUserCovariance(cov.nrow() - 1);
         for(int i = 0, j =0; i < cov.nrow(); i++)
         {
            if(i == n) continue;
            result.set(j,j,cov.get(i,i));
            j++;
         }
         return result;
      }
      
      MnAlgebraicSymMatrix squeezed = squeeze(hess, n);
      
      try
      {
         squeezed.invert();
      }
      catch (MatrixInversionException x)
      {
         logger.info("MnUserCovariance back-inversion failed; return diagonal matrix;");
         MnUserCovariance result = new MnUserCovariance(squeezed.nrow());
         for(int i = 0; i < squeezed.nrow(); i++)
         {
            result.set(i,i,1./squeezed.get(i,i));
         }
         return result;
      }
      
      return new MnUserCovariance(squeezed.data(), squeezed.nrow());
      
   }
   
   static MinimumError squeeze(MinimumError err,  int n)
   {
      MnAlgebraicSymMatrix hess = err.hessian();
      MnAlgebraicSymMatrix squeezed = squeeze(hess, n);
      try
      {
         squeezed.invert();
      }
      catch (MatrixInversionException x)
      {
         logger.info("MnCovarianceSqueeze: MinimumError inversion fails; return diagonal matrix.");
         MnAlgebraicSymMatrix tmp = new MnAlgebraicSymMatrix(squeezed.nrow());
         for(int i = 0; i < squeezed.nrow(); i++)
         {
            tmp.set(i,i, 1./squeezed.get(i,i));
         }
         return new MinimumError(tmp, new MinimumError.MnInvertFailed());
      }
      
      return new MinimumError(squeezed, err.dcovar());     
   }
   
   static MnAlgebraicSymMatrix squeeze(MnAlgebraicSymMatrix hess,  int n)
   {
      assert(hess.nrow() > 0);
      assert(n < hess.nrow());
      
      MnAlgebraicSymMatrix hs = new MnAlgebraicSymMatrix(hess.nrow() - 1);
      for(int i = 0, j = 0; i < hess.nrow(); i++)
      {
         if(i == n) continue;
         for(int k = i, l = j; k < hess.nrow(); k++)
         {
            if(k == n) continue;
            hs.set(j,l,hess.get(i,k));
            l++;
         }
         j++;
      }
      
      return hs;      
   }
   private static final Logger logger = Logger.getLogger(MnCovarianceSqueeze.class.getName());
}
