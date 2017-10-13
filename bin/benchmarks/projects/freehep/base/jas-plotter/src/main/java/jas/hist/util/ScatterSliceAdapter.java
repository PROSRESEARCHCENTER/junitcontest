package jas.hist.util;

import jas.hist.Rebinnable2DHistogramData;
import jas.hist.ScatterEnumeration;
import jas.hist.ScatterPlotSource;

import javax.swing.event.EventListenerList;

/**
 * Takes as its source a Rebinnable2DHistogramData which also 
 * implements HasScatterPlotData and in addition to relaying the source
 * data to the destination, also implements HasSlices and SliceAdapter
 */

public class ScatterSliceAdapter extends TwoDSliceAdapter
{
	private EventListenerList listenerList = new EventListenerList();

	public ScatterSliceAdapter(Rebinnable2DHistogramData source)
	{
		super(source);
	}
	public ScatterSliceAdapter(ScatterPlotSource source)
	{
		super(new ScatterTwoDAdapter(source));
	}

	public int addSlice(double x, double y, double width, double height, double phi)
	{
		if (scatter != null && scatter.hasScatterPlotData())
		{
			int n = slices.size();
			String title = (width==Double.POSITIVE_INFINITY?"Projection ":"Slice ")+n;
			slices.addElement(new Slice(title,x,y,width,height,phi));
			fireSliceAdded(n);
			return n;
		}
		else return super.addSlice(x,y,width,height,phi);
	}	
	private class Slice extends AbstractSlice
	{
		private double m_min, m_max;
		private boolean minMaxValid = false;
		
		Slice(String title, double x, double y, double width, double height, double phi)
		{
			super(title, (width == Double.POSITIVE_INFINITY));
		
			this.parm = new DefaultSliceParameters(x,y,width,height,phi)
			{
				protected void changed()
				{
					sendUpdate();
				}
			};
		}
		public void sendUpdate()
		{
			minMaxValid = false;
			super.sendUpdate();
		}
		
		public double[][] rebin(int bins, double min, double max, boolean wantErrors, boolean hurry)
		{
			double[] hist = new double[bins];
			ScatterEnumeration e = scatter.startEnumeration();
					
			double sinPhi = Math.sin(parm.phi);
			double cosPhi = Math.cos(parm.phi);
					
			double p0 = -parm.x*sinPhi + parm.y*cosPhi;
			double q0 = parm.x*cosPhi + parm.y*sinPhi;
								
			double[] point = new double[2];
			while (e.getNextPoint(point))
			{
				double q = point[0]*cosPhi + point[1]*sinPhi;
				if (!projection)
				{
					if (Math.abs(q-q0) > parm.width) continue;
					double p = - point[0]*sinPhi + point[1]*cosPhi;
					if (Math.abs(p-p0) > parm.height) continue;
				}
				if (q<min) continue;
				int bin = (int) ((q-min)*bins/(max-min));
				if (bin >= bins) continue;
				hist[bin]++; 
			}
			double[][] result = { hist };
			return result;
		}
		private void calcMinMax()
		{
			minMaxValid = true;
			if (projection)
			{
				ScatterEnumeration e = scatter.startEnumeration();
							
				double sinPhi = Math.sin(parm.phi);
				double cosPhi = Math.cos(parm.phi);
				double[] point = new double[2];

				double min = 0;
				double max = 0;
				for (boolean first=true; e.getNextPoint(point); first=false)
				{
					double q = point[0]*cosPhi + point[1]*sinPhi;
					if (first) min = max = q;
					else
					{
						min = Math.min(min,q);
						max = Math.max(max,q);
					}
				}
				m_min = min;
				m_max = max;
			}
			else
			{
				double sinPhi = Math.sin(parm.phi);
				double cosPhi = Math.cos(parm.phi);
				double q0 = parm.x*cosPhi + parm.y*sinPhi;
				m_min = q0-parm.width;
				m_max = q0+parm.width;
			}
		}
		public double getMin()
		{
			if (!minMaxValid) calcMinMax();
			return m_min;
		}
		public double getMax()
		{
			if (!minMaxValid) calcMinMax();
			return m_max;
		}
		public int getBins()
		{
			return 40;
		}
		public boolean isRebinnable()
		{
			return true;
		}
	}
}
