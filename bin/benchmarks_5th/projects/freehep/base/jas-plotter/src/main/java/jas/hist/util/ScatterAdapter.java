package jas.hist.util;

import jas.hist.HasScatterPlotData;
import jas.hist.Rebinnable2DHistogramData;
import jas.hist.ScatterEnumeration;

/**
 * A ScatterAdapter takes a Rebinnable2DHistogramData source which (optionally)
 * also implements HasScatterPlotData, and in turn provides the same interface
 * to its observers.
 * 
 * It is designed to be used as a base class for classes that do some more interesting
 * transformation of the data.
 */

public class ScatterAdapter extends TwoDAdapter implements HasScatterPlotData
{
	protected HasScatterPlotData scatter;
	public ScatterAdapter(Rebinnable2DHistogramData source)
	{
		super(source);
		if (source instanceof HasScatterPlotData) scatter = (HasScatterPlotData) source;
	}
	public ScatterEnumeration startEnumeration(double xMin, double xMax, double yMin, double yMax)
	{
		if (scatter == null) throw new RuntimeException("No Scatter Plot Data Awailable");
		return scatter.startEnumeration(xMin,xMax,yMin,yMax);
	}
	public ScatterEnumeration startEnumeration()
	{
		if (scatter == null) throw new RuntimeException("No Scatter Plot Data Awailable");
		return scatter.startEnumeration();
	}
	public boolean hasScatterPlotData()
	{
		return scatter != null && scatter.hasScatterPlotData();
	}
}
