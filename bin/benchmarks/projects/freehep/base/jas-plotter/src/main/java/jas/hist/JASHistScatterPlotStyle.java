package jas.hist;

import java.awt.Color;
import java.awt.Graphics;

public final class JASHistScatterPlotStyle extends JASHist2DHistogramStyle
{
        final public static int SYMBOL_BOX        = 0;
	final public static int SYMBOL_TRIANGLE   = 1;
	final public static int SYMBOL_DIAMOND    = 2;
	final public static int SYMBOL_STAR       = 3;
	final public static int SYMBOL_VERT_LINE  = 4;
	final public static int SYMBOL_HORIZ_LINE = 5;
	final public static int SYMBOL_CROSS      = 6;
	final public static int SYMBOL_SQUARE     = 7; 


	static final Color[] lineColors = 
	{ 
		Color.blue, Color.red, Color.darkGray,Color.magenta,
		Color.yellow, Color.green, Color.orange, Color.cyan,  
	};
	static int n = 0;

	public JASHistScatterPlotStyle()
	{
		m_displayAsScatterPlot = false;
		m_dataPointSize = 3;
		m_dataPointStyle = n;
		m_dataPointColor = lineColors[n];
		n++;
		if (n == lineColors.length) n = 0;
	}
	public boolean getDisplayAsScatterPlot()
	{
		return m_displayAsScatterPlot;
	}
	public void setDisplayAsScatterPlot(boolean value)
	{
		m_displayAsScatterPlot = value;
		changeNotify();
	}
	public int getDataPointStyle() 
	{
		return m_dataPointStyle;
	}
	public void setDataPointStyle(int nNewValue) 
	{	
		m_dataPointStyle = nNewValue;
		changeNotify();
	}
	public int getDataPointSize() 
	{
		return m_dataPointSize;
	}

	public void setDataPointSize(int newValue) 
	{
		m_dataPointSize = newValue;
		changeNotify();
	}
	public Color getDataPointColor() 
	{
		return m_dataPointColor;
	}

	public void setDataPointColor(Color nNewValue) 
	{
		m_dataPointColor = nNewValue;
		changeNotify();
	}
	void drawLegend(Graphics g, int x, int y, int width, int height)
	{
	}
	private boolean m_displayAsScatterPlot;
	private int m_dataPointSize;
	private Color m_dataPointColor;
	private int m_dataPointStyle;
}
