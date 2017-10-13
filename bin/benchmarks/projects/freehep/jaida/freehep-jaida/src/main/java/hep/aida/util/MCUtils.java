package hep.aida.util;

import hep.aida.ICloud;
import hep.aida.ICloud1D;
import hep.aida.ICloud2D;
import hep.aida.ICloud3D;
import hep.aida.IFunction;
import hep.aida.IHistogram;
import hep.aida.IHistogram1D;
import hep.aida.IHistogram2D;
import hep.aida.IHistogram3D;
import hep.aida.ITuple;
import hep.aida.ref.tuple.Tuple;
import java.util.Random;

/**
 * This class generates distribution based on a given IFunction.
 * IFunction is assumed to be non negative.
 *
 * @author The FreeHEP team @ SLAC.
 * @version $Id: MCUtils.java 13016 2007-07-17 18:33:57Z serbo $
 */
public abstract class MCUtils {
    private static double scaleMaxHeight = 1.2;
    
    /**
     * Fill IHistogram1D/2D/3D according to a given IFunction.
     * User has to create histogram and function first.
     * This method uses hist.axis().lowerEdge() and
     * hist.axis().uppedEdge() to determine function domain
     */
    public static void generateMCDistribution( IHistogram hist, IFunction f, int entries ) {
        
        long seed = System.currentTimeMillis();
        generateMCDistribution(hist, f, entries, seed);
    }
    
    public static void generateMCDistribution( IHistogram hist, IFunction f, int entries,
            long seed ) {
        
        int dim = f.dimension();
        double[] xMin = null;
        double[] xMax = null;
        if (dim == 1) {
            xMin = new double[] { ((IHistogram1D) hist).axis().lowerEdge() };
            xMax = new double[] { ((IHistogram1D) hist).axis().upperEdge() };
        } else if (dim == 2) {
            xMin = new double[] { 
                ((IHistogram2D) hist).xAxis().lowerEdge(),
                ((IHistogram2D) hist).yAxis().lowerEdge()
            };
            xMax = new double[] { 
                ((IHistogram2D) hist).xAxis().upperEdge(),
                ((IHistogram2D) hist).yAxis().upperEdge()
            };
        } else if (dim == 3) {
            xMin = new double[] { 
                ((IHistogram3D) hist).xAxis().lowerEdge(),
                ((IHistogram3D) hist).yAxis().lowerEdge(),
                ((IHistogram3D) hist).zAxis().lowerEdge()
            };
            xMax = new double[] { 
                ((IHistogram3D) hist).xAxis().upperEdge(),
                ((IHistogram3D) hist).yAxis().upperEdge(),
                ((IHistogram3D) hist).zAxis().upperEdge()
            };
        }
        generateMCDistribution(hist, f, entries, xMin, xMax, seed);
    }
    
    /**
     * This method uses min and max to determine function domain
     */
    public static void generateMCDistribution( IHistogram hist, IFunction f, int entries,
            double[] min, double[] max ) {
        
        long seed = System.currentTimeMillis();
        generateMCDistribution(hist, f, entries, min, max, seed);
    }
    
    public static void generateMCDistribution( IHistogram hist, IFunction f, int entries,
            double[] min, double[] max, long seed ) {
        
        int evEntries = (int) (entries/10);
        if (evEntries < 100) evEntries = entries;
        double maxHeight = evaluateMaxHeight(f, min, max, evEntries );
        generateMCDistribution(hist, f, entries, min, max, seed, maxHeight);
    }
    
    public static void generateMCDistribution( IHistogram hist, IFunction f, int entries,
            double[] min, double[] max, long seed, double maxHeight ) {
        
        int dim = f.dimension();
        double[] x = new double[dim+1];
        double[] point = null;
        Random rand = new Random(seed);
        try {
            for ( int i = 0; i < entries; i++ ) {
                point = getValidPoint(f, min, max, maxHeight, x, rand);
                if (dim == 1) ((IHistogram1D) hist).fill(point[0]);
                if (dim == 2) ((IHistogram2D) hist).fill(point[0], point[1]);
                if (dim == 3) ((IHistogram3D) hist).fill(point[0], point[1], point[2]);
            }
        } catch (MaxHeightException e) {
            System.out.println("\nWARNING: \t"+e.getMessage());
            System.out.println("         \tSet maxHeight="+maxHeight*scaleMaxHeight+" and re-fill histogram\n");
            hist.reset();
            generateMCDistribution(hist, f, entries, min, max, seed, maxHeight*scaleMaxHeight);
        }
    }
    
    
    /**
     * Fill ICloud1D/2D/3D according to a given IFunction.
     * User has to create cloud and function first.
     * This method uses min and max to determine function domain
     */
    public static void generateMCDistribution( ICloud cloud, IFunction f, int entries,
            double[] min, double[] max ) {
        
        long seed = System.currentTimeMillis();
        generateMCDistribution(cloud, f, entries, min, max, seed);
    }
    
    public static void generateMCDistribution( ICloud cloud, IFunction f, int entries,
            double[] min, double[] max, long seed ) {
        
        int evEntries = (int) (entries/10);
        if (evEntries < 100) evEntries = entries;
        double maxHeight = evaluateMaxHeight(f, min, max, evEntries );
        generateMCDistribution(cloud, f, entries, min, max, seed, maxHeight*scaleMaxHeight);
    }
    
