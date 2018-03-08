package org.freehep.math.minuit.example.tutorial;

import java.util.List;
import org.freehep.math.minuit.FunctionMinimum;
import org.freehep.math.minuit.MnContours;
import org.freehep.math.minuit.MnMigrad;
import org.freehep.math.minuit.MnUserParameters;
import org.freehep.math.minuit.Point;

/**
 *
 * @version $Id: Quad2FMain.java 8584 2006-08-10 23:06:37Z duns $
 */
public class Quad2FMain
{
   public static void main(String[] args)
   {
      {
         //test constructor
         Quad2F fcn = new Quad2F();
         MnUserParameters upar = new MnUserParameters();
         upar.add("x", 1., 0.1);
         upar.add("y", 1., 0.1);
         MnMigrad migrad = new MnMigrad(fcn, upar);
         migrad.setUseAnalyticalDerivatives(false);
         FunctionMinimum min = migrad.minimize();
         System.out.println("min= "+min);
      }
      {
         // using VariableMetricMinimizer, analytical derivatives
         Quad2F fcn = new Quad2F();
         MnUserParameters upar = new MnUserParameters();
         upar.add("x", 1., 0.1);
         upar.add("y", 1., 0.1);
         MnMigrad migrad = new MnMigrad(fcn, upar);
         FunctionMinimum min = migrad.minimize();
         System.out.println("min= "+min);
      }
      {
         // test Contours for two parameters
         Quad2F fcn = new Quad2F();
         MnUserParameters upar = new MnUserParameters();
         upar.add("x", 1., 0.1);
         upar.add("y", 1., 0.1);
         MnMigrad migrad = new MnMigrad(fcn, upar);
         FunctionMinimum min = migrad.minimize();
         MnContours contours = new MnContours(fcn, min);
         //1-sigma around the minimum
         List<Point> cont = contours.points(0, 1, 1, 20);
         // the minimum
         System.out.println("1-sigma contours");
         for(Point ipair : cont)
         {
            System.out.printf("x,y %g,%g\n",ipair.first,ipair.second);
         }
         
         //2-sigma around the minimum
         System.out.println("2-sigma contours");
         List<Point> cont4 = contours.points(0, 1, 4, 20);
         for(Point ipair : cont4)
         {
            System.out.printf("x,y %g,%g\n",ipair.first,ipair.second);
         }
      }
   }
}
