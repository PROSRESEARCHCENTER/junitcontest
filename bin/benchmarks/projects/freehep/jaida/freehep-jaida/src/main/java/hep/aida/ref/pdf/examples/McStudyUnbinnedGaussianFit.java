package hep.aida.ref.pdf.examples;

//package hep.aida.ref.pdf.examples;

import hep.aida.*;
import java.util.Random;
import hep.aida.ref.pdf.*;
import hep.aida.ref.function.*;
import hep.aida.ref.histogram.HistUtils;

public class McStudyUnbinnedGaussianFit
{
   public static void main(String[] args)
   {
      // Create factories
      IAnalysisFactory  analysisFactory = IAnalysisFactory.create();
      ITreeFactory      treeFactory = analysisFactory.createTreeFactory();
      ITree             tree = treeFactory.create();
      IPlotter          plotter = analysisFactory.createPlotterFactory().create("Plotter");
      ITupleFactory     tupleFactory = analysisFactory.createTupleFactory(tree);
      IHistogramFactory histogramFactory = analysisFactory.createHistogramFactory(tree);
      IFunctionFactory  functionFactory = analysisFactory.createFunctionFactory(tree);
      IFitFactory       fitFactory = analysisFactory.createFitFactory();
    
      Dependent x = new Dependent("x",-3,8);
      Gaussian g = new Gaussian("gauss",x);
      g.setParameter("mean",3);
      g.setParameter("sigma",0.5);

      ITuple t = FunctionMcStudy.generateTuple(g, 10000);
      
      ICloud1D c = histogramFactory.createCloud1D("c");
      IHistogram1D h = histogramFactory.createHistogram1D("h",100,-3,8);

      IEvaluator ev = tupleFactory.createEvaluator("x");
      t.project(c,ev);
      
      c.fillHistogram(h);
      h.scale(1./HistUtils.histogramNormalization(h));
      
      plotter.region(0).plot(h);

      PdfFitter fitter = new PdfFitter("uml","jminuit");
      fitter.setUseFunctionGradient(false);

      fitter.fit(c,g);

      plotter.region(0).plot(g);
      plotter.show();
   }
}