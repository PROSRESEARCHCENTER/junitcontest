/*
 * Normalizer.java
 *
 * Created on January 23, 2001, 11:50 AM
 */

package jas.hist.normalization;

/**
 * A normalizer allows a dataset to be normalized by an arbitrary factor.
 * @see jas.hist.JASHistData#setNormalization(Normalizer)
 * @author  tonyj
 * @version $Id: Normalizer.java 11550 2007-06-05 21:44:14Z duns $
 */
public interface Normalizer 
{
    /**
     * The displayed data will be divided by this factor
     */
    public double getNormalizationFactor();
}


