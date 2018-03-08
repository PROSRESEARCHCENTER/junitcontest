package org.freehep.math.minuit.example.tutorial;

import org.freehep.math.minuit.FunctionMinimum;
import org.freehep.math.minuit.MnMigrad;
import org.freehep.math.minuit.MnUserParameters;

/**
 *
 * @version $Id: Quad4FMain.java 8584 2006-08-10 23:06:37Z duns $
 */
public class Quad4FMain
{
   public static void main(String[] args)
   {
      {
         Quad4F fcn = new Quad4F();
         MnUserParameters upar = new MnUserParameters();
         upar.add("x", 1., 0.1);
         upar.add("y", 1., 0.1);
         upar.add("z", 1., 0.1);
         upar.add("w", 1., 0.1);
         
         MnMigrad migrad = new MnMigrad(fcn, upar);
         FunctionMinimum min = migrad.minimize();
         System.out.println("minimum: "+min);
      }
   }
   
}
