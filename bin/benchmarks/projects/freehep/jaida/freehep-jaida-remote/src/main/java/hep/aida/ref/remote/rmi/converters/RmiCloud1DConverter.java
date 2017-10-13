/*
 * RmiHist1DConverter.java
 *
 * Created on October 14, 2003, 7:39 PM
 */

package hep.aida.ref.remote.rmi.converters;

import hep.aida.IAnnotation;
import hep.aida.ICloud1D;
import hep.aida.IHistogram1D;
import hep.aida.IManagedObject;
import hep.aida.ref.Annotation;
import hep.aida.ref.remote.RemoteCloud1D;
import hep.aida.ref.remote.RemoteHistogram1D;
import hep.aida.ref.remote.rmi.data.RmiAnnotationItem;
import hep.aida.ref.remote.rmi.data.RmiCloud1DData;
import hep.aida.ref.remote.rmi.data.RmiHist1DData;

/**
 *
 * @author  serbo
 */
public class RmiCloud1DConverter extends RmiConverter {
    
    private static RmiCloud1DConverter converter = null;
    
    /** Creates a new instance of RmiHist1DAdapter */
    public static RmiCloud1DConverter getInstance() {
        if (converter == null) converter = new RmiCloud1DConverter();
        return converter;
    }
    
    /** Creates a new instance of RmiHist1DConverter */
    private RmiCloud1DConverter() {
        super();
        dataType = "RmiCloud1DData";
        aidaType = "ICloud1D";
    }
    
    public Object createAidaObject(String name) {
        RemoteCloud1D result = new RemoteCloud1D(name);        
        return result;
    }
    
    public Object extractData(Object aidaObject) {
        RmiCloud1DData data = null;
        if (aidaObject instanceof hep.aida.ICloud1D) {
            data = createData((hep.aida.ICloud1D) aidaObject);
        } else if (aidaObject instanceof Object[] && ((Object[]) aidaObject)[0] instanceof ICloud1D) {
            ICloud1D[] arr = new ICloud1D[((Object[]) aidaObject).length];
            for (int i=0; i<arr.length; i++) {
                arr[i] = (ICloud1D) ((Object[]) aidaObject)[i];
            }
            data = createData(arr);
        } else
            throw new IllegalArgumentException("Not supported data type: "+aidaObject.getClass().getName());

        return data;
    }
    
    public boolean updateAidaObject(Object aidaObject, Object newData) {
        RmiCloud1DData data = null;
        if (newData instanceof RmiCloud1DData) {
            data = (RmiCloud1DData) newData;
        }

        if (!(aidaObject instanceof hep.aida.ref.remote.RemoteCloud1D))
            throw new IllegalArgumentException("Not supported object type: "+aidaObject.getClass().getName());
        if (!(data != null && data instanceof hep.aida.ref.remote.rmi.data.RmiCloud1DData))
            throw new IllegalArgumentException("Not supported data type: "+(newData == null ? "null" : newData.getClass().getName()));

        updateData((RemoteCloud1D) aidaObject, data);
        return true;
    }
    
    
    // Service methods
 
    /**
     * Update data in RemoteHistogram1d from RmiHist1DData
     * and calls setDataValid(true) method.
     */
    public IManagedObject updateData(RemoteCloud1D cloud, RmiCloud1DData data) {
        // If data == null, just leave the old dats in.
        // Maybe should clear the histogram instead?
        if (data == null) return cloud;
        
        synchronized (cloud) {
            cloud.setFillable(true);
            
            // Set historam info
            RmiHist1DData histData = data.getHist();
            RemoteHistogram1D hist = (RemoteHistogram1D) cloud.histogram();
            RmiHist1DConverter.getInstance().updateData(hist, histData);
            hist.setFillable(true);
            hist.setDataValid(true);
            
            // Check for "stat.Updated" info
            java.util.Date date = new java.util.Date();
            java.text.DateFormat df = java.text.DateFormat.getTimeInstance();
            String dateString = df.format(date);
            IAnnotation localAnnotation = cloud.annotation();
            if (localAnnotation instanceof Annotation)  
                ((Annotation) localAnnotation).setFillable(true);          
            try {
                String value = localAnnotation.value("stat.Updated");
                if (value == null || value.equals("0") || value.equals(""))
                    localAnnotation.setValue("stat.Updated", dateString);
            } catch (IllegalArgumentException e) {
                localAnnotation.addItem("stat.Updated", dateString);                
            }
            
            if (localAnnotation instanceof Annotation)  
                ((Annotation) localAnnotation).setFillable(false);          
        
      
 
            // Set all other information
            cloud.setConverted(data.getConverted());
            cloud.setEntries(data.getEntries());
            cloud.setSummOfWeights(data.getSumOfWeights());
            cloud.setMaxEntries(data.getMaxEntries());
            
            cloud.setLowerEdge(data.getLowerEdge());
            cloud.setUpperEdge(data.getUpperEdge());
            cloud.setMean(data.getMean());
            cloud.setRms(data.getRms());
            
            cloud.setValues(data.getValues());
            cloud.setWeights(data.getWeights());
            
            cloud.setFillable(false);
            cloud.setDataValid(true);
        }
        
        return cloud;
    }

