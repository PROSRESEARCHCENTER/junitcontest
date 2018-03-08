package hep.aida.ref.pdf.examples;


import hep.aida.IAnalysisFactory;
import hep.aida.ICloud1D;
import hep.aida.IFitFactory;
import hep.aida.IFunctionFactory;
import hep.aida.IHistogram1D;
import hep.aida.IHistogramFactory;
import hep.aida.IPlotter;
import hep.aida.ITree;
import hep.aida.ITreeFactory;
import hep.aida.ref.fitter.fitdata.FitData;
import hep.aida.ref.fitter.fitdata.FitDataCreator;
import hep.aida.ref.histogram.HistUtils;
import hep.aida.ref.pdf.Dependent;
import hep.aida.ref.pdf.FunctionIntegrator;
import hep.aida.ref.pdf.Gaussian;
import hep.aida.ref.pdf.NonParametricPdf;
import hep.aida.ref.pdf.Parameter;
import hep.aida.ref.pdf.PdfFitter;
import hep.aida.ref.pdf.Sum;

import java.util.Random;

public class NonParametricPdfFit
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
    
      IHistogram1D hBkg    = histogramFactory.createHistogram1D("hBkg",100,-10,10);
      IHistogram1D hSignal = histogramFactory.createHistogram1D("Signal",100,-10,10);
      ICloud1D cBkg = histogramFactory.createCloud1D("cBkg");
      ICloud1D cSignal = histogramFactory.createCloud1D("Signal");

      Random r = new Random();

      for (int i=0; i<5000; i++) {
          double x = r.nextDouble();
          if ( ( x < 0.5 && r.nextDouble() < x ) || ( x > 0.5 && r.nextDouble() < 0.5 ) )  {
              x = x*20 - 10;
              cBkg.fill(x);
              hBkg.fill(x);
              cSignal.fill(x);
              hSignal.fill(x);
          }
          x = r.nextGaussian();
          cSignal.fill(x);
          hSignal.fill(x);
      }

      Dependent x = new Dependent("x",-10,10);
      Parameter m = new Parameter("mean",cSignal.mean(),0.01);
      Parameter s = new Parameter("sigma",1);

      //Create the Signal gaussians
      Gaussian gauss = new Gaussian("gauss", x, m, s);
      
      //Create the Bkg non-parametric distribution
      NonParametricPdf bkg = new NonParametricPdf("bkg",(FitData)FitDataCreator.create(cBkg),x,2);
      NonParametricPdf bkgNoMirror = new NonParametricPdf("bkgNoMirror",(FitData)FitDataCreator.create(cBkg),x,0);

      System.out.println("Integral : "+FunctionIntegrator.integralTrapezoid(bkg,x)+" "+FunctionIntegrator.integralTrapezoid(bkgNoMirror,x));
      
      //Add the gaussians
      Parameter f0 = new Parameter("f0", 0.2, 0, 1);
      Sum sum = new Sum("Sum of Gauss",gauss, bkg,f0);

      PdfFitter fitter = new PdfFitter("uml","jminuit");
      fitter.setUseFunctionGradient(false);
      fitter.fit(cSignal,sum);


      double normSignal = HistUtils.histogramNormalization(hSignal);
      hSignal.scale(1./normSignal);

      double normBkg = HistUtils.histogramNormalization(hBkg);
      hBkg.scale(1./normBkg);

      plotter.createRegions(2,2);
      plotter.region(0).plot(hBkg);
      plotter.region(0).plot(bkg);
      plotter.region(1).plot(hBkg);
      plotter.region(1).plot(bkgNoMirror);
      plotter.region(2).plot(hSignal);
      plotter.region(2).plot(sum);
      plotter.show();


   }
}