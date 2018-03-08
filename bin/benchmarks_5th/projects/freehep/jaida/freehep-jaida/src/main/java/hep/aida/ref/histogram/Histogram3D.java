package hep.aida.ref.histogram;

/**
 * Implementation of IHistogram3D.
 * @author The AIDA Team at SLAC.
 * @version $Id: Histogram3D.java 9095 2006-10-06 17:53:30Z serbo $
 *
 */

import hep.aida.IAxis;
import hep.aida.IHistogram3D;
import hep.aida.ref.histogram.binner.BasicBinner3D;
import hep.aida.ref.histogram.binner.Binner3D;
import hep.aida.ref.histogram.binner.EfficiencyBinner3D;

import java.util.Map;

public class Histogram3D extends Histogram implements IHistogram3D {
    
    /**
     * Create a 3-dimensional Histogram.
     */
    public Histogram3D(){
        super("","",3, "");
    }
    
    /**
     * Create a 3-dimensional Histogram.
     * @param name The name of the Histogram as a ManagedObject.
     * @param title The title of the Histogram.
     * @param xAxis The x-axis of the Histogram.
     * @param yAxis The y-axis of the Histogram.
     * @param zAxis The z-axis of the Histogram.
     *
     */
    protected Histogram3D(String name, String title, IAxis xAxis, IAxis yAxis, IAxis zAxis) {
        this(name,title,xAxis,yAxis,zAxis,"");
    }
    
    /**
     * Create a 3-dimensional Histogram.
     * @param name The name of the Histogram as a ManagedObject.
     * @param title The title of the Histogram.
     * @param xAxis The x-axis of the Histogram.
     * @param yAxis The y-axis of the Histogram.
     * @param zAxis The z-axis of the Histogram.
     * @param options The options of the Histogram.
     *
     */
    protected Histogram3D(String name, String title, IAxis xAxis, IAxis yAxis, IAxis zAxis, String options) {
        super(name, title, 3, options);
        initHistogram3D(xAxis,yAxis,zAxis,options);
    }
    
    /**
     * Fill the Histogram with unit weight.
     * @param x The x value to be filled.
     * @param y The y value to be filled.
     * @param z The z value to be filled.
     *
     */
    public void fill(double x, double y, double z) {
        fill(x, y, z, 1.);
    }
    
