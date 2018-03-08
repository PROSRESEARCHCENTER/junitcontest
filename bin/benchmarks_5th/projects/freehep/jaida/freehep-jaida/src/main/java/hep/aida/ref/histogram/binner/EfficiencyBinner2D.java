/*
 * EfficiencyBinner2D.java
 *
 * Created on July 22, 2002, 10:19 AM
 */

package hep.aida.ref.histogram.binner;

/**
 *
 * @author  The AIDA team at SLAC.
 *
 */
public class EfficiencyBinner2D extends AbstractBinner2D {
    
    private int[][] entries;
    private double[][] heights;
    private double[][] meansX;
    private double[][] rmssX;
    private double[][] meansY;
    private double[][] rmssY;
    
    private int xBins;
    private int yBins;
    
    /**
     * Creates a new instance of EfficiencyBinner1D.
     */
    
    public EfficiencyBinner2D(int xBins, int yBins) {
        if (xBins < 0) throw new IllegalArgumentException("Number of bins cannot be negative!!! "+xBins);
        if (yBins < 0) throw new IllegalArgumentException("Number of bins cannot be negative!!! "+yBins);
        setNumberOfBins(xBins, yBins);
    }
    
    public void fill( int xBin, int yBin, double x, double y, double weight) {
        if ( weight < 0 || weight > 1 ) throw new IllegalArgumentException("Wrong weight "+weight+" !! It has to be between 0 and 1");
        entries[xBin][yBin]++;
        heights[xBin][yBin]     += weight;
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
        if ( entries(xBin,yBin) > 0 )
            return heights[xBin][yBin]/entries(xBin,yBin);
        return 0;
    }
    
    public double plusError(int xBin, int yBin) {
        return EfficiencyBinnerUtils.H95CL(heights[xBin][yBin],entries[xBin][yBin],1);
    }
    
    public double minusError(int xBin, int yBin) {
        return EfficiencyBinnerUtils.H95CL(heights[xBin][yBin],entries[xBin][yBin],2);
    }
    
    public double meanX(int xBin, int yBin) {
        double h = heights[xBin][yBin];
        if ( h != 0 ) return meansX[xBin][yBin]/h;
        return Double.NaN;
    }
    
    public double meanY(int xBin, int yBin) {
        double h = heights[xBin][yBin];
        if ( h != 0 ) return meansY[xBin][yBin]/h;
        return Double.NaN;
    }
    
    public double rmsX(int xBin, int yBin) {
        double h = heights[xBin][yBin];
        double m = meanX(xBin,yBin);
        if ( h != 0 ) return Math.sqrt( Math.abs(rmssX[xBin][yBin]/h - m*m) );
        return Double.NaN;
    }
    
    public double rmsY(int xBin, int yBin) {
        double h = heights[xBin][yBin];
        double m = meanY(xBin,yBin);
        if ( h != 0 ) return Math.sqrt( Math.abs(rmssY[xBin][yBin]/h - m*m) );
        return Double.NaN;
    }
    
    public void setBinContent(int xBin, int yBin, int entries, double height, double plusError, double minusError, double meanX, double rmsX, double meanY, double rmsY) {

        this.entries[xBin][yBin] = entries;

        if ( ! Double.isNaN(height) )
            heights[xBin][yBin] = height*entries;
        else
            heights[xBin][yBin] = entries*entries;
        
        meansX[xBin][yBin] = 0;
        rmssX[xBin][yBin] = 0;
        meansY[xBin][yBin] = 0;
        rmssY[xBin][yBin] = 0;
        if ( heights[xBin][yBin] != 0 ) {
            meansX[xBin][yBin] = meanX*heights[xBin][yBin];
            rmssX[xBin][yBin] = rmsX*rmsX*heights[xBin][yBin] + meanX*meanX*heights[xBin][yBin];
            meansY[xBin][yBin] = meanY*heights[xBin][yBin];
            rmssY[xBin][yBin] = rmsY*rmsY*heights[xBin][yBin] + meanY*meanY*heights[xBin][yBin];
        }
    }

    public void scale( double scaleFactor ) {
        for ( int xBin = 0; xBin < xBins; xBin++ ) {
            for ( int yBin = 0; yBin < yBins; yBin++ ) {
                heights[xBin][yBin]     *= scaleFactor;
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
        meansX = new double[xBins][yBins];
        rmssX = new double[xBins][yBins];
        meansY = new double[xBins][yBins];
        rmssY = new double[xBins][yBins];
    }
    
}

