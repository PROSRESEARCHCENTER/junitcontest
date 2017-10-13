/*
 * Cloud3D.java
 *
 * Created on February 26, 2001, 3:21 PM
 */

package hep.aida.ref.histogram;
import hep.aida.ICloud3D;
import hep.aida.IHistogram;
import hep.aida.IHistogram3D;

import java.util.ArrayList;

/**
 *
 * @author  The AIDA team @ SLAC.
 *
 */
public class Cloud3D extends Cloud implements ICloud3D {
    /**
     * Create a new Cloud3D
     */
    public Cloud3D() {
        super("","",3,0,"");
    }
    
    /**
     * Create a new Cloud3D
     * @param name  The Cloud's name.
     * @param title The Cloud's title.
     * @param nMax  The maximum number of entries stored in the Cloud. If nMax is greater than zero the Cloud
     *              will be converted to an Histogram when the number of entries is more than nMax.
     * @param options Some options.
     *
     */
    protected Cloud3D(String name,String title,int nMax,String options) {
        super(name,title,3,nMax,options);
    }
    /**
     * Fill the Cloud with new values with unit weight
     * @param xValue The x value to add to the Cloud.
     * @param yValue The y value to add to the Cloud.
     * @param zValue The z value to add to the Cloud.
     * @return <code>true</code> if the fill was successful.
     *
     */
    public void fill(double xValue, double yValue, double zValue) {
        fill(xValue,yValue,zValue,1.0);
    }
    /**
     * Fill the Cloud with new values with given weight
     * @param xValue The x value to add to the Cloud.
     * @param yValue The y value to add to the Cloud.
     * @param zValue The z value to add to the Cloud.
     * @param weight The values weight.
     * @return <code>true</code> if the fill was successful.
     *
     */
    public void fill(double xValue, double yValue, double zValue, double weight) {
        if (nEntries == 0) {
            lowerEdgeX = upperEdgeX = xValue;
            lowerEdgeY = upperEdgeY = yValue;
            lowerEdgeZ = upperEdgeZ = zValue;
        }
        else {
            if (xValue<lowerEdgeX) lowerEdgeX = xValue;
            if (xValue>upperEdgeX) upperEdgeX = xValue;
            if (yValue<lowerEdgeY) lowerEdgeY = yValue;
            if (yValue>upperEdgeY) upperEdgeY = yValue;
            if (zValue<lowerEdgeZ) lowerEdgeZ = zValue;
            if (zValue>upperEdgeZ) upperEdgeZ = zValue;
        }
        
        if ( histo != null ) {
            histo.fill(xValue,yValue,zValue,weight);
        } else if ( autoConvert() && nEntries == maxEntries ) {
            if ( histo != null ) throw new RuntimeException("Cloud already been converted");
            histo = toShowableHistogram(conversionBinsX(),conversionLowerEdgeX(),conversionUpperEdgeX(),conversionBinsY(),conversionLowerEdgeY(),conversionUpperEdgeY(),conversionBinsZ(),conversionLowerEdgeZ(),conversionUpperEdgeZ());
            histo.fill(xValue,yValue,zValue,weight);
            weights = null;
            xValues = null;
            yValues = null;
            zValues = null;
            xValuesArray.clear();
            yValuesArray.clear();
            zValuesArray.clear();
            weightsArray.clear();
            xValuesArray = null;
            yValuesArray = null;
            zValuesArray = null;
            weightsArray = null;
        } else {
            if ( nEntries%arraySize == 0 ) {
                xValues = new double[ arraySize ];
                yValues = new double[ arraySize ];
                zValues = new double[ arraySize ];
                weights = new double[ arraySize ];
                xValuesArray.add( xValues );
                yValuesArray.add( yValues );
                zValuesArray.add( zValues );
                weightsArray.add( weights );
            }
            
            xValues[ nEntries%arraySize ] = xValue;
            yValues[ nEntries%arraySize ] = yValue;
            zValues[ nEntries%arraySize ] = zValue;
            weights[ nEntries%arraySize ] = weight;
            
            if ( ( !Double.isNaN(xValue) ) && ( !Double.isNaN(yValue) ) && ( !Double.isNaN(zValue) ) && ( !Double.isNaN(weight) ) ) {
                sumOfWeights += weight;
                
                meanX += xValue*weight;
                rmsX  += xValue*xValue*weight;
                meanY += yValue*weight;
                rmsY  += yValue*yValue*weight;
                meanZ += zValue*weight;
                rmsZ  += zValue*zValue*weight;
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
     * Get the Cloud's z lower edge.
     * @return The Cloud's z lower edge.
     *
     */
    public double lowerEdgeZ() {
        return lowerEdgeZ;
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
     * Get the Cloud's z upper edge.
     * @return The Cloud's z upper edge.
     *
     */
    public double upperEdgeZ() {
        return upperEdgeZ;
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
     * Set the Cloud's z lower edge
     * @param lowerEdgeZ The Cloud's z lower edge.
     *
     */
    public void setLowerEdgeZ( double lowerEdgeZ ) {
        this.lowerEdgeZ = lowerEdgeZ;
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
     * Set the Cloud's z upper edge
     * @param upperEdgeZ The Cloud's z upper edge.
     *
     */
    public void setUpperEdgeZ( double upperEdgeZ ) {
        this.upperEdgeZ = upperEdgeZ;
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
     * Get a given z value from the Cloud.
     * @param index The z value's index.
     * @return The Cloud's corresponding z value.
     * @exception RuntimeException if the Cloud has been converted
     *
     */
    public double valueZ(int index) {
        if (histo!=null) throw new RuntimeException("Cloud has been converted");
        double[] val = (double[])zValuesArray.get( index/arraySize );
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
     * Get the Cloud's z mean.
     * @return The Cloud's z mean.
     *
     */
    public double meanZ() {
        if ( histo != null ) return histo.meanZ();
        return meanZ / sumOfWeights();
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
     * Get the Cloud's z rms.
     * @return The Cloud's z rms.
     *
     */
    public double rmsZ() {
        if ( histo != null ) return histo.rmsZ();
        return Math.sqrt( rmsZ / sumOfWeights() - meanZ*meanZ/sumOfWeights()/sumOfWeights() );
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
     * @param nBinsZ     The Histogram's z number of bins.
     * @param lowerEdgeZ The Histogram's z lower edge.
     * @param upperEdgeZ The Histogram's z upper edge.
     *
     */
    public void convert(int nBinsX, double lowerEdgeX, double upperEdgeX,
    int nBinsY, double lowerEdgeY, double upperEdgeY,
    int nBinsZ, double lowerEdgeZ, double upperEdgeZ) {
        if ( histo != null ) throw new RuntimeException("Cloud already been converted");
        histo= toShowableHistogram(nBinsX, lowerEdgeX, upperEdgeX,
        nBinsY, lowerEdgeY, upperEdgeY,
        nBinsZ, lowerEdgeZ, upperEdgeZ);
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
     * @param nBinsZ     The Histogram's z number of bins.
     * @param lowerEdgeZ The Histogram's z lower edge.
     * @param upperEdgeZ The Histogram's z upper edge.
     * @return The Histogram representing the Cloud.
     *
     */
    private IHistogram3D toShowableHistogram(int nBinsX, double lowerEdgeX, double upperEdgeX,
    int nBinsY, double lowerEdgeY, double upperEdgeY,
    int nBinsZ, double lowerEdgeZ, double upperEdgeZ) {
        if ( histo != null ) return histo;
        return HistUtils.toShowableHistogram(this,
        nBinsX, lowerEdgeX, upperEdgeX,
        nBinsY, lowerEdgeY, upperEdgeY,
        nBinsZ, lowerEdgeZ, upperEdgeZ);
    }
    
    public void convert( double[] binEdgesX, double[] binEdgesY, double[] binEdgesZ ) {
        if ( histo != null ) throw new RuntimeException("Cloud already been converted");
        
        IHistogram3D hist = new Histogram3D(name(), title(),
        new VariableAxis(binEdgesX),
        new VariableAxis(binEdgesY),
        new VariableAxis(binEdgesZ));
        for(int i=0; i<nEntries; i++) hist.fill( valueX(i), valueY(i), valueZ(i), weight(i) );
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
    public IHistogram3D histogram() throws RuntimeException {
        if ( histo == null ) throw new RuntimeException("Cloud has not been converted");
        return histo;
    }
    /**
     * Set the Histogram representation of the Cloud.
     * @param hist The Histogram representing the Cloud.
     *
     */
    public void setHistogram( IHistogram3D hist ) {
        if ( histo != null ) throw new RuntimeException("Cloud already been converted");
        histo = hist;
        if (isValid) fireStateChanged();
    }
    
    public void fillHistogram(hep.aida.IHistogram3D hist3d) {
        if ( histo != null ) throw new IllegalArgumentException("Cloud has already been converted");
        for(int i=0; i<nEntries; i++) hist3d.fill( valueX(i), valueY(i), valueZ(i), weight(i) );
    }
    
    public void reset() {
        nEntries = 0;
        lowerEdgeX = Double.NaN;
        upperEdgeX = Double.NaN;
        lowerEdgeY = Double.NaN;
        upperEdgeY = Double.NaN;
        lowerEdgeZ = Double.NaN;
        upperEdgeZ = Double.NaN;
        meanX = 0.;
        rmsX = 0.;
        meanY = 0.;
        rmsY = 0.;
        meanZ = 0.;
        rmsZ = 0.;
        sumOfWeights = 0.;
        if ( histo != null )
            histo.reset();
        histo = null;
        xValuesArray = new ArrayList();
        yValuesArray = new ArrayList();
        zValuesArray = new ArrayList();
        weightsArray = new ArrayList();
        xValues = null;
        yValues = null;
        zValues = null;
        weights = null;
        super.reset();
    }
    
    public void convertToHistogram() {
        if ( histo != null ) throw new IllegalArgumentException("Cloud has already been converted to an Histogram");
        histo = toShowableHistogram(conversionBinsX(),conversionLowerEdgeX(),conversionUpperEdgeX(),conversionBinsY(),conversionLowerEdgeY(),conversionUpperEdgeY(),conversionBinsZ(),conversionLowerEdgeZ(),conversionUpperEdgeZ());
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
            meanZ *= scaleFactor;
            rmsZ *= scaleFactor;
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
    public double lowerEdgeZWithMargin() {
        if ( Double.isNaN(lowerEdgeZ) )
            return Double.NaN;
        double le = lowerEdgeZ != upperEdgeZ ? lowerEdgeZ : lowerEdgeZ - 1;
        double ue = lowerEdgeZ != upperEdgeZ ? upperEdgeZ : upperEdgeZ + 1;
        double delta = ue - le;
        return le - margin()*Math.abs(delta);
    }
    public double upperEdgeZWithMargin() {
        if ( Double.isNaN(upperEdgeZ) )
            return Double.NaN;
        double le = lowerEdgeZ != upperEdgeZ ? lowerEdgeZ : lowerEdgeZ - 1;
        double ue = lowerEdgeZ != upperEdgeZ ? upperEdgeZ : upperEdgeZ + 1;
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
     * @param binsZ      The Z axis number of bins of the conversion IHistogram.
     * @param lowerEdgeZ The Z axis lower edge of the conversion IHistogram.
     * @param upperEdgeZ The Z axis upper edge of the conversion IHistogram.
     *
     */
    public void setConversionParameters(int binsX, double lowerEdgeX, double upperEdgeX, int binsY, double lowerEdgeY, double upperEdgeY, int binsZ, double lowerEdgeZ, double upperEdgeZ) {
        this.convBinsX = binsX;
        this.convLowerEdgeX = lowerEdgeX;
        this.convUpperEdgeX = upperEdgeX;
        this.convBinsY = binsY;
        this.convLowerEdgeY = lowerEdgeY;
        this.convUpperEdgeY = upperEdgeY;
        this.convBinsZ = binsZ;
        this.convLowerEdgeZ = lowerEdgeZ;
        this.convUpperEdgeZ = upperEdgeZ;
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

    public int conversionBinsZ() {
        return convBinsZ;
    }
    
    public double conversionLowerEdgeZ() {
        if ( Double.isNaN(convLowerEdgeZ) )
            return lowerEdgeZWithMargin();
        return convLowerEdgeZ;
    }

    public double conversionUpperEdgeZ() {
        if ( Double.isNaN(convUpperEdgeZ) )
            return upperEdgeZWithMargin();
        return convUpperEdgeZ;
    }


    private int nEntries=0;
    private double lowerEdgeX = Double.NaN, upperEdgeX = Double.NaN;
    private double lowerEdgeY = Double.NaN, upperEdgeY = Double.NaN;
    private double lowerEdgeZ = Double.NaN, upperEdgeZ = Double.NaN;
    private double meanX, rmsX;
    private double meanY, rmsY;
    private double meanZ, rmsZ;
    
    private IHistogram3D histo;
    protected double sumOfWeights;
    
    private ArrayList xValuesArray = new ArrayList();
    private ArrayList yValuesArray = new ArrayList();
    private ArrayList zValuesArray = new ArrayList();
    private ArrayList weightsArray = new ArrayList();
    private double[] xValues,yValues,zValues,weights;

    private int convBinsX = 50;
    private double convLowerEdgeX = Double.NaN;
    private double convUpperEdgeX = Double.NaN;
    private int convBinsY = 50;
    private double convLowerEdgeY = Double.NaN;
    private double convUpperEdgeY = Double.NaN;
    private int convBinsZ = 50;
    private double convLowerEdgeZ = Double.NaN;
    private double convUpperEdgeZ = Double.NaN;  
    
    
}


