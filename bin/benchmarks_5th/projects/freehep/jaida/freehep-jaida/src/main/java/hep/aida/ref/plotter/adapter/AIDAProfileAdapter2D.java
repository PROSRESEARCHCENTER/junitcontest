package hep.aida.ref.plotter.adapter;
import hep.aida.IProfile2D;
import jas.hist.DataSource;
import jas.hist.HasStatistics;
import jas.hist.Rebinnable2DHistogramData;
import jas.hist.Statistics;

/**
 *
 * @author  manj
 * @version $Id: AIDAProfileAdapter2D.java 10740 2007-05-21 18:05:50Z serbo $
 */
class AIDAProfileAdapter2D extends AIDAProfileAdapter implements Rebinnable2DHistogramData, HasStatistics
{
    AIDAProfileAdapter2D(IProfile2D profile)
   {
      super(profile);
      this.profile=profile;
      String tmp = null;
        try {
            tmp = profile.annotation().value("xAxisType");
            if (tmp != null && tmp.equalsIgnoreCase("date")) xAxisType = DataSource.DATE;
        } catch (IllegalArgumentException e) {}
        try {
            tmp = profile.annotation().value("yAxisType");
            if (tmp != null && tmp.equalsIgnoreCase("date")) xAxisType = DataSource.DATE;
        } catch (IllegalArgumentException e) {}
   }
   public double[][][] rebin(int xbins, double xmin, double xmax,
                             int ybins, double ymin, double ymax,
                             boolean wantErrors, boolean hurry, boolean overflow)
   {
      setValid();
      double[][][] data=new double [2][profile.xAxis().bins()][profile.yAxis().bins()];
      for(int i=0;i<profile.xAxis().bins();i++)
         for(int j=0;j<profile.yAxis().bins();j++)
         {
            data[0][i][j]=profile.binHeight(i,j);
            if (Double.isInfinite(data[0][i][j])) data[0][i][j] = Double.NaN;
            if ( errorMode() == USE_ERROR_ON_MEAN ) data[1][i][j]=profile.binError(i,j);
            else data[1][i][j]=profile.binRms(i,j);
         }
      return data;
   }
   public double getXMin()
   {
      return profile.xAxis().lowerEdge();
   }
   public double getXMax()
   {
      return profile.xAxis().upperEdge();
   }
   public double getYMin()
   {
      return profile.yAxis().lowerEdge();
   }
   public double getYMax()
   {
      return profile.yAxis().upperEdge();
   }
   public int getXBins()
   {
      return profile.xAxis().bins();
   }
   public int getYBins()
   {
      return profile.yAxis().bins();
   }
   public boolean isRebinnable()
   {
      return false;
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
      return profile.title();
   }
   public Statistics getStatistics()
   {
      return new AIDAProfileStatistics2D(profile);
   }
   
   protected IProfile2D profile;
}