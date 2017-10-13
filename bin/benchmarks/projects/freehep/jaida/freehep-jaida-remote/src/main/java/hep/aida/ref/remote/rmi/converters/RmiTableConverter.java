/*
 * RmiHist1DConverter.java
 *
 * Created on October 14, 2003, 7:39 PM
 */

package hep.aida.ref.remote.rmi.converters;

import hep.aida.IAnnotation;
import hep.aida.IManagedObject;
import hep.aida.ref.Annotation;
import hep.aida.ref.remote.RemoteTable;
import hep.aida.ref.remote.interfaces.ITable;
import hep.aida.ref.remote.rmi.data.RmiAnnotationItem;
import hep.aida.ref.remote.rmi.data.RmiTableData;

/**
 *
 * @author  serbo
 */
public class RmiTableConverter extends RmiConverter {
    
    protected static RmiTableConverter converter = null;
    
    /** Creates a new instance of RmiTableConverter */
    public static RmiTableConverter getInstance() {
        if (converter == null) converter = new RmiTableConverter();
        return converter;
    }
    
    /** Creates a new instance of RmiTableConverter */
    protected RmiTableConverter() {
        super();
        dataType = "RmiTableData";
        aidaType = "ITable";
    }
    
    public Object createAidaObject(String name) {
        RemoteTable result = new RemoteTable(name);        
        return result;
    }
    
    public Object extractData(Object aidaObject) {
        RmiTableData data = null;
        if (aidaObject instanceof ITable) {
            data = createData((ITable) aidaObject);
        } else if (aidaObject instanceof Object[] && ((Object[]) aidaObject)[0] instanceof ITable) {
            ITable[] arr = new ITable[((Object[]) aidaObject).length];
            for (int i=0; i<arr.length; i++) {
                arr[i] = (ITable) ((Object[]) aidaObject)[i];
            }
            data = createData(arr);
        } else
            throw new IllegalArgumentException("Not supported data type: "+aidaObject.getClass().getName());

        return data;
    }
    
    public boolean updateAidaObject(Object aidaObject, Object newData) {
        RmiTableData data = null;
        if (newData instanceof RmiTableData) {
            data = (RmiTableData) newData;
        }

        if (!(aidaObject instanceof hep.aida.ref.remote.RemoteTable))
            throw new IllegalArgumentException("Not supported object type: "+aidaObject.getClass().getName());
        if (!(data != null && data instanceof hep.aida.ref.remote.rmi.data.RmiTableData))
            throw new IllegalArgumentException("Not supported data type: "+(newData == null ? "null" : newData.getClass().getName()));

        updateData((RemoteTable) aidaObject, data);
        return true;
    }
    
    
    // Service methods
 
   /**
    * Update data in RemoteHistogram1d from RmiHist1DData
    * and calls setDataValid(true) method.
    */ 
   public IManagedObject updateData(RemoteTable hist, RmiTableData data)
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
        hist.setLabels(data.getLabels());
        hist.setValues(data.getValues());
        
        hist.setFillable(false);
        hist.setDataValid(true);
      }
      
      return hist;
   }
   
   /**
    * Create RmiHist1DData structure from an IHistogram1D
    */
    public RmiTableData createData(ITable hist) {
        RmiTableData data = new RmiTableData();

        RmiAnnotationItem[] rAnnotation = null;

        String[] labels = null;
        Object[][] values = null;
        
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
            
            int columns = hist.columnCount();
            int rows = hist.rowCount();
            labels = new String[columns];
            values = new Object[rows][columns];
            
            for (int i=0; i<columns; i++) {
                labels[i] = hist.columnName(i);
                for (int j=0; j<rows; j++) {
                    values[j][i] = hist.valueAt(j, i);
                }
            }
            
        } // end synchronized
        
        
        // Set all the information
        data.setAnnotationItems(rAnnotation);
        
        data.setLabels(labels);
        data.setValues(values);
        
        return data;
    } 
        
   /**
    * Create RmiTableData structure from an array of ITable
    * For now just use the first element in array
    */
    public RmiTableData createData(ITable[] arr) {
        /*
        RmiTableData[] allData = new RmiTableData[arr.length];
        for (int i=0; i<allData.length; i++) {
            allData[i] = createData(arr[i]);
        }
        RmiTableData data = new RmiTableData();
        data.setAnnotationItems(allData[0].getAnnotationItems());
        */
        
        RmiTableData data = createData(arr[0]);
        return data;
    }
}
