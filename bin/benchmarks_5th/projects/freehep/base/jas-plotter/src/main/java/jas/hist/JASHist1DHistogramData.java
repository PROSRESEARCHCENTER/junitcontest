package jas.hist;

import jas.plot.Overlay;
import jas.util.ColorConverter;

import java.io.Serializable;
import java.util.Observable;

class JASHist1DHistogramData extends JASHistData {
    JASHist1DHistogramData(DataManager dm,DataSource ds) {
        super(dm);
        dataSource = ds;
        initTransientData();
        
        JASHistStyle s = null;
        if (ds instanceof HasStyle) s = ((HasStyle) ds).getStyle();
        if (s == null) s = new JASHist1DHistogramStyle();
        setStyle(s);
        String property = System.getProperty("hurry", "false");
        hurry = property != null && property.equalsIgnoreCase("true");
    }
    private void initTransientData() {
        yLimitsValid = false;
        isBinned = false;
    }
    public void setStyle(JASHistStyle style) {
        if (!(style instanceof JASHist1DHistogramStyle))
            throw new IllegalArgumentException("Style is not subclass of JASHist1DHistogramStyle");
        if (this.style != null) this.style.deleteObserver(this);
        this.style = (JASHist1DHistogramStyle) style;
        this.style.addObserver(this);
    }
    public String getTitle() {
        return dataSource.getTitle();
    }
    String[] getAxisLabels() {
        return dataSource instanceof Rebinnable1DHistogramData ? ((Rebinnable1DHistogramData) dataSource).getAxisLabels() : null;
    }
    Overlay createOverlay() {
        return new OneDOverlay(this);
    }
    void writeAsXML(XMLPrintWriter pw, boolean snapshot) {
        pw.setAttribute("axis","y"+getYAxis());
        pw.openTag("data1d");
        String theAxisType = pw.convertAxisTypeToString(getAxisType());
        
        if (snapshot) {
            if (dataSource instanceof Rebinnable1DHistogramData) {
                pw.setAttribute("title",getTitle());
                pw.openTag("bins1d");
                
                for (int i=0; i < data.length; i++) {
                    pw.print(data[i]);
                    pw.print(",");
                    pw.print(plusError[i]);
                    pw.print(",");
                    pw.print(minusError[i]);
                    pw.println();
                }
                pw.closeTag();
                
                pw.printBinnedDataAxisAttributes(
                "x", "" + xLow, "" + xHigh,
                "" + xBins, theAxisType);
                
                if (theAxisType.equals("string")) {
                    pw.setAttribute("type","x0");
                    pw.openTag("axisLabels");
                    String[] labels = getAxisLabels();
                    for (int i=0; i < labels.length; i++) {
                        pw.setAttribute("value",labels[i]);
                        pw.printTag("axisLabel");
                    }
                    pw.closeTag();
                }
            }
            else {
                pw.setAttribute("title",getTitle());
                pw.openTag("points");
                
                for (int i=0; i < data.length; i++) {
                    pw.print(dataX[i]);
                    pw.print(',');
                    pw.print(data[i]);
                    pw.print(',');
                    pw.print(plusError[i]);
                    pw.print(',');
                    pw.print(minusError[i]);
                    pw.println();
                }
                pw.closeTag();
                pw.setAttribute("axis","x");
                pw.setAttribute("type",theAxisType);
                pw.printTag("pointDataAxisAttributes");
            }
            
            if (dataSource instanceof HasStatistics) {
                Statistics stats = ((HasStatistics) dataSource).getStatistics();
                if (stats != null) {
                    pw.openTag("statistics");
                    String[] names = stats.getStatisticNames();
                    for (int i=0; i<names.length; i++) {
                        String name = names[i];
                        pw.setAttribute("name",name);
                        String valueString = null;
                        if (stats instanceof ExtendedStatistics)
                        {
                           Object value = ((ExtendedStatistics) stats).getExtendedStatistic(name);
                           if (value != null) valueString = value.toString();
                        }
                        if (valueString == null) valueString = String.valueOf(stats.getStatistic(name));
                        pw.setAttribute("value",valueString);
                        pw.printTag("statistic");
                    }
                    pw.closeTag();
                }
            }
        }
        else // !snapshot
        {
            if (dataSource instanceof jas.util.xml.HasXMLRepresentation) {
                ((jas.util.xml.HasXMLRepresentation) dataSource).writeAsXML(pw);
            }
            else {
                if (dataSource instanceof HasDataSource) pw.setAttribute("name",dataSource.getClass().getName());
                else pw.setAttribute("name","???");
                pw.setAttribute("param","???");
                pw.printTag("class");
            }
        }
        pw.setAttribute("histogramBarsFilled",style.getHistogramFill());
        pw.setAttribute("histogramBarColor",
        ColorConverter.colorToString(style.getHistogramBarColor()));
        pw.setAttribute("errorBarColor",
        ColorConverter.colorToString(style.getErrorBarColor()));
        pw.setAttribute("dataPointColor",
        ColorConverter.colorToString(style.getDataPointColor()));
        pw.setAttribute("dataPointStyle",pw.convertStyleToString(style.getDataPointStyle()));
        pw.setAttribute("dataPointSize",style.getDataPointSize());
        pw.setAttribute("lineColor",ColorConverter.colorToString(style.getLineColor()));
        pw.setAttribute("showHistogramBars",style.getShowHistogramBars());
        pw.setAttribute("showErrorBars",style.getShowErrorBars());
        pw.setAttribute("showDataPoints",style.getShowDataPoints());
        pw.setAttribute("showLinesBetweenPoints",style.getShowLinesBetweenPoints());
        pw.printTag("style1d");
        
        pw.closeTag();
    }
    boolean isRebinnable() {
        return dataSource instanceof Rebinnable1DHistogramData ? ((Rebinnable1DHistogramData) dataSource).isRebinnable() : false;
    }
    double getXMin() {
        if (dataSource instanceof Rebinnable1DHistogramData) return ((Rebinnable1DHistogramData) dataSource).getMin();
        if (!xLimitsValid) calcXLimits();
        return xMin;
    }
    double getXMax() {
        if (dataSource instanceof Rebinnable1DHistogramData) return ((Rebinnable1DHistogramData) dataSource).getMax();
        if (!xLimitsValid) calcXLimits();
        return xMax;
    }
    void setXRange(int xBins,double xLow, double xHigh) {
        if (isRebinnable()) {
            if (xBins != this.xBins || xLow != this.xLow || xHigh != this.xHigh) {
                this.xBins = xBins;
                isBinned = false;
            }
        }
        else if (dataSource instanceof Rebinnable1DHistogramData) {
            this.xBins = ((Rebinnable1DHistogramData) dataSource).getBins();
        }
        yLimitsValid = false;
        this.xLow = xLow;
        this.xHigh = xHigh;
    }
    private void doXYBin() {
        isBinned = true;
        XYDataSource xy = (XYDataSource) dataSource;
        int n = xy.getNPoints();
        dataX = new double[n];
        data = new double[n];
        plusError = new double[n];
        minusError = new double[n];
        
        for (int i=0; i<n; i++) {
            dataX[i] = xy.getX(i);
            data[i] = xy.getY(i);
            plusError[i] = xy.getPlusError(i);
            minusError[i] = xy.getMinusError(i);
        }
        // apply normalization
        if (normalization != null) {
            double factor = 1./normalization.getNormalizationFactor();
            double[] normalizedData = new double[n];
            double[] normalizedPlusError = new double[n];
            double[] normalizedMinusError = new double[n];
            
            for (int i=0; i<n; i++) {
                normalizedData[i] = data[i] * factor;
                normalizedPlusError[i] = plusError[i] * factor;
                normalizedMinusError[i] = minusError[i] * factor;
            }
            if (overlay instanceof OneDOverlay) {
                ((OneDOverlay) overlay).setData(dataX,normalizedData,normalizedPlusError,normalizedMinusError);
            }
        }
        else if (overlay instanceof OneDOverlay) {
            ((OneDOverlay) overlay).setData(dataX,data,plusError,minusError);
        }
    }
    private void doBin() {
        if ( dataSource instanceof XYDataSource )
            doXYBin();
        else {
            int type = getAxisType();
            String[] labels = null;
            double xl,xh;
            
            if (type == dataSource.STRING) {
                labels = getAxisLabels();
                xBins = labels.length;
                xl = 0;
                xh = labels.length;
            }
            else if (isRebinnable()) {
                xl = xLow;
                xh = xHigh;
            }
            else {
                xl = ((Rebinnable1DHistogramData) dataSource).getMin();
                xh = ((Rebinnable1DHistogramData) dataSource).getMax();
            }
            
            isBinned = true; // Set before call to rebin to avoid race condition
            double[][] result =  ((Rebinnable1DHistogramData) dataSource).rebin(xBins,xl,xh,true,hurry);
            if (result == null) result = new double[1][xBins];
            
            data = result[0];
            
            if (data.length != xBins)
                System.err.println("Warning xbins="+xBins+" data.length="+data.length);
            
            if (result.length > 1) {
                plusError = result[1];
                if (result.length > 2) minusError = result[2];
                else                   minusError = plusError;
            }
            else {
                plusError = new double[xBins];
                for (int i=0; i<xBins; i++) {
                    plusError[i] = Math.sqrt(Math.abs(data[i]));
                }
                minusError = plusError;
            }
            if (plusError.length != xBins)
                System.err.println("Warning xbins="+xBins+" plusError.length="+plusError.length);
            if (minusError.length != xBins)
                System.err.println("Warning xbins="+xBins+" minusError.length="+minusError.length);
            
            // apply normalization
            if (normalization != null) {
                double factor = 1./normalization.getNormalizationFactor();
                double[] normalizedData = new double[xBins];
                double[] normalizedPlusError = new double[xBins];
                double[] normalizedMinusError = new double[xBins];
                
                for (int i=0; i<data.length; i++) {
                    normalizedData[i] = data[i] * factor;
                    normalizedPlusError[i] = plusError[i] * factor;
                    normalizedMinusError[i] = minusError[i] * factor;
                }
                if (overlay instanceof OneDOverlay) {
                    if (type ==  dataSource.STRING) ((OneDOverlay) overlay).setData(normalizedData,normalizedPlusError,normalizedMinusError,labels);
                    else                            ((OneDOverlay) overlay).setData(normalizedData,normalizedPlusError,normalizedMinusError,xl,xh);
                }
            }
            else {
                if (overlay instanceof OneDOverlay) {
                    if (type ==  dataSource.STRING) ((OneDOverlay) overlay).setData(data,plusError,minusError,labels);
                    else                            ((OneDOverlay) overlay).setData(data,plusError,minusError,xl,xh);
                }
            }
        }
    }
    // Note: This set xMin and xMax to NaN if the number of points == 0. See JAS=334.
    private void calcXLimits() {
        xLimitsValid = true;
        XYDataSource xy = (XYDataSource) dataSource;
        int n = xy.getNPoints();
        if (n == 0)
        {
           xMin = Double.NaN;
           xMax = Double.NaN;
        }
        else
        {
           xMin = Double.MAX_VALUE;
           xMax = -Double.MAX_VALUE;
           for (int i=0; i<n; i++) {
               double x = xy.getX(i);
               if (x<xMin) xMin = x;
               if (x>xMax) xMax = x;
           }
           if (xMin > xMax) {
               xMin = 0;
               xMax = 1;
           }
           else {
               // Allow a little extra space for the size of the points
               double delta = (xMax-xMin)/25;
               xMin -= delta;
               xMax += delta;
           }
        }
    }
    private void calcYLimits() {
        if (!isBinned) {
            if (dataSource instanceof XYDataSource) doXYBin();
            else doBin();
            fittableDataSource.binningChanged();
        }
        yLimitsValid = true;
        // Note: If the data member for a bin is NaN then we should
        // skip that point, and not count it in the Y limits.
        // Note2: In the case of non-rebinnable histograms, not all of the points may
        // be visible. Only include the visible points in the calculation.
        int nGoodPoints = 0;
        yLow = Double.MAX_VALUE;
        yHigh = -Double.MAX_VALUE; // not the same as MIN_VALUE
        
        // Also remember points just outside the [xLow, xHigh] region - 
        // if there are no good points in this region, will use those
        // to set the Y Axis limits
        yNearLow = Double.MAX_VALUE;
        yNearHigh = -Double.MAX_VALUE; 
        xNearLow = -Double.MAX_VALUE;
        xNearHigh = Double.MAX_VALUE; 
        int iNearLow = -1;
        int iNearHigh = -1;
        int ndp = -1;
        double dm = 0.;
        double dp = 0.;
        
        boolean eb = style.getShowErrorBars();
        boolean fixed = !isRebinnable() && getAxisType() != dataSource.STRING;
        if (dataSource instanceof Rebinnable1DHistogramData) {
            double x = getXMin();
            double bw = (getXMax() - getXMin())/xBins;
            ndp = xBins;
            
            for (int i=0; i<xBins; i++) {
               if (fixed) {
                    double xBinMin = x;
                    x += bw;
                    double xBinMax = x;
                    if (xBinMax < xLow) {
                        if (x>xNearLow) {
                            xNearLow = x;
                            iNearLow = i;
                        }
                        continue;
                    }
                    if (xBinMin > xHigh) {
                        if (x<xNearHigh) {
                            xNearHigh = x;
                            iNearHigh = i;
                        }
                        continue;
                    }
                }
                double d = data[i];
                if (Double.isNaN(d)) continue;
                nGoodPoints++;
                yLow = Math.min(yLow,d-(eb ? minusError[i] : 0));
                yHigh = Math.max(yHigh,d+(eb ? plusError[i] : 0));
            }
        } else {
            ndp = dataX.length;
            for (int i=0; i<dataX.length; i++) {
                double x = dataX[i];
                double d = data[i];
                if (Double.isNaN(d)) continue;
                if (x<xLow) {
                    if (x>xNearLow) {
                        xNearLow = x;
                        iNearLow = i;
                    }
                    continue;
                }
                if (x>xHigh) {
                    if (x<xNearHigh) {
                        xNearHigh = x;
                        iNearHigh = i;
                    }
                    continue;
                }
                
                nGoodPoints++;
                yLow = Math.min(yLow , d-(eb ? minusError[i] : 0));
                yHigh = Math.max(yHigh  , d+(eb ? plusError[i] : 0));
            }           
            // Allow a little extra space for the size of the points
            double delta = (yHigh-yLow)/25;
            if ( delta == 0 )
                delta = 1;
            yLow -= delta;
            yHigh += delta;
         }
        if (iNearHigh < 0 && iNearLow < 0 && nGoodPoints == 0) {
            yLow = 0;
            yHigh = 1;
        } else {
            if (nGoodPoints == 0) {
                if (iNearLow >= 0) {
                    double d = data[iNearLow];
                    dm = d-(eb ? minusError[iNearLow] : 0);
                    dp = d+(eb ? minusError[iNearLow] : 0);
                    if (dm < yNearLow)  yNearLow  = dm;
                    if (dp > yNearHigh) yNearHigh = dp;
                }
                if (iNearHigh >= 0) {
                    double d = data[iNearHigh];
                    dm = d-(eb ? minusError[iNearHigh] : 0);
                    dp = d+(eb ? minusError[iNearHigh] : 0);
                    if (dm < yNearLow)  yNearLow  = dm;
                    if (dp > yNearHigh) yNearHigh = dp;
                }
                yLow  = yNearLow;
                yHigh = yNearHigh;
            }
        }

        if (iNearHigh < 0 && iNearLow < 0 && nGoodPoints == 0) {
            yLow = 0;
            yHigh = 1;
        } else {
            if (nGoodPoints == 0) {
                if (iNearLow >= 0) {
                    double d = data[iNearLow];
                    dm = d-(eb ? minusError[iNearLow] : 0);
                    dp = d+(eb ? minusError[iNearLow] : 0);
                    if (dm < yNearLow)  yNearLow  = dm;
                    if (dp > yNearHigh) yNearHigh = dp;
                }
                if (iNearHigh >= 0) {
                    double d = data[iNearHigh];
                    dm = d-(eb ? minusError[iNearHigh] : 0);
                    dp = d+(eb ? minusError[iNearHigh] : 0);
                    if (dm < yNearLow)  yNearLow  = dm;
                    if (dp > yNearHigh) yNearHigh = dp;
                }
                yLow  = yNearLow;
                yHigh = yNearHigh;
            } else {              
                // apply normalization
                if (normalization != null) {
                    double factor = 1./normalization.getNormalizationFactor();
                    yLow *= factor;
                    yHigh *= factor;
                }
            }
        }  
        
        // Round yLow and yHigh for a more sane mumbers
        yLow  = JASHistUtil.roundDown(yLow, 2);
        yHigh = JASHistUtil.roundUp(yHigh, 2);        
    }

