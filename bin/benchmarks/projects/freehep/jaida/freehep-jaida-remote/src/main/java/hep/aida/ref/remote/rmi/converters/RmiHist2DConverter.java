/*
 * RmiHist1DConverter.java
 *
 * Created on October 14, 2003, 7:39 PM
 */

package hep.aida.ref.remote.rmi.converters;

import hep.aida.IAnnotation;
import hep.aida.IAxis;
import hep.aida.IHistogram2D;
import hep.aida.IManagedObject;
import hep.aida.ref.Annotation;
import hep.aida.ref.histogram.Histogram2D;
import hep.aida.ref.remote.RemoteHistogram2D;
import hep.aida.ref.remote.rmi.data.RmiAnnotationItem;
import hep.aida.ref.remote.rmi.data.RmiAxis;
import hep.aida.ref.remote.rmi.data.RmiHist2DData;

/**
 *
 * @author  serbo
 */
public class RmiHist2DConverter extends RmiConverter {
    
    private static RmiHist2DConverter converter = null;
    
    /** Creates a new instance of RmiHist1DAdapter */
    public static RmiHist2DConverter getInstance() {
        if (converter == null) converter = new RmiHist2DConverter();
        return converter;
    }
    
    /** Creates a new instance of RmiHist1DConverter */
    private RmiHist2DConverter() {
        super();
        dataType = "RmiHist2DData";
        aidaType = "IHistogram2D";
    }
    
    public Object createAidaObject(String name) {
        RemoteHistogram2D result = new RemoteHistogram2D(name);        
        return result;
    }
    
    public Object extractData(Object aidaObject) {
        RmiHist2DData data = null;
        if (aidaObject instanceof RemoteHistogram2D) {
            data = createRemoteData((RemoteHistogram2D) aidaObject);
        } else if (aidaObject instanceof hep.aida.IHistogram2D) {
            data = createData((hep.aida.IHistogram2D) aidaObject);
        } else if (aidaObject instanceof Object[] && ((Object[]) aidaObject)[0] instanceof IHistogram2D) {
            IHistogram2D[] arr = new IHistogram2D[((Object[]) aidaObject).length];
            for (int i=0; i<arr.length; i++) {
                arr[i] = (IHistogram2D) ((Object[]) aidaObject)[i];
            }
            data = createData(arr);
        } else
            throw new IllegalArgumentException("Not supported data type: "+aidaObject.getClass().getName());

        return data;
    }
    
    public boolean updateAidaObject(Object aidaObject, Object newData) {
        RmiHist2DData data = null;
        if (newData instanceof RmiHist2DData) {
            data = (RmiHist2DData) newData;
        }

        if (!(aidaObject instanceof hep.aida.ref.remote.RemoteHistogram2D))
            throw new IllegalArgumentException("Not supported object type: "+aidaObject.getClass().getName());
        if (!(data != null && data instanceof hep.aida.ref.remote.rmi.data.RmiHist2DData))
            throw new IllegalArgumentException("Not supported data type: "+(newData == null ? "null" : newData.getClass().getName()));

        updateData((RemoteHistogram2D) aidaObject, data);
        return true;
    }
    
    
    // Service methods
 
