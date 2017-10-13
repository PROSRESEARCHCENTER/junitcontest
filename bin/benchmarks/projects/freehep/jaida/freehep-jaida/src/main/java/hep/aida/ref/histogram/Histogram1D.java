package hep.aida.ref.histogram;

/**
 * Implementation of IHistogram1D.
 * @author The AIDA Team at SLAC.
 * @version $Id: Histogram1D.java 14498 2013-03-15 23:15:22Z onoprien $
 *
 */

import hep.aida.IAxis;
import hep.aida.IHistogram1D;
import hep.aida.ref.event.IsObservable;
import hep.aida.ref.histogram.binner.AbstractBinner1D;
import hep.aida.ref.histogram.binner.BasicBinner1D;
import hep.aida.ref.histogram.binner.Binner1D;
import hep.aida.ref.histogram.binner.BinnerMath;
import hep.aida.ref.histogram.binner.EfficiencyBinner1D;

import java.util.Map;

public class Histogram1D extends Histogram implements IHistogram1D, IsObservable {
    
    /**
     * Create a 1-dimensional Histogram.
     *
     */
    public Histogram1D(){
        super("","",1, "");
    }
    
    /**
     * Create a 1-dimensional Histogram.
     * @param name The name of the Histogram as a ManagedObject.
     * @param title The title of the Histogram.
     * @param axis The x-axis of the Histogram.
     *
     */
    public Histogram1D(String name, String title, IAxis axis) {
        this(name,title,axis,"");
    }
    
    /**
     * Create a 1-dimensional Histogram.
     * @param name The name of the Histogram as a ManagedObject.
     * @param title The title of the Histogram.
     * @param axis The x-axis of the Histogram.
     * @param options options of the Histogram.
     *
     */
    protected Histogram1D(String name, String title, IAxis axis, String options) {
        super(name, title, 1, options);
        initHistogram1D(axis,options);
    }
    
    /**
     * Fill the Histogram with unit weight.
     * @param x The value to be filled.
     *
     */
    public void fill(double x) {
        fill(x,1.);
    }
    
    /**
     * Fill the Histogram.
     * @param x The value to be filled.
     * @param weight The weight for this entry.
     *
     */
    public void fill(double x, double weight) {
        if ( ! isFillable() ) throw new UnfillableHistogramException();
        allEntries++;
        if ( (! Double.isNaN(x)) && (!Double.isNaN(weight)) ) {
            int coordToIndex = xAxis.coordToIndex(x);
            int bin = mapBinNumber(coordToIndex, axis());
            /*
            double x0 = 0d;
            if ( coordToIndex == IAxis.UNDERFLOW_BIN ) {
                x0 = axis().lowerEdge();
            } else if ( coordToIndex == IAxis.OVERFLOW_BIN ) {
                x0 = axis().upperEdge();
            } else {
                x0 = axis().binCenter(coordToIndex);
            }
             */
            binner1D.fill(bin, x, weight);
            
            if ( coordToIndex >= 0 || useOutflows() ) {
                double delata = x - center;
                validEntries++;
                mean += delata*weight;
                rms  += delata*delata*weight;
                sumOfWeights += weight;
                sumOfWeightsSquared += weight*weight;
            }
        }
        
        if (isValid) fireStateChanged();
    }
    
    /**
     * Reset the Histogram. After calling this method the Histogram
     * is as it was just created.
     *
     */
    public void reset() {
        binner1D.clear();
        setBinCenters();
        mean = 0;
        rms  = 0;
        center = (xAxis.upperEdge() + xAxis.lowerEdge())/2;
        super.reset();
    }
    
    private void setBinCenters() {
        double x0 = 0;
        for (int i=0; i<axis().bins()+2; i++) {
            int mi;
            if ( i == 0 ) {
                x0 = axis().lowerEdge();
            } else if ( i == axis().bins()+1 ) {
                x0 = axis().upperEdge();
            } else {
                mi = i - 1;
                x0 = axis().binCenter(mi);
            }
            binner1D.setBinCenter(i, x0);
        }
    }
    
    /**
     * Get the number of entries in the underflow and overflow bins.
     * @return The number of entries outside the range of the Histogram.
     *
     */
    public int extraEntries() {
        return binEntries(IAxis.UNDERFLOW_BIN) + binEntries(IAxis.OVERFLOW_BIN);
    }
    
    /**
     * Get the sum of the bin heights for all the entries, in-range and out-range ones.
     * @return The sum of all the bin's heights.
     *
     */
    public double sumAllBinHeights() {
        double sum = 0;
        for (int i=xAxis.bins(); --i >= -2; )
            sum += binHeight(i);
        return sum;
    }
    
