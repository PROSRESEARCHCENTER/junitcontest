package jas.hist.util;

import jas.hist.HasStyle;
import jas.hist.JASHistStyle;
import jas.hist.ScatterEnumeration;
import jas.hist.ScatterPlotSource;

import java.util.Observable;

class ScatterSourceAdapter extends ObserverAdapter implements ScatterPlotSource, HasStyle
{
	protected ScatterPlotSource source;
	public ScatterSourceAdapter(ScatterPlotSource source)
	{
		super(source instanceof Observable ? (Observable) source : null);
		this.source = source;
	}
	public ScatterEnumeration startEnumeration(double xMin, double xMax, double yMin, double yMax)
	{
		return source.startEnumeration(xMin,xMax,yMin,yMax);
	}
	public ScatterEnumeration startEnumeration()
	{
		return source.startEnumeration();
	}
	public double getXMin()
	{
		return source.getXMin();
	}
	public double getXMax()
	{
		return source.getXMax();
	}
	public double getYMin()
	{
		return source.getYMin();
	}
	public double getYMax()
	{
		return source.getYMax();
	}
	public int getXAxisType()
	{
		return source.getXAxisType();
	}
	public int getYAxisType()
	{
		return source.getYAxisType();
	}
	public String getTitle()
	{
		return source.getTitle();
	}
	public JASHistStyle getStyle()
	{
		if (source instanceof HasStyle) return ((HasStyle) source).getStyle();
		return null;
	}
}
