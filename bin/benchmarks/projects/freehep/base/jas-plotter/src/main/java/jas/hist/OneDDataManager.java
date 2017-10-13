package jas.hist;

import jas.plot.DataArea;
import jas.plot.Legend;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Enumeration;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

abstract class OneDDataManager
	extends SliceableDataManager
{
	OneDDataManager(JASHist plot, DataArea da, Legend l, StatisticsBlock stats)
	{
		super(plot, da,l, stats);
	}
	protected transient double xLow;
	protected transient double xHigh;

	JASHistData add(DataSource ds)
	{	
		JASHistData dw = new JASHist1DHistogramData(this,ds);
		data.addElement(dw); 
		return dw;
	}

	void modifyPopupMenu(final JPopupMenu menu, final Component source)
	{
		final JMenuItem outline = new HistMenuItem("Show Histogram Bars", "ShowHistogramBars");
		final JMenuItem fill = new HistMenuItem("Fill Histogram Bars", "HistogramFill");
		final JMenuItem error = new HistMenuItem("Show Error Bars", "ShowErrorBars");
		final JMenuItem lines = new HistMenuItem("Draw Lines Between Points", "ShowLinesBetweenPoints");
		final JMenuItem symbols = new HistMenuItem("Show Data Points", "ShowDataPoints");

		if (menu.getComponentCount() > 0) menu.addSeparator();
		menu.add(outline);
		menu.add(fill);
		menu.add(error);
		menu.add(lines);
		menu.add(symbols);

		if (fill.isEnabled()) fill.setEnabled(outline.isSelected());
	}

	final protected void doUpdate()
	{
		if (isInit)
		{
			if (xm.needsAttention())
			{
				computeXAxisRange();
				XAxisUpdated();
			}
			computeYAxisRange();
			stats.repaint();
			da.repaint();
		}
	}

	public void update(final HistogramUpdate hu, final JASHistData data)
	{
		// Danger: likely to be run in a different thread
		//if (hu.isReset())
				//parent.resetNumberOfBins(this);
		int index = data.getYAxis();

		if (hu.isRangeUpdate() || hu.isReset()) xm.setAttentionNeeded();
		else ym[index].setAttentionNeeded();

		if (hu.isFinalUpdate() || hu.isReset())
		{
			SwingUtilities.invokeLater(this);
			if (hu.isReset())
			// When we get a reset we have to abandon all assumptions
			// about the data.  We may have to create a new data manager
			// (because a reset is sent when the partition changes)
			// and inform JASHist somehow, but this has not yet been
			// implemented.  For now, we will make sure that the
			// isFixed flag is correct in the x axis managed axis
			// because that is currently the only instance where
			// a reset is sent.  We also check if the number of bins is set.
			// We also set rangeAutomatic.
			{
//				SwingUtilities.invokeLater(new Runnable()
//				{
//					final public void run()
//					{
						//final boolean isRebinnable = ((Rebinnable1DHistogramData) data.getDataSource()).isRebinnable();
                                                DataSource dataSource = data.getDataSource();
                                                final boolean isRebinnable = (dataSource instanceof Rebinnable1DHistogramData) ? ((Rebinnable1DHistogramData) dataSource).isRebinnable() : false;
						xm.setFixed(!isRebinnable);
						xm.setRangeAutomatic(isRebinnable);
//					}
//				});
			}
		}
		else timer.start();
	}

	final void axisChanged(final JASHistData data)
	{
		int index = data.getYAxis();
		if (ym[index] == null) createYAxis(index);
		else ym[index].setAttentionNeeded(); // BUG: What about the OLD y-axis, doesnt it need attention?
		SwingUtilities.invokeLater(this);
	}

	void styleUpdate(JASHistData data)
	{
		int index = data.getYAxis();
		if (ym[index] == null) createYAxis(index);
		else ym[index].setAttentionNeeded();// BUG: What about the OLD y-axis, doesnt it need attention?
		SwingUtilities.invokeLater(this);
	}
	private final class HistMenuItem extends JCheckBoxMenuItem
	{
		HistMenuItem(final String menuLabel, final String methodRoot)
		{
			super(menuLabel);

			if (numberOfDataSources() > 0)
			{
				try
				{
					m_methodRoot = methodRoot;
					final Method get = JASHist1DHistogramStyle.class.getMethod("get".concat(methodRoot), new Class[0]);
					boolean selected = true;
					final Enumeration e = getDataSources();
					final Object[] emptyList = new Object[0];
					while (selected && e.hasMoreElements())
						selected = ((Boolean) get.invoke(((JASHistData) e.nextElement()).getStyle(), emptyList)).booleanValue();
					setSelected(selected);
				}
				catch (NoSuchMethodException x)
				{
					setEnabled(false);
					setSelected(false);
				}
				catch (IllegalAccessException x)
				{
					setEnabled(false);
					setSelected(false);
				}
				catch (InvocationTargetException x)
				{
					setEnabled(false);
					setSelected(false);
				}
			}
			else
			{
				setEnabled(false);
				setSelected(false);
			}
		}
		protected void fireActionPerformed(final ActionEvent event)
		{
			final Class[] booleanList = { boolean.class };
			try
			{
				final Method set = JASHist1DHistogramStyle.class.getMethod("set".concat(m_methodRoot), booleanList);
				final Object[] arg = { new Boolean(isSelected()) };
				final Enumeration enumer = getDataSources();
				while (enumer.hasMoreElements())
				{
					try
					{
						set.invoke(((JASHistData) enumer.nextElement()).getStyle(), arg);
					}
					catch (final Exception exception)
					{
					}
				}
			}
			catch (final Exception exception)
			{
			}
		}
		private String m_methodRoot;
	}
}

