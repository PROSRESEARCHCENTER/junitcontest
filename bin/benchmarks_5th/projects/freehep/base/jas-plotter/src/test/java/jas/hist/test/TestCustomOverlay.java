package jas.hist.test;

import jas.hist.CustomOverlay;
import jas.hist.DataSource;
import jas.hist.HasStyle;
import jas.hist.HistogramUpdate;
import jas.hist.JASHistScatterPlotStyle;
import jas.hist.JASHistStyle;
import jas.hist.ScatterEnumeration;
import jas.hist.ScatterPlotSource;
import jas.plot.DateCoordinateTransformation;
import jas.plot.DoubleCoordinateTransformation;
import jas.plot.LegendEntry;
import jas.plot.OverlayContainer;
import jas.plot.PlotGraphics;

import java.util.Observable;

public class TestCustomOverlay extends Observable implements ScatterPlotSource, HasStyle, Runnable 
{
	private double[] values = new double[100];
	private double[] times = new double[100];
	private int maxPoint = 0;
	private double xmin = 0;
	private double xmax = 0;
	private static final HistogramUpdate hdr = new HistogramUpdate(HistogramUpdate.DATA_UPDATE+HistogramUpdate.RANGE_UPDATE,true);
	
	public TestCustomOverlay()
	{
		hdr.setAxis(hdr.HORIZONTAL_AXIS);
		hdr.setAxis(hdr.VERTICAL_AXIS);
		Thread t = new Thread(this);
		t.start();
	}
	public void run()
	{
		double previousValue = 0;
		double value = 0;
		try
		{
			for (;;)
			{
				Thread.sleep(100);
				value += Math.random() - 0.5;
				if (Math.abs(value - previousValue) > 2)
				{
					addPoint(value,System.currentTimeMillis()/1000.);
					previousValue = value;
				}
			}
		}
		catch (InterruptedException e)
		{
		}
	}
	synchronized void addPoint(double value, double time)
	{
		if (maxPoint < values.length)
		{
			values[maxPoint] = value;
			times[maxPoint] = time;
			
			xmin = Math.min(xmin,value);
			xmax = Math.max(xmax,value);
			maxPoint++;
		}
		else
		{
			xmin = xmax = values[1];
			for (int i=1; i<maxPoint; i++)
			{
				double v = values[i];
				xmin = Math.min(xmin,v);
				xmax = Math.max(xmax,v);
				values[i-1] = v;
				times[i-1] = times[i];
			}
			values[maxPoint-1] = value;
			times[maxPoint-1] = time;
		}
		setChanged();
		notifyObservers(hdr);
	}
	public JASHistStyle getStyle()
	{
		JASHistScatterPlotStyle style = new JASHistScatterPlotStyle();
		style.setCustomOverlay(new MyCustomOverlay());
		return style;
	}

	public ScatterEnumeration startEnumeration(double xMin, double xMax, double yMin, double yMax)
	{
		return new CustomEnumeration();
	}
	public ScatterEnumeration startEnumeration()
	{
		return new CustomEnumeration();
	}

	public double getXMin()
	{
		return times[0];
	}

	public double getXMax()
	{
		return times[maxPoint-1];
	}

	public double getYMin()
	{
		return xmin;
	}

	public double getYMax()
	{
		return xmax;
	}

	public void clearChanges()
	{
		// TODO: Add your own implementation.
	}

	public int getXAxisType()
	{
		return DATE;
	}

	public int getYAxisType()
	{
		return DOUBLE;
	}

	public String getTitle()
	{
		return "Custom Overlay";
	}
	private class CustomEnumeration implements ScatterEnumeration
	{
		public boolean getNextPoint(double[] d)
		{
			if (n<maxPoint)
			{
				d[0] = times[n];
				d[1] = values[n];
				n++;
				return true;
			}
			else return false;
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
	}
	protected double[] x;
	protected double[] y;
	private java.util.Random random = new java.util.Random();
}

class MyCustomOverlay implements LegendEntry, CustomOverlay
{
	public void setDataSource(DataSource ds)
	{
		source = (ScatterPlotSource) ds;
	}
	
	public void paintIcon(PlotGraphics g, int size, int height)
	{
		// TODO: Add your own implementation.
	}

	public String getTitle()
	{
		return source.getTitle();
	}

	public void paint(PlotGraphics g, boolean isPrinting)
	{
		DateCoordinateTransformation xtd = (DateCoordinateTransformation) container.getXTransformation();
		DoubleCoordinateTransformation yt = (DoubleCoordinateTransformation) container.getYTransformation();
		DoubleCoordinateTransformation xt = new DateTransformationConverter(xtd);
		
		g.setTransformation(xt,yt);
		g.setColor(java.awt.Color.blue);
		
		ScatterEnumeration e = source.startEnumeration();
		double[] point = new double[2];
		double[] lastPoint = null;
		while (e.getNextPoint(point))
		{
			if (lastPoint != null)
				g.drawLine(lastPoint[0],lastPoint[1],point[0],lastPoint[1]);
			g.drawSymbol(point[0],point[1],5,g.SYMBOL_DOT);
			lastPoint = point;
			point = new double[2];
		}
	}

	public void containerNotify(OverlayContainer c)
	{
		container = c;
	}
	OverlayContainer container;
	ScatterPlotSource source;
	
	private class DateTransformationConverter implements DoubleCoordinateTransformation
	{
		private DateCoordinateTransformation source;
		
		DateTransformationConverter(DateCoordinateTransformation source)
		{
			this.source = source;
		}	
		public double convert(double d)
		{
			return source.convert((long) (d*1000));
		}
		public double unConvert(double i)
		{
			return source.map(i)/1000.;
		}
		public double getPlotMin()
		{
			return source.getAxisMin()/1000.;
		}
		public double getPlotMax()
		{
			return source.getAxisMax()/1000.;
		}
	}
}
