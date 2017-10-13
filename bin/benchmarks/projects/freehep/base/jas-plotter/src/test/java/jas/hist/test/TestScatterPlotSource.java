package jas.hist.test;

import jas.hist.ScatterEnumeration;
import jas.hist.ScatterPlotSource;
import jas.hist.Statistics;

import java.util.Date;
import java.util.Observable;

class TimeScatterPlotSource extends TestScatterPlotSource
{
	TimeScatterPlotSource()
	{
		super();
		end = new Date().getTime()/1000;
		start = end - 60;
		for (int i=0; i<x.length; i++) x[i] = start + (x[i]+3)/6*(end-start);
	}
	public double getXMin()
	{
		return start;
	}
	public double getXMax()
	{
		return end;
	}
	public int getXAxisType()
	{
		return DATE;
	}
	private double start;
	private double end;
}
public class TestScatterPlotSource extends Observable implements ScatterPlotSource
{
	public TestScatterPlotSource(int delay)
	{
		this();
		this.delay = delay;
	}
	public TestScatterPlotSource()
	{
		x = new double[10000];
		y = new double[10000];
		for (int i=0; i<10000; i++)
		{
			x[i] = random.nextGaussian();
			y[i] = random.nextGaussian()*2;
		}
	}
	public ScatterEnumeration startEnumeration()
	{
		return new TestEnumeration();
	}
	public ScatterEnumeration startEnumeration(double a,double b,double c,double d)
	{
		return new TestEnumeration(a,b,c,d);
	}
	public String getTitle()
	{
		return "Scatter Plot";
	}
	public double getXMin()
	{
		return -3;
	}
	public double getXMax()
	{
		return +3;
	}
	public double getYMin()
	{
		return -6;
	}
	public double getYMax()
	{
		return +6;
	}
	public int getXAxisType()
	{
		return DOUBLE;
	}
	public int getYAxisType()
	{
		return DOUBLE;
	}
	public Statistics getStatistics()
	{
		return null;
	}
	public void clearChanges()
	{
	}
	private class TestEnumeration implements ScatterEnumeration
	{
		TestEnumeration()
		{
			limits = false;
		}
		TestEnumeration(double xmin, double xmax, double ymin, double ymax)
		{
			this.xmin = xmin;
			this.xmax = xmax;
			this.ymin = ymin;
			this.ymax = ymax;
			limits = true;
		}
		public boolean getNextPoint(double[] d)
		{
			// simulate slow network connection
			if (delay > 0)
			{
				try
				{
				  Thread.sleep(delay);
				}
				catch (InterruptedException e) {} 
			}
			if (limits)
			{
				while (n<10000)
				{
					if (x[n]>=xmin && x[n]<xmax &&
						y[n]>=ymin && y[n]<ymax)
					{
						d[0] = x[n];
						d[1] = y[n];
						n++;
						return true;
					}
					n++;
				}
				return false;
			}
			else
			{
				if (n==10000) return false;
				d[0] = x[n];
				d[1] = y[n];
				n++;
				return true;
			}

		}
		public void resetEndPoint()
		{
			// ??
		}
		public void restart()
		{
			n = 0;
		}
		private int n = 0;
		private boolean limits;
		private double ymin, ymax, xmin, xmax;
	}
	private int delay = 0;
	protected double[] x;
	protected double[] y;
	private java.util.Random random = new java.util.Random();

}
