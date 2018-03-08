package hep.aida.ref.histogram;

/**
 * Implementation of IProfile1D.
 *
 * @author The AIDA team at SLAC.
 *
 */

import hep.aida.IAxis;
import hep.aida.IProfile1D;
import hep.aida.ref.event.IsObservable;
import hep.aida.ref.histogram.binner.Binner2D;

public class Profile1D extends Profile implements hep.aida.IProfile1D, IsObservable {
    
    private Histogram2D hist;
    
    /**
     * Create a 1-dimensional Profile.
     *
     */
    public Profile1D() {
        super("","",1);
    }
    
    /**
     * Create a 1-dimensional Profile.
     * @param name  The name of the Profile as a ManagedObject.
     * @param title The title of the Profile.
     * @param axis  The x-axis of the Profile.
     *
     */
    protected Profile1D(String name, String title, IAxis axis) {
        this(name, title, axis, "" );
    }
    
    protected Profile1D(String name, String title, IAxis axis, String options) {
        super(name, title, 1, options);
        initProfile1D(axis, options);
    }
    
    public void reset() {
        hist.reset();
        if (isValid) fireStateChanged();
    }
    
    public int entries() {
        return hist.entries();
    }
    
    public int allEntries() {
        return hist.allEntries();
    }
    
    public int extraEntries() {
        return binEntries(IAxis.UNDERFLOW_BIN) + binEntries(IAxis.OVERFLOW_BIN);
    }
    
    public double sumAllBinHeights() {
        double sum = 0;
        for (int i=axis().bins(); --i >= -2;)
            sum += binHeight(i);
        return sum;
    }
    
    public double sumBinHeights() {
        return sumAllBinHeights() - sumExtraBinHeights();
    }
    
    public double sumExtraBinHeights() {
        return binHeight(IAxis.UNDERFLOW_BIN) + binHeight(IAxis.OVERFLOW_BIN);
    }
    
    public double minBinHeight() {
        double min=Double.NaN;
        for(int i=1; i<=axis().bins(); i++)
            if(Double.isNaN(min) || binHeight(i) <= min) min=binHeight(i);
        return min;
    }
    
    public double maxBinHeight() {
        double max=Double.NaN;
        for(int i=1; i<=axis().bins(); i++)
            if(Double.isNaN(max) || binHeight(i) >= max) max=binHeight(i);
        return max;
    }
    
    public void fill(double x, double y, double weight) {
        hist.fill(x, y, weight);
        if (isValid) fireStateChanged();
    }
    
    public void fill(double x, double y) {
        fill(x, y, 1.);
    }
    
    public int binEntries(int index) {
        return hist.binEntries(index,0);
    }
    
    public double binHeight(int index) {
        if ( binEntries(index) == 0 )
            return 0;
        return hist.binMeanY(index,0);
        
    }
    
    public double binError(int index ) {
        if ( binEntries(index) == 0 )
            return 0;
        double sOfWeights = hist.binHeight(index,0);
        if ( sOfWeights != 0 ) return Math.abs(hist.binMeanY(index,0)/sOfWeights);
        return 0.;
    }
    
    public double binMean(int index) {
        return hist.binMeanX(index,0);
    }
    
    public double binRms(int index) {
        if ( binEntries(index) == 0 )
            return 0;
        return hist.binRmsY(index,0);
    }
    
    public double mean() {
        return hist.meanX();
    }
    
    public double rms() {
        return hist.rmsX();
    }
    
    public IAxis axis() {
        return hist.xAxis();
    }
    
    public int coordToIndex(double coord) {
        return axis().coordToIndex(coord);
    }
    
    public void scale(double scaleFactor) throws IllegalArgumentException {
        hist.scale( scaleFactor );
        if (isValid) fireStateChanged();
    }
    
    public void add(IProfile1D profile) throws IllegalArgumentException {
        hist.add( ((Profile1D)profile).histogram() );
        if (isValid) fireStateChanged();
    }
    
    /**
     * non-AIDA methods down here.
     *
     */
    
    /**
     * Get the internal histogram.
     *
     */
    protected Histogram2D histogram() {
        return hist;
    }
    protected void setHistogram(Histogram2D hist) {
        this.hist = hist;
    }
    
    public void initProfile1D( IAxis axis ) {
        initProfile1D(axis, "");
    }
    
    public void initProfile1D( IAxis axis, String options ) {
        IAxis yAxis = new FixedAxis(1, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
        hist = new Histogram2D(name(), title(), axis, yAxis, options);
        reset();
    }
    
    public void setRms(double rms) {
        hist.setRmsX(rms);
    }
    
    public void setMean(double mean) {
        hist.setMeanX(mean);
    }
    
    /**
     * Set the content of the whole Histogram at once. This is a convenience method for saving/restoring Histograms.
     * Of the arguments below the heights, the errors and the entries array cannot be null.
     * If the means array is null, the mean is defaulted to the geometric center of the bin.
     * If the rms array is null, the rms is taken to be the bin width over the root of 12.
     *
     * @param heights The bins heights
     * @param errors The bins errors
     * @param entries The bin entries.
     * @param rmss The rmss of the bins
     * @param meanXs The mean of the bin.
     *
     */
    public void setContents(double[] heights, double[] errors, int[] entries, double[] rmss, double[] meanXs) {
        int binX = axis().bins()+2;
        
        int[][] n  = new int[binX][3];
        double[][] h  = new double[binX][3];
        double[][] e  = new double[binX][3];
        double[][] mx = new double[binX][3];
        double[][] rx = new double[binX][3];
        double[][] my = new double[binX][3];
        double[][] ry = new double[binX][3];
        
        for ( int i = 0; i < binX; i++ ) {
            if ( errors[i] != 0 ) h[i][1]  = Math.abs(heights[i]/errors[i]);
            e[i][1]  = errors[i];
            n[i][1]  = entries[i];
            if (meanXs != null) mx[i][1] = meanXs[i];
            my[i][1] = heights[i];
            if (rmss != null) ry[i][1] = Math.abs(rmss[i]);
        }
        
        hist.setContents(h,e,n,mx,rx,my,ry);
    }
    public void setNEntries(int entries) {
        hist.setNEntries(entries);
    }
    public void setValidEntries(int entries) {
        hist.setValidEntries(entries);
    }
    
    public Binner2D binner() {
        return hist.binner();
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
     * <li>[5] : weight * y
     * <li>[6] : weight * y * y
     * <li>[7] : weight * x * y
     */
    public double[] getStatistics() {
      return hist.getStatistics();
    }

}
