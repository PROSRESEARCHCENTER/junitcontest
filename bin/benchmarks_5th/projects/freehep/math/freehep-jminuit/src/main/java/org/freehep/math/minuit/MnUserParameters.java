package org.freehep.math.minuit;

import java.util.List;

/**
 * API class for the user interaction with the parameters.
 * Serves as input to the minimizer as well as output from it;
 * users can interact: fix/release parameters, set values and errors, etc.;
 * parameters can be accessed via their parameter number or via their 
 * user-specified name.
 * @version $Id: MnUserParameters.java 8584 2006-08-10 23:06:37Z duns $
 */
public class MnUserParameters
{
   private MnUserTransformation theTransformation;
   /** Creates a new instance of MnUserParameters */
   public MnUserParameters()
   {
      theTransformation = new MnUserTransformation();
   }
   public MnUserParameters(double[] par, double[] err)
   {
      theTransformation = new MnUserTransformation(par,err);
   }
   protected MnUserParameters clone()
   {
      return new MnUserParameters(this);
   }
   private MnUserParameters(MnUserParameters other)
   {
      theTransformation = other.theTransformation.clone();
   }
   MnUserTransformation trafo()
   {
      return theTransformation;
   }
   
   public int variableParameters()
   {
      return theTransformation.variableParameters();
   }
   
   /** access to parameters (row-wise) */
   List<MinuitParameter> parameters()
   {
      return theTransformation.parameters();
   }
   
   /** access to parameters and errors in column-wise representation */
   double[] params()
   {
      return theTransformation.params();
   }
   double[] errors()
   {
      return theTransformation.errors();
   }
   
   /** access to single parameter */
   MinuitParameter parameter(int index)
   {
      return theTransformation.parameter(index);
   }
   
   /**
    * Add free parameter name, value, error
    * <p>
    * When adding parameters, MINUIT assigns indices to each parameter which will be
    * the same as in the double[] in the FCNBase.valueOf(). That means the
    * first parameter the user adds gets index 0, the second index 1, and so on. When
    * calculating the function value inside FCN, MINUIT will call FCNBase.valueOf() with
    * the elements at their respective positions.
    */
   public void add(String name, double val, double err)
   {
      theTransformation.add(name,val,err);
   }
   /**
    * Add limited parameter name, value, lower bound, upper bound
    */
   public void add(String name, double val, double err, double low, double up)
   {
      theTransformation.add(name,val,err,low,up);
   }
   /**
    * Add const parameter name, value
    */
   public void add(String name, double val)
   {
      theTransformation.add(name,val);
   }
   
   /// interaction via external number of parameter
   /**
    * Fixes the specified parameter (so that the minimizer will no longer vary it)
    */
   public void fix(int index)
   {
      theTransformation.fix(index);
   }
   /**
    * Releases the specified parameter (so that the minimizer can vary it)
    */
   public void release(int index)
   {
      theTransformation.release(index);
   }
   /**
    * Set the value of parameter. The parameter in
    * question may be variable, fixed, or constant, but must be defined.
    */
   public void setValue(int index, double val)
   {
      theTransformation.setValue(index,val);
   }
   public void setError(int index, double err)
   {
      theTransformation.setError(index, err);
   }
   /**
    * Set the lower and upper bound on the specified variable.
    */
   public void setLimits(int index, double low, double up)
   {
      theTransformation.setLimits(index,low,up);
   }
   public void setUpperLimit(int index, double up)
   {
      theTransformation.setUpperLimit(index, up);
   }
   public void setLowerLimit(int index, double low)
   {
      theTransformation.setLowerLimit(index,low);
   }
   public void removeLimits(int index)
   {
      theTransformation.removeLimits(index);
   }
   
   public double value(int index)
   {
      return theTransformation.value(index);
   }
   public double error(int index)
   {
      return theTransformation.error(index);
   }
   
   /// interaction via name of parameter
   /**
    * Fixes the specified parameter (so that the minimizer will no longer vary it)
    */
   public void fix(String name)
   {
      theTransformation.fix(name);
   }
   /**
    * Releases the specified parameter (so that the minimizer can vary it)
    */
   public void release(String name)
   {
      theTransformation.release(name);
   }
   /**
    * Set the value of parameter. The parameter in
    * question may be variable, fixed, or constant, but must be defined.
    */
   public void setValue(String name, double val)
   {
      theTransformation.setValue(name,val);
   }
   public void setError(String name, double err)
   {
      theTransformation.setError(name,err);
   }
   /**
    * Set the lower and upper bound on the specified variable.
    */
   public void setLimits(String name, double low, double up)
   {
      theTransformation.setLimits(name,low,up);
   }
   public void setUpperLimit(String name, double up)
   {
      theTransformation.setUpperLimit(name,up);
   }
   public void setLowerLimit(String name, double low)
   {
      theTransformation.setLowerLimit(name,low);
   }
   public void removeLimits(String name)
   {
      theTransformation.removeLimits(name);
   }
   
   public double value(String name)
   {
      return theTransformation.value(name);
   }
   public double error(String name)
   {
      return theTransformation.error(name);
   }
   
   /** convert name into external number of parameter */
   int index(String name)
   {
      return theTransformation.index(name);
   }
   /** convert external number into name of parameter */
   String name(int index)
   {
      return theTransformation.name(index);
   }
   
   public MnMachinePrecision precision()
   {
      return theTransformation.precision();
   }
   public void setPrecision(double eps)
   {
      theTransformation.setPrecision(eps);
   }
   public String toString()
   {
      return MnPrint.toString(this);
   }
}
