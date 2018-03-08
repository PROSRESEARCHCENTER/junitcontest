package hep.aida.ref.plotter.adapter;

import hep.aida.IHistogram1D;
import jas.hist.DataSource;
import jas.hist.HasStatistics;
import jas.hist.Rebinnable1DHistogramData;
import jas.hist.Statistics;

/**
 * @author manj
 * @version $Id: AIDAHistogramAdapter1D.java 13788 2010-11-24 19:43:56Z turri $
 */
public class AIDAHistogramAdapter1D extends AIDAHistogramAdapter implements Rebinnable1DHistogramData, HasStatistics, AIDA1DAdapterWithAxisLabels
{
    String[] axisLabels = null;
    
    AIDAHistogramAdapter1D(IHistogram1D histo)
   {
      super(histo);
      this.h1d=histo;
      String tmp = null;
       try {
            tmp = histo.annotation().value("xAxisType");
            if (tmp != null && tmp.equalsIgnoreCase("date")) xAxisType = DataSource.DATE;
        } catch (IllegalArgumentException e) {}
   }  
   public double[][] rebin(int bins, double min, double max, boolean wantErrors, boolean hurry)
   {
      double[][] data=new double[2][h1d.axis().bins()];
      for(int i=0;i<h1d.axis().bins();i++)
      {
         data[0][i]=h1d.binHeight(i);
         if (Double.isInfinite(data[0][i])) data[0][i] = Double.NaN;
         data[1][i]=h1d.binError(i);
      }
      setValid();
      return data;
   }
   /**
    * Returns the (suggested) minimum value for the X axis
    */
   public double getMin()
   {
      return h1d.axis().lowerEdge();
   }
   public double getMax()
   {
      return h1d.axis().upperEdge();
   }
   public int getBins()
   {
      return h1d.axis().bins();
   }
   public boolean isRebinnable()
   {
      return false;
   }
    
    public int getAxisType() {
        return xAxisType;
    }
    
    public void setAxisType(int type) {
        xAxisType = type;
    }   
   
   public String[] getAxisLabels()
   {
      return axisLabels;
   }

   public void setAxisLabels(String[] axisLabels) {
       this.axisLabels = axisLabels;
   }
   public String getTitle()
   {
      return h1d.title();
   }
   public Statistics getStatistics()
   {
      return new AIDAHistogramStatistics1D(h1d);  
   }
   protected IHistogram1D h1d;
}
