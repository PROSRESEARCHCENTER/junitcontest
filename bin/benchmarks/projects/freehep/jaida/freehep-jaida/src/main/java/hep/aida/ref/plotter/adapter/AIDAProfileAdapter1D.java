package hep.aida.ref.plotter.adapter;

import hep.aida.IProfile1D;
import jas.hist.DataSource;
import jas.hist.HasStatistics;
import jas.hist.Rebinnable1DHistogramData;
import jas.hist.Statistics;

/**
 * @author manj
 * @version $Id: AIDAProfileAdapter1D.java 13788 2010-11-24 19:43:56Z turri $
 */
public class AIDAProfileAdapter1D extends AIDAProfileAdapter implements Rebinnable1DHistogramData, HasStatistics, AIDA1DAdapterWithAxisLabels
{

    String[] axisLabels = null;

    AIDAProfileAdapter1D(IProfile1D profile)
   {
      super(profile);
      this.profile=profile;
      String tmp = null;
       try {
            tmp = profile.annotation().value("xAxisType");
            if (tmp != null && tmp.equalsIgnoreCase("date")) xAxisType = DataSource.DATE;
        } catch (IllegalArgumentException e) {}
  }  
   public double[][] rebin(int bins, double min, double max, boolean wantErrors, boolean hurry)
   {
      setValid();
      double[][] data=new double[2][profile.axis().bins()];
      for(int i=0;i<profile.axis().bins();i++)
      {
         data[0][i]=profile.binHeight(i);
         if (Double.isInfinite(data[0][i])) data[0][i] = Double.NaN;
         if ( errorMode() == USE_ERROR_ON_MEAN ) data[1][i]=profile.binError(i);
         else data[1][i]=profile.binRms(i);
      }
      return data;
   }
   /**
    * Returns the (suggested) minimum value for the X axis
    */
   public double getMin()
   {
      return profile.axis().lowerEdge();
   }
   public double getMax()
   {
      return profile.axis().upperEdge();
   }
   public int getBins()
   {
      return profile.axis().bins();
   }
   public boolean isRebinnable()
   {
      return false;
   }
   public int getAxisType()
   {
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
      return profile.title();
   }
   public Statistics getStatistics()
   {
      return new AIDAProfileStatistics1D(profile);  
   }
   
   protected IProfile1D profile;
}
