package jas.hist;

import jas.plot.DataArea;
import jas.plot.DateAxis;
import jas.plot.DoubleAxis;
import jas.plot.Legend;

import java.util.TimeZone;

final class DateScatterDataManager extends ScatterDataManager
{
	DateScatterDataManager(JASHist plot, final DataArea da, final Legend l, StatisticsBlock stats)
	{
		super(plot, da, l, stats);

		// Configure the Axes

		if (xm.getType() instanceof DateAxis)
			xAxisType = (DateAxis) xm.getType();
		else
		{
			xAxisType = new DateAxis();
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
	final protected void calcMinMaxXBins(double x1, double x2) 
	{
		long iLow = (long) (x1*1000);
		long iHigh = (long) (x2*1000);
		long oldXMin = xAxisType.getAxisMin();
		long oldXMax = xAxisType.getAxisMax();
		// Only update the axis if the new range is outside of the old range,
		// or occupies less than 75% of the old range
		if (iLow < oldXMin || iHigh > oldXMax || (iHigh - iLow) / (oldXMax - oldXMin) < 0.75)
		{
			xAxisType.setMin(iLow);
 			xAxisType.setMax(iHigh);
			xm.invalidate();
		}
	}
	JASHistData add(DataSource ds)
	{
            JASHistData jhd = super.add(ds);
            TimeZone tz = jhd.getStyle().getTimeZone();
            if (tz != null) xAxisType.setTimeZone(tz);
            return jhd;
        }
        void styleUpdate(JASHistData data) 
        {
            TimeZone tz = data.getStyle().getTimeZone();
            if (tz != null) xAxisType.setTimeZone(tz);
            super.styleUpdate(data);
        }
   
	private DateAxis xAxisType;
}
