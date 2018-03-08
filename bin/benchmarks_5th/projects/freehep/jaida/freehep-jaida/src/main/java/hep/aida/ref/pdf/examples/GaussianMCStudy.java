package hep.aida.ref.pdf.examples;

import hep.aida.*;
import hep.aida.ref.pdf.FunctionMcStudy;
import hep.aida.ref.pdf.Gaussian;
import java.util.Random;

/**
 *
 * @author turri
 */
public class GaussianMCStudy {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        IAnalysisFactory analysisFactory = IAnalysisFactory.create();
        ITreeFactory treeFactory = analysisFactory.createTreeFactory();
        ITree tree = treeFactory.create();
        IHistogramFactory histogramFactory = analysisFactory.createHistogramFactory(tree);
        ITupleFactory tupleFactory = analysisFactory.createTupleFactory(tree);
        IFunctionFactory functionFactory = analysisFactory.createFunctionFactory(tree);
        IFitFactory fitFactory = analysisFactory.createFitFactory();
        IPlotter plotter = analysisFactory.createPlotterFactory().create("Plotter");

        ICloud1D c1 = histogramFactory.createCloud1D("Cloud 1D");

        Random r_1 = new Random(123);
        Random r_2 = new Random(456);

        int entries = 10000;
        for (int i = 0; i < entries; i++) {
            double x = r_1.nextGaussian();
//            if (r_1.nextDouble() < 0.2) {
//                x += 3 * r_2.nextGaussian();
//            }
            c1.fill(x);
        }

        plotter.show();

        plotter.region(0).plot(c1);

        Gaussian gaussian = new Gaussian("Gaussian");

        ITuple tuple = FunctionMcStudy.generateTuple(gaussian, entries);
        ICloud1D mcGauss = histogramFactory.createCloud1D("Mc Gauss");

        String[] columnNames = tuple.columnNames();
        for ( int i = 0; i < columnNames.length; i++ )
            System.out.println("*** "+columnNames[i]);

//        tuple.project(c1, tupleFactory.createEvaluator(arg0));

    }

}
