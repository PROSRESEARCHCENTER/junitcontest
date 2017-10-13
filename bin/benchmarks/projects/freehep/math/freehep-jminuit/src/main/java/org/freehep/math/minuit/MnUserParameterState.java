package org.freehep.math.minuit;

import java.util.ArrayList;
import java.util.List;

/**
 * The class MnUserParameterState contains the MnUserParameters and the MnUserCovariance. 
 * It can be created on input by the user, or by MINUIT itself as user
 * representable format of the result of the minimization.
 * @version $Id: MnUserParameterState.java 8584 2006-08-10 23:06:37Z duns $
 */
public class MnUserParameterState
{
   private boolean theValid;
   private boolean theCovarianceValid;
   private boolean theGCCValid;
   
   private double theFVal;
   private double theEDM;
   private int theNFcn;
   
   private MnUserParameters theParameters;
   private MnUserCovariance theCovariance;
   private MnGlobalCorrelationCoeff theGlobalCC;
   
   private List<Double> theIntParameters;
   private MnUserCovariance theIntCovariance;
   
   MnUserParameterState()
   {
      theValid = false;
      theCovarianceValid = false;
      theParameters = new MnUserParameters();
      theCovariance = new MnUserCovariance();
      theIntParameters = new ArrayList<Double>();
      theIntCovariance =  new MnUserCovariance();
   }
   protected MnUserParameterState clone()
   {
      return new MnUserParameterState(this);
   }
   private MnUserParameterState(MnUserParameterState other)
   {
      theValid = other.theValid;
      theCovarianceValid = other.theCovarianceValid;
      theGCCValid = other.theGCCValid;
      
      theFVal = other.theFVal;
      theEDM = other.theEDM;
      theNFcn = other.theNFcn;
      
      theParameters = other.theParameters.clone();
      theCovariance = other.theCovariance;
      theGlobalCC = other.theGlobalCC;
      
      theIntParameters = new ArrayList<Double>(other.theIntParameters);
      theIntCovariance = other.theIntCovariance.clone();
   }
   
   /** construct from user parameters (before minimization) */
   MnUserParameterState(double[] par, double[] err)
   {
      theValid = true;
      theParameters = new MnUserParameters(par, err);
      theCovariance = new MnUserCovariance();
      theGlobalCC = new MnGlobalCorrelationCoeff();
      theIntParameters = new ArrayList<Double>(par.length);
      for (int i=0; i<par.length; i++) theIntParameters.add(par[i]);
      theIntCovariance = new MnUserCovariance();
   }
   
   MnUserParameterState(MnUserParameters par)
   {
      theValid = true;
      theParameters = par;
      theCovariance = new MnUserCovariance();
      theGlobalCC = new MnGlobalCorrelationCoeff();
      theIntParameters = new ArrayList<Double>(par.variableParameters());
      theIntCovariance = new MnUserCovariance();
      
      int i = 0;
      for (MinuitParameter ipar : par.parameters())
      {
         if (ipar.isConst() || ipar.isFixed()) continue;
         if (ipar.hasLimits())
            theIntParameters.add(ext2int(ipar.number(),ipar.value()));
         else
            theIntParameters.add(ipar.value());
      }
   }
   
   /** construct from user parameters + covariance (before minimization) */
   MnUserParameterState(double[] par, double[] cov, int nrow)
   {
      theValid = true;
      theCovarianceValid = true;
      theCovariance = new MnUserCovariance(cov, nrow);
      theGlobalCC = new MnGlobalCorrelationCoeff();
      theIntParameters = new ArrayList<Double>(par.length);
      theIntCovariance = new MnUserCovariance(cov, nrow);
      
      double[] err = new double[par.length];
      for (int i = 0; i < par.length; i++)
      {
         assert(theCovariance.get(i,i) > 0.);
         err[i] = Math.sqrt(theCovariance.get(i,i));
         theIntParameters.add(par[i]);
      }
      theParameters = new MnUserParameters(par, err);
      assert(theCovariance.nrow() == variableParameters());
   }
   
