package hep.aida.ref.test.pdf;

import hep.aida.ICloud1D;
import hep.aida.ref.pdf.Dependent;
import hep.aida.ref.pdf.Gaussian;
import hep.aida.ref.pdf.Parameter;
import hep.aida.ref.pdf.PdfFitter;
import hep.aida.ref.pdf.Sum;

import java.util.Random;

public class SumOfGaussianFit extends AbstractPdfTestCase {
    
    private ICloud1D c1;
    
    public SumOfGaussianFit(String name) {
        super(name);
        
        c1 = histogramFactory().createCloud1D("Cloud 1D");
        
        Random r_1 = new Random(123);
        Random r_2 = new Random(456);
        
        for (int i=0; i<100000; i++) {
            double x = r_1.nextGaussian();
            if ( r_1.nextDouble() < 0.2 )
                x += 3*r_2.nextGaussian();
            c1.fill(x);
        }
        
    }
    
    public void testFit() {
        
        Dependent x = new Dependent("x",c1.lowerEdge(),c1.upperEdge());
        Parameter m1 = new Parameter("mean1",c1.mean(),0.01);
        Parameter s1 = new Parameter("sigma1",1);
        Parameter m2 = new Parameter("mean2",c1.mean(),0.01);
        Parameter s2 = new Parameter("sigma2",3);
        
        //Create two gaussians
        Gaussian gauss1 = new Gaussian("myGauss1", x, m1, s1);
        Gaussian gauss2 = new Gaussian("myGauss2", x, m2, s2);
        
        //Add the gaussians
        Sum s = new Sum("Sum of Gauss",gauss1, gauss2);        
        Parameter f0 = s.getParameter("f0");
        f0.setValue(0.2);
        f0.setBounds(0,1);
        
        PdfFitter fitter = new PdfFitter("uml","minuit");
        
        fitter.fit(c1,s);
        
        assertEquals(c1.mean(), s.getParameter("mean1").value(),0.01);
        assertEquals(1, s.getParameter("sigma1").value(),0.02);
        assertEquals(c1.mean(), s.getParameter("mean2").value(),0.01);
        assertEquals(3, s.getParameter("sigma2").value(),0.2);
        
    }
}