package jas.hist.util;
import jas.hist.HasScatterPlotData;
import jas.hist.Rebinnable2DHistogramData;
import jas.hist.ScatterEnumeration;
import jas.hist.ScatterPlotSource;

/**
 * An adaptor that converts a ScatterPlotSource to a DataSource that implements both
 * Rebinnable2DHistogramData and HasScatterPlotData
 */

public class ScatterTwoDAdapter extends ScatterSourceAdapter implements Rebinnable2DHistogramData, HasScatterPlotData
{
	public ScatterTwoDAdapter(ScatterPlotSource source)
	{
		super(source);
	}
	public double[][][] rebin(int Xbins, double Xmin, double Xmax, int Ybins, double Ymin, double Ymax, boolean wantErrors, boolean hurry, boolean overflow)
	{
		double[][] hist = new double[Xbins + (overflow?2:0)][Ybins + (overflow?2:0)]; 
		double XbinWidth = (Xmax - Xmin)/Xbins;
		double YbinWidth = (Ymax - Ymin)/Ybins;

		ScatterEnumeration e = overflow ? source.startEnumeration() : source.startEnumeration(Xmin,Xmax,Ymin,Ymax);
		
		double[] point = new double[2];
		while (e.getNextPoint(point))
		{
			// Note Math.floor returns the largest integer value <= the argument
			int Xbin = (int) Math.floor((point[0] - Xmin)/XbinWidth);
			int Ybin = (int) Math.floor((point[1] - Ymin)/YbinWidth);
			if (Xbin>=0 && Xbin<Xbins && Ybin>=0 && Ybin<Ybins) hist[Xbin][Ybin] += 1;
			else if (overflow)
			{
				if (Xbin < 0   ) Xbin = Xbins;
				if (Xbin >Xbins) Xbin = Xbins+1;
				if (Ybin < 0   ) Ybin = Ybins;
				if (Ybin >Ybins) Ybin = Ybins+1;
				hist[Xbin][Ybin] += 1;
			}
		}
		double[][][] result = new double[1][][];
		result[0] = hist;
		return result;
	}
	public int getXBins()
	{
		return 40;
	}
	public int getYBins()
	{
		return 40;
	}
	public boolean isRebinnable()
	{
		return true;
	}
	public String[] getXAxisLabels()
	{
		return null;
	}
	public String[] getYAxisLabels()
	{
		return null;
	}
	public boolean hasScatterPlotData()
	{
		return true;
	}
}