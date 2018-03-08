package jas.hist.test;

import jas.hist.DataSource;
import jas.hist.HasDataSource;
import jas.hist.HasStyle;
import jas.hist.HistogramUpdate;
import jas.hist.JASHist1DHistogramStyle;
import jas.hist.JASHistStyle;
import jas.hist.Rebinnable1DHistogramData;
import jas.hist.Statistics;
import jas.hist.util.ObserverAdapter;

import java.util.Observable;

public class MemoryDataSource extends ObserverAdapter implements 
	HasDataSource, Rebinnable1DHistogramData, HasStyle
{
	private static final HistogramUpdate hdr = new HistogramUpdate(HistogramUpdate.DATA_UPDATE,true);
	private static final int SECONDS = 60;
	
	public MemoryDataSource()
	{
		super(t);
	}
	public DataSource getDataSource(String param) 
	{
		return this;
	}
	public double[][] rebin(int nbin, double min, double max, boolean wantErrors, boolean hurry)
	{
		double[] result = t.getSnapshot();
		double[][] r = { result };
		return r;
	}
	public double getMin()
	{
		return -SECONDS;
	}
	public double getMax()
	{
		return 0;
	}
	public int getBins()
	{
		return SECONDS;
	}
	public boolean isRebinnable()
	{
		return false;
	}
	public int getAxisType()
	{
		return Rebinnable1DHistogramData.INTEGER;
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
		return "Java Memory Usage";
	}
	public JASHistStyle getStyle()
	{
		JASHist1DHistogramStyle style = new JASHist1DHistogramStyle();
		style.setShowErrorBars(false);
		style.setShowDataPoints(true);
		style.setShowLinesBetweenPoints(true);
		style.setShowHistogramBars(false);
		return style;
	}
	private static MemoryThread t = new MemoryThread();
	private static class MemoryThread extends Observable implements Runnable
	{
		MemoryThread()
		{
			bins = new double[SECONDS];
			for (int i=0; i<SECONDS; i++) bins[i] = Double.NaN;
			thread = new Thread(this);
         thread.setDaemon(true);
			thread.start();
		}
		public void run()
		{
			try
			{
				Runtime r = Runtime.getRuntime();
				for (;;)
				{
					synchronized (this)
					{
						bins[index++] = (r.totalMemory() - r.freeMemory())/1000000.;
						if (index>=SECONDS) index = 0;
					}
					setChanged();
					notifyObservers(hdr);
					thread.sleep(1000);			
				}
			}
			catch (InterruptedException x) { }
		}
		synchronized double[] getSnapshot()
		{
			double[] result = new double[SECONDS];
			int j=0;
			for (int i=index; i<SECONDS; i++) result[j++] = bins[i];
			for (int i=0; i<index; i++) result[j++] = bins[i];
			return result;
		}
		private Thread thread;
		private int index = 0;
		private double[] bins;
	}
}
