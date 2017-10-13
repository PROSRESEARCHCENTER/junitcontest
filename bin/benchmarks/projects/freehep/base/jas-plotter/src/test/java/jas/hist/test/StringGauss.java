package jas.hist.test;

import jas.hist.Rebinnable1DHistogramData;

public class StringGauss extends Gauss
{
	public StringGauss(String name, int entries,double max,double offX,double offY)
	{
		super(name,entries,labels.length,offX,offY);
	}
	public int getAxisType()
	{
		return Rebinnable1DHistogramData.STRING;
	}
	public boolean isRebinnable()
	{
		return false;
	}
	public String[] getAxisLabels()
	{
		return labels;
	}
	public int getBins()
	{
		return labels.length;
	}
	private static String[] labels = {
		"Ace","2","3","4","5","6","7","8","9","10","Jack","Queen","King"};
}
