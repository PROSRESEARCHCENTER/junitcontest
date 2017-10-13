/*
 * CorbaHist1DAdapter.java
 *
 * Created on June 12, 2003, 5:54 PM
 */

package hep.aida.ref.remote.corba.converters;

//import hep.aida.dev.IConverter;
import hep.aida.IAnnotation;
import hep.aida.IAxis;
import hep.aida.IHistogram1D;
import hep.aida.IManagedObject;
import hep.aida.ref.Annotation;
import hep.aida.ref.remote.RemoteHistogram1D;
import hep.aida.ref.remote.corba.generated.AnnotationItem;
import hep.aida.ref.remote.corba.generated.Axis;
import hep.aida.ref.remote.corba.generated.Bin1D;
import hep.aida.ref.remote.corba.generated.Hist1DData;
import hep.aida.ref.remote.corba.generated.Hist1DDataHelper;
import hep.aida.ref.remote.corba.generated.HistInfo1D;
import hep.aida.ref.remote.corba.generated.Statistics;

import org.omg.CORBA.Any;

/**
 * Converts Hist1DData to RemoteHistogram1D
 * @author  serbo
 */
public final class CorbaHist1DConverter extends CorbaConverter {
    
    private static CorbaHist1DConverter converter = null;
    
    /** Creates a new instance of CorbaHist1DAdapter */
    public static CorbaHist1DConverter getInstance() {
        if (converter == null) converter = new CorbaHist1DConverter();
        return converter;
    }
    
    private CorbaHist1DConverter() {
        super();
        dataType = "Hist1DData";
        aidaType = "IHistogram1D";
    }
    
    
    /**
     * Creates new instance of type "type".
     */
    public Object createAidaObject(String name) {
        RemoteHistogram1D result = new RemoteHistogram1D(name);        
        return result;
    }
    
    /**
     * Updates data contained by object.
     * Input can be Hist1DData or Any wrapped around Hist1DData.
     */
    public boolean updateAidaObject(Object aidaObject, Object newData) {
        Hist1DData data = null;
        if (newData instanceof Any) {
            data = Hist1DDataHelper.extract((Any) newData);
        } else if (newData instanceof Hist1DData) {
            data = (Hist1DData) newData;
        }

        if (!(aidaObject instanceof hep.aida.ref.remote.RemoteHistogram1D))
            throw new IllegalArgumentException("Not supported object type: "+aidaObject.getClass().getName());
        if (!(data instanceof hep.aida.ref.remote.corba.generated.Hist1DData))
            throw new IllegalArgumentException("Not supported data type: "+(data == null ? "null" : newData.getClass().getName()));

        updateData((RemoteHistogram1D) aidaObject, data);
        return true;
    }
  
    /**
     * Returns CORBA Any object
     */
    public Object extractData(Object aidaObject) {
        if (!(aidaObject instanceof hep.aida.IHistogram1D))
            throw new IllegalArgumentException("Not supported data type: "+aidaObject.getClass().getName());

        Hist1DData data = null;
        synchronized (aidaObject) {
            data = createData((hep.aida.IHistogram1D) aidaObject);
        }
        Any a = orb.create_any();
        System.out.println("TreeServantImpl.find INSERTING ANY");
        Hist1DDataHelper.insert(a, data);

        return a;
    }
  
    
   /**
    * Update data in RemoteHistogram1d from IHistogram1D
    * and calls setDataValid(true) method.
    */ 
   public IManagedObject updateData(RemoteHistogram1D hist, Hist1DData data)
   {
       hist.setFillable(true);
       
      // Check if X axis binning or edges are different
      IAxis lAxis = hist.axis();      
      Axis rAxis = data.axis;  
      int nBins = data.bins.length;
      IAxis newAxis = null;
      if (lAxis == null || lAxis.bins() != nBins || 
          lAxis.lowerEdge() != rAxis.min || lAxis.upperEdge() != rAxis.max) {
          hist.setAxis(nBins, rAxis.min, rAxis.max); 
      }
      
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
      
      // Set bin information
      int[] entries    = null;
      double[] heights = null;
      double[] errors  = null;
      double[] means   = null;
      double[] rmss    = null;
      if (nBins > 0) {
        entries    = new int[nBins+2];
        heights = new double[nBins+2];
        errors  = new double[nBins+2];
        means   = new double[nBins+2];
        rmss    = new double[nBins+2];
        int i = 0;
        for (i=0; i<nBins; i++) {
            heights[i+1] = data.bins[i].height;
            errors[i+1]  = data.bins[i].error;
            entries[i+1] = data.bins[i].entries;
            means[i+1]   = data.bins[i].weightedMean;
            rmss[i+1]    = data.bins[i].weightedRms;
        }
        
        // UNDERFLOW_BIN
        i = 0;
        heights[i] = data.underflowBin.height;
        errors[i]  = data.underflowBin.error;
        entries[i] = data.underflowBin.entries;
        means[i]   = data.underflowBin.weightedMean;
        rmss[i]    = data.underflowBin.weightedRms;
      
        // OVERFLOW_BIN
        i = nBins+1;
        heights[i] = data.overflowBin.height;
        errors[i]  = data.overflowBin.error;
        entries[i] = data.overflowBin.entries;
        means[i]   = data.overflowBin.weightedMean;
        rmss[i]    = data.overflowBin.weightedRms;
      }
      synchronized (hist) {
        hist.setHeights(heights);
        hist.setErrors(errors);
        hist.setEntries(entries);
        hist.setMeans(means);
        hist.setRmss(rmss);
        hist.setMean(data.statistics.mean);
        hist.setRms(data.statistics.rms);
        hist.setFillable(false);
        hist.setDataValid(true);
      }
      return hist;
   }
   
