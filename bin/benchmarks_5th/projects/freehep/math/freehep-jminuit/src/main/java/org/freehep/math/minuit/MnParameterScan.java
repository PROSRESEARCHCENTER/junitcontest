package org.freehep.math.minuit;

import java.util.ArrayList;
import java.util.List;

/** Scans the values of FCN as a function of one parameter and retains the
 * best function and parameter values found
 * @version $Id: MnParameterScan.java 8584 2006-08-10 23:06:37Z duns $
 */
class MnParameterScan
{
   MnParameterScan(FCNBase fcn,  MnUserParameters par)
   {
      theFCN = fcn;
      theParameters = par;
      theAmin = fcn.valueOf(par.params());
   }
   
   MnParameterScan(FCNBase fcn,  MnUserParameters par, double fval)
   {
      theFCN = fcn;
      theParameters = par;
      theAmin = fval;
   }
   
   List<Point> scan(int par)
   {
      return scan(par,41);
   }
   List<Point> scan(int par, int maxsteps)
   {
      return scan(par,maxsteps,0,0);
   }
   /** returns pairs of (x,y) points, x=parameter value, y=function value of FCN */
   List<Point> scan(int par, int maxsteps, double low, double high)
   {
      if(maxsteps > 101) maxsteps = 101;
      List<Point> result = new ArrayList<Point>(maxsteps+1);
      double[] params = theParameters.params();
      result.add(new Point(params[par], theAmin));
      
      if(low > high) return result;
      if(maxsteps < 2) return result;
      
      if(low == 0. && high == 0.)
      {
         low = params[par] - 2.*theParameters.error(par);
         high = params[par] + 2.*theParameters.error(par);
      }
      
      if(low == 0. && high == 0. && theParameters.parameter(par).hasLimits())
      {
         if(theParameters.parameter(par).hasLowerLimit())
            low = theParameters.parameter(par).lowerLimit();
         if(theParameters.parameter(par).hasUpperLimit())
            high = theParameters.parameter(par).upperLimit();
      }
      
      if(theParameters.parameter(par).hasLimits())
      {
         if(theParameters.parameter(par).hasLowerLimit())
            low = Math.max(low, theParameters.parameter(par).lowerLimit());
         if(theParameters.parameter(par).hasUpperLimit())
            high = Math.min(high, theParameters.parameter(par).upperLimit());
      }
      
      double x0 = low;
      double stp = (high - low)/(maxsteps - 1.);
      for(int i = 0; i < maxsteps; i++)
      {
         params[par] = x0 + ((double)i)*stp;
         double fval = theFCN.valueOf(params);
         if(fval < theAmin)
         {
            theParameters.setValue(par, params[par]);
            theAmin = fval;
         }
         result.add(new Point(params[par], fval));
      }
      
      return result;
   }
   
   MnUserParameters parameters()
   {
      return theParameters;
   }
   double fval()
   {
      return theAmin;
   }
   
   private FCNBase theFCN;
   private MnUserParameters theParameters;
   private double theAmin;
}
