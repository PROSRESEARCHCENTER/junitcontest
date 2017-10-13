package jas.hist.util;
import jas.hist.HasSlices;
import jas.hist.HasStatistics;
import jas.hist.HasStyle;
import jas.hist.JASHistStyle;
import jas.hist.Rebinnable1DHistogramData;
import jas.hist.Rebinnable2DHistogramData;
import jas.hist.SliceParameters;
import jas.hist.Statistics;

import java.util.Observable;

/**
 * A class which simply acts as a proxy for the DataSource provided as an argument to its
 * constructor. Not very useful in itself, but can be used as a base class for more interesting
 * adapters.
 */
public class TwoDAdapter extends ObserverAdapter implements Rebinnable2DHistogramData, HasStyle, HasStatistics, HasSlices
{	
	protected Rebinnable2DHistogramData source;
	
	public TwoDAdapter(Rebinnable2DHistogramData source)
	{
		super(source instanceof Observable ? (Observable) source : null);
		this.source = source;
	}
	
	public double[][][] rebin(int xbins, double xmin, double xmax, int ybins, double ymin, double ymax, boolean wantErrors, boolean hurry, boolean overflow)
	{
		return source.rebin(xbins,xmin,xmax,ybins,ymin,ymax,wantErrors,hurry,overflow);
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

	public int getXBins()
	{
		return source.getXBins();
	}

	public int getYBins()
	{
		return source.getYBins();
	}

	public boolean isRebinnable()
	{
		return source.isRebinnable();
	}

	public int getXAxisType()
	{
		return source.getXAxisType();
	}

	public int getYAxisType()
	{
		return source.getYAxisType();
	}

	public String[] getXAxisLabels()
	{
		return source.getXAxisLabels();
	}

	public String[] getYAxisLabels()
	{
		return source.getYAxisLabels();
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
	public Statistics getStatistics()
	{
		if (source instanceof HasStatistics) return ((HasStatistics) source).getStatistics();
		return null;
	}
	public String toString()
	{
		return source.toString();
	}
	public int getNSlices()
	{
		if (source instanceof HasSlices) return ((HasSlices) source).getNSlices();
		return 0;
	}
	public SliceParameters getSliceParameters(int n)
	{
		return ((HasSlices) source).getSliceParameters(n);
	}
	public Rebinnable1DHistogramData getSlice(int n)
	{
		return ((HasSlices) source).getSlice(n);
	}
	public boolean canAddRemoveSlices()
	{
		if (source instanceof HasSlices) return ((HasSlices) source).canAddRemoveSlices();
		return false;
	}
	public int addSlice(double x, double y, double width, double height, double phi)
	{
		return ((HasSlices) source).addSlice(x,y,width,height,phi);
	}
	public void removeSlice(int n)
	{
		((HasSlices) source).removeSlice(n);
	}
}
