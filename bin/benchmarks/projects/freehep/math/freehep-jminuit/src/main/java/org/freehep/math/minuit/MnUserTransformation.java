package org.freehep.math.minuit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** knows how to transform between user specified parameters (external) and
 * internal parameters used for minimization
 * @version $Id: MnUserTransformation.java 8584 2006-08-10 23:06:37Z duns $
 */
class MnUserTransformation
{
   private MnMachinePrecision thePrecision;
   private List<MinuitParameter> theParameters;
   private List<Integer> theExtOfInt;
   private Map<String, Integer> nameMap = new HashMap<String, Integer>();
   
   private SinParameterTransformation theDoubleLimTrafo;
   private SqrtUpParameterTransformation theUpperLimTrafo;
   private SqrtLowParameterTransformation theLowerLimTrafo;
   
   private List<Double> theCache;
   
   MnUserTransformation()
   {
      thePrecision = new MnMachinePrecision();
      theParameters = new ArrayList<MinuitParameter>();
      theExtOfInt = new ArrayList<Integer>();
      theDoubleLimTrafo = new SinParameterTransformation();
      theUpperLimTrafo = new SqrtUpParameterTransformation();
      theLowerLimTrafo = new SqrtLowParameterTransformation();
      theCache = new ArrayList<Double>(0);
   }
   protected MnUserTransformation clone()
   {
      return new MnUserTransformation(this);
   }
   private MnUserTransformation(MnUserTransformation other)
   {
      thePrecision = other.thePrecision;
      theParameters = new ArrayList<MinuitParameter>(other.theParameters.size());
      for (MinuitParameter par : other.theParameters) theParameters.add(par.clone());
      theExtOfInt = new ArrayList<Integer>(other.theExtOfInt);
      theDoubleLimTrafo = new SinParameterTransformation();
      theUpperLimTrafo = new SqrtUpParameterTransformation();
      theLowerLimTrafo = new SqrtLowParameterTransformation();
      theCache = new ArrayList<Double>(other.theCache);
   }
   MnAlgebraicVector transform(MnAlgebraicVector pstates)
   {
      // FixMe: Worry about efficiency here
      MnAlgebraicVector result = new MnAlgebraicVector(theCache.size());
      for (int i=0; i<result.size();i++) result.set(i,theCache.get(i));
      for(int i = 0; i < pstates.size(); i++)
      {
         if(theParameters.get(theExtOfInt.get(i)).hasLimits())
         {
            result.set(theExtOfInt.get(i) , int2ext(i, pstates.get(i)));
         } 
         else
         {
            result.set(theExtOfInt.get(i) , pstates.get(i));
         }
      }
      return result;
   }
   
   MnUserTransformation(double[] par, double[] err)
   {
      thePrecision = new MnMachinePrecision();
      theDoubleLimTrafo = new SinParameterTransformation();
      theUpperLimTrafo = new SqrtUpParameterTransformation();
      theLowerLimTrafo = new SqrtLowParameterTransformation();
      theParameters = new ArrayList<MinuitParameter>(par.length);
      theExtOfInt = new ArrayList<Integer>(par.length);
      theCache = new ArrayList<Double>(par.length);
      for (int i = 0; i < par.length; i++)
      {
         add("p"+i, par[i], err[i]);
      }
   }
   //forwarded interface
   MnMachinePrecision precision()
   {
      return thePrecision;
   }
   void setPrecision(double eps)
   {
      thePrecision.setPrecision(eps);
   }
   List<MinuitParameter> parameters()
   {
      return theParameters;
   }
   int variableParameters()
   {
      return theExtOfInt.size();
   }
   
   //access to parameters and errors in column-wise representation
   double[] params()
   {
      double[] result = new double[theParameters.size()];
      int i = 0;
      for (MinuitParameter parameter : theParameters)
      {
         result[i++] = parameter.value();
      }
      return result;
   }
   double[] errors()
   {
      double[] result = new double[theParameters.size()];
      int i = 0;
      for (MinuitParameter parameter : theParameters)
      {
         result[i++] = parameter.error();
      }
      return result;
   }
   
   /** access to single parameter */
   MinuitParameter parameter(int index)
   {
      return theParameters.get(index);
   }
   
   /** add free parameter */
   void add(String name, double val, double err)
   {
      if (nameMap.containsKey(name)) throw new IllegalArgumentException("duplicate name: "+name);
      nameMap.put(name,theParameters.size());
      theExtOfInt.add(theParameters.size());
      theCache.add(val);
      theParameters.add(new MinuitParameter(theParameters.size(), name, val, err));
   }
   /** add limited parameter */
   void add(String name, double val, double err, double low, double up)
   {
      if (nameMap.containsKey(name)) throw new IllegalArgumentException("duplicate name: "+name);
      nameMap.put(name,theParameters.size());
      theExtOfInt.add(theParameters.size());
      theCache.add(val);
      theParameters.add(new MinuitParameter(theParameters.size(), name, val, err, low, up));
   }
   /** add  parameter */
   void add(String name, double val)
   {
      if (nameMap.containsKey(name)) throw new IllegalArgumentException("duplicate name: "+name);
      nameMap.put(name,theParameters.size());
      theCache.add(val);
      theParameters.add(new MinuitParameter(theParameters.size(), name, val));
   }
   
