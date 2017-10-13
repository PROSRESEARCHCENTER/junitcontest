package org.freehep.math.minuit;

import java.util.logging.Logger;

/**
 * With MnHesse the user can instructs MINUIT to calculate, by finite differences, the
 * Hessian or error matrix. That is, it calculates the full matrix of second derivatives
 * of the function with respect to the currently variable parameters, and inverts it.
 * @version $Id: MnHesse.java 16142 2014-09-05 02:52:34Z tonyj $
 */
public class MnHesse
{
   
   /** default constructor with default strategy */
   public MnHesse()
   {
      theStrategy = new MnStrategy(1);
   }
   
   /** constructor with user-defined strategy level */
   public MnHesse(int stra)
   {
      theStrategy = new MnStrategy(stra);
   }
   
   /** conctructor with specific strategy */
   public MnHesse(MnStrategy stra)
   {
      theStrategy = stra;
   }
   
   ///
   /// low-level API
   ///
   public MnUserParameterState calculate(FCNBase fcn,double[] par,double[] err)
   {
      return calculate(fcn,par,err,0);
   }
   /** FCN + parameters + errors */
   public MnUserParameterState calculate(FCNBase fcn,double[] par,double[] err,int maxcalls)
   {
      return calculate(fcn, new MnUserParameterState(par, err), maxcalls);
   }
   public MnUserParameterState calculate(FCNBase fcn ,double[] par,MnUserCovariance cov)
   {
      return calculate(fcn,par,cov,0);
   }
   /** FCN + parameters + MnUserCovariance */
   public MnUserParameterState calculate(FCNBase fcn ,double[] par,MnUserCovariance cov,int maxcalls)
   {
      return calculate(fcn, new MnUserParameterState(par, cov), maxcalls);
   }
   ///
   /// high-level API
   ///
   public MnUserParameterState calculate(FCNBase fcn,MnUserParameters par)
   {
      return calculate(fcn,par,0);
   }
   /** FCN + MnUserParameters */
   public MnUserParameterState calculate(FCNBase fcn,MnUserParameters par,int maxcalls)
   {
      return calculate(fcn, new MnUserParameterState(par), maxcalls);
   }
   public MnUserParameterState calculate(FCNBase fcn,MnUserParameters par,MnUserCovariance cov)
   {
      return calculate(fcn, par, 0);
   }
   /** FCN + MnUserParameters + MnUserCovariance */
   public MnUserParameterState calculate(FCNBase fcn,MnUserParameters par,MnUserCovariance cov,int maxcalls)
   {
      return calculate(fcn, new MnUserParameterState(par, cov), maxcalls);
   }
   /** FCN + MnUserParameterState */
   public MnUserParameterState calculate(FCNBase fcn,MnUserParameterState state,int maxcalls)
   {
      double errDef = 1; // FixMe!
      int n = state.variableParameters();
      MnUserFcn mfcn = new MnUserFcn(fcn, errDef, state.trafo());
      MnAlgebraicVector x = new MnAlgebraicVector(n);
      for(int i = 0; i < n; i++) x.set(i,state.intParameters().get(i));
      double amin = mfcn.valueOf(x);
      Numerical2PGradientCalculator gc = new Numerical2PGradientCalculator(mfcn, state.trafo(), theStrategy);
      MinimumParameters par = new MinimumParameters(x, amin);
      FunctionGradient gra = gc.gradient(par);
      MinimumState tmp = calculate(mfcn, new MinimumState(par, new MinimumError(new MnAlgebraicSymMatrix(n), 1.), gra, state.edm(), state.nfcn()), state.trafo(), maxcalls);
      
      return new MnUserParameterState(tmp, errDef, state.trafo());
   }
   ///
   /// internal interface
   ///
   MinimumState calculate(MnFcn mfcn,MinimumState st,MnUserTransformation trafo,int maxcalls)
   {
      MnMachinePrecision prec = trafo.precision();
      // make sure starting at the right place
      double amin = mfcn.valueOf(st.vec());
      double aimsag = Math.sqrt(prec.eps2())*(Math.abs(amin)+mfcn.errorDef());
      
      // diagonal elements first
      
      int n = st.parameters().vec().size();
      if(maxcalls == 0) maxcalls = 200 + 100*n + 5*n*n;
      
      MnAlgebraicSymMatrix vhmat = new MnAlgebraicSymMatrix(n);
      MnAlgebraicVector g2 = st.gradient().g2().clone();
      MnAlgebraicVector gst = st.gradient().gstep().clone();
      MnAlgebraicVector grd = st.gradient().grad().clone();
      MnAlgebraicVector dirin = st.gradient().gstep().clone();
      MnAlgebraicVector yy = new MnAlgebraicVector(n);
      if(st.gradient().isAnalytical())
      {
         InitialGradientCalculator igc = new InitialGradientCalculator(mfcn, trafo, theStrategy);
         FunctionGradient tmp = igc.gradient(st.parameters());
         gst = tmp.gstep().clone();
         dirin = tmp.gstep().clone();
         g2 = tmp.g2().clone();
      }
      try
      {      
         MnAlgebraicVector x = st.parameters().vec().clone();
         
         for(int i = 0; i < n; i++)
         {
            
            double xtf = x.get(i);
            double dmin = 8.*prec.eps2()*(Math.abs(xtf) + prec.eps2());
            double d = Math.abs(gst.get(i));
            if(d < dmin) d = dmin;
            
            for(int icyc = 0; icyc < ncycles(); icyc++)
            {
               double sag = 0.;
               double fs1 = 0.;
               double fs2 = 0.;
               int multpy = 0;
               for(; multpy < 5; multpy++)
               {
                  x.set(i,xtf + d);
                  fs1 = mfcn.valueOf(x);
                  x.set(i,xtf - d);
                  fs2 = mfcn.valueOf(x);
                  x.set(i,xtf);
                  sag = 0.5*(fs1+fs2-2.*amin);
                  if(sag > prec.eps2()) break;
                  if(trafo.parameter(i).hasLimits())
                  {
                     if(d > 0.5)
                     {
                        throw new MnHesseFailed("MnHesse: 2nd derivative zero for parameter");
                     }
                     d *= 10.;
                     if(d > 0.5) d = 0.51;
                     continue;
                  }
                  d *= 10.;
               }
               if (multpy >= 5) throw new MnHesseFailed("MnHesse: 2nd derivative zero for parameter");
               
               double g2bfor = g2.get(i);
               g2.set(i, 2.*sag/(d*d));
               grd.set(i, (fs1-fs2)/(2.*d));
               gst.set(i, d);
               dirin.set(i, d);
               yy.set(i, fs1);
               double dlast = d;
               d = Math.sqrt(2.*aimsag/Math.abs(g2.get(i)));
               if(trafo.parameter(i).hasLimits()) d = Math.min(0.5, d);
               if(d < dmin) d = dmin;
               
               // see if converged
               if(Math.abs((d-dlast)/d) < tolerstp()) break;
               if(Math.abs((g2.get(i)-g2bfor)/g2.get(i)) < tolerg2()) break;
               d = Math.min(d, 10.*dlast);
               d = Math.max(d, 0.1*dlast);
            }
            vhmat.set(i,i,g2.get(i));
            if(mfcn.numOfCalls() - st.nfcn() > maxcalls) throw new MnHesseFailed("MnHesse: maximum number of allowed function calls exhausted.");
         }
         
         if(theStrategy.strategy() > 0)
         {
            // refine first derivative
            HessianGradientCalculator hgc = new HessianGradientCalculator(mfcn, trafo, theStrategy);
            FunctionGradient gr = hgc.gradient(st.parameters(), new FunctionGradient(grd, g2, gst));
            grd = gr.grad();
         }
         
         //off-diagonal elements
         for(int i = 0; i < n; i++)
         {
            x.set(i, x.get(i) + dirin.get(i));
            for(int j = i+1; j < n; j++)
            {
               x.set(j, x.get(j) + dirin.get(j));
               double fs1 = mfcn.valueOf(x);
               double elem = (fs1 + amin - yy.get(i) - yy.get(j))/(dirin.get(i)*dirin.get(j));
               vhmat.set(i,j, elem);
               x.set(j, x.get(j) - dirin.get(j));
            }
            x.set(i ,  x.get(i) - dirin.get(i));
         }
         
         //verify if matrix pos-def (still 2nd derivative)
         MinimumError tmp = MnPosDef.test(new MinimumError(vhmat,1.), prec);
         vhmat = tmp.invHessian();
         try
         {
            vhmat.invert();
         }
         catch (MatrixInversionException xx)
         {
            throw new MnHesseFailed("MnHesse: matrix inversion fails!");
         }
         
         FunctionGradient gr = new FunctionGradient(grd, g2, gst);
         
         if(tmp.isMadePosDef())
         {
            logger.info("MnHesse: matrix is invalid!");
            logger.info("MnHesse: matrix is not pos. def.!");
            logger.info("MnHesse: matrix was forced pos. def.");
            return new MinimumState(st.parameters(), new MinimumError(vhmat, new MinimumError.MnMadePosDef()), gr, st.edm(), mfcn.numOfCalls());
         }
         
         //calculate edm
         MinimumError err = new MinimumError(vhmat, 0.);
         double edm = new VariableMetricEDMEstimator().estimate(gr, err);
         
         return new MinimumState(st.parameters(), err, gr, edm, mfcn.numOfCalls());
      }
      catch (MnHesseFailed x)
      {
         logger.info(x.getMessage());
         logger.info("MnHesse fails and will return diagonal matrix ");
         
         for(int j = 0; j < n; j++)
         {
            double tmp = g2.get(j) < prec.eps2() ? 1. : 1./g2.get(j);
            vhmat.set(j,j,tmp < prec.eps2() ? 1. : tmp);
         }
         
         return new MinimumState(st.parameters(), new MinimumError(vhmat, new MinimumError.MnHesseFailed()), st.gradient(), st.edm(), st.nfcn()+mfcn.numOfCalls());
         
      }
   }
   
   /// forward interface of MnStrategy
   int ncycles()
   {
      return theStrategy.hessianNCycles();
   }
   double tolerstp()
   {
      return theStrategy.hessianStepTolerance();
   }
   double tolerg2()
   {
      return theStrategy.hessianG2Tolerance();
   }
   
   private MnStrategy theStrategy;
   
   private class MnHesseFailed extends Exception
   {
      MnHesseFailed(String message)
      {
         super(message);
      }
      
   }
   private static final Logger logger = Logger.getLogger(MnHesse.class.getName());
}
