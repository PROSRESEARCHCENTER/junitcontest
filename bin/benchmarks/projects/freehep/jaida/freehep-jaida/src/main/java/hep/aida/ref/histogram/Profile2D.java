package hep.aida.ref.histogram;

/**
 * Implementation of IProfile1D.
 * 
 * @author The AIDA team at SLAC.
 *
 */

import hep.aida.IAxis;
import hep.aida.IProfile2D;
import hep.aida.ref.event.IsObservable;

public class Profile2D extends Profile implements hep.aida.IProfile2D, IsObservable {
    
    private Histogram3D hist;
    
    /**
     * Create a 1-dimensional Profile.
     *
     */
    public Profile2D() {
        super("","",2);
    }
    
    /**
     * Create a 1-dimensional Profile.
     * @param name  The name of the Profile as a ManagedObject.
     * @param title The title of the Profile.
     * @param xAxis  The x-axis of the Profile.
     * @param yAxis  The y-axis of the Profile.
     *
     */
    protected Profile2D(String name, String title, IAxis xAxis, IAxis yAxis) {
        this( name, title, xAxis, yAxis, "");
    }
    
    protected Profile2D(String name, String title, IAxis xAxis, IAxis yAxis, String options) {
        super(name, title, 2, options);
        initProfile2D( xAxis, yAxis, options );
    }
    
    public void reset() {
        hist.reset();
        if (isValid) fireStateChanged();
    }
    
    public double binMeanX( int indexX, int indexY ) {
        return hist.binMeanX(indexX,indexY,0);
    }
    
    public double binMeanY( int indexX, int indexY ) {
        return hist.binMeanY(indexX,indexY,0);
    }
    
    public int binEntries(int indexX, int indexY) {
        return hist.binEntries(indexX, indexY, 0);
    }
    
    public int binEntriesX(int indexX) {
        return hist.binEntriesX(indexX);
    }
    
    public int binEntriesY(int indexY) {
        return hist.binEntriesY(indexY);
    }
    
    public double binError(int indexX, int indexY) {
        if ( binEntries(indexX,indexY) == 0 )
            return 0;
        double sOfWeights = hist.binHeight(indexX, indexY, 0);
        if ( sOfWeights != 0 ) return Math.abs(hist.binMeanZ(indexX, indexY,0)/sOfWeights);
        return 0.;
    }
    
    public double binHeight(int indexX, int indexY) {
        if ( binEntries(indexX,indexY) == 0 )
            return 0;
        return hist.binMeanZ(indexX, indexY, 0);
    }
    
    public double binHeightX(int indexX) {
        return hist.binHeightX(indexX);
    }

    public double binHeightY(int indexY) {
        return hist.binHeightY(indexY);
    }
    
    public double binRms(int indexX, int indexY) {
        if ( binEntries(indexX,indexY) == 0 )
            return 0;
        return hist.binRmsZ(indexX, indexY, 0);
    }
    
    public int coordToIndexX(double coord) {
        return hist.xAxis().coordToIndex(coord);
    }
    
    public int coordToIndexY(double coord) {
        return hist.yAxis().coordToIndex(coord);
    }
    
    public void fill(double x, double y, double z) throws java.lang.IllegalArgumentException {
        hist.fill(x, y, z);
        if (isValid) fireStateChanged();
    }
    
    public void fill(double x, double y, double z, double weight) throws java.lang.IllegalArgumentException {
        hist.fill(x, y, z, weight);
        if (isValid) fireStateChanged();
    }
        
    public double meanX() {
        return hist.meanX();
    }
    
    public double meanY() {
        return hist.meanY();
    }
    
    public double rmsX() {
        return hist.rmsX();
    }
    
    public double rmsY() {
        return hist.rmsY();
    }
    
    public IAxis xAxis() {
        return hist.xAxis();
    }
    
    public IAxis yAxis() {
        return hist.yAxis();
    }
    
    public int entries() {
        return hist.entries();
    }
    
    public int allEntries() {
        return hist.allEntries();
    }

