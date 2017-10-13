package jas.hist;

import jas.plot.DataArea;
import jas.plot.DoubleAxis;
import jas.plot.Legend;
import jas.plot.LegendEntry;
import jas.util.ColorMenu;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.SwingUtilities;
import javax.swing.colorchooser.ColorSelectionModel;
import javax.swing.colorchooser.DefaultColorSelectionModel;

abstract class ScatterDataManager extends TwoDDataManager
{
	ScatterDataManager(JASHist plot, final DataArea da, final Legend l, StatisticsBlock stats)
	{
		super(plot, da, l, stats);
		m_defaultNumberOfBins = 40; // defaultNumberOfBins;

		// Configure the Axes

                xAxis = new DoubleAxis();
		yAxis = new DoubleAxis();

		xm.setDataManager(this,true, xAxis);
		ym[0].setDataManager(this,true,yAxis);

		//new DoubleAxisListener(xm);

		xm.setBins(m_defaultNumberOfBins);
		ym[0].setBins(m_defaultNumberOfBins);
	}
	void styleUpdate(final JASHistData source)
	{
		styleChanged = true;
		JASHist2DHistogramStyle style = (JASHist2DHistogramStyle) source.getStyle();
		if (oldStyle != style.getHistStyle())
		{
			boolean lego = style.getHistStyle() == style.STYLE_3DLEGOPLOT;
			boolean surface = style.getHistStyle() == style.STYLE_3DSURFACEPLOT;
			try
			{
				if (lego)
				{
					Class klass = Class.forName("gov.fnal.plot3d.jas.SpecialLego");
					SpecialComponent special = (SpecialComponent) klass.newInstance();
					special.setData(source.getDataSource());
					da.setSpecialComponent(special.getDisplayComponent());
				}
				else if (surface)
				{
					Class klass = Class.forName("gov.fnal.plot3d.jas.SpecialSurface");
					SpecialComponent special = (SpecialComponent) klass.newInstance();
					special.setData(source.getDataSource());
					da.setSpecialComponent(special.getDisplayComponent());
            }
				else da.setSpecialComponent(null);
			}
			catch (Throwable t)
			{
				t.printStackTrace();
				da.setSpecialComponent(null);
			}
			oldStyle = style.getHistStyle();
		}
		SwingUtilities.invokeLater(this);
	}
	final void init()
	{
		computeXAxisRange();
		XAxisUpdated();
		computeYAxisRange();
		YAxisUpdated();
		computeZAxisRange();

		isInit = true;
	}
	void axisChanged(final JASHistData source)
	{
		final int index = source.getYAxis();
		if (ym[index] == null)
		{
			createYAxis(index);
			((DoubleAxis) ym[index].getType()).setUseSuggestedRange(false);
		}
		else
			ym[index].setAttentionNeeded(); 
		SwingUtilities.invokeLater(this);
	}
	void update(final HistogramUpdate update, final JASHistData ds)
	{
		JASHist2DHistogramData source = (JASHist2DHistogramData) ds;
		final int index = source.getYAxis();
		if (update.isRangeUpdate())
		{
			if (update.axisIsSet(update.HORIZONTAL_AXIS))
			{
				xm.setAttentionNeeded();
			}
			if (update.axisIsSet(update.VERTICAL_AXIS))
			{
				ym[index].setAttentionNeeded();
			}
		}
		else if (update.isReset())
		{
			//TODO: Fix this
			//source.resetSent = true;
				xm.setAttentionNeeded();
				ym[index].setAttentionNeeded();
		}
		else if (update.isDataUpdate())
		{
			//TODO: Fix this
			//source.dataChanged = true;
				xm.setAttentionNeeded();
				ym[index].setAttentionNeeded();
		}

		if (update.isFinalUpdate() || update.isReset())
			SwingUtilities.invokeLater(this);
		else
			timer.start();
	}
	protected void doUpdate()
	{            
		if (isInit)
		{
			boolean axisChanged = false;
			if (xm.needsAttention())
			{
				computeXAxisRange();
				XAxisUpdated();
				axisChanged = true;
			}
			int index = 0; // Bug, what about the other axis
			if (ym[index].needsAttention())
			{
				computeYAxisRange();
				YAxisUpdated();
				axisChanged = true;
			}
			computeZAxisRange();
			
			final Enumeration e = data.elements();
			while (e.hasMoreElements())
			{
				final Object o = e.nextElement();
				if (o instanceof JASHist2DScatterData)
				{
					JASHist2DScatterData scatData = (JASHist2DScatterData) o;
					//data.clearChanges();
					final boolean needNewEnumeration = axisChanged || scatData.resetSent || scatData.onNewAxis;
					if (needNewEnumeration || styleChanged)
					{
						scatData.restartImage(needNewEnumeration);
						scatData.onNewAxis = false;
						scatData.resetSent = false;
					}
					else if (scatData.dataChanged)
					{
						scatData.continueImage();
						scatData.dataChanged = false;
					}
				}
			}
			da.validate(); // When do we need to do this?
			da.repaint();
			styleChanged = false;
		}
	}
	JASHistData add(final DataSource ds)
	{
		da.setSpecialComponent(null);
		oldStyle = -1;
		if (ds instanceof HasScatterPlotData && ((HasScatterPlotData) ds).hasScatterPlotData())
		{
			HasScatterPlotData source = (HasScatterPlotData) ds;
			final JASHist2DScatterData d = new JASHist2DScatterData(this, source);
			data.addElement(d);
			return d;
		}
		else if (ds instanceof Rebinnable2DHistogramData)
		{
			Rebinnable2DHistogramData source = (Rebinnable2DHistogramData) ds;
			final JASHist2DHistogramData d = new JASHist2DHistogramData(this, source);
                        data.addElement(d);
			return d;			
		}
		else if (ds instanceof ScatterPlotSource)
		{
			ScatterPlotSource source = (ScatterPlotSource) ds;
			final JASHist2DScatterData d = new JASHistScatterPlotData(this, source);
			data.addElement(d);
			return d;
		}
		else throw new RuntimeException("Unknown subtype of DataSource added to ScatterDataManager");
	}
	void computeXAxisRange()
	{
		if (!xm.needsAttention()) return;
		xm.payingAttention(); // do first to avoid race conditions

		if (data.isEmpty()) return;
		if (!xm.getRangeAutomatic() || xm.isFixed())
		{
			xLow = xm.getMin();
			xHigh = xm.getMax();
			return;
		}
	
		int nShowing = 0;
		xLow = 0;
		xHigh = 0;
		boolean hasRebinnables = false;
			
		for (Enumeration e = data.elements(); e.hasMoreElements();)
		{
			JASHist2DHistogramData dw = (JASHist2DHistogramData) e.nextElement();
			if (!dw.isShowing()) continue;
			
			if (nShowing++ == 0)
			{
				xLow = dw.getXMin();
				xHigh = dw.getXMax();
			}
			else
			{
				xLow = Math.min(xLow,dw.getXMin());
				xHigh = Math.max(xHigh,dw.getXMax());	
			}
			if (dw.isRebinnable()) hasRebinnables = true;
		}
		if (nShowing == 0) return;
		xm.setBinned(hasRebinnables);

		if (!xm.getAllowSuppressedZero())
		{
			if (xLow > 0) xLow = 0;
			if (xHigh < 0) xHigh = 0;
		}
		if (xHigh <= xLow) xHigh = xLow + 1;

		calcMinMaxXBins(xLow,xHigh);
	}
	protected void calcMinMaxXBins(double x1, double x2) 
	{
		double oldXMin = xAxis.getPlotMin();
		double oldXMax = xAxis.getPlotMax();
		if (x1 != oldXMin || x2 != oldXMax)
		{
			xAxis.setMin(x1);
 			xAxis.setMax(x2);
			xm.invalidate();
		}
	}
	void computeYAxisRange()
	{
		int index = 0; // Bug, what about the other axis
		if (!ym[index].needsAttention()) return;
		ym[index].payingAttention(); // do first to avoid race conditions

		if (data.isEmpty()) return;
		if (!ym[index].getRangeAutomatic() || ym[index].isFixed())
		{
			yLow = ym[index].getMin();
			yHigh = ym[index].getMax();
			return;
		}
	
		int nShowing = 0;
		yLow = 0;
		yHigh = 0;
		boolean hasRebinnables = false;
			
		for (Enumeration e = data.elements(); e.hasMoreElements();)
		{
			JASHist2DHistogramData dw = (JASHist2DHistogramData) e.nextElement();
			if (!dw.isShowing()) continue;
			
			if (nShowing++ == 0)
			{
				yLow = dw.getYMin();
				yHigh = dw.getYMax();
			}
			else
			{
				yLow = Math.min(yLow,dw.getYMin());
				yHigh = Math.max(yHigh,dw.getYMax());	
			}
			if (dw.isRebinnable()) hasRebinnables = true;
		}
		if (nShowing == 0) return;
		ym[index].setBinned(hasRebinnables);

		if (!ym[index].getAllowSuppressedZero())
		{
			if (yLow > 0) yLow = 0;
			if (yHigh < 0) yHigh = 0;
		}
		if (yHigh <= yLow) yHigh = yLow + 1;

		calcMinMaxYBins(yLow,yHigh);

	}
	protected void calcMinMaxYBins(double y1, double y2) 
	{
		double oldYMin = yAxis.getPlotMin();
		double oldYMax = yAxis.getPlotMax();
		if (y1 != oldYMin || y2 != oldYMax)
		{
			yAxis.setMin(y1);
 			yAxis.setMax(y2);
			ym[0].invalidate();
		}
	}
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		in.defaultReadObject();
		data = new Vector();
	}
	final void requestShow(JASHistData data)
	{
 		da.add(data.getOverlay());
 		nVisible++;
		
		if (legend != null)
		{
			LegendEntry le = data.getLegendEntry();
 			if (le != null) 
			{
				legend.add(le);
				nVisibleLegend++;
				showLegend();
			}
		}
		if (stats != null)
		{
			stats.add(data);
		}
		if (isInit)
		{
			xm.setAttentionNeeded();
			computeXAxisRange();
			XAxisUpdated();
			ym[0].setAttentionNeeded();
			computeYAxisRange();
			YAxisUpdated();
			computeZAxisRange();
			da.revalidate(); 
			da.repaint();
		}
		//restartImages();
	}
	void requestHide(JASHistData data)
	{
		da.remove(data.getOverlay());
		nVisible--;
		if (legend != null)
		{
			LegendEntry le = data.getLegendEntry();
 			if (le != null) 
			{
				legend.remove(le);
				nVisibleLegend--;
				showLegend();
			}
		}
		if (stats != null)
		{
			stats.remove(data);
		}
		
		if (isInit)
		{
			xm.setAttentionNeeded();
			computeXAxisRange();
			XAxisUpdated();
			ym[0].setAttentionNeeded();
			computeYAxisRange();
			YAxisUpdated();
			computeZAxisRange();
			da.revalidate(); 
			da.repaint();
		}
	}
	void XAxisUpdated()
	{
		for (Enumeration e = data.elements(); e.hasMoreElements();)
		{
			JASHist2DHistogramData dw = (JASHist2DHistogramData) e.nextElement();
			if (dw.isShowing()) dw.setXRange(xm.getBins(),xLow,xHigh);
		}
	}
	void YAxisUpdated()
	{
		for (Enumeration e = data.elements(); e.hasMoreElements();)
		{
			JASHist2DHistogramData dw = (JASHist2DHistogramData) e.nextElement();
			if (dw.isShowing()) dw.setYRange(ym[0].getBins(),yLow,yHigh);
		}
	}
	final private void computeZAxisRange()
	{
		for (Enumeration e = data.elements(); e.hasMoreElements();)
		{
			JASHist2DHistogramData dw = (JASHist2DHistogramData) e.nextElement();
			if (dw.isShowing()) dw.calcZLimits();
		}
	}
	private void restartImages()
	{
		final Enumeration e = data.elements();
		while (e.hasMoreElements())
		{
			try
			{
				final JASHist2DScatterData data = (JASHist2DScatterData) e.nextElement();
				if (data.isVisible)
					data.restartImage(false);
			}
			catch (NullPointerException x)
			{
				// don't know where this comes from yet, but this works as is
			}
		}
	}	
	private int oldStyle = -1;
	private final int m_defaultNumberOfBins;
	transient protected double xLow, xHigh; // todo: get rid of these
	transient protected double yLow, yHigh;
	private DoubleAxis xAxis;
	private DoubleAxis yAxis;
	transient private boolean styleChanged;
	private SizeMenu m_sizeMenu;
	private StyleMenu m_styleMenu;
	private static boolean enabled3d;
	static
	{
		try
		{
			// check that java 3d and the plot3d routines are both available
			Class.forName("javax.media.j3d.Canvas3D");
			Class.forName("gov.fnal.plot3d.jas.SpecialLego");
			Class.forName("gov.fnal.plot3d.jas.SpecialSurface");
			enabled3d = true;
         if (JPopupMenu.getDefaultLightWeightPopupEnabled())
         {
            JPopupMenu.setDefaultLightWeightPopupEnabled(false);
         }
		}
		catch (Throwable t)
		{
			enabled3d = false;
		}
	}
	
	final void modifyPopupMenu(final JPopupMenu menu, final Component source)
	{
		if (menu.getComponentCount() > 0) menu.addSeparator();

		boolean scat = false;
		boolean allScat = true;
		boolean allTwoD = true;
		
		Enumeration enumer = getDataSources();
		while (enumer.hasMoreElements())
		{
			JASHistData data = (JASHistData) enumer.nextElement();
			if (data instanceof JASHist2DScatterData && ((JASHist2DScatterData) data).hasScatterPlotData()) 
			{
				scat = true;
				JASHistScatterPlotStyle pstyle = (JASHistScatterPlotStyle) data.getStyle();
				if (pstyle.getDisplayAsScatterPlot()) allTwoD = false;
				else allScat = false;
			}
		}
		if (scat)
		{
			JRadioButtonMenuItem b1 = new JRadioButtonMenuItem("Display As Scatter Plot");
			JRadioButtonMenuItem b2 = new JRadioButtonMenuItem("Display As Binned Plot");
			ButtonGroup bg = new ButtonGroup();
			bg.add(b1);
			bg.add(b2);
			b1.setSelected(allScat);
			b2.setSelected(allTwoD);
			b1.addActionListener(new ScatterActionListener(true));
			b2.addActionListener(new ScatterActionListener(false));
			menu.add(b1);
			menu.add(b2);
		}
		if (!scat || !allScat)
		{
			menu.add(addPerDataSourceMenu("Plot Style",new DataSourceMenuFactory()
			{
				public JMenu createMenu(String name, final JASHistData ds)
				{
					return new StyleMenu(name,ds);	
				}
			}));			
		}
		if (scat && !allTwoD)
		{
			if (m_sizeMenu == null) m_sizeMenu = new SizeMenu();
			m_sizeMenu.init();
			menu.add(m_sizeMenu);
			menu.add(addPerDataSourceMenu("Point Color",new DataSourceMenuFactory()
			{
				public JMenu createMenu(String name, final JASHistData ds)
				{
					final JASHistScatterPlotStyle style = (JASHistScatterPlotStyle) ds.getStyle();
					ColorSelectionModel cm = new DefaultColorSelectionModel()
					{
						public Color getSelectedColor()
						{
							return style.getDataPointColor();
						}
						public void setSelectedColor(Color c)
						{
							style.setDataPointColor(c);
						}
					};
					return new ColorMenu(name,cm,true);	
				}
			}));
		}
		super.modifyPopupMenu(menu,source);
	}
	final private class ScatterActionListener implements ActionListener
	{
		ScatterActionListener(boolean state)
		{
			this.state = state;
		}
		public void actionPerformed(ActionEvent e)
		{
			Enumeration enumer = getDataSources();
			while (enumer.hasMoreElements())
			{
				JASHistData data = (JASHistData) enumer.nextElement();
				if ((data instanceof JASHist2DScatterData)) 
				{
					JASHistScatterPlotStyle pstyle = (JASHistScatterPlotStyle) data.getStyle();
					pstyle.setDisplayAsScatterPlot(state);
				}
			}
		}
		private boolean state;
		
	}
	final private class SizeMenu extends JMenu
	{
		public SizeMenu()
		{
			super("Point Size");
			setMnemonic('S');
							
			addButton("Huge",'H',20);
			addButton("Large",'L',10);
			addButton("Medium",'M',5);
			addButton("Small",'S',3);
			addButton("Tiny",'T',1);
		}
		private void addButton(String name, char mnemonic, final int size)
		{
			JRadioButtonMenuItem item = new SizeButton(name,size);
			item.setMnemonic(mnemonic);
			group.add(item);
			this.add(item);
		}
		public void init()
		{
			int iSize = -1;
			
			Enumeration enumer = getDataSources();
			while (enumer.hasMoreElements())
			{
				JASHistData data = (JASHistData) enumer.nextElement();
				if (!(data instanceof JASHist2DScatterData)) continue;
				JASHistScatterPlotStyle style = (JASHistScatterPlotStyle) data.getStyle();
				if (iSize == -1) iSize = style.getDataPointSize();
				else if (iSize != style.getDataPointSize()) iSize = -2;
			}
			Enumeration e = group.getElements();
			while (e.hasMoreElements())
			{
				SizeButton b = (SizeButton) e.nextElement();
				b.setSize(iSize);
			}
		}
		ButtonGroup group = new ButtonGroup();
	}
	final class SizeButton extends JRadioButtonMenuItem
	{
		SizeButton(String name, int size)
		{
			super(name);
			this.size = size;
		}
		public void fireActionPerformed(ActionEvent e)
		{
			Enumeration enumer = getDataSources();
			while (enumer.hasMoreElements())
			{
				JASHistData data = (JASHistData) enumer.nextElement();
				JASHistScatterPlotStyle style = (JASHistScatterPlotStyle) data.getStyle();
				style.setDataPointSize(size);
			}
		}
		void setSize(int size)
		{
			setSelected(this.size == size);
		}
		private int size;
	}
	final private class StyleMenu extends JMenu
	{
		public StyleMenu(String name, JASHistData ds)
		{
			super(name);
			this.data = ds;
			
			JMenu map = new JMenu("Color Map");	
			map.setMnemonic('M');
				
			StyleMenu.this.add(addButton("Box",'B',JASHist2DHistogramStyle.STYLE_BOX,-1));
			StyleMenu.this.add(addButton("Ellipse",'E',JASHist2DHistogramStyle.STYLE_ELLIPSE,-1));
			if (enabled3d)
			{
				StyleMenu.this.add(addButton("3D Lego Plot",'L',JASHist2DHistogramStyle.STYLE_3DLEGOPLOT,-1));
				StyleMenu.this.add(addButton("3D Surface Plot",'S',JASHist2DHistogramStyle.STYLE_3DSURFACEPLOT,-1));
			}
			StyleMenu.this.add(map);
			map.add(addButton("Warm",'W',JASHist2DHistogramStyle.STYLE_COLORMAP,JASHist2DHistogramStyle.COLORMAP_WARM));
			map.add(addButton("Cool",'C',JASHist2DHistogramStyle.STYLE_COLORMAP,JASHist2DHistogramStyle.COLORMAP_COOL));
			map.add(addButton("Thermal",'T',JASHist2DHistogramStyle.STYLE_COLORMAP,JASHist2DHistogramStyle.COLORMAP_THERMAL));
			map.add(addButton("Rainbow",'B',JASHist2DHistogramStyle.STYLE_COLORMAP,JASHist2DHistogramStyle.COLORMAP_RAINBOW));
			map.add(addButton("Gray Scale",'G',JASHist2DHistogramStyle.STYLE_COLORMAP,JASHist2DHistogramStyle.COLORMAP_GRAYSCALE));
         final JCheckBoxMenuItem log = new JCheckBoxMenuItem("Logarithmic Z Axis");
         log.setMnemonic('Z');
         final JASHist2DHistogramStyle style = (JASHist2DHistogramStyle) data.getStyle();
         log.setState(style.getLogZ());
         log.addActionListener(new ActionListener()
         {
            public void actionPerformed(ActionEvent e)
            {
               style.setLogZ(log.getState());
            }
         });
         StyleMenu.this.add(log);
         init();
		}
		private JRadioButtonMenuItem addButton(String name, char mnemonic, final int iStyle, final int cStyle)
		{
			JRadioButtonMenuItem item = new StyleButton(name,iStyle,cStyle);
			item.setMnemonic(mnemonic);
			group.add(item);
			return item;
		}
		private void init()
		{
			int iStyle = -1;
			int cStyle = -1;

			JASHist2DHistogramStyle style = (JASHist2DHistogramStyle) data.getStyle();
			iStyle = style.getHistStyle();
			cStyle = style.getColorMapScheme();

			Enumeration e = group.getElements();
			while (e.hasMoreElements())
			{
				StyleButton b = (StyleButton) e.nextElement();
				b.setStyle(iStyle,cStyle);
			}
		}
		ButtonGroup group = new ButtonGroup();
		JASHistData data;
		final class StyleButton extends JRadioButtonMenuItem
		{
			StyleButton(String name, int iStyle, int cStyle)
			{
				super(name);
				this.iStyle = iStyle;
				this.cStyle = cStyle;
			}
			public void fireActionPerformed(ActionEvent e)
			{
				JASHist2DHistogramStyle style = (JASHist2DHistogramStyle) data.getStyle();
				style.setHistStyle(iStyle);
				if (cStyle >= 0) style.setColorMapScheme(cStyle);
			}
			void setStyle(int iStyle, int cStyle)
			{
				this.setSelected(this.iStyle == iStyle && (this.cStyle == -1 || this.cStyle == cStyle));
			}
			private int iStyle;
			private int cStyle;
		}
	}
}