   /**
    * Create RmiHist1DData structure from an IHistogram1D
    */
    public RmiCloud1DData createData(ICloud1D cloud) {
        RmiCloud1DData data = new RmiCloud1DData();

        RmiHist1DData histData = null;
        RmiAnnotationItem[] rAnnotation = null;
        boolean isConverted = false;
        int maxEntries = 0;
        int entries    = 0;
        double sumOfWeights = 0.;

        double lowerEdge = Double.NaN;
        double upperEdge = Double.NaN;
        double mean = Double.NaN;
        double rms = Double.NaN;
        double[] values  = null;
        double[] weights = null;
        
        
        synchronized (cloud) {
            isConverted  = cloud.isConverted();
            maxEntries   = cloud.maxEntries();
            entries      = cloud.entries();
            sumOfWeights = cloud.sumOfWeights();
            lowerEdge    = cloud.lowerEdge();
            upperEdge    = cloud.upperEdge();
            if (isConverted) {
                IHistogram1D hist = cloud.histogram();
                histData = RmiHist1DConverter.getInstance().createData(hist);
            } else {
                
                // Get Annotation information
                boolean sticky = false;
                IAnnotation lAnnotation = cloud.annotation();
                if (lAnnotation != null && lAnnotation.size() > 0) {
                    rAnnotation = new RmiAnnotationItem[lAnnotation.size()];
                    for (int i=0; i<lAnnotation.size(); i++) {
                        String key = lAnnotation.key(i);
                        String value = lAnnotation.value(key);
                        rAnnotation[i] = new RmiAnnotationItem(key, value, sticky);
                    }
                }
                
               // Get global histogram information
                mean = cloud.mean();
                rms = cloud.rms();
                
                // Get bin information
                if (entries > 0) {
                    values  = new double[entries];
                    weights = new double[entries];
                    int i = 0;
                    for (i=0; i<entries; i++) {
                        values[i]  = cloud.value(i);
                        weights[i] = cloud.weight(i);
                    }
                    
                }
            }
        } // end synchronized
        
        
        // Set all the information
        if (isConverted) {
            data.setHist(histData);
        } else {
            histData = new RmiHist1DData();
            histData.setAnnotationItems(rAnnotation);
        }
        
        data.setValues(values);
        data.setWeights(weights);
        
        data.setConverted(isConverted);
        data.setEntries(entries);
        data.setSumOfWeights(sumOfWeights);
        data.setMaxEntries(maxEntries);
        
        data.setLowerEdge(lowerEdge);
        data.setUpperEdge(upperEdge);
        
        data.setMean(mean);
        data.setRms(rms);
        
        return data;
    } 
        
   /**
    * Create RmiHist1DData structure from an array of IHistogram1D
    */
    public RmiCloud1DData createData(ICloud1D[] arr) {
        System.out.println("Create data for: "+arr.length);
        
        RmiCloud1DData[] allData = new RmiCloud1DData[arr.length];
        for (int i=0; i<allData.length; i++) {
            allData[i] = createData(arr[i]);
        }
        
        /*
        RmiCloud1DData data = new RmiCloud1DData();
        
        RmiHist1DData histData = null;
        RmiAnnotationItem[] rAnnotation = null;
        boolean isConverted = false;
        int maxEntries = 0;
        int entries    = 0;

        double lowerEdge = Double.NaN;
        double upperEdge = Double.NaN;
        double mean = Double.NaN;
        double rms = Double.NaN;
        double[] values  = null;
        double[] weights = null;
        
        // Do Merging here:
        
            
        // Set all the information
        if (isConverted) {
            data.setHist(histData);
        } else {
            histData = new RmiHist1DData();
            histData.setAnnotationItems(rAnnotation);
        }
        
        data.setValues(values);
        data.setWeights(weights);
        
        data.setConverted(isConverted);
        data.setEntries(entries);
        data.setMaxEntries(maxEntries);
        
        data.setLowerEdge(lowerEdge);
        data.setUpperEdge(upperEdge);
        
        data.setMean(mean);
        data.setRms(rms);  
        
        return data;
        */
        
        return allData[0];
    }
}