   MnUserParameterState(double[] par, MnUserCovariance cov)
   {
      theValid = true;
      theCovarianceValid = true;
      theCovariance = cov;
      theGlobalCC = new MnGlobalCorrelationCoeff();
      theIntParameters = new ArrayList<Double>(par.length);
      theIntCovariance = cov.clone();
      
      if (theCovariance.nrow() != variableParameters()) throw new IllegalArgumentException("Bad covariance size");
      double[] err = new double[par.length];
      for (int i = 0; i < par.length; i++)
      {
         if (theCovariance.get(i,i) <= 0.) throw new IllegalArgumentException("Bad covariance");
         err[i] = Math.sqrt(theCovariance.get(i,i));
         theIntParameters.add(par[i]);
      }
      theParameters = new MnUserParameters(par, err);
   }
   
   MnUserParameterState(MnUserParameters par, MnUserCovariance cov)
   {
      theValid = true;
      theCovarianceValid = true;
      theParameters = par;
      theCovariance = cov;
      theGlobalCC = new MnGlobalCorrelationCoeff();
      theIntParameters = new ArrayList<Double>();
      theIntCovariance = cov.clone();
      
      theIntCovariance.scale(0.5);
      int i = 0;
      for (MinuitParameter ipar : par.parameters())
      {
         if(ipar.isConst() || ipar.isFixed()) continue;
         if(ipar.hasLimits())
            theIntParameters.add(ext2int(ipar.number(), ipar.value()));
         else
            theIntParameters.add(ipar.value());
      }
      assert(theCovariance.nrow() == variableParameters());
   }
   
   /** construct from internal parameters (after minimization) */
   MnUserParameterState(MinimumState st, double up, MnUserTransformation trafo)
   {
      theValid = st.isValid();
      theCovarianceValid = false;
      theGCCValid = false;
      theFVal = st.fval();
      theEDM = st.edm();
      theNFcn = st.nfcn();
      theParameters = new MnUserParameters();
      theCovariance = new MnUserCovariance();
      theGlobalCC = new MnGlobalCorrelationCoeff();
      theIntParameters = new ArrayList<Double>();
      theIntCovariance = new MnUserCovariance();
      
      for (MinuitParameter ipar : trafo.parameters())
      {
         if(ipar.isConst())
         {
            add(ipar.name(), ipar.value());
         }
         else if(ipar.isFixed())
         {
            add(ipar.name(), ipar.value(), ipar.error());
            if(ipar.hasLimits())
            {
               if(ipar.hasLowerLimit() && ipar.hasUpperLimit())
                  setLimits(ipar.name(), ipar.lowerLimit(),ipar.upperLimit());
               else if(ipar.hasLowerLimit() && !ipar.hasUpperLimit())
                  setLowerLimit(ipar.name(), ipar.lowerLimit());
               else
                  setUpperLimit(ipar.name(), ipar.upperLimit());
            }
            fix(ipar.name());
         }
         else if(ipar.hasLimits())
         {
            int i = trafo.intOfExt(ipar.number());
            double err = st.hasCovariance() ? Math.sqrt(2.*up*st.error().invHessian().get(i,i)) : st.parameters().dirin().get(i);
            add(ipar.name(), trafo.int2ext(i, st.vec().get(i)), trafo.int2extError(i, st.vec().get(i), err));
            if(ipar.hasLowerLimit() && ipar.hasUpperLimit())
               setLimits(ipar.name(), ipar.lowerLimit(), ipar.upperLimit());
            else if(ipar.hasLowerLimit() && !ipar.hasUpperLimit())
               setLowerLimit(ipar.name(), ipar.lowerLimit());
            else
               setUpperLimit(ipar.name(), ipar.upperLimit());
         }
         else
         {
            int i = trafo.intOfExt(ipar.number());
            double err = st.hasCovariance() ? Math.sqrt(2.*up*st.error().invHessian().get(i,i)) : st.parameters().dirin().get(i);
            add(ipar.name(), st.vec().get(i), err);
         }
      }
      
      theCovarianceValid = st.error().isValid();
      
      if(theCovarianceValid)
      {
         theCovariance = trafo.int2extCovariance(st.vec(), st.error().invHessian());
         theIntCovariance = new MnUserCovariance(st.error().invHessian().data().clone(), st.error().invHessian().nrow());
         theCovariance.scale(2.*up);
         theGlobalCC = new MnGlobalCorrelationCoeff(st.error().invHessian());
         theGCCValid = true;
         
         assert(theCovariance.nrow() == variableParameters());
      }
      
   }
   
