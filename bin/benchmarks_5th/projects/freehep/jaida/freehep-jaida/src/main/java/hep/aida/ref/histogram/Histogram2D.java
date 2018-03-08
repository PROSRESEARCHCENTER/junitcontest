package hep.aida.ref.histogram;

/**
 * Implementation of IHistogram2D.
 * @author The AIDA Team at SLAC.
 * @version $Id: Histogram2D.java 14505 2013-04-01 22:53:53Z onoprien $
 *
 */

import hep.aida.IAxis;
import hep.aida.IHistogram2D;
import hep.aida.ref.histogram.binner.BasicBinner2D;
import hep.aida.ref.histogram.binner.Binner2D;
import hep.aida.ref.histogram.binner.EfficiencyBinner2D;

import java.util.Map;

public class Histogram2D extends Histogram implements IHistogram2D {
    
    /**
     * Create a 2-dimensional Histogram.
     */
    public Histogram2D(){
        super("","",2, "");
    }
    
    /**
     * Create a 2-dimensional Histogram.
     * @param name The name of the Histogram as a ManagedObject.
     * @param title The title of the Histogram.
     * @param xAxis The x-axis of the Histogram.
     * @param yAxis The y-axis of the Histogram.
     *
     */
    public Histogram2D(String name, String title, IAxis xAxis, IAxis yAxis) {
        this(name,title,xAxis,yAxis,"");
    }
    
    /**
     * Create a 2-dimensional Histogram.
     * @param name The name of the Histogram as a ManagedObject.
     * @param title The title of the Histogram.
     * @param xAxis The x-axis of the Histogram.
     * @param yAxis The y-axis of the Histogram.
     * @param options The options of the Histogram.
     *
     */
    protected Histogram2D(String name, String title, IAxis xAxis, IAxis yAxis, String options) {
        super(name, title, 2, options);
        initHistogram2D(xAxis,yAxis,options);
    }
    
    /**
     * Fill the Histogram with unit weight.
     * @param x The x value to be filled.
     * @param y The y value to be filled.
     *
     */
    public void fill(double x, double y) {
        fill(x, y, 1.);
    }
    
    /**
     * Fill the Histogram.
     * @param x The x value to be filled.
     * @param y The y value to be filled.
     * @param weight The weight for this entry.
     *
     */
    public void fill( double x, double y, double weight) {
        if ( ! isFillable() ) throw new UnfillableHistogramException();
        allEntries++;
        if ( (! Double.isNaN(x)) && (!Double.isNaN(y)) && (!Double.isNaN(weight))) {
            int xCoordToIndex = xAxis.coordToIndex(x);
            int yCoordToIndex = yAxis.coordToIndex(y);
            int xBin = mapBinNumber(xCoordToIndex, xAxis());
            int yBin = mapBinNumber(yCoordToIndex, yAxis());
            binner2D.fill(xBin, yBin, x, y, weight);
            if ( ( xCoordToIndex >= 0 && yCoordToIndex >= 0) || useOutflows() ) {
                validEntries++;
                meanX += x*weight;
                rmsX  += x*x*weight;
                meanY += y*weight;
                rmsY  += y*y*weight;
                sumOfWeights += weight;
                sumOfWeightsSquared += weight*weight;
                rmsXY += x*y*weight;
            }
        }
        if (isValid) fireStateChanged();
    }
    
    /**
     * Reset the Histogram. After calling this method the Histogram
     * is as it was just created.
     */
    public void reset() {
        binner2D.clear();
        meanX = 0;
        rmsX  = 0;
        meanY = 0;
        rmsY  = 0;
        rmsXY = 0;
        super.reset();
    }
    
    /**
     * Get the number of entries in the underflow and overflow bins.
     * @return The number of entries outside the range of the Histogram.
     *
     */
    public int extraEntries() {
        int n = 0;
        for (int i=xAxis.bins(); --i >= -2;)
            for (int j=yAxis.bins(); --j >= -2;)
                if ( i<0 || j<0 ) n += binEntries(i,j);
        return n;
    }
    
