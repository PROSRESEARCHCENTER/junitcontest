package org.freehep.math.minuit;

/**
 *
 * @version $Id: InitialGradientCalculator.java 8584 2006-08-10 23:06:37Z duns $
 */
class InitialGradientCalculator
{
   
   InitialGradientCalculator(MnFcn fcn, MnUserTransformation par, MnStrategy stra)
   {
      theFcn = fcn;
      theTransformation = par;
      theStrategy = stra;
   }
   
   FunctionGradient gradient(MinimumParameters par)
   {
      if (!par.isValid()) throw new IllegalArgumentException("Parameters are invalid");
      
      int n = trafo().variableParameters();
      if (n != par.vec().size()) throw new IllegalArgumentException("Parameters have invalid size");;
      
      MnAlgebraicVector gr = new MnAlgebraicVector(n);
      MnAlgebraicVector gr2 = new MnAlgebraicVector(n);
      MnAlgebraicVector gst = new MnAlgebraicVector(n);
      
      // initial starting values
      for(int i = 0; i < n; i++)
      {
         int exOfIn = trafo().extOfInt(i);
         
         double var = par.vec().get(i);
         double werr = trafo().parameter(exOfIn).error();
         double sav = trafo().int2ext(i, var);
         double sav2 = sav + werr;
         if(trafo().parameter(exOfIn).hasLimits())
         {
            if(trafo().parameter(exOfIn).hasUpperLimit() &&
            sav2 > trafo().parameter(exOfIn).upperLimit())
               sav2 = trafo().parameter(exOfIn).upperLimit();
         }
         double var2 = trafo().ext2int(exOfIn, sav2);
         double vplu = var2 - var;
         sav2 = sav - werr;
         if(trafo().parameter(exOfIn).hasLimits())
         {
            if(trafo().parameter(exOfIn).hasLowerLimit() &&
            sav2 < trafo().parameter(exOfIn).lowerLimit())
               sav2 = trafo().parameter(exOfIn).lowerLimit();
         }
         var2 = trafo().ext2int(exOfIn, sav2);
         double vmin = var2 - var;
         double dirin = 0.5*(Math.abs(vplu) + Math.abs(vmin));
         double g2 = 2.0*theFcn.errorDef()/(dirin*dirin);
         double gsmin = 8.*precision().eps2()*(Math.abs(var) + precision().eps2());
         double gstep = Math.max(gsmin, 0.1*dirin);
         double grd = g2*dirin;
         if(trafo().parameter(exOfIn).hasLimits())
         {
            if(gstep > 0.5) gstep = 0.5;
         }
         gr.set(i,grd);
         gr2.set(i,g2);
         gst.set(i,gstep);
      }
      
      return new FunctionGradient(gr, gr2, gst);
   }
   
   FunctionGradient gradient(MinimumParameters par, FunctionGradient gra)
   {
      return gradient(par);
   }
   
   MnFcn fcn()
   {
      return theFcn;
   }
   MnUserTransformation trafo()
   {
      return theTransformation;
   }
   MnMachinePrecision precision()
   {
      return theTransformation.precision();
   }
   MnStrategy strategy()
   {
      return theStrategy;
   }
   
   int ncycle()
   {
      return strategy().gradientNCycles();
   }
   double stepTolerance()
   {
      return strategy().gradientStepTolerance();
   }
   double gradTolerance()
   {
      return strategy().gradientTolerance();
   }
   
   private MnFcn theFcn;
   private MnUserTransformation theTransformation;
   private MnStrategy theStrategy;
}
