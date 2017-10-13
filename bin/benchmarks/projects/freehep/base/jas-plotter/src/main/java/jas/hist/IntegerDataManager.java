package jas.hist;

import jas.plot.DataArea;
import jas.plot.DoubleAxis;
import jas.plot.Legend;

final class IntegerDataManager extends BinnedDataManager
{
	IntegerDataManager(JASHist plot, DataArea da, Legend l, StatisticsBlock stats, int bins)
	{
		super(plot, da,l,stats,bins);
		
		// Configure the Axes

		xAxis = new DoubleAxis();
		DoubleAxis yAxis = new DoubleAxis();
		yAxis.setUseSuggestedRange(true);

		xm.setDataManager(this,true, xAxis);
		ym[0].setDataManager(this,false,yAxis);

		new DoubleAxisListener(xm);

		xm.setBins(bins);
		targetBins = bins;
	}
	JASHistData add(DataSource data)
	{
      if (data instanceof Rebinnable1DHistogramData)
      {
         Rebinnable1DHistogramData d = (Rebinnable1DHistogramData) data;
         // We only support adding items with integer axes
         if (d.getAxisType() != d.INTEGER) throw new DataManagerException("Incompatible data type for axis"); 
      }
      else
      {
         XYDataSource d = (XYDataSource) data;
         // We only support adding items with integer axes
         if (d.getAxisType() != d.INTEGER) throw new DataManagerException("Incompatible data type for axis"); 
      }
		return super.add(data);
	}
	protected void calcMinMaxBins(double x1, double x2)
	{
		int binWidth = (int) (1 + (x2-x1)/targetBins);
		int bins = (int) (1+(x2-x1)/binWidth);

		xLow = x1 - 0.5;
                double product = (double)binWidth*(double)bins; // See JAS-170 
		xHigh = xLow + product;

		double oldXMin = xAxis.getPlotMin();
		double oldXMax = xAxis.getPlotMax();
		if (xLow != oldXMin || xHigh != oldXMax)
		{
			xm.setBins(bins);
			xAxis.setMin(xLow);
 			xAxis.setMax(xHigh);
			xAxis.getAxis().invalidate();
		}
	}
	private DoubleAxis xAxis;
	private int targetBins;
}
