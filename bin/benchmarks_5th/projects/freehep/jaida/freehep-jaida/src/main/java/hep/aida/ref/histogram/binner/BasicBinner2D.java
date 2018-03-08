/*
 * OneDBinner.java
 *
 * Created on July 18, 2002, 5:02 PM
 */

package hep.aida.ref.histogram.binner;

/**
 * Simple 2D binner.
 * 
 * @author  The AIDA team at SLAC
 */
public class BasicBinner2D extends AbstractBinner2D {
    
    int[][] entries;
    double[][] heights;
    double[][] plusErrors;
    double[][] meansX;
    double[][] rmssX;
    double[][] meansY;
    double[][] rmssY;
    
    protected int xBins;
    protected int yBins;
    
    /**
     * Creates a new instance of OneDBinner.
     *
     */
    public BasicBinner2D(int xBins, int yBins) {
        if (xBins < 0) throw new IllegalArgumentException("Number of bins cannot be negative!!! "+xBins);
        if (yBins < 0) throw new IllegalArgumentException("Number of bins cannot be negative!!! "+yBins);
        setNumberOfBins(xBins, yBins);
    }
    
    public void fill( int xBin, int yBin, double x, double y, double weight) {
        //        if ( weight < 0 || weight > 1 ) throw new IllegalArgumentException("Wrong weight "+weight+" !! It has to be between 0 and 1");
        entries[xBin][yBin]++;
        heights[xBin][yBin]     += weight;
        plusErrors[xBin][yBin]  += weight*weight;
        meansX[xBin][yBin]      += x*weight;
        rmssX[xBin][yBin]       += x*x*weight;
        meansY[xBin][yBin]      += y*weight;
        rmssY[xBin][yBin]       += y*y*weight;
    }
    
    public void clear() {
        createArrays( xBins, yBins );
    }
    
    private void setNumberOfBins(int xBins, int yBins) {
        this.xBins = xBins;
        this.yBins = yBins;
        createArrays(xBins, yBins);
    }
    
    public int entries(int xBin, int yBin) {
        return entries[xBin][yBin];
    }
    
    public double height(int xBin, int yBin) {
        return heights[xBin][yBin];
    }
    
    public double plusError(int xBin, int yBin) {
        return Math.sqrt(plusErrors[xBin][yBin]);
    }
    
    public double minusError(int xBin, int yBin) {
        return plusError(xBin,yBin);
    }
    
    public double meanX(int xBin, int yBin) {
        double h = height(xBin,yBin);
        if ( h != 0 ) return meansX[xBin][yBin]/h;
        return Double.NaN;
    }
    
    public double meanY(int xBin, int yBin) {
        double h = height(xBin,yBin);
        if ( h != 0 ) return meansY[xBin][yBin]/h;
        return Double.NaN;
    }
    
    public double rmsX(int xBin, int yBin) {
        double h = height(xBin,yBin);
        double m = meanX(xBin,yBin);
        if ( h != 0 ) return Math.sqrt( Math.abs(rmssX[xBin][yBin]/h - m*m) );
        return Double.NaN;
    }
    
    public double rmsY(int xBin, int yBin) {
        double h = height(xBin,yBin);
        double m = meanY(xBin,yBin);
        if ( h != 0 ) return Math.sqrt( Math.abs(rmssY[xBin][yBin]/h - m*m) );
        return Double.NaN;
    }
    
    public void setBinContent(int xBin, int yBin, int entries, double height, double plusError, double minusError, double meanX, double rmsX, double meanY, double rmsY) {
        this.entries[xBin][yBin] = entries;

        if ( ! Double.isNaN(height) )
            heights[xBin][yBin] = height;
        else
            heights[xBin][yBin] = entries;

        if ( ! Double.isNaN(plusError) )
            plusErrors[xBin][yBin] = plusError*plusError;
        else
            plusErrors[xBin][yBin] = entries;

        meansX[xBin][yBin] = 0;
        rmssX[xBin][yBin] = 0;
        meansY[xBin][yBin] = 0;
        rmssY[xBin][yBin] = 0;
        if ( height != 0 ) {
            meansX[xBin][yBin] = meanX*height;
            rmssX[xBin][yBin] = rmsX*rmsX*height + meanX*meanX*height;
            meansY[xBin][yBin] = meanY*height;
            rmssY[xBin][yBin] = rmsY*rmsY*height + meanY*meanY*height;
        }
    }
    
    public void scale( double scaleFactor ) {
        for ( int xBin = 0; xBin < xBins; xBin++ ) {
            for ( int yBin = 0; yBin < yBins; yBin++ ) {
                heights[xBin][yBin]     *= scaleFactor;
                plusErrors[xBin][yBin]  *= scaleFactor*scaleFactor;
                meansX[xBin][yBin]      *= scaleFactor;
                rmssX[xBin][yBin]       *= scaleFactor;
                meansY[xBin][yBin]      *= scaleFactor;
                rmssY[xBin][yBin]       *= scaleFactor;
            }
        }
    }
    
    private void createArrays(int xBins, int yBins) {
        entries = new int[xBins][yBins];
        heights = new double[xBins][yBins];
        plusErrors = new double[xBins][yBins];
        meansX = new double[xBins][yBins];
        rmssX = new double[xBins][yBins];
        meansY = new double[xBins][yBins];
        rmssY = new double[xBins][yBins];
    }

    @Override
    public double sumWW(int xBin, int yBin) {
        return plusErrors[xBin][yBin];
    }

    @Override
    public double sumXW(int xBin, int yBin) {
        return meansX[xBin][yBin];
    }

    @Override
    public double sumXXW(int xBin, int yBin) {
        return rmssX[xBin][yBin];
    }

    @Override
    public double sumYW(int xBin, int yBin) {
        return meansY[xBin][yBin];
    }

    @Override
    public double sumYYW(int xBin, int yBin) {
        return rmssY[xBin][yBin];
    }

}
