/*
 * Grid.java
 *
 * Created on October 24, 2002, 9:59 AM
 */

package hep.aida.ref.pdf;

import java.util.Random;

/**
 *
 * @author  The AIDA team @ SLAC
 *
 */
public class Grid {
    
    private final int maxBins = 50; // This number MUST be even!!!
    private int gridDim;
    private double[][] lowerEdges;
    private double[][] upperEdges;
    private double[][] widths;
    private double[][][] values;
    private double[][][] binEdges;
    private int[] dimBlocks;
    private double[] xin;
    private double[][] weights;
    
    private boolean isValid;
    
    private double gridVol;
    private int gridBins;
    private int gridBoxes;
    
    Random rnd = new Random();
    
    private Dependent[] deps;
    
    public Grid( Dependent[] deps ) {
        initializeGrid(deps);
    }
    
    private void initializeGrid(Dependent[] deps) {
        
        this.deps = deps;
        
        gridDim = deps.length;
        lowerEdges = new double[gridDim][];
        upperEdges = new double[gridDim][];
        widths     = new double[gridDim][];
        dimBlocks  = new int[gridDim];
        values     = new double[gridDim][maxBins][];
        binEdges   = new double[gridDim][maxBins+1][];
        xin        = new double[maxBins+1];
        weights    = new double[maxBins][];
        initialize();
        
        
    }

    protected void initialize() {
        gridVol  = 1;
        gridBins = 1;
        
        for ( int i = 0; i < gridDim; i++ ) {
            RangeSet rangeSet = deps[i].range();
            int size = rangeSet.size();
            lowerEdges[i] = rangeSet.lowerBounds();
            upperEdges[i] = rangeSet.upperBounds();
            widths[i]     = new double[size];
            dimBlocks[i]  = size;
            
            for ( int m = 0; m<maxBins; m++ ) {
                binEdges [i][m] = new double[size];
                values   [i][m] = new double[size];
                if ( i == 0 ) weights  [m]    = new double[size];
            }
            binEdges[i][maxBins] = new double[size];
            
            double rangeWidth = 0;
            for ( int j = 0; j < size; j++ ) {
                if ( Double.isInfinite(lowerEdges[i][j]) || Double.isInfinite(upperEdges[i][j]) ) throw new IllegalArgumentException("Cannot have infinite ranges");
                
                double width = upperEdges[i][j] - lowerEdges[i][j];
                if ( width <= 0 ) throw new IllegalArgumentException("Invalid range of width "+width);
                rangeWidth += width;
                widths[i][j] = width;
                binEdges[i][0][j] = 0;
                binEdges[i][1][j] = 1;
            }
            gridVol *= rangeWidth;
        }
        isValid = true;
    }
    
    protected void resize( int bins ) {
        if ( bins == gridBins ) return;
        
        double grane = (double) gridBins/ (double) bins;
        
        for ( int i = 0; i < gridDim; i++ ) {
            for ( int k = 0; k < dimBlocks[i]; k++ ) {
                double xOld;
                double xNew = 0;
                double delta = 0;
                int count = 1;
                
                for ( int j = 1; j <= gridBins; j++ ) {
                    delta += 1;
                    xOld = xNew;
                    xNew = binEdges[i][j][k];
                    while( delta > grane ) {
                        delta -= grane;
                        xin[count++] = xNew - (xNew-xOld)*delta;
                    }
                }
                
                for ( int n = 1; n < bins; n++ ) 
                    binEdges[i][n][k] = xin[n];
                binEdges[i][bins][k] = 1;
            }
        }
        gridBins = bins;
    }
    
    protected void resetValues() {
        for ( int i = 0; i < gridDim; i++ )
            for ( int j = 0; j < gridBins; j++ )
                for ( int k = 0; k < dimBlocks[i]; k++ ) 
                    values[i][j][k] = 0;
    }
    
