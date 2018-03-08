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
public interface Binner1D {
    
    void fill( int bin, double x, double weight);    
    void clear();
    int entries(int bin);
    double height(int bin);
    double plusError(int bin);
    double minusError(int bin);
    double mean(int bin);
    double rms(int bin);
    void scale(double scaleFactor);
    int bins();   

    /**
     * This method can be used to improve accuracy of the bin RMB and MEAN calculations.
     * During the fill running sums are calculated with respect to the bin center: 
     * sum( (x - binCenter)*W ), sum( (x - binCenter)*(x - binCenter)*W )
     * Default bin center is 0
     */
    double binCenter(int bin);
    
    /**
     *
     */
    void setBinContent(int bin, double binCenter, int entries, double height, double plusError, double minusError, double sumWW, double sumXW, double sumXXW);

    double sumWW(int bin);
    double sumXW(int bin);
    double sumXXW(int bin);
}
