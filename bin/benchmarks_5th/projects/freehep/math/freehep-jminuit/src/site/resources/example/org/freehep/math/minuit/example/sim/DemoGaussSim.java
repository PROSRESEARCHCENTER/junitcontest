package org.freehep.math.minuit.example.sim;

import java.io.IOException;
import java.util.List;
import org.freehep.math.minuit.ContoursError;
import org.freehep.math.minuit.FunctionMinimum;
import org.freehep.math.minuit.MinosError;
import org.freehep.math.minuit.MnContours;
import org.freehep.math.minuit.MnMigrad;
import org.freehep.math.minuit.MnMinos;
import org.freehep.math.minuit.MnPlot;
import org.freehep.math.minuit.MnUserParameters;
import org.freehep.math.minuit.Point;

/**
 *
 * @version $Id: DemoGaussSim.java 8584 2006-08-10 23:06:37Z duns $
 */
public class DemoGaussSim
{
   public static void main(String[] args) throws IOException
   {
      // generate the data (100 data points)
      //GaussDataGen gdg = new GaussDataGen(100);
      GaussDataGen gdg = new GaussDataGen(DemoGaussSim.class.getResourceAsStream("GaussDataGen.txt"));
      
      double[] pos = gdg.positions();
      double[] meas = gdg.measurements();
      double[] var = gdg.variances();
      
      // create FCN function
      GaussFcn theFCN = new GaussFcn(meas, pos, var);
      
      // create initial starting values for parameters
      double x = 0.;
      double x2 = 0.;
      double norm = 0.;
      double dx = pos[1]-pos[0];
      double area = 0.;
      for( int i = 0; i < meas.length; i++)
      {
         norm += meas[i];
         x += (meas[i]*pos[i]);
         x2 += (meas[i]*pos[i]*pos[i]);
         area += dx*meas[i];
      }
      double mean = x/norm;
      double rms2 = x2/norm - mean*mean;
      double rms = rms2 > 0. ? Math.sqrt(rms2) : 1.;
      
      System.out.printf("%g %g %g\n",mean,rms,area);
      
      {
         // demonstrate minimal required interface for minimization
         // create Minuit parameters without names
         
         // starting values for parameters
         double[] init_par = { mean, rms, area };
         
         // starting values for initial uncertainties
         double[] init_err = { 0.1, 0.1, 0.1 };
         
         // create minimizer (default constructor)
         MnMigrad migrad = new MnMigrad(theFCN, init_par, init_err);
         
         // minimize
         FunctionMinimum min = migrad.minimize();
         
         // output
         System.out.println("minimum: "+min);
      }
      {
         // demonstrate standard minimization using MIGRAD
         // create Minuit parameters with names
         MnUserParameters upar = new MnUserParameters();
         upar.add("mean", mean, 0.1);
         upar.add("sigma", rms, 0.1);
         upar.add("area", area, 0.1);
         
         // create MIGRAD minimizer
         MnMigrad migrad = new MnMigrad(theFCN, upar);
         
         // minimize
         FunctionMinimum min = migrad.minimize();
         
         // output
         System.out.println("minimum: "+min);
      }
      {
         // demonstrate full interaction with parameters over subsequent
         // minimizations
         
         // create Minuit parameters with names
         MnUserParameters upar = new MnUserParameters();
         upar.add("mean", mean, 0.1);
         upar.add("sigma", rms, 0.1);
         upar.add("area", area, 0.1);
         
         // access parameter by name to set limits...
         upar.setLimits("mean", mean-0.01, mean+0.01);
         
         // ... or access parameter by index
         upar.setLimits(1, rms-0.1, rms+0.1);
         
         // create Migrad minimizer
         MnMigrad migrad = new MnMigrad(theFCN, upar);
         
         // fix a parameter...
         migrad.fix("mean");
         
         // ... and minimize
         FunctionMinimum min = migrad.minimize();
         
         // output
         System.out.println("minimum: "+min);
         
         // release a parameter...
         migrad.release("mean");
         
         // ... and fix another one
         migrad.fix(1);
         
         // and minimize again
         FunctionMinimum min1 = migrad.minimize();
         
         // output
         System.out.println("minimum: "+min1);
         
         // release the parameter...
         migrad.release(1);
         
         // ... and minimize with all three parameters (still with limits!)
         FunctionMinimum min2 = migrad.minimize();
         
         // output
         System.out.println("minimum: "+min2);
         
         // remove all limits on parameters...
         migrad.removeLimits("mean");
         migrad.removeLimits("sigma");
         
         // ... and minimize again with all three parameters (now without limits!)
         FunctionMinimum min3 = migrad.minimize();
         
         // output
         System.out.println("minimum: "+min3);
         
      }
      {
         // test single sided limits
         MnUserParameters upar = new MnUserParameters();
         upar.add("mean", mean, 0.1);
         upar.add("sigma", rms-1., 0.1);
         upar.add("area", area, 0.1);
         
         // test lower limits
         upar.setLowerLimit("mean", mean-0.01);
         
         // test upper limits
         upar.setUpperLimit("sigma", rms-0.5);
         
         // create MIGRAD minimizer
         MnMigrad migrad = new MnMigrad(theFCN, upar);
         
         // ... and minimize
         FunctionMinimum min = migrad.minimize();
         System.out.println("test lower limit minimim= "+min);
      }
      {
         // demonstrate MINOS error analysis
         
         // create Minuit parameters with names
         MnUserParameters upar = new MnUserParameters();
         upar.add("mean", mean, 0.1);
         upar.add("sigma", rms, 0.1);
         upar.add("area", area, 0.1);
         
         // create Migrad minimizer
         MnMigrad migrad = new MnMigrad(theFCN, upar);
         
         // minimize
         FunctionMinimum min = migrad.minimize();
         
         // create MINOS error factory
         MnMinos minos = new MnMinos(theFCN, min);
         
         {
            // 1-sigma MINOS errors (minimal interface)  
            // output
            System.out.println("1-sigma minos errors: ");
            System.out.printf("par0: %g %g %g\n",min.userState().value("mean"),minos.lower(0),minos.upper(0));
            System.out.printf("par1: %g %g %g\n",min.userState().value(1),minos.lower(1),minos.upper(1));
            System.out.printf("par2: %g %g %g\n",min.userState().value("area"),minos.lower(2),minos.upper(2));
         }
         
         {
            // 2-sigma MINOS errors (rich interface)
            MinosError e0 = minos.minos(0,4.);
            MinosError e1 = minos.minos(1,4.);
            MinosError e2 = minos.minos(2,4.);
            
            // output
            System.out.println("2-sigma minos errors: ");
            System.out.println(e0);
            System.out.println(e1);
            System.out.println(e2);
         }
      }
      
      {
         // demonstrate how to use the CONTOURs
         
         // create Minuit parameters with names
         MnUserParameters upar = new MnUserParameters();
         upar.add("mean", mean, 0.1);
         upar.add("sigma", rms, 0.1);
         upar.add("area", area, 0.1);
         
         // create Migrad minimizer
         MnMigrad migrad = new MnMigrad(theFCN, upar);
         
         // minimize
         FunctionMinimum min = migrad.minimize();
         
         // create contours factory with FCN and minimum
         MnContours contours = new MnContours(theFCN, min);
         
         //70% confidence level for 2 parameters contour around the minimum
         // (minimal interface)
         List<Point> cont = contours.points(0, 1, 2.41, 20);
         
         //95% confidence level for 2 parameters contour
         // (rich interface)
         ContoursError cont4 = contours.contour(0, 1, 5.99, 20);
         
         // plot the contours
         MnPlot plot = new MnPlot();
         cont.addAll(cont4.points());
         plot.plot(min.userState().value("mean"), min.userState().value("sigma"), cont);
         
         // print out one contour
         System.out.println(cont4);
      }
      
   }
}
