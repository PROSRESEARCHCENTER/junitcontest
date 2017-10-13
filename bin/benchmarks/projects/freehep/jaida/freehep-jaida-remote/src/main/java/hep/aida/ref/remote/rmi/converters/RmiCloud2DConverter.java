/*
 * RmiHist1DConverter.java
 *
 * Created on October 14, 2003, 7:39 PM
 */

package hep.aida.ref.remote.rmi.converters;

import hep.aida.IAnnotation;
import hep.aida.ICloud2D;
import hep.aida.IHistogram2D;
import hep.aida.IManagedObject;
import hep.aida.ref.Annotation;
import hep.aida.ref.remote.RemoteCloud2D;
import hep.aida.ref.remote.RemoteHistogram2D;
import hep.aida.ref.remote.rmi.data.RmiAnnotationItem;
import hep.aida.ref.remote.rmi.data.RmiCloud2DData;
import hep.aida.ref.remote.rmi.data.RmiHist2DData;

/**
 *
 * @author  serbo
 */
public class RmiCloud2DConverter extends RmiConverter {
    
    private static RmiCloud2DConverter converter = null;
    
    /** Creates a new instance of RmiHist1DAdapter */
    public static RmiCloud2DConverter getInstance() {
        if (converter == null) converter = new RmiCloud2DConverter();
        return converter;
    }
    
    /** Creates a new instance of RmiHist1DConverter */
    private RmiCloud2DConverter() {
        super();
        dataType = "RmiCloud2DData";
        aidaType = "ICloud2D";
    }
    
    public Object createAidaObject(String name) {
        RemoteCloud2D result = new RemoteCloud2D(name);        
        return result;
    }
    
    public Object extractData(Object aidaObject) {
        RmiCloud2DData data = null;
        if (aidaObject instanceof hep.aida.ICloud2D) {
            data = createData((hep.aida.ICloud2D) aidaObject);
        } else if (aidaObject instanceof Object[] && ((Object[]) aidaObject)[0] instanceof ICloud2D) {
            ICloud2D[] arr = new ICloud2D[((Object[]) aidaObject).length];
            for (int i=0; i<arr.length; i++) {
                arr[i] = (ICloud2D) ((Object[]) aidaObject)[i];
            }
            data = createData(arr);
        } else
            throw new IllegalArgumentException("Not supported data type: "+aidaObject.getClass().getName());

        return data;
    }
    
    public boolean updateAidaObject(Object aidaObject, Object newData) {
        RmiCloud2DData data = null;
        if (newData instanceof RmiCloud2DData) {
            data = (RmiCloud2DData) newData;
        }

        if (!(aidaObject instanceof hep.aida.ref.remote.RemoteCloud2D))
            throw new IllegalArgumentException("Not supported object type: "+aidaObject.getClass().getName());
        if (!(data != null && data instanceof hep.aida.ref.remote.rmi.data.RmiCloud2DData))
            throw new IllegalArgumentException("Not supported data type: "+(newData == null ? "null" : newData.getClass().getName()));

        updateData((RemoteCloud2D) aidaObject, data);
        return true;
    }
    
    
    // Service methods
 
    /**
     * Update data in RemoteHistogram1d from RmiHist1DData
     * and calls setDataValid(true) method.
     */
    public IManagedObject updateData(RemoteCloud2D cloud, RmiCloud2DData data) {
        // If data == null, just leave the old dats in.
        // Maybe should clear the histogram instead?
        if (data == null) return cloud;
        
        synchronized (cloud) {
            cloud.setFillable(true);
            
            // Set historam info
            RmiHist2DData histData = data.getHist();
            RemoteHistogram2D hist = (RemoteHistogram2D) cloud.histogram();
            RmiHist2DConverter.getInstance().updateData(hist, histData);
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
            cloud.setNanEntries(data.getNanEntries());
            
            cloud.setLowerEdgeX(data.getLowerEdgeX());
            cloud.setLowerEdgeY(data.getLowerEdgeY());
            cloud.setUpperEdgeX(data.getUpperEdgeX());
            cloud.setUpperEdgeY(data.getUpperEdgeY());
            cloud.setMeanX(data.getMeanX());
            cloud.setMeanY(data.getMeanY());
            cloud.setRmsX(data.getRmsX());
            cloud.setRmsY(data.getRmsY());
            
            cloud.setValuesX(data.getValuesX());
            cloud.setValuesY(data.getValuesY());
            cloud.setWeights(data.getWeights());
            
            cloud.setFillable(false);
            cloud.setDataValid(true);
        }
        
        return cloud;
    }

