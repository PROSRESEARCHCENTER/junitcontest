package hep.aida.ref.histogram;

/**
 * Implementation of ICloud1D.
 * @author The AIDA team at SLAC.
 *
 */

import hep.aida.ICloud1D;
import hep.aida.IHistogram;
import hep.aida.IHistogram1D;

import java.util.ArrayList;

public class Cloud1D extends Cloud implements ICloud1D {
    
    /**
     * Create a new Cloud1D
     */
    public Cloud1D() {
        super("","",1,0,"");
    }
    
    /**
     * Create a new Cloud1D
     * @param name  The Cloud's name.
     * @param title The Cloud's title.
     * @param nMax  The maximum number of entries stored in the Cloud. If nMax is greater than zero the Cloud
     *              will be converted to an Histogram when the number of entries is more than nMax.
     * @param options Some options.
     *
     */
    protected Cloud1D(String name,String title,int nMax,String options) {
        super(name,title,1,nMax,options);
    }
    
    /**
     * Fill the Cloud with a new value with unit weight
     * @param value The value to add to the Cloud.
     * @return <code>true</code> if the fill was successful.
     *
     */
    public void fill(double value) {
        fill(value,1.0);
    }
    /**
     * Fill the Cloud with a new value with given weight
     * @param value The value to add to the Cloud.
     * @param weight The value's weight.
     * @return <code>true</code> if the fill was successful.
     *
     */
    public void fill(double value, double weight) {
        if (nEntries == 0) {
            lowerEdge = upperEdge=value;
        }
        else {
            if (value<lowerEdge) lowerEdge=value;
            if (value>upperEdge) upperEdge=value;
        }
        
        if ( histo != null ) {
            histo.fill(value,weight);
        } else if ( autoConvert() && nEntries == maxEntries ) {
            if ( histo != null ) throw new RuntimeException("Cloud already been converted");
            histo= toShowableHistogram(conversionBins(), conversionLowerEdge(), conversionUpperEdge());
            histo.fill(value,weight);
            values = null;
            weights = null;
            valuesArray.clear();
            weightsArray.clear();
            valuesArray = null;
            weightsArray = null;
        } else {
            if ( nEntries%arraySize == 0 ) {
                values  = new double[ arraySize ];
                weights = new double[ arraySize ];
                valuesArray.add( values );
                weightsArray.add( weights );
            }
            
            values[ nEntries%arraySize ] = value;
            weights[ nEntries%arraySize ] = weight;
            if ( ( !Double.isNaN(value) ) && ( !Double.isNaN(weight) ) ) {
                sumOfWeights += weight;
                mean += value*weight;
                rms += value*value*weight;
                validEntries++;
            }
            nEntries++;
            //            if ( nEntries > maxEntries && maxEntries > 0 ) throw new IllegalArgumentException();
        }
        if (isValid) fireStateChanged();
    }
    
    /**
     * Get the Cloud's lower edge.
     * @return The Cloud's lower edge.
     *
     */
    public double lowerEdge() {
        return lowerEdge;
    }
    /**
     * Get the Cloud's upper edge.
     * @return The Cloud's upper edge.
     *
     */
    public double upperEdge() {
        return upperEdge;
    }
    /**
     * Set the Cloud's upper edge
     * @param upperEdge The Cloud's upper edge.
     *
     */
    public void setUpperEdge( double upperEdge ) {
        this.upperEdge = upperEdge;
    }
    /**
     * Set the Cloud's lower edge
     * @param lowerEdge The Cloud's lower edge.
     *
     */
    public void setLowerEdge( double lowerEdge ) {
        this.lowerEdge = lowerEdge;
    }
    /**
     * Get a given value from the Cloud.
     * @param index The value's index.
     * @return The Cloud's corresponding value.
     * @exception RuntimeException if the Cloud has been converted
     *
     */
    public double value(int index) {
        if (histo!=null) throw new RuntimeException("Cloud has been converted");
        double[] val = (double[])valuesArray.get( index/arraySize );
        return val[index%arraySize];
    }
    /**
     * Get a given weight from the Cloud.
     * @param index The weight's index.
     * @return The Cloud's corresponding weight.
     * @exception RuntimeException if the Cloud has been converted
     *
     */
    public double weight(int index) {
        if (histo!=null) throw new RuntimeException("Cloud has been converted");
        double[] val = (double[])weightsArray.get( index/arraySize );
        return val[index%arraySize];
    }
    /**
     * Get the Cloud's mean.
     * @return The Cloud's mean.
     *
     */
    public double mean() {
        if ( histo != null ) return histo.mean();
        return mean / sumOfWeights();
    }
    /**
     * Get the Cloud's rms.
     * @return The Cloud's rms.
     *
     */
    public double rms() {
        if ( histo != null ) return histo.rms();
        return Math.sqrt( rms / sumOfWeights() - mean*mean/sumOfWeights()/sumOfWeights() );
    }
    /**
     * Get the Cloud's entries.
     * @return The Cloud's entries.
     *
     */
    public int entries() {
        if ( histo != null ) return histo.allEntries();
        return nEntries;
    }
    /**
     * Represent the Cloud as a Histogram.
     * @param nBins The Histogram's number of bins.
     * @param lowerEdge The Histogram's lower edge.
     * @param upperEdge The Histogram's upper edge.
     * @return The Histogram representing the Cloud.
     *
     */
    private IHistogram1D toShowableHistogram(int nBins, double lowerEdge, double upperEdge) {
        if ( histo != null ) return histo;
        return HistUtils.toShowableHistogram(this, nBins, lowerEdge, upperEdge);
    }
    
