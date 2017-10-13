package hep.aida.ref.dataset;

/**
 *
 * @author The FreeHEP team @ SLAC
 *
 * This class is meant to calculate the mean and rms associated with a 
 * one dimensional data set. In particular it calculates the following quantities for
 * "fillable" data objects like Histograms, Clouds, Profiles, Tuples:
 *  - mean
 *  - rootMeanSquared
 * These quantities for a data set {xi} with weights {wi} are defined as:
 *  - mean = sum(xi*wi)/sumOfWeights
 *  - rootMeanSquared = sqrt( sum(xi*xi*wi)*sum(wi) - sum(xi*wi)*sum(xi*wi) )/sum(wi)
 *  - sumOfWeights = sum(wi)
 *
 * This class is meant to be used only within this package.
 *
 */
class MeanAndRmsStatistics {
    
    /**
     * Internally we keep track of the following additive quantities:
     * - m   =  sum(xi*wi)
     * - r   =  sum(xi*xi*wi)
     * - sw  = sum(wi)
     *
     * The mean and the rootMeanSquared are obtained as :
     * - mean = m/sw
     * - rootMeanSquared = sqrt( r*sw - m*m )/sw
     *
     */
    private double m, r, sw;
    
    //The description for this DataSetStatistics
    private String description;
    
    /** 
     * Creates a new instance of DataSetStatistics.
     * At creation the reset() method is invoked.
     */
    protected MeanAndRmsStatistics(String description) {
        setDescription( description );
        reset();
    }
    
    /**
     * Add a weighted entry to this DataSetStatistics.
     * The statistical information is updated.
     * @param x The added entry.
     * @param w The corresponding weight.
     *
     */
    public void addEntry( double x, double w ) {
        if ( w < 0 ) throw new IllegalArgumentException("Cannot accept an entry with negative weight "+w);
        m   += x*w;
        r   += x*x*w;
        sw  += w;
    }
    
    /**
     * Add a new entry to this DataSetStatistics with unit weight.
     * @param x The added entry.
     *
     */
    public void addEntry( double x ) {
        addEntry( x, 1. );
    }


    /**
     * Remove a weighted entry from this DataSetStatistics.
     * The statistical information is updated.
     * @param x The entry to remove.
     * @param w The corresponding weight.
     *
     */
    public void removeEntry( double x, double w ) {
        if ( w < 0 ) throw new IllegalArgumentException("Cannot accept an entry with negative weight "+w);
        m   -= x*w;
        r   -= x*x*w;
        sw  -= w;
    }
    
    /**
     * Remove an entry from this DataSetStatistics with unit weight.
     * @param x The entry to remove.
     *
     */
    public void removeEntry( double x ) {
        removeEntry( x, 1. );
    }

    /**
     * Add a set of weighted entries to this DataSetStatistics.
     * The statistical information is updated.
     * @param mean    The mean of the entries to be added.
     * @param rms     The rms of the entries to be added.
     * @param sumw    The sum of weights of the entries to be added (for entries
     *                with unit weight it is the number of entries).
     *
     */
    public void addEntries( double mean, double rms, double sumw ) {
        double mEntries = mean*sumw;
        double rEntries = (rms*rms + mean*mean)*sumw;
        m  += mEntries;
        r  += rEntries;
        sw += sumw;
    }

    /**
     * Remove the information corresponding to a set of weighted entries.
     * @param mean    The mean of the entries to be removed.
     * @param rms     The rms of the entries to be removed.
     * @param sumw    The sum of weights of the entries to be removed (for entries
     *                with unit weight it is the number of entries).
     *
     */
    public void removeEntries( double mean, double rms, double sumw ) {
        double mEntries = mean*sumw;
        double rEntries = (rms*rms + mean*mean)*sumw;
        m  -= mEntries;
        r  -= rEntries;
        if ( r < 0 ) r = 0;
        sw -= sumw;
    }
        
    /**
     * Get the mean.
     * @return The mean of the dataSet.
     *
     */
    public double mean() {
        if ( sw != 0 )
            return m/sw;
        return 0.;
    }
    
    /**
     * Get the rms.
     * @return The rms of the dataSet.
     *
     */
    public double rms() {
        if ( sw != 0 ) {
            double up2 = r*sw-m*m;
            if ( up2 < -1E-12*m*m ) up2 = 0;
            return Math.sqrt( Math.abs(up2) )/sw;
        }
        return 0.;
    }
       
    /**
     * Scale the statistics by a give scaleFactor
     * Rescaling is equivalent to multiplying all the weights by the scale factor.
     * @param scaleFactor The scaleFactor.
     *
     */
    public void scale( double scaleFactor ) {
        if ( scaleFactor > 0 ) {
            m  *= scaleFactor;
            r  *= scaleFactor;
            sw *= scaleFactor;
        } else
            throw new IllegalArgumentException("Invalid scale factor "+scaleFactor+". It must be positive");
    }    
    
    /**
     * Get the description of this DataSetStatistics
     * @return The description.
     *
     */
    public String description() {
        return description;
    }
    
    /**
     * Set the description of this DataSetStatistics
     * @param description The description.
     *
     */
    protected void setDescription( String description ) {
        this.description = description;
    }

    /**
     * Reset all the statistics quantities to zero.
     *
     */
    public void reset() {
        m   = 0;
        r   = 0;
        sw  = 0;
    }
    
}
