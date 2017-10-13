/*
 * PolynomialCoreNotNorm.java
 *
 * Created on September 4, 2002, 6:15 AM
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
 * Not normalised Polynomial (Pn) distribution in the form:
 *    f = p0 + p1*x + p2*x*x + ... ,  has n+1 parameters
 */
public class PolynomialCoreNotNorm extends FunctionCore {
    
    protected boolean providesNormalization;
    private double[] tmpVar = new double[1];
    
    public PolynomialCoreNotNorm(int dim, int nPar) {
        super(dim, nPar);
        setTitle("PolynomialCoreNotNorm::P" + nPar);
        providesNormalization = false;
    }
    
    public PolynomialCoreNotNorm(int dim, int nPar, double[] pVal) {
        super(dim, nPar, pVal);
        setTitle("PolynomialCoreNotNorm::P" + nPar);
        providesNormalization = false;
    }
    
    public PolynomialCoreNotNorm(String str) {
        super(1, getDimension(str));
        setTitle("PolynomialCoreNotNorm::"+str);
        providesNormalization = false;
    }
    
    public PolynomialCoreNotNorm(String str, double[] pVal) {
        super(1, getDimension(str), pVal);
        setTitle("PolynomialCoreNotNorm::"+str);
        providesNormalization = false;
    }
    
    // Value of the function WITHOUT Normalization factor (as if N=1)
    public double functionValue(double[] var) {
        double val = 0;
        for (int i=1; i<numberOfParameters; i++) { val += p[i]*Math.pow(var[0], i); }
        return val+p[0];
    }
    
    // Each concrete FunctionCore has to implement those methods that deal with Gradients and Normalization
    public boolean providesGradient() { return true; }
    
    public double[] gradient(double[] var)  {
        double[] tmp = new double[] {0.};
        if (numberOfParameters == 1) return tmp;
        double val = p[1];
        for (int i=2; i<numberOfParameters; i++) { val += i*p[i]*Math.pow(var[0], i-1); }
        tmp[0] = val;
        return tmp;
    }
    
    public boolean providesParameterGradient() { return true; }
    
    public double[] parameterGradient(double[] var) {
	double[] tmp = new double[numberOfParameters];
        tmp[0] = 1;
	for (int i=1; i<numberOfParameters; i++) {tmp[i] = Math.pow(var[0], i); } 
	return tmp;
    }
    
    public boolean providesNormalization() { return providesNormalization; }
    
    public double normalizationAmplitude(double[] xMin, double[] xMax) {
        throw new UnsupportedOperationException(title() + " *****  Can not calculate normalization for a not normalized function");
    }
    
    public static int getDimension(String str) {
        if(!str.toLowerCase().startsWith("p"))
            throw new IllegalArgumentException("Polynomial Function Qualifier must start with \"P\"");
        return (1 + Integer.parseInt(str.substring(1)));
    }
    
