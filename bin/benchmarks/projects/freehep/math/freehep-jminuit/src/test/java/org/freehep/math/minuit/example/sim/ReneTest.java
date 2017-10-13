package org.freehep.math.minuit.example.sim;

import java.util.List;
import org.freehep.math.minuit.FCNBase;
import org.freehep.math.minuit.FunctionMinimum;
import org.freehep.math.minuit.MnMigrad;
import org.freehep.math.minuit.MnPlot;
import org.freehep.math.minuit.MnScan;
import org.freehep.math.minuit.MnStrategy;
import org.freehep.math.minuit.MnUserParameters;
import org.freehep.math.minuit.Point;
/**
 *
 * @version $Id: ReneTest.java 8584 2006-08-10 23:06:37Z duns $
 */
public class ReneTest
{
   public static void main(String[] args)
   {
      double[] tmp =
      {38.,36.,46.,52.,54.,52.,61.,52.,64.,77.,
       60.,56.,78.,71.,81.,83.,89.,96.,118.,96.,
       109.,111.,107.,107.,135.,156.,196.,137.,
       160.,153.,185.,222.,251.,270.,329.,422.,
       543.,832.,1390.,2835.,3462.,2030.,1130.,
       657.,469.,411.,375.,295.,281.,281.,289.,
       273.,297.,256.,274.,287.,280.,274.,286.,
       279.,293.,314.,285.,322.,307.,313.,324.,
       351.,314.,314.,301.,361.,332.,342.,338.,
       396.,356.,344.,395.,416.,406.,411.,422.,
       393.,393.,409.,455.,427.,448.,459.,403.,
       441.,510.,501.,502.,482.,487.,506.,506.,
       526.,517.,534.,509.,482.,591.,569.,518.,
       609.,569.,598.,627.,617.,610.,662.,666.,
       652.,671.,647.,650.,701.};
       
       double[] measurements = tmp.clone();
       
       ReneFcn theFCN = new ReneFcn(measurements);
       
       MnUserParameters upar = new MnUserParameters();
       upar.add("p0", 100., 10.);
       upar.add("p1", 100., 10.);
       upar.add("p2", 100., 10.);
       upar.add("p3", 100., 10.);
       upar.add("p4", 1., 0.3);
       upar.add("p5", 1., 0.3);
       
       System.out.println("Initial parameters: "+upar);
       
       System.out.println("start migrad");
       MnMigrad migrad = new MnMigrad(theFCN, upar);
       FunctionMinimum min = migrad.minimize();
       if(!min.isValid())
       {
          //try with higher strategy
          System.out.println("FM is invalid, try with strategy = 2.");
          MnMigrad migrad2 = new MnMigrad(theFCN, min.userState(), new MnStrategy(2));
          min = migrad2.minimize();
       }
       System.out.println("minimum: "+min);
       {
          double[] params = {1,1,1,1,1,1};
          double[] error = {1,1,1,1,1,1};
          MnScan scan = new MnScan(theFCN, params, error);
          System.out.println("scan parameters: "+scan.parameters());
          MnPlot plot = new MnPlot();
          for(int i = 0; i < upar.variableParameters(); i++)
          {
             List<Point> xy = scan.scan(i);
             plot.plot(xy);
          }
          System.out.println("scan parameters: "+scan.parameters());
       }
       
       {
          double[] params = {1,1,1,1,1,1};
          double[] error = {1,1,1,1,1,1};
          MnScan scan = new MnScan(theFCN, params, error);
          System.out.println("scan parameters: "+scan.parameters());
          FunctionMinimum min2 = scan.minimize();
          //     std::cout<<min<<std::endl;
          System.out.println("scan parameters: "+scan.parameters());
       }
       
   }
   static class ReneFcn implements FCNBase
   {
      ReneFcn(double[] meas)
      {
         theMeasurements = meas;
      }
      public double errorDef()
      {
         return 1;
      }
      
      public double valueOf(double[] par)
      {
         double a = par[2];
         double b = par[1];
         double c = par[0];
         double p0 = par[3];
         double p1 = par[4];
         double p2 = par[5];
         double fval = 0.;
         for( int i = 0; i < theMeasurements.length; i++)
         {
            double ni = theMeasurements[i];
            if(ni < 1.e-10) continue;
            double xi = (i+1.)/40. - 1./80.; //xi=0-3
            double ei = ni;
            double nexp = a*xi*xi + b*xi + c + (0.5*p0*p1/Math.PI)/Math.max(1.e-10, (xi-p2)*(xi-p2) + 0.25*p1*p1);
            fval += (ni-nexp)*(ni-nexp)/ei;
         }
         return fval;
      }
      private double[] theMeasurements;
   }
}
