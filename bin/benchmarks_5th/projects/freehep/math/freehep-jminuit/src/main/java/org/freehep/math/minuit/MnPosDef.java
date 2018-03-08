package org.freehep.math.minuit;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @version $Id: MnPosDef.java 16142 2014-09-05 02:52:34Z tonyj $
 */
abstract class MnPosDef
{
   static MinimumState test(MinimumState st, MnMachinePrecision prec)
   {
      MinimumError err = test(st.error(), prec);
      return new MinimumState(st.parameters(), err, st.gradient(), st.edm(), st.nfcn());
      
   }
   static MinimumError test(MinimumError e, MnMachinePrecision prec)
   {
      MnAlgebraicSymMatrix err = e.invHessian().clone();
      if(err.size() == 1 && err.get(0,0) < prec.eps())
      {
         err.set(0,0,1.);
         return new MinimumError(err, new MinimumError.MnMadePosDef());
      }
      if(err.size() == 1 && err.get(0,0) > prec.eps())
      {
         return e;
      }
      //   std::cout<<"MnPosDef init matrix= "<<err<<std::endl;
      
      double epspdf = Math.max(1.e-6, prec.eps2());
      double dgmin = err.get(0,0);
      
      for(int i = 0; i < err.nrow(); i++)
      {
         if(err.get(i,i) < prec.eps2()) logger.log(Level.INFO, "negative or zero diagonal element {0} in covariance matrix", i);
         if(err.get(i,i) < dgmin) dgmin = err.get(i,i);
      }
      double dg = 0.;
      if(dgmin < prec.eps2())
      {
         dg = 1. + epspdf - dgmin;
         //     dg = 0.5*(1. + epspdf - dgmin);
         logger.log(Level.INFO, "added {0} to diagonal of error matrix", dg);
      }
      
      MnAlgebraicVector s = new MnAlgebraicVector(err.nrow());
      MnAlgebraicSymMatrix p = new MnAlgebraicSymMatrix(err.nrow());
      for(int i = 0; i < err.nrow(); i++)
      {
         err.set(i,i,err.get(i,i)+dg);
         if(err.get(i,i) < 0.) err.set(i,i,1.);
         s.set(i,1./Math.sqrt(err.get(i,i)));
         for(int j = 0; j <= i; j++)
         {
            p.set(i,j, err.get(i,j)*s.get(i)*s.get(j));
         }
      }
      
      //   std::cout<<"MnPosDef p: "<<p<<std::endl;
      MnAlgebraicVector eval = p.eigenvalues();
      double pmin = eval.get(0);
      double pmax = eval.get(eval.size() - 1);
      //   std::cout<<"pmin= "<<pmin<<" pmax= "<<pmax<<std::endl;
      pmax = Math.max(Math.abs(pmax), 1.);
      if(pmin > epspdf*pmax) return e;
      
      double padd = 0.001*pmax - pmin;
      logger.info("eigenvalues: ");
      for(int i = 0; i < err.nrow(); i++)
      {
         err.set(i,i,err.get(i,i) * (1. + padd));
         logger.info(String.format("%5g",eval.get(i)));
      }
      //   std::cout<<"MnPosDef final matrix: "<<err<<std::endl;
      logger.log(Level.INFO, "matrix forced pos-def by adding {0} to diagonal", padd);
      //   std::cout<<"eigenvalues: "<<eval<<std::endl;
      return new MinimumError(err, new MinimumError.MnMadePosDef());
      
   }
   private static final Logger logger = Logger.getLogger(MnPosDef.class.getName());
}