    public Handle[] getHandles(double xLow, double xHigh, double yLow, double yHigh)
    // using notation f(x) = ax^2 + bx + c
    {
        final double xmin = xLow;
        final double xmax = xHigh;
        final double ymin = yLow;
        final double ymax = yHigh;
        final int order = numberOfParameters() - 1;
                /*
                 * copying these parameters over to instance
                 * variables makes them accessible to the
                 * anonymous class definitions below
                 */
        Handle[] result = null;
        
        if ( order == 0 ) {
            result = new Handle[1];
            /* there will be one handle that can move the function up and down only */
            result[0] = new Handle() {
                
                public void moveTo(double x, double y) {
                    p[0] = y;
                    notifyCoreChanged();
                }
                public double getY() {
                    return p[0];
                }
                public double getX() {
                    return (xmax + xmin) / 2.0;
                }
                
                public Cursor cursor() {
                    return ImageHandler.getBestCursor("resizeNSCursor.png",PolynomialCoreNotNorm.class,0,0);
                }
            };
        }
        else if ( order == 1 ) {
            result = new Handle[2];
            
            double x0 = (ymax-p[0])/p[1];
            if ( x0 < xmin ) x0 = xmin;
            if ( x0 > xmax ) x0 = xmax;
            
            double x1 = (ymin-p[0])/p[1];
            if ( x1 < xmin ) x1 = xmin;
            if ( x1 > xmax ) x1 = xmax;
            
            final double xm = (x0+x1)/2;
            final double ym = p[1]*xm + p[0];
            
            result[0] = new InPlotHandle(xmin, xmax, ymin, ymax) {
                
                double tmpx = Double.NaN;
                
                public void moveTo(double x, double y) {
                    tmpx = x;
                    //                    p[1] = (xm-y)/(xm-x);
                    p[0] = y - p[1]*x;
                    if ( ! isInPlot( x, y ) )
                        tmpx = Double.NaN;
                    notifyCoreChanged();
                }
                public double getY() {
                    return p[1]*getX()+p[0];
                }
                public double getX() {
                    if ( Double.isNaN( tmpx ) ) {
                        calculateIntersections();
                        tmpx = 0.8*x0()+0.2*x1();
                    }
                    return tmpx;
                }
                
                public Cursor cursor() {
                    return ImageHandler.getBestCursor("moveCursor.png",PolynomialCoreNotNorm.class,0,0);
                }
            };
            
            result[1] = new InPlotHandle(xmin, xmax, ymin, ymax) {
                
                double tmpx = Double.NaN;
                
                public void moveTo(double x, double y) {
                    tmpx = x;
                    double yh = mirrorHandle().getY();
                    double xh = mirrorHandle().getX();
                    if ( xh != x )
                        p[1] = (yh-y)/(xh-x);
                    p[0] = y - p[1]*x;
                    if ( ! isInPlot( x, y ) )
                        tmpx = Double.NaN;
                    notifyCoreChanged();
                }
                public double getY() {
                    return p[1]*getX()+p[0];
                }
                
                public double getX() {
                    if ( Double.isNaN( tmpx ) ) {
                        calculateIntersections();
                        tmpx = 0.2*x0()+0.8*x1();
                    }
                    return tmpx;
                }
                
                public Cursor cursor() {
                    return ImageHandler.getBestCursor("rotateCursor.png",PolynomialCoreNotNorm.class,0,0);
                }
            };
            
            ( (MirrorHandle) result[1] ).setMirrorHandle( (MirrorHandle) result[0] );
            
        }
        else if ( order == 2 ) {
            result = new Handle[2];
            result[0] = new Handle() {
                // this handle is the vertex of the parabola
                public void moveTo(double x, double y) {
                    p[1] = -2.0 * p[2] * x;
                    p[0] = y - p[2] * x * x - p[1] * x;
                    notifyCoreChanged();
                }
                public double getX() {
                    return -p[1] / (2 * p[2]);
                }
                public double getY() {
                    tmpVar[0] = getX();
                    return functionValue( tmpVar );
                }
                public Cursor cursor() {
                    return ImageHandler.getBestCursor("moveCursor.png",PolynomialCoreNotNorm.class,0,0);
                }
            };

            result[1] = new Handle() {

                public void moveTo(double x, double y) {
                    final double xVertex = -p[1] / (2.0 * p[2]);
                    tmpVar[0] = xVertex;
                    final double yVertex = functionValue( tmpVar );
                    p[2] = (y - yVertex) / Math.pow(x - xVertex, 2);
                    p[1] = -2.0 * p[2] * xVertex;
                    p[0] = y - (p[2] * x * x) - (p[1] * x);
                    notifyCoreChanged();
                }
                public double getY() {
                    tmpVar[0] = getX();
                    return functionValue( tmpVar );
                }
                public double getX() {
                    final double xVertex = -p[1] / (2 * p[2]);
                    final double xMiddle = (xmax - xmin) / 2.0;
                    if (xVertex > xMiddle && xVertex < xmax)
                        return ( (xVertex - xmin) / 2.0 ) + xmin;
                    else if (xVertex <= xMiddle && xVertex > xmin)
                        return xmax - ( (xmax - xVertex) / 2.0 );
                    else
                        return xMiddle;
                }
                public Cursor cursor() {
                    return ImageHandler.getBestCursor("resizeNSCursor.png",PolynomialCoreNotNorm.class,0,0);
                }
            };
        }
        return result;
    }
    
    
    private abstract class MirrorHandle extends Handle {
        
        private MirrorHandle h = null;
        
        void setMirrorHandle( MirrorHandle h ) {
            this.h = h;
        }
        
        MirrorHandle mirrorHandle() {
            return h;
        }
        
        boolean hasMirrorHandle() {
            return h == null ? false : true;
        }
    }

    private abstract class InPlotHandle extends MirrorHandle {
        
        double x0, y0, x1, y1;
        double xmin, xmax, ymin, ymax;
        
        InPlotHandle( double xmin, double xmax, double ymin, double ymax ) {
            super();
            this.xmin = xmin;
            this.xmax = xmax;
            this.ymin = ymin;
            this.ymax = ymax;
            calculateIntersections();
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
            y0 = p[1]*x0+p[0];
            if ( y0 > ymax )
                y0 = ymax;
            if ( y0 < ymin )
                y0 = ymin;
            if ( p[1] != 0 )
                x0 = ( y0 - p[0] )/p[1];

            x1 = xmax;
            y1 = p[1]*x1+p[0];
            if ( y1 > ymax )
                y1 = ymax;
            if ( y1 < ymin )
                y1 = ymin;
            if ( p[1] != 0 )
                x1 = ( y1 - p[0] )/p[1];
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
