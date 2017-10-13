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
public interface Binner3D {
    
    public void fill(int xBin, int yBin, int zBin, double x, double y, double z, double weight);    
    public void clear();
    public int entries(int xBin, int yBin, int zBins);
    public double height(int xBin, int yBin, int zBins);
    public double plusError(int xBin, int yBin, int zBins);
    public double minusError(int xBin, int yBin, int zBins);
    public double meanX(int xBin, int yBin, int zBins);
    public double rmsX(int xBin, int yBin, int zBins);
    public double meanY(int xBin, int yBin, int zBins);
    public double rmsY(int xBin, int yBin, int zBins);
    public double meanZ(int xBin, int yBin, int zBins);
    public double rmsZ(int xBin, int yBin, int zBins);
    public void setBinContent(int xBin, int yBin, int zBins, int entries, double height, double plusError, double minusError, double meanX, double rmsX, double meanY, double rmsY, double meanZ, double rmsZ);
    public void scale(double scaleFactor);
}
