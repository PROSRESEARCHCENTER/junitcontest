package jas.hist.test;

import jas.hist.Rebinnable1DHistogramData;

import java.util.Date;

public class TimeGauss extends Gauss
{
	public TimeGauss(String name, int entries,double max,double offX,double offY)
	{
		super(name,entries,max,offX,offY);
	}
	public int getAxisType()
	{
		return Rebinnable1DHistogramData.DATE;
	}
	public static TimeGauss create(String name,int entries,Date start,Date end)
	{
		double offX = start.getTime()/1000;
		double max = (end.getTime()/1000 - offX);
		return new TimeGauss(name,entries,max,offX,0);
	}
}