   //user external representation
   MnUserParameters parameters()
   {
      return theParameters;
   }
   MnUserCovariance covariance()
   {
      return theCovariance;
   }
   MnGlobalCorrelationCoeff globalCC()
   {
      return theGlobalCC;
   }
   
   /** Minuit internal representation */
   List<Double> intParameters()
   {
      return theIntParameters;
   }
   MnUserCovariance intCovariance()
   {
      return theIntCovariance;
   }
   
   /** transformation internal <-> external */
   MnUserTransformation trafo()
   {
      return theParameters.trafo();
   }
   
   /**
    * Returns <CODE>true</CODE> if the the state is valid, <CODE>false</CODE> if not
    */
   public boolean isValid()
   {
      return theValid;
   }
   /**
    * Returns <CODE>true</CODE>
    * if the the state has a valid covariance, <CODE>false</CODE> otherwise.
    */
   public boolean hasCovariance()
   {
      return theCovarianceValid;
   }
   public boolean hasGlobalCC()
   {
      return theGCCValid;
   }
   
   /**
    * returns the function value at the minimum
    */
   public double fval()
   {
      return theFVal;
   }
   /**
    * Returns the expected vertival distance to the minimum (EDM)
    */
   public double edm()
   {
      return theEDM;
   }
   /**
    * Returns the number of function calls during the minimization.
    */
   public int nfcn()
   {
      return theNFcn;
   }
   
   // facade: forward interface of MnUserParameters and MnUserTransformation
   
   /** access to parameters (row-wise) */
   List<MinuitParameter> minuitParameters()
   {
      return theParameters.parameters();
   }
   
   /** access to parameters and errors in column-wise representation */
   public double[] params()
   {
      return theParameters.params();
   }
   public double[] errors()
   {
      return theParameters.errors();
   }
   
   MinuitParameter parameter(int i)
   {
      return theParameters.parameter(i);
   }
   
   /** add free parameter name, value, error */
   public void add(String name, double val, double err)
   {
      theParameters.add(name, val, err);
      theIntParameters.add(val);
      theCovarianceValid = false;
      theGCCValid = false;
      theValid = true;
   }
   /** add limited parameter name, value, lower bound, upper bound */
   public void add(String name, double val, double err, double low, double up)
   {
      theParameters.add(name, val, err, low, up);
      theCovarianceValid = false;
      theIntParameters.add(ext2int(index(name), val));
      theGCCValid = false;
      theValid = true;
   }
   /** add const parameter name, value */
   public void add(String name, double val)
   {
      theParameters.add(name, val);
      theValid = true;
   }
   