   /** interaction via external number of parameter */
   void fix(int index)
   {
      int iind = intOfExt(index);
      theExtOfInt.remove(iind);
      theParameters.get(index).fix();
   }
   void release(int index)
   {
      if (theExtOfInt.contains(index)) throw new IllegalArgumentException("index="+index);
      theExtOfInt.add(index);
      Collections.sort(theExtOfInt);
      theParameters.get(index).release();
   }
   void setValue(int index, double val)
   {
      theParameters.get(index).setValue(val);
      theCache.set(index,val);
   }
   void setError(int index, double err)
   {
      theParameters.get(index).setError(err);
   }
   void setLimits(int index, double low, double up)
   {
      theParameters.get(index).setLimits(low, up);
   }
   void setUpperLimit(int index, double up)
   {
      theParameters.get(index).setUpperLimit(up);
   }
   void setLowerLimit(int index, double low)
   {
      theParameters.get(index).setLowerLimit(low);
   }
   void removeLimits(int index)
   {
      theParameters.get(index).removeLimits();
   }
   
   double value(int index)
   {
      return theParameters.get(index).value();
   }
   double error(int index)
   {
      return theParameters.get(index).error();
   }
   
   /** interaction via name of parameter */
   void fix(String name)
   {
      fix(index(name));
   }
   void release(String name)
   {
      release(index(name));
   }
   void setValue(String name, double val)
   {
      setValue(index(name), val);
   }
   void setError(String name, double err)
   {
      setError(index(name), err);
   }
   void setLimits(String name, double low, double up)
   {
      setLimits(index(name), low, up);
   }
   void setLowerLimit(String name, double low)
   {
      setLowerLimit(index(name), low);
   }
   void setUpperLimit(String name, double up)
   {
      setUpperLimit(index(name), up);
   }
   void removeLimits(String name)
   {
      removeLimits(index(name));
   }
   
   double value(String name)
   {
      return value(index(name));
   }
   double error(String name)
   {
      return error(index(name));
   }
   
   /** convert name into external number of parameter */
   int index(String name)
   {
      return nameMap.get(name);
   }
   /** convert external number into name of parameter */
   String name(int index)
   {
      return theParameters.get(index).name();
   }
   double int2ext(int i, double val)
   {
      MinuitParameter parm = theParameters.get(theExtOfInt.get(i));
      if(parm.hasLimits())
      {
         if(parm.hasUpperLimit() && parm.hasLowerLimit())
            return theDoubleLimTrafo.int2ext(val, parm.upperLimit(), parm.lowerLimit());
         else if(parm.hasUpperLimit() && !parm.hasLowerLimit())
            return theUpperLimTrafo.int2ext(val, parm.upperLimit());
         else
            return theLowerLimTrafo.int2ext(val, parm.lowerLimit());
      }
      return val;
   }
   double int2extError(int i, double val, double err)
   {
      double dx = err;
      MinuitParameter parm = theParameters.get(theExtOfInt.get(i));
      if(parm.hasLimits())
      {
         double ui = int2ext(i, val);
         double du1 = int2ext(i, val+dx) - ui;
         double du2 = int2ext(i, val-dx) - ui;
         if(parm.hasUpperLimit() && parm.hasLowerLimit())
         {
            if(dx > 1.) du1 = parm.upperLimit() - parm.lowerLimit();
            dx = 0.5*(Math.abs(du1) + Math.abs(du2));
         }
         else
         {
            dx = 0.5*(Math.abs(du1) + Math.abs(du2));
         }
      }
      
      return dx;
   }
   MnUserCovariance int2extCovariance(MnAlgebraicVector vec, MnAlgebraicSymMatrix cov)
   {
      
      MnUserCovariance result = new MnUserCovariance(cov.nrow());
      for(int i = 0; i < vec.size(); i++)
      {
         double dxdi = 1.;
         if (theParameters.get(theExtOfInt.get(i)).hasLimits())
         {
            dxdi = dInt2Ext(i, vec.get(i));
         }
         for(int j = i; j < vec.size(); j++)
         {
            double dxdj = 1.;
            if(theParameters.get(theExtOfInt.get(j)).hasLimits())
            {
               
               dxdj = dInt2Ext(j, vec.get(j));
            }
            result.set(i,j,dxdi*cov.get(i,j)*dxdj);
         }
      }
      
      return result;
   }
   
   double ext2int(int i, double val)
   {
      MinuitParameter parm = theParameters.get(i);
      if(parm.hasLimits())
      {
         if(parm.hasUpperLimit() && parm.hasLowerLimit())
            return theDoubleLimTrafo.ext2int(val, parm.upperLimit(), parm.lowerLimit(), precision());
         else if(parm.hasUpperLimit() && !parm.hasLowerLimit())
            return theUpperLimTrafo.ext2int(val, parm.upperLimit(), precision());
         else
            return theLowerLimTrafo.ext2int(val, parm.lowerLimit(), precision());
      }
      
      return val;
   }
   
   double dInt2Ext(int i, double val)
   {
      double dd = 1.;
      MinuitParameter parm = theParameters.get(theExtOfInt.get(i));
      if(parm.hasLimits())
      {
         if(parm.hasUpperLimit() && parm.hasLowerLimit())
            dd = theDoubleLimTrafo.dInt2Ext(val, parm.upperLimit(), parm.lowerLimit());
         else if(parm.hasUpperLimit() && !parm.hasLowerLimit())
            dd = theUpperLimTrafo.dInt2Ext(val, parm.upperLimit());
         else
            dd = theLowerLimTrafo.dInt2Ext(val, parm.lowerLimit());
      }
      
      return dd;
   }
   int intOfExt(int ext)
   {
      for (int iind = 0; iind < theExtOfInt.size(); iind++)
      {
         if (ext == theExtOfInt.get(iind)) return iind;
      }
      throw new IllegalArgumentException("ext="+ext);
   }
   int extOfInt(int internal)
   {
      return theExtOfInt.get(internal);
   }
}
