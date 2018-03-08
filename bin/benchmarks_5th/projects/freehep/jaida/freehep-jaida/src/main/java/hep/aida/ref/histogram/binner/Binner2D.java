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
public interface Binner2D {
    
    void fill(int xBin, int yBin, double x, double y, double weight);    
    void clear();
    int entries(int xBin, int yBin);
    double height(int xBin, int yBin);
    double plusError(int xBin, int yBin);
    double minusError(int xBin, int yBin);
    double meanX(int xBin, int yBin);
    double rmsX(int xBin, int yBin);
    double meanY(int xBin, int yBin);
    double rmsY(int xBin, int yBin);
    void setBinContent(int xBin, int yBin, int entries, double height, double plusError, double minusError, double meanX, double rmsX, double meanY, double rmsY);
    void scale(double scaleFactor);
    
    double sumWW(int xBin, int yBin);
    double sumXW(int xBin, int yBin);
    double sumXXW(int xBin, int yBin);
    double sumYW(int xBin, int yBin);
    double sumYYW(int xBin, int yBin);
    double sumXYW(int xBin, int yBin);
}
