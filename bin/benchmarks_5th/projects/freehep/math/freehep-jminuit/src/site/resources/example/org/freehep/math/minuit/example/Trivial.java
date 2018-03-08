package org.freehep.math.minuit.example;

import org.freehep.math.minuit.FCNBase;
import org.freehep.math.minuit.FunctionMinimum;
import org.freehep.math.minuit.MnMigrad;
import org.freehep.math.minuit.MnUserParameters;

public class Trivial
{
   public static void main(String[] args)
   {
      FCNBase myFunction = new FCNBase()
      {
         public double valueOf(double[] par)
         {
            double x = par[0];
            return 1 + x*x;
         }
      };
      MnUserParameters myParameters = new MnUserParameters();
      myParameters.add("x", 1., 0.1);
      
      MnMigrad migrad = new MnMigrad(myFunction, myParameters);
      FunctionMinimum min = migrad.minimize();
      
      System.out.printf("Minimum value is %g found using %d function calls",
         min.fval(),min.nfcn());
   }
}