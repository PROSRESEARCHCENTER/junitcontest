package jas.hist.util;
import jas.hist.HistogramUpdate;
import jas.hist.Rebinnable1DHistogramData;
import jas.hist.SliceParameters;
import jas.hist.Statistics;

import java.util.Observable;

abstract class AbstractSlice extends Observable implements Rebinnable1DHistogramData
{
	protected DefaultSliceParameters parm;
	private String title;
	protected boolean projection;
	private HistogramUpdate hu = new HistogramUpdate(HistogramUpdate.DATA_UPDATE+HistogramUpdate.RANGE_UPDATE,true);
		
	AbstractSlice(String title, boolean projection)
	{
		this.title = title;
		this.projection = projection;
	}
	public void sendUpdate()
	{
		setChanged();
		notifyObservers(hu);
	}
	public int getAxisType()
	{
		return DOUBLE;
	}

	public String[] getAxisLabels()
	{
		return null;
	}
	public Statistics getStatistics()
	{
		return null;
	}

	public String getTitle()
	{
		return title;
	}
	SliceParameters getParameters()
	{
		return parm;
	}
}
