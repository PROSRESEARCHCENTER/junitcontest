package org.freehep.math.minuit;

import java.util.logging.Logger;

/** API class for Minos error analysis (asymmetric errors).
 * Minimization has to be done before and minimum must be valid;
 * possibility to ask only for one side of the Minos error;
 * @version $Id: MnMinos.java 16141 2014-09-05 01:17:10Z tonyj $
 */
public class MnMinos
{
   private static final Logger logger = Logger.getLogger(MnMinos.class.getName());
   /** construct from FCN + minimum */
   public MnMinos(FCNBase fcn, FunctionMinimum min)
   {
      this(fcn,min,MnApplication.DEFAULT_STRATEGY);
   }
   
   /** construct from FCN + minimum + strategy */
   public MnMinos(FCNBase fcn, FunctionMinimum min, int stra)
   {
      this(fcn,min,new MnStrategy(stra));
   }
   
   /** construct from FCN + minimum + strategy */
   public MnMinos(FCNBase fcn, FunctionMinimum min, MnStrategy stra)
   {
      theFCN = fcn;
      theMinimum = min;
      theStrategy = stra;
   }
   public MinosError minos(int par)
   {
      return minos(par,1.);
   }
   public MinosError minos(int par, double errDef)
   {
      return minos(par,errDef, MnApplication.DEFAULT_MAXFCN);
   }   
   /**
    * Causes a MINOS error analysis to be performed on the parameter whose number is
    * specified. MINOS errors may be expensive to calculate, but are very reliable since
    * they take account of non-linearities in the problem as well as parameter correlations,
    * and are in general asymmetric.
    * @param maxcalls Specifies the (approximate) maximum number of function calls per parameter 
    * requested, after which the calculation will be stopped for that parameter.
    */
   public MinosError minos(int par,  double errDef, int maxcalls)
   {
      assert(theMinimum.isValid());
      assert(!theMinimum.userState().parameter(par).isFixed());
      assert(!theMinimum.userState().parameter(par).isConst());
      
      MnCross up = upval(par, errDef, maxcalls);
      MnCross lo = loval(par, errDef, maxcalls);
      
      return new MinosError(par, theMinimum.userState().value(par), lo, up);
   }
   public Point range(int par)
   {
      return range(par,1);
   }
   public Point range(int par, double errDef)
   {
      return range(par,errDef, MnApplication.DEFAULT_MAXFCN);
   }
   /**
    * Causes a MINOS error analysis for external parameter n.
    * @return The lower and upper bounds of parameter 
    */
   public Point range(int par, double errDef, int maxcalls)
   {
      MinosError mnerr = minos(par, errDef, maxcalls);
      return mnerr.range();
   }
   public double lower(int par)
   {
      return lower(par,1);
   }
   public double lower(int par, double errDef)
   {
      return lower(par,errDef, MnApplication.DEFAULT_MAXFCN);
   }  
   /** calculate one side (negative or positive error) of the parameter */
   public double lower(int par, double errDef, int maxcalls)
   {
      MnUserParameterState upar = theMinimum.userState();
      double err = theMinimum.userState().error(par);
      MnCross aopt = loval(par, errDef, maxcalls);
      double lower = aopt.isValid() ? -1.*err*(1.+ aopt.value()) : (aopt.atLimit() ? upar.parameter(par).lowerLimit() : upar.value(par));
      return lower;
   }
   public double upper(int par)
   {
      return upper(par,1);
   }
   public double upper(int par, double errDef)
   {
      return upper(par,errDef, MnApplication.DEFAULT_MAXFCN);
   } 
   public double upper(int par, double errDef, int maxcalls)
   {
      MnUserParameterState upar = theMinimum.userState();
      double err = theMinimum.userState().error(par);
      MnCross aopt = upval(par, errDef, maxcalls);
      double upper = aopt.isValid() ? err*(1.+ aopt.value()) : (aopt.atLimit() ? upar.parameter(par).upperLimit() : upar.value(par));
      return upper;
   }
   public MnCross loval(int par)
   {
      return loval(par,1);
   }
   public MnCross loval(int par, double errDef)
   {
      return loval(par,errDef, MnApplication.DEFAULT_MAXFCN);
   } 
   public MnCross loval(int par, double errDef, int maxcalls)
   {
      errDef *= theMinimum.errorDef();
      assert(theMinimum.isValid());
      assert(!theMinimum.userState().parameter(par).isFixed());
      assert(!theMinimum.userState().parameter(par).isConst());
      if(maxcalls == 0)
      {
         int nvar = theMinimum.userState().variableParameters();
         maxcalls = 2*(nvar+1)*(200 + 100*nvar + 5*nvar*nvar);
      }
      int[] para = {par};
      
      MnUserParameterState upar = theMinimum.userState().clone();
      double err = upar.error(par);
      double val = upar.value(par) - err;
      double[] xmid = {val};
      double[] xdir = {-err};
      
      int ind = upar.intOfExt(par);
      MnAlgebraicSymMatrix m = theMinimum.error().matrix();
      double xunit = Math.sqrt(errDef/err);
      for(int i = 0; i < m.nrow(); i++)
      {
         if(i == ind) continue;
         double xdev = xunit*m.get(ind,i);
         int ext = upar.extOfInt(i);
         upar.setValue(ext, upar.value(ext) - xdev);
      }
      
      upar.fix(par);
      upar.setValue(par, val);
      
      double toler = 0.1;
      MnFunctionCross cross = new MnFunctionCross(theFCN, upar, theMinimum.fval(), theStrategy, errDef);
      
      MnCross aopt = cross.cross(para, xmid, xdir, toler, maxcalls);
      
      if(aopt.atLimit())
         logger.info("MnMinos parameter "+par+" is at lower limit.");
      if(aopt.atMaxFcn())
         logger.info("MnMinos maximum number of function calls exceeded for parameter "+par);
      if(aopt.newMinimum())
         logger.info("MnMinos new minimum found while looking for parameter "+par);
      if(!aopt.isValid())
         logger.info("MnMinos could not find lower value for parameter "+par+".");
      
      return aopt;
      
   }
   public MnCross upval(int par)
   {
      return upval(par,1);
   }
   public MnCross upval(int par, double errDef)
   {
      return upval(par,errDef, MnApplication.DEFAULT_MAXFCN);
   } 
   public MnCross upval(int par, double errDef, int maxcalls)
   {
      errDef *= theMinimum.errorDef();
      assert(theMinimum.isValid());
      assert(!theMinimum.userState().parameter(par).isFixed());
      assert(!theMinimum.userState().parameter(par).isConst());
      if(maxcalls == 0)
      {
         int nvar = theMinimum.userState().variableParameters();
         maxcalls = 2*(nvar+1)*(200 + 100*nvar + 5*nvar*nvar);
      }
      
      int[] para = { par };
      
      MnUserParameterState upar = theMinimum.userState().clone();
      double err = upar.error(par);
      double val = upar.value(par) + err;
      double[] xmid = {val};
      double[] xdir = {err};
      
      int ind = upar.intOfExt(par);
      MnAlgebraicSymMatrix m = theMinimum.error().matrix();
      double xunit = Math.sqrt(errDef/err);
      for(int i = 0; i < m.nrow(); i++)
      {
         if(i == ind) continue;
         double xdev = xunit*m.get(ind,i);
         int ext = upar.extOfInt(i);
         upar.setValue(ext, upar.value(ext) + xdev);
      }
      
      upar.fix(par);
      upar.setValue(par, val);
      
      double toler = 0.1;
      MnFunctionCross cross = new MnFunctionCross(theFCN, upar, theMinimum.fval(), theStrategy, errDef);
      MnCross aopt = cross.cross(para, xmid, xdir, toler, maxcalls);
      
      if(aopt.atLimit())
        logger.info("MnMinos parameter "+par+" is at upper limit.");
      if(aopt.atMaxFcn())
        logger.info("MnMinos maximum number of function calls exceeded for parameter "+par);
      if(aopt.newMinimum())
        logger.info("MnMinos new minimum found while looking for parameter "+par);
      if(!aopt.isValid())
        logger.info("MnMinos could not find upper value for parameter "+par+".");
      
      return aopt;
   }
   
   private FCNBase theFCN;
   private FunctionMinimum theMinimum;
   private MnStrategy theStrategy;
}
