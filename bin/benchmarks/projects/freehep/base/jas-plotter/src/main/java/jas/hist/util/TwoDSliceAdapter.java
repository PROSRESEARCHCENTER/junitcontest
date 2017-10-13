package jas.hist.util;
import jas.hist.HasSlices;
import jas.hist.Rebinnable1DHistogramData;
import jas.hist.Rebinnable2DHistogramData;
import jas.hist.SliceParameters;

import java.util.Vector;

import javax.swing.event.EventListenerList;

/**
 * A Two2SliceAdapter can be used to convert any
 * Rebinnable2DHistogramData object to a Rebinnable2DHistogramData
 * which also implements HasSlices
 */

public class TwoDSliceAdapter extends ScatterAdapter implements HasSlices, SliceAdapter
{ 
	protected Vector slices = new Vector();
	private EventListenerList listenerList = new EventListenerList();
	
	public TwoDSliceAdapter(Rebinnable2DHistogramData source)
	{
		super(source);
	}

	public void addSliceListener(SliceListener l) 
	{
		listenerList.add(SliceListener.class, l);
	}

	public void removeSliceListener(SliceListener l) 
	{
		listenerList.remove(SliceListener.class, l);
	}


	// Notify all listeners that have registered interest for
	// notification on this event type.  The event instance 
	// is lazily created using the parameters passed into 
	// the fire method.