   /**
    * Update data in RemoteHistogram1d from RmiHist2DData
    * and calls setDataValid(true) method.
    */ 
   public IManagedObject updateData(RemoteHistogram2D hist, RmiHist2DData data)
   {
       // If data == null, just leave the old dats in. 
       // Maybe should clear the histogram instead?
       if (data == null) return hist;
       
      synchronized (hist) {
        hist.setFillable(true);
       
        // Check if X axis binning or edges are different
        IAxis lXAxis = hist.xAxis();      
        IAxis rXAxis = data.getXAxis(); 
        int nXBins = rXAxis.bins();
        if (lXAxis == null || lXAxis.bins() != nXBins || 
            lXAxis.lowerEdge() != rXAxis.lowerEdge() || lXAxis.upperEdge() != rXAxis.upperEdge()) {
            hist.setXAxis(nXBins, rXAxis.lowerEdge(), rXAxis.upperEdge()); 
        }
      
        // Check if Y axis binning or edges are different
        IAxis lYAxis = hist.yAxis();      
        IAxis rYAxis = data.getYAxis(); 
        int nYBins = rYAxis.bins();
        if (lYAxis == null || lYAxis.bins() != nYBins || 
            lYAxis.lowerEdge() != rYAxis.lowerEdge() || lYAxis.upperEdge() != rYAxis.upperEdge()) {
            hist.setYAxis(nYBins, rYAxis.lowerEdge(), rYAxis.upperEdge()); 
        }
      
        // Check and set Annotation
        RmiAnnotationItem[] items = data.getAnnotationItems();
        if (items != null && items.length > 0) {
             boolean sticky = false;
            IAnnotation localAnnotation = hist.annotation();
            if (localAnnotation instanceof Annotation)  
                ((Annotation) localAnnotation).setFillable(true);
            for (int i=0; i<items.length; i++) {
                String key = items[i].key;
                String newValue = items[i].value;
                String oldValue = null;
                try {
                   oldValue = localAnnotation.value(key);
                } catch (IllegalArgumentException e) {}
                if (oldValue == null) localAnnotation.addItem(key, newValue, sticky);
                else if (!newValue.equals(oldValue)) {
                    localAnnotation.setValue(key,  newValue);
                    localAnnotation.setSticky(key,  sticky);
                }
            }
            
            // Check for "stat.Updated" info
            java.util.Date date = new java.util.Date();
            java.text.DateFormat df = java.text.DateFormat.getTimeInstance();
            String dateString = df.format(date);
            try {
                String value = localAnnotation.value("stat.Updated");
                if (value == null || value.equals("0") || value.equals(""))
                    localAnnotation.setValue("stat.Updated", dateString);
            } catch (IllegalArgumentException e) {
                localAnnotation.addItem("stat.Updated", dateString);                
            }
            
            if (localAnnotation instanceof Annotation)  
                ((Annotation) localAnnotation).setFillable(false);          
        }
      
        // Set all other information
        
        // Setting arrays
        hist.setHeights(data.getBinHeights());
        hist.setErrors(data.getBinErrors());
        hist.setEntries(data.getBinEntries());
        
        hist.setBinMeansX(data.getBinMeansX());
        hist.setBinRmssX(data.getBinRmssX());
        hist.setBinMeansY(data.getBinMeansY());
        hist.setBinRmssY(data.getBinRmssY());
        
        hist.setBinEntriesX(data.getBinEntriesX());
        hist.setBinEntriesY(data.getBinEntriesY());
        hist.setBinHeightsX(data.getBinHeightsX());
        hist.setBinHeightsY(data.getBinHeightsY());
        
        // Setting single numbers
        hist.setMeanX(data.getMeanX());
        hist.setRmsX(data.getRmsX());
        hist.setMeanY(data.getMeanY());
        hist.setRmsY(data.getRmsY());
        
        hist.setEquivalentBinEntries(data.getEquivalentBinEntries());
        hist.setNanEntries(data.getNanEntries());

        hist.setInRangeEntries(data.getInRangeEntries());
        hist.setExtraEntries(data.getExtraEntries());
        hist.setMinBinEntries(data.getMinBinEntries());
        hist.setMaxBinEntries(data.getMaxBinEntries());

        hist.setInRangeBinHeights(data.getInRangeBinHeights());
        hist.setExtraBinHeights(data.getExtraBinHeights());
        hist.setMinBinHeights(data.getMinBinHeights());
        hist.setMaxBinHeights(data.getMaxBinHeights());
        
        hist.setFillable(false);
        hist.setDataValid(false);
        hist.setDataValid(true);
      }      
      return hist;
   }
   
