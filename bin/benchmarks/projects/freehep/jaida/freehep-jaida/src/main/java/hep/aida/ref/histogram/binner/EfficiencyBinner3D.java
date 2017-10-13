/*
 * EfficiencyBinner3D.java
 *
 * Created on July 22, 2002, 10:19 AM
 */

package hep.aida.ref.histogram.binner;

/**
 *
 * @author  The AIDA team at SLAC.
 *
 */
public class EfficiencyBinner3D implements Binner3D {
    
    private int[][][] entries;
    private double[][][] heights;
    private double[][][] meansX;
    private double[][][] rmssX;
    private double[][][] meansY;
    private double[][][] rmssY;
    private double[][][] meansZ;
    private double[][][] rmssZ;
    
    private int xBins;
    private int yBins;
    private int zBins;
    
    /**
     * Creates a new instance of EfficiencyBinner1D.
     */
    
    public EfficiencyBinner3D(int xBins, int yBins, int zBins) {
        if (xBins < 0) throw new IllegalArgumentException("Number of bins cannot be negative!!! "+xBins);
        if (yBins < 0) throw new IllegalArgumentException("Number of bins cannot be negative!!! "+yBins);
        if (zBins < 0) throw new IllegalArgumentException("Number of bins cannot be negative!!! "+zBins);
        setNumberOfBins(xBins, yBins, zBins);
    }
    
    public void fill( int xBin, int yBin, int zBin, double x, double y,double z, double weight) {
        if ( weight < 0 || weight > 1 ) throw new IllegalArgumentException("Wrong weight "+weight+" !! It has to be between 0 and 1");
        entries[xBin][yBin][zBin]++;
        heights[xBin][yBin][zBin]     += weight;
        meansX[xBin][yBin][zBin]      += x*weight;
        rmssX[xBin][yBin][zBin]       += x*x*weight;
        meansY[xBin][yBin][zBin]      += y*weight;
        rmssY[xBin][yBin][zBin]       += y*y*weight;
        meansZ[xBin][yBin][zBin]      += z*weight;
        rmssZ[xBin][yBin][zBin]       += z*z*weight;
    }
    
    public void clear() {
        createArrays( xBins, yBins, zBins);
    }
    
    private void setNumberOfBins(int xBins, int yBins, int zBins) {
        this.xBins = xBins;
        this.yBins = yBins;
        this.zBins = zBins;
        createArrays(xBins, yBins, zBins);
    }
    
    public int entries(int xBin, int yBin, int zBin) {
        return entries[xBin][yBin][zBin];
    }
    
    public double height(int xBin, int yBin, int zBin) {
        if ( entries(xBin,yBin,zBin) > 0 )
            return heights[xBin][yBin][zBin]/entries(xBin,yBin,zBin);
        return 0;
    }
    
    public double plusError(int xBin, int yBin, int zBin) {
        return EfficiencyBinnerUtils.H95CL(heights[xBin][yBin][zBin],entries[xBin][yBin][zBin],1);
    }
    
    public double minusError(int xBin, int yBin, int zBin) {
        return EfficiencyBinnerUtils.H95CL(heights[xBin][yBin][zBin],entries[xBin][yBin][zBin],2);
    }
    
    public double meanX(int xBin, int yBin, int zBin) {
        double h = heights[xBin][yBin][zBin];
        if ( h != 0 ) return meansX[xBin][yBin][zBin]/h;
        return Double.NaN;
    }
    
    public double meanY(int xBin, int yBin, int zBin) {
        double h = heights[xBin][yBin][zBin];
        if ( h != 0 ) return meansY[xBin][yBin][zBin]/h;
        return Double.NaN;
    }

    public double meanZ(int xBin, int yBin, int zBin) {
        double h = heights[xBin][yBin][zBin];
        if ( h != 0 ) return meansZ[xBin][yBin][zBin]/h;
        return Double.NaN;
    }
    
