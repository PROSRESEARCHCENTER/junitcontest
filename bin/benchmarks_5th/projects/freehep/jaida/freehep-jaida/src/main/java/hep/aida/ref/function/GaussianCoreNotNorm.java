/*
 *ExponentialCoreNotNorm .java
 *
 * Created on October 5, 2002, 1:14 PM
 */

package hep.aida.ref.function;
import jas.hist.Handle;

import java.awt.Cursor;

import org.freehep.util.images.ImageHandler;

/**
 *
 * @author  serbo
 */

/**
 * Not normalised Gaussian (G) distribution in the form:
 *    f = amplitude*exp(-(x-mean)^2/(2*sigma^2))  has 3 parameters
 */
public class GaussianCoreNotNorm extends FunctionCore {

    protected boolean providesNormalization;
    private final static double fwhm = 2.354/2;
    
    public GaussianCoreNotNorm(String str) {
        super(1, 3, new double[] {1., 0., 1.});
        setTitle("GaussianCoreNotNorm::"+str);
        providesNormalization = false;
        
        String[] names = new String[] { "amplitude","mean", "sigma" };
        setParameterNames(names);
    }
    
    public GaussianCoreNotNorm(String str, double[] pVal) {
        super(1, 3, pVal);
        setTitle("GaussianCoreNotNorm::"+str);
        providesNormalization = false;
        
        String[] names = new String[] { "amplitude","mean", "sigma" };
        setParameterNames(names);
    }
    
    // Value of the function WITHOUT Normalization factor (as if N=1)
    public double functionValue(double[] var) {
        return p[0]*Math.exp( -Math.pow( var[0] - p[1], 2 )/(2*Math.pow( p[2], 2 )) );
    }
    
    // Each concrete FunctionCore has to implement those methods that deal with Gradients and Normalization
    public boolean providesGradient() { return true; }
    
    public double[] gradient(double[] var)  {
        return new double[] { functionValue(var)*(-2.)*(var[0] - p[1])/(2*Math.pow( p[2], 2 )) };
    }
    
    public boolean providesParameterGradient() { 
        return true; 
    }
    
    public double[] parameterGradient(double[] var) {
        double y = functionValue(var);

        String[] parNames = parameterNames();

        double[] grad = new double[] {  y/p[0] ,
        y*2.*(var[0] - p[1])/(2*Math.pow( p[2], 2 )),
        y*2.*Math.pow(var[0] - p[1],2)/(2*Math.pow( p[2], 3 )) };
        
//        System.out.print("*** Gradient : ");
//        for ( int i = 0; i < grad.length; i++ )
//            System.out.print(parNames[i]+" ("+p[i]+") = "+grad[i]+"   ");
//        System.out.println();

        return grad;
    }
    
    public boolean providesNormalization() { return providesNormalization; }
    
    public double normalizationAmplitude(double[] xMin, double[] xMax) {
        throw new UnsupportedOperationException(title() + " *****  Can not calculate normalization for a not normalized function");
    }
    
    public Handle[] getHandles(double xLow, double xHigh, double yLow, double yHigh) {
        Handle[] result = new Handle[3];
        
        result[0] = new Handle() {
            public void moveTo(double x, double y) {
                p[1] = x;
                p[0] = y;
                notifyCoreChanged();
            }
            public double getX() {
                return p[1];
            }
            public double getY() {
                return p[0];
            }
            public Cursor cursor() {
                return ImageHandler.getBestCursor("moveCursor.png",GaussianCoreNotNorm.class,0,0);
            }
        };
        result[1] = new Handle() {
            public void moveTo(double x, double y) {
                p[2] = (p[1] - x)/fwhm;
                notifyCoreChanged();
            }
            public double getY() {
                return p[0]/2;
            }
            public double getX() {
                return p[1]-p[2]*fwhm;
            }
            public Cursor cursor() {
                return ImageHandler.getBestCursor("resizeEWCursor.png",GaussianCoreNotNorm.class,0,0);
            }
        };
        result[2] = new Handle() {
            public void moveTo(double x, double y) {
                p[2] = (x - p[1])/fwhm;
                notifyCoreChanged();
            }
            public double getY() {
                return p[0]/2;
            }
            public double getX() {
                return p[1]+p[2]*fwhm;
            }
            public Cursor cursor() {
                return ImageHandler.getBestCursor("resizeEWCursor.png",GaussianCoreNotNorm.class,0,0);
            }
        };
        return result;
    }
}
