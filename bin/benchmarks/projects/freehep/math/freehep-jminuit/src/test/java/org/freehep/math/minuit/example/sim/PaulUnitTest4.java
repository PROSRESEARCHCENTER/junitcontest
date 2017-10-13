package org.freehep.math.minuit.example.sim;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import junit.framework.TestCase;
import org.freehep.math.minuit.FCNBase;
import org.freehep.math.minuit.FunctionMinimum;
import org.freehep.math.minuit.MnMigrad;
import org.freehep.math.minuit.MnUserParameters;
import org.freehep.math.minuit.MnSimplex;

/**
 *
 * @version $Id: PaulUnitTest4.java 8584 2006-08-10 23:06:37Z duns $
 */
public class PaulUnitTest4 extends TestCase
{
   private double[] m,p,v;
   public PaulUnitTest4(String testName)
   {
      super(testName);
   }
   
   public static junit.framework.Test suite()
   {
      
      junit.framework.TestSuite suite = new junit.framework.TestSuite(PaulUnitTest4.class);
      return suite;
   }
   protected void setUp() throws java.lang.Exception
   {
      List<Double> positions = new ArrayList<Double>();
      List<Double> measurements = new ArrayList<Double>();
      List<Double> var = new ArrayList<Double>();
      
      Scanner in = new Scanner(PaulTest.class.getResourceAsStream("paul4.txt"));
      
      // read input data
      {
         while(in.hasNextDouble())
         {
            double x = in.nextDouble();
            double y = in.nextDouble();
            double err = in.nextDouble();
            
            positions.add(x);
            measurements.add(y);
            var.add(err*err);
         }
      }
      m = new double[var.size()];
      p = new double[var.size()];
      v = new double[var.size()];
      for (int i=0; i<var.size(); i++)
      {
         m[i] = measurements.get(i);
         p[i] = positions.get(i);
         v[i] = var.get(i);
      }
   }
   public void testPaul4Chi2()
   {
      // create Chi2 FCN function
      PaulTest4.PowerLawChi2FCN theFCN = new PaulTest4.PowerLawChi2FCN(m, p, v);

      MnUserParameters upar = new MnUserParameters();
      upar.add("p0", -2.3, 0.2);
      upar.add("p1", 1100., 10.);

      MnMigrad migrad = new MnMigrad(theFCN, upar);
      FunctionMinimum min = migrad.minimize();
      if(!min.isValid())
      {
         migrad = new MnMigrad(theFCN, upar, 2);
         min = migrad.minimize();
      }
      assertTrue(min.isValid());
      assertEquals(102,min.nfcn());
      assertEquals(95.243,min.fval(),1e-3);
      assertEquals(3.7209e-11,min.edm(),1e-15);
      
      assertEquals(-2.10019, min.userParameters().value(0),1e-5);
      assertEquals(999.225, min.userParameters().value(1),1e-3);
      
      assertEquals(0.0001592, min.userParameters().error(0),1e-7);
      assertEquals(0.8073, min.userParameters().error(1),1e-4);
      
      assertEquals(2.53567e-08,min.userCovariance().get(0,0),1e-13);
      assertEquals(0.000127332,min.userCovariance().get(1,0),1e-9);
      assertEquals(0.651708,min.userCovariance().get(1,1),1e-6);
   }
   public void testPaul4LogLike()
   {

      // create LogLikelihood FCN function
      PaulTest4.PowerLawLogLikeFCN theFCN = new PaulTest4.PowerLawLogLikeFCN(m, p);

      MnUserParameters upar = new MnUserParameters();
      upar.add("p0", -2.1, 0.2);
      upar.add("p1", 1000., 10.);

      MnMigrad migrad = new MnMigrad(theFCN, upar);
      migrad.setErrorDef(0.5);
      FunctionMinimum min = migrad.minimize();
      if(!min.isValid())
      {
         //try with higher strategy
         migrad = new MnMigrad(theFCN, upar, 2);
         min = migrad.minimize();
      }
      assertTrue(min.isValid());
      assertEquals(63,min.nfcn());
      assertEquals(-1.33678e+09,min.fval(),1e4);
      assertEquals(0.0170964,min.edm(),1e-4);
      
      assertEquals(-2.10016, min.userParameters().value(0),1e-5);
      assertEquals(999.394, min.userParameters().value(1),1e-3);
      
      assertEquals(0.0001488, min.userParameters().error(0),1e-7);
      assertEquals(0.7544, min.userParameters().error(1),1e-4);
      
      assertEquals(2.21365e-08,min.userCovariance().get(0,0),1e-13);
      assertEquals(0.000111025,min.userCovariance().get(1,0),1e-9);
      assertEquals(0.569138,min.userCovariance().get(1,1),1e-6);
   }
   public void testPaul4Simplex()
   {
      PaulTest4.PowerLawChi2FCN chi2 = new PaulTest4.PowerLawChi2FCN(m, p, v);
      PaulTest4.PowerLawLogLikeFCN mlh = new PaulTest4.PowerLawLogLikeFCN(m, p);

      MnUserParameters upar;
      double[] par = {-2.3, 1100.};
      double[] err = { 1., 1.};

      MnSimplex simplex = new MnSimplex(chi2, par, err);

      FunctionMinimum min = simplex.minimize();
      assertTrue(min.isValid());
      assertEquals(105,min.nfcn());
      assertEquals(95.2506,min.fval(),1e-4);
      assertEquals(0.0286894,min.edm(),1e-7);
      
      assertEquals(-2.10018, min.userParameters().value(0),1e-5);
      assertEquals(999.286, min.userParameters().value(1),1e-3);
      
      assertEquals(0.0001122, min.userParameters().error(0),1e-7);
      assertEquals(0.7701, min.userParameters().error(1),1e-4);

      MnSimplex simplex2 = new MnSimplex(mlh, par, err);   
      simplex2.setErrorDef(0.5);
      min = simplex2.minimize();
      assertTrue(min.isValid());
      assertEquals(84,min.nfcn());
      assertEquals(-1.337e+09,min.fval(),1e6);
      assertEquals(0.03377,min.edm(),1e-5);
      
      assertEquals(-2.10018, min.userParameters().value(0),1e-5);
      assertEquals(999.279, min.userParameters().value(1),1e-3);
      
      assertEquals(0.0001793, min.userParameters().error(0),1e-7);
      assertEquals(0.7549, min.userParameters().error(1),1e-4);
   }
} 
