package jas.hist.util;
import jas.hist.HasStatistics;
import jas.hist.HasStyle;
import jas.hist.JASHistStyle;
import jas.hist.Rebinnable1DHistogramData;
import jas.hist.Statistics;

import java.util.Observable;
/**
 * A class which simply acts as a proxy for the DataSource provided as an argument to its
 * constructor. Not very useful in itself, but can be used as a base class for more interesting
 * adapters.
 */

public class OneDAdapter extends ObserverAdapter implements Rebinnable1DHistogramData, HasStatistics, HasStyle
{
	protected Rebinnable1DHistogramData source;
	
	public OneDAdapter(Rebinnable1DHistogramData source)
	{
		super(source instanceof Observable ? (Observable) source : null);
		this.source = source;
	}
	public double[][] rebin(int bins, double min, double max, boolean wantErrors, boolean hurry)
	{
		return source.rebin(bins,min,max,wantErrors,hurry);
	}
	public double getMin()
	{
		return source.getMin();
	}
	public double getMax()
	{
		return source.getMax();
	}
	public int getBins()
	{
		return source.getBins();
	}
	public boolean isRebinnable()
	{
		return source.isRebinnable();
	}
	public int getAxisType()
	{
		return source.getAxisType();
	}
	public String[] getAxisLabels()
	{
		return source.getAxisLabels();
	}
	public String getTitle()
	{
		return source.getTitle();
	}
	public Statistics getStatistics()
	{
		return source instanceof HasStatistics ?  
			   ((HasStatistics) source).getStatistics() : null;
	}
	public JASHistStyle getStyle()
	{
		return source instanceof HasStyle ?  
			   ((HasStyle) source).getStyle() : null;
	}
	public String toString()
	{
		return source.toString();
	}
}
