/*
 * XYDataSource.java
 *
 * Created on November 21, 2001, 11:49 AM
 */

package jas.hist;

/**
 * A Data Source for XY plots. Note this interface is suitable for use with
 * up to several hundred points. For more points use ScatterPlotSource instead.
 * @see ScatterPlotSource
 * @author tonyj
 * @version $Id: XYDataSource.java 11550 2007-06-05 21:44:14Z duns $
 */
public interface XYDataSource extends DataSource 
{
    public int getNPoints();
    public double getX(int index);
    public double getY(int index);
    public double getPlusError(int index);
    public double getMinusError(int index);
    /**
     * Returns one of DOUBLE or DATE
     */
    public int getAxisType();
}

