/*
 * DataSourceNormalizer.java
 *
 * Created on January 24, 2001, 11:55 AM
 */

package jas.hist.normalization;
import jas.hist.DataSource;

import java.util.Observable;
import java.util.Observer;

/**
 * A base class for Normalizers which depend upon a DataSource
 * @author tonyj
 * @version $Id: DataSourceNormalizer.java 11553 2007-06-05 22:06:23Z duns $
 */
public abstract class DataSourceNormalizer extends SimpleNormalizer implements Observer
{
    /** Creates a new DataSourceNormalizer
     * @param source The Data Source
     */
    public DataSourceNormalizer(DataSource source)
    {
        super(1);
        this.source = source;
        String property = System.getProperty("hurry", "false");
        hurry = property != null && property.equalsIgnoreCase("true");
    }
/** To be called by superclasses, typically at the end of the constructor.
 */
    protected void init()
    {
        if (source instanceof Observable) ((Observable) source).addObserver(this);
        norm = calculateNormalization();
    }
    public void update(Observable obs,Object arg)
    {
        double newNorm = calculateNormalization();
        if (newNorm != norm)
        {
            norm = newNorm;
            normalizationChanged();
        }
    }
    public double getNormalizationFactor()
    {
        return super.getNormalizationFactor()*norm;
    }
/** Calculates the normalization factor.
 * @return The normalization factor.
 */
    protected abstract double calculateNormalization();
    protected boolean hurry;
    protected DataSource source;
    private double norm;
}