   /**
    * Create RmiHist1DData structure from an IHistogram1D
    */
    public RmiCloud2DData createData(ICloud2D cloud) {
        RmiCloud2DData data = new RmiCloud2DData();

        RmiHist2DData histData = null;
        RmiAnnotationItem[] rAnnotation = null;
        boolean isConverted = false;
        int maxEntries = 0;
        int nanEntries = 0;
        int entries    = 0;
        double sumOfWeights = 0.;

        double lowerEdgeX = Double.NaN;
        double lowerEdgeY = Double.NaN;
        double upperEdgeX = Double.NaN;
        double upperEdgeY = Double.NaN;
        double meanX = Double.NaN;
        double meanY = Double.NaN;
        double rmsX = Double.NaN;
        double rmsY = Double.NaN;
        double[] valuesX  = null;
        double[] valuesY  = null;
        double[] weights  = null;
        
        
        synchronized (cloud) {
            isConverted = cloud.isConverted();
            maxEntries = cloud.maxEntries();
            nanEntries = cloud.nanEntries();
            entries    = cloud.entries();
            sumOfWeights = cloud.sumOfWeights();
            lowerEdgeX = cloud.lowerEdgeX();
            lowerEdgeY = cloud.lowerEdgeY();
            upperEdgeX = cloud.upperEdgeX();
            upperEdgeY = cloud.upperEdgeY();
            if (isConverted) {
                IHistogram2D hist = cloud.histogram();
                histData = RmiHist2DConverter.getInstance().createData(hist);
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
                meanX = cloud.meanX();
                meanY = cloud.meanY();
                rmsX = cloud.rmsX();
                rmsY = cloud.rmsY();
                
                // Get bin information
                if (entries > 0) {
                    valuesX  = new double[entries];
                    valuesY  = new double[entries];
                    weights = new double[entries];
                    int i = 0;
                    for (i=0; i<entries; i++) {
                        valuesX[i] = cloud.valueX(i);
                        valuesY[i] = cloud.valueY(i);
                        weights[i] = cloud.weight(i);
                    }
                    
                }
            }
        } // end synchronized
        
        
        // Set all the information
        if (isConverted) {
            data.setHist(histData);

        } else {
            histData = new RmiHist2DData();
            histData.setAnnotationItems(rAnnotation);
        }
        
        data.setValuesX(valuesX);
        data.setValuesY(valuesY);
        data.setWeights(weights);
        
        data.setConverted(isConverted);
        data.setEntries(entries);
        data.setSumOfWeights(sumOfWeights);
        data.setMaxEntries(maxEntries);
        data.setNanEntries(nanEntries);
        
        data.setLowerEdgeX(lowerEdgeX);
        data.setLowerEdgeY(lowerEdgeY);
        data.setUpperEdgeX(upperEdgeX);
        data.setUpperEdgeY(upperEdgeY);
        
        data.setMeanX(meanX);
        data.setMeanY(meanY);
        data.setRmsX(rmsX);
        data.setRmsY(rmsY);
        
        return data;
    } 
        
   /**
    * Create RmiHist1DData structure from an array of IHistogram1D
    */
    public RmiCloud2DData createData(ICloud2D[] arr) {
        RmiCloud2DData[] allData = new RmiCloud2DData[arr.length];
        for (int i=0; i<allData.length; i++) {
            allData[i] = createData(arr[i]);
        }
        
        /*
        RmiCloud2DData data = new RmiCloud2DData();
        
        RmiHist2DData histData = null;
        RmiAnnotationItem[] rAnnotation = null;
        boolean isConverted = false;
        int maxEntries = 0;
        int entries    = 0;
        double sumOfWeights = 0.;

        double lowerEdgeX = Double.NaN;
        double lowerEdgeY = Double.NaN;
        double upperEdgeX = Double.NaN;
        double upperEdgeY = Double.NaN;
        double meanX = Double.NaN;
        double meanY = Double.NaN;
        double rmsX = Double.NaN;
        double rmsY = Double.NaN;
        double[] valuesX  = null;
        double[] valuesY  = null;
        double[] weights  = null;
        
        // Do Merging here:
        
            
        // Set all the information
        if (isConverted) {
            data.setHist(histData);
        } else {
            histData = new RmiHist2DData();
            histData.setAnnotationItems(rAnnotation);
        }
        
        data.setValuesX(valuesX);
        data.setValuesY(valuesY);
        data.setWeights(weights);
        
        data.setConverted(isConverted);
        data.setEntries(entries);
        data.setSumOfWeights(sumOfWeights);
        data.setMaxEntries(maxEntries);
        
        data.setLowerEdgeX(lowerEdgeX);
        data.setLowerEdgeY(lowerEdgeY);
        data.setUpperEdgeX(upperEdgeX);
        data.setUpperEdgeY(upperEdgeY);
        
        data.setMeanX(meanX);
        data.setMeanY(meanY);
        data.setRmsX(rmsX);
        data.setRmsY(rmsY);
        
        return data;
        */
        return allData[0]; 
    }
}
