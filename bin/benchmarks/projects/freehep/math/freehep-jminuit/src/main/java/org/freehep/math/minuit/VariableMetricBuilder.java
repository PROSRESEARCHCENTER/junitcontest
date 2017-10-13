package org.freehep.math.minuit;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 *
 * @version $Id: VariableMetricBuilder.java 16141 2014-09-05 01:17:10Z tonyj $
 */
class VariableMetricBuilder implements MinimumBuilder
{
   VariableMetricBuilder()
   {
      theEstimator = new VariableMetricEDMEstimator();
      theErrorUpdator = new DavidonErrorUpdator();
   }
   
   public FunctionMinimum minimum(MnFcn fcn, GradientCalculator gc, MinimumSeed seed, MnStrategy strategy, int maxfcn, double edmval)
   {
      FunctionMinimum min = minimum(fcn, gc, seed, maxfcn, edmval);
      if( (strategy.strategy() == 2) || (strategy.strategy() == 1 && min.error().dcovar() > 0.05) )
      {
         MinimumState st = new MnHesse(strategy).calculate(fcn, min.state(), min.seed().trafo(),0);
         min.add(st);
      }
      if(!min.isValid())logger.info("FunctionMinimum is invalid.");
      
      return min;
   }
   
   FunctionMinimum minimum(MnFcn fcn, GradientCalculator gc, MinimumSeed seed, int maxfcn, double edmval)
   {
      edmval *= 0.0001;
      
      if(seed.parameters().vec().size() == 0)
      {
         return new FunctionMinimum(seed, fcn.errorDef());
      }
      
      MnMachinePrecision prec = seed.precision();
      
      List<MinimumState> result = new ArrayList<MinimumState>(8);
      
      double edm = seed.state().edm();

      if(edm < 0.)
      {
        logger.info("VariableMetricBuilder: initial matrix not pos.def.");
         if (seed.error().isPosDef()) throw new RuntimeException("Something is wrong!");
         return new FunctionMinimum(seed, fcn.errorDef());
      }
      
      result.add(seed.state());
      
      // iterate until edm is small enough or max # of iterations reached
      edm *= (1. + 3.*seed.error().dcovar());
      MnAlgebraicVector step = new MnAlgebraicVector(seed.gradient().vec().size());
      do
      {
         MinimumState s0 = result.get(result.size()-1);
   
         step = MnUtils.mul(MnUtils.mul(s0.error().invHessian(),s0.gradient().vec()),-1);
         
         double gdel = MnUtils.innerProduct(step, s0.gradient().grad());
         if(gdel > 0.)
         {
           logger.info("VariableMetricBuilder: matrix not pos.def.");
           logger.info("gdel > 0: "+gdel);
            s0 = MnPosDef.test(s0, prec);
            step = MnUtils.mul(MnUtils.mul(s0.error().invHessian(),s0.gradient().vec()),-1);
            gdel = MnUtils.innerProduct(step, s0.gradient().grad());
           logger.info("gdel: "+gdel);
            if(gdel > 0.)
            {
               result.add(s0);
               return new FunctionMinimum(seed, result, fcn.errorDef());
            }
         }
         MnParabolaPoint pp = MnLineSearch.search(fcn, s0.parameters(), step, gdel, prec);
         if(Math.abs(pp.y() - s0.fval()) < prec.eps())
         {
           logger.info("VariableMetricBuilder: no improvement");
            break; //no improvement
         }
         MinimumParameters p = new MinimumParameters(MnUtils.add(s0.vec(), MnUtils.mul(step,pp.x())), pp.y());
         FunctionGradient g = gc.gradient(p, s0.gradient());
         
         edm = estimator().estimate(g, s0.error());
         if(edm < 0.)
         {
           logger.info("VariableMetricBuilder: matrix not pos.def.");
           logger.info("edm < 0");
            s0 = MnPosDef.test(s0, prec);
            edm = estimator().estimate(g, s0.error());
            if(edm < 0.)
            {
               result.add(s0);
               return new FunctionMinimum(seed, result, fcn.errorDef());
            }
         }
         MinimumError e = errorUpdator().update(s0, p, g);
         result.add(new MinimumState(p, e, g, edm, fcn.numOfCalls()));
         //     result[0] = MinimumState(p, e, g, edm, fcn.numOfCalls());
         edm *= (1. + 3.*e.dcovar());
      } while(edm > edmval && fcn.numOfCalls() < maxfcn);
      
      if(fcn.numOfCalls() >= maxfcn)
      {
        logger.info("VariableMetricBuilder: call limit exceeded.");
         return new FunctionMinimum(seed, result, fcn.errorDef(), new FunctionMinimum.MnReachedCallLimit());
      }
      
      if(edm > edmval)
      {
         if(edm < Math.abs(prec.eps2()*result.get(result.size()-1).fval()))
         {
           logger.info("VariableMetricBuilder: machine accuracy limits further improvement.");
            return new FunctionMinimum(seed, result, fcn.errorDef());
         } 
         else if(edm < 10.*edmval)
         {
            return new FunctionMinimum(seed, result, fcn.errorDef());
         } 
         else
         {
           logger.info("VariableMetricBuilder: finishes without convergence.");
           logger.info("VariableMetricBuilder: edm= "+edm+" requested: "+edmval);
            return new FunctionMinimum(seed, result, fcn.errorDef(), new FunctionMinimum.MnAboveMaxEdm());
         }
      }
      
      return new FunctionMinimum(seed, result, fcn.errorDef());
   }
   
   VariableMetricEDMEstimator estimator()
   {
      return theEstimator;
   }
   DavidonErrorUpdator errorUpdator()
   {
      return theErrorUpdator;
   }
   
   private final VariableMetricEDMEstimator theEstimator;
   private final DavidonErrorUpdator theErrorUpdator;
   private final static Logger logger = Logger.getLogger(VariableMetricBuilder.class.getName());
}
