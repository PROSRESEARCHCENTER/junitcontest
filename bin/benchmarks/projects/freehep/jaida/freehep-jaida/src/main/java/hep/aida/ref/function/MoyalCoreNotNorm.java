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
 * @author The FreeHEP team @ SLAC
 */

public class MoyalCoreNotNorm extends FunctionCore {
        
    protected boolean providesNormalization;
    private static double c = Math.sqrt(2*Math.PI*Math.E);
    private static double d = Math.sqrt(  Math.exp(-(1+1/Math.E))/(2*Math.PI) );
    
    
    public MoyalCoreNotNorm(String str) {
        super(1, 3, new double[] {1., 1., 1.});
        setTitle("MoyalCoreNotNorm::"+str);
        providesNormalization = false;
        
        String[] names = new String[] { "amplitude","mean", "sigma" };
        setParameterNames(names);
    }
    
    public MoyalCoreNotNorm(String str, double[] pVal) {
        super(1, 3, pVal);
        setTitle("MoyalCoreNotNorm::"+str);
        providesNormalization = false;
        
        String[] names = new String[] { "amplitude","mean", "sigma" };
        setParameterNames(names);
    }
    
    public double functionValue(double[] var) {
        double v = var[0];
        double n = p[0];
        double x0 = p[1];
        double s  = p[2];
        double lambda = (v-x0)/s;
        double arg = lambda + Math.exp( -1.*lambda );
        double num = Math.exp( -1.*arg );
        return n*Math.sqrt( num/(2*Math.PI) );
    }
    
    public boolean providesGradient() { return false; }
    public double[] gradient(double[] var)  {
        throw new UnsupportedOperationException();
    }
        
    public boolean providesParameterGradient() { return false; }
    public double[] parameterGradient(double[] var) {
        throw new UnsupportedOperationException();
    }
    
    public boolean providesNormalization() { return providesNormalization; }
    
    public double normalizationAmplitude(double[] xMin, double[] xMax) {
        throw new UnsupportedOperationException(title() + " *****  Can not calculate normalization for a not normalized function");
    }

    
    
    
    public Handle[] getHandles(double xLow, double xHigh, double yLow, double yHigh) {
        Handle[] result = new Handle[2];
        
        result[0] = new Handle() {
            public void moveTo(double x, double y) {
                p[1] = x;
                p[0] = y*c;
                notifyCoreChanged();
            }
            public double getX() {
                return p[1];
            }
            public double getY() {
                return p[0]/c;
            }
            public Cursor cursor() {
                return ImageHandler.getBestCursor("moveCursor.png",MoyalCoreNotNorm.class,0,0);
            }
        };

        result[1] = new Handle() {
            public void moveTo(double x, double y) {
                p[2] = x-p[1];
                p[0] = y/d;
                notifyCoreChanged();
            }
            public double getY() {
                return p[0]*d;
            }
            public double getX() {
                return p[1]+p[2];
            }
            public Cursor cursor() {
                return ImageHandler.getBestCursor("moveCursor.png",MoyalCoreNotNorm.class,0,0);
            }
        };
        return result;
    }
}
