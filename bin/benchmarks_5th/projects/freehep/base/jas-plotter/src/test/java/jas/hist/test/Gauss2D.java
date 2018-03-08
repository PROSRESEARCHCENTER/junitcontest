package jas.hist.test;

import jas.hist.HistogramUpdate;
import jas.hist.Rebinnable1DHistogramData;
import jas.hist.Rebinnable2DHistogramData;
import jas.hist.Statistics;

import java.util.Observable;

public class Gauss2D extends Observable implements Rebinnable2DHistogramData
{
	public Gauss2D(String name, int entries)
	{
		if (entries > MAXSIZE) throw new IllegalArgumentException("Gauss2D: Size too big");

		m_tupleX = new double[MAXSIZE];
		m_tupleY = new double[MAXSIZE];
		m_entries = entries;
		m_name = name;

		for (int j=0; j<MAXSIZE; j++)
		{
			double k = Math.random()+Math.random()+Math.random()+Math.random()+Math.random()+Math.random();
			double l = Math.random()+Math.random()+Math.random()+Math.random()+Math.random()+Math.random();
			m_tupleX[j] = k/6;
			m_tupleY[j] = ymax*l/6;
		}
	}
	public String getTitle()
	{
		return m_name;
	}
	public double getXMin()
	{
		return 0;
	}
	public double getXMax()
	{
		return 1;
	}
	public double getYMin()
	{
		return 0;
	}
	public double getYMax()
	{
		return ymax;
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
	public int getXBins()
	{
		return 40;
	}
	public int getYBins()
	{
		return 40;
	}
	public int getXAxisType()
	{
		return Rebinnable1DHistogramData.DOUBLE;
	}
	public int getYAxisType()
	{
		return Rebinnable1DHistogramData.DOUBLE;
	}

	public double[][][] rebin(int Xbins, double Xmin, double Xmax, 
		                      int Ybins, double Ymin, double Ymax,
		                      boolean wantErrors, boolean hurry, boolean overflow)
	{
		synchronized (this) { setChanged(); }

		double[][] hist = new double[Xbins + (overflow?2:0)][Ybins + (overflow?2:0)]; 
		double XbinWidth = (Xmax - Xmin)/Xbins;
		double YbinWidth = (Ymax - Ymin)/Ybins;

		for (int k=0; k<m_entries; k++)
		{
			int Xbin = (int) ((m_tupleX[k] - Xmin)/XbinWidth);
			int Ybin = (int) ((m_tupleY[k] - Ymin)/YbinWidth);
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
	public int getSize()
	{
		return m_entries;
	}
	public void setSize(int newSize)
	{
		if (newSize > MAXSIZE) throw new IllegalArgumentException("Gauss: Size too big");
		m_entries = newSize;
		synchronized (this) { notifyObservers(new HistogramUpdate(HistogramUpdate.DATA_UPDATE,true)); }
	}
	public double getXData(int i)
	{
		return m_tupleX[i];
	}
	public double getYData(int i)
	{
		return m_tupleY[i];
	}
	public Statistics getStatistics()
	{
		return null;
	}
	double ymax = Math.random();
	double m_tupleX[];
	double m_tupleY[];
	int m_entries;
	String m_name;
	protected final static int MAXSIZE = 10000;
}