    /**
     * Get the sum of the bin heights for all the entries outside the Histogram's range.
     * @return The sum of the out of range bin's heights.
     *
     */
    public double sumExtraBinHeights() {
        return binHeight(IAxis.UNDERFLOW_BIN) + binHeight(IAxis.OVERFLOW_BIN);
    }
    
    /**
     * Get the minimum height of in-range bins in the Histogram.
     * @return The minimum bin height for in range bins.
     *
     */
    public double minBinHeight() {
        double min=Double.NaN;
        for(int i=0; i<xAxis.bins(); i++)
            if(Double.isNaN(min) || binHeight(i) <= min) min=binHeight(i);
        return min;
    }
    
    /**
     * Get the maximum height of in-range bins in the Histogram.
     * @return The maximum bin height for in range bins.
     *
     */
    public double maxBinHeight() {
        double max=Double.NaN;
        for(int i=0; i<xAxis.bins(); i++)
            if(Double.isNaN(max) || binHeight(i) >= max) max=binHeight(i);
        return max;
    }
    
    /**
     * Number of entries in the corresponding bin (ie the number of times fill was called for this bin).
     * @param index the bin number in the external representation: (0...N-1) or OVERFLOW or UNDERFLOW.
     * @return The number of entries for the corresponding bin.
     *
     */
    public int binEntries(int index) {
        return binner1D.entries(mapBinNumber(index, axis()));
    }
    
    /**
     * Total height of the corresponding bin.
     * @param index The bin number in the external representation: (0...N-1) or OVERFLOW or UNDERFLOW.
     * @return The bin height for the corresponding bin.
     *
     */
    public double binHeight(int index) {
        return binner1D.height(mapBinNumber(index, axis()));
    }
    
    /**
     * The error on this bin.
     * @param index the bin number in the external representation: (0...N-1) or OVERFLOW or UNDERFLOW.
     * @return The error on the corresponding bin.
     *
     */
    public double binError(int index) {
        return binner1D.plusError(mapBinNumber(index, axis()));
    }
    
    
    /**
     * Get the mean of the whole Histogram. It includes all the entries (in and out of range).
     * @return The mean of the Histogram.
     *
     */
    public double mean() {
        if ( validEntries != 0 ) return mean/sumOfWeights + center;
        return 0;
    }
    
    /**
     * Get the RMS of the whole Histogram. It includes all the entries (in and out of range).
     * @return The RMS of the Histogram.
     *
     */
    public double rms(){
        if ( validEntries != 0 ) return Math.sqrt((rms - mean*mean/sumOfWeights)/sumOfWeights);
        return 0;
    }
    
    /**
     * Set the rms of the Histogram.
     * @param rms The Historam's x rms
     *
     */
    //public void setRms( double rms ) {
    //    this.rms = rms*rms*sumOfWeights + mean*mean*sumOfWeights;
    //}
    
    /**
     * Set the mean of the Histogram.
     * @param mean The Histogram's x mean
     *
     */
    public void setMeanAndRms( double otherMean, double otherRms ) {
        this.meanAndRmsIsSet = true;
        this.mean = (otherMean - center)*sumOfWeights;
        this.rms = otherRms*otherRms*sumOfWeights + (otherMean - center)*(otherMean - center)*sumOfWeights;
    }
    
    /**
     * Get the X axis.
     * @return The x axis.
     *
     */
    public IAxis axis() {
        return xAxis;
    }
    
    /**
     * Convenience method, equivalent to <tt>axis().coordToIndex(coord)</tt>.
     * @see IAxis#coordToIndex(double)
     */
    public int coordToIndex(double coord) {
        return axis().coordToIndex(coord);
    }
    
    /**
     * Scale the weights and the errors by a given factor.
     *
     */
    public void scale(double scaleFactor) throws IllegalArgumentException {
        if ( scaleFactor <= 0 ) throw new IllegalArgumentException("Illegal scale factor "+scaleFactor+" it has to be positive");
        binner1D.scale(scaleFactor);
        mean *= scaleFactor;
        rms  *= scaleFactor;
        sumOfWeights *= scaleFactor;
        sumOfWeightsSquared *= scaleFactor*scaleFactor;
        if (isValid) fireStateChanged();
    }
    
