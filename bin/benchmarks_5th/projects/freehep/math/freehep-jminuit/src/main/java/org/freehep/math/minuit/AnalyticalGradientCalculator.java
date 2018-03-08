package org.freehep.math.minuit;

/**
 *
 * @version $Id: AnalyticalGradientCalculator.java 8584 2006-08-10 23:06:37Z duns $
 */
class AnalyticalGradientCalculator implements GradientCalculator
{
   AnalyticalGradientCalculator(FCNGradientBase fcn, MnUserTransformation state, boolean checkGradient)
   {
      theGradCalc = fcn;
      theTransformation = state;
      theCheckGradient = checkGradient;
   }
   
   public FunctionGradient gradient(MinimumParameters par)
   {
      double[] grad = theGradCalc.gradient(theTransformation.transform(par.vec()).data());
      if (grad.length != theTransformation.parameters().size()) throw new IllegalArgumentException("Invalid parameter size");
      
      MnAlgebraicVector v = new MnAlgebraicVector(par.vec().size());
      for( int i = 0; i < par.vec().size(); i++)
      {
         int ext = theTransformation.extOfInt(i);
         if(theTransformation.parameter(ext).hasLimits())
         {
            double dd = theTransformation.dInt2Ext(i, par.vec().get(i));
            v.set(i,dd*grad[ext]);
         } 
         else
         {
            v.set(i,grad[ext]);
         }
      }
      
      return new FunctionGradient(v);
   }
   
   public FunctionGradient gradient(MinimumParameters par, FunctionGradient grad)
   {
      return gradient(par);
   }
   
   boolean checkGradient()
   {
      return theCheckGradient;
   }
   
   private FCNGradientBase theGradCalc;
   private MnUserTransformation theTransformation;
   private boolean theCheckGradient;
}
