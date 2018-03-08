/*
 *ExponentialCoreNotNorm .java
 *
 * Created on September 6, 2002, 3:28 PM
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
 * Not normalised Exponential (E) distribution in the form:
 *    f = amplitude*exp((x-origin)/exponent)  has 3 parameters
 */
public class ExponentialCoreNotNorm extends FunctionCore {
    
    protected boolean providesNormalization;
    
    public ExponentialCoreNotNorm(String str) {
        super(1, 2, new double[] {1., 1.});
        setTitle("ExponentialCoreNotNorm::"+str);
        providesNormalization = false;
        
        String[] names = new String[] { "amplitude","exponent" };
        setParameterNames(names);
    }
    
    public ExponentialCoreNotNorm(String str, double[] pVal) {
        super(1, 2, pVal);
        setTitle("ExponentialCoreNotNorm::"+str);
        providesNormalization = false;
        
        String[] names = new String[] { "amplitude","exponent" };
        setParameterNames(names);
    }
    
    // Value of the function WITHOUT Normalization factor (as if N=1)
    public double functionValue(double[] var) {
        return p[0]*Math.exp(var[0]*p[1]);
    }
    
    // Each concrete FunctionCore has to implement those methods that deal with Gradients and Normalization
    public boolean providesGradient() { return true; }
    
    public double[] gradient(double[] var)  {
        double[] tmp = new double[] {0.};
        return new double[] { functionValue(var)*p[1] };
    }
    
    public boolean providesParameterGradient() { return true; }
    
    public double[] parameterGradient(double[] var) {
        double y = functionValue(var);
        return new double[] {  y/p[0] ,  y*var[0] };
    }
    
    public boolean providesNormalization() { return providesNormalization; }
    
    public double normalizationAmplitude(double[] xMin, double[] xMax) {
        throw new UnsupportedOperationException(title() + " *****  Can not calculate normalization for a not normalized function");
    }

    public Handle[] getHandles(double xLow, double xHigh, double yLow, double yHigh) {
        Handle[] result = new Handle[2];
        
        final double xmin = xLow;
        final double xmax = xHigh;
        final double ymin = yLow;
        final double ymax = yHigh;
        
        result[0] = new MyHandle(xmin, xmax, ymin, ymax)
        // Reciprocal exponent
        {
            double tmpY = Double.NaN;
            
            public void moveTo(double x, double y) {
                tmpY = y;                
                double x0 = handle().getX();
                double y0 = handle().getY();
                if ( y != 0 ) {
                    p[1] = Math.log(y0/y)/(x0-x);                    
                    p[0] = y/(Math.exp(x*p[1]));
                } else
                    p[0] = 0;
                if ( ! isInPlot( x, y ) )
                    tmpY = Double.NaN;
                notifyCoreChanged();
            }
            public double getY() {
                if ( Double.isNaN(tmpY) ) {
                    calculateIntersections();
                    tmpY = 0.8*y0()+0.2*y1();
                }
                return tmpY;
            }
            public double getX() {
                return Math.log(getY()/p[0])/p[1];
            }
            public Cursor cursor() {
                return ImageHandler.getBestCursor("rotateCursor.png",ExponentialCoreNotNorm.class,0,0);
            }
        };
        
        result[1] = new MyHandle(xmin, xmax, ymin, ymax) {
            double tmpY = Double.NaN;
            
            public void moveTo(double x, double y) {
                tmpY = y;
                p[1] = Math.log(y/p[0])/x;
                if ( ! isInPlot( x, y ) )
                    tmpY = Double.NaN;
                notifyCoreChanged();
            }
            public double getY() {
                if ( Double.isNaN(tmpY) ) {
                    calculateIntersections();
                    tmpY = 0.2*y0()+0.8*y1();
                }
                return tmpY;
            }
            public double getX() {
                return Math.log(getY()/p[0])/p[1];
            }
            public Cursor cursor() {
                return ImageHandler.getBestCursor("moveCursor.png",ExponentialCoreNotNorm.class,0,0);
            }
        };
        
        ((MyHandle) result[0]).setHandle( result[1] );
        return result;
        
    }
    
    private abstract class MyHandle extends Handle {
        
        private Handle handle = null;
        double x0, y0, x1, y1;
        double xmin, xmax, ymin, ymax;
        
        MyHandle( double xmin, double xmax, double ymin, double ymax ) {
            super();
            this.xmin = xmin;
            this.xmax = xmax;
            this.ymin = ymin;
            this.ymax = ymax;
            calculateIntersections();
        }
        
        void setHandle( Handle handle ) {
            this.handle = handle;
        }
        
        Handle handle() {
            return handle;
        }
    
        boolean isInPlot(double x, double y) {
            if ( x > xmax || x < xmin )
                return false;
            if ( y > ymax || y < ymin )
                return false;
            return true;
        }
        
        void calculateIntersections() {
            x0 = xmin;
            y0 = p[0]*Math.exp(x0*p[1]);
            if ( y0 > ymax )
                y0 = ymax;
            if ( y0 < ymin )
                y0 = ymin;
            x0 = Math.log(y0/p[0])/p[1];

            x1 = xmax;
            y1 = p[0]*Math.exp(x1*p[1]);
            if ( y1 > ymax )
                y1 = ymax;
            if ( y1 < ymin )
                y1 = ymin;
            x1 = Math.log(y1/p[0])/p[1];
        }
        
        double x0() {
            return x0;
        }
        double x1() {
            return x1;
        }
        double y0() {
            return y0;
        }
        double y1() {
            return y1;
        }
        
    }
}
