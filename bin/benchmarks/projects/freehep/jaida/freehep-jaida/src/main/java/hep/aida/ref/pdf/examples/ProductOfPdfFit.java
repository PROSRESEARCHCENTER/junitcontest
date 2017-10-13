package hep.aida.ref.pdf.examples;

import hep.aida.*;
import java.util.Random;
import hep.aida.ref.pdf.*;
import hep.aida.ref.function.*;

public class ProductOfPdfFit
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
    
      ICloud1D c1 = histogramFactory.createCloud1D("Cloud 1D");

      for (int i=0; i<100000; i++) {
//          double x = cern.jet.random.Normal.staticNextDouble(0, 2);
//          if ( x > .5 )
//              x = cern.jet.random.Exponential.staticNextDouble(0.3);
//          c1.fill(x);
      }

      IHistogram1D h1 = histogramFactory.createHistogram1D("Histogram 1D",50,c1.lowerEdge(),c1.upperEdge());
      c1.fillHistogram(h1);

      Dependent x = new Dependent("x",c1.lowerEdge(),c1.upperEdge());

      Parameter m = new Parameter("mean", 0);
      Parameter s = new Parameter("sigma", 2);
      Gaussian gauss = new Gaussian("myGauss", x, m, s);

      Parameter le = new Parameter("le",c1.lowerEdge());
      Parameter ue = new Parameter("ue",c1.upperEdge());
      Parameter a = new Parameter("a",0.5);
      Step lowerStep = new Step("lowerStep",x,le,a);
      Step upperStep = new Step("upperStep",x,a,ue);
      
      Parameter tau = new Parameter("tau",0.3);
      Exponential expo = new Exponential("exp",x, tau, Exponential.DECAY);
      IModelFunction exp = FunctionConverter.getIModelFunction(expo);

      Product p1f = new Product("p1",lowerStep,gauss);
      Product p2f = new Product("p2",upperStep,expo);
      
      IModelFunction p1 = FunctionConverter.getIModelFunction(p1f);
      IModelFunction p2 = FunctionConverter.getIModelFunction(p2f);

      Sum ss = new Sum("total dist",p1f, p2f);
      IModelFunction sum = FunctionConverter.getIModelFunction(ss);

      IFitter fit = fitFactory.createFitter("uml","minuit","noClone=true");
  
      fit.fitParameterSettings("mean").setStepSize(0.01);
      fit.fitParameterSettings("f0").setStepSize(0.1);
      fit.fitParameterSettings("f0").setBounds(0,1);

      fit.fitParameterSettings("le").setFixed(true);
      fit.fitParameterSettings("ue").setFixed(true);
      fit.fitParameterSettings("a").setFixed(true);


      long start = System.currentTimeMillis();
  
      IFitResult fitResult = fit.fit(c1,p1);
      long end = System.currentTimeMillis();
      long time = end-start;

      System.out.println("Time to fit : "+time);

      double h1Norm = h1.sumBinHeights()*(h1.axis().upperEdge()-h1.axis().lowerEdge())/h1.axis().bins();
      h1.scale(1./h1Norm);
      plotter.region(0).plot(h1);
      plotter.region(0).plot(fitResult.fittedFunction());
      plotter.show();
   }
}