    /**
     * Get the sum of the bin heights for all the entries, in-range and out-range ones.
     * @return The sum of all the bin's heights.
     *
     */
    public double sumAllBinHeights() {
        double sum = 0;
        for (int i=xAxis.bins(); --i >= -2;)
            for (int j=yAxis.bins(); --j >= -2;)
                sum += binHeight(i,j);
        return sum;
    }
    
    /**
     * Get the sum of the bin heights for all the entries outside the Histogram's range.
     * @return The sum of the out of range bin's heights.
     *
     */
    public double sumExtraBinHeights() {
        int sum = 0;
        for (int i=xAxis.bins(); --i >= -2;)
            for (int j=yAxis.bins(); --j >= -2;)
                if ( i<0 || j<0 ) sum += binHeight(i,j);
        return sum;
    }
    
    /**
     * Get the minimum height of in-range bins in the Histogram.
     * @return The minimum bin height for in range bins.
     *
     */
    public double minBinHeight() {
        double min=Double.NaN;
        for(int i=1; i<=xAxis.bins(); i++)
            for(int j=1; j<=yAxis.bins(); j++)
                if(Double.isNaN(min) || binHeight(i,j) <= min) min=binHeight(i,j);
        return min;
    }
    
    /**
     * Get the maximum height of in-range bins in the Histogram.
     * @return The maximum bin height for in range bins.
     *
     */
    public double maxBinHeight() {
        double max=Double.NaN;
        for(int i=1; i<=xAxis.bins(); i++)
            for(int j=1; j<=yAxis.bins(); j++)
                if(Double.isNaN(max) || binHeight(i,j) >= max) max=binHeight(i,j);
        return max;
    }
    
    /**
     * Number of entries in the corresponding bin (ie the number of times fill was called for this bin).
     * @param indexX the x bin number in the external representation: (0...N-1) or OVERFLOW or UNDERFLOW.
     * @param indexY the y bin number in the external representation: (0...N-1) or OVERFLOW or UNDERFLOW.
     * @return The number of entries for the corresponding bin.
     *
     */
    public int binEntries(int indexX, int indexY ) {
        return binner2D.entries(mapBinNumber(indexX, xAxis()),mapBinNumber(indexY, yAxis()));
    }
    
    /**
     * Number of entries with a given x bin number (ie the number of times fill was called for these bins).
     * Equivalent to <tt>projectionX().binEntries(indexX)</tt>.
     * @param indexX the x bin number in the external representation: (0...N-1) or OVERFLOW or UNDERFLOW.
     * @return The number of entries for the corresponding bins.
     */
    public int binEntriesX(int indexX) {
        int n = 0;
        for (int j=IAxis.UNDERFLOW_BIN; j<yAxis().bins(); j++)
            n += binEntries(indexX,j);
        return n;
    }
    
    /**
     * Number of entries with a given y bin number (ie the number of times fill was called for these bins).
     * Equivalent to <tt>projectionY().binEntries(indexY)</tt>.
     * @param indexY the y bin number in the external representation: (0...N-1) or OVERFLOW or UNDERFLOW.
     * @return The number of entries for the corresponding bins.
     */
    public int binEntriesY(int indexY) {
        int n = 0;
        for (int i=IAxis.UNDERFLOW_BIN; i<xAxis().bins(); i++)
            n += binEntries(i,indexY);
        return n;
    }
    
    /**
     * Total height of the corresponding bin.
     * @param indexX The x bin number in the external representation: (0...N-1) or OVERFLOW or UNDERFLOW.
     * @param indexY The y bin number in the external representation: (0...N-1) or OVERFLOW or UNDERFLOW.
     * @return The bin height for the corresponding bin.
     *
     */
    public double binHeight(int indexX, int indexY) {
        return binner2D.height(mapBinNumber(indexX, xAxis()),mapBinNumber(indexY, yAxis()));
    }
    
    /**
     * Total height of the corresponding x bin along y.
     * Equivalent to <tt>projectionX().binHeight(indexX)</tt>.
     * @param indexX The x bin number in the external representation: (0...N-1) or OVERFLOW or UNDERFLOW.
     * @return The bin height for the corresponding bin.
     *
     */
    public double binHeightX(int indexX) {
        double d = 0;
        for (int j=IAxis.UNDERFLOW_BIN; j<yAxis().bins(); j++)
            d += binHeight(indexX,j);
        return d;
    }
    