	protected void fireSliceAdded(int index) 
	{
		SliceEvent sliceEvent = null;
		// Guaranteed to return a non-null array
		Object[] listeners = listenerList.getListenerList();
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length-2; i>=0; i-=2) 
		{
			if (listeners[i]==SliceListener.class) 
			{
				// Lazily create the event:
				if (sliceEvent == null) 
					sliceEvent = new SliceEvent(this,SliceEvent.EventType.SLICEADDED,index);
				((SliceListener)listeners[i+1]).sliceAdded(sliceEvent);
			}	       
		}
	}	
	protected void fireSliceRemoved(int index) 
	{
		SliceEvent sliceEvent = null;
		// Guaranteed to return a non-null array
		Object[] listeners = listenerList.getListenerList();
		// Process the listeners last to first, notifying
		// those that are interested in this event
		for (int i = listeners.length-2; i>=0; i-=2) 
		{
			if (listeners[i]==SliceListener.class) 
			{
				// Lazily create the event:
				if (sliceEvent == null) 
					sliceEvent = new SliceEvent(this,SliceEvent.EventType.SLICEREMOVED,index);
				((SliceListener)listeners[i+1]).sliceRemoved(sliceEvent);
			}	       
		}
	}	
	public int getNSlices()
	{
		return slices.size();
	}

	public SliceParameters getSliceParameters(int n)
	{
		return ((AbstractSlice) slices.elementAt(n)).getParameters();
	}

	public Rebinnable1DHistogramData getSlice(int n)
	{
		return (Rebinnable1DHistogramData) slices.elementAt(n);
	}

	public boolean canAddRemoveSlices()
	{
		return true;
	}

	public int addSlice(double x, double y, double width, double height, double phi)
	{
		int n = slices.size();
		String title = (width==Double.POSITIVE_INFINITY?"Projection ":"Slice ")+n;
		if (source.isRebinnable())
		{
			slices.addElement(new RebinnableSlice(title,x,y,width,height,phi));
		}
		else
		{
			slices.addElement(new NonRebinnableSlice(title,x,y,width,height,phi));
		}
		fireSliceAdded(n);
		return n;
	}

	public void removeSlice(int n)
	{
		fireSliceRemoved(n);
		slices.removeElementAt(n);
	}
	private class NonRebinnableSlice extends AbstractSlice
	{
		NonRebinnableSlice(String title, double x, double y, double width, double height, double phi)
		{
			super(title, (width == Double.POSITIVE_INFINITY));
		
			this.parm = new DefaultSliceParameters(x,y,width,height,phi)
			{
				// We need to override all the set methods, since we are restrained by the source binning
				public void setPhi(double value)
				{
					value += 2*Math.PI;
					value %= Math.PI;
					value = (value > Math.PI/4 && value < 3*Math.PI/4) ? Math.PI/2 : 0;
					super.setPhi(value);
				}
				public void setX(double value)
				{
					double min = source.getXMin();
					double max = source.getXMax();
					int bins = source.getXBins();
					int bin = Math.round((float) (bins*(value-min)/(max-min)));
					super.setX(min + bin*(max-min)/bins);
				}
				public void setY(double value)
				{
					double min = source.getYMin();
					double max = source.getYMax();
					int bins = source.getYBins();
					int bin = Math.round((float) (bins*(value-min)/(max-min)));
					super.setY(min + bin*(max-min)/bins);
				}
				public void setWidth(double value)
				{
					double min,max;
					int bins;
					if (parm.phi == 0)
					{
						min = source.getXMin();
						max = source.getXMax();
						bins = source.getXBins();
					}
					else
					{
						min = source.getYMin();
						max = source.getYMax();
						bins = source.getYBins();	
					}
					double binWidth = (max-min)/bins;
					int w = Math.round((float) (value/binWidth));
					super.setWidth(w*binWidth);
				}
				public void setHeight(double value)
				{
					double min,max;
					int bins;
					if (parm.phi == 0)
					{
						min = source.getYMin();
						max = source.getYMax();
						bins = source.getYBins();
					}
					else
					{
						min = source.getXMin();
						max = source.getXMax();
						bins = source.getXBins();
					}
					double binWidth =  (max-min)/bins;
					int w = Math.round((float) (value/binWidth));
					super.setHeight(w*binWidth);
				}
				protected void changed()
				{
					sendUpdate();
				}
			};
		}
		public double[][] rebin(int bins, double min, double max, boolean wantErrors, boolean hurry)
		{
			double xmin = source.getXMin();
			double xmax = source.getXMax();
			double ymin = source.getYMin();
			double ymax = source.getYMax();
			int xbins = source.getXBins();
			int ybins = source.getYBins();
			
			double[][][] sresult = source.rebin(xbins,xmin,xmax,ybins,ymin,ymax,wantErrors,hurry,false);
			double[][] sdata = sresult[0];
			
			double [] hist = new double[bins];
			
			if (parm.phi == 0)  // if slice is || to X axis
			{
				int minXBin = Math.round((float) ((min-xmin)*xbins/(xmax-xmin)));
				int maxXBin = Math.round((float) ((max-xmin)*xbins/(xmax-xmin)));
				if (maxXBin-minXBin != bins) throw new RuntimeException("Failed sanity check");

				int minYBin = Math.round((float) ((parm.y-parm.height-ymin)*ybins/(ymax-ymin)));
				int maxYBin = Math.round((float) ((parm.y+parm.height-ymin)*ybins/(ymax-ymin)));
				
				for (int i = Math.max(minXBin,0); i < Math.min(maxXBin,xbins); i++) {
					for (int j = Math.max(minYBin,0); j < Math.min(maxYBin,ybins); j++) {
						hist[i-minXBin] += sdata[i][j]; 
					}
				}
			}		
			else 
			{   
				int minXBin = Math.round((float) ((min-ymin)*ybins/(ymax-ymin)));
				int maxXBin = Math.round((float) ((max-ymin)*ybins/(ymax-ymin)));
				if (maxXBin-minXBin != bins) throw new RuntimeException("Failed sanity check");

				int minYBin = Math.round((float) ((parm.x-parm.height-xmin)*xbins/(xmax-xmin)));
				int maxYBin = Math.round((float) ((parm.x+parm.height-xmin)*xbins/(xmax-xmin)));
				
				for (int i = Math.max(minXBin,0); i < Math.min(maxXBin,ybins); i++) 
				{
					for (int j = Math.max(minYBin,0); j < Math.min(maxYBin,xbins); j++) 
					{
						hist[i-minXBin] += sdata[j][i]; 
					}
				}
			}
			double[][] result = { hist };
			return result;
		}
		public double getMin()
		{
			if (projection)
			{
				return parm.phi == 0 ? source.getXMin() : source.getYMin();
			}
			else
			{
				return parm.phi == 0 ? parm.x-parm.width : parm.y-parm.width;
			}
		}
		public double getMax()
		{
			if (projection)
			{
				return parm.phi == 0 ? source.getXMax() : source.getYMax();
			}
			else
			{
				return parm.phi == 0 ? parm.x+parm.width : parm.y+parm.width;
			}
		}		
		public int getBins()
		{
			double min = getMin();
			double max = getMax();
			double binWidth = parm.phi == 0 ? (source.getXMax() - source.getXMin())/source.getXBins() :
								  		      (source.getYMax() - source.getYMin())/source.getYBins();
			return Math.round((float) ((max-min)/binWidth));
		}
		public boolean isRebinnable()
		{
			return false;
		}
	}
	private class RebinnableSlice extends AbstractSlice
	{
		RebinnableSlice(String title, double x, double y, double width, double height, double phi)
		{
			super(title, (width == Double.POSITIVE_INFINITY));
		
			this.parm = new DefaultSliceParameters(x,y,width,height,phi)
			{
				// We need to override the setPhi method, since we only allow phi = 0 or PI/2
				public void setPhi(double value)
				{
					value += 2*Math.PI;
					value %= Math.PI;
					value = (value > Math.PI/4 && value < 3*Math.PI/4) ? Math.PI/2 : 0;
					super.setPhi(value);
				}
				protected void changed()
				{
					sendUpdate();
				}
			};
		}
		public double[][] rebin(int bins, double min, double max, boolean wantErrors, boolean hurry)
		{
			// Since we know our source is rebinnable, we just ask it to do all the hard work!
			if (parm.phi == 0)
			{
				double[][][] data = source.rebin(bins,min,max,1,parm.y-parm.height,parm.y+parm.height,wantErrors,hurry,false);
				double[] hist = new double[bins];
				for (int i=0; i<bins; i++) hist[i] = data[0][i][0];
				double[][] result = { hist };
				return result;
			}
			else
			{
				double[][][] data = source.rebin(1,parm.x-parm.height,parm.x+parm.height,bins,min,max,wantErrors,hurry,false);
				return data[0];
			}
		}
		public double getMin()
		{
			if (projection)
			{
				return parm.phi == 0 ? source.getXMin() : source.getYMin();
			}
			else
			{
				return parm.phi == 0 ? parm.x-parm.width : parm.y-parm.width;
			}
		}
		public double getMax()
		{
			if (projection)
			{
				return parm.phi == 0 ? source.getXMax() : source.getYMax();
			}
			else
			{
				return parm.phi == 0 ? parm.x+parm.width : parm.y+parm.width;
			}
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
