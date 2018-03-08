package jas.hist;
import jas.plot.EditableLabel;

import java.awt.Color;
import java.awt.Font;

public interface JASHistAxis
{
	public static final int LEFT = 1;
	public static final int RIGHT = 2;

	public static final int DOUBLE = Rebinnable1DHistogramData.DOUBLE;
	public static final int STRING = Rebinnable1DHistogramData.STRING;
	public static final int DATE = Rebinnable1DHistogramData.DATE;

	public boolean isVertical();
	public boolean isLogarithmic();
	public void setLogarithmic(boolean value);
	public boolean isShowing();
	public void setShowing(boolean value);
	public String getLabel();
	public void setLabel(String s);
	public EditableLabel getLabelObject();
	public void setLabelObject(EditableLabel p_newLabel);	
	public double getMin();
	public void setMin(double d);
	public double getMax();
	public void setMax(double d);
	public void setRange(double min, double max);
	public Object getMinObject();
	public Object getMaxObject();
	public void setMinObject(Object value);
	public void setMaxObject(Object value);
	public boolean getRangeAutomatic();
	public void setRangeAutomatic(boolean b);
	public boolean getAllowSuppressedZero();
	public void setAllowSuppressedZero(boolean b);
	public boolean isBinned();
	public boolean isFixed();
	public int getBins();
	public void setBins(int value);
	public double getBinWidth();
	public void setBinWidth(double value);
	public int getLabelPosition();
	public void setLabelPosition(int pos);
	public int getPosition();
	public void  setPosition(int value);
	public int getAxisType();
	public void setAxisType(int value);
	public boolean getShowOverflows();
	public void setShowOverflows(boolean value);
	public Font getFont();
	/** 
	 * The font used for the axis values
	 */
	public void setFont(Font value);
	public Color getAxisColor();
	public float getAxisWidth();
	public void setAxisWidth(float value);
	/**
	 * The color used for the axis itself (and markers)
	 * By default the foreground color of the JASHist is used
	 */
	public void setAxisColor(Color c);
	public Color getTextColor();
	/**
	 * The color used for the axis values.
	 * By default the foreground color of the JASHist is used
	 */
	public void setTextColor(Color c);
}
