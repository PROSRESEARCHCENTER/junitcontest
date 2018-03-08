package org.freehep.math.minuit;

/**
 *
 * @version $Id: Numerical2PGradientCalculator.java 8584 2006-08-10 23:06:37Z duns $
 */ 
class Numerical2PGradientCalculator implements GradientCalculator
{
   Numerical2PGradientCalculator(MnFcn fcn, MnUserTransformation par, MnStrategy stra)
   {
      theFcn = fcn;
      theTransformation = par;
      theStrategy = stra;
   }
   
   public FunctionGradient gradient(MinimumParameters par)
   {
      InitialGradientCalculator gc = new InitialGradientCalculator(theFcn, theTransformation, theStrategy);
      FunctionGradient gra = gc.gradient(par);
      return gradient(par, gra);
   }
   
   public FunctionGradient gradient(MinimumParameters par, FunctionGradient gradient)
   {
      if (!par.isValid()) throw new IllegalArgumentException("Parameters are invalid");
      
      MnAlgebraicVector x = par.vec().clone();
      
      double fcnmin = par.fval();
      double dfmin = 8.*precision().eps2()*(Math.abs(fcnmin)+theFcn.errorDef());
      double vrysml = 8.*precision().eps()*precision().eps();
      
      int n = x.size();
      MnAlgebraicVector grd = gradient.grad().clone();
      MnAlgebraicVector g2 = gradient.g2().clone();
      MnAlgebraicVector gstep = gradient.gstep().clone();
      for(int i = 0; i < n; i++)
      {
         double xtf = x.get(i);
         double epspri = precision().eps2() + Math.abs(grd.get(i)*precision().eps2());
         double stepb4 = 0.;
         for(int j = 0; j < ncycle(); j++)
         {
            double optstp = Math.sqrt(dfmin/(Math.abs(g2.get(i))+epspri));
            double step = Math.max(optstp, Math.abs(0.1*gstep.get(i)));
            
            if(trafo().parameter(trafo().extOfInt(i)).hasLimits())
            {
               if(step > 0.5) step = 0.5;
            }
            double stpmax = 10.*Math.abs(gstep.get(i));
            if(step > stpmax) step = stpmax;
            
            double stpmin = Math.max(vrysml, 8.*Math.abs(precision().eps2()*x.get(i)));
            if(step < stpmin) step = stpmin;
            if(Math.abs((step-stepb4)/step) < stepTolerance())
            {
               break;
            }
            gstep.set(i,step);
            stepb4 = step;
            
            x.set(i,xtf + step);
            double fs1 = theFcn.valueOf(x);
            x.set(i,xtf - step);
            double fs2 = theFcn.valueOf(x);
            x.set(i,xtf);
            
            double grdb4 = grd.get(i);
            
            grd.set(i, 0.5*(fs1 - fs2)/step);
            g2.set(i, (fs1 + fs2 - 2.*fcnmin)/step/step);
            
            if(Math.abs(grdb4-grd.get(i))/(Math.abs(grd.get(i))+dfmin/step) < gradTolerance())
            {
               break;
            }
         }
         
      }
      return new FunctionGradient(grd, g2, gstep);
      
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
