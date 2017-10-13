package hep.aida.ref.dataset;

/**
 *
 * @author The FreeHEP team @ SLAC
 *
 * This class is meant to calculate the statistics associated with a 
 * n-dimensional data set. In particular it calculates the following quantities for
 * "fillable" data objects like Histograms, Clouds, Profiles, Tuples for each
 * of its coordinates:
 *  - mean
 *  - rootMeanSquared
 * and the global quantities:
 *  - entries
 *  - sum of weights
 *  - equivalent entries
 * These quantities for a data set {xi} with weights {wi} are defined as:
 *  - mean = sum(xi*wi)/sumOfWeights
 *  - rootMeanSquared = sqrt( sum(xi*xi*wi)*sum(wi) - sum(xi*wi)*sum(xi*wi) )/sum(wi)
 *  - sumOfWeights = sum(wi)
 *  - entries = sum(1)
 *  - equivalent entries = sum(wi)*sum(wi)/sum(wi*wi)
 *
 * Internally it uses the class MeanAndRmsStatistics to calculate the mean and rms for each of the
 * coordinates.
 *
 */
public class DataStatistics {
    
    /**
     * Internally we keep track of the following additive quantities:
     * - sw  = sum(wi)
     * - sws = sum(wi*wi)
     * - en  = sum(1)
     *
     * The equivalent bin entries are calculated as:
     *
     * equivBinEntries = sw*sw/sws
     *
     */
    private double sw, sws;
    private int en;
    private MeanAndRmsStatistics[] stats;
    private int dimension;
        
    /** 
     * Creates a new instance of DataSetStatistics.
     * @param descriptions Is an array specifying the description for each
     *                     of the coordinates of this data set.
     */
    public DataStatistics(String[] descriptions) {
        this( descriptions.length );
        setDescription( descriptions );
    }
    
    /** 
     * Creates a new instance of DataSetStatistics.
     * @param dimension The dimension, i.e. the number of coordinates
     *                  of this data set.
     *
     */
    public DataStatistics(int dimension) {
        this.dimension = dimension;
        this.stats = new MeanAndRmsStatistics[ dimension ]; 
        for ( int i = 0; i < dimension; i++ )
            stats[i] = new MeanAndRmsStatistics(String.valueOf(i));
        reset();
    }

    /**
     * Get the dimension of the DataStatistics.
     * @return The dimension of the DataStatistics.
     *
     */
    public int dimension() {
        return dimension;
    }
    
    /**
     * Add a weighted entry to this DataSetStatistics.
     * The statistical information is updated.
     * @param x The coordinates of the added entry.
     * @param w The corresponding weight.
     *
     */
    public void addEntry( double x[], double w ) {
        if ( w < 0 ) throw new IllegalArgumentException("Cannot accept an entry with negative weight "+w);
        for ( int i = 0; i < dimension; i++ )
            stats[i].addEntry( x[i], w );
        sw  += w;
        sws += w*w;
        en++;
    }
    
    /**
     * Add a new entry to this DataSetStatistics with unit weight.
     * @param x The coordinates of the added entry.
     *
     */
    public void addEntry( double[] x ) {
        addEntry( x, 1. );
    }

    /**
     * Remove a weighted entry from this DataSetStatistics.
     * The statistical information is updated.
     * @param x The coordinates of the removed entry.
     * @param w The corresponding weight.
     *
     */
    public void removeEntry( double x[], double w ) {
        if ( w < 0 ) throw new IllegalArgumentException("Cannot accept an entry with negative weight "+w);
        for ( int i = 0; i < dimension; i++ )
            stats[i].removeEntry( x[i], w );
        sw  -= w;
        sws -= w*w;
        en--;
    }
    
    /**
     * Remove an entry from this DataSetStatistics with unit weight.
     * @param x The coordinates of the entry to remove.
     *
     */
    public void removeEntry( double[] x ) {
        removeEntry( x, 1. );
    }
    
    /**
     * Add a set of weighted entries to this DataSetStatistics.
     * The statistical information is updated.
     * @param mean    The mean of the entries to be added.
     * @param rms     The rms of the entries to be added.
     * @param sumw    The sum of weights of the entries to be added.
     * @param sumw2   The sum of weights squared of the entries to be added.
     * @param entries The number of the entries to be added.
     *
     */
    public void addEntries( double[] mean, double[] rms, double sumw, double sumw2, int entries ) {
        for ( int i = 0; i < dimension; i++ )
            stats[i].addEntries( mean[i], rms[i], sumw );
        sw  += sumw;
        sws += sumw2;
        en  += entries;
    }

