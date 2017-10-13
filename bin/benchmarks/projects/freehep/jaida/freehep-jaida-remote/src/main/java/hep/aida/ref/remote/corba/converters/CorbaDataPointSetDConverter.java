/*
 * CorbaHist1DAdapter.java
 *
 * Created on June 12, 2003, 5:54 PM
 */

package hep.aida.ref.remote.corba.converters;

//import hep.aida.dev.IConverter;
import hep.aida.IAnnotation;
import hep.aida.IDataPoint;
import hep.aida.IDataPointSet;
import hep.aida.IManagedObject;
import hep.aida.IMeasurement;
import hep.aida.ref.Annotation;
import hep.aida.ref.remote.RemoteDataPointSet;
import hep.aida.ref.remote.corba.generated.AnnotationItem;
import hep.aida.ref.remote.corba.generated.DataPointData;
import hep.aida.ref.remote.corba.generated.DataPointSetData;
import hep.aida.ref.remote.corba.generated.DataPointSetDataHelper;
import hep.aida.ref.remote.corba.generated.MeasurementData;

import org.omg.CORBA.Any;

/**
 * Converts Hist1DData to RemoteHistogram1D
 * @author  serbo
 */
public final class CorbaDataPointSetDConverter extends CorbaConverter {
    
    private static CorbaDataPointSetDConverter converter = null;
    
    /** Creates a new instance of CorbaHist1DAdapter */
    public static CorbaDataPointSetDConverter getInstance() {
        if (converter == null) converter = new CorbaDataPointSetDConverter();
        return converter;
    }
    
    private CorbaDataPointSetDConverter() {
        super();
        dataType = "DataPointSetData";
        aidaType = "IDataPointSet";
    }
    
    
    /**
     * Creates new instance of type "type".
     */
    public Object createAidaObject(String name) {
        RemoteDataPointSet result = new RemoteDataPointSet(name);        
        return result;
    }
    
    /**
     * Updates data contained by object.
     * Input can be Hist1DData or Any wrapped around Hist1DData.
     */
    public boolean updateAidaObject(Object aidaObject, Object newData) {
        DataPointSetData data = null;
        if (newData instanceof Any) {
            data = DataPointSetDataHelper.extract((Any) newData);
        } else if (newData instanceof DataPointSetData) {
            data = (DataPointSetData) newData;
        }

        if (!(aidaObject instanceof hep.aida.ref.remote.RemoteDataPointSet))
            throw new IllegalArgumentException("Not supported object type: "+aidaObject.getClass().getName());
        if (!(data instanceof hep.aida.ref.remote.corba.generated.DataPointSetData))
            throw new IllegalArgumentException("Not supported data type: "+(data == null ? "null" : newData.getClass().getName()));

        updateData((RemoteDataPointSet) aidaObject, data);
        return true;
    }
  