    public static void generateMCDistribution( ICloud cloud, IFunction f, int entries,
            double[] min, double[] max, double maxHeight ) {
        long seed = System.currentTimeMillis();
        generateMCDistribution(cloud, f, entries, min, max, seed, maxHeight);
    }
    
    public static void generateMCDistribution( ICloud cloud, IFunction f, int entries,
            double[] min, double[] max, long seed, double maxHeight ) {
        
        int dim = f.dimension();
        double[] x = new double[dim+1];
        double[] point = null;
        Random rand = new Random(seed);
        try {
            for ( int i = 0; i < entries; i++ ) {
                point = getValidPoint(f, min, max, maxHeight, x, rand);
                if (dim == 1) ((ICloud1D) cloud).fill(point[0]);
                else if (dim == 2) ((ICloud2D) cloud).fill(point[0], point[1]);
                else if (dim == 3) ((ICloud3D) cloud).fill(point[0],point[1], point[2]);
            }
        } catch (MaxHeightException e) {
            System.out.println("\nWARNING: \t"+e.getMessage());
            System.out.println("         \tSet maxHeight="+maxHeight*scaleMaxHeight+" and re-fill histogram\n");
            cloud.reset();
            generateMCDistribution(cloud, f, entries, min, max, seed, maxHeight*scaleMaxHeight);
        }
    }
    
    
      
    
    /**
     * Create and fill ITuple according to a given IFunction.
     * ITuple is created as un-managed object - it will not appear
     * in the AIDA Tree.
     */
    public static ITuple generateMCTuple( IFunction f, int entries, 
            double[] min, double[] max ) {
        
        int evEntries = (int) (entries/10);
        if (evEntries < 100) evEntries = entries;
        double maxHeight = evaluateMaxHeight(f, min, max, evEntries );
        return generateMCTuple(f, entries, min, max, maxHeight*scaleMaxHeight);
    }   
    
    public static ITuple generateMCTuple( IFunction f, int entries, 
            double[] min, double[] max, double maxHeight ) {
        
        long seed = System.currentTimeMillis();
        return generateMCTuple(f, entries, min, max, seed, maxHeight);
    }
    
    public static ITuple generateMCTuple( IFunction f, int entries, 
            double[] min, double[] max, long seed ) {
        
        int evEntries = (int) (entries/10);
        if (evEntries < 100) evEntries = entries;
        double maxHeight = evaluateMaxHeight(f, min, max, evEntries );
        return generateMCTuple(f, entries, min, max, seed, maxHeight*scaleMaxHeight);
    }
    
    public static ITuple generateMCTuple( IFunction f, int entries, 
            double[] min, double[] max, long seed, double maxHeight ) {
        
        int dim = f.dimension();
        String[] columnNames = new String[dim+1];
        Class[]  columnTypes = new Class[dim+1];
        for ( int i = 0; i < dim; i++ ) {
            columnNames[i] = "x[" + i + "]";
            columnTypes[i] = Double.TYPE;
        }
        columnNames[dim] = "value";
        columnTypes[dim] = Double.TYPE;
        
        String name = "";
        String title = "Generated from "+f.title();
        Tuple tuple = new Tuple(name, title, columnNames, columnTypes, "");
        
        double[] x = new double[dim+1];
        double[] point = null;
        Random rand = new Random(seed);
        try {
            for ( int i = 0; i < entries; i++ ) {
                point = getValidPoint(f, min, max, maxHeight, x, rand);
                for ( int j=0; j<dim+1; j++ ) {
                    tuple.fill(j, point[j]);
                }
                tuple.addRow();
            }
        } catch (MaxHeightException e) {
            System.out.println("\nWARNING: \t"+e.getMessage());
            System.out.println("         \tSet maxHeight="+maxHeight*scaleMaxHeight+" and re-fill ITuple\n");
            tuple.reset();
            return generateMCTuple(f, entries, min, max, seed, maxHeight*scaleMaxHeight);
        }
        return tuple;
    }
    
    // x = new double[f.dimension() + 1]
    private static double[] getValidPoint(IFunction f, double[] min, double[] max,
            double maxHeight, double[] x, Random rand) {
        
        if (rand == null) rand = new Random();
        double y = 0;
        double functionValue = 0;
        int dim = f.dimension();
        while (true) {
            for ( int j = 0; j < dim; j++ ) {
                x[j] = min[j] + (max[j] - min[j])*rand.nextDouble();
            }
            x[dim] = f.value(x);
            if (x[dim] > maxHeight) {
                String message = "" + x[0];
                for ( int j = 1; j < dim; j++ ) message += ", "+x[j];
                message = "f( "+message+ " ) = "+functionValue +", maxHeight = "+maxHeight;
                throw new MaxHeightException(message);
            } else {
                y = maxHeight*rand.nextDouble();
                if (x[dim] > y) return x;
            }
        }
    }
    
    private static double evaluateMaxHeight(IFunction f, double[] min, double[] max, int entries) {
        int dim = f.dimension();
        double[] x = new double[dim];
        Random rand = new Random();
        double maxHeight = 0;
        double tmp = 0;
        for ( int i = 0; i < entries; i++ ) {
            for ( int j = 0; j < dim; j++ ) {
                x[j] = min[j] + (max[j] - min[j])*rand.nextDouble();
            }
            tmp = f.value(x);
            if ( tmp > maxHeight )
                maxHeight = tmp;
        }
        return maxHeight;
    }
    
    private static class MaxHeightException extends RuntimeException {
        private MaxHeightException(String message) {
            super(message);
        }
    }
    
}