    /**
     * Add a set of entries to this DataSetStatistics.
     * The statistical information is updated.
     * @param mean    The mean of the entries to be added.
     * @param rms     The rms of the entries to be added.
     * @param entries The number of the entries to be added.
     *
     */
    public void addEntries( double[] mean, double[] rms, int entries ) {
        addEntries(mean, rms, entries, entries, entries);
    }
    
    /**
     * Remove the information corresponding to a set of weighted entries.
     * @param mean    The mean of the entries to be removed.
     * @param rms     The rms of the entries to be removed.
     * @param sumw    The sum of weights of the entries to be removed.
     * @param sumw2   The sum of weights squared of the entries to be removed.
     * @param entries The number of the entries to be added.
     *
     */
    public void removeEntries( double[] mean, double[] rms, double sumw, double sumw2, int entries ) {
        for ( int i = 0; i < dimension; i++ )
            stats[i].removeEntries( mean[i], rms[i], sumw );
        sw  -= sumw;
        sws -= sumw2;
        en  -= entries;
    }
        
    /**
     * Remove the information corresponding to a set of weighted entries.
     * @param mean    The mean of the entries to be removed.
     * @param rms     The rms of the entries to be removed.
     * @param entries The number of the entries to be added.
     *
     */
    public void removeEntries( double[] mean, double[] rms, int entries ) {
        removeEntries( mean, rms, entries, entries, entries );
    }

    /**
     * Get the mean for a given coordinate.
     * @param  coord The index of the coordinate.
     * @return The mean of the coordinate coord.
     *
     */
    public double mean(int coord) {
        return stats[coord].mean();
    }
    
    /**
     * Get the rms for a given coordinate.
     * @param  coord The index of the coordinage;
     * @return The rms of the coordinate coord.
     *
     */
    public double rms(int coord) {
        return stats[coord].rms();
    }
       
    /**
     * Get the sum of weights for this data set.
     * @return The sum of weights.
     *
     */
    public double sumOfWeights() {
        return sw;
    }
    
    /**
     * Get the equivalent entries for this data set.
     * @return The equivalent entries for this data set.
     *
     */
    public double equivalentEntries() {
        if ( en > 0 ) 
            return sw*sw/sws;
        return 0;
    }
    
    /**
     * Get the number of entries in this data set
     * @return The number of entries.
     *
     */
    public int entries() {
        return en;
    }
    
    /**
     * Scale the statistics by a give scaleFactor
     * Rescaling is equivalent to multiplying all the weights by the scale factor.
     * @param scaleFactor The scaleFactor.
     *
     */
    public void scale( double scaleFactor ) {
        if ( scaleFactor > 0 ) {
            for ( int i = 0; i < dimension; i++ )
                stats[i].scale( scaleFactor );
            sw  *= scaleFactor;
            sws *= scaleFactor*scaleFactor;
        } else
            throw new IllegalArgumentException("Invalid scale factor "+scaleFactor+". It must be positive");
    }    
     
    /**
     * Reset all the statistics quantities to zero.
     *
     */
    public void reset() {
        sw  = 0;
        sws = 0;
        en  = 0;
        for ( int i = 0; i < dimension; i++ )
            stats[i].reset();
    }
    
    /**
     * Set the description for all the coordinates.
     * @param descriptions The array containing the description for each coordinate.
     *
     */
    public void setDescription( String[] descriptions ) {
        if ( descriptions.length != dimension ) 
            throw new IllegalArgumentException("Illegal dimension "+descriptions.length+" for the array of descriptions. It has to be equal to the dimension of the data set: "+dimension);
        for ( int i = 0; i < dimension; i++ )
            setDescription( i, descriptions[i] );
    }
    
    /**
     * Set the description for a given coordinate
     * @param coord The index of the coordinate.
     * @param description The description for the coordinate
     *
     */
    public void setDescription( int coord, String description ) {
        if ( coord < 0 || coord >= dimension )
            throw new IllegalArgumentException("Illegal coordinate "+coord+". It must be between 0 and "+dimension);
        stats[coord].setDescription(description);
    }
    
    /**
     * Get the description corresponding to a coordinate.
     * @param coord The coordinate.
     * @return      The description corresponding to that coordinate.
     *
     */
    public String description( int coord ) {
        return stats[coord].description();
    }
}
