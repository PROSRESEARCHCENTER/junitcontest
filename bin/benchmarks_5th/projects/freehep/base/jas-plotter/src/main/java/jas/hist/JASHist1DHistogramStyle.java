package jas.hist;

import jas.plot.PlotGraphics;

import java.awt.Color;
import java.io.IOException;
import java.io.Serializable;

/**
 *  Used to control the display style of 1D Histograms.
 */
public class JASHist1DHistogramStyle extends JASHistStyle implements Serializable
{
   final public static int SYMBOL_DOT        = PlotGraphics.SYMBOL_DOT;
   final public static int SYMBOL_BOX        = PlotGraphics.SYMBOL_BOX;
   final public static int SYMBOL_TRIANGLE   = PlotGraphics.SYMBOL_TRIANGLE;
   final public static int SYMBOL_DIAMOND    = PlotGraphics.SYMBOL_DIAMOND;
   final public static int SYMBOL_STAR       = PlotGraphics.SYMBOL_STAR;
   final public static int SYMBOL_VERT_LINE  = PlotGraphics.SYMBOL_VERT_LINE;
   final public static int SYMBOL_HORIZ_LINE = PlotGraphics.SYMBOL_HORIZ_LINE;
   final public static int SYMBOL_CROSS      = PlotGraphics.SYMBOL_CROSS;
   final public static int SYMBOL_CIRCLE     = PlotGraphics.SYMBOL_CIRCLE;
   final public static int SYMBOL_SQUARE     = PlotGraphics.SYMBOL_SQUARE;
   
   static final Color[] lineColors =
   {
      Color.blue, Color.red, Color.magenta,
      Color.green, Color.orange, Color.cyan,
   };
   static final int[] pointStyles =
   {
      SYMBOL_DOT, SYMBOL_BOX, SYMBOL_TRIANGLE, SYMBOL_DIAMOND,
      SYMBOL_STAR, SYMBOL_CROSS, SYMBOL_CIRCLE, SYMBOL_SQUARE
   };
   static int n = 0;
   static int np = 0;
   static final long serialVersionUID = 7779996364086801435L;
   
   public JASHist1DHistogramStyle()
   {
      initTransientData();
      
      m_showDataPoints = false;
      m_showErrorBars = true;
      m_histogramFill = true;
      m_showHistogramBars = true;
      m_showLinesBetweenPoints = false;
      
      m_histogramBarLineWidth = 1.0f;
      m_linesBetweenPointsWidth = 1.0f;
      m_errorBarWidth = 1.0f;
      
      m_dataPointSize = 6;
      m_dataPointStyle = pointStyles[np];
      
      m_histogramBarColor = lineColors[n];
      m_histogramBarLineColor = Color.BLACK;
      m_errorBarColor = lineColors[n];
      m_errorBarColorIsDefault = true;
      m_dataPointColor = lineColors[n];
      m_lineColor = lineColors[n];
      n++;
      np++;
      if (n == lineColors.length) n = 0;
      if (np == pointStyles.length) np = 0;
   }
   public boolean getShowHistogramBars()
   {
      return m_showHistogramBars;
   }
   
   public void setShowHistogramBars(boolean bNewValue)
   {
      m_showHistogramBars = bNewValue;
      //		if (!m_showHistogramBars) m_histogramFill = false;
      changeNotify();
   }
   
   public boolean getShowErrorBars()
   {
      return m_showErrorBars;
   }
   
   public void setShowErrorBars(boolean bNewValue)
   {
      m_showErrorBars = bNewValue;
      changeNotify();
   }
   
   public boolean getShowDataPoints()
   {
      return m_showDataPoints;
   }
   
   /**
    * Controls whether markers are drawn at each data point.
    * @param bNewValue true to show markers
    * @see #setDataPointStyle(int)
    * @see #setDataPointSize(int)
    */
   public void setShowDataPoints(boolean bNewValue)
   {
      m_showDataPoints = bNewValue;
      changeNotify();
   }
   
   public boolean getShowLinesBetweenPoints()
   {
      return m_showLinesBetweenPoints;
   }
   /**
    * Controls whether (straight) lines are drawn between data points
    * @param bNewValue true to show lines
    */
   public void setShowLinesBetweenPoints(boolean bNewValue)
   {
      m_showLinesBetweenPoints = bNewValue;
      changeNotify();
   }
   public int getLinesBetweenPointsStyle()
   {
      return m_linesBetweenPointsStyle;
   }
   public void setLinesBetweenPointsStyle(int style)
   {
      m_linesBetweenPointsStyle = style;
      changeNotify();
   }
   public float getLinesBetweenPointsWidth()
   {
      return m_linesBetweenPointsWidth != 0 ? m_linesBetweenPointsWidth : (float)0.0001;
   }
   public void setLinesBetweenPointsWidth(float width)
   {
      this.m_linesBetweenPointsWidth = width;
      changeNotify();
   }
   public int getDataPointStyle()
   {
      return m_dataPointStyle;
   }
   /**
    * Set the style (shape) of the data points.
    * The legal values to pass in are:
    * <ul>
    * <li>SYMBOL_DOT
    * <li>SYMBOL_BOX
    * <li>SYMBOL_TRIANGLE
    * <li>SYMBOL_DIAMOND
    * <li>SYMBOL_STAR
    * <li>SYMBOL_VERT_LINE
    * <li>SYMBOL_HORIZ_LINE
    * <li>SYMBOL_CROSS
    * <li>SYMBOL_CIRCLE
    * <li>SYMBOL_SQUARE
    * </ul>
    * @param newValue The data point style to use
    */
   public void setDataPointStyle(int newValue)
   {
      m_dataPointStyle = newValue;
      changeNotify();
   }
   