   /// interaction via external number of parameter
   public void fix(int e)
   {
      int i = intOfExt(e);
      if(theCovarianceValid)
      {
         theCovariance = MnCovarianceSqueeze.squeeze(theCovariance, i);
         theIntCovariance = MnCovarianceSqueeze.squeeze(theIntCovariance, i);
      }
      theIntParameters.remove(i);
      theParameters.fix(e);
      theGCCValid = false;
   }
   public void release(int e)
   {
      theParameters.release(e);
      theCovarianceValid = false;
      theGCCValid = false;
      int i = intOfExt(e);
      if(parameter(e).hasLimits())
         theIntParameters.add(i, ext2int(e, parameter(e).value()));
      else
         theIntParameters.add(i, parameter(e).value());
   }
   public void setValue(int e, double val)
   {
      theParameters.setValue(e, val);
      if(!parameter(e).isFixed() && !parameter(e).isConst())
      {
         int i = intOfExt(e);
         if(parameter(e).hasLimits())
            theIntParameters.set(i,ext2int(e, val));
         else
            theIntParameters.set(i,val);
      }
   }
   public void setError(int e, double err)
   {
      theParameters.setError(e, err);
   }
   public void setLimits(int e, double low, double up)
   {
      theParameters.setLimits(e, low, up);
      theCovarianceValid = false;
      theGCCValid = false;
      if(!parameter(e).isFixed() && !parameter(e).isConst())
      {
         int i = intOfExt(e);
         if(low < theIntParameters.get(i) && theIntParameters.get(i) < up)
            theIntParameters.set(i,ext2int(e, theIntParameters.get(i)));
         else
            theIntParameters.set(i,ext2int(e, 0.5*(low+up)));
      }
   }
   public void setUpperLimit(int e, double up)
   {
      theParameters.setUpperLimit(e, up);
      theCovarianceValid = false;
      theGCCValid = false;
      if(!parameter(e).isFixed() && !parameter(e).isConst())
      {
         int i = intOfExt(e);
         if(theIntParameters.get(i) < up)
            theIntParameters.set(i,ext2int(e, theIntParameters.get(i)));
         else
            theIntParameters.set(i,ext2int(e, up - 0.5*Math.abs(up + 1.)));
      }
   }
   public void setLowerLimit(int e, double low)
   {
      theParameters.setLowerLimit(e, low);
      theCovarianceValid = false;
      theGCCValid = false;
      if(!parameter(e).isFixed() && !parameter(e).isConst())
      {
         int i = intOfExt(e);
         if(low < theIntParameters.get(i))
            theIntParameters.set(i,ext2int(e, theIntParameters.get(i)));
         else
            theIntParameters.set(i,ext2int(e, low + 0.5*Math.abs(low + 1.)));
      }
   }
   public void removeLimits(int e)
   {
      theParameters.removeLimits(e);
      theCovarianceValid = false;
      theGCCValid = false;
      if(!parameter(e).isFixed() && !parameter(e).isConst())
         theIntParameters.set(intOfExt(e),value(e));
   }
   
   public double value(int index)
   {
      return theParameters.value(index);
   }
   public double error(int index)
   {
      return theParameters.error(index);
   }
   
   /// interaction via name of parameter
   public void fix(String name)
   {
      fix(index(name));
   }
   public void release(String name)
   {
      release(index(name));
   }
   public void setValue(String name, double val)
   {
      setValue(index(name), val);;
   }
   public void setError(String name, double err)
   {
      setError(index(name), err);
   }
   public void setLimits(String name, double low, double up)
   {
      setLimits(index(name), low, up);
   }
   public void setUpperLimit(String name, double up)
   {
      setUpperLimit(index(name), up);
   }
   public void setLowerLimit(String name, double low)
   {
      setLowerLimit(index(name), low);
   }
   public void removeLimits(String name)
   {
      removeLimits(index(name));
   }
   
   public double value(String name)
   {
      return value(index(name));
   }
   public double error(String name)
   {
      return error(index(name));
   }
   
   /** convert name into external number of parameter */
   public int index(String name)
   {
      return theParameters.index(name);
   }
   /** convert external number into name of parameter */
   public String name(int index)
   {
      return theParameters.name(index);
   }
   
   // transformation internal <-> external
   double int2ext(int i, double val)
   {
      return theParameters.trafo().int2ext(i,val);
   }
   double ext2int(int i, double val)
   {
      return theParameters.trafo().ext2int(i,val);
   }
   int intOfExt(int ext)
   {
      return theParameters.trafo().intOfExt(ext);
   }
   public int extOfInt(int internal)
   {
      return theParameters.trafo().extOfInt(internal);
   }
   public int variableParameters()
   {
      return theParameters.variableParameters();
   }
   public MnMachinePrecision precision()
   {
      return theParameters.precision();
   }
   public void setPrecision(double eps)
   {
      theParameters.setPrecision(eps);
   }
   public String toString()
   {
      return MnPrint.toString(this);
   }
}
