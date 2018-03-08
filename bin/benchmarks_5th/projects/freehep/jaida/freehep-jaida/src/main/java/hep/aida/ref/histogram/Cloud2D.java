/*
 * Cloud2D.java
 *
 * Created on February 26, 2001, 2:11 PM
 */

package hep.aida.ref.histogram;
import hep.aida.ICloud2D;
import hep.aida.IHistogram;
import hep.aida.IHistogram2D;

import java.util.ArrayList;

/**
 *
 * @author  The AIDA team @ SLAC.
 *
 */
public class Cloud2D extends Cloud implements ICloud2D {
    
    /**
     * Create a new Cloud2D
     */
    public Cloud2D() {
        super("","",2,0,"");
    }
    
    /**
     * Create a new Cloud2D
     * @param name  The Cloud's name.
     * @param title The Cloud's title.
     * @param nMax  The maximum number of entries stored in the Cloud. If nMax is greater than zero the Cloud
     *              will be converted to an Histogram when the number of entries is more than nMax.
     * @param options Some options.
     *
     */
    protected Cloud2D(String name,String title,int nMax,String options) {
        super(name,title,2,nMax,options);
    }
    
    /**
     * Fill the Cloud with new values with unit weight
     * @param xValue The x value to add to the Cloud.
     * @param yValue The y value to add to the Cloud.
     * @return <code>true</code> if the fill was successful.
     *
     */
    public void fill(double xValue, double yValue) {
        fill(xValue,yValue,1.0);
    }
    /**
     * Fill the Cloud with new values with given weight
     * @param xValue The x value to add to the Cloud.
     * @param yValue The y value to add to the Cloud.
     * @param weight The values weight.
     * @return <code>true</code> if the fill was successful.
     *
     */
    public void fill(double xValue, double yValue, double weight) {
        if (nEntries == 0) {
            lowerEdgeX = upperEdgeX = xValue;
            lowerEdgeY = upperEdgeY = yValue;
        }
        else {
            if (xValue<lowerEdgeX) lowerEdgeX = xValue;
            if (xValue>upperEdgeX) upperEdgeX = xValue;
            if (yValue<lowerEdgeY) lowerEdgeY = yValue;
            if (yValue>upperEdgeY) upperEdgeY = yValue;
        }
        
        if ( histo != null ) {
            histo.fill(xValue,yValue,weight);
        } else if ( autoConvert() && nEntries == maxEntries ) {
            if ( histo != null ) throw new RuntimeException("Cloud already been converted");
            histo= toShowableHistogram(conversionBinsX(),conversionLowerEdgeX(),conversionUpperEdgeX(),conversionBinsY(),conversionLowerEdgeY(),conversionUpperEdgeY());
            histo.fill(xValue,yValue,weight);
            weights = null;
            xValues = null;
            yValues = null;
            xValuesArray.clear();
            yValuesArray.clear();
            weightsArray.clear();
            xValuesArray = null;
            yValuesArray = null;
            weightsArray = null;
        } else {
            if ( nEntries%arraySize == 0 ) {
                xValues = new double[ arraySize ];
                yValues = new double[ arraySize ];
                weights = new double[ arraySize ];
                xValuesArray.add( xValues );
                yValuesArray.add( yValues );
                weightsArray.add( weights );
            }
            
            xValues[ nEntries%arraySize ] = xValue;
            yValues[ nEntries%arraySize ] = yValue;
            weights[ nEntries%arraySize ] = weight;
            
            if ( ( !Double.isNaN(xValue) ) && ( !Double.isNaN(yValue) ) && ( !Double.isNaN(weight) ) ) {
                sumOfWeights += weight;
                
                meanX += xValue*weight;
                rmsX  += xValue*xValue*weight;
                meanY += yValue*weight;
                rmsY  += yValue*yValue*weight;
                validEntries++;                
            }
            nEntries++;
            if ( nEntries > maxEntries && maxEntries > 0 ) throw new IllegalArgumentException();
        }
        if (isValid) fireStateChanged();
    }


