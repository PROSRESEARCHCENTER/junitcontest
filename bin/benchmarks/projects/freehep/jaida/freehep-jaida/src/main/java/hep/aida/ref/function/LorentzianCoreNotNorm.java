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

public class LorentzianCoreNotNorm extends FunctionCore {

    protected boolean providesNormalization;    
    
    public LorentzianCoreNotNorm(String str) {
        super(1, 3, new double[] {1., 1., 1.});
        setTitle("LorentzianCoreNotNorm::"+str);
        providesNormalization = false;
        
        String[] names = new String[] { "amplitude","mu", "gamma" };
        setParameterNames(names);
    }
    
    public LorentzianCoreNotNorm(String str, double[] pVal) {
        super(1, 3, pVal);
        setTitle("LorentzianCoreNotNorm::"+str);
        providesNormalization = false;
        
        String[] names = new String[] { "amplitude","mu", "gamma" };
        setParameterNames(names);
    }
    
    public double functionValue(double[] var) {        
        double x = var[0];
        double n = p[0]/Math.PI;
        double mu = p[1];
        double gamma = p[2];
        double gammaHalf = gamma/2.;
        double gammaHalfSquared = gammaHalf*gammaHalf;
        return  (n)*(gammaHalf)/((x-mu)*(x-mu)+gammaHalfSquared);
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
                p[0] = y*p[2]*Math.PI/2;
                notifyCoreChanged();
            }
            public double getX() {
                return p[1];
            }
            public double getY() {
                return p[0]*2/(p[2]*Math.PI);
            }
            public Cursor cursor() {
                return ImageHandler.getBestCursor("moveCursor.png",LorentzianCoreNotNorm.class,0,0);
            }
        };

        result[1] = new Handle() {
            public void moveTo(double x, double y) {
                p[2] = 2*(x-p[1]);
                p[0] = y*Math.PI*p[2];
                notifyCoreChanged();
            }
            public double getY() {
                return p[0]/(Math.PI*p[2]);
            }
            public double getX() {
                return p[1]+p[2]/2;
            }
            public Cursor cursor() {
                return ImageHandler.getBestCursor("moveCursor.png",LorentzianCoreNotNorm.class,0,0);
            }
        };
        return result;
    }
}
