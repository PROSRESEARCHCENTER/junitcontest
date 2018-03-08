package jas.hist;
import jas.plot.DataArea;
import jas.plot.Legend;

abstract class TwoDDataManager extends SliceableDataManager
{	
	TwoDDataManager(JASHist plot, final DataArea da, final Legend l, StatisticsBlock stats)
	{
		super(plot, da, l, stats);
	}
}

