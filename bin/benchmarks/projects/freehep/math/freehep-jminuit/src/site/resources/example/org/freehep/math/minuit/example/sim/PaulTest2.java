package org.freehep.math.minuit.example.sim;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import org.freehep.math.minuit.FunctionMinimum;
import org.freehep.math.minuit.MinosError;
import org.freehep.math.minuit.MnMigrad;
import org.freehep.math.minuit.MnMinos;
import org.freehep.math.minuit.MnUserParameters;

/**
 *
 * @version $Id: PaulTest2.java 8584 2006-08-10 23:06:37Z duns $
 */
public class PaulTest2
{
   public static void main(String[] args) throws IOException
   {
      List<Double> positions = new ArrayList<Double>();
      List<Double> measurements = new ArrayList<Double>();
      List<Double> var = new ArrayList<Double>();
      int nmeas = 0;
      
      Scanner in = new Scanner(PaulTest.class.getResourceAsStream("paul2.txt"));
      
      // read input data
      {
         while(in.hasNextDouble())
         {
            double x = in.nextDouble();
            double y = in.nextDouble();
            double width = in.nextDouble();
            double err = in.nextDouble();
            double unl = in.nextDouble();
            double un2 = in.nextDouble();
            if(err < 1.e-8) continue;
            positions.add(x);
            measurements.add(y);
            var.add(err*err);
            nmeas += y;
         }
         System.out.printf("size= %d\n",var.size());
         assert(var.size() > 0);
         System.out.printf("nmeas: %d\n",nmeas);
      }
      double[] m = new double[var.size()];
      double[] p = new double[var.size()];
      double[] v = new double[var.size()];
      for (int i=0; i<var.size(); i++)
      {
         m[i] = measurements.get(i);
         p[i] = positions.get(i);
         v[i] = var.get(i);
      }
      
      // create FCN function
      GaussFcn theFCN = new GaussFcn(m, p, v);
      
      double[] meas = theFCN.measurements();
      double[] pos = theFCN.positions();
      
      // create initial starting values for parameters
      double x = 0.;
      double x2 = 0.;
      double norm = 0.;
      double area = 0.;
      double dx = pos[1]-pos[0];
      for(int i = 0; i < meas.length; i++)
      {
         norm += meas[i];
         x += (meas[i]*pos[i]);
         x2 += (meas[i]*pos[i]*pos[i]);
         area += dx*meas[i];
      }
      double mean = x/norm;
      double rms2 = x2/norm - mean*mean;
      
      System.out.printf("initial mean: %g\n",mean);
      System.out.printf("initial sigma: %g\n",Math.sqrt(rms2));
      System.out.printf("initial area: %g\n",area);
      double[] init_val = { mean, Math.sqrt(rms2), area};
      System.out.printf("initial fval: %g\n",theFCN.valueOf(init_val));
      
      MnUserParameters upar = new MnUserParameters();
      upar.add("mean", mean, 1.);
      upar.add("sigma", Math.sqrt(rms2), 1.);
      upar.add("area", area, 10.);
      
      MnMigrad migrad = new MnMigrad(theFCN, upar);
      System.out.println("start migrad");
      FunctionMinimum min = migrad.minimize();
      System.out.println("minimum: "+min);
      
      System.out.println("start minos");
      MnMinos minos = new MnMinos(theFCN, min);
      MinosError e0 = minos.minos(0);
      MinosError e1 = minos.minos(1);
      MinosError e2 = minos.minos(2);
      
      System.out.printf("par0: %g %g %g\n",min.userState().value("mean"),e0.lower(),e0.upper());
      System.out.printf("par1: %g %g %g\n",min.userState().value("sigma"),e1.lower(),e1.upper());
      System.out.printf("par2: %g %g %g\n",min.userState().value("area"),e2.lower(),e2.upper());  
   }
   
}