    /**
     * Total height of the corresponding y bin along x.
     * Equivalent to <tt>projectionY().binHeight(indexY)</tt>.
     * @param indexY The y bin number in the external representation: (0...N-1) or OVERFLOW or UNDERFLOW.
     * @return The bin height for the corresponding bin.
     *
     */
    public double binHeightY(int indexY) {
        double d = 0;
        for (int i=IAxis.UNDERFLOW_BIN; i<xAxis().bins(); i++)
            d += binHeight(i, indexY);
        return d;
    }
    
    /**
     * The error on this bin.
     * @param indexX The x bin number in the external representation: (0...N-1) or OVERFLOW or UNDERFLOW.
     * @param indexY The y bin number in the external representation: (0...N-1) or OVERFLOW or UNDERFLOW.
     * @return The error on the corresponding bin.
     *
     */
    public double binError(int indexX,int indexY) {
        return binner2D.plusError(mapBinNumber(indexX, xAxis()),mapBinNumber(indexY, yAxis()));
    }
    
    /**
     * Get the mean of the whole Histogram as projected on the x axis. It includes all the entries (in and out of range).
     * @return The mean of the Histogram on the x axis.
     *
     */
    public double meanX() {
        if( validEntries != 0) return meanX/sumOfWeights;
        return 0;
    }
    
    /**
     * Get the mean of the whole Histogram as projected on the y axis. It includes all the entries (in and out of range).
     * @return The mean of the Histogram on the y axis.
     *
     */
    public double meanY() {
        if( validEntries != 0) return meanY/sumOfWeights;
        return 0;
    }
    
    /**
     * Get the RMS of the whole Histogram as projected on the x axis. It includes all the entries (in and out of range).
     * @return The RMS of the Histogram on the x axis.
     *
     */
    public double rmsX(){
        if ( validEntries != 0 ) return Math.sqrt(rmsX/sumOfWeights - meanX*meanX/sumOfWeights/sumOfWeights);
        return 0;
    }
    
    /**
     * Get the RMS of the whole Histogram as projected on the y axis. It includes all the entries (in and out of range).
     * @return The RMS of the Histogram on the y axis.
     *
     */
    public double rmsY(){
        if ( validEntries != 0 ) return Math.sqrt(rmsY/sumOfWeights - meanY*meanY/sumOfWeights/sumOfWeights);
        return 0;
    }
    
    /**
     * Get the X axis.
     * @return The x axis.
     *
     */
    public IAxis xAxis() {
        return xAxis;
    }
    
    /**
     * Get the Y axis.
     * @return The y axis.
     *
     */
    public IAxis yAxis() {
        return yAxis;
    }
    
    /**
     * Convenience method, equivalent to <tt>xAxis().coordToIndex(coord)</tt>.
     * @see IAxis#coordToIndex(double)
     * @return The bin's index along the x axis corresponding to the position coordX.
     *
     */
    public int coordToIndexX(double coordX) {
        return xAxis().coordToIndex(coordX);
    }
    
    /**
     * Convenience method, equivalent to <tt>yAxis().coordToIndex(coord)</tt>.
     * @see IAxis#coordToIndex(double)
     * @return The bin's index along the y axis corresponding to the position coordY.
     *
     */
    public int coordToIndexY(double coordY) {
        return yAxis().coordToIndex(coordY);
    }
    