    /**
     * Modifies this histogram by adding the contents of h to it.
     *
     * @param hist The histogram to be added to this histogram
     * @throws IllegalArgumentException if histogram binnings are incompatible
     */
    public void add(IHistogram1D hist) throws IllegalArgumentException {
        HistMath.checkCompatibility(axis(), hist.axis());
        int bins = axis().bins()+2;
        boolean h1Aida = !(hist instanceof Histogram1D);
        if (!h1Aida) {
            BinnerMath.add(binner1D, binner1D, ((Histogram1D) hist).binner());
            initHistogram1D(binner1D);
        } else {
            double[] newHeights = new double[bins];
            double[] newErrors  = new double[bins];
            double[] newMeans   = new double[bins];
            double[] newRmss    = new double[bins];
            int[]    newEntries = new int   [bins];
            double rms2 = 0;
            
            for(int i=IAxis.UNDERFLOW_BIN; i<bins-2;i++) {
                double height1 = binHeight(i);
                double height2 = hist.binHeight(i);
                double h       = height1+height2;
                double mean1   = binMean(i);
                double mean2   = hist.binMean(i);
                mean1 = HistUtils.isValidDouble(mean1) ? mean1 : 0;
                mean2 = HistUtils.isValidDouble(mean2) ? mean2 : 0;
                double m    = 0;
                double rms1 = binRms(i);
                if (h1Aida) rms2 = (hist.axis().binUpperEdge(i)-hist.axis().binLowerEdge(i))/Math.sqrt(12);
                else rms2 = ((Histogram1D) hist).binRms(i);
                double r    = 0;
                if ( h != 0 ) {
                    m = ( mean1*height1 + mean2*height2 )/(height1+height2);
                    r = Math.sqrt(((rms1*rms1*height1 + mean1*mean1*height1)+(rms2*rms2*height2 + mean2*mean2*height2))/h - m*m);
                }
                
                int bin = mapBinNumber(i,axis());
                newHeights[bin] = h;
                newErrors [bin] = Math.sqrt( Math.pow(binError(i),2) + Math.pow(hist.binError(i),2) );
                newEntries[bin] = binEntries(i)+hist.binEntries(i);
                newMeans  [bin] = m;
                newRmss   [bin] = r;
            }
            setContents(newHeights,newErrors,newEntries,newMeans,newRmss);
            setMeanAndRms( hist.mean(), hist.rms() );
        }
    }
    
    
    /**
     *
     * All the non-AIDA methods should go below this point.
     *
     */
    
    
    /**
     * Get the mean of a bin.
     * @param index The bin number in the external representation: (0...N-1) or OVERFLOW or UNDERFLOW.
     * @return The mean of the corresponding bin. If the bin has zero height, zero is returned.
     *
     */
    public double binMean(int index) {
        int bin = mapBinNumber(index, axis());
        double m = binner1D.mean(bin);
        if (Double.isNaN(m)) return binner1D.binCenter(bin);
        return m + binner1D.binCenter(bin);
    }
    
    /**
     * Get the RMS of a bin.
     * @param index the bin number in the external representation:(0...N-1) or OVERFLOW or UNDERFLOW.
     * @return The RMS of the corresponding bin. If the bin has zero height, zero is returned.
     *
     */
    public double binRms(int index) {
        double r = binner1D.rms(mapBinNumber(index, axis()));
        return Double.isNaN(r) ? axis().binWidth(index)/Math.sqrt(12) : r;
    }
    
    /**
     * Set the content of the whole Histogram at once. This is a convenience method for saving/restoring
     * Histograms. Of the arguments below the heights array cannot be null. The errors array should in
     * general be non-null, but this depends on the specific binner.
     * The entries array can be null, in which case the entry of a bin is taken to be the integer part
     * of the height.
     * If the means array is null, the mean is defaulted to the geometric center of the bin.
     * If the rms array is null, the rms is taken to be the bin width over the root of 12.
     *
     * @param heights The bins heights
     * @param errors The bins errors
     * @param entries The bin entries.
     * @param means The means of the bins.
     * @param rmss The rmss of the bins
     *
     */
    public void setContents(double[] heights, double[] errors, int[] entries, double[] means, double[] rmss ) {
        reset();
        
        double[] newCenters = null; //new double[heights.length];
        for (int i=0; i<axis().bins()+2; i++) {
            double h = heights[i];
            double m = 0;
            //newCenters[i] = 0; //binner1D.binCenter(i);
            if ( means != null && !Double.isInfinite(means[i])) {
                m = means[i]*h;
                means[i] = m;
            }
            
            double r;
            if ( rmss != null ) {
                r = (rmss[i]*rmss[i] + m*m)*h;
                rmss[i] = r;
            }
        }
       
        setContents(newCenters, heights, errors, entries, null, means, rmss);
    }
    
