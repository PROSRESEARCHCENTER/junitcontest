package jas.hist;

import jas.plot.Axis;
import jas.plot.AxisType;
import jas.plot.DateAxis;
import jas.plot.DoubleAxis;
import jas.plot.EditableLabel;
import jas.plot.StringAxis;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Date;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

// This has to be public so that the properties dialog can use reflection on it??? 
// (otherwise get IllegalAccessException)

public final class ManagedAxis extends Axis implements JASHistAxis, Externalizable
{
	ManagedAxis(int orientation)
	{
		super(orientation);
		this.dataManager = null;
		this.binned = false;
	}
	ManagedAxis(DataManager dm, int orientation, boolean binned)
	{
		super(orientation);
		this.dataManager = dm;
		this.binned = binned;
	}
	// for deserialization/deserialization only do not call
	public ManagedAxis()
	{
	}
	void setDataManager(DataManager dm, boolean binned)
	{
		this.dataManager = dm;
		this.binned = binned;
	}
	void setDataManager(DataManager dm, boolean binned, AxisType t)
	{
		this.dataManager = dm;
		this.binned = binned;
		setType(t);
	}
	public void writeExternal(ObjectOutput out) throws IOException
	{
		out.writeInt(bins);
		out.writeBoolean(rangeAutomatic);
		if (!rangeAutomatic)
		{
			out.writeDouble(getMin());
			out.writeDouble(getMax());
		}
		out.writeBoolean(allowSuppressedZero);
		out.writeBoolean(fixed);
		out.writeBoolean(binned);
		out.writeInt(position);

		final AxisType type = getType();
		out.writeInt(getAxisOrientation());
		out.writeBoolean(getOnLeftSide());
		out.writeObject(type);
		out.writeObject(dataManager);

		// THINGS THAT SHOULD BE INCLUDED LATER (here and in readObject):
		//  - axis colors (i.e., minorTickMarkColor, axisLineColor, textColor, etc.)
		//    (no point in doing this one now because they are always black)
		//  - whether is visible
		//    (no point in doing this now because can't change whether is visible)
	}
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException
	{
		bins = in.readInt();
		rangeAutomatic = in.readBoolean();
		if (!rangeAutomatic)
		{
			setMin(in.readDouble());
			setMax(in.readDouble());
		}
		else
			attentionNeeded();
		allowSuppressedZero = in.readBoolean();
		fixed = in.readBoolean();
		binned = in.readBoolean();
		position = in.readInt();

		setAxisOrientation(in.readInt());
		setOnLeftSide(in.readBoolean());
		setType((AxisType) in.readObject());
		dataManager = (DataManager) in.readObject();
	}
	public boolean isVertical()
	{
		return getAxisOrientation() == Axis.VERTICAL;
	}
	public boolean isLogarithmic()
	{
		AxisType type = getType();
		if (type instanceof DoubleAxis) 
		{
			return ((DoubleAxis) type).isLogarithmic();
		}
		return false;
	}
	public void setLogarithmic(boolean value)
	{
		AxisType type = getType();
		if (type instanceof DoubleAxis) 
		{
			((DoubleAxis) type).setLogarithmic(value);
			attentionNeeded();
		}
	}
	public boolean isShowing()
	{
		// TODO: Not supported yet by Axis
		return true;
	}
	public void setShowing(boolean value)
	{
		// TODO: Not supported yet by Axis
	}
	/**
	 * @return the EditableLabel itself, not just the text
	 * @see #getLabel()
	 */
	public EditableLabel getLabelObject()
	{
		if (dataManager == null) return null;
		EditableLabel label = dataManager.getLabel(this);
		if (label == null) return null;
		return label;
	}
	/**
	 * @return the label text
	 * @see #getLabelObject()
	 */
	public String getLabel()
	{
		EditableLabel label = dataManager.getLabel(this);
		return label==null ? "" : label.getText();
	}
	/**
	 * sets the label to be the EditableLabel passed in
	 * @param p_newLabel the new label
	 */
	public void setLabelObject(EditableLabel p_newLabel)
	{
		dataManager.setLabel(this, p_newLabel);
	}
	/**
	 * sets the label text, creating the label if needed
	 * @param s the new text of the label
	 */
	public void setLabel(String s)
	{
		EditableLabel label = dataManager.getLabel(this);
		if (label == null) 
		{
			label = new EditableLabel(s,"Axis Label",JLabel.LEFT);
			dataManager.setLabel(this, label);
		}
		else label.setText(s);
		revalidate(); // Attract the attention of the layout manager!
	}
	public double getMin()
	{
//		getParent().validate();
		AxisType type = getType();
		if (type instanceof DoubleAxis) 
		{
			return ((DoubleAxis) type).getPlotMin();
		}
		else if (type instanceof DateAxis)
		{
			return ((DateAxis) type).getAxisMin()/1000.;
		}
		throw new RuntimeException("getMin undefined for this axis type");
	}
	public void setMin(double d)
	{
		AxisType type = getType();
		if (type instanceof DoubleAxis) 
		{
			rangeAutomatic = false;
			((DoubleAxis) type).setMin(d);
			attentionNeeded();
		}
		else if (type instanceof DateAxis)
		{
			rangeAutomatic = false;
			((DateAxis) type).setMin((long) (d*1000));
			attentionNeeded();
		}
		else throw new RuntimeException("setMin undefined for this axis type");
	}
	public double getMax()
	{
		AxisType type = getType();
		if (type instanceof DoubleAxis) 
		{
			return ((DoubleAxis) type).getPlotMax();
		}
		else if (type instanceof DateAxis)
		{
			return ((DateAxis) type).getAxisMax()/1000.;
		}
		throw new RuntimeException("getMax undefined for this axis type");
	}
	public void setMax(double d)
	{
		AxisType type = getType();
		if (type instanceof DoubleAxis) 
		{
			rangeAutomatic = false;
			((DoubleAxis) type).setMax(d);
			attentionNeeded();
		}
		else if (type instanceof DateAxis)
		{
			rangeAutomatic = false;
			((DateAxis) type).setMax((long) (d*1000));
			invalidate();
			attentionNeeded();
		}
		else throw new RuntimeException("setMax undefined for this axis type");
	}
	public void setRange(double min, double max)
	{
		setMin(min);
		setMax(max);
	}
	public Object getMinObject()
	{
		if (getAxisType() == DATE) return new Date((long) (getMin()*1000));
		else return new Double(getMin());
	}
	public Object getMaxObject()
	{
		if (getAxisType() == DATE) return new Date((long) (getMax()*1000));
		else return new Double(getMax());
	}
	public void setMinObject(Object value)
	{
		try
		{
			double min;
			if (getAxisType() == DATE) min = ((Date) value).getTime()/1000; 
			else min = ((Double) value).doubleValue();
			setMin(min);
//			attentionNeeded();
//			invalidate();
		}
		catch (ClassCastException x)
		{
			throw new IllegalArgumentException("Argument to setMinObject is of wrong type");
		}
	}
	public void setMaxObject(Object value)
	{
		try
		{
			double max;
			if (getAxisType() == DATE) max = ((Date) value).getTime()/1000; 
			else max = ((Double) value).doubleValue();
			setMax(max);
//			attentionNeeded();
//			invalidate();
		}
		catch (ClassCastException x)
		{
			throw new IllegalArgumentException("Argument to setMaxObject is of wrong type");
		}
	}
	public boolean getRangeAutomatic()
	{
		return rangeAutomatic;
	}
	public void setRangeAutomatic(boolean b)
	{
		rangeAutomatic = b;
		attentionNeeded();
	}
	public boolean getAllowSuppressedZero()
	{
		return allowSuppressedZero;
	}
	public void setAllowSuppressedZero(boolean b)
	{
		allowSuppressedZero = b;
		attentionNeeded();
	}
	public boolean isBinned()
	{
		return binned;
	}
	public void setBinned(boolean value)
	{
		binned = value;
	}
	public boolean isFixed()
	{
		return fixed;
	}
	public void setFixed(boolean value)
	{
		fixed = value;
	}
	public int getBins()
	{
		return bins;
	}
	public void setBins(int value)
	{
		bins = value;
		attentionNeeded();
	}
	public double getBinWidth()
	{
		if (!binned) return 0;
		double range = getMax() - getMin();
		return range/bins;
	}
	public void setBinWidth(double value)
	{
		double range =  getMax() - getMin();
		bins = (int) Math.max(1,range/value);
		attentionNeeded();
	}
	public boolean getShowOverflows()
	{
		return showOverflows;
	}
	public void setShowOverflows(boolean value)
	{
		showOverflows = value;
		attentionNeeded();
	}
	public int getLabelPosition()
	{
		return 0;
	}
	public void setLabelPosition(int pos)
	{
		//final int positions[] = 
		//{
		//	JCLegend.EAST, JCLegend.WEST, JCLegend.NORTHEAST, 
		//	JCLegend.SOUTHEAST, JCLegend.NORTHWEST, JCLegend.SOUTHEAST 
		//}; 
		//JCAxisTitle t = yAxis.getTitle();
		//if (t != null) t.setPlacement(positions[pos]);
	}
	public int getPosition()
	{
		return position; 
	}
	public void  setPosition(int value)
	{
		//if (!isVertical()) throw new IllegalArgumentException("Cannot set position of X axis");
		//if      (value == LEFT ) axis.setPlacement(JCAxis.MIN);
		//else if (value == RIGHT) axis.setPlacement(JCAxis.MAX);
		//else throw new IllegalArgumentException("Illegal argument to setPosition");

		position = value;
	}
	public int getAxisType()
	{
		AxisType type = getType();
		if (type instanceof DateAxis) return DATE;
		if (type instanceof StringAxis) return STRING;
		return DOUBLE;
	}
	public void setAxisType(int value)
	{
		if (value != getAxisType())
		{
			if (value == DATE)
			{
				setType(new DateAxis());
			}
			else if (value == STRING)
			{
				setType(new StringAxis());
			}
			else
			{
				setType(new DoubleAxis());
			}
		}
	}
	/**
	 * attention needed is called by ManagedAxis when it wants to attract the
	 * attention of the DataManager, e.g. after the user updates some property.
	 */
	private void attentionNeeded()
	{
		boolean requestedAttention = needsAttention;
		needsAttention = true;
		if (!requestedAttention && dataManager != null) dataManager.invalidate(); 
	}
	/**
	 * set attention needed can be called by the DataManager to remind itself
	 * it needs to work on the axis
	 */
	void setAttentionNeeded()
	{
		needsAttention = true;
	}
	/**
	 * called by the DataManager when it has updated itself to take into account
	 * changes to the axis. Resets the needsAttention flag.
	 */
	void payingAttention()
	{
		needsAttention = false;
	}
	/**
	 * return true if attnetion is needed
	 */
	boolean needsAttention()
	{
		return needsAttention;
	}
	public void modifyPopupMenu(final JPopupMenu menu, final Component source)
	{
		if (dataManager != null && !(dataManager instanceof DefaultDataManager))
		{
			super.modifyPopupMenu(menu,source);
			if (getAxisType() != JASHistAxis.STRING)
			// BUG: should have a better way of testing whether these items are appropriate
			{
				if (getAxisType() != JASHistAxis.DATE)
				// BUG: should have a better way of testing whether this item is appropriate
				{
					menu.add(new JCheckBoxMenuItem("Logarithmic", isLogarithmic())
					{
						final protected void fireActionPerformed(final ActionEvent e)
						{
							setLogarithmic(isSelected());
						}
					});
				}
				menu.add(new JCheckBoxMenuItem("Range Automatic", getRangeAutomatic())
				{
					final protected void fireActionPerformed(final ActionEvent e)
					{
						setRangeAutomatic(isSelected());
					}
				});
				if (getAxisType() != JASHistAxis.DATE)
				// BUG: should have a better way of testing whether this item is appropriate
				{
					final JCheckBoxMenuItem suppZero = new JCheckBoxMenuItem("Suppressed Zeros", getAllowSuppressedZero())
					{
						final protected void fireActionPerformed(final ActionEvent e)
						{
							setAllowSuppressedZero(isSelected());
						}
					};
					menu.add(suppZero);
					suppZero.setEnabled(getRangeAutomatic());
				}
			}
			menu.add(new JMenuItem("Axis Properties...")
			{
				final protected void fireActionPerformed(final ActionEvent e)
				{
					dataManager.getPlot().showProperties(
						getAxisOrientation() == Axis.VERTICAL ?
						(getOnLeftSide() ? JASHistPropertyDialog.Y_AXIS_LEFT :
						JASHistPropertyDialog.Y_AXIS_RIGHT) :
						JASHistPropertyDialog.X_AXIS);
				}
			});
		}
	}

	private int bins;
	private boolean rangeAutomatic = true;
	private boolean allowSuppressedZero = true;
	private boolean showOverflows = false; 
	
	private boolean needsAttention = true;
	private boolean fixed = false;
	private boolean binned;
	private int position;
	private DataManager dataManager;
}