   /**
    * Create Hist1DData structure from an IHistogram1D
    */
    public Hist1DData createData(IHistogram1D hist) {
        Hist1DData data = new Hist1DData();

        data.name = ((IManagedObject) hist).name();
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

        IAxis axis = hist.axis();
        data.axis = new Axis();
        data.axis.direction = "x";
        data.axis.min = axis.lowerEdge();
        data.axis.max = axis.upperEdge();
        data.axis.nBins = axis.bins();

        data.statistics = new Statistics("x", hist.mean(), hist.rms(), false);

        int o = IAxis.OVERFLOW_BIN;
        data.overflowBin = new Bin1D();
        data.overflowBin.binNum = o;
        data.overflowBin.weightedMean = hist.binMean(o);
        data.overflowBin.height = hist.binHeight(o);
        data.overflowBin.error = hist.binError(o);
        data.overflowBin.entries = hist.binEntries(o);
        data.overflowBin.error2 = 0;
        if (hist instanceof hep.aida.ref.histogram.Histogram1D) {
            data.overflowBin.rms = ((hep.aida.ref.histogram.Histogram1D) hist).binRms(o);
            data.overflowBin.weightedRms = ((hep.aida.ref.histogram.Histogram1D) hist).binRms(o);
        }

        data.underflowBin = new Bin1D();
        int u = IAxis.UNDERFLOW_BIN;
        data.underflowBin = new Bin1D();
        data.underflowBin.binNum = u;
        data.underflowBin.weightedMean = hist.binMean(u);
        data.underflowBin.height = hist.binHeight(u);
        data.underflowBin.error = hist.binError(u);
        data.underflowBin.entries = hist.binEntries(u);
        data.underflowBin.error2 = 0;
        if (hist instanceof hep.aida.ref.histogram.Histogram1D) {
            data.underflowBin.rms = ((hep.aida.ref.histogram.Histogram1D) hist).binRms(u);
            data.underflowBin.weightedRms = ((hep.aida.ref.histogram.Histogram1D) hist).binRms(u);
        }


        int nBins = hist.axis().bins();
        data.bins = new Bin1D[nBins];
        for (int i=0; i<nBins; i++) {
            data.bins[i] = new Bin1D();
            data.bins[i].binNum = i;
            data.bins[i].weightedMean = hist.binMean(i);
            data.bins[i].height = hist.binHeight(i);
            data.bins[i].error = hist.binError(i);
            data.bins[i].entries = hist.binEntries(i);

            if (hist instanceof hep.aida.ref.histogram.Histogram1D) {
                data.bins[i].rms = ((hep.aida.ref.histogram.Histogram1D) hist).binRms(i);
                data.bins[i].weightedRms = ((hep.aida.ref.histogram.Histogram1D) hist).binRms(i);
            }
            data.bins[i].error2 = 0;
        }
        data.info = new HistInfo1D();
        data.info.entries = hist.entries();
        data.info.allEntries = hist.allEntries();
        data.info.equivalentBinEntries = hist.equivalentBinEntries();
        data.info.maxBinHeight = hist.maxBinHeight();
        data.info.minBinHeight = hist.minBinHeight();
        data.info.sumBinHeights = hist.sumBinHeights();
        data.info.sumAllBinHeights =hist.sumAllBinHeights();

        return data;
    }   
}
