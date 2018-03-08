/*
 * RmiHist1DConverter.java
 *
 * Created on October 14, 2003, 7:39 PM
 */

package hep.aida.ref.remote.rmi.converters;

import hep.aida.IAnnotation;
import hep.aida.IAxis;
import hep.aida.IHistogram1D;
import hep.aida.IManagedObject;
import hep.aida.ref.Annotation;
import hep.aida.ref.histogram.Histogram1D;
import hep.aida.ref.remote.RemoteHistogram1D;
import hep.aida.ref.remote.rmi.data.RmiAnnotationItem;
import hep.aida.ref.remote.rmi.data.RmiAxis;
import hep.aida.ref.remote.rmi.data.RmiHist1DData;

/**
 *
 * @author  serbo
 */
public class RmiHist1DConverter extends RmiConverter {
    
    protected static RmiHist1DConverter converter = null;
    
    /** Creates a new instance of RmiHist1DAdapter */
    public static RmiHist1DConverter getInstance() {
        if (converter == null) converter = new RmiHist1DConverter();
        return converter;
    }
    
    /** Creates a new instance of RmiHist1DConverter */
    protected RmiHist1DConverter() {
        super();
        dataType = "RmiHist1DData";
        aidaType = "IHistogram1D";
    }
    
    public Object createAidaObject(String name) {
        RemoteHistogram1D result = new RemoteHistogram1D(name);        
        return result;
    }
    
    public Object extractData(Object aidaObject) {
        RmiHist1DData data = null;
        if (aidaObject instanceof RemoteHistogram1D) {
            data = createRemoteData((RemoteHistogram1D) aidaObject);
        } else if (aidaObject instanceof hep.aida.IHistogram1D) {
            data = createData((hep.aida.IHistogram1D) aidaObject);
        } else if (aidaObject instanceof Object[] && ((Object[]) aidaObject)[0] instanceof IHistogram1D) {
            IHistogram1D[] arr = new IHistogram1D[((Object[]) aidaObject).length];
            for (int i=0; i<arr.length; i++) {
                arr[i] = (IHistogram1D) ((Object[]) aidaObject)[i];
            }
            data = createData(arr);
        } else
            throw new IllegalArgumentException("Not supported data type: "+aidaObject.getClass().getName());

        return data;
    }
    
    public boolean updateAidaObject(Object aidaObject, Object newData) {
        RmiHist1DData data = null;
        if (newData instanceof RmiHist1DData) {
            data = (RmiHist1DData) newData;
        }

        if (!(aidaObject instanceof hep.aida.ref.remote.RemoteHistogram1D))
            throw new IllegalArgumentException("Not supported object type: "+aidaObject.getClass().getName());
        if (!(data != null && data instanceof hep.aida.ref.remote.rmi.data.RmiHist1DData))
            throw new IllegalArgumentException("Not supported data type: "+(newData == null ? "null" : newData.getClass().getName()));

        updateData((RemoteHistogram1D) aidaObject, data);
        return true;
    }
    
    
    // Service methods
 
