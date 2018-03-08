package hep.aida.ref.plotter.adapter;

import hep.aida.ICloud1D;
import hep.aida.IHistogram1D;
import hep.aida.ref.histogram.Cloud1D;
import hep.aida.ref.histogram.HistUtils;
import jas.hist.DataSource;
import jas.hist.HasStatistics;
import jas.hist.Rebinnable1DHistogramData;
import jas.hist.Statistics;


/**
 *
 * @author  manj
 * @version $Id: AIDACloudAdapter1D.java 13422 2007-12-04 17:20:07Z serbo $
 */
public class AIDACloudAdapter1D extends AIDACloudAdapter implements Rebinnable1DHistogramData, HasStatistics
{
   public AIDACloudAdapter1D(ICloud1D cloud)
   {
      super(cloud);
      this.cloud=cloud;
      String tmp = null;
        try {
            tmp = cloud.annotation().value("xAxisType");
            if (tmp != null && tmp.equalsIgnoreCase("date")) xAxisType = DataSource.DATE;
        } catch (IllegalArgumentException e) {}
   }
   public double[][] rebin(int bins, double min, double max, boolean wantErrors, boolean hurry)
   {
      IHistogram1D histo = null;
      setValid();
      if(isRebinnable()) { 
          histo=HistUtils.toShowableHistogram(cloud,bins,min,max);
          nBins = bins;
      } else               histo=cloud.histogram();
      
      double[][] data=new double [2][histo.axis().bins()];
      for(int i=0;i<histo.axis().bins();i++)
      {
         data[0][i]=histo.binHeight(i);
         if (Double.isInfinite(data[0][i])) data[0][i] = Double.NaN;
         data[1][i]=histo.binError(i);
      }
      return data;
   }
   public double getMin()
   {
       if ( isRebinnable() ) {
           if ( cloud instanceof Cloud1D ) 
               return ((Cloud1D)cloud).lowerEdgeWithMargin();
           else {
               double edge= cloud.lowerEdge();
               if ( Double.isNaN(edge) ) return Double.NaN;
               return edge - getMarginValue(edge, cloud.upperEdge());
           }
       }
       return cloud.histogram().axis().lowerEdge();
   }
   public double getMax()
   {
       if ( isRebinnable() ) {
           if ( cloud instanceof Cloud1D ) 
               return ((Cloud1D)cloud).upperEdgeWithMargin();
           else {
               double edge= cloud.upperEdge();
               if ( Double.isNaN(edge) ) return Double.NaN;
               return edge + getMarginValue(cloud.lowerEdge(), edge);
           }
       }
       return cloud.histogram().axis().upperEdge();
   }
   public int getBins()
   {
      return isRebinnable() ?  nBins : cloud.histogram().axis().bins();
   }
   public boolean isRebinnable()
   {
      return (!cloud.isConverted());
   }

    public int getAxisType() {
        return xAxisType;
    }   
   
    public void setAxisType(int type) {
        xAxisType = type;
    }   
   
   public String[] getAxisLabels()
   {
      return null;
   }
   
   public String getTitle()
   {
      return cloud.title();
   }
   public Statistics getStatistics()
   {
      return new AIDACloudStatistics1D(cloud);
   }
   private ICloud1D cloud;
   private int nBins = 50;
}


