package jas.hist;

import jas.plot.Axis;
import jas.plot.DataArea;
import jas.plot.EditableLabel;

import java.awt.Component;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Enumeration;

import javax.swing.JPopupMenu;

// This is the data manager that is installed when there is no data, hence it
// doesn't actally manage any data at all!

final class DefaultDataManager extends DataManager
{
	DefaultDataManager(JASHist plot, DataArea da)
	{	
		super(plot, da);
		xm.setDataManager(this, false);
		ym[0].setDataManager(this, false);
	}
	void init()
	{
	}
	JASHistAxis getXAxis()
	{
		return (JASHistAxis) da.getXAxis();
	}
	JASHistAxis getYAxis(int index)
	{
		return (JASHistAxis) da.getYAxis(index);
	}
	JASHistData add(DataSource data)
	{
		return null;
	}
	void remove(JASHistData data)
	{
	}
	void requestShow(JASHistData data)
	{
	}
	void requestHide(JASHistData data)
	{
	}
	void invalidate()
	{
	}
	boolean isRealized()
	{
		return false;
	}
	void XAxisUpdated()
	{
	}
	void computeYAxisRange()
	{
	}
	void computeXAxisRange()
	{
	}
	void update(HistogramUpdate update, JASHistData data)
	{
	}
	int numberOfDataSources()
	{
		return 0;
	}
	Enumeration getDataSources()
	{
		return nullEnumeration;
	}
	void destroy()
	{
	}
	public EditableLabel getLabel(Axis m)
	{
		return da.getLabel(m);
	}
	public void setLabel(Axis m, EditableLabel l)
	{
		da.setLabel(m,l);
	}
	void modifyPopupMenu(JPopupMenu menu, Component source)
	{
	}
	protected void showLegend()
	{
	}
	void styleUpdate(JASHistData source)
	{
	}
	void axisChanged(JASHistData source)
	{
	}
	private void writeObject(final ObjectOutputStream out) throws IOException
	{
		out.defaultWriteObject();
//		out.writeObject(da.getXAxis());
//		out.writeObject(da.getYAxis(0));
//		out.writeObject(da.getYAxis(1));
	}
	private void readObject(final ObjectInputStream in) throws ClassNotFoundException, IOException
	{
		in.defaultReadObject();
//		da.add((Axis) in.readObject(), DataAreaLayout.X_AXIS);
//		da.add((Axis) in.readObject(), DataAreaLayout.Y_AXIS_LEFT);
//		da.add((Axis) in.readObject(), DataAreaLayout.Y_AXIS_RIGHT);
	}
        
        void setRealized(boolean b) {
        }
        
}