    /**
     * Get the Cloud's x lower edge.
     * @return The Cloud's x lower edge.
     *
     */
    public double lowerEdgeX() {
        return lowerEdgeX;
    }
    /**
     * Get the Cloud's y lower edge.
     * @return The Cloud's y lower edge.
     *
     */
    public double lowerEdgeY() {
        return lowerEdgeY;
    }
    /**
     * Get the Cloud's x upper edge.
     * @return The Cloud's x upper edge.
     *
     */
    public double upperEdgeX() {
        return upperEdgeX;
    }
    /**
     * Get the Cloud's y upper edge.
     * @return The Cloud's y upper edge.
     *
     */
    public double upperEdgeY() {
        return upperEdgeY;
    }
    /**
     * Set the Cloud's x lower edge
     * @param lowerEdgeX The Cloud's x lower edge.
     *
     */
    public void setLowerEdgeX( double lowerEdgeX ) {
        this.lowerEdgeX = lowerEdgeX;
    }
    /**
     * Set the Cloud's y lower edge
     * @param lowerEdgeY The Cloud's y lower edge.
     *
     */
    public void setLowerEdgeY( double lowerEdgeY ) {
        this.lowerEdgeY = lowerEdgeY;
    }
    /**
     * Set the Cloud's x upper edge
     * @param upperEdgeX The Cloud's x upper edge.
     *
     */
    public void setUpperEdgeX( double upperEdgeX ) {
        this.upperEdgeX = upperEdgeX;
    }
    /**
     * Set the Cloud's y upper edge
     * @param upperEdgeY The Cloud's y upper edge.
     *
     */
    public void setUpperEdgeY( double upperEdgeY ) {
        this.upperEdgeY = upperEdgeY;
    }
    /**
     * Get a given x value from the Cloud.
     * @param index The x value's index.
     * @return The Cloud's corresponding x value.
     * @exception RuntimeException if the Cloud has been converted
     *
     */
    public double valueX(int index) {
        if (histo!=null) throw new RuntimeException("Cloud has been converted");
        double[] val = (double[])xValuesArray.get( index/arraySize );
        return val[index%arraySize];
    }
    /**
     * Get a given y value from the Cloud.
     * @param index The y value's index.
     * @return The Cloud's corresponding y value.
     * @exception RuntimeException if the Cloud has been converted
     *
     */
    public double valueY(int index) {
        if (histo!=null) throw new RuntimeException("Cloud has been converted");
        double[] val = (double[])yValuesArray.get( index/arraySize );
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
     * Get the Cloud's x mean.
     * @return The Cloud's x mean.
     *
     */
    public double meanX() {
        if ( histo != null ) return histo.meanX();
        return meanX / sumOfWeights();
    }
    /**
     * Get the Cloud's y mean.
     * @return The Cloud's y mean.
     *
     */
    public double meanY() {
        if ( histo != null ) return histo.meanY();
        return meanY / sumOfWeights();
    }
    /**
     * Get the Cloud's x rms.
     * @return The Cloud's x rms.
     *
     */
    public double rmsX() {
        if ( histo != null ) return histo.rmsX();
        return Math.sqrt( rmsX / sumOfWeights() - meanX*meanX/sumOfWeights()/sumOfWeights() );
    }
    /**
     * Get the Cloud's y rms.
     * @return The Cloud's y rms.
     *
     */
    public double rmsY() {
        if ( histo != null ) return histo.rmsY();
        return Math.sqrt( rmsY / sumOfWeights() - meanY*meanY/sumOfWeights()/sumOfWeights() );
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
     * Convert the Cloud to a Histogram.
     * @param nBinsX     The Histogram's x number of bins.
     * @param lowerEdgeX The Histogram's x lower edge.
     * @param upperEdgeX The Histogram's x upper edge.
     * @param nBinsY     The Histogram's y number of bins.
     * @param lowerEdgeY The Histogram's y lower edge.
     * @param upperEdgeY The Histogram's y upper edge.
     *
     */
    public void convert(int nBinsX, double lowerEdgeX, double upperEdgeX,
    int nBinsY, double lowerEdgeY, double upperEdgeY) {
        if ( histo != null ) throw new RuntimeException("Cloud already been converted");
        histo= toShowableHistogram(nBinsX, lowerEdgeX, upperEdgeX, nBinsY, lowerEdgeY, upperEdgeY);
        if (isValid) fireStateChanged();
    }
    /**
     * Represent the Cloud as a Histogram.
     * @param nBinsX     The Histogram's x number of bins.
     * @param lowerEdgeX The Histogram's x lower edge.
     * @param upperEdgeX The Histogram's x upper edge.
     * @param nBinsY     The Histogram's y number of bins.
     * @param lowerEdgeY The Histogram's y lower edge.
     * @param upperEdgeY The Histogram's y upper edge.
     * @return The Histogram representing the Cloud.
     *
     */
    private IHistogram2D toShowableHistogram(int nBinsX, double lowerEdgeX, double upperEdgeX,
    int nBinsY, double lowerEdgeY, double upperEdgeY) {
        if ( histo != null ) return histo;
        return HistUtils.toShowableHistogram(this, nBinsX, lowerEdgeX, upperEdgeX, nBinsY, lowerEdgeY, upperEdgeY);
    }
    
    /**
     * Convert the ICloud to an IHistogram by specifying the bin edges.
     *
     */
    public void convert( double[] binEdgesX, double[] binEdgesY ) {
        if ( histo != null ) throw new RuntimeException("Cloud already been converted");
        IHistogram2D hist = new Histogram2D(name(),title(),new VariableAxis(binEdgesX),new VariableAxis(binEdgesY),"");
        for(int i=0; i<nEntries; i++) hist.fill( valueX(i), valueY(i), weight(i) );
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
    public IHistogram2D histogram() throws RuntimeException {
        if ( histo == null ) throw new RuntimeException("Cloud has not been converted");
        return histo;
    }
    /**
     * Set the Histogram representation of the Cloud.
     * @param hist The Histogram representing the Cloud.
     *
     */
    public void setHistogram( IHistogram2D hist ) {
        if ( histo != null ) throw new RuntimeException("Cloud already been converted");
        histo = hist;
        if (isValid) fireStateChanged();
    }
    
    public void fillHistogram(hep.aida.IHistogram2D hist2d) {
        if ( histo != null ) throw new IllegalArgumentException("Cloud has already been converted");
        for(int i=0; i<nEntries; i++) hist2d.fill( valueX(i), valueY(i), weight(i) );
    }
    
    public void reset() {
        nEntries = 0;
        lowerEdgeX = Double.NaN;
        upperEdgeX = Double.NaN;
        lowerEdgeY = Double.NaN;
        upperEdgeY = Double.NaN;
        meanX = 0.;
        rmsX = 0.;
        meanY = 0.;
        rmsY = 0.;
        sumOfWeights = 0.;
        if ( histo != null )
            histo.reset();
        histo = null;
        xValuesArray = new ArrayList();
        yValuesArray = new ArrayList();
        weightsArray = new ArrayList();
        xValues = null;
        yValues = null;
        weights = null;
        super.reset();
    }
    
    public void convertToHistogram() {
        if ( histo != null ) throw new IllegalArgumentException("Cloud has already been converted to an Histogram");
        histo= toShowableHistogram(conversionBinsX(),conversionLowerEdgeX(),conversionUpperEdgeX(),conversionBinsY(),conversionLowerEdgeY(),conversionUpperEdgeY());
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
            meanX *= scaleFactor;
            rmsX *= scaleFactor;
            meanY *= scaleFactor;
            rmsY *= scaleFactor;
        }
        if (isValid) fireStateChanged();
    }
    
    public double lowerEdgeXWithMargin() {
        if ( Double.isNaN(lowerEdgeX) )
            return Double.NaN;
        double le = lowerEdgeX != upperEdgeX ? lowerEdgeX : lowerEdgeX - 1;
        double ue = lowerEdgeX != upperEdgeX ? upperEdgeX : upperEdgeX + 1;
        double delta = ue - le;
        return le - margin()*Math.abs(delta);
    }
    public double upperEdgeXWithMargin() {
        if ( Double.isNaN(upperEdgeX) )
            return Double.NaN;
        double le = lowerEdgeX != upperEdgeX ? lowerEdgeX : lowerEdgeX - 1;
        double ue = lowerEdgeX != upperEdgeX ? upperEdgeX : upperEdgeX + 1;
        double delta = ue - le;
        return ue + margin()*Math.abs(delta);
    }
    public double lowerEdgeYWithMargin() {
        if ( Double.isNaN(lowerEdgeY) )
            return Double.NaN;
        double le = lowerEdgeY != upperEdgeY ? lowerEdgeY : lowerEdgeY - 1;
        double ue = lowerEdgeY != upperEdgeY ? upperEdgeY : upperEdgeY + 1;
        double delta = ue - le;
        return le - margin()*Math.abs(delta);
    }
    public double upperEdgeYWithMargin() {
        if ( Double.isNaN(upperEdgeY) )
            return Double.NaN;
        double le = lowerEdgeY != upperEdgeY ? lowerEdgeY : lowerEdgeY - 1;
        double ue = lowerEdgeY != upperEdgeY ? upperEdgeY : upperEdgeY + 1;
        double delta = ue - le;
        return ue + margin()*Math.abs(delta);
    }
    
    /** Get the sum of weights of of all the entries
     * @return The sum of the weights of all the entries.
     *
     */
    public double sumOfWeights() {
        if ( histo != null ) return histo.sumAllBinHeights();
        return sumOfWeights;
    }
    
    protected IHistogram hist() {
        return (IHistogram) histogram();
    }
    
    /**
     * Set the parameters for the ICloud conversion to an IHistogram.
     * @param binsX      The X axis number of bins of the conversion IHistogram.
     * @param lowerEdgeX The X axis lower edge of the conversion IHistogram.
     * @param upperEdgeX The X axis upper edge of the conversion IHistogram.
     * @param binsY      The Y axis number of bins of the conversion IHistogram.
     * @param lowerEdgeY The Y axis lower edge of the conversion IHistogram.
     * @param upperEdgeY The Y axis upper edge of the conversion IHistogram.
     *
     */
    public void setConversionParameters(int binsX, double lowerEdgeX, double upperEdgeX, int binsY, double lowerEdgeY, double upperEdgeY) {
        this.convBinsX = binsX;
        this.convLowerEdgeX = lowerEdgeX;
        this.convUpperEdgeX = upperEdgeX;
        this.convBinsY = binsY;
        this.convLowerEdgeY = lowerEdgeY;
        this.convUpperEdgeY = upperEdgeY;
    }
    
    public int conversionBinsX() {
        return convBinsX;
    }
    
    public double conversionLowerEdgeX() {
        if ( Double.isNaN(convLowerEdgeX) )
            return lowerEdgeXWithMargin();
        return convLowerEdgeX;
    }

    public double conversionUpperEdgeX() {
        if ( Double.isNaN(convUpperEdgeX) )
            return upperEdgeXWithMargin();
        return convUpperEdgeX;
    }

    public int conversionBinsY() {
        return convBinsY;
    }
    
    public double conversionLowerEdgeY() {
        if ( Double.isNaN(convLowerEdgeY) )
            return lowerEdgeYWithMargin();
        return convLowerEdgeY;
    }

    public double conversionUpperEdgeY() {
        if ( Double.isNaN(convUpperEdgeY) )
            return upperEdgeYWithMargin();
        return convUpperEdgeY;
    }

    private int nEntries=0;
    private double lowerEdgeX, upperEdgeX;
    private double lowerEdgeY, upperEdgeY;
    
    private double meanX, rmsX;
    private double meanY, rmsY;
    private IHistogram2D histo;
    protected double sumOfWeights;
    
    private ArrayList xValuesArray = new ArrayList();
    private ArrayList yValuesArray = new ArrayList();
    private ArrayList weightsArray = new ArrayList();
    private double[] xValues,yValues,weights;

    private int convBinsX = 50;
    private double convLowerEdgeX = Double.NaN;
    private double convUpperEdgeX = Double.NaN;
    private int convBinsY = 50;
    private double convLowerEdgeY = Double.NaN;
    private double convUpperEdgeY = Double.NaN;

}
