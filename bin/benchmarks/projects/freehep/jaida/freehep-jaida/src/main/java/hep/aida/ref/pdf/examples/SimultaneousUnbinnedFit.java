package hep.aida.ref.pdf.examples;

import hep.aida.*;
import java.util.Random;
import hep.aida.ref.pdf.*;

public class SimultaneousUnbinnedFit
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
    
      ICloud1D c1 = histogramFactory.createCloud1D("c1");
      ICloud1D c2 = histogramFactory.createCloud1D("c2");

      IHistogram1D h1 = histogramFactory.createHistogram1D("h1",50,-10,10);
      IHistogram1D h2 = histogramFactory.createHistogram1D("h2",50,-10,10);

      Random r = new Random();

      for (int i=0; i<100000; i++) {
          double x = r.nextGaussian();
          c1.fill(x);
          h1.fill(x);
      }

      for (int i=0; i<100; i++) {
          double x = 3+r.nextGaussian();
          c2.fill(x);
          h2.fill(x);
      }

      double h1Norm = h1.sumBinHeights()*(h1.axis().upperEdge()-h1.axis().lowerEdge())/h1.axis().bins();
      h1.scale(1./h1Norm);
      double h2Norm = h2.sumBinHeights()*(h2.axis().upperEdge()-h2.axis().lowerEdge())/h2.axis().bins();
      h2.scale(1./h2Norm);

      Parameter mean1 = new Parameter("mean1",0,0.1);
      Parameter mean2 = new Parameter("mean2",3,0.1);
      Parameter sigma1 = new Parameter("sigma1",1,0.1);
      Parameter sigma2 = new Parameter("sigma2",1,0.1);
      Dependent x = new Dependent("x", c1.lowerEdge(), c1.upperEdge());
      
      
      Gaussian g1    = new Gaussian("g1",x,mean1,sigma1);
      Gaussian g2 = new Gaussian("g2",x,mean2,sigma2);
      g1.getNormalizationParameter().setValue(h1.maxBinHeight());
      g2.getNormalizationParameter().setValue(h2.maxBinHeight());

      Parameter mean1Sim = new Parameter("mean1Sim",0,0.1);
      Parameter mean2Sim = new Parameter("mean2Sim",3,0.1);
      Parameter sigma1Sim = new Parameter("sigma1Sim",1,0.1);

      Gaussian g1Sim = new Gaussian("g1Sim",x,mean1Sim,sigma1Sim);
      Gaussian g2Sim = new Gaussian("g2Sim",x,mean2Sim,sigma1Sim);
      
      plotter.createRegions(2,2);
      plotter.region(0).plot(h1);
      plotter.region(2).plot(h2);
      plotter.region(1).plot(h1);
      plotter.region(3).plot(h2);

      PdfFitter fitter = new PdfFitter("uml","minuit");
      
      fitter.fit(c1, g1);
      fitter.fit(c2, g2);
      fitter.fit( new Object[] {c1, c2}, new Function[] {g1Sim, g2Sim} );

      plotter.region(0).plot(g1);
      plotter.region(2).plot(g2);
      plotter.region(1).plot(g1Sim);
      plotter.region(3).plot(g2Sim);
      plotter.show();
      
   }
}