    public void update(Observable o, Object arg) {
        // Dragons: Likely to be called by different thread
        if (o == dataSource) {
            // Currently there are several types of update notifications
            // - DataUpdate
            // - RangeUpdate
            // - FinalUpdate
            // - TitleUpdate
            // - Reset (Ususlly for when partition changes)
            
            HistogramUpdate hu = (HistogramUpdate) arg;
            
            // If we are not binned, we must pass the update onto any fits.
            // If we are binned then hopefully our observer will ask us to rebin
            // ourselves, and the fit will be informed then.
            
            isBinned = false;
            yLimitsValid = false;
            xLimitsValid = false;
            
            //if (hu.isReset())
            //parent.resetNumberOfBins(this);
            
            //if (hu.isRangeUpdate() || hu.isReset())
            //parent.invalidateXAxis();
            
            //long delay = hu.isFinalUpdate() || hu.isReset() ? 0 : 1000;
            //parent.scheduleDataUpdate(delay);
            parent.update(hu, this);
        }
        else if (o == style) {
            yLimitsValid = false; //. in case error bar settings changed
            parent.styleUpdate(this);
        }
        else if (o == normalization) {
            normalizationChanged(false);
        }
    }
    void normalizationChanged(boolean now) {
        // treat the same as dataChanged
        // Could be more efficient by just renormalizing the cached copy of the data
        // It would be more efficient just to change the normalization of the fit,
        // there is no need to refit the plot just because the normalization changed
        HistogramUpdate hu = new HistogramUpdate(HistogramUpdate.DATA_UPDATE,now);
        if (!isBinned || countObservers() == 0) fittableDataSource.update(hu);
        isBinned = false;
        yLimitsValid = false;
        parent.update(hu,this);
    }
    public boolean hasChanged() {
        return !isBinned;
    }
    // Note YMin and YMax include allowances for error bars, if shown
    double getYMin() {
        if (!yLimitsValid) calcYLimits();
        return yLow;
    }
    double getYMax() {
        if (!yLimitsValid) calcYLimits();
        return yHigh;
    }
    void validate() {
        if (!isBinned) doBin();
    }
    void axisChanged() {
        parent.axisChanged(this);
    }
    public DataSource getDataSource() {
        return dataSource;
    }
    public DataSource getFittableDataSource() {
        return fittableDataSource;
    }
    public boolean getBinnable() {
        return true;
    }
    public int getBins() {
        return dataSource instanceof Rebinnable1DHistogramData ? ((Rebinnable1DHistogramData) dataSource).getBins() : ((XYDataSource) dataSource).getNPoints();
    }
    int getAxisType() {
        if (dataSource instanceof Rebinnable1DHistogramData) return ((Rebinnable1DHistogramData) dataSource).getAxisType();
        else return ((XYDataSource) dataSource).getAxisType();
    }
    public JASHistStyle getStyle() {
        return style;
    }
    void destroy() {
        if (dataSource instanceof Observable) ((Observable) dataSource).deleteObserver(this);
        style.deleteObserver(this);
        super.deleteNormalizationObserver();
    }
    private DataSource dataSource;
    JASHist1DHistogramStyle style; // directly accessed by Overlay
    
    private boolean hurry;
    private boolean isBinned = false;
    private boolean yLimitsValid = false;
    private boolean xLimitsValid = false;
    private double[] data;
    private double[] dataX;
    private double[] plusError;
    private double[] minusError;
    private int xBins;
    private double xMin;
    private double xMax;
    private double xLow;
    private double xHigh;
    private double yLow;
    private double yHigh;
    private double xNearLow;
    private double xNearHigh;
    private double yNearLow;
    private double yNearHigh;
    private static final HistogramUpdate hu = new HistogramUpdate(HistogramUpdate.DATA_UPDATE,true);
    
    static final long serialVersionUID = -3529869583896718619L;
    
    private FittableDataSource fittableDataSource = new jas.hist.JASHist1DHistogramData.FittableDataSource();
    
    /**
     * Fits are done to binned data, so the result of the fit
     * depends on the current binning. FittableDataSource is
     * a DataSource that reflects the current binning.
     */
    
    class FittableDataSource extends Observable implements XYDataSource, Serializable {
        
        public int getAxisType() {
            return DOUBLE;
        }
        public String getTitle() {
            return dataSource.getTitle();
        }
        void update(Object obj) {
            this.setChanged();
            this.notifyObservers(obj);
        }
        void binningChanged() {
            this.setChanged();
            this.notifyObservers(hu);
        }
        JASHist1DHistogramData parent() // Used in FitManager
        {
            return JASHist1DHistogramData.this;
        }
        public double getMinusError(int index) {
            return minusError[index];
        }
        public double getPlusError(int index) {
            return plusError[index];
        }
        public double getX(int index) {
            if (dataX != null) return dataX[index];
            else {
                double bw = (xHigh-xLow)/xBins;
                return xLow+bw*index+bw/2;
            }
        }
        public double getY(int index) {
            return data[index];
        }
        public int getNPoints() {
            if (data == null) return 0;
            return data.length;
        }
        public String toString() {
            return getTitle();
        }
    }
}
