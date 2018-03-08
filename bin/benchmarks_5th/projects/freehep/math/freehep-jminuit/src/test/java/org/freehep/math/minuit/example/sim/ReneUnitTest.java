package org.freehep.math.minuit.example.sim;

import java.util.List;
import junit.framework.TestCase;
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
 * @version $Id: ReneUnitTest.java 8584 2006-08-10 23:06:37Z duns $
 */
public class ReneUnitTest extends TestCase
{
   public ReneUnitTest(String testName)
   {
      super(testName);
   }
   
   public static junit.framework.Test suite()
   {
      
      junit.framework.TestSuite suite = new junit.framework.TestSuite(ReneUnitTest.class);
      return suite;
   }
   public void testRene()
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
       
       ReneTest.ReneFcn theFCN = new ReneTest.ReneFcn(measurements);
       
       MnUserParameters upar = new MnUserParameters();
       upar.add("p0", 100., 10.);
       upar.add("p1", 100., 10.);
       upar.add("p2", 100., 10.);
       upar.add("p3", 100., 10.);
       upar.add("p4", 1., 0.3);
       upar.add("p5", 1., 0.3);
       
       MnMigrad migrad = new MnMigrad(theFCN, upar);
       FunctionMinimum min = migrad.minimize();
       if(!min.isValid())
       {
          //try with higher strategy
          MnMigrad migrad2 = new MnMigrad(theFCN, min.userState(), new MnStrategy(2));
          min = migrad2.minimize();
       }
       assertTrue(min.isValid());
       assertEquals(306,min.nfcn());
       assertEquals(134.2,min.fval(),1e-1);
       assertEquals(2.258e-06,min.edm(),1e-9);
       
       assertEquals(32.036, min.userParameters().value(0),1e-4);
       assertEquals(98.1083, min.userParameters().value(1),1e-4);
       assertEquals(39.1555, min.userParameters().value(2),1e-4);
       assertEquals(362.439, min.userParameters().value(3),1e-3);
       assertEquals(0.0670762, min.userParameters().value(4),1e-7);
       assertEquals(1.00624, min.userParameters().value(5),1e-5);
       
       assertEquals(2.346, min.userParameters().error(0),1e-3);
       assertEquals(6.002, min.userParameters().error(1),1e-3);
       assertEquals(2.33, min.userParameters().error(2),1e-3);
       assertEquals(3.882, min.userParameters().error(3),1e-3);
       assertEquals(0.001061, min.userParameters().error(4),1e-6);
       assertEquals(0.0004247, min.userParameters().error(5),1e-7);
       {
          double[] params = {1,1,1,1,1,1};
          double[] error = {1,1,1,1,1,1};
          MnScan scan = new MnScan(theFCN, params, error);
          for(int i = 0; i < upar.variableParameters(); i++)
          {
             List<Point> xy = scan.scan(i);
          }
          assertEquals(3.,scan.parameters().value(0),1e-6);
          assertEquals(3.,scan.parameters().value(1),1e-6);
          assertEquals(3.,scan.parameters().value(2),1e-6);
          assertEquals(3.,scan.parameters().value(3),1e-6);
          assertEquals(0.1,scan.parameters().value(4),1e-6);
          assertEquals(1,scan.parameters().value(5),1e-6);          
       }
       
       {
          double[] params = {1,1,1,1,1,1};
          double[] error = {1,1,1,1,1,1};
          MnScan scan = new MnScan(theFCN, params, error);
          FunctionMinimum min2 = scan.minimize();
          assertEquals(3.,min2.userParameters().value(0),1e-6);
          assertEquals(3.,min2.userParameters().value(1),1e-6);
          assertEquals(3.,min2.userParameters().value(2),1e-6);
          assertEquals(3.,min2.userParameters().value(3),1e-6);
          assertEquals(0.1,min2.userParameters().value(4),1e-6);
          assertEquals(1,min2.userParameters().value(5),1e-6);
       }
   }
}