    /**
     * Scale the weights and the errors by a given factor.
     *
     */
    public void scale(double scaleFactor) {
        if ( scaleFactor <= 0 ) throw new IllegalArgumentException("Illegal scale factor "+scaleFactor+" it has to be positive");
        binner2D.scale(scaleFactor);
        meanX *= scaleFactor;
        rmsX  *= scaleFactor;
        meanY *= scaleFactor;
        rmsY  *= scaleFactor;
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
    public void add(IHistogram2D hist) throws IllegalArgumentException {
        HistMath.checkCompatibility(xAxis(), hist.xAxis());
        HistMath.checkCompatibility(yAxis(), hist.yAxis());
        int xbins = xAxis().bins()+2;
        int ybins = yAxis().bins()+2;
        double[][] newHeights = new double[xbins][ybins];
        double[][] newErrors  = new double[xbins][ybins];
        double[][] newMeanXs  = new double[xbins][ybins];
        double[][] newRmsXs   = new double[xbins][ybins];
        double[][] newMeanYs  = new double[xbins][ybins];
        double[][] newRmsYs   = new double[xbins][ybins];
        int[][] newEntries = new    int[xbins][ybins];
        double rmsx2 = 0;
        double rmsy2 = 0;
        boolean h1Aida = !(hist instanceof Histogram2D);
        for(int i=IAxis.UNDERFLOW_BIN; i<xAxis().bins(); i++) {
            for(int j=IAxis.UNDERFLOW_BIN; j<yAxis().bins(); j++) {
                
                double height1 = binHeight(i,j);
                double height2 = hist.binHeight(i,j);
                double h       = height1+height2;
                double meanx1  = binMeanX(i,j);
                double meanx2  = hist.binMeanX(i,j);
                meanx1 = HistUtils.isValidDouble(meanx1) ? meanx1 : 0; 
                meanx2 = HistUtils.isValidDouble(meanx2) ? meanx2 : 0; 
                double mx      = 0;
                double rmsx1   = binRmsX(i,j);
                double rx      = 0;
                double meany1  = binMeanY(i,j);
                double meany2  = hist.binMeanY(i,j);
                meany1 = HistUtils.isValidDouble(meany1) ? meany1 : 0; 
                meany2 = HistUtils.isValidDouble(meany2) ? meany2 : 0; 
                double my      = 0;
                double rmsy1   = binRmsY(i,j);
                if (h1Aida) {
                    rmsx2 = (hist.xAxis().binUpperEdge(i)-hist.xAxis().binLowerEdge(i))/Math.sqrt(12);
                    rmsy2 = (hist.yAxis().binUpperEdge(j)-hist.yAxis().binLowerEdge(j))/Math.sqrt(12);
                } else {
                    rmsx2 = ((Histogram2D) hist).binRmsX(i, j);
                    rmsy2 = ((Histogram2D) hist).binRmsY(i, j);
                }
                double ry      = 0;
                if ( h != 0 ) {
                    mx = ( meanx1*height1 + meanx2*height2 )/(height1+height2);
                    rx = Math.sqrt(((rmsx1*rmsx1*height1 + meanx1*meanx1*height1)+(rmsx2*rmsx2*height2 + meanx2*meanx2*height2))/h - mx*mx);
                    my = ( meany1*height1 + meany2*height2 )/(height1+height2);
                    ry = Math.sqrt(((rmsy1*rmsy1*height1 + meany1*meany1*height1)+(rmsy2*rmsy2*height2 + meany2*meany2*height2))/h - my*my);
                }
                
                int binx = mapBinNumber(i,xAxis());
                int biny = mapBinNumber(j,yAxis());
                newHeights[binx][biny] = h;
                newErrors [binx][biny] = Math.sqrt( Math.pow(binError(i,j),2) + Math.pow(hist.binError(i,j),2) );
                newEntries[binx][biny] = binEntries(i,j)+hist.binEntries(i,j);
                newMeanXs [binx][biny] = mx;
                newRmsXs  [binx][biny] = rx;
                newMeanYs [binx][biny] = my;
                newRmsYs  [binx][biny] = ry;
            }
        }
        setContents(newHeights,newErrors,newEntries,newMeanXs,newRmsXs,newMeanYs,newRmsYs);
    }
    
    /**
     *
     * All the non-AIDA methods should go below this point.
     *
     */
    
    public void setMeanX(double meanX) {
        this.meanX = meanX*sumOfWeights;
    }
    
    public void setRmsX(double rmsX) {
        this.rmsX = rmsX*rmsX*sumOfWeights + meanX()*meanX()*sumOfWeights;
    }
    
    public void setMeanY(double meanY) {
        this.meanY = meanY*sumOfWeights;
    }
    
    public void setRmsY(double rmsY) {
        this.rmsY = rmsY*rmsY*sumOfWeights + meanY()*meanY()*sumOfWeights;
    }
    
    /**
     * Get the mean of a bin along the x axis.
     * @param indexX The x bin number in the external representation: (0...N-1) or OVERFLOW or UNDERFLOW.
     * @param indexY The y bin number in the external representation: (0...N-1) or OVERFLOW or UNDERFLOW.
     * @return The mean of the corresponding bin along x. If the bin has zero height, zero is returned.
     *
     */
    public double binMeanX(int indexX, int indexY) {
        int binx = mapBinNumber(indexX, xAxis());
        int biny = mapBinNumber(indexY, yAxis());
        double m = binner2D.meanX(binx, biny);
        return Double.isNaN(m) ? xAxis().binCenter(indexX) : m;
    }
    
    /**
     * Get the mean of a bin along the y axis.
     * @param indexX The x bin number in the external representation: (0...N-1) or OVERFLOW or UNDERFLOW.
     * @param indexY The y bin number in the external representation: (0...N-1) or OVERFLOW or UNDERFLOW.
     * @return The mean of the corresponding bin along y. If the bin has zero height, zero is returned.
     *
     */
    public double binMeanY(int indexX, int indexY) {
        int binx = mapBinNumber(indexX, xAxis());
        int biny = mapBinNumber(indexY, yAxis());
        double m = binner2D.meanY(binx, biny);
        return Double.isNaN(m) ? yAxis().binCenter(indexY) : m;
    }
    
    /**
     * Get the RMS of a bin along the x axis.
     * @param indexX The x bin number in the external representation:(0...N-1) or OVERFLOW or UNDERFLOW.
     * @param indexY The y bin number in the external representation:(0...N-1) or OVERFLOW or UNDERFLOW.
     * @return The RMS of the corresponding bin along x. If the bin has zero height, zero is returned.
     *
     */
    public double binRmsX(int indexX, int indexY) {
        int binx = mapBinNumber(indexX, xAxis());
        int biny = mapBinNumber(indexY, yAxis());
        double r = binner2D.rmsX(binx, biny);
        return Double.isNaN(r) ? xAxis().binWidth(indexX) : r;
    }
    
    /**
     * Get the RMS of a bin along the y axis.
     * @param indexX The x bin number in the external representation:(0...N-1) or OVERFLOW or UNDERFLOW.
     * @param indexY The y bin number in the external representation:(0...N-1) or OVERFLOW or UNDERFLOW.
     * @return The RMS of the corresponding bin along y. If the bin has zero height, zero is returned.
     *
     */
    public double binRmsY(int indexX, int indexY) {
        int binx = mapBinNumber(indexX, xAxis());
        int biny = mapBinNumber(indexY, yAxis());
        double r = binner2D.rmsY(binx, biny);
        return Double.isNaN(r) ? yAxis().binWidth(indexY) : r;
    }
    
    /**
     * Set the error on this bin.
     * @param indexX the bin number (0...N-1) or OVERFLOW or UNDERFLOW.
     * @param indexY the bin number (0...N-1) or OVERFLOW or UNDERFLOW.
     * @param error the error.
     */
    public void setBinError(int indexX, int indexY, double error) {
        int binx = mapBinNumber(indexX, xAxis());
        int biny = mapBinNumber(indexY, yAxis());
        binner2D.setBinContent(binx,biny,binEntries(indexX,indexY),binHeight(indexX,indexY),error, error,binMeanX(indexX,indexY),binRmsX(indexX,indexY),binMeanY(indexX,indexY),binRmsY(indexX,indexY));
    }
    
    /**
     * Set the content of the whole Histogram at once. This is a convenience method for saving/restoring Histograms.
     * Of the arguments below the heights array cannot be null. The errors array should in general be non-null, but this depends on
     * the specific binner.
     * The entries array can be null, in which case the entry of a bin is taken to be the integer part of the height.
     * If the means array is null, the mean is defaulted to the geometric center of the bin.
     * If the rms array is null, the rms is taken to be the bin width over the root of 12.
     *
     *
     * @param heights The bin heights
     * @param errors The bin errors
     * @param entries The bin entries
     * @param meanXs The means of the bin along the x axis
     * @param rmsXs The rmss of the bin along the x axis
     * @param meanYs The means of the bin along the y axis
     * @param rmsYs The rmss of the bin along the y axis
     *
     */
    public void setContents(double[][] heights, double[][] errors, int[][] entries, double[][] meanXs, double[][] rmsXs, double[][] meanYs, double[][] rmsYs) {
        reset();
        
        for (int i=0; i<xAxis().bins()+2; i++) {
            int mi;
            if ( i == 0 )
                mi = IAxis.UNDERFLOW_BIN;
            else if ( i == xAxis().bins()+1 )
                mi = IAxis.OVERFLOW_BIN;
            else
                mi = i - 1;
            
            for (int j=0; j<yAxis().bins()+2; j++) {
                int mj;
                if ( j == 0 )
                    mj = IAxis.UNDERFLOW_BIN;
                else if ( j == yAxis().bins()+1 )
                    mj = IAxis.OVERFLOW_BIN;
                else
                    mj = j - 1;
                
                double h = heights[i][j];
                
                double mx;
                if ( meanXs != null )
                    mx = meanXs[i][j];
                else
                    mx = (xAxis().binLowerEdge(mi)+xAxis().binUpperEdge(mi))/2.;
                
                double my;
                if ( meanYs != null )
                    my = meanYs[i][j];
                else
                    my = (yAxis().binLowerEdge(mj)+yAxis().binUpperEdge(mj))/2.;
                
                double rx;
                if ( rmsXs != null )
                    rx = rmsXs[i][j];
                else
                    rx = (xAxis().binUpperEdge(mi)-xAxis().binLowerEdge(mi))/Math.sqrt(12);
                
                double ry;
                if ( rmsYs != null )
                    ry = rmsYs[i][j];
                else
                    ry = (yAxis().binUpperEdge(mj)-yAxis().binLowerEdge(mj))/Math.sqrt(12);
                
                int e;
                if ( entries != null )
                    e = entries[i][j];
                else
                    e = (int)h;
                
                binner2D.setBinContent(i,j,e,h,errors[i][j],errors[i][j],mx,rx,my,ry);
                
                h = binner2D.height(i,j);
                allEntries+= e;
                
                if ( ( mi >= 0 && mj >= 0 ) || useOutflows() ) {
                    if ( ! Double.isNaN(mx) && ! Double.isNaN(my) && ! Double.isInfinite(mx) && ! Double.isInfinite(my) ) {
                        meanX += mx*h;
                        rmsX  += rx*rx*h+mx*mx*h;
                        meanY += my*h;
                        rmsY  += ry*ry*h+my*my*h;
                    }
                    validEntries += e;
                    sumOfWeights += h;
                    sumOfWeightsSquared += h*h;
                }
            }
        }
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
      double[] out = new double[8];
      out[0] = validEntries;
      out[1] = sumOfWeights;
      out[2] = sumOfWeightsSquared;
      out[3] = meanX;
      out[4] = rmsX;
      out[5] = meanY;
      out[6] = rmsY;
      out[7] = rmsXY;
      return out;
    }
    
    public Binner2D binner() {
      return binner2D;
    }
    
    public void initHistogram2D( IAxis xAxis, IAxis yAxis, String options ) {
        this.xAxis = xAxis;
        this.yAxis = yAxis;
        Map optionMap = hep.aida.ref.AidaUtils.parseOptions( options );
        String type = (String) optionMap.get("type");
        if ( type == null || type.equals("default"))
            binner2D = new BasicBinner2D(xAxis.bins()+2, yAxis.bins()+2);
        else if ( type.equals("efficiency") )
            binner2D = new EfficiencyBinner2D(xAxis.bins()+2, yAxis.bins()+2);
        else
            throw new IllegalArgumentException("Wrong histogram type "+type);
        
        String useOutflowsString = (String) optionMap.get("useOutflowsInStatistics");
        if ( useOutflowsString != null )
            setUseOutflows( Boolean.valueOf(useOutflowsString).booleanValue() );
        reset();
    }
    
    private double meanX = 0, rmsX = 0;
    private double meanY = 0, rmsY = 0;
    private double rmsXY = 0.;
    private IAxis xAxis, yAxis;
    private Binner2D binner2D;
}
