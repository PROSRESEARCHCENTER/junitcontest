/*
 * OneDBinner.java
 *
 * Created on July 18, 2002, 5:02 PM
 */

package hep.aida.ref.histogram.binner;

/**
 *
 * @author  The AIDA team at SLAC
 *
 */
public class BasicBinner3D implements Binner3D{
    
    private int[][][] entries;
    private double[][][] heights;
    private double[][][] plusErrors;
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
     * Creates a new instance of OneDBinner.
     *
     */
    public BasicBinner3D(int xBins, int yBins, int zBins) {
        if (xBins < 0) throw new IllegalArgumentException("Number of bins cannot be negative!!! "+xBins);
        if (yBins < 0) throw new IllegalArgumentException("Number of bins cannot be negative!!! "+yBins);
        if (zBins < 0) throw new IllegalArgumentException("Number of bins cannot be negative!!! "+zBins);
        setNumberOfBins(xBins, yBins, zBins);
    }
    
    public void fill( int xBin, int yBin, int zBin, double x, double y,double z, double weight) {
        entries[xBin][yBin][zBin]++;
        heights[xBin][yBin][zBin]     += weight;
        plusErrors[xBin][yBin][zBin]  += weight*weight;
        meansX[xBin][yBin][zBin]      += x*weight;
        rmssX[xBin][yBin][zBin]       += x*x*weight;
        meansY[xBin][yBin][zBin]      += y*weight;
        rmssY[xBin][yBin][zBin]       += y*y*weight;
        meansZ[xBin][yBin][zBin]      += z*weight;
        rmssZ[xBin][yBin][zBin]       += z*z*weight;
    }
    
    public void clear() {
        createArrays(xBins, yBins, zBins);
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
        return heights[xBin][yBin][zBin];
    }
    
    public double plusError(int xBin, int yBin, int zBin) {
        return Math.sqrt(plusErrors[xBin][yBin][zBin]);
    }
    
    public double minusError(int xBin, int yBin, int zBin) {
        return plusError(xBin, yBin, zBin);
    }
    
    public double meanX(int xBin, int yBin, int zBin) {
        double h = height(xBin, yBin, zBin);
        if ( h != 0 ) return meansX[xBin][yBin][zBin]/h;
        return Double.NaN;
    }
    
    public double meanY(int xBin, int yBin, int zBin) {
        double h = height(xBin, yBin, zBin);
        if ( h != 0 ) return meansY[xBin][yBin][zBin]/h;
        return Double.NaN;
    }
    
    public double meanZ(int xBin, int yBin, int zBin) {
        double h = height(xBin, yBin, zBin);
        if ( h != 0 ) return meansZ[xBin][yBin][zBin]/h;
        return Double.NaN;
    }
    
    public double rmsX(int xBin, int yBin, int zBin) {
        double h = height(xBin, yBin, zBin);
        double m = meanX(xBin, yBin, zBin);
        if ( h != 0 ) return Math.sqrt( Math.abs(rmssX[xBin][yBin][zBin]/h - m*m) );
        return Double.NaN;
    }
    
    public double rmsY(int xBin, int yBin, int zBin) {
        double h = height(xBin, yBin, zBin);
        double m = meanY(xBin, yBin, zBin);
        if ( h != 0 ) return Math.sqrt( Math.abs(rmssY[xBin][yBin][zBin]/h - m*m) );
        return Double.NaN;
    }
    
    public double rmsZ(int xBin, int yBin, int zBin) {
        double h = height(xBin, yBin, zBin);
        double m = meanZ(xBin, yBin, zBin);
        if ( h != 0 ) return Math.sqrt( Math.abs(rmssZ[xBin][yBin][zBin]/h - m*m) );
        return Double.NaN;
    }
    
    public void setBinContent(int xBin, int yBin, int zBin, int entries, double height, double plusError, double minusError, double meanX, double rmsX, double meanY, double rmsY, double meanZ, double rmsZ) {
        this.entries[xBin][yBin][zBin] = entries;

        if ( ! Double.isNaN(height) )
            heights[xBin][yBin][zBin] = height;
        else
            heights[xBin][yBin][zBin] = entries;

        if ( ! Double.isNaN(plusError) )
            plusErrors[xBin][yBin][zBin] = plusError*plusError;
        else
            plusErrors[xBin][yBin][zBin] = entries;

        meansX[xBin][yBin][zBin] = 0;
        rmssX[xBin][yBin][zBin] = 0;
        meansY[xBin][yBin][zBin] = 0;
        rmssY[xBin][yBin][zBin] = 0;
        meansZ[xBin][yBin][zBin] = 0;
        rmssZ[xBin][yBin][zBin] = 0;
        if ( height != 0 ) {
            meansX[xBin][yBin][zBin] = meanX*height;
            rmssX[xBin][yBin][zBin] = rmsX*rmsX*height + meanX*meanX*height;
            meansY[xBin][yBin][zBin] = meanY*height;
            rmssY[xBin][yBin][zBin] = rmsY*rmsY*height + meanY*meanY*height;
            meansZ[xBin][yBin][zBin] = meanZ*height;
            rmssZ[xBin][yBin][zBin] = rmsZ*rmsZ*height + meanZ*meanZ*height;
        }
    }
    
    public void scale( double scaleFactor ) {
        for ( int xBin = 0; xBin < xBins; xBin++ ) {
            for ( int yBin = 0; yBin < yBins; yBin++ ) {
                for ( int zBin = 0; zBin < zBins; zBin++ ) {
                    heights[xBin][yBin][zBin]     *= scaleFactor;
                    plusErrors[xBin][yBin][zBin]  *= scaleFactor*scaleFactor;
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
        plusErrors = new double[xBins][yBins][zBins];
        meansX = new double[xBins][yBins][zBins];
        rmssX = new double[xBins][yBins][zBins];
        meansY = new double[xBins][yBins][zBins];
        rmssY = new double[xBins][yBins][zBins];
        meansZ = new double[xBins][yBins][zBins];
        rmssZ = new double[xBins][yBins][zBins];
    }
        
}
