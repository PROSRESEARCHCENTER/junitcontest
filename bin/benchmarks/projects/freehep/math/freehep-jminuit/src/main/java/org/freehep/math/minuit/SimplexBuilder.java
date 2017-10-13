package org.freehep.math.minuit;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 *
 * @version $Id: SimplexBuilder.java 16142 2014-09-05 02:52:34Z tonyj $
 */
class SimplexBuilder implements MinimumBuilder
{
   @Override
   public FunctionMinimum minimum(MnFcn mfcn, GradientCalculator gc, MinimumSeed seed, MnStrategy strategy, int maxfcn, double minedm)
   {
      MnMachinePrecision prec = seed.precision();
      MnAlgebraicVector x = seed.parameters().vec().clone();
      MnAlgebraicVector step = MnUtils.mul(seed.gradient().gstep(),10.);
      
      int n = x.size();
      double wg = 1./n;
      double alpha = 1., beta = 0.5, gamma = 2., rhomin = 4., rhomax = 8.;
      double rho1 = 1. + alpha;
      double rho2 = 1. + alpha*gamma;
      
      List<Pair<Double,MnAlgebraicVector>> simpl =  new ArrayList<Pair<Double,MnAlgebraicVector>>(n+1);
      simpl.add(new Pair<Double, MnAlgebraicVector>(seed.fval(), x.clone()));
      
      int jl = 0, jh = 0;
      double amin = seed.fval(), aming = seed.fval();
      
      for(int i = 0; i < n; i++)
      {
         double dmin = 8.*prec.eps2()*(Math.abs(x.get(i)) + prec.eps2());
         if(step.get(i) < dmin) step.set(i,dmin);
         x.set(i, x.get(i) +  step.get(i));
         double tmp = mfcn.valueOf(x);
         if(tmp < amin)
         {
            amin = tmp;
            jl = i+1;
         }
         if(tmp > aming)
         {
            aming = tmp;
            jh = i+1;
         }
         simpl.add(new Pair<Double, MnAlgebraicVector>(tmp, x.clone()));
         x.set(i, x.get(i) - step.get(i));
      }
      SimplexParameters simplex = new SimplexParameters(simpl, jh, jl);
      
      do
      {
         amin = simplex.get(jl).first;
         jl = simplex.jl();
         jh = simplex.jh();
         MnAlgebraicVector pbar = new MnAlgebraicVector(n);
         for(int i = 0; i < n+1; i++)
         {
            if(i == jh) continue;
            pbar = MnUtils.add(pbar,MnUtils.mul(simplex.get(i).second,wg));
         }
         
         MnAlgebraicVector pstar = MnUtils.sub(MnUtils.mul(pbar,1. + alpha),MnUtils.mul(simplex.get(jh).second,alpha));
         double ystar = mfcn.valueOf(pstar);
         
         if(ystar > amin)
         {
            if(ystar < simplex.get(jh).first)
            {
               simplex.update(ystar, pstar);
               if(jh != simplex.jh()) continue;
            }
            MnAlgebraicVector pstst = MnUtils.add(MnUtils.mul(simplex.get(jh).second,beta),MnUtils.mul(pbar,1. - beta));
            double ystst = mfcn.valueOf(pstst);
            if(ystst > simplex.get(jh).first) break;
            simplex.update(ystst, pstst);
            continue;
         }
         
         MnAlgebraicVector pstst = MnUtils.add(MnUtils.mul(pstar,gamma),MnUtils.mul(pbar,1. - gamma));
         double ystst = mfcn.valueOf(pstst);
         
         double y1 = (ystar - simplex.get(jh).first)*rho2;
         double y2 = (ystst - simplex.get(jh).first)*rho1;
         double rho = 0.5*(rho2*y1 - rho1*y2)/(y1 - y2);
         if(rho < rhomin)
         {
            if(ystst < simplex.get(jl).first) simplex.update(ystst, pstst);
            else simplex.update(ystar, pstar);
            continue;
         }
         if(rho > rhomax) rho = rhomax;
         MnAlgebraicVector prho = MnUtils.add(MnUtils.mul(pbar,rho),MnUtils.mul(simplex.get(jh).second,1. - rho));
         double yrho = mfcn.valueOf(prho);
         if(yrho < simplex.get(jl).first && yrho < ystst)
         {
            simplex.update(yrho, prho);
            continue;
         }
         if(ystst < simplex.get(jl).first)
         {
            simplex.update(ystst, pstst);
            continue;
         }
         if(yrho > simplex.get(jl).first)
         {
            if(ystst < simplex.get(jl).first) simplex.update(ystst, pstst);
            else simplex.update(ystar, pstar);
            continue;
         }
         if(ystar > simplex.get(jh).first)
         {
            pstst = MnUtils.add(MnUtils.mul(simplex.get(jh).second,beta),MnUtils.mul(pbar,1-beta));
            ystst = mfcn.valueOf(pstst);
            if(ystst > simplex.get(jh).first) break;
            simplex.update(ystst, pstst);
         }
      } while(simplex.edm() > minedm && mfcn.numOfCalls() < maxfcn);
      
      amin = simplex.get(jl).first;
      jl = simplex.jl();
      jh = simplex.jh();
      
      MnAlgebraicVector pbar = new MnAlgebraicVector(n);
      for(int i = 0; i < n+1; i++)
      {
         if(i == jh) continue;
         pbar = MnUtils.add(pbar,MnUtils.mul(simplex.get(i).second,wg));
      }
      double ybar = mfcn.valueOf(pbar);
      if(ybar < amin) simplex.update(ybar, pbar);
      else
      {
         pbar = simplex.get(jl).second;
         ybar = simplex.get(jl).first;
      }
      
      MnAlgebraicVector dirin = simplex.dirin();
      //   scale to sigmas on parameters werr^2 = dirin^2 * (up/edm)
      dirin = MnUtils.mul(dirin,Math.sqrt(mfcn.errorDef()/simplex.edm()));
      
      MinimumState st = new MinimumState(new MinimumParameters(pbar, dirin, ybar), simplex.edm(), mfcn.numOfCalls());
      List<MinimumState> states = new ArrayList<MinimumState>(1);
      states.add(st);      
      
      if(mfcn.numOfCalls() > maxfcn)
      {
         logger.info("Simplex did not converge, #fcn calls exhausted.");
         return new FunctionMinimum(seed, states, mfcn.errorDef(), new FunctionMinimum.MnReachedCallLimit());
      }
      if(simplex.edm() > minedm)
      {
         logger.info("Simplex did not converge, edm > minedm.");
         return new FunctionMinimum(seed, states, mfcn.errorDef(), new FunctionMinimum.MnAboveMaxEdm());
      }
      
      return new FunctionMinimum(seed, states, mfcn.errorDef());
   }
   private static final Logger logger = Logger.getLogger(SimplexBuilder.class.getName());
}