    public int extraEntries() {
        int n = 0;
        for (int i=xAxis().bins(); --i >= -2;)
            for (int j=yAxis().bins(); --j >= -2;) 
                if ( i<0 || j<0 ) n += binEntries(i,j);
        return n;
    }
    
    public double maxBinHeight() {
        double max=Double.NaN;
        for(int i=1; i<=xAxis().bins(); i++)
            for(int j=1; j<=yAxis().bins(); j++)
                if(Double.isNaN(max) || binHeight(i,j) >= max) max=binHeight(i,j);
        return max;
    }
    
    public double minBinHeight() {
        double min=Double.NaN;
        for(int i=1; i<=xAxis().bins(); i++)
            for(int j=1; j<=yAxis().bins(); j++)
                if(Double.isNaN(min) || binHeight(i,j) <= min) min=binHeight(i,j);
        return min;
    }
    
    public double sumAllBinHeights() {
        double sum = 0;
        for (int i=xAxis().bins(); --i >= -2;)
            for (int j=yAxis().bins(); --j >= -2;)
                sum += binHeight(i,j);
        return sum;
    }
    
    public double sumBinHeights() {
        return sumAllBinHeights() - sumExtraBinHeights();
    }

    public double sumExtraBinHeights() {
        int sum = 0;
        for (int i=xAxis().bins(); --i >= -2;)
            for (int j=yAxis().bins(); --j >= -2;)
                if ( i<0 || j<0 ) sum += binHeight(i,j);
        return sum;
    }
    
    public void scale(double scaleFactor) throws java.lang.IllegalArgumentException {
        hist.scale(scaleFactor);
        if (isValid) fireStateChanged();
    }
    
    public void add(IProfile2D profile) throws IllegalArgumentException {
        hist.add( ((Profile2D)profile).histogram() );
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
    protected Histogram3D histogram() {
        return hist;
    }
    protected void setHistogram(Histogram3D hist) {
        this.hist = hist;
    }

    public void initProfile2D( IAxis xAxis, IAxis yAxis ) {
        initProfile2D(xAxis, yAxis, "");
    }
    public void initProfile2D( IAxis xAxis, IAxis yAxis, String options ) {
        IAxis zAxis = new FixedAxis(1, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
        hist = new Histogram3D(name(), title(), xAxis, yAxis, zAxis, options);
        reset();
    }
    
    public void setRmsX(double rmsX) {
        hist.setRmsX(rmsX);
    }

    public void setRmsY(double rmsY) {
        hist.setRmsY(rmsY);
    }

    public void setMeanX(double meanX) {
        hist.setMeanX(meanX);
    }

    public void setMeanY(double meanY) {
        hist.setMeanY(meanY);
    }
        
    public void setNEntries(int entries)
    {
       hist.setNEntries(entries);
    }
    public void setValidEntries(int entries)
    {
       hist.setValidEntries(entries);
    }
    
    public void setContents(double[][] heights, double[][] errors, int[][] entries, double[][] rmss, double[][] meanXs, double[][] meanYs) {
        int binX = xAxis().bins()+2;
        int binY = yAxis().bins()+2;
        
        int[][][] n  = new int[binX][binY][3];
        double[][][] h  = new double[binX][binY][3];
        double[][][] e  = new double[binX][binY][3];
        double[][][] mx = new double[binX][binY][3];
        double[][][] rx = new double[binX][binY][3];
        double[][][] my = new double[binX][binY][3];
        double[][][] ry = new double[binX][binY][3];
        double[][][] mz = new double[binX][binY][3];
        double[][][] rz = new double[binX][binY][3];
        
        for ( int i = 0; i < binX; i++ ) {
            for ( int j = 0; j < binY; j++ ) {
                if ( errors[i][j] != 0 ) h[i][j][1]  = Math.abs(heights[i][j]/errors[i][j]);
                e[i][j][1]  = errors[i][j];
                n[i][j][1]  = entries[i][j];
                mx[i][j][1] = meanXs[i][j];
                my[i][j][1] = meanYs[i][j];
                mz[i][j][1] = heights[i][j];
                rz[i][j][1] = Math.abs(rmss[i][j]);
            }
        }
        
        hist.setContents(h,e,n,mx,rx,my,ry,mz,rz);
    }
     
}
