package jas.hist;

import jas.plot.DataArea;
import jas.plot.DateAxis;
import jas.plot.DoubleAxis;
import jas.plot.Legend;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.TimeZone;

final class DateDataManager extends BinnedDataManager
{
   DateDataManager(JASHist plot, DataArea da, Legend l, StatisticsBlock stats, int bins)
   {
      super(plot, da,l,stats,bins);
      
      // Configure the Axes
      
      xAxis = new DateAxis();
      
      DoubleAxis yAxis = new DoubleAxis();
      yAxis.setUseSuggestedRange(true);
      
      xm.setDataManager(this,true, xAxis);
      ym[0].setDataManager(this,false,yAxis);
      
      new DateAxisListener(xm);
      xm.setBins(bins);
      //createYAxis(1); // todo: something better
   }
   JASHistData add(DataSource data)
   {
      if (data instanceof Rebinnable1DHistogramData)
      {
         Rebinnable1DHistogramData d = (Rebinnable1DHistogramData) data;
         // We only support adding items with date axis
         if (d.getAxisType() != d.DATE) throw new DataManagerException("Incompatible data type for axis");
      }
      else
      {
         XYDataSource d = (XYDataSource) data;
         // We only support adding items with date axis
         if (d.getAxisType() != d.DATE) throw new DataManagerException("Incompatible data type for axis");
      }
      
      JASHistData jhd = super.add(data);
      TimeZone tz = jhd.getStyle().getTimeZone();
      if (tz != null) xAxis.setTimeZone(tz);
      return jhd;
   }
   
    void styleUpdate(JASHistData data) 
    {
      TimeZone tz = data.getStyle().getTimeZone();
      if (tz != null) xAxis.setTimeZone(tz);
      super.styleUpdate(data);
    }
   
   private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
   {
      in.defaultReadObject();
      new DateAxisListener(xm);
   }
   protected void calcMinMaxBins(double x1, double x2)
   {
      long iLow = (long) (x1*1000);
      long iHigh = (long) (x2*1000);
      long oldXMin = xAxis.getAxisMin();
      long oldXMax = xAxis.getAxisMax();
      if (iLow != oldXMin || iHigh != oldXMax)
      {
         xAxis.setMin(iLow);
         xAxis.setMax(iHigh);
         xAxis.getAxis().invalidate();
      }
   }
   private DateAxis xAxis;
}
