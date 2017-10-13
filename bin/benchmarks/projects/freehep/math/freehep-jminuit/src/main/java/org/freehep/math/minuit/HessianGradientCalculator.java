package org.freehep.math.minuit;

/**
 *
 * @version $Id: HessianGradientCalculator.java 8584 2006-08-10 23:06:37Z duns $
 */
class HessianGradientCalculator implements GradientCalculator
{
   HessianGradientCalculator(MnFcn fcn, MnUserTransformation par, MnStrategy stra)
   {
      theFcn= fcn;
      theTransformation = par;
      theStrategy = stra;
   }
   
   public FunctionGradient gradient(MinimumParameters par)
   {
      InitialGradientCalculator gc = new InitialGradientCalculator(theFcn, theTransformation, theStrategy);
      FunctionGradient gra = gc.gradient(par);
      return gradient(par, gra);
   }
   
   public FunctionGradient gradient(MinimumParameters par , FunctionGradient gradient)
   {
      return deltaGradient(par, gradient).first;
   }
   
   Pair<FunctionGradient, MnAlgebraicVector> deltaGradient(MinimumParameters par, FunctionGradient gradient)
   {
      if (!par.isValid()) throw new IllegalArgumentException("parameters are invalid");
      
      MnAlgebraicVector x = par.vec().clone();
      MnAlgebraicVector grd = gradient.grad().clone();
      MnAlgebraicVector g2 = gradient.g2();
      MnAlgebraicVector gstep = gradient.gstep();
      
      double fcnmin = par.fval();
      //   std::cout<<"fval: "<<fcnmin<<std::endl;
      
      double dfmin = 4.*precision().eps2()*(Math.abs(fcnmin)+theFcn.errorDef());
      
      int n = x.size();
      MnAlgebraicVector dgrd = new MnAlgebraicVector(n);
      
      // initial starting values
      for(int i = 0; i < n; i++)
      {
         double xtf = x.get(i);
         double dmin = 4.*precision().eps2()*(xtf + precision().eps2());
         double epspri = precision().eps2() + Math.abs(grd.get(i)*precision().eps2());
         double optstp = Math.sqrt(dfmin/(Math.abs(g2.get(i))+epspri));
         double d = 0.2*Math.abs(gstep.get(i));
         if(d > optstp) d = optstp;
         if(d < dmin) d = dmin;
         double chgold = 10000.;
         double dgmin = 0.;
         double grdold = 0.;
         double grdnew = 0.;
         for(int j = 0; j < ncycle(); j++)
         {
            x.set(i, xtf + d);
            double fs1 = theFcn.valueOf(x);
            x.set(i, xtf - d);
            double fs2 = theFcn.valueOf(x);
            x.set(i, xtf);
            //       double sag = 0.5*(fs1+fs2-2.*fcnmin);
            grdold = grd.get(i);
            grdnew = (fs1-fs2)/(2.*d);
            dgmin = precision().eps()*(Math.abs(fs1) + Math.abs(fs2))/d;
            if (Math.abs(grdnew) < precision().eps()) break;
            double change = Math.abs((grdold-grdnew)/grdnew);
            if(change > chgold && j > 1) break;
            chgold = change;
            grd.set(i, grdnew);
            if(change < 0.05) break;
            if( Math.abs(grdold-grdnew) < dgmin) break;
            if(d < dmin) break;
            d *= 0.2;
         }
         dgrd.set(i, Math.max(dgmin, Math.abs(grdold-grdnew)));
      }
      
      return new Pair<FunctionGradient, MnAlgebraicVector>(new FunctionGradient(grd, g2, gstep), dgrd);
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
      return strategy().hessianGradientNCycles();
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
