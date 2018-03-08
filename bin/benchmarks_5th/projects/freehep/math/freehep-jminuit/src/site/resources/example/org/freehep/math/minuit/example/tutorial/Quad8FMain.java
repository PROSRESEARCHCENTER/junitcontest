package org.freehep.math.minuit.example.tutorial;

import org.freehep.math.minuit.FunctionMinimum;
import org.freehep.math.minuit.MnMigrad;
import org.freehep.math.minuit.MnUserParameters;

/**
 *
 * @version $Id: Quad8FMain.java 8584 2006-08-10 23:06:37Z duns $
 */
public class Quad8FMain
{
   public static void main(String[] args)
   {
      Quad8F fcn = new Quad8F();
      {
         //test constructor
         MnUserParameters upar = new MnUserParameters();
         upar.add("x", 1., 0.1);
         upar.add("y", 1., 0.1);
         upar.add("z", 1., 0.1);
         upar.add("w", 1., 0.1);
         upar.add("x0", 1., 0.1);
         upar.add("y0", 1., 0.1);
         upar.add("z0", 1., 0.1);
         upar.add("w0", 1., 0.1);
         
         MnMigrad migrad = new MnMigrad(fcn, upar);
         FunctionMinimum min = migrad.minimize();
         System.out.println("minimum: "+min);
      }
   }
}