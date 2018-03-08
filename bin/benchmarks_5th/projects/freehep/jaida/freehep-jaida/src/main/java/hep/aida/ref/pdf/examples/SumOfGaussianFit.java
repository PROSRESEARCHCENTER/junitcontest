package hep.aida.ref.pdf.examples;

import hep.aida.*;
import java.util.Random;
import hep.aida.ref.pdf.*;
import hep.aida.ref.function.*;

public class SumOfGaussianFit
{
   public static void main(String[] args)
   {
      // Create factories
      IAnalysisFactory  analysisFactory = IAnalysisFactory.create();
      ITreeFactory      treeFactory = analysisFactory.createTreeFactory();
      ITree             tree = treeFactory.create();
      IPlotter          plotter = analysisFactory.createPlotterFactory().create("Plotter");
      IHistogramFactory histogramFactory = analysisFactory.createHistogramFactory(tree);
      IFunctionFactory  functionFactory = analysisFactory.createFunctionFactory(tree);
      IFitFactory       fitFactory = analysisFactory.createFitFactory();
    
      IHistogram1D h1 = histogramFactory.createHistogram1D("Histogram 1D",50,-10,10);
      ICloud1D c1 = histogramFactory.createCloud1D("Cloud 1D");

      Random r_1 = new Random(123);
      Random r_2 = new Random(456);

      for (int i=0; i<10000; i++) {
          double x = r_1.nextGaussian();
          if ( r_1.nextDouble() < 0.2 )
              x += 3*r_2.nextGaussian();
          h1.fill(x);
          c1.fill(x);
      }

      Dependent x = new Dependent("x",-20,20);
      Parameter m1 = new Parameter("mean1",h1.mean(),0.01);
      Parameter s1 = new Parameter("sigma1",1);
      Parameter m2 = new Parameter("mean2",h1.mean(),0.01);
      Parameter s2 = new Parameter("sigma2",3);

      //Create two gaussians
      Gaussian gauss1 = new Gaussian("myGauss1", x, m1, s1);
      Gaussian gauss2 = new Gaussian("myGauss2", x, m2, s2);
      
      //Add the gaussians
      Parameter f0 = new Parameter("f0", 0.2, 0, 1);
      Sum sum = new Sum("Sum of Gauss",gauss1, gauss2,f0);
      sum.getNormalizationParameter().setValue(h1.maxBinHeight());

      IRangeSet r1 = sum.normalizationRange(0);
      r1.excludeAll();
      r1.include(c1.lowerEdge(),c1.upperEdge());



      IFitter fitter = fitFactory.createFitter("chi2","jminuit","noClone=true");
      fitter.setUseFunctionGradient(false);
      IFitResult result = fitter.fit(h1,sum);
      System.out.println("Quality: "+result.quality());

      double h1Norm = h1.sumBinHeights()*(h1.axis().upperEdge()-h1.axis().lowerEdge())/h1.axis().bins();
      h1.scale(1./h1Norm);

      plotter.region(0).plot(h1);
      plotter.region(0).plot(sum);
      plotter.region(0).plot(gauss1);
      plotter.region(0).plot(gauss2);
      plotter.show();

   }
}