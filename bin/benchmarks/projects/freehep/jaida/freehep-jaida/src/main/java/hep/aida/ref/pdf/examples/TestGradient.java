package hep.aida.ref.pdf.examples;

import hep.aida.*;
import hep.aida.IFitResult;
import hep.aida.dev.IDevFitData;
import hep.aida.dev.IDevFitDataIterator;
import hep.aida.ext.IFitMethod;
import hep.aida.ref.fitter.InternalFitFunction;
import hep.aida.ref.function.BaseModelFunction;
import hep.aida.ref.pdf.Dependent;
import hep.aida.ref.pdf.InternalObjectiveFunction;
import hep.aida.ref.pdf.PdfFitter;
import java.util.Random;
import hep.aida.ref.pdf.*;

public class TestGradient {

    public static void main(String[] args) {
        // Create factories
        IAnalysisFactory analysisFactory = IAnalysisFactory.create();
        ITreeFactory treeFactory = analysisFactory.createTreeFactory();
        ITree tree = treeFactory.create();
        IPlotter plotter = analysisFactory.createPlotterFactory().create("Plotter");
        IHistogramFactory histogramFactory = analysisFactory.createHistogramFactory(tree);
        IFunctionFactory functionFactory = analysisFactory.createFunctionFactory(tree);
        IFitFactory fitFactory = analysisFactory.createFitFactory();
        IDataPointSetFactory dataPointSetFactory = analysisFactory.createDataPointSetFactory(tree);


        double lowRange = -4, highRange = -1*lowRange;

        IHistogram1D h1 = histogramFactory.createHistogram1D("Histogram 1D", 50, lowRange, highRange);
        ICloud1D c1 = histogramFactory.createCloud1D("Cloud");

        Random r = new Random();

        for (int i = 0; i < 100; i++) {
            double x = r.nextGaussian();
            h1.fill(x);
            c1.fill(x);
        }

        double h1Norm = h1.sumBinHeights()*(h1.axis().upperEdge()-h1.axis().lowerEdge())/h1.axis().bins();
        h1.scale(1./h1Norm);



        boolean norm = true;

        double[] f_pars = new double[]{0, 0.5};
        double[] g_pars = new double[]{f_pars[0], f_pars[1], 1.};


        Dependent x = new Dependent("x", lowRange, highRange);

//        Gaussian g_notNorm = new Gaussian("myGauss not normalized", x);
//        g_notNorm.setParameters(g_pars);


        Gaussian g = new Gaussian("myGauss", x);
        g.setParameters(g_pars);
        IRangeSet g_range = g.normalizationRange(0);
        g_range.excludeAll();
        g_range.include(lowRange, highRange);
        g.normalize(norm);

        IModelFunction f = (IModelFunction) functionFactory.createFunctionByName("IGauss", "g");
        IRangeSet f_range = f.normalizationRange(0);
        f_range.excludeAll();
        f_range.include(lowRange, highRange);
        f.normalize(norm);
        // WATCH THIS!!! THE PARAMETERS MUST BE SET _AFTER_ THE NORMALIZATION . WHEN NORMALIZING THE FUNCTION CORE IS SWITCHED!!!!
        f.setParameters(f_pars);


        IDataPointSet gDataPointSet = dataPointSetFactory.create("g", 2);
        IDataPointSet fDataPointSet = dataPointSetFactory.create("f", 2);

        int points = 200;
        double delta = (highRange - lowRange) / (double) points;

        for (int i = 0; i < points; i++) {
            double[] xv = new double[] {lowRange + delta * (double) i};



            IDataPoint gPoint = gDataPointSet.addPoint();
            gPoint.coordinate(0).setValue(xv[0]);
            gPoint.coordinate(1).setValue(g.value(xv));

            IDataPoint fPoint = fDataPointSet.addPoint();
            fPoint.coordinate(0).setValue(xv[0]);
            fPoint.coordinate(1).setValue(f.value(xv));

        }








        plotter.createRegions(2, 2);
        plotter.region(0).plot(h1);
        plotter.region(0).plot(g);
        plotter.region(1).plot(h1);
        plotter.region(1).plot(g);
//        plotter.region(1).plot(g_notNorm);
        plotter.region(1).plot(f);
        plotter.region(2).plot(gDataPointSet);
        plotter.region(2).plot(fDataPointSet);
        plotter.region(2).plot(gDataPointSet);
        plotter.show();

        double[] xVals = new double[]{r.nextDouble()};
        x.setValue(xVals[0]);

        System.out.println("Function value at x=" + xVals[0] + " g: " + g.value() + " f: " + f.value(xVals));
        ((BaseModelFunction)f).calculateNormalizationAmplitude();
        System.out.println("Function Normalization at x=" + xVals[0] + " g: " + 1./g.evaluateAnalyticalNormalization(x) + " f: " + ((BaseModelFunction)f).getNormalizationAmplitude());






        double[] g_grad = g.gradient();
//        double[] g_grad_notNorm = g_notNorm.gradient();
        double[] f_grad = f.gradient(xVals);

        System.out.println("Gradient Size: " + g_grad.length + " " + f_grad.length);

        for (int i = 0; i < g_grad.length; i++) {
            System.out.println("Gradient at x=" + xVals[0] + " g: " + g_grad[i] + /*"(" + g_grad_notNorm[i] + ")*/" f: " + f_grad[i]);
        }



        double[] g_par_grad = g.parameterGradient(xVals);
        double[] f_par_grad = f.parameterGradient(xVals);

        for (int i = 0; i < f_par_grad.length; i++) {

            System.out.println("Gradient for par " + f.parameterNames()[i] + "(" + g.getParameter(i).name() + ") at x=" + xVals[0] + " g: " + g_par_grad[i] + " f: " + f_par_grad[i]);

        }



        IFitData fitData = fitFactory.createFitData();
        fitData.create1DConnection(c1);
        IFitMethod fitMethod = PdfFitter.getFitMethod("uml");

        InternalObjectiveFunction g_objectiveFunction = new InternalObjectiveFunction(new IFitData[]{fitData}, new Function[]{g}, fitMethod);



        IDevFitDataIterator dataIter = ((IDevFitData) fitData).dataIterator();
        InternalFitFunction f_objectiveFunction = new InternalFitFunction(dataIter, f, fitMethod);

        String[] g_vars = g_objectiveFunction.variableNames();
        String[] f_vars = f_objectiveFunction.variableNames();

        if (g_vars.length != f_vars.length) {
            throw new RuntimeException("Should have the same dimension ");

            
        }
        for (int i = 0; i < g_vars.length; i++) {
            System.out.println("g var[" + i + "] = " + g_vars[i] + "    f var[" + i + "] = " + f_vars[i]);
        }

        double g_of_value = g_objectiveFunction.value(g_pars);
        double[] g_of_grad = g_objectiveFunction.gradient(g_pars);

        double f_of_value = f_objectiveFunction.value(f_pars);
        double[] f_of_grad = f_objectiveFunction.gradient(f_pars);

        System.out.println("Objective function value g = " + g_of_value + "    f = " + f_of_value);

        for (int i = 0; i < f_of_grad.length; i++) {
            System.out.println("Objective function gradient for var " + f_objectiveFunction.variableName(i) + " (" + g_objectiveFunction.variableName(i) + ")  g: " + g_of_grad[i] + " f: " + f_of_grad[i]);
        }


        /*











        Gaussian g = new Gaussian("myGauss");
        g.setParameter("norm",h1.maxBinHeight());
        g.setParameter("mean",h1.mean()+1);
        g.setParameter("sigma",h1.rms());


        plotter.region(0).plot(h1);

        PdfFitter gaussFit = new PdfFitter("Chi2","fminuit");
        gaussFit.setUseFunctionGradient(false);

        long start = System.currentTimeMillis();

        //      gaussFit.fit(h1, g);

        long end = System.currentTimeMillis();
        long time = end-start;

        System.out.println("Time to fit : "+time);


        IFitter fitter = fitFactory.createFitter("uml","fminuit","noClone=true");
        fitter.setUseFunctionGradient(true);
        IFunction ig = FunctionConverter.convert(g);

        start = System.currentTimeMillis();
        IFitResult fitResult = fitter.fit(c1, ig);
        end = System.currentTimeMillis();
        time = end-start;

        System.out.println("Time to fit : "+time+" "+fitResult.quality());

        //      plotter.region(0).plot(g);
        plotter.region(0).plot(fitResult.fittedFunction());
        plotter.show();
         */
    }
}