    /**
     * Returns CORBA Any object
     */
    public Object extractData(Object aidaObject) {
        if (!(aidaObject instanceof hep.aida.IDataPointSet))
            throw new IllegalArgumentException("Not supported data type: "+aidaObject.getClass().getName());

        DataPointSetData data = null;
        synchronized (aidaObject) {
            data = createData((hep.aida.IDataPointSet) aidaObject);
        }
        Any a = orb.create_any();
        System.out.println("TreeServantImpl.find INSERTING ANY");
        DataPointSetDataHelper.insert(a, data);

        return a;
    }
  
    
   /**
    * Update data in RemoteHistogram1d from IHistogram1D
    * and calls setDataValid(true) method.
    */ 
   public IManagedObject updateData(RemoteDataPointSet hist, DataPointSetData data)
   {
       hist.setFillable(true);
             
      // Check and set Annotation
      if (data.annotation != null && data.annotation.length > 0) {
          IAnnotation localAnnotation = hist.annotation();
          if (localAnnotation instanceof Annotation)  
              ((Annotation) localAnnotation).setFillable(true);
          for (int i=0; i<data.annotation.length; i++) {
              String key = data.annotation[i].key;
              String newValue = data.annotation[i].value;
              boolean sticky = data.annotation[i].sticky;
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
          if (localAnnotation instanceof Annotation)  
              ((Annotation) localAnnotation).setFillable(false);          
      }
      
      // Set DataPoints information
      // Put all data in one array: { Point_1_Coor_1, Point_1_Coord_2, ..., Point_2_Coord_1, ... }
      int nPoints = data.points.length;
      int dimension = data.dimension;
      double[] values      = null;
      double[] plusErrors  = null;
      double[] minusErrors = null;
      double[] upperExtent = null;
      double[] lowerExtent = null;

      if (nPoints > 0 && dimension > 0) {
          int n = nPoints*dimension;
          upperExtent = new double[dimension];
          lowerExtent = new double[dimension];
          for (int dim=0; dim<dimension; dim++) {
              upperExtent[dim] = Double.NaN;
              lowerExtent[dim] = Double.NaN;
          }
          values      = new double[n];
          plusErrors  = new double[n];
          minusErrors = new double[n];
          int i = 0;
          for (int index=0; index<nPoints; index++) { 
            for (int dim=0; dim<dimension; dim++) {
                i = index*dimension + dim;
                double v  = data.points[index].measurements[dim].value;
                double ep = data.points[index].measurements[dim].errorPlus;
                double em = data.points[index].measurements[dim].errorMinus;
                values[i]      = v;
                plusErrors[i]  = ep;
                minusErrors[i] = em;
                System.out.println(i+"\t  v="+v+",  ep="+ep+"  em="+em);
                double up = values[i]+plusErrors[i];
                if (Double.isNaN(upperExtent[dim]) || upperExtent[dim] < up) upperExtent[dim] = up;
                double down = values[i]-minusErrors[i];
                if (Double.isNaN(lowerExtent[dim]) || lowerExtent[dim] > down) lowerExtent[dim] = down;
            } // End loop over dimensions for one point
          } // End loop over Points
      }
      synchronized (hist) {
        hist.setValues(values);
        hist.setPlusErrors(plusErrors);
        hist.setMinusErrors(minusErrors);
        hist.setUpperExtent(upperExtent);
        hist.setLowerExtent(lowerExtent);
        hist.setDimension(dimension);
        
        hist.print();
        
        hist.setFillable(false);
        hist.setDataValid(true);
      }
      return hist;
   }
   
   /**
    * Create Hist1DData structure from an IHistogram1D
    */
    public DataPointSetData createData(IDataPointSet hist) {
        DataPointSetData data = new DataPointSetData();

        data.name = ((IManagedObject) hist).name();
        data.dimension = hist.dimension();
        String title = hist.title();
        title = (title == null || title.length() == 0) ? data.name : title;

        IAnnotation an = hist.annotation();
        if (an == null || an.size() == 0) data.annotation = new AnnotationItem[0];
        else {
            int size = an.size();
            data.annotation = new AnnotationItem[size];
            //System.out.println("Annotation Size = "+size);
            for (int i=0; i<size; i++) {
                data.annotation[i] = new AnnotationItem(an.key(i), an.value(i), false);
            }
        }

        int nPoints = hist.size();
        if (nPoints > 0 && data.dimension > 0) {
            data.points = new DataPointData[nPoints];
            for (int index=0; index<nPoints; index++) {
               data.points[index] = new DataPointData();
               data.points[index].dimension = data.dimension;
               data.points[index].measurements = new MeasurementData[data.dimension];
               IDataPoint point = hist.point(index);
               for (int dim=0; dim<data.dimension; dim++) {
                    IMeasurement m = point.coordinate(dim);
                    double v  = m.value();
                    double ep = m.errorPlus();
                    double em = m.errorMinus();
                    data.points[index].measurements[dim] = new MeasurementData(v, ep, em); 
               } // End loop over dimensions for one point
            } // End loop over Points
        } else {
            data.points = new DataPointData[0];
        }
        return data;
    }   
}
