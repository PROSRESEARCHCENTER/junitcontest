package jas.hist;

import jas.plot.DataArea;
import jas.plot.Legend;
import jas.plot.Overlay;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

abstract class SliceableDataManager extends AbstractDataManager implements SupportsSlices
{
	private Vector slices = new Vector();
	
	SliceableDataManager(JASHist plot, final DataArea da, final Legend l, StatisticsBlock stats)
	{
		super(plot, da, l, stats);
	}
	
	public void addSlice(SliceParameters sp)
	{					
		// Maybe we should make a JASHistSliceData here
		Overlay so = new SliceOverlay(sp);
		slices.addElement(so);
		da.add(so);
	}
	public void removeAllSlices()
	{
		Enumeration e = slices.elements();
		while (e.hasMoreElements())
		{
			SliceOverlay so = (SliceOverlay) e.nextElement();
			da.remove(so);
			//so.destroy(); 
		}
		slices.removeAllElements();
	}
	void destroy()
	{
		super.destroy();
		removeAllSlices();
		slices.removeAllElements();
	}
	public void fillSliceMenu(JMenu menu)
	{
		menu.add(addPerDataSourceMenu("Add Slice/Projection",new DataSourceMenuFactory()
		{
			public JMenu createMenu(String name, final JASHistData ds)
			{
				return new SliceMenu(name,ds);	
			}
		}));
		menu.add(addPerDataSourceMenu("Remove Slice/Projection",new DataSourceMenuFactory()
		{
			public JMenu createMenu(String name, final JASHistData ds)
			{
				return new RemoveSliceMenu(name,ds);
			}
		}));	
	}
	void modifyPopupMenu(final JPopupMenu menu, final Component source)
	{
		menu.add(addPerDataSourceMenu("Add Slice/Projection",new DataSourceMenuFactory()
		{
			public JMenu createMenu(String name, final JASHistData ds)
			{
				return new SliceMenu(name,ds);	
			}
		}));
		menu.add(addPerDataSourceMenu("Remove Slice/Projection",new DataSourceMenuFactory()
		{
			public JMenu createMenu(String name, final JASHistData ds)
			{
				return new RemoveSliceMenu(name,ds);
			}
		}));
	}
	final private class SliceMenu extends JMenu
	{
		public SliceMenu(String name, JASHistData data)
		{
			super(name);
			DataSource ds = data.getDataSource();
			boolean slice = ds instanceof HasSlices && ((HasSlices) ds).canAddRemoveSlices();
			setEnabled(slice);
			if (slice)
			{
				hasSlices = (HasSlices) ds;
				this.add(new SliceItem("X Projection",0));
				this.add(new SliceItem("Y Projection",Math.PI/2));
				this.add(new SliceItem("X Slice",xm,ym[0],0));
				this.add(new SliceItem("Y Slice",xm,ym[0],Math.PI/2));
			}
		}
		private HasSlices hasSlices;
		private class SliceItem extends JMenuItem
		{
			SliceItem(String name, double phi)
			{
				super(name);
				this.phi = phi;
				this.x = 0;
				this.y = 0;
				this.width = Double.POSITIVE_INFINITY;
				this.height = Double.POSITIVE_INFINITY;
			}
			SliceItem(String name, ManagedAxis x, ManagedAxis y, double phi)
			{
				super(name);
				this.phi = phi;
				this.x = (x.getMin()+x.getMax())/2;
				this.y = (y.getMin()+y.getMax())/2;
				if (phi == 0)
				{
					this.width = (x.getMax()-x.getMin())/4;
					this.height = (y.getMax()-y.getMin())/4;
				}
				else
				{
					this.height = (x.getMax()-x.getMin())/4;
					this.width = (y.getMax()-y.getMin())/4;
				}
			}
			public void fireActionPerformed(ActionEvent e)
			{
				int n = hasSlices.addSlice(x,y,width,height,phi);
				addSlice(hasSlices.getSliceParameters(n));
			}
			private double phi; 
			private double x;
			private double y;
			private double width;
			private double height;
		}
	}
	final private class RemoveSliceMenu extends JMenu
	{
		public RemoveSliceMenu(String name, JASHistData data)
		{
			super(name);
			DataSource ds = data.getDataSource();
			boolean slice = ds instanceof HasSlices && 
							((HasSlices) ds).canAddRemoveSlices() &&
							((HasSlices) ds).getNSlices()>0;
			setEnabled(slice);
			if (slice)
			{
				hasSlices = (HasSlices) ds;
				//for (int i=0; i<hasSlices.getNSlices(); i++)
				//{
				//	add(new SliceItem(hasSlices.getSlice(i).getTitle(),i));
				//}
				//if (hasSlices.getNSlices()>1)
				//{
					add(new JMenuItem("Remove All")
					{
						public void fireActionPerformed(ActionEvent e)
						{
							removeAllSlices();
							for (int i=hasSlices.getNSlices(); i>0;)
							{
								hasSlices.removeSlice(--i);
							}
						}
					});
				//}
			}
		}
		private HasSlices hasSlices;
		private class SliceItem extends JMenuItem
		{
			SliceItem(String name, int index)
			{
				super(name);
				this.index = index;
			}
			public void fireActionPerformed(ActionEvent e)
			{
				
				hasSlices.removeSlice(index);
			}
			private int index;
		}
	}
	
}