   /**
    * Create RmiHist1DData structure from generic IHistogram1D
    */
    public RmiHist2DData createData(IHistogram2D hist) {
        RmiHist2DData data = new RmiHist2DData();

        RmiAxis rXAxis = null;
        RmiAxis rYAxis = null;
        RmiAnnotationItem[] rAnnotation = null;

        double meanX = Double.NaN;
        double rmsX = Double.NaN;
        double meanY = Double.NaN;
        double rmsY = Double.NaN;
        
        double equivalentBinEntries = 0.0;
        int nanEntries = 0;
        
        int inRangeEntries = 0;
        int extraEntries = 0;
        int minBinEntries = 0;
        int maxBinEntries = 0;
        
        double inRangeBinHeights = 0.0;
        double extraBinHeights = 0.0;
        double minBinHeights = 0.0;
        double maxBinHeights = 0.0;
        
        int[][] entries    = null;
        double[][] heights = null;
        double[][] errors  = null;
        double[][] rmss    = null;
        double[][] meansX  = null;
        double[][] rmssX   = null;
        double[][] meansY  = null;
        double[][] rmssY   = null;
        
        int[] binEntriesX = null;
        int[] binEntriesY = null;
        double[] binHeightsX = null;
        double[] binHeightsY = null;
        
        synchronized (hist) {
            
            // Get X axis information. So far support only FixedAxis binning            
            IAxis lXAxis = hist.xAxis();      
            int nXBins = lXAxis.bins();
            rXAxis = new RmiAxis(nXBins, lXAxis.lowerEdge(), lXAxis.upperEdge());
      
            // Get Y axis information. So far support only FixedAxis binning            
            IAxis lYAxis = hist.yAxis();      
            int nYBins = lYAxis.bins();
            rYAxis = new RmiAxis(nYBins, lYAxis.lowerEdge(), lYAxis.upperEdge());
      
            // Get Annotation information
            boolean sticky = false;
            IAnnotation lAnnotation = hist.annotation();
            if (lAnnotation != null && lAnnotation.size() > 0) {
                rAnnotation = new RmiAnnotationItem[lAnnotation.size()];
                for (int i=0; i<lAnnotation.size(); i++) {
                    String key = lAnnotation.key(i);
                    String value = lAnnotation.value(key);
                    rAnnotation[i] = new RmiAnnotationItem(key, value, sticky);
                }  
            }
            
            // Get bin information
            if (nXBins > 0 && nYBins>0) {
                entries    = new int[nXBins+2][nYBins+2];
                heights = new double[nXBins+2][nYBins+2];
                errors  = new double[nXBins+2][nYBins+2];
                meansX  = new double[nXBins+2][nYBins+2];
                rmssX   = new double[nXBins+2][nYBins+2];
                meansY  = new double[nXBins+2][nYBins+2];
                rmssY   = new double[nXBins+2][nYBins+2];

                binEntriesX = new int[nXBins+2];
                binEntriesY = new int[nYBins+2];
                binHeightsX = new double[nXBins+2];
                binHeightsY = new double[nYBins+2];

                minBinEntries = Integer.MAX_VALUE;
                maxBinEntries = Integer.MIN_VALUE;
                minBinHeights = Double.MAX_VALUE;
                maxBinHeights = Double.MIN_VALUE;

                int ii = 0;
                int jj = 0;
                for (int i=0; i<nXBins+2; i++) {
                    for (int j=0; j<nYBins+2; j++) {
                        ii = convertToAIDAIndex(nXBins, i);
                        jj = convertToAIDAIndex(nYBins, j);
                        
                        double h = hist.binHeight(ii, jj);
                        int e = hist.binEntries(ii, jj);
                        
                        if (ii != IAxis.OVERFLOW_BIN && ii != IAxis.UNDERFLOW_BIN) {
                            if (jj != IAxis.OVERFLOW_BIN && jj != IAxis.UNDERFLOW_BIN) {
                                if (e > maxBinEntries) maxBinEntries = e;
                                if (e < minBinEntries) minBinEntries = e;
                                if (h > maxBinHeights) maxBinHeights = h;
                                if (h < minBinHeights) minBinHeights = h;
                            }
                        }
                        
                        heights[i][j] = h;
                        entries[i][j] = e;
                        errors[i][j]  = hist.binError(ii, jj);
                        meansX[i][j]  = hist.binMeanX(ii, jj);
                        meansY[i][j]  = hist.binMeanY(ii, jj);
                        if (hist instanceof Histogram2D) {
                            rmssX[i][j]   = ((Histogram2D) hist).binRmsX(ii, jj);
                            rmssY[i][j]   = ((Histogram2D) hist).binRmsY(ii, jj);
                        }
                    }
                }
                
                // Go along X Axis
                for ( int i=0; i<nXBins+2; i++) {
                    ii = convertToAIDAIndex(nXBins, i);
                    binEntriesX[i] = hist.binEntriesX(ii);
                    binHeightsX[i] = hist.binHeightX(ii);
                } 
                
                // Go along Y Axis
                for ( int j=0; j<nYBins+2; j++) {
                    jj = convertToAIDAIndex(nYBins, j);
                    binEntriesY[j] = hist.binEntriesY(jj);
                    binHeightsY[j] = hist.binHeightY(jj);
                }                        
            }
            
            
            // Get global histogram information
            meanX = hist.meanX();
            rmsX = hist.rmsX();
            meanY = hist.meanY();
            rmsY = hist.rmsY();
            
            equivalentBinEntries = hist.equivalentBinEntries();
            nanEntries = hist.nanEntries();
            
            extraEntries = hist.extraEntries();
            inRangeEntries = hist.entries();
            
            extraBinHeights = hist.sumExtraBinHeights();
            inRangeBinHeights = hist.sumAllBinHeights() - extraBinHeights;
            minBinHeights = hist.minBinHeight();
            maxBinHeights = hist.maxBinHeight();
        } // end synchronized
        
        
        // Set all the information
        // Set arrays
        data.setXAxis(rXAxis);
        data.setYAxis(rYAxis);
        data.setAnnotationItems(rAnnotation);
        
        data.setBinHeights(heights);
        data.setBinErrors(errors);
        data.setBinEntries(entries);
        data.setBinMeansX(meansX);
        data.setBinRmssX(rmssX);
        data.setBinMeansY(meansY);
        data.setBinRmssY(rmssY);
        
        data.setBinEntriesX(binEntriesX);
        data.setBinEntriesY(binEntriesY);
        data.setBinHeightsX(binHeightsX);
        data.setBinHeightsY(binHeightsY);
        
        // Set single numbers
        data.setMeanX(meanX);
        data.setRmsX(rmsX);        
        data.setMeanY(meanY);
        data.setRmsY(rmsY);        
        
        data.setEquivalentBinEntries(equivalentBinEntries);
        data.setNanEntries(nanEntries);

        data.setInRangeEntries(inRangeEntries);
        data.setExtraEntries(extraEntries);
        data.setMinBinEntries(minBinEntries);
        data.setMaxBinEntries(maxBinEntries);

        data.setInRangeBinHeights(inRangeBinHeights);
        data.setExtraBinHeights(extraBinHeights);
        data.setMinBinHeights(minBinHeights);
        data.setMaxBinHeights(maxBinHeights);
                
        return data;
    } 
    
