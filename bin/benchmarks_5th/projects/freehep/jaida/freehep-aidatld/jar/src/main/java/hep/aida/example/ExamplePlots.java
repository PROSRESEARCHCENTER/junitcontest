package hep.aida.example;

import hep.aida.IAnalysisFactory;
import hep.aida.IDataPointSet;
import hep.aida.IDataPointSetFactory;
import hep.aida.IHistogram1D;
import hep.aida.IHistogramFactory;
import hep.aida.IPlotter;
import hep.aida.ITree;

import java.util.Random;

/**
 * A controller to display the examples.
 * 
 * @author The AIDA Team @ SLAC
 *
 */
public class ExamplePlots {

    public ExamplePlots() {
    }

    /**
     * Return a two dimensional IDataPointSet.
     */
    public IDataPointSet getDps2D() {
        IAnalysisFactory analysisFactory = IAnalysisFactory.create();
        ITree tree = analysisFactory.createTreeFactory().create();
        IDataPointSetFactory dpsf = analysisFactory
                .createDataPointSetFactory(tree);
        IDataPointSet dps2D = dpsf.create("dps2D",
                "Two Dimensional IDataPointSet", 2);

        Random r = new Random();

        double[] yVals2D = { 0.12, 0.22, 0.35, 0.42, 0.54, 0.61 };
        double[] yErrP2D = { 0.01, 0.02, 0.03, 0.03, 0.04, 0.04 };
        double[] yErrM2D = { 0.02, 0.02, 0.02, 0.04, 0.06, 0.05 };
        double[] xVals2D = { 1.5, 2.6, 3.4, 4.6, 5.5, 6.4 };
        double[] xErrP2D = { 0.5, 0.5, 0.4, 0.4, 0.5, 0.5 };
        for (int i = 0; i < yVals2D.length; i++) {
            dps2D.addPoint();
            dps2D.point(i).coordinate(0).setValue(xVals2D[i]);
            dps2D.point(i).coordinate(0).setErrorPlus(xErrP2D[i]);
            dps2D.point(i).coordinate(1).setValue(
                    yVals2D[i] + r.nextGaussian() / 30.0);
            dps2D.point(i).coordinate(1).setErrorPlus(yErrP2D[i]);
            dps2D.point(i).coordinate(1).setErrorMinus(yErrM2D[i]);
        }
        return dps2D;
    }

    public IPlotter getHistogram() {
        IAnalysisFactory analysisFactory = IAnalysisFactory.create();
        ITree tree = analysisFactory.createTreeFactory().create();
        IHistogramFactory histogramFactory = analysisFactory
                .createHistogramFactory(tree);

        IHistogram1D hist = histogramFactory.createHistogram1D("test", 100,
                -3.0, 3.0);
        Random r = new Random();
        for (int i = 0; i < 1000; i++) {
            hist.fill(r.nextGaussian());
        }

        IPlotter plotter = analysisFactory.createPlotterFactory().create(
                "Histogram1DFormPlotter");

        plotter.region(0).plot(hist);
        return plotter;
    }
}