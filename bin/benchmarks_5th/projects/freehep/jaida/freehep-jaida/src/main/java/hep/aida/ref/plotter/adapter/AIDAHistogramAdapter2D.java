package hep.aida.ref.plotter.adapter;
import hep.aida.IHistogram2D;
import jas.hist.DataSource;
import jas.hist.HasStatistics;
import jas.hist.Rebinnable2DHistogramData;
import jas.hist.Statistics;

/**
 *
 * @author  manj
 * @version $Id: AIDAHistogramAdapter2D.java 10738 2007-05-16 22:47:34Z serbo $
 */
public class AIDAHistogramAdapter2D extends AIDAHistogramAdapter implements Rebinnable2DHistogramData, HasStatistics
{
   AIDAHistogramAdapter2D(IHistogram2D histo)
   {
      super(histo);
      this.h2d=histo;
      String tmp = null;
        try {
            tmp = h2d.annotation().value("xAxisType");
            if (tmp != null && tmp.equalsIgnoreCase("date")) xAxisType = DataSource.DATE;
        } catch (IllegalArgumentException e) {}
        try {
            tmp = h2d.annotation().value("yAxisType");
            if (tmp != null && tmp.equalsIgnoreCase("date")) xAxisType = DataSource.DATE;
        } catch (IllegalArgumentException e) {}
   }
   public double[][][] rebin(int xbins, double xmin, double xmax,
                             int ybins, double ymin, double ymax,
                             boolean wantErrors, boolean hurry, boolean overflow)
   {
      double[][][] data=new double [2][h2d.xAxis().bins()][h2d.yAxis().bins()];
      for(int i=0;i<h2d.xAxis().bins();i++)
         for(int j=0;j<h2d.yAxis().bins();j++)
         {
            data[0][i][j]=h2d.binHeight(i,j);
            if (Double.isInfinite(data[0][i][j])) data[0][i][j] = Double.NaN;
            data[1][i][j]=h2d.binError(i,j);
         }
      setValid();
      return data;
   }
   public double getXMin()
   {
      return h2d.xAxis().lowerEdge();
   }
   public double getXMax()
   {
      return h2d.xAxis().upperEdge();
   }
   public double getYMin()
   {
      return h2d.yAxis().lowerEdge();
   }
   public double getYMax()
   {
      return h2d.yAxis().upperEdge();
   }
   public int getXBins()
   {
      return h2d.xAxis().bins();
   }
   public int getYBins()
   {
      return h2d.yAxis().bins();
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
      return h2d.title();
   }
   public Statistics getStatistics()
   {
      return new AIDAHistogramStatistics2D(h2d);
   }
   protected IHistogram2D h2d;
}