   /**
    * Create RmiHist1DData structure from RemoteHistogram1D
    */
    public RmiHist2DData createRemoteData(RemoteHistogram2D hist) {
        RmiHist2DData data = new RmiHist2DData();

        RmiAxis rXAxis = null;
        RmiAxis rYAxis = null;
        RmiAnnotationItem[] rAnnotation = null;

        double meanX = Double.NaN;
        double rmsX = Double.NaN;
        double meanY = Double.NaN;
        double rmsY = Double.NaN;
        
        double equivalentBinEntries = 0.0;
        int nanEntries = 0;
        
        int inRangeEntries = 0;
        int extraEntries = 0;
        int minBinEntries = 0;
        int maxBinEntries = 0;
        
        double inRangeBinHeights = 0.0;
        double extraBinHeights = 0.0;
        double minBinHeights = 0.0;
        double maxBinHeights = 0.0;
      
        int[][] entries    = null;
        double[][] heights = null;
        double[][] errors  = null;
        double[][] rmss    = null;
        double[][] meansX  = null;
        double[][] rmssX   = null;
        double[][] meansY  = null;
        double[][] rmssY   = null;
        
        int[] binEntriesX = null;
        int[] binEntriesY = null;
        double[] binHeightsX = null;
        double[] binHeightsY = null;
        
        synchronized (hist) {
            
            // Get X axis information. So far support only FixedAxis binning            
            IAxis lXAxis = hist.xAxis();      
            int nXBins = lXAxis.bins();
            rXAxis = new RmiAxis(nXBins, lXAxis.lowerEdge(), lXAxis.upperEdge());
      
            // Get Y axis information. So far support only FixedAxis binning            
            IAxis lYAxis = hist.yAxis();      
            int nYBins = lYAxis.bins();
            rYAxis = new RmiAxis(nYBins, lYAxis.lowerEdge(), lYAxis.upperEdge());
      
            // Get Annotation information
            boolean sticky = false;
            IAnnotation lAnnotation = hist.annotation();
            if (lAnnotation != null && lAnnotation.size() > 0) {
                rAnnotation = new RmiAnnotationItem[lAnnotation.size()];
                for (int i=0; i<lAnnotation.size(); i++) {
                    String key = lAnnotation.key(i);
                    String value = lAnnotation.value(key);
                    rAnnotation[i] = new RmiAnnotationItem(key, value, sticky);
                }  
            }
            
            // Get bin information
            if (nXBins > 0 && nYBins>0) {
                if (hist.getBinEntries() != null) entries = new int[nXBins+2][nYBins+2];
                if (hist.getBinHeights() != null) heights = new double[nXBins+2][nYBins+2];
                if (hist.getBinErrors()  != null) errors  = new double[nXBins+2][nYBins+2];
                if (hist.getBinMeansX()  != null) meansX  = new double[nXBins+2][nYBins+2];
                if (hist.getBinRmssX()   != null) rmssX   = new double[nXBins+2][nYBins+2];
                if (hist.getBinMeansY()  != null) meansY  = new double[nXBins+2][nYBins+2];
                if (hist.getBinRmssY()   != null) rmssY   = new double[nXBins+2][nYBins+2];
                
                if (hist.getBinEntriesX() != null) binEntriesX = new int[nXBins+2];
                if (hist.getBinEntriesY() != null) binEntriesY = new int[nYBins+2];
                if (hist.getBinHeightsX() != null) binHeightsX = new double[nXBins+2];
                if (hist.getBinHeightsY() != null) binHeightsY = new double[nYBins+2];
                
                for (int i=0; i<(nXBins+2); i++) {
                    if (hist.getBinEntries() != null) System.arraycopy(hist.getBinEntries()[i], 0, entries[i], 0, entries[i].length);
                    if (hist.getBinHeights() != null) System.arraycopy(hist.getBinHeights()[i], 0, heights[i], 0, heights[i].length);
                    if (hist.getBinErrors()  != null) System.arraycopy(hist.getBinErrors()[i],  0, errors[i],  0, errors[i].length);
                    if (hist.getBinMeansX()  != null) System.arraycopy(hist.getBinMeansX()[i],  0, meansX[i],  0, meansX[i].length);
                    if (hist.getBinRmssX()   != null) System.arraycopy(hist.getBinRmssX()[i],   0, rmssX[i],   0, rmssX[i].length);
                    if (hist.getBinMeansY()  != null) System.arraycopy(hist.getBinMeansY()[i],  0, meansY[i],  0, meansY[i].length);
                    if (hist.getBinRmssY()   != null) System.arraycopy(hist.getBinRmssY()[i],   0, rmssY[i],   0, rmssY[i].length);
                }
                
                if (hist.getBinEntriesX() != null) System.arraycopy(hist.getBinEntriesX(), 0, binEntriesX, 0, binEntriesX.length);
                if (hist.getBinEntriesY() != null) System.arraycopy(hist.getBinEntriesY(), 0, binEntriesY, 0, binEntriesY.length);
                if (hist.getBinHeightsX() != null) System.arraycopy(hist.getBinHeightsX(), 0, binHeightsX, 0, binHeightsX.length);
                if (hist.getBinHeightsY() != null) System.arraycopy(hist.getBinHeightsY(), 0, binHeightsY, 0, binHeightsY.length);
                
                // Get global histogram information
                meanX = hist.meanX();
                rmsX  = hist.rmsX();
                meanY = hist.meanY();
                rmsY  = hist.rmsY();
                
                equivalentBinEntries = hist.equivalentBinEntries();
                nanEntries = hist.nanEntries();

                extraEntries = hist.extraEntries();
                inRangeEntries = hist.entries();
                minBinEntries = hist.getMinBinEntries();
                maxBinEntries = hist.getMinBinEntries();
                
                extraBinHeights = hist.sumExtraBinHeights();
                inRangeBinHeights = hist.sumAllBinHeights() - extraBinHeights;
                minBinHeights = hist.minBinHeight();
                maxBinHeights = hist.maxBinHeight();
            }
        } // end synchronized
        
        
        // Set all the information
        // Set arrays
        data.setXAxis(rXAxis);
        data.setYAxis(rYAxis);
        data.setAnnotationItems(rAnnotation);
        
        data.setBinHeights(heights);
        data.setBinErrors(errors);
        data.setBinEntries(entries);
        data.setBinMeansX(meansX);
        data.setBinRmssX(rmssX);
        data.setBinMeansY(meansY);
        data.setBinRmssY(rmssY);
        
        data.setBinEntriesX(binEntriesX);
        data.setBinEntriesY(binEntriesY);
        data.setBinHeightsX(binHeightsX);
        data.setBinHeightsY(binHeightsY);
        
        // Set single numbers
        data.setMeanX(meanX);
        data.setRmsX(rmsX);        
        data.setMeanY(meanY);
        data.setRmsY(rmsY);        
        
        data.setEquivalentBinEntries(equivalentBinEntries);
        data.setNanEntries(nanEntries);

        data.setInRangeEntries(inRangeEntries);
        data.setExtraEntries(extraEntries);
        data.setMinBinEntries(minBinEntries);
        data.setMaxBinEntries(maxBinEntries);

        data.setInRangeBinHeights(inRangeBinHeights);
        data.setExtraBinHeights(extraBinHeights);
        data.setMinBinHeights(minBinHeights);
        data.setMaxBinHeights(maxBinHeights);
                
        return data;
    } 
    
