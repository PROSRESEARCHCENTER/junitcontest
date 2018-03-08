package jas.hist;

import jas.plot.Axis;
import jas.plot.DataArea;
import jas.plot.DataAreaLayout;
import jas.plot.DoubleAxis;
import jas.plot.EditableLabel;

import java.awt.Component;
import java.util.Enumeration;
import java.util.NoSuchElementException;

import javax.swing.JPopupMenu;

/**
 * The Data Manager is used to control overlaying of multiple data sets 
 * on a single plot. There are many different subclasses of data manager
 * to handle different types of plots and data. The Data Manager is also 
 * responsible for coordinating the update of displays when the data source
 * changes.
 * @author Tony Johnson
 */
abstract class DataManager
{
	DataManager(JASHist plot, DataArea da)
	{
		this.plot = plot;
		this.da = da;
		da.setSpecialComponent(null);
		this.xm = (ManagedAxis) da.getXAxis();
		this.ym[0] = (ManagedAxis) da.getYAxis();
	}
	JASHist getPlot()
	{
		return plot;
	}
	abstract void init();
	abstract JASHistData add(DataSource data);
	abstract void remove(JASHistData data);
	abstract void requestShow(JASHistData data);
	abstract void requestHide(JASHistData data);
	abstract void invalidate();
	abstract boolean isRealized();
	abstract void setRealized(boolean b);
	abstract void XAxisUpdated();
	abstract void computeYAxisRange();
	abstract void computeXAxisRange();
	abstract void update(HistogramUpdate update, JASHistData data);
	abstract int numberOfDataSources();
	abstract Enumeration getDataSources();
	abstract void destroy(); 
	abstract void modifyPopupMenu(JPopupMenu menu, Component source);
	abstract protected void showLegend();
	// For the below two functions, the argument JASHistData will always be either
	// JASHist1DHistogramData or JASHistScatterPlotData (but never JASHist1DFunctionData)
	// When the axis or style changes for a function, update(JASHist1DFunctionData) is called.
	abstract void styleUpdate(JASHistData source);
	abstract void axisChanged(JASHistData source);
	public EditableLabel getLabel(Axis m)
	{
		return da.getLabel(m);
	}
	public void setLabel(Axis m, EditableLabel l)
	{
		da.setLabel(m,l);
	}
	JASHistAxis getXAxis()
	{
		return xm;
	}
	JASHistAxis[] getYAxes()
	{
		return ym;
	}
	JASHistAxis getYAxis(int index)
	{
		if (index >= ym.length) throw new IllegalArgumentException("Y axis index out of range");
		if (ym[index] == null) createYAxis(index);
		return ym[index];
	}
	protected void createYAxis(int index)
	{
		ym[index] = new ManagedAxis(this,Axis.VERTICAL,true);
		DoubleAxis a = (DoubleAxis) ym[index].getType();
		a.setUseSuggestedRange(true);
		ym[index].setOnLeftSide(false);
		da.add(ym[index],DataAreaLayout.Y_AXIS_RIGHT);
		da.revalidate();
	}
        protected void destroyYAxis(int index)
        {
           if (ym[index] != null) da.remove(ym[index]);
           da.revalidate();
        }
	protected final static Enumeration nullEnumeration = new NullEnumeration();
	final private static class NullEnumeration implements Enumeration
	{
		public boolean hasMoreElements()
		{
			return false;
		}
		public Object nextElement()
		{
			throw new NoSuchElementException();
		}
	}
	protected ManagedAxis xm;
	protected ManagedAxis[] ym = new ManagedAxis[2];
	final protected JASHist plot;
	final protected DataArea da;
}
