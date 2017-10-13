/*
 * SimpleNormalizer.java
 *
 * Created on January 23, 2001, 12:09 PM
 */

package jas.hist.normalization;
import java.util.Observable;

/**
 * A normalizer that normalizes by a set factor. 
 * @author  tonyj
 * @version $Id: SimpleNormalizer.java 11553 2007-06-05 22:06:23Z duns $
 */
public class SimpleNormalizer extends Observable implements Normalizer
{
    /** Creates new SimpleNormalizer */
    public SimpleNormalizer(double factor)
    {
        this.factor = factor;
    }
    public void setFactor(double factor)
    {
        if (this.factor != factor)
        {
            this.factor = factor;
            normalizationChanged();
        }
    }
    protected void normalizationChanged()
    {
        setChanged();
        notifyObservers();
    }
    public double getNormalizationFactor()
    {
        return factor;
    }
    private double factor;
}

