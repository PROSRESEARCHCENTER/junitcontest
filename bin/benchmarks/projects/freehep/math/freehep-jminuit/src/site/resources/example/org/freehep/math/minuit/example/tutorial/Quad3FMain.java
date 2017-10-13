package org.freehep.math.minuit.example.tutorial;

import org.freehep.math.minuit.FunctionMinimum;
import org.freehep.math.minuit.MnMigrad;
import org.freehep.math.minuit.MnUserParameters;

/**
 *
 * @version $Id: Quad3FMain.java 8584 2006-08-10 23:06:37Z duns $
 */
public class Quad3FMain
{
   public static void main(String[] args)
   {
      //test constructor
      {
         // using migrad, numerical derivatives
         Quad3F fcn = new Quad3F();
         MnUserParameters upar = new MnUserParameters();
         upar.add("x", 1., 0.1);
         upar.add("y", 1., 0.1);
         upar.add("z", 1., 0.1);
         MnMigrad migrad = new MnMigrad(fcn, upar);
         migrad.setUseAnalyticalDerivatives(false);
         FunctionMinimum min = migrad.minimize();
         System.out.println("min= "+min);
      }
      {
         // using VariableMetricMinimizer, analytical derivatives
         Quad3F fcn = new Quad3F();
         MnUserParameters upar = new MnUserParameters();
         upar.add("x", 1., 0.1);
         upar.add("y", 1., 0.1);
         upar.add("z", 1., 0.1);
         MnMigrad migrad = new MnMigrad(fcn, upar);
         FunctionMinimum min = migrad.minimize();
         System.out.println("min= "+min);
      }
   }
   
}