    /**
     * Fill the Histogram.
     * @param x The x value to be filled.
     * @param y The y value to be filled.
     * @param z The z value to be filled.
     * @param weight The weight for this entry.
     *
     */
    public void fill( double x, double y, double z, double weight) {
        if ( ! isFillable() ) throw new UnfillableHistogramException();
        allEntries++;
        
        if ( (! Double.isNaN(x)) && (!Double.isNaN(y)) && (!Double.isNaN(z)) && (!Double.isNaN(weight))) {
            int xCoordToIndex = xAxis.coordToIndex(x);
            int yCoordToIndex = yAxis.coordToIndex(y);
            int zCoordToIndex = zAxis.coordToIndex(z);
            int xBin = mapBinNumber(xCoordToIndex, xAxis());
            int yBin = mapBinNumber(yCoordToIndex, yAxis());
            int zBin = mapBinNumber(zCoordToIndex, zAxis());
            binner3D.fill(xBin,yBin,zBin,x,y,z,weight);
            
            if ( ( xCoordToIndex >= 0 && yCoordToIndex >= 0 && zCoordToIndex >= 0) || useOutflows() ) {
                validEntries++;
                meanX += x*weight;
                rmsX += x*x*weight;
                meanY += y*weight;
                rmsY += y*y*weight;
                meanZ += z*weight;
                rmsZ += z*z*weight;
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
        binner3D.clear();
        meanX = 0;
        rmsX = 0;
        meanY = 0;
        rmsY = 0;
        meanZ = 0;
        rmsZ = 0;
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
                for (int k=zAxis.bins(); --k >= -2;) {
            if ( i<0 || j<0 || k<0 ) n += binEntries(i,j,k);
                }
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
                for (int k=zAxis.bins(); --k >= -2;)
                    sum += binHeight(i,j,k);
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
                for (int k=zAxis.bins(); --k >= -2;)
                    if ( i<0 || j<0 || k<0 ) sum += binHeight(i,j,k);
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
                for(int k=1; k<=zAxis.bins(); k++)
                    if(Double.isNaN(min) || binHeight(i,j,k) <= min) min=binHeight(i,j,k);
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
                for(int k=1; k<=zAxis.bins(); k++)
                    if(Double.isNaN(max) || binHeight(i,j,k) >= max) max=binHeight(i,j,k);
        return max;
    }
    
    /**
     * Number of entries in the corresponding bin (ie the number of times fill was called for this bin).
     * @param indexX The x bin number in the external representation: (0...N-1) or OVERFLOW or UNDERFLOW.
     * @param indexY The y bin number in the external representation: (0...N-1) or OVERFLOW or UNDERFLOW.
     * @param indexZ The z bin number in the external representation: (0...N-1) or OVERFLOW or UNDERFLOW.
     * @return The number of entries for the corresponding bin.
     *
     */
    public int binEntries(int indexX, int indexY, int indexZ) {
        return binner3D.entries(mapBinNumber(indexX, xAxis()),mapBinNumber(indexY, yAxis()),mapBinNumber(indexZ,zAxis()));
    }
    
    /**
     * Number of entries with a given x bin number (ie the number of times fill was called for these bins).
     * Equivalent to <tt>projectionXY().binEntriesX(indexX)</tt>.
     * @param indexX the x bin number in the external representation: (0...N-1) or OVERFLOW or UNDERFLOW.
     * @return The number of entries for the corresponding bins.
     */
    public int binEntriesX(int indexX) {
        int n = 0;
        for (int j=IAxis.UNDERFLOW_BIN; j<yAxis().bins(); j++)
            for (int k=IAxis.UNDERFLOW_BIN; k<zAxis().bins(); k++)
                n += binEntries(indexX,j,k);
        return n;
    }
    
    /**
     * Number of entries with a given x bin number (ie the number of times fill was called for these bins).
     * Equivalent to <tt>projectionXY().binEntriesY(indexY)</tt>.
     * @param indexY the y bin number in the external representation: (0...N-1) or OVERFLOW or UNDERFLOW.
     * @return The number of entries for the corresponding bins.
     */
    public int binEntriesY(int indexY) {
        int n = 0;
        for (int i=IAxis.UNDERFLOW_BIN; i<xAxis().bins(); i++)
            for (int k=IAxis.UNDERFLOW_BIN; k<zAxis().bins(); k++)
                n += binEntries(i,indexY,k);
        return n;
    }
    
    /**
     * Number of entries with a given x bin number (ie the number of times fill was called for these bins).
     * Equivalent to <tt>projectionXZ().binEntriesZ(indexZ)</tt>.
     * @param indexZ the z bin number in the external representation: (0...N-1) or OVERFLOW or UNDERFLOW.
     * @return The number of entries for the corresponding bins.
     */
    public int binEntriesZ(int indexZ) {
        int n = 0;
        for (int i=IAxis.UNDERFLOW_BIN; i<xAxis().bins(); i++)
            for (int j=IAxis.UNDERFLOW_BIN; j<yAxis().bins(); j++)
                n += binEntries(i,j,indexZ);
        return n;
    }
    
    /**
     * Total height of the corresponding bin.
     * @param indexX The x bin number in the external representation: (0...N-1) or OVERFLOW or UNDERFLOW.
     * @param indexY The y bin number in the external representation: (0...N-1) or OVERFLOW or UNDERFLOW.
     * @param indexZ The z bin number in the external representation: (0...N-1) or OVERFLOW or UNDERFLOW.
     * @return The bin height for the corresponding bin.
     *
     */
    public double binHeight(int indexX, int indexY, int indexZ) {
        return binner3D.height(mapBinNumber(indexX, xAxis()),mapBinNumber(indexY, yAxis()),mapBinNumber(indexZ, zAxis()));
    }
    
    /**
     * Total height of the corresponding x bin along y and z.
     * Equivalent to <tt>projectionXY().binHeightX(indexX)</tt>.
     * @param indexX The x bin number in the external representation: (0...N-1) or OVERFLOW or UNDERFLOW.
     * @return The bin height for the corresponding bin.
     *
     */
    public double binHeightX(int indexX) {
        double d = 0;
        for (int j=IAxis.UNDERFLOW_BIN; j<yAxis().bins(); j++)
            for (int k=IAxis.UNDERFLOW_BIN; k<zAxis().bins(); k++)
                d += binHeight(indexX,j,k);
        return d;
    }
    
    /**
     * Total height of the corresponding y bin along x and z.
     * Equivalent to <tt>projectionXY().binHeightY(indexY)</tt>.
     * @param indexY The y bin number in the external representation: (0...N-1) or OVERFLOW or UNDERFLOW.
     * @return The bin height for the corresponding bin.
     *
     */
    public double binHeightY(int indexY) {
        double d = 0;
        for (int i=IAxis.UNDERFLOW_BIN; i<xAxis().bins(); i++)
            for (int k=IAxis.UNDERFLOW_BIN; k<zAxis().bins(); k++)
                d += binHeight(i,indexY,k);
        return d;
    }
    
    /**
     * Total height of the corresponding z bin along x and y.
     * Equivalent to <tt>projectionXZ().binHeightZ(indexZ)</tt>.
     * @param indexZ The y bin number in the external representation: (0...N-1) or OVERFLOW or UNDERFLOW.
     * @return The bin height for the corresponding bin.
     *
     */
    public double binHeightZ(int indexZ) {
        double d = 0;
        for (int i=IAxis.UNDERFLOW_BIN; i<xAxis().bins(); i++)
            for (int j=IAxis.UNDERFLOW_BIN; j<yAxis().bins(); j++)
                d += binHeight(i,j,indexZ);
        return d;
    }
    
    /**
     * The error on this bin.
     * @param indexX The x bin number in the external representation: (0...N-1) or OVERFLOW or UNDERFLOW.
     * @param indexY The y bin number in the external representation: (0...N-1) or OVERFLOW or UNDERFLOW.
     * @param indexZ The z bin number in the external representation: (0...N-1) or OVERFLOW or UNDERFLOW.
     * @return The error on the corresponding bin.
     *
     */
    public double binError(int indexX, int indexY, int indexZ) {
        return binner3D.plusError(mapBinNumber(indexX, xAxis()),mapBinNumber(indexY, yAxis()),mapBinNumber(indexZ, zAxis()));
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
     * Get the mean of the whole Histogram as projected on the z axis. It includes all the entries (in and out of range).
     * @return The mean of the Histogram on the z axis.
     *
     */
    public double meanZ() {
        if( validEntries != 0) return meanZ/sumOfWeights;
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
     * Get the RMS of the whole Histogram as projected on the z axis. It includes all the entries (in and out of range).
     * @return The RMS of the Histogram on the z axis.
     *
     */
    public double rmsZ(){
        if ( validEntries != 0 ) return Math.sqrt(rmsZ/sumOfWeights - meanZ*meanZ/sumOfWeights/sumOfWeights);
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
     * Get the Z axis.
     * @return The z axis.
     *
     */
    public IAxis zAxis() {
        return zAxis;
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
     * Convenience method, equivalent to <tt>zAxis().coordToIndex(coord)</tt>.
     * @see IAxis#coordToIndex(double)
     * @return The bin's index along the z axis corresponding to the position coordY.
     *
     */
    public int coordToIndexZ(double coordZ) {
        return zAxis().coordToIndex(coordZ);
    }
    
    /**
     * Scale the weights and the errors by a given factor.
     *
     */
    public void scale(double scaleFactor) throws IllegalArgumentException {
        if ( scaleFactor <= 0 ) throw new IllegalArgumentException("Illegal scale factor "+scaleFactor+" it has to be positive");
        binner3D.scale(scaleFactor);
        meanX *= scaleFactor;
        rmsX  *= scaleFactor;
        meanY *= scaleFactor;
        rmsY  *= scaleFactor;
        meanZ *= scaleFactor;
        rmsZ  *= scaleFactor;
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
    public void add(IHistogram3D hist) throws IllegalArgumentException {
        HistMath.checkCompatibility(xAxis(), hist.xAxis());
        HistMath.checkCompatibility(yAxis(), hist.yAxis());
        HistMath.checkCompatibility(zAxis(), hist.zAxis());
        int xbins = xAxis().bins()+2;
        int ybins = yAxis().bins()+2;
        int zbins = zAxis().bins()+2;
        double[][][] newHeights=new double[xbins][ybins][zbins];
        double[][][] newErrors=new double[xbins][ybins][zbins];
        double[][][] newMeanXs  = new double[xbins][ybins][zbins];
        double[][][] newRmsXs   = new double[xbins][ybins][zbins];
        double[][][] newMeanYs  = new double[xbins][ybins][zbins];
        double[][][] newRmsYs   = new double[xbins][ybins][zbins];
        double[][][] newMeanZs  = new double[xbins][ybins][zbins];
        double[][][] newRmsZs   = new double[xbins][ybins][zbins];
        double rmsx2 = 0;
        double rmsy2 = 0;
        double rmsz2 = 0;
        boolean h1Aida = !(hist instanceof Histogram3D);
        int[][][] newEntries = new int[xbins][ybins][zbins];
        for(int i=IAxis.UNDERFLOW_BIN; i<xAxis().bins();i++)
            for(int j=IAxis.UNDERFLOW_BIN; j<yAxis().bins();j++)
                for(int k=IAxis.UNDERFLOW_BIN; k<zAxis().bins();k++) {
            
            double height1 = binHeight(i,j,k);
            double height2 = hist.binHeight(i,j,k);
            double h       = height1+height2;
            double meanx1  = binMeanX(i,j,k);
            double meanx2  = hist.binMeanX(i,j,k);
            meanx1 = HistUtils.isValidDouble(meanx1) ? meanx1 : 0;
            meanx2 = HistUtils.isValidDouble(meanx2) ? meanx2 : 0;
            double mx      = 0;
            double rmsx1   = binRmsX(i,j,k);
            double rx      = 0;
            double meany1  = binMeanY(i,j,k);
            double meany2  = hist.binMeanY(i,j,k);
            meany1 = HistUtils.isValidDouble(meany1) ? meany1 : 0;
            meany2 = HistUtils.isValidDouble(meany2) ? meany2 : 0;
            double my      = 0;
            double rmsy1   = binRmsY(i,j,k);
            double ry      = 0;
            double meanz1  = binMeanZ(i,j,k);
            double meanz2  = hist.binMeanZ(i,j,k);
            meanz1 = HistUtils.isValidDouble(meanz1) ? meanz1 : 0;
            meanz2 = HistUtils.isValidDouble(meanz2) ? meanz2 : 0;
            double mz      = 0;
            double rmsz1   = binRmsZ(i,j,k);
            if (h1Aida) {
                rmsx2 = (hist.xAxis().binUpperEdge(i)-hist.xAxis().binLowerEdge(i))/Math.sqrt(12);
                rmsy2 = (hist.yAxis().binUpperEdge(j)-hist.yAxis().binLowerEdge(j))/Math.sqrt(12);
                rmsy2 = (hist.zAxis().binUpperEdge(k)-hist.zAxis().binLowerEdge(k))/Math.sqrt(12);
            } else {
                rmsx2 = ((Histogram3D) hist).binRmsX(i, j, k);
                rmsy2 = ((Histogram3D) hist).binRmsY(i, j, k);
                rmsz2 = ((Histogram3D) hist).binRmsZ(i, j, k);
            }
            double rz      = 0;
            if ( h != 0 ) {
                mx = ( meanx1*height1 + meanx2*height2 )/(height1+height2);
                rx = Math.sqrt(((rmsx1*rmsx1*height1 + meanx1*meanx1*height1)+(rmsx2*rmsx2*height2 + meanx2*meanx2*height2))/h - mx*mx);
                my = ( meany1*height1 + meany2*height2 )/(height1+height2);
                ry = Math.sqrt(((rmsy1*rmsy1*height1 + meany1*meany1*height1)+(rmsy2*rmsy2*height2 + meany2*meany2*height2))/h - my*my);
                mz = ( meanz1*height1 + meanz2*height2 )/(height1+height2);
                rz = Math.sqrt(((rmsz1*rmsz1*height1 + meanz1*meanz1*height1)+(rmsz2*rmsz2*height2 + meanz2*meanz2*height2))/h - mz*mz);
            }
            int binx = mapBinNumber(i,xAxis());
            int biny = mapBinNumber(j,yAxis());
            int binz = mapBinNumber(k,zAxis());
            newHeights[binx][biny][binz] = h;
            newErrors [binx][biny][binz] = Math.sqrt( Math.pow(binError(i,j,k),2) + Math.pow(hist.binError(i,j,k),2) );
            newEntries[binx][biny][binz] = binEntries(i,j,k)+hist.binEntries(i,j,k);
            newMeanXs [binx][biny][binz] = mx;
            newRmsXs  [binx][biny][binz] = rx;
            newMeanYs [binx][biny][binz] = my;
            newRmsYs  [binx][biny][binz] = ry;
            newMeanZs [binx][biny][binz] = mz;
            newRmsZs  [binx][biny][binz] = rz;
            
                }
        setContents(newHeights,newErrors,newEntries,newMeanXs,newRmsXs,newMeanYs,newRmsYs,newMeanZs,newRmsZs);
        if (isValid) fireStateChanged();
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
    
    public void setMeanZ(double meanZ) {
        this.meanZ = meanZ*sumOfWeights;
    }
    
    public void setRmsZ(double rmsZ) {
        this.rmsZ = rmsZ*rmsZ*sumOfWeights + meanZ()*meanZ()*sumOfWeights;
    }
    
    /**
     * Get the mean of a bin along the x axis.
     * @param indexX The x bin number in the external representation: (0...N-1) or OVERFLOW or UNDERFLOW.
     * @param indexY The y bin number in the external representation: (0...N-1) or OVERFLOW or UNDERFLOW.
     * @param indexZ The z bin number in the external representation: (0...N-1) or OVERFLOW or UNDERFLOW.
     * @return The mean of the corresponding bin along x. If the bin has zero height, zero is returned.
     *
     */
    public double binMeanX(int indexX, int indexY, int indexZ) {
        int binx = mapBinNumber(indexX, xAxis());
        int biny = mapBinNumber(indexY, yAxis());
        int binz = mapBinNumber(indexZ, zAxis());
        double m = binner3D.meanX(binx, biny, binz);
        return Double.isNaN(m) ? xAxis().binCenter(indexX) : m;
    }
    
    /**
     * Get the mean of a bin along the y axis.
     * @param indexX The x bin number in the external representation: (0...N-1) or OVERFLOW or UNDERFLOW.
     * @param indexY The y bin number in the external representation: (0...N-1) or OVERFLOW or UNDERFLOW.
     * @param indexZ The z bin number in the external representation: (0...N-1) or OVERFLOW or UNDERFLOW.
     * @return The mean of the corresponding bin along y. If the bin has zero height, zero is returned.
     *
     */
    public double binMeanY(int indexX, int indexY, int indexZ) {
        int binx = mapBinNumber(indexX, xAxis());
        int biny = mapBinNumber(indexY, yAxis());
        int binz = mapBinNumber(indexZ, zAxis());
        double m = binner3D.meanY(binx, biny, binz);
        return Double.isNaN(m) ? yAxis().binCenter(indexY) : m;
    }
    
    /**
     * Get the mean of a bin along the z axis.
     * @param indexX The x bin number in the external representation: (0...N-1) or OVERFLOW or UNDERFLOW.
     * @param indexY The y bin number in the external representation: (0...N-1) or OVERFLOW or UNDERFLOW.
     * @param indexZ The z bin number in the external representation: (0...N-1) or OVERFLOW or UNDERFLOW.
     * @return The mean of the corresponding bin along z. If the bin has zero height, zero is returned.
     *
     */
    public double binMeanZ(int indexX, int indexY, int indexZ) {
        int binx = mapBinNumber(indexX, xAxis());
        int biny = mapBinNumber(indexY, yAxis());
        int binz = mapBinNumber(indexZ, zAxis());
        double m = binner3D.meanZ(binx, biny, binz);
        return Double.isNaN(m) ? zAxis().binCenter(indexZ) : m;
    }
    
    
    /**
     * Get the RMS of a bin along the x axis.
     * @param indexX The x bin number in the external representation:(0...N-1) or OVERFLOW or UNDERFLOW.
     * @param indexY The y bin number in the external representation:(0...N-1) or OVERFLOW or UNDERFLOW.
     * @param indexZ The z bin number in the external representation:(0...N-1) or OVERFLOW or UNDERFLOW.
     * @return The RMS of the corresponding bin along x. If the bin has zero height, zero is returned.
     *
     */
    public double binRmsX(int indexX, int indexY, int indexZ) {
        int binx = mapBinNumber(indexX, xAxis());
        int biny = mapBinNumber(indexY, yAxis());
        int binz = mapBinNumber(indexZ, zAxis());
        double r = binner3D.rmsX(binx, biny, binz);
        return Double.isNaN(r) ? xAxis().binWidth(indexX) : r;
    }
    
    /**
     * Get the RMS of a bin along the y axis.
     * @param indexX The x bin number in the external representation:(0...N-1) or OVERFLOW or UNDERFLOW.
     * @param indexY The y bin number in the external representation:(0...N-1) or OVERFLOW or UNDERFLOW.
     * @param indexZ The z bin number in the external representation:(0...N-1) or OVERFLOW or UNDERFLOW.
     * @return The RMS of the corresponding bin along y. If the bin has zero height, zero is returned.
     *
     */
    public double binRmsY(int indexX, int indexY, int indexZ) {
        int binx = mapBinNumber(indexX, xAxis());
        int biny = mapBinNumber(indexY, yAxis());
        int binz = mapBinNumber(indexZ, zAxis());
        double r = binner3D.rmsY(binx, biny, binz);
        return Double.isNaN(r) ? yAxis().binWidth(indexY) : r;
    }
    
    /**
     * Get the RMS of a bin along the z axis.
     * @param indexX The x bin number in the external representation:(0...N-1) or OVERFLOW or UNDERFLOW.
     * @param indexY The y bin number in the external representation:(0...N-1) or OVERFLOW or UNDERFLOW.
     * @param indexZ The z bin number in the external representation:(0...N-1) or OVERFLOW or UNDERFLOW.
     * @return The RMS of the corresponding bin along z. If the bin has zero height, zero is returned.
     *
     */
    public double binRmsZ(int indexX, int indexY, int indexZ) {
        int binx = mapBinNumber(indexX, xAxis());
        int biny = mapBinNumber(indexY, yAxis());
        int binz = mapBinNumber(indexZ, zAxis());
        double r = binner3D.rmsZ(binx, biny, binz);
        return Double.isNaN(r) ? zAxis().binWidth(indexZ) : r;
    }
    
    /**
     * Set the error on this bin.
     * @param indexX the bin number (0...N-1) or OVERFLOW or UNDERFLOW.
     * @param indexY the bin number (0...N-1) or OVERFLOW or UNDERFLOW.
     * @param indexZ the bin number (0...N-1) or OVERFLOW or UNDERFLOW.
     */
    public void setBinError(int indexX, int indexY, int indexZ, double error) {
        int binx = mapBinNumber(indexX, xAxis());
        int biny = mapBinNumber(indexY, yAxis());
        int binz = mapBinNumber(indexZ, zAxis());
        binner3D.setBinContent(binx,biny,binz,binEntries(indexX,indexY,indexZ),binHeight(indexX,indexY,indexZ),error, error,binMeanX(indexX,indexY,indexZ),binRmsX(indexX,indexY,indexZ),binMeanY(indexX,indexY,indexZ),binRmsY(indexX,indexY,indexZ),binMeanZ(indexX,indexY,indexZ),binRmsZ(indexX,indexY,indexZ));
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
     * @param meanZs The means of the bin along the z axis
     * @param rmsZs The rmss of the bin along the z axis
     *
     */
    public void setContents(double[][][] heights, double[][][] errors, int[][][] entries, double[][][] meanXs, double[][][] rmsXs, double[][][] meanYs, double[][][] rmsYs, double[][][] meanZs, double[][][]rmsZs) {
        
        reset();
        
        for (int i=0; i<xAxis.bins()+2; i++) {
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
                for (int k=0; k<zAxis.bins()+2; k++) {
                    double h = heights[i][j][k];
                    
                    int mk;
                    if ( k == 0 )
                        mk = IAxis.UNDERFLOW_BIN;
                    else if ( k == zAxis().bins()+1 )
                        mk = IAxis.OVERFLOW_BIN;
                    else
                        mk = k - 1;
                    
                    double mx;
                    if ( meanXs != null )
                        mx = meanXs[i][j][k];
                    else
                        mx = (xAxis().binLowerEdge(mi)+xAxis().binUpperEdge(mi))/2.;
                    
                    double my;
                    if ( meanYs != null )
                        my = meanYs[i][j][k];
                    else
                        my = (yAxis().binLowerEdge(mj)+yAxis().binUpperEdge(mj))/2.;
                    
                    double mz;
                    if ( meanZs != null )
                        mz = meanZs[i][j][k];
                    else
                        mz = (zAxis().binLowerEdge(mk)+zAxis().binUpperEdge(mk))/2.;
                    
                    double rx;
                    if ( rmsXs != null )
                        rx = rmsXs[i][j][k];
                    else
                        rx = (xAxis().binUpperEdge(mi)-xAxis().binLowerEdge(mi))/Math.sqrt(12);
                    
                    double ry;
                    if ( rmsYs != null )
                        ry = rmsYs[i][j][k];
                    else
                        ry = (yAxis().binUpperEdge(mj)-yAxis().binLowerEdge(mj))/Math.sqrt(12);
                    
                    double rz;
                    if ( rmsZs != null )
                        rz = rmsZs[i][j][k];
                    else
                        rz = (zAxis().binUpperEdge(mk)-zAxis().binLowerEdge(mk))/Math.sqrt(12);
                    
                    int e;
                    if ( entries != null )
                        e = entries[i][j][k];
                    else
                        e = (int)h;
                    
                    binner3D.setBinContent(i,j,k,e,h,errors[i][j][k],errors[i][j][k],mx,rx,my,ry,mz,rz);
                    
                    h = binner3D.height(i,j,k);
                    
                    allEntries+= e;
                    if ( ( mi >= 0 && mj >= 0 && mk >=0 ) || useOutflows() ) {
                        if ( ! Double.isNaN(mx) && ! Double.isNaN(my) && ! Double.isNaN(mz) && ! Double.isInfinite(mx) && ! Double.isInfinite(my) && ! Double.isInfinite(mz) ) {
                            meanX += mx*h;
                            rmsX  += rx*rx*h+mx*mx*h;
                            meanY += my*h;
                            rmsY  += ry*ry*h+my*my*h;
                            meanZ += mz*h;
                            rmsZ  += rz*rz*h+mz*mz*h;
                        }
                        validEntries += e;
                        sumOfWeights += h;
                        sumOfWeightsSquared = h*h;
                    }
                }
            }
        }
    }
    
    public void initHistogram3D( IAxis xAxis, IAxis yAxis, IAxis zAxis, String options ) {
        this.xAxis = xAxis;
        this.yAxis = yAxis;
        this.zAxis = zAxis;
        Map optionMap = hep.aida.ref.AidaUtils.parseOptions( options );
        String type = (String) optionMap.get("type");
        if ( type == null || type.equals("default"))
            binner3D = new BasicBinner3D( xAxis.bins()+2, yAxis.bins()+2, zAxis.bins()+2);
        else if ( type.equals("efficiency") )
            binner3D = new EfficiencyBinner3D( xAxis.bins()+2, yAxis.bins()+2, zAxis.bins()+2);
        else
            throw new IllegalArgumentException("Wrong histogram type "+type);
        
        String useOutflowsString = (String) optionMap.get("useOutflowsInStatistics");
        if ( useOutflowsString != null )
            setUseOutflows( Boolean.valueOf(useOutflowsString).booleanValue() );
        reset();
    }
    
    private double meanX = 0, rmsX = 0;
    private double meanY = 0, rmsY = 0;
    private double meanZ = 0, rmsZ = 0;
    private IAxis xAxis, yAxis, zAxis;
    private Binner3D binner3D;
    
}
