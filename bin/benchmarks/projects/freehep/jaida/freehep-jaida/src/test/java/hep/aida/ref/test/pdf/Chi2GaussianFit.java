package hep.aida.ref.test.pdf;

import hep.aida.IFitResult;
import hep.aida.IFitter;
import hep.aida.IFunction;
import hep.aida.IHistogram1D;
import hep.aida.IModelFunction;
import hep.aida.ref.pdf.FunctionConverter;
import hep.aida.ref.pdf.Gaussian;

import java.util.Random;

public class Chi2GaussianFit extends AbstractPdfTestCase {
    
    private IHistogram1D h1;
    
    public Chi2GaussianFit(String name) {
        super(name);
        
        h1 = histogramFactory().createHistogram1D("Histogram 1D",50,-10,10);
        Random r = new Random();
        
        for (int i=0; i<100000; i++)
            h1.fill(r.nextGaussian());

    }
    
    public void testFit() {

        Gaussian g = new Gaussian("myGauss");
        IModelFunction gauss = FunctionConverter.getIModelFunction(g);

        gauss.setParameter("norm",h1.maxBinHeight());
        gauss.setParameter("mean",h1.mean());
        gauss.setParameter("sigma",h1.rms());
        
        IFitter gaussFit = fitFactory().createFitter("Chi2","minuit","noClone=true");

        long start = System.currentTimeMillis();
        IFitResult gaussFitResult = gaussFit.fit(h1,gauss);
        long end = System.currentTimeMillis();
        long time = end-start;
                
        assertEquals(h1.mean(), g.getParameter("mean").value(),0.01);
        assertEquals(h1.rms(), g.getParameter("sigma").value(),0.02);
        assertEquals(h1.maxBinHeight(), g.getParameter("norm").value(),500);

        
        IFunction gauss2 = functionFactory().createFunctionByName("gauss","g");
        gauss2.setParameter("amplitude",h1.maxBinHeight());
        gauss2.setParameter("mean",h1.mean());
        gauss2.setParameter("sigma",h1.rms());
                
        // Fit the second gaussian
        long start2 = System.currentTimeMillis();
        IFitResult gauss2FitResult = gaussFit.fit(h1,gauss2);
        long end2 = System.currentTimeMillis();
        long time2 = end2-start2;
        
        assertEquals(gauss2FitResult.fittedFunction().parameter("amplitude"), g.getParameter("norm").value(),0.00001);
        assertEquals(gauss2FitResult.fittedFunction().parameter("mean"), g.getParameter("mean").value(),0.000001);
        assertEquals(gauss2FitResult.fittedFunction().parameter("sigma"), g.getParameter("sigma").value(),0.000001);
                
        assertEquals( time, 100, 10);
    }
}