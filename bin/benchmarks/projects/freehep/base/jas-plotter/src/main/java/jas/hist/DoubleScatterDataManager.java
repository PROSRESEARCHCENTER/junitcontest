package jas.hist;

import jas.plot.DataArea;
import jas.plot.DoubleAxis;
import jas.plot.Legend;

final class DoubleScatterDataManager extends ScatterDataManager
{
	DoubleScatterDataManager(JASHist plot, final DataArea da, final Legend l, StatisticsBlock stats)
	{
		super(plot, da, l, stats);

		// Configure the Axes

		if (xm.getType() instanceof DoubleAxis)
			xAxisType = (DoubleAxis) xm.getType();
		else
		{
			xAxisType = new DoubleAxis();
			xm.setType(xAxisType);
		}

		DoubleAxis yAxisType;
		if (ym[0].getType() instanceof DoubleAxis)
			yAxisType = (DoubleAxis) ym[0].getType();
		else
		{
			yAxisType = new DoubleAxis();
			ym[0].setType(yAxisType);
		}

		xm.setDataManager(this, false, xAxisType);
		ym[0].setDataManager(this, false, yAxisType);
	}
	final protected void calcXMinMax(double x1, double x2) 
	{
		final double oldXMin = xAxisType.getPlotMin();
		final double oldXMax = xAxisType.getPlotMax();
		// Only update the axis if the new range is outside of the old range,
		// or occupies less than 75% of the old range
		if (x1 < oldXMin || x2 > oldXMax || (x2 - x1) / (oldXMax - oldXMin) < 0.75)
		{
			xAxisType.setMin(x1);
 			xAxisType.setMax(x2);
			xm.invalidate();
		}
	}
	private DoubleAxis xAxisType;
}