    public double rmsX(int xBin, int yBin, int zBin) {
        double h = heights[xBin][yBin][zBin];
        double m = meanX(xBin,yBin,zBin);
        if ( h != 0 ) return Math.sqrt( Math.abs(rmssX[xBin][yBin][zBin]/h - m*m) );
        return Double.NaN;
    }
    
    public double rmsY(int xBin, int yBin, int zBin) {
        double h = heights[xBin][yBin][zBin];
        double m = meanY(xBin,yBin,zBin);
        if ( h != 0 ) return Math.sqrt( Math.abs(rmssY[xBin][yBin][zBin]/h - m*m) );
        return Double.NaN;
    }
    
    public double rmsZ(int xBin, int yBin, int zBin) {
        double h = heights[xBin][yBin][zBin];
        double m = meanZ(xBin,yBin,zBin);
        if ( h != 0 ) return Math.sqrt( Math.abs(rmssZ[xBin][yBin][zBin]/h - m*m) );
        return Double.NaN;
    }

    public void setBinContent(int xBin, int yBin, int zBin, int entries, double height, double plusError, double minusError, double meanX, double rmsX, double meanY, double rmsY, double meanZ, double rmsZ) {

        this.entries[xBin][yBin][zBin] = entries;

        if ( ! Double.isNaN(height) )
            heights[xBin][yBin][zBin] = height*entries;
        else
            heights[xBin][yBin][zBin] = entries*entries;
        
        meansX[xBin][yBin][zBin] = 0;
        rmssX[xBin][yBin][zBin] = 0;
        meansY[xBin][yBin][zBin] = 0;
        rmssY[xBin][yBin][zBin] = 0;
        meansZ[xBin][yBin][zBin] = 0;
        rmssZ[xBin][yBin][zBin] = 0;
        if ( heights[xBin][yBin][zBin] != 0 ) {
            meansX[xBin][yBin][zBin] = meanX*heights[xBin][yBin][zBin];
            rmssX[xBin][yBin][zBin] = rmsX*rmsX*heights[xBin][yBin][zBin] + meanX*meanX*heights[xBin][yBin][zBin];
            meansY[xBin][yBin][zBin] = meanY*heights[xBin][yBin][zBin];
            rmssY[xBin][yBin][zBin] = rmsY*rmsY*heights[xBin][yBin][zBin] + meanY*meanY*heights[xBin][yBin][zBin];
            meansZ[xBin][yBin][zBin] = meanZ*heights[xBin][yBin][zBin];
            rmssZ[xBin][yBin][zBin] = rmsZ*rmsZ*heights[xBin][yBin][zBin] + meanZ*meanZ*heights[xBin][yBin][zBin];
        }
    }
    
    public void scale( double scaleFactor ) {
        for ( int xBin = 0; xBin < xBins; xBin++ ) {
            for ( int yBin = 0; yBin < yBins; yBin++ ) {
                for ( int zBin = 0; zBin < zBins; zBin++ ) {
                    heights[xBin][yBin][zBin]     *= scaleFactor;
                    meansX[xBin][yBin][zBin]      *= scaleFactor;
                    rmssX[xBin][yBin][zBin]       *= scaleFactor;
                    meansY[xBin][yBin][zBin]      *= scaleFactor;
                    rmssY[xBin][yBin][zBin]       *= scaleFactor;
                    meansZ[xBin][yBin][zBin]      *= scaleFactor;
                    rmssZ[xBin][yBin][zBin]       *= scaleFactor;
                }
            }
        }
    }

    private void createArrays(int xBins, int yBins, int zBins) {
        entries = new int[xBins][yBins][zBins];
        heights = new double[xBins][yBins][zBins];
        meansX = new double[xBins][yBins][zBins];
        rmssX = new double[xBins][yBins][zBins];
        meansY = new double[xBins][yBins][zBins];
        rmssY = new double[xBins][yBins][zBins];
        meansZ = new double[xBins][yBins][zBins];
        rmssZ = new double[xBins][yBins][zBins];
    }
        
}

