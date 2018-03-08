package jas.hist;

import jas.plot.DataArea;
import jas.plot.DoubleAxis;
import jas.plot.Legend;

import java.io.IOException;
import java.io.ObjectInputStream;

class DoubleDataManager extends BinnedDataManager
{
	DoubleDataManager(JASHist plot, DataArea da, Legend l, StatisticsBlock stats, int bins)
	{
		super(plot,da,l,stats,bins);
		
		// Configure the Axes

		xAxis = new DoubleAxis();
		DoubleAxis yAxis = new DoubleAxis();
		yAxis.setUseSuggestedRange(true);

		xm.setDataManager(this,true, xAxis);
		ym[0].setDataManager(this,false,yAxis);

		new DoubleAxisListener(xm);

		xm.setBins(bins);
	}
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		in.defaultReadObject();
		new DoubleAxisListener(xm);
	}
	final JASHistData add(DataSource data)
	{
      if (data instanceof Rebinnable1DHistogramData)
      {
         Rebinnable1DHistogramData d = (Rebinnable1DHistogramData) data;
         // We only support adding items with continuous axes
         if (d.getAxisType() != d.DOUBLE) throw new DataManagerException("Incompatible data type for axis");
      }
      else
      {
         XYDataSource d = (XYDataSource) data;
         if (d.getAxisType() != d.DOUBLE) throw new DataManagerException("Incompatible data type for axis");
      }
      return super.add(data);
	}
	final protected void calcMinMaxBins(double x1, double x2) 
	{
		double oldXMin = xAxis.getPlotMin();
		double oldXMax = xAxis.getPlotMax();
		if (x1 != oldXMin || x2 != oldXMax)
		{
			xAxis.setMin(x1);
 			xAxis.setMax(x2);
			xm.invalidate();
		}
	}
	private DoubleAxis xAxis;
}