    /**
     * @param binCenters The bins centers, can be null
     * @param heights The bins heights, can NOT be null
     * @param errors The bins errors
     * @param entries The bin entries, can be null
     * @param sumWW The sumWW of the bins, can be null
     * @param sumXW The sumXW of the bins, can not be null
     * @param sumXXW The sumXXW of the bins, can not be null
     *
     */
    public void setContents(double[] binCenters, double[] heights, double[] errors, int[] entries, double[] sumWW, double[] sumXW, double[] sumXXW ) {
        reset();
        for (int i=0; i<axis().bins()+2; i++) {
            int mi;
            if ( i == 0 ) {
                mi = IAxis.UNDERFLOW_BIN;
            } else if ( i == axis().bins()+1 ) {
                mi = IAxis.OVERFLOW_BIN;
            } else {
                mi = i - 1;
            }
            
            double b;
            if ( binCenters != null ) {
                b = binCenters[i];
            } else
                b = 0;
            
            double h = heights[i];
            int e;
            if ( entries != null )
                e = entries[i];
            else
                e = (int) h;
            
            double w;
            if ( sumWW != null ) {
                w = sumWW[i];
            } else
                w = Double.NaN;
            
            double m = Double.NaN;
            if ( sumXW != null ) {
                m = sumXW[i];
            } 
            if (!HistUtils.isValidDouble(m)) {
                m = binner1D.binCenter(i)*h;
            }
            
            double r = Double.NaN;
            if ( sumXXW != null ) {
                r = sumXXW[i];
            }
            if (!HistUtils.isValidDouble(r)) {
                r = (axis().binUpperEdge(mi)-axis().binLowerEdge(mi))/Math.sqrt(12);
                if (h != 0) r = r*r*h + m*m/h;
                else r = 0; // protection against NaN
            }
            binner1D.setBinContent(i, b, e, h, errors[i], errors[i], w, m, r);
        }
        initHistogram1D(binner1D);
    }

    /**
     * Returns an array of statistic sums for this histograms.
     * The array contains sums of the following quantities over all valid entries (w = weight) :
     * <ul>
     * <li>[0] : 1
     * <li>[1] : weight
     * <li>[2] : weight * weight
     * <li>[3] : weight * x
     * <li>[4] : weight * x * x
     */
    public double[] getStatistics() {
      double[] out = new double[5];
      out[0] = validEntries;
      out[1] = sumOfWeights;
      out[2] = sumOfWeightsSquared;
      out[3] = mean + center * sumOfWeights;
      out[4] = rms + 2.*center*mean + center*center*sumOfWeights;
      return out;
    }
    
    public void initHistogram1D( IAxis xAxis, String options ) {
        initHist1D(xAxis, options);
    }
    
    /**
     * Inits global histogram parameters only: mean, rms, etc.
     */
    void initHistogram1D( Binner1D b2 ) {
        mean = 0;
        rms  = 0;
        center = (xAxis.upperEdge() + xAxis.lowerEdge())/2;
        super.reset();

        for (int i=0; i<axis().bins()+2; i++) {
            int mi;
            if ( i == 0 ) {
                mi = IAxis.UNDERFLOW_BIN;
            } else if ( i == axis().bins()+1 ) {
                mi = IAxis.OVERFLOW_BIN;
            } else {
                mi = i - 1;
            }
            
            allEntries += b2.entries(i);
            double h = b2.height(i);
            double m = b2.sumXW(i);
            double r = b2.sumXXW(i);
            if ( mi >= 0 || useOutflows() && HistUtils.isValidDouble(h)) {
                if ( HistUtils.isValidDouble(m) ) {
                    double d = b2.binCenter(i) - center;
                    mean   += m + d*h;
                    rms    += r + d*(2*m + d*h);
                }
                validEntries += b2.entries(i);
                sumOfWeights += h;
                sumOfWeightsSquared += h*h;
            }
        }
    }
    void initHist1D( IAxis xAxis, String options ) {
        this.xAxis = xAxis;
        Map optionMap = hep.aida.ref.AidaUtils.parseOptions( options );
        String type = (String) optionMap.get("type");
        if ( type == null || type.equals("default") ) {
            binner1D = new BasicBinner1D(xAxis.bins()+2);
        } else if ( type.equals("efficiency") ) {
            binner1D = new EfficiencyBinner1D(xAxis.bins()+2);
        } else
            throw new IllegalArgumentException("Wrong histogram type "+type);
        
        String useOutflowsString = (String) optionMap.get("useOutflowsInStatistics");
        if ( useOutflowsString != null )
            setUseOutflows( Boolean.valueOf(useOutflowsString).booleanValue() );
        reset();
    }
    
    
    public AbstractBinner1D binner() {
        return binner1D;
    }
    
    private double mean = 0;
    private double rms = 0;
    private double center = 0;
    private IAxis xAxis;
    private AbstractBinner1D binner1D;
    
}
