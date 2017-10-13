package org.freehep.math.minuit.example.tutorial;

import org.freehep.math.minuit.FunctionMinimum;
import org.freehep.math.minuit.MinosError;
import org.freehep.math.minuit.MnMigrad;
import org.freehep.math.minuit.MnMinos;
import org.freehep.math.minuit.MnUserParameters;

/**
 *
 * @version $Id: Quad1FMain.java 8584 2006-08-10 23:06:37Z duns $
 */
public class Quad1FMain
{
   public static void main(String[] args)
   {   
      {
         //test constructor
         Quad1F fcn = new Quad1F();
         MnUserParameters upar = new MnUserParameters();
         upar.add("x", 1., 0.1);
         MnMigrad migrad = new MnMigrad(fcn, upar);
         migrad.setUseAnalyticalDerivatives(false);
         FunctionMinimum min = migrad.minimize();
         System.out.println("min= "+min);
      }
      {
         // using VariableMetricMinimizer, analytical derivatives
         Quad1F fcn = new Quad1F();
         MnUserParameters upar = new MnUserParameters();
         upar.add("x", 1., 0.1);
         MnMigrad migrad = new MnMigrad(fcn, upar);
         FunctionMinimum min = migrad.minimize();
         System.out.println("min= "+min);
      }
      {
         Quad1F fcn = new Quad1F();
         MnUserParameters upar = new MnUserParameters();
         upar.add("x", 1., 0.1);
         MnMigrad migrad = new MnMigrad(fcn, upar);
         FunctionMinimum min = migrad.minimize();
         MnMinos minos = new MnMinos(fcn, min);
         MinosError me = minos.minos(0);
         System.out.printf("par0: %g %g %g\n",min.userState().value(0),me.lower(),me.upper());
         
         MnMinos minos2 = new MnMinos(fcn, min);
         MinosError me2 = minos.minos(0, 4.);
         System.out.printf("par0: %g %g %g\n",min.userState().value(0),me2.lower(),me2.upper());
      }
      
   }
   
}
