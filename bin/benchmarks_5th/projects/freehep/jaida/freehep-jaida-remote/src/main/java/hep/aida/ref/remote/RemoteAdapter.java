/*
 * BasicConvertor.java
 *
 * Created on May 25, 2003, 5:22 PM
 */

package hep.aida.ref.remote;

import hep.aida.IAxis;
import hep.aida.IHistogram1D;
import hep.aida.IManagedObject;
import hep.aida.dev.IDevMutableStore;
import hep.aida.ref.histogram.Histogram1D;
import hep.aida.ref.histogram.Histogram2D;

/**
 * Class that performs data updates for hep.aida.ref Histograms for
 * another IHistogram.
 * @author  serbo
 */
public class RemoteAdapter {
    
    /** Creates a new instance of BasicConvertor */
    public RemoteAdapter() {
    }
    
   /**
    * Update data in RemoteHistogram1d from IHistogram1D
    */ 
   private static IManagedObject updateData(RemoteHistogram1D hist, IHistogram1D h1)
   {
      // Check if binning or edges are different
      IAxis lAxis = hist.axis();      
      IAxis rAxis = h1.axis();  
      int nBins = rAxis.bins();
      IAxis newAxis = null;
      if (lAxis == null || lAxis.bins() != nBins || 
          lAxis.lowerEdge() != rAxis.lowerEdge() || lAxis.upperEdge() != rAxis.upperEdge()) {
          hist.setAxis(nBins, rAxis.lowerEdge(), rAxis.upperEdge()); 
      }
      
      String title = h1.title();
      if (!title.equals(hist.title())) hist.setTitle(title);
      
      int[] entries = new int[nBins+2];
      double[] heights = new double[nBins+2];
      double[] errors = new double[nBins+2];
      double[] means = new double[nBins+2];
      double[] rmss = null;
      if (h1 instanceof Histogram1D) rmss = new double[nBins+2];
      for (int i=IAxis.UNDERFLOW_BIN; i<nBins-1; i++) {
        heights[i+2] = h1.binHeight(i);
        errors[i+2]  = h1.binError(i);
        entries[i+2] = h1.binEntries(i);
        means[i+2]   = h1.binMean(i);
        if (h1 instanceof Histogram1D) rmss[i+2] = ((Histogram1D) h1).binRms(i);
      }
      synchronized (hist) {
        hist.setHeights(heights);
        hist.setErrors(errors);
        hist.setEntries(entries);
        hist.setMeans(means);
        hist.setRmss(rmss);
        hist.setMean(h1.mean());
        hist.setRms(h1.rms());
        hist.setDataValid(true);
      }
      return hist;
   }

   static IManagedObject update(Object h, Object o)
   {
      if (h instanceof RemoteHistogram1D && o instanceof IHistogram1D)  
          return updateData((RemoteHistogram1D) h, (IHistogram1D) o);
      
      else return null;
   }  
   
   static IManagedObject create(IDevMutableStore store, String name, String type)
   {
       if (type.equals("IHistogram1D")) return new RemoteHistogram1D(store, name);
       if (type.equals("IHistogram2D")) return new Histogram2D(name, name, null, null);
       else return null;
   }
}