    /**
     * Generate a random vector in the specified box and store its coordinates in the array x, its
     * bin indices in the bin array and its volume in the variable vol.
     */
    protected void generatePoint( int[][] box, int bin[][], double[] vol) {
        vol[0] = 1;
        for ( int i = 0; i < gridDim; i++ ) {
            Dependent x = deps[i];
            double tmpx = rnd.nextDouble();
            double point = ((box[i][0] + tmpx)/gridBoxes)*gridBins;
            int binIndex = (int) point;
            bin[i][0] = binIndex;
            int block = box[i][1];
            bin[i][1] = block; 
            double binWidth = binEdges[i][binIndex+1][block] - binEdges[i][binIndex][block];
            double length = binEdges[i][binIndex][block] + ( point - binIndex )*binWidth;
            x.setValue(lowerEdges[i][block] + length*widths[i][block]);
            vol[0] *= binWidth;
        }
    }
        
    protected int[][] firstBox() {
        int[][] box = new int[gridDim][2];
        for ( int i = 0; i < gridDim; i++ )
            for ( int j = 0; j < 2; j++ )
                box[i][j] = 0;
        return box;
    }
    
    protected boolean nextBox( int[][] box ) {
        int j = gridDim-1;
        while( j >= 0 ) {
            box[j][0] = ( box[j][0]+1 ) % gridBoxes;
            if ( 0 != box[j][0] ) return true;
            box[j][1] = ( box[j][1]+1 ) % dimBlocks[j];
            if ( 0 != box[j][1] ) return true;
            j--;
        }
        return false;
    }
    
    protected int maxBins() {
        return maxBins;
    }
    
    protected void refine( double alpha ) {
        for ( int i = 0; i < gridDim; i++ ) {
            for ( int k = 0; k < dimBlocks[i]; k++ ) {
                double oldVal = values[i][0][k];
                double newVal = values[i][1][k];
                values[i][0][k] = ( oldVal + newVal )/2;
                double content = values[i][0][k];
                
                for ( int j = 1; j < gridBins-1; j++ ) {
                    double r = oldVal+newVal;
                    oldVal = newVal;
                    newVal = values[i][j+1][k];
                    values[i][j][k] = ( r + newVal ) / 3;
                    content += values[i][j][k];
                }
                values[i][gridBins-1][k] = ( newVal + oldVal ) / 2;
                content += values[i][gridBins-1][k];
                
                double dimWeight = 0;
                for ( int j = 0; j < gridBins; j++ ) {
                    weights[j][k] = 0;
                    if ( values[i][j][k] > 0 ) {
                        oldVal = content/values[i][j][k];
                        weights[j][k] = Math.pow( ( (oldVal-1)/oldVal/Math.log(oldVal) ), alpha );
                    }
                    dimWeight += weights[j][k];
                }
                
                double pointsPerBin = dimWeight/gridBins;
                
                double xOld = 0;
                double xNew = 0;
                double dw = 0;
                
                int count = 1;
                for ( int j = 0; j < gridBins; j++ ) {
                    dw += weights[j][k];
                    xOld = xNew;
                    xNew = binEdges[i][j+1][k];
                    while( dw > pointsPerBin ) {
                        dw -= pointsPerBin;
                        xin[count++] = xNew - ( xNew - xOld )*dw/weights[j][k];
                    }
                }
                
                for ( int j = 1; j < gridBins; j++ )
                    binEdges[i][j][k] = xin[j];
                binEdges[i][gridBins][k] = 1;
            }
        }
    }

                
    
    protected void accumulate( int[][] bin, double amount ) {
        for ( int i = 0; i < gridDim; i++ ) values[i][bin[i][0]][bin[i][1]] += amount;
    }
        
    public boolean isValid() {
        return isValid;
    }
    
    public int dimension() {
        return gridDim;
    }
    
    public double volume() {
        return gridVol;
    }
    
    public int nBins() {
        return gridBins;
    }
    
    public int nBoxes() {
        return gridBoxes;
    }
    
    public void setBoxes( int nBoxes ) {
        gridBoxes = nBoxes;
    }
    
    public void printBinning() {
        for ( int i = 0; i<gridDim; i++ ) {
            System.out.println("\n**** Dimension "+i+" binning *****");
            for ( int j = 0; j < dimBlocks[i]; j++ ) {
                System.out.println("     For interval "+j);
                System.out.print("       ");   
                for ( int k = 0; k <= gridBins; k++ ) 
                    System.out.print("  "+binEdges[i][k][j]);
                System.out.println();
            }
        }
    }    
}