    /**
     * Convert the ICloud to an IHistogram.
     *
     */
    public void convert(int nBins, double lowerEdge, double upperEdge) {
        if ( histo != null ) throw new RuntimeException("Cloud already been converted");
        histo= toShowableHistogram(nBins, lowerEdge, upperEdge);
        if (isValid) fireStateChanged();
    }
    
    /**
     * Convert the ICloud to an IHistogram by specifying the bin edges.
     *
     */
    public void convert( double[] binEdges ) {
        if ( histo != null ) throw new RuntimeException("Cloud already been converted");
        IHistogram1D hist = new Histogram1D(name(),title(),new VariableAxis(binEdges));
        for(int i=0; i<nEntries; i++) hist.fill( value(i), weight(i) );
        histo = hist;
        if (isValid) fireStateChanged();
    }
    
    /**
     * Has the Cloud been converted to a Histogram?
     * @return <code>true<\code> if the Cloud has been converted to a Histogram.
     *
     */
    public boolean isConverted() {
        return histo != null ? true : false;
    }
    /**
     * Get the Histogram representing the Cloud
     * @return the histogram.
     * @exception RuntimeException if the histogram is not auto-convertible and "convert"
     * has not been called.
     */
    public IHistogram1D histogram() throws RuntimeException {
        if ( histo == null ) throw new RuntimeException("Cloud has not been converted");
        return histo;
    }
    /**
     * Set the Histogram representation of the Cloud.
     * @param hist The Histogram representing the Cloud.
     *
     */
    public void setHistogram( IHistogram1D hist ) {
        if ( histo != null ) throw new RuntimeException("Cloud already been converted");
        histo = hist;
        if (isValid) fireStateChanged();
    }
    
    public void fillHistogram(hep.aida.IHistogram1D hist1d) {
        if ( histo != null ) throw new IllegalArgumentException("Cloud has already been converted");
        for(int i=0; i<nEntries; i++) hist1d.fill( value(i), weight(i) );
    }
    
    public void reset() {
        nEntries = 0;
        lowerEdge = Double.NaN;
        upperEdge = Double.NaN;
        mean = 0.;
        rms = 0.;
        sumOfWeights = 0.;
        if ( histo != null )
            histo.reset();
        histo = null;
        valuesArray  = new ArrayList();
        weightsArray = new ArrayList();
        values = null;
        weights = null;
        super.reset();
    }
    
    public void convertToHistogram() {
        if ( histo != null ) throw new IllegalArgumentException("Cloud has already been converted to an Histogram");
        histo = toShowableHistogram(conversionBins(), conversionLowerEdge(), conversionUpperEdge());
        if (isValid) fireStateChanged();
    }
    
    public void scale(double scaleFactor) throws IllegalArgumentException {
        if ( scaleFactor <= 0 ) throw new IllegalArgumentException("Illegal scale factor "+scaleFactor+" it has to be positive");
        if ( isConverted() ) histo.scale( scaleFactor );
        else {
            for ( int i = 0; i < entries(); i++ ) {
                double[] weights = (double[])weightsArray.get( i/arraySize );
                weights[i%arraySize] *= scaleFactor;
            }
            sumOfWeights *= scaleFactor;
            mean *= scaleFactor;
            rms *= scaleFactor;
        }
        if (isValid) fireStateChanged();
    }
    
    /** Get the sum of weights of of all the entries
     * @return The sum of the weights of all the entries.
     *
     */
    public double sumOfWeights() {
        if ( histo != null ) return histo.sumAllBinHeights();
        return sumOfWeights;
    }
    
    public double lowerEdgeWithMargin() {
        if ( Double.isNaN(lowerEdge) )
            return Double.NaN;
        double le = lowerEdge != upperEdge ? lowerEdge : lowerEdge - 1;
        double ue = lowerEdge != upperEdge ? upperEdge : upperEdge + 1;
        double delta = ue - le;
        return le - margin()*Math.abs(delta);
    }
    public double upperEdgeWithMargin() {
        if ( Double.isNaN(upperEdge) )
            return Double.NaN;
        double le = lowerEdge != upperEdge ? lowerEdge : lowerEdge - 1;
        double ue = lowerEdge != upperEdge ? upperEdge : upperEdge + 1;
        double delta = ue - le;
        return ue + margin()*Math.abs(delta);
    }
    
    protected IHistogram hist() {
        return (IHistogram) histogram();
    }

    /**
     * Set the parameters for the ICloud conversion to an IHistogram.
     * @param bins      The number of bins of the conversion IHistogram.
     * @param lowerEdge The lower edge of the conversion IHistogram.
     * @param upperEdge The upper edge of the conversion IHistogram.
     *
     */
    public void setConversionParameters(int bins, double lowerEdge, double upperEdge) {
        this.convBins = bins;
        this.convLowerEdge = lowerEdge;
        this.convUpperEdge = upperEdge;
    }
    
    public int conversionBins() {
        return convBins;
    }
    
    public double conversionLowerEdge() {
        if ( Double.isNaN(convLowerEdge) )
            return lowerEdgeWithMargin();
        return convLowerEdge;
    }

    public double conversionUpperEdge() {
        if ( Double.isNaN(convUpperEdge) )
            return upperEdgeWithMargin();
        return convUpperEdge;
    }
    
    private int nEntries=0;
    private double lowerEdge = Double.NaN, upperEdge = Double.NaN;
    protected double sumOfWeights;
    
    private double mean, rms;
    private IHistogram1D histo;
    private ArrayList valuesArray  = new ArrayList();
    private ArrayList weightsArray = new ArrayList();
    private double[] values, weights;
    
    private int convBins = 50;
    private double convLowerEdge = Double.NaN;
    private double convUpperEdge = Double.NaN;
}
