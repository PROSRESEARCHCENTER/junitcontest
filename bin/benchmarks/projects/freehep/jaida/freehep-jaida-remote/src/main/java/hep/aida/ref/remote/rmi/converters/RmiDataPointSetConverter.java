/*
 * RmiHist1DConverter.java
 *
 * Created on October 14, 2003, 7:39 PM
 */

package hep.aida.ref.remote.rmi.converters;

import hep.aida.IAnnotation;
import hep.aida.IDataPoint;
import hep.aida.IDataPointSet;
import hep.aida.IManagedObject;
import hep.aida.IMeasurement;
import hep.aida.ref.Annotation;
import hep.aida.ref.remote.RemoteDataPointSet;
import hep.aida.ref.remote.RemoteManagedObject;
import hep.aida.ref.remote.rmi.data.RmiAnnotationItem;
import hep.aida.ref.remote.rmi.data.RmiDataPointSetData;

/**
 *
 * @author  serbo
 */
public class RmiDataPointSetConverter extends RmiConverter {
    
    private static RmiDataPointSetConverter converter = null;
    
    /** Creates a new instance of RmiHist1DAdapter */
    public static RmiDataPointSetConverter getInstance() {
        if (converter == null) converter = new RmiDataPointSetConverter();
        return converter;
    }
    
    /** Creates a new instance of RmiHist1DConverter */
    private RmiDataPointSetConverter() {
        super();
        dataType = "RmiDataPointSetData";
        aidaType = "IDataPointSet";
    }
    
    public Object createAidaObject(String name) {
        RemoteDataPointSet result = new RemoteDataPointSet(name);        
        return result;
    }
    
    public Object extractData(Object aidaObject) {
        if (!(aidaObject instanceof hep.aida.IDataPointSet))
            throw new IllegalArgumentException("Not supported data type: "+aidaObject.getClass().getName());

        RmiDataPointSetData data = createData((hep.aida.IDataPointSet) aidaObject);
 
        return data;
    }
    
    public boolean updateAidaObject(Object aidaObject, Object newData) {
        RmiDataPointSetData data = null;
        if (newData instanceof RmiDataPointSetData) {
            data = (RmiDataPointSetData) newData;
        }

        if (!(aidaObject instanceof hep.aida.ref.remote.RemoteDataPointSet))
            throw new IllegalArgumentException("Not supported object type: "+aidaObject.getClass().getName());
        if (!(data != null && data instanceof hep.aida.ref.remote.rmi.data.RmiDataPointSetData))
            throw new IllegalArgumentException("Not supported data type: "+(newData == null ? "null" : newData.getClass().getName()));

        updateData((RemoteDataPointSet) aidaObject, data);
        return true;
    }
    
    
    // Service methods
 
   /**
    * Update data in RemoteHistogram1d from RmiHist1DData
    * and calls setDataValid(true) method.
    */ 
   public IManagedObject updateData(RemoteDataPointSet hist, RmiDataPointSetData data)
   {
       // If data == null, just leave the old dats in. 
       // Maybe should clear the histogram instead?
       if (data == null) return hist;
       
      synchronized (hist) {
        hist.setFillable(true);
       
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
        //hist.setXAxisType(data.getXAxisType());
        //hist.setYAxisType(data.getYAxisType());
        //hist.setTimeOfLastUpdate(data.getTimeOfLastUpdate());
        
        hist.setDimension(data.getDimension());
        hist.setUpperExtent(data.getUpperExtent());
        hist.setLowerExtent(data.getLowerExtent());
        hist.setValues(data.getValues());
        hist.setPlusErrors(data.getPlusErrors());
        hist.setMinusErrors(data.getMinusErrors());
        
        hist.setFillable(false);
        hist.setDataValid(true);
      }
      
      return hist;
   }
   
   /**
    * Create RmiHist1DData structure from an IHistogram1D
    */
    public RmiDataPointSetData createData(IDataPointSet hist) {
        RmiDataPointSetData data = new RmiDataPointSetData();

        RmiAnnotationItem[] rAnnotation = null;

         int dimension    = 0;
         double[] upperExtent = null;
         double[] lowerExtent = null;
         double[] values      = null;
         double[] plusErrors  = null;
         double[] minusErrors = null;
         String xAxisType = null;
         String yAxisType = null;
         long timeOfLastUpdate = 0;
        
        synchronized (hist) {
            
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
            
            int nPoints = hist.size();
            dimension = hist.dimension();
            
            // Get information
            if (nPoints > 0) {
                upperExtent = new double[dimension];
                lowerExtent = new double[dimension];
                values = new double[dimension*nPoints];
                plusErrors = new double[dimension*nPoints];
                minusErrors = new double[dimension*nPoints];
                
                for (int dim=0; dim<dimension; dim++) {
                    upperExtent[dim] = hist.upperExtent(dim);
                    lowerExtent[dim] = hist.lowerExtent(dim);
                }

                int index = 0;
                for (int ip=0; ip<nPoints; ip++) {
                    IDataPoint p = hist.point(ip);
                    for (int dim=0; dim<dimension; dim++) {
                        index = ip*dimension+dim;
                        IMeasurement m = p.coordinate(dim);
                        values[index]      = m.value();
                        plusErrors[index]  = m.errorPlus();
                        minusErrors[index] = m.errorMinus();
                    }                     
                }
            }
            if (hist instanceof RemoteManagedObject) {
                RemoteManagedObject rdps = (RemoteManagedObject) hist;
                //xAxisType = rdps.getXAxisType();
                //yAxisType = rdps.getYAxisType();
                //timeOfLastUpdate = rdps.getTimeOfLastUpdate();
            }
        } // end synchronized
        
        
        // Set all the information
        data.setAnnotationItems(rAnnotation);
        
        //data.setXAxisType(xAxisType);
        //data.setYAxisType(yAxisType);
        //data.setTimeOfLastUpdate(timeOfLastUpdate);
        
        data.setDimension(dimension);
        data.setUpperExtent(upperExtent);
        data.setLowerExtent(lowerExtent);
        data.setValues(values);
        data.setPlusErrors(plusErrors);
        data.setMinusErrors(minusErrors);
       
        return data;
    } 
        
}