   /**
    * Update data in RemoteHistogram1d from RmiHist1DData
    * and calls setDataValid(true) method.
    */ 
   public IManagedObject updateData(RemoteHistogram1D hist, RmiHist1DData data)
   {
       // If data == null, just leave the old dats in. 
       // Maybe should clear the histogram instead?
       if (data == null) return hist;
       
      synchronized (hist) {
        hist.setFillable(true);
       
        // Set all other information
        hist.setHeights(data.getBinHeights());
        hist.setErrors(data.getBinErrors());
        hist.setEntries(data.getBinEntries());
        hist.setMeans(data.getBinMeans());
        hist.setRmss(data.getBinRmss());
        hist.setEquivalentBinEntries(data.getEquivalentBinEntries());
        hist.setNanEntries(data.getNanEntries());
        hist.setMean(data.getMean());
        hist.setRms(data.getRms());
        
        // Check if X axis binning or edges are different
        IAxis lAxis = hist.axis();      
        IAxis rAxis = data.getAxis(); 
        int nBins = rAxis.bins();
        IAxis newAxis = null;
        if (lAxis == null || lAxis.bins() != nBins || 
            lAxis.lowerEdge() != rAxis.lowerEdge() || lAxis.upperEdge() != rAxis.upperEdge()) {
            hist.setAxis(nBins, rAxis.lowerEdge(), rAxis.upperEdge()); 
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
      
        hist.setFillable(false);
        hist.setDataValid(true);
      }
      
      return hist;
   }
   
   /**
    * Create RmiHist1DData structure from an IHistogram1D
    */
    public RmiHist1DData createData(IHistogram1D hist) {
        if (hist == null) return null;
        
        RmiHist1DData data = new RmiHist1DData();

        RmiAxis rAxis = null;
        RmiAnnotationItem[] rAnnotation = null;

        double equivalentBinEntries = Double.NaN;
        int nanEntries = 0;
        double mean = Double.NaN;
        double rms = Double.NaN;
        
        int[] entries    = null;
        double[] heights = null;
        double[] errors  = null;
        double[] means   = null;
        double[] rmss    = null;
        
        synchronized (hist) {
            
            // Get X axis information. So far support only FixedAxis binning            
            IAxis lAxis = hist.axis();      
            int nBins = lAxis.bins();
            rAxis = new RmiAxis(nBins, lAxis.lowerEdge(), lAxis.upperEdge());
      
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
            
            // Get global histogram information
            equivalentBinEntries = hist.equivalentBinEntries();
            nanEntries = hist.nanEntries();
            mean = hist.mean();
            rms = hist.rms();
            
            // Get bin information
            if (nBins > 0) {
                entries    = new int[nBins+2];
                heights = new double[nBins+2];
                errors  = new double[nBins+2];
                means   = new double[nBins+2];
                rmss    = new double[nBins+2];
                int i = 0;
                for (i=0; i<nBins; i++) {
                    heights[i+1] = hist.binHeight(i);
                    errors[i+1]  = hist.binError(i);
                    entries[i+1] = hist.binEntries(i);
                    means[i+1]   = hist.binMean(i);
                    if (hist instanceof Histogram1D) rmss[i+1]    = ((Histogram1D) hist).binRms(i);
                }
        
                // UNDERFLOW_BIN
                i = IAxis.UNDERFLOW_BIN;
                int j = 0;
                heights[j] = hist.binHeight(i);
                errors[j]  = hist.binError(i);
                entries[j] = hist.binEntries(i);
                means[j]   = hist.binMean(i);
                if (hist instanceof Histogram1D) rmss[j]    = ((Histogram1D) hist).binRms(i);
      
                // OVERFLOW_BIN
                i = IAxis.OVERFLOW_BIN;
                j = nBins+1;
                heights[j] = hist.binHeight(i);
                errors[j]  = hist.binError(i);
                entries[j] = hist.binEntries(i);
                means[j]   = hist.binMean(i);
                if (hist instanceof Histogram1D) rmss[j]    = ((Histogram1D) hist).binRms(i);            
            }
        } // end synchronized
        
        
        // Set all the information
        data.setAxis(rAxis);
        data.setAnnotationItems(rAnnotation);
        
        data.setBinHeights(heights);
        data.setBinErrors(errors);
        data.setBinEntries(entries);
        data.setBinMeans(means);
        data.setBinRmss(rmss);
        
        data.setEquivalentBinEntries(equivalentBinEntries);
        data.setNanEntries(nanEntries);
        data.setMean(mean);
        data.setRms(rms);        
        
        return data;
    } 
        
   /**
    * Create RmiHist1DData structure from an IHistogram1D
    */
    public RmiHist1DData createRemoteData(RemoteHistogram1D hist) {
        if (hist == null) return null;
        
        RmiHist1DData data = new RmiHist1DData();

        RmiAxis rAxis = null;
        RmiAnnotationItem[] rAnnotation = null;

        double equivalentBinEntries = 0;
        int nanEntries = 0;
        double mean = 0;
        double rms = 0;
        
        int[] entries    = null;
        double[] heights = null;
        double[] errors  = null;
        double[] means   = null;
        double[] rmss    = null;
        
        synchronized (hist) {
            
            // Get X axis information. So far support only FixedAxis binning            
            IAxis lAxis = hist.axis();      
            int nBins = lAxis.bins();
            rAxis = new RmiAxis(nBins, lAxis.lowerEdge(), lAxis.upperEdge());
      
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
            
            // Get global histogram information
            equivalentBinEntries = hist.equivalentBinEntries();
            nanEntries = hist.nanEntries();
            mean = hist.mean();
            rms = hist.rms();
            
            // Get bin information
            entries = hist.getBinEntries();
            heights = hist.getBinHeights();
            errors  = hist.getBinErrors();
            means   = hist.getBinMeans();
            rmss    = hist.getBinRms();
            
        } // end synchronized
        
        
        // Set all the information
        data.setAxis(rAxis);
        data.setAnnotationItems(rAnnotation);
        
        data.setBinHeights(heights);
        data.setBinErrors(errors);
        data.setBinEntries(entries);
        data.setBinMeans(means);
        data.setBinRmss(rmss);
        
        data.setEquivalentBinEntries(equivalentBinEntries);
        data.setNanEntries(nanEntries);
        data.setMean(mean);
        data.setRms(rms);        
        
        return data;
    } 
        
   /**
    * Create RmiHist1DData structure from an array of IHistogram1D
    */
    public RmiHist1DData createData(IHistogram1D[] arr) {
        RmiHist1DData[] allData = new RmiHist1DData[arr.length];
        for (int i=0; i<allData.length; i++) {
            if (arr[i] instanceof RemoteHistogram1D) allData[i] = createRemoteData((RemoteHistogram1D) arr[i]);
            else allData[i] = createData(arr[i]);
        }
        RmiHist1DData data = new RmiHist1DData();
        data.setAxis(allData[0].getAxis());
        data.setAnnotationItems(allData[0].getAnnotationItems());
        
        double mean = 0;
        int nanEntries = 0;
        double rms = 0;
        double[] sumBinHeights = new double[allData.length];
        
        int nBins = data.getAxis().bins();
        int[] entries    = new int[nBins+2];
        double[] heights = new double[nBins+2];
        double[] errors  = new double[nBins+2];
        double[] means   = new double[nBins+2];
        double[] rmss    = new double[nBins+2];
        
            
        for (int ii=0; ii<nBins+2; ii++) {
            for (int i=0; i<allData.length; i++) {
                double h = allData[i].getBinHeights()[ii];
                if (ii>0 && ii<nBins+1) sumBinHeights[i] += h;
                heights[ii] += h;
                entries[ii] += allData[i].getBinEntries()[ii];
                errors[ii]  += Math.pow(allData[i].getBinErrors()[ii], 2);
                means[ii]   += allData[i].getBinMeans()[ii]*h;
                rmss[ii]    += (Math.pow((allData[i].getBinRmss()[ii]), 2) + Math.pow((allData[i].getBinMeans()[ii]), 2))*h;
            }
            errors[ii]  = Math.sqrt(errors[ii]);
            means[ii]   = means[ii]/heights[ii];
            rmss[ii]    = Math.sqrt(rmss[ii]/heights[ii] - means[ii]*means[ii]);
        }
       
        data.setBinHeights(heights);
        data.setBinErrors(errors);
        data.setBinEntries(entries);
        data.setBinMeans(means);
        data.setBinRmss(rmss);
        
        double sh = 0;
        for (int i=0; i<allData.length; i++) {
            sh += sumBinHeights[i];
            mean += allData[i].getMean()*sumBinHeights[i];
            rms  += (Math.pow(allData[i].getRms(), 2) + Math.pow(allData[i].getMean(), 2))*sumBinHeights[i];
            nanEntries += allData[i].getNanEntries();
        }
        mean = mean/sh;
        rms = Math.sqrt(rms/sh - mean*mean);
        
        data.setMean(mean);
        data.setNanEntries(nanEntries);
        data.setRms(rms);        
        
        return data;
    }
}
