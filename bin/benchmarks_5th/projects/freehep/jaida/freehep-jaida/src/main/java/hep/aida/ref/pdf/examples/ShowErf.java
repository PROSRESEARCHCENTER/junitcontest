/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hep.aida.ref.pdf.examples;

import hep.aida.IAnalysisFactory;
import hep.aida.IDataPoint;
import hep.aida.IDataPointSet;
import hep.aida.IDataPointSetFactory;
import hep.aida.IFitFactory;
import hep.aida.IFunctionFactory;
import hep.aida.IHistogramFactory;
import hep.aida.IPlotter;
import hep.aida.ITree;
import hep.aida.ITreeFactory;
import hep.aida.ref.pdf.MathUtils;
import org.apache.commons.math.special.Erf;

/**
 *
 * @author turri
 */
public class ShowErf {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        IAnalysisFactory analysisFactory = IAnalysisFactory.create();
        ITreeFactory treeFactory = analysisFactory.createTreeFactory();
        ITree tree = treeFactory.create();
        IPlotter plotter = analysisFactory.createPlotterFactory().create("Plotter");
        IHistogramFactory histogramFactory = analysisFactory.createHistogramFactory(tree);
        IFunctionFactory functionFactory = analysisFactory.createFunctionFactory(tree);
        IFitFactory fitFactory = analysisFactory.createFitFactory();
        IDataPointSetFactory dataPointSetFactory = analysisFactory.createDataPointSetFactory(tree);

        IDataPointSet erfDataPointSet = dataPointSetFactory.create("erf", 2);
        IDataPointSet apacheErfDataPointSet = dataPointSetFactory.create("apache erf", 2);
        IDataPointSet diffDataPointSet = dataPointSetFactory.create("Diff", 2);

        double upperEdge = 5, lowerEdge = -1 * upperEdge;
        int points = 200;
        double delta = (upperEdge - lowerEdge) / (double) points;

        for (int i = 0; i < points; i++) {
            double x = lowerEdge + delta * (double) i;

            double erf = MathUtils.erf(x);
            double apacheErf = -2;
            try {
                apacheErf = Erf.erf(x);
            } catch (Exception e) {
                System.out.println("Problem evaluating Apache Erf for " + x);
            }

            double diff = erf - apacheErf;
            IDataPoint diffPoint = diffDataPointSet.addPoint();
            diffPoint.coordinate(0).setValue(x);
            diffPoint.coordinate(1).setValue(diff);


            IDataPoint erfPoint = erfDataPointSet.addPoint();
            erfPoint.coordinate(0).setValue(x);
            erfPoint.coordinate(1).setValue(erf);

            IDataPoint apacheErfPoint = apacheErfDataPointSet.addPoint();
            apacheErfPoint.coordinate(0).setValue(x);
            apacheErfPoint.coordinate(1).setValue(apacheErf);

        }

        plotter.createRegions(1,2);
        plotter.region(0).plot(erfDataPointSet);
        plotter.region(0).plot(apacheErfDataPointSet);
        plotter.region(1).plot(diffDataPointSet);
        plotter.show();



    }
}
