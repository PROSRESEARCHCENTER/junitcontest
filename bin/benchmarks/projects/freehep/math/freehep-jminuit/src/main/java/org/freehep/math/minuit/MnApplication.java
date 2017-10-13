package org.freehep.math.minuit;

import java.util.List;

/** Base class for minimizers.
 * @version $Id: MnApplication.java 8584 2006-08-10 23:06:37Z duns $
 */
public abstract class MnApplication
{
   static int DEFAULT_STRATEGY = 1;
   static int DEFAULT_MAXFCN = 0;
   static double DEFAULT_TOLER = 0.1;
   
   MnApplication(FCNBase fcn, MnUserParameterState state, MnStrategy stra)
   {
      theFCN = fcn;
      theState = state;
      theStrategy = stra;
      checkAnalyticalDerivatives = true;
      useAnalyticalDerivatives = true;      
   }
   
   MnApplication(FCNBase fcn, MnUserParameterState state, MnStrategy stra, int nfcn)
   {
      theFCN = fcn;
      theState = state;
      theStrategy = stra;
      theNumCall = nfcn;
      checkAnalyticalDerivatives = true;
      useAnalyticalDerivatives = true;  
   }
   
   /**
    * 
    */
   
   public FunctionMinimum minimize()
   {
      return minimize(DEFAULT_MAXFCN);
   }
   public FunctionMinimum minimize(int maxfcn)
   {
      return minimize(maxfcn,DEFAULT_TOLER);
   }
   /**
    * Causes minimization of the FCN and returns the result in form of a FunctionMinimum.
    * @param maxfcn specifies the (approximate) maximum number of function calls after
    * which the calculation will be stopped even if it has not yet converged.
    * @param toler specifies the required tolerance on the function value at the minimum. 
    * The default tolerance value is 0.1, and the minimization will stop when the
    * estimated vertical distance to the minimum (EDM) is less than 0:001*tolerance*errorDef
    */
   public FunctionMinimum minimize(int maxfcn, double toler)
   {
      if(!theState.isValid()) throw new RuntimeException("Invalid state");
      int npar = variableParameters();
      if(maxfcn == 0) maxfcn = 200 + 100*npar + 5*npar*npar;
      FunctionMinimum min = minimizer().minimize(theFCN, theState, theStrategy, maxfcn, toler, theErrorDef, useAnalyticalDerivatives,checkAnalyticalDerivatives);
      theNumCall += min.nfcn();
      theState = min.userState();
      return min;
   }
   
   abstract ModularFunctionMinimizer minimizer();
   
   public MnMachinePrecision precision()
   {
      return theState.precision();
   }
   public MnUserParameterState state()
   {
      return theState;
   }
   public MnUserParameters parameters()
   {
      return theState.parameters();
   }
   public MnUserCovariance covariance()
   {
      return theState.covariance();
   }
   public FCNBase fcnbase()
   { 
      return theFCN;
   }
   public MnStrategy strategy()
   {
      return theStrategy;
   }
   public int numOfCalls()
   {
      return theNumCall;
   }
   
   // facade: forward interface of MnUserParameters and MnUserTransformation
   // via MnUserParameterState
   
   /** access to parameters (row-wise) */
   List<MinuitParameter> minuitParameters()
   {
      return theState.minuitParameters();
   }
   /** access to parameters and errors in column-wise representation */
   public double[] params()
   {
      return theState.params();
   }
   public double[] errors()
   {
      return theState.errors();
   }
   
   /** access to single parameter */
   MinuitParameter parameter(int i)
   {
      return theState.parameter(i);
   }
   
   /** add free parameter */
   public void add(String name, double val, double err)
   {
      theState.add(name, val, err);
   }
   /** add limited parameter */
   public void add(String name, double val, double err, double low, double up)
   {
      theState.add(name, val, err, low, up);
   }
   /** add const parameter */
   public void add(String name, double val)
   {
      theState.add(name, val);
   }
   
   //interaction via external number of parameter
   public void fix(int index)
   {
      theState.fix(index);   
   }
   public void release(int index)
   {
      theState.release(index);
   }
   public void setValue(int index, double val)
   {
      theState.setValue(index,val);
   }
   public void setError(int index, double err)
   {
      theState.setError(index,err);
   }
   public void setLimits(int index, double low, double up)
   {
      theState.setLimits(index,low,up);
   }
   public void removeLimits(int index)
   {
      theState.removeLimits(index);
   }
   
   public double value(int index) 
   {
      return theState.value(index);
   }
   public double error(int index)
   {
      return theState.error(index);
   }
   
   //interaction via name of parameter
   public void fix(String name)
   {
      theState.fix(name);
   }
   public void release(String name)
   {
      theState.release(name);
   }
   public void setValue(String name, double val)
   {
      theState.setValue(name,val);
   }
   public void setError(String name, double err)
   {
      theState.setError(name,err);
   }
   public void setLimits(String name, double low, double up)
   {
      theState.setLimits(name,low,up);
   }
   public void removeLimits(String name)
   {
      theState.removeLimits(name);
   }
   public void setPrecision(double prec)
   {
      theState.setPrecision(prec);
   }
   
   public double value(String name)
   {
      return theState.value(name);
   }
   public double error(String name)
   {
      return theState.error(name);
   }
   
   /** convert name into external number of parameter */
   public int index(String name)
   {
      return theState.index(name);
   }
   /** convert external number into name of parameter */
   public String name(int index)
   {
      return theState.name(index);
   }
   
   // transformation internal <-> external
   double int2ext(int i, double value)
   {
      return theState.int2ext(i,value);
   }
   double ext2int(int i, double value)
   {
      return theState.ext2int(i,value);
   }
   int intOfExt(int i)
   {
      return theState.intOfExt(i);
   }
   int extOfInt(int i)
   {
      return theState.extOfInt(i);
   }
   public int variableParameters()
   {
      return theState.variableParameters();
   }
   /**
    * By default if the function to be minimized implements FCNGradientBase then the 
    * analytical gradient provided by the function will be used. Set this to <CODE>false</CODE>
    * to disable this behaviour and force numerical calculation of the gradient.
    */
   public void setUseAnalyticalDerivatives(boolean use)
   {
      useAnalyticalDerivatives = use;
   }
   public boolean useAnalyticalDerivaties()
   {
      return useAnalyticalDerivatives;
   }
   /**
    * Minuit does a check of the user gradient at the beginning, if this is not
    * wanted the set this to "false".
    */
   public void setCheckAnalyticalDerivatives(boolean check)
   {
      checkAnalyticalDerivatives = check;
   }
   public boolean checkAnalyticalDerivatives()
   {
      return checkAnalyticalDerivatives;
   }
   /** errorDef() is the error definition of the function. 
    *  E.g. is 1 if function is Chi2 and
    *  0.5 if function is -logLikelihood. If the user wants instead the 2-sigma
    *  errors, errorDef() = 4, as Chi2(x+n*sigma) = Chi2(x) + n*n.
    */
   public void setErrorDef(double errorDef)
   {
      theErrorDef = errorDef;
   }
   public double errorDef()
   {
      return theErrorDef;
   }
   /* package protected */ boolean useAnalyticalDerivatives;
   /* package protected */ boolean checkAnalyticalDerivatives;
   /* package protected */ FCNBase theFCN;
   /* package protected */ MnUserParameterState theState;
   /* package protected */ MnStrategy theStrategy;
   /* package protected */ int theNumCall;   
   /* package protected */ double theErrorDef = 1;
}
