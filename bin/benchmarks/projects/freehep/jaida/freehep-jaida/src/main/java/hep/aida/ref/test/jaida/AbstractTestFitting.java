package hep.aida.ref.test.jaida;

import hep.aida.IAnalysisFactory;
import hep.aida.IFitFactory;
import hep.aida.IFitResult;
import hep.aida.IFitter;
import hep.aida.IFunction;
import hep.aida.IFunctionFactory;
import hep.aida.IHistogram1D;
import hep.aida.IHistogramFactory;
import hep.aida.ITree;

import java.util.Random;

import junit.framework.TestCase;


/**
 * 
 * @author AIDA Team
 * @version $Id: AbstractTestFitting.java 8584 2006-08-10 23:06:37Z duns $
 */
public class AbstractTestFitting extends TestCase
{
   private IAnalysisFactory af;
   private ITree tree;
   private IHistogram1D hist;
   private String fitter;
   
   /** Creates a new instance of FitTest */
   public AbstractTestFitting(String name,String fitter)
   {
      super(name);
      this.fitter = fitter;
   }
   public void testScriptedFunction()
   {
      IFunctionFactory functionfact = af.createFunctionFactory(tree);
      IFunction f = functionfact.createFunctionFromScript("gauss",1,"b*lambda*exp(-x[0]*lambda)+a*exp(-(x[0]-mean)*(x[0]-mean)/sigma/sigma)","b,lambda,a,mean,sigma","Gaussian+Exponential");
      assertEquals(1,f.dimension());
      assertEquals(5,f.parameters().length);
      f.setParameter("b",1800);
      f.setParameter("lambda",.3);
      f.setParameter("a",70);
      f.setParameter("mean",10);
      f.setParameter("sigma",1.5);
      
      testFunction(fitter,f);
   }
   public void testBuiltinFunction()
   {
      IFunctionFactory functionfact = af.createFunctionFactory(tree);
      IFunction f = functionfact.createFunctionByName("e+g","e+g");
      assertEquals(1,f.dimension());
      assertEquals(5,f.parameters().length);
      f.setParameter("amplitude",600);
      f.setParameter("exponent",-.3);
      f.setParameter("amplitude_1",80);
      f.setParameter("mean",10);
      f.setParameter("sigma",1);
      
      testFunction(fitter,f);
   }

   private void testFunction(String fitter, IFunction f)
   {
      IFitFactory ff = af.createFitFactory();
      IFitter minuit = ff.createFitter("Chi2",fitter);
      IFitResult result = minuit.fit(hist,f);
      
      assertTrue(result.isValid());
      assertEquals(5,result.fittedParameterNames().length);
      //assertEquals(95,result.ndf());
      int status = result.fitStatus();
      assertTrue(status>0 && status<6);
      assertTrue(result.quality()<1.5);
   }
   
   protected void setUp() throws java.lang.Exception
   {
      af = IAnalysisFactory.create();
      tree = af.createTreeFactory().create();
      IHistogramFactory hf = af.createHistogramFactory(tree);

      Random r = new Random();
      
      hist = hf.createHistogram1D("hist",100,0,20);

      for (int i=0; i<10000; i++)
      {
         hist.fill(-Math.log(r.nextDouble()) / .3);
      }
      for (int i=0; i<1000; i++)
      {
         hist.fill(r.nextGaussian()+10);
      }
   }   
}
