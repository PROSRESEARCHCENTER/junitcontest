/*
 * AidaCloudAdapter2D.java
 *
 * Created on March 8, 2001, 11:08 AM
 */

package hep.aida.ref.plotter.adapter;

import hep.aida.ICloud2D;
import hep.aida.IHistogram2D;
import hep.aida.ref.histogram.Cloud2D;
import hep.aida.ref.histogram.HistUtils;
import jas.hist.DataSource;
import jas.hist.HasScatterPlotData;
import jas.hist.HasStatistics;
import jas.hist.HasStyle;
import jas.hist.JASHistScatterPlotStyle;
import jas.hist.JASHistStyle;
import jas.hist.Rebinnable2DHistogramData;
import jas.hist.ScatterEnumeration;
import jas.hist.Statistics;

/**
 *
 * @author manj
 * @version $Id: AIDACloudAdapter2D.java 13402 2007-11-02 21:19:21Z serbo $
 */
public class AIDACloudAdapter2D extends AIDACloudAdapter implements Rebinnable2DHistogramData, HasScatterPlotData, HasStatistics, HasStyle
{
   public AIDACloudAdapter2D(ICloud2D cloud)
   {
      super(cloud);
      this.cloud=cloud;
      xBins=50;
      yBins=50;
      String tmp = null;
        try {
            tmp = cloud.annotation().value("xAxisType");
            if (tmp != null && tmp.equalsIgnoreCase("date")) xAxisType = DataSource.DATE;
        } catch (IllegalArgumentException e) {}
        try {
            tmp = cloud.annotation().value("yAxisType");
            if (tmp != null && tmp.equalsIgnoreCase("date")) xAxisType = DataSource.DATE;
        } catch (IllegalArgumentException e) {}

      style = new JASHistScatterPlotStyle();
   }
   public double[][][] rebin(int xbins, double xmin, double xmax,
   int ybins, double ymin, double ymax,
   boolean wantErrors, boolean hurry, boolean overflow)
   {
      setValid();
      IHistogram2D histo;
      if(isRebinnable()) histo=HistUtils.toShowableHistogram(cloud,xbins,xmin,xmax,ybins,ymin,ymax);
      else               histo=cloud.histogram();
      
      double[][][] data=new double [2][histo.xAxis().bins()][histo.yAxis().bins()];
      for(int i=0;i<histo.xAxis().bins();i++)
         for(int j=0;j<histo.yAxis().bins();j++)
         {
            data[0][i][j]=histo.binHeight(i,j);
            if (Double.isInfinite(data[0][i][j])) data[0][i][j] = Double.NaN;
            data[1][i][j]=histo.binError(i,j);
         }
      return data;
   }
   public double getXMin()
   {
       if ( isRebinnable() ) {
           if ( cloud instanceof Cloud2D )
               return ((Cloud2D)cloud).lowerEdgeXWithMargin();
           else {
               double edge= cloud.lowerEdgeX();
               if ( Double.isNaN(edge) ) return Double.NaN;
               return edge - getMarginValue(edge, cloud.upperEdgeX());
           }
       }
       else
           return cloud.histogram().xAxis().lowerEdge();
   }
   public double getXMax()
   {
       if ( isRebinnable() ) {
           if ( cloud instanceof Cloud2D )
               return ((Cloud2D)cloud).upperEdgeXWithMargin();
           else {
               double edge= cloud.upperEdgeX();
               if ( Double.isNaN(edge) ) return Double.NaN;
               return edge + getMarginValue(cloud.lowerEdgeX(), edge);
           }
       }
       else
           return cloud.histogram().xAxis().upperEdge();
   }
   public double getYMin()
   {
       if ( isRebinnable() ) {
           if ( cloud instanceof Cloud2D )
               return ((Cloud2D)cloud).lowerEdgeYWithMargin();
           else {
               double edge= cloud.lowerEdgeY();
               if ( Double.isNaN(edge) ) return Double.NaN;
               return edge - getMarginValue(edge, cloud.upperEdgeY());
           }
       }
       else
           return cloud.histogram().yAxis().lowerEdge();
   }
   public double getYMax()
   {
       if ( isRebinnable() ) {
           if ( cloud instanceof Cloud2D )
               return ((Cloud2D)cloud).upperEdgeYWithMargin();
           else {
               double edge= cloud.upperEdgeY();
               if ( Double.isNaN(edge) ) return Double.NaN;
               return edge + getMarginValue(cloud.lowerEdgeY(), edge);
           }
       }
       else
           return cloud.histogram().yAxis().upperEdge();
   }
   public int getXBins()
   {
      return isRebinnable() ? xBins : cloud.histogram().xAxis().bins();
   }
   public int getYBins()
   {
      return isRebinnable() ? yBins : cloud.histogram().yAxis().bins();
   }
   public boolean isRebinnable()
   {
      return (!cloud.isConverted());
   }
   public int getXAxisType()
   {
      return xAxisType;
   }
   public int getYAxisType()
   {
      return yAxisType;
   }
    public void setXAxisType(int type) {
        xAxisType = type;
    }   
   
    public void setYAxisType(int type) {
        yAxisType = type;
    }   
   
   public String[] getXAxisLabels()
   {
      return null;
   }
   public String[] getYAxisLabels()
   {
      return null;
   }
   public String getTitle()
   {
      return cloud.title();
   }
   public Statistics getStatistics()
   {
      return new AIDACloudStatistics2D(cloud);
   }
   public boolean hasScatterPlotData()
   {
      return isRebinnable();
   }
   public ScatterEnumeration startEnumeration()
   {
      setValid();
      return new CloudEnumeration();
   }
   public ScatterEnumeration startEnumeration(double xMin, double xMax, double yMin, double yMax)
   {
      setValid();
      return new CloudEnumeration();
   }
   private class CloudEnumeration implements ScatterEnumeration
   {
      private int index = 0;
      public boolean getNextPoint(double[] values)
      {
         if (index < cloud.entries())
         {
            try
            {
               values[0] = cloud.valueX(index);
               values[1] = cloud.valueY(index);
               index++;
               return true;
            }
            catch (RuntimeException x) // cloud probably converted 
            {
               style.setDisplayAsScatterPlot(false);
               return false;
            }
         }
         else return false;
      }
      public void restart()
      {
         setValid();
         index = 0;
      }
      public void resetEndPoint()
      {
      }  
   }
   public JASHistStyle getStyle()
   {
      style.setDisplayAsScatterPlot(isRebinnable());
      return style;
   }
   private JASHistScatterPlotStyle style;
   private ICloud2D cloud;
   private int xBins,yBins;

}