    /**
    * Create RmiHist1DData structure from an array of IHistogram1D
    */
    public RmiHist2DData createData(IHistogram2D[] arr) {
        RmiHist2DData[] allData = new RmiHist2DData[arr.length];
        for (int i=0; i<allData.length; i++) {
            if (arr[i] instanceof RemoteHistogram2D) allData[i] = createRemoteData((RemoteHistogram2D) arr[i]);
            else allData[i] = createData(arr[i]);
        }
        RmiHist2DData data = new RmiHist2DData();
        
        int nXBins = allData[0].getBinHeightsX().length;
        int nYBins = allData[0].getBinHeightsY().length;
        
        
        // Calculate global histogram values
        double[] sumBinHeights = new double[allData.length];
        
        double meanX = 0;
        double rmsX = 0;
        double meanY = 0;
        double rmsY = 0;
        
        int inRangeEntries = 0;
        int extraEntries = 0;
        double equivalentBinEntries = 0;
        double inRangeBinHeights = 0;
        double extraBinHeights = 0;
        double minBinHeights = Double.POSITIVE_INFINITY;
        double maxBinHeights = Double.NEGATIVE_INFINITY;
        
        double h = 0;
        for (int k=0; k<allData.length; k++) {
            h += allData[k].getInRangeBinHeights();
            inRangeBinHeights += h;
            
            inRangeEntries += allData[k].getInRangeEntries();
            extraEntries   += allData[k].getExtraEntries();
            equivalentBinEntries += allData[k].getEquivalentBinEntries();
            extraBinHeights      += allData[k].getExtraBinHeights();
            
            if (allData[k].getMinBinHeights() < minBinHeights) minBinHeights = allData[k].getMinBinHeights();
            if (allData[k].getMaxBinHeights() > maxBinHeights) maxBinHeights = allData[k].getMaxBinHeights();
            
            meanX += allData[k].getMeanX()*h;
            meanY += allData[k].getMeanY()*h;
            rmsX  += (Math.pow(allData[k].getRmsX(), 2) + Math.pow(allData[k].getMeanX(), 2))*h;
            rmsY  += (Math.pow(allData[k].getRmsY(), 2) + Math.pow(allData[k].getMeanY(), 2))*h;
        }
        meanX = meanX/inRangeBinHeights;
        meanY = meanY/inRangeBinHeights;
        rmsX = Math.sqrt(rmsX/inRangeBinHeights - meanX*meanX);
        rmsY = Math.sqrt(rmsY/inRangeBinHeights - meanY*meanY);
        
        
        
        
        // Calculate new Bin information
        int[][] entries    = new int[nXBins][nYBins];
        double[][] heights = new double[nXBins][nYBins];
        double[][] errors  = new double[nXBins][nYBins];
        double[][] meansX  = new double[nXBins][nYBins];
        double[][] rmssX   = new double[nXBins][nYBins];
        double[][] meansY  = new double[nXBins][nYBins];
        double[][] rmssY   = new double[nXBins][nYBins];
        
        int[] binEntriesX = new int[nXBins];
        int[] binEntriesY = new int[nYBins];
        double[] binHeightsX = new double[nXBins];
        double[] binHeightsY = new double[nYBins];
        
        for (int ii=0; ii<nXBins; ii++) {
            for (int jj=0; jj<nYBins; jj++) {
                for (int k=0; k<allData.length; k++) {                    
                    h = allData[k].getBinHeights()[ii][jj];
                    entries[ii][jj]  += allData[k].getBinEntries()[ii][jj] ;
                    heights[ii][jj]  += h;
                    errors[ii][jj]   += Math.pow((allData[k].getBinErrors()[ii][jj]), 2);
                    meansX[ii][jj]   += h*allData[k].getBinMeansX()[ii][jj];
                    rmssX[ii][jj]    += h*(Math.pow((allData[k].getBinRmssX()[ii][jj]), 2) + Math.pow((allData[k].getBinMeansX()[ii][jj]), 2));
                    meansY[ii][jj]   += h*allData[k].getBinMeansY()[ii][jj];
                    rmssY[ii][jj]    += h*(Math.pow((allData[k].getBinRmssY()[ii][jj]), 2) + Math.pow((allData[k].getBinMeansY()[ii][jj]), 2));
                }
                errors[ii][jj] = Math.sqrt(errors[ii][jj]);
                meansX[ii][jj] = meansX[ii][jj]/heights[ii][jj];
                meansY[ii][jj] = meansY[ii][jj]/heights[ii][jj];
                rmssX[ii][jj]  = Math.sqrt(rmssX[ii][jj]/heights[ii][jj] - meansX[ii][jj]*meansX[ii][jj]);
                rmssY[ii][jj]  = Math.sqrt(rmssY[ii][jj]/heights[ii][jj] - meansY[ii][jj]*meansY[ii][jj]);
            }
        }        

        for (int ii=0; ii<nXBins; ii++) {
            for (int k=0; k<allData.length; k++) {                    
                binEntriesX[ii] += allData[k].getBinEntriesX()[ii];
                binHeightsX[ii] += allData[k].getBinHeightsX()[ii];
            }
        }
        
        for (int jj=0; jj<nYBins; jj++) {
            for (int k=0; k<allData.length; k++) {                    
                binEntriesY[jj] += allData[k].getBinEntriesY()[jj];
                binHeightsY[jj] += allData[k].getBinHeightsY()[jj];
            }
        }
        
        
        
        // Set all the information
        // Set arrays
        data.setXAxis(allData[0].getXAxis());
        data.setYAxis(allData[0].getYAxis());        
        data.setAnnotationItems(allData[0].getAnnotationItems());
        
        data.setBinHeights(heights);
        data.setBinErrors(errors);
        data.setBinEntries(entries);
        data.setBinMeansX(meansX);
        data.setBinRmssX(rmssX);
        data.setBinMeansY(meansY);
        data.setBinRmssY(rmssY);
        
        data.setBinEntriesX(binEntriesX);
        data.setBinEntriesY(binEntriesY);
        data.setBinHeightsX(binHeightsX);
        data.setBinHeightsX(binHeightsY);
        
        // Set single numbers
        data.setMeanX(meanX);
        data.setRmsX(rmsX);        
        data.setMeanY(meanY);
        data.setRmsY(rmsY);        
        
        data.setInRangeEntries(inRangeEntries);
        data.setExtraEntries(extraEntries);
        data.setEquivalentBinEntries(equivalentBinEntries);
        data.setInRangeBinHeights(inRangeBinHeights);
        data.setExtraBinHeights(extraBinHeights);
        data.setMinBinHeights(minBinHeights);
        data.setMaxBinHeights(maxBinHeights);

        return data;
    }
    
    private int convertToAIDAIndex(int nBins, int index) {
        int mi;
        if ( index == 0 )
            mi = IAxis.UNDERFLOW_BIN;
        else if ( index == nBins+1 )
            mi = IAxis.OVERFLOW_BIN;
        else
            mi = index - 1;
        return mi;
    }
 
}
