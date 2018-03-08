/*
 * RelativeNormalizer.java
 *
 * Created on January 23, 2001, 5:24 PM
 */

package jas.hist.normalization;
import java.util.Observable;
import java.util.Observer;

/**
 * A normalizar that normalizes by the ratio of two other normalizers
 * @author  tonyj
 * @version $Id: RelativeNormalizer.java 11553 2007-06-05 22:06:23Z duns $
 */
public class RelativeNormalizer extends Observable implements Normalizer, Observer
{
    /** Creates new RelativeNormalizer */
    public RelativeNormalizer(Normalizer numerator, Normalizer denominator)
    {
        this.numerator = numerator;
        this.denominator = denominator;
        if (numerator instanceof Observable) ((Observable) numerator).addObserver(this);
        if (denominator instanceof Observable) ((Observable) denominator).addObserver(this);
    }
    public double getNormalizationFactor()
    {
        return numerator.getNormalizationFactor()/denominator.getNormalizationFactor();
    }
    public void update(Observable obs, Object arg)
    {
        setChanged();
        notifyObservers();
    }
    private Normalizer numerator;
    private Normalizer denominator;
}

