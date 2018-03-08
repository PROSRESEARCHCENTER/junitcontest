/*
 * FunctionUtils.java
 *
 * Created on February 18, 2004, 1:53 PM
 */

package hep.aida.ref.function;

import hep.aida.IFunction;
import hep.aida.IHistogram1D;
import hep.aida.IHistogram2D;
import hep.aida.IHistogram3D;

import java.util.Random;

/**
 *
 * @author  serbo
 */
public abstract class FunctionUtils {
    
    private static Random r = new Random();
        
    public static void setRandomSeed(long seed) { r.setSeed(seed); }
    
    /**
     *  Fills IHistogram1D with random entries according to the 
     *  provided distribution.
     *
     *  @param hist Histogram to be filled
     *  @param func 
     *  @param entries How many entries to put in the Histogram
     *  @param yMax Maximum value the function can take on the 
     *              range. This does not have to be the exact value,
     *              just make sure that function never takes value
     *              bigger than yMax.
     */
    public static void fill(IHistogram1D hist, IFunction func, int entries, double yMax) {
        double lowerEdge = hist.axis().lowerEdge();
        double upperEdge = hist.axis().upperEdge();
        double[] vars = new double[1];
        int count = 0;
        while ( count < entries ) {
            vars[0] = lowerEdge + r.nextDouble()*(upperEdge-lowerEdge);
            double y = r.nextDouble()*yMax;
            double f = func.value(vars);
            if ( y < f ) {
                count++;
                hist.fill(vars[0]);
            }
        }                
    }
    
    public static void fill(IHistogram2D hist, IFunction func, int entries, double yMax) {
        double lowerEdgeX = hist.xAxis().lowerEdge();
        double upperEdgeX = hist.xAxis().upperEdge();
        double lowerEdgeY = hist.yAxis().lowerEdge();
        double upperEdgeY = hist.yAxis().upperEdge();
        double[] vars = new double[2];
        int count = 0;
        while ( count < entries ) {
            vars[0] = lowerEdgeX + r.nextDouble()*(upperEdgeX-lowerEdgeX);
            vars[1] = lowerEdgeY + r.nextDouble()*(upperEdgeY-lowerEdgeY);
            double y = r.nextDouble()*yMax;
            double f = func.value(vars);
            if ( y < f ) {
                count++;
                hist.fill(vars[0], vars[1]);
            }
        }                
    }
    
    public static void fill(IHistogram3D hist, IFunction func, int entries, double yMax) {
        double lowerEdgeX = hist.xAxis().lowerEdge();
        double upperEdgeX = hist.xAxis().upperEdge();
        double lowerEdgeY = hist.yAxis().lowerEdge();
        double upperEdgeY = hist.yAxis().upperEdge();
        double lowerEdgeZ = hist.zAxis().lowerEdge();
        double upperEdgeZ = hist.zAxis().upperEdge();
        double[] vars = new double[3];
        int count = 0;
        while ( count < entries ) {
            vars[0] = lowerEdgeX + r.nextDouble()*(upperEdgeX-lowerEdgeX);
            vars[1] = lowerEdgeY + r.nextDouble()*(upperEdgeY-lowerEdgeY);
            vars[2] = lowerEdgeZ + r.nextDouble()*(upperEdgeZ-lowerEdgeZ);
            double y = r.nextDouble()*yMax;
            double f = func.value(vars);
            if ( y < f ) {
                count++;
                hist.fill(vars[0], vars[1], vars[3]);
            }
        }                
    }
}