   public int getDataPointSize()
   {
      return m_dataPointSize;
   }
   /**
    * Set the size of data points
    * @param newValue The new size for data points (in pixels)
    */
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
   
   public Color getHistogramBarColor()
   {
      return m_histogramBarColor;
   }
   
   public void setHistogramBarColor(Color nNewValue)
   {
      m_histogramBarColor = nNewValue;
      changeNotify();
   }
   
   
   public Color getHistogramBarLineColor()
   {
      //This is to keep backward compatibility with the JAS2 code that does
      //not make use of this method, i.e. the color of the histogram bars outline
      //is always blak when the bar is filled and the fill color when the bars
      //are not filled.
      if ( ( ! histogramBarLineColorChanged ) && ( ! getHistogramFill() ) )
         return getHistogramBarColor();
      return m_histogramBarLineColor;
   }
   
   public void setHistogramBarLineColor(Color nNewValue)
   {
      histogramBarLineColorChanged = true;
      m_histogramBarLineColor = nNewValue;
      changeNotify();
   }
   public int getHistogramBarLineStyle()
   {
      return m_histogramBarLineStyle;
   }
   public void setHistogramBarLineStyle(int style)
   {
      m_histogramBarLineStyle = style;
      changeNotify();
   }
   public float getHistogramBarLineWidth()
   {
      return m_histogramBarLineWidth != 0 ? m_histogramBarLineWidth : (float)0.0001;
   }
   public void setHistogramBarLineWidth(float width)
   {
      m_histogramBarLineWidth = width;
      changeNotify();
   }   
   
   public Color getLineColor()
   {
      return m_lineColor;
   }
   
   public void setLineColor(Color nNewValue)
   {
      m_lineColor = nNewValue;
      changeNotify();
   }
   
   public Color getErrorBarColor()
   {
      if (m_histogramFill && m_errorBarColorIsDefault) return Color.black;
      return m_errorBarColor;
   }
   
   public void setErrorBarColor(Color nNewValue)
   {
      m_errorBarColor = nNewValue;
      m_errorBarColorIsDefault = false;
      changeNotify();
   }
   
   public int getErrorBarStyle()
   {
      return m_errorBarStyle;
   }
   public void setErrorBarStyle(int style)
   {
      m_errorBarStyle = style;
      changeNotify();
   }
   
   // Error Bar Decoration: 
   //    less than 0 - automatic
   //              0 - no decoration
   // greater than 0 - fraction of the bin width
   public float getErrorBarDecoration()
   {
      return m_errorBarDecoration;
   }
   public void setErrorBarDecoration(float f)
   {
      m_errorBarDecoration = f;
      changeNotify();
   }
   public float getErrorBarWidth()
   {
      return m_errorBarWidth != 0 ? m_errorBarWidth : (float)0.0001;
   }
   public void setErrorBarWidth(float width)
   {
      m_errorBarWidth = width;
      changeNotify();
   }   
   public boolean getHistogramFill()
   {
      return m_histogramFill;
   }
   
   public void setHistogramFill(boolean bNewValue)
   {
      m_histogramFill = bNewValue;
      changeNotify();
   }
   protected void changeNotify()
   {
      super.changeNotify();
   }
   private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException
   {
      in.defaultReadObject();
      initTransientData();
   }
   private void initTransientData()
   {
   }
   private boolean m_histogramFill;
   private Color m_histogramBarColor;
   private Color m_histogramBarLineColor;
   private boolean histogramBarLineColorChanged = false;
   private Color m_errorBarColor;
   private Color m_lineColor;
   private Color m_dataPointColor;
   private int m_style;
   private int m_dataPointStyle;
   private int m_dataPointSize;
   private boolean m_showHistogramBars;
   private boolean m_showErrorBars;
   private boolean m_showDataPoints;
   private boolean m_showLinesBetweenPoints;
   private float m_linesBetweenPointsWidth;
   private int m_linesBetweenPointsStyle;
   private float m_errorBarWidth;
   private int m_errorBarStyle;
   private float m_errorBarDecoration = -1.0f;
   private float m_histogramBarLineWidth;
   private int m_histogramBarLineStyle;
   private boolean m_errorBarColorIsDefault;
}
