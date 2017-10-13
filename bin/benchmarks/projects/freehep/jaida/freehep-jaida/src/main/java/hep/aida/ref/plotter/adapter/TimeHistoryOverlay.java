package hep.aida.ref.plotter.adapter;

import jas.hist.CustomOverlay;
import jas.hist.DataSource;
import jas.hist.JASHist1DHistogramStyle;
import jas.hist.JASHistData;
import jas.hist.JASHistStyle;
import jas.hist.XYDataSource;
import jas.plot.CoordinateTransformation;
import jas.plot.DateCoordinateTransformation;
import jas.plot.DoubleCoordinateTransformation;
import jas.plot.MutableLegendEntry;
import jas.plot.OverlayContainer;
import jas.plot.PlotGraphics;

import java.awt.BasicStroke;

/**
 * Overlay that can be used with XYDataSource to represent time histories
 * It draws straight horizontal line after the point and vertical just before 
 * the next point:
 *
 *        *----- 
 *   *----|
 */

public class TimeHistoryOverlay implements CanSetData, CustomOverlay, MutableLegendEntry
{
   private XYDataSource source;
   private OverlayContainer container;
   private JASHistData data;
   private JASHist1DHistogramStyle style;
   private String[] labels;
   private double[] xPoly;
   private double[] yPoly;
   private double xmax;
   private double xmin;
   private int oldn = 0;

   private static final float[][] lineStyles = {null, { 1, 5 },{ 4, 6 },{ 6, 4, 2, 4 }};
   
   public TimeHistoryOverlay()
   {
   }


   public void setData(JASHistData data) {
       this.data = data;
   }

   public void setStyle(JASHistStyle s) {
       this.style = (JASHist1DHistogramStyle) s;
       if (style != null) style.setShowHistogramBars(true);
   }

   public JASHist1DHistogramStyle getStyle() {
       return style;
   }


    // LegendEntry methods

   public String getTitle()
   {
      return source.getTitle();
   }

   public void paintIcon(PlotGraphics g, int width, int height)
   {
      if (style == null) style = (JASHist1DHistogramStyle) data.getStyle();
      if (style.getShowDataPoints())
      {
         g.setColor(style.getDataPointColor());
         g.drawSymbol( width / 2, height / 2, width / 2, style.getDataPointStyle());
      }
      else if (style.getShowHistogramBars())
      {
         if ( style.getHistogramFill() ) {
            g.setColor(style.getHistogramBarColor());
            g.fillRect(1, 1, width - 2, height - 2);
         }
         else {
            g.setColor(style.getHistogramBarLineColor());
            BasicStroke s = new BasicStroke(style.getHistogramBarLineWidth(),BasicStroke.CAP_SQUARE,BasicStroke.JOIN_ROUND,10,lineStyles[style.getHistogramBarLineStyle()],0);
            g.setStroke(s);
            g.drawLine(1, height/2, width-2, height/2);
            g.setStroke(null);
         }
      }
      else if (style.getShowLinesBetweenPoints())
      {
         g.setColor(style.getLineColor());
         BasicStroke s = new BasicStroke(style.getLinesBetweenPointsWidth(),BasicStroke.CAP_SQUARE,BasicStroke.JOIN_ROUND,10,lineStyles[style.getLinesBetweenPointsStyle()],0);
         g.setStroke(s);
         g.drawLine(1, height/2, width-2, height/2);
         g.setStroke(null);
      }
      else if (style.getShowErrorBars())
      {
         g.setColor(style.getErrorBarColor());
         BasicStroke s = new BasicStroke(style.getErrorBarWidth(),BasicStroke.CAP_SQUARE,BasicStroke.JOIN_ROUND,10,lineStyles[style.getErrorBarStyle()],0);
         g.setStroke(s);
         g.drawLine(1, height/2, width-2, height/2);
         g.setStroke(null);
      }
      else if (style.getHistogramFill())
      {
         g.setColor(style.getHistogramBarColor());
         g.fillRect(1, 1, width - 2, height - 2);
      }
   }


    // MutableLegendEntry methods 

   public void setTitle(String newTitle)
   {
   }

   public boolean titleIsChanged()
   {
       return false; //source.isLegendChanged();
   }



    // CustomOverlay mathods

   public void setDataSource(DataSource sr) {
       if (sr instanceof XYDataSource) this.source = (XYDataSource) sr;
       else throw new IllegalArgumentException("DataSource is not XYDataSource,  DataSource="+sr.toString());
   }


    // Overlay methods

   public void containerNotify(OverlayContainer c)
   {
      this.container = c;
   }

   public void paint(PlotGraphics g, boolean isPrinting)
   {
      if (style == null) style = (JASHist1DHistogramStyle) data.getStyle();

      CoordinateTransformation xp = container.getXTransformation();
      final CoordinateTransformation yp = container.getYTransformation(data.getYAxis());

      if (xp instanceof DateCoordinateTransformation)
      {
         xp = new DateTransformationConverter((DateCoordinateTransformation) xp);
      }

      if (xp instanceof DoubleCoordinateTransformation && yp instanceof DoubleCoordinateTransformation)
      {
         final DoubleCoordinateTransformation xt = (DoubleCoordinateTransformation) xp;
         final DoubleCoordinateTransformation yt = (DoubleCoordinateTransformation) yp;

	 xmin = xt.getPlotMin();
	 xmax = xt.getPlotMax();

         final int bins = source.getNPoints();
         final double binWidth = (xmax - xmin) / bins;
         final double pixelWidth = (xt.convert(xmax) - xt.convert(xmin)) / bins;
         final boolean outline = pixelWidth > 5;
         final double errorBarWidth = Math.min(binWidth, (3 * binWidth) / pixelWidth);

         g.setTransformation(xt, yt);

         double x = xmin;
         double oldx = xmin;

         // y0 is used as the base of histogram bars. It should
         // be 0 unless there is a suppressed 0 on the Y axis.
         double y0 = 0;
         if (yt.getPlotMin() > y0)
         {
            y0 = yt.getPlotMin();
         }
         if (yt.getPlotMax() < y0)
         {
            y0 = yt.getPlotMax();
         }

         if ((style.getShowLinesBetweenPoints() || style.getShowDataPoints()) && bins != oldn) {
	     xPoly = new double[bins+1];
	     yPoly = new double[bins+1];
	     oldn = bins;
	 }
         int index = 0;
	 double y = source.getY(index);
	 x = source.getX(index);

         double oldy = y;

	 xPoly[index] = x;
	 yPoly[index] = y;


         for (int i = 0; i < bins; i++)
         {
            y = source.getY(i);
            x = source.getX(i);

	    //System.out.println("N="+i+"\ty="+y+"\tx="+x);

	    // skip point if X or Y == Double.NaN
	    if (Double.isNaN(y) || Double.isNaN(x)) continue;

	    xPoly[index] = x;
	    yPoly[index] = y;

            BasicStroke s = new BasicStroke(style.getHistogramBarLineWidth(),BasicStroke.CAP_SQUARE,BasicStroke.JOIN_ROUND,10,lineStyles[style.getHistogramBarLineStyle()],0);
            g.setStroke(s);
	    if (style.getShowHistogramBars()) {
		g.setColor(style.getHistogramBarColor());
		if (style.getHistogramFill()) {
		    g.setColor(style.getHistogramBarLineColor());
		    if (i > 0) g.fillRect(oldx, y0, x, oldy);
		    if (outline) {
                        g.drawRect(oldx, y0, x, oldy);
		    } else {
                        if (i > 0) g.drawLine(oldx, oldy, x, oldy);
                        g.drawLine(x, oldy, x, y);
		    }
		} else {
		    g.setColor(style.getHistogramBarLineColor());
		    g.drawLine(oldx, oldy, x, oldy);
		    g.drawLine(x, oldy, x, y);
		}
            }

            if (style.getShowErrorBars())
            {
		g.setColor(style.getErrorBarColor());
		s = new BasicStroke(style.getErrorBarWidth(),BasicStroke.CAP_SQUARE,BasicStroke.JOIN_ROUND,10,lineStyles[style.getErrorBarStyle()],0);
		g.setStroke(s);

		//final double xm = (binWidth == 0) ? x : (oldx + (binWidth / 2));
		final double xm = x;
		final double xe = errorBarWidth;
		final double yplus = y + source.getPlusError(i);
		final double yminus = y - source.getMinusError(i);
		if (!Double.isNaN(yplus) && !Double.isNaN(yminus) && yminus != yplus) {
		    //System.out.println("***** xm="+xm+",  xe="+xe+",  y="+y+", yminus="+yminus+", yplus="+yplus);
		    g.drawLine(xm, yplus, xm, yminus);
		    if (outline)
			{
			    BasicStroke ss = new BasicStroke(style.getErrorBarWidth(),BasicStroke.CAP_SQUARE,BasicStroke.JOIN_ROUND,10);
			    g.setStroke(ss);
			    g.drawLine(xm - xe, yplus, xm + xe, yplus);
			    g.drawLine(xm - xe, yminus, xm + xe, yminus);
			}
		}
            }
         
            oldx = x;
            oldy = y;
	    index++;

	 } // end for

	 if (index > 0) {
	     if (style.getShowLinesBetweenPoints()) {
		 g.setColor(style.getLineColor());
		 BasicStroke s = new BasicStroke(style.getLinesBetweenPointsWidth(),BasicStroke.CAP_SQUARE,BasicStroke.JOIN_ROUND,10,lineStyles[style.getLinesBetweenPointsStyle()],0);
		 g.setStroke(s);
		 g.drawPolyLine(xPoly, yPoly, index);
	     }
	     if (style.getShowDataPoints()) {
		 g.setColor(style.getDataPointColor());
		 g.drawPolySymbol(xPoly, yPoly, style.getDataPointSize(), style.getDataPointStyle(), index);
	     }
	 }

	 // Draw line from the last point till the end of the plot
	 x = xmax;  
	 if (x > oldx) {
	     if (style.getShowHistogramBars()) {
		 BasicStroke s = new BasicStroke(style.getHistogramBarLineWidth(),BasicStroke.CAP_SQUARE,BasicStroke.JOIN_ROUND,10,lineStyles[style.getHistogramBarLineStyle()],0);
		 g.setStroke(s);
		 g.setColor(style.getHistogramBarColor());
		 if (style.getHistogramFill()) {
		     g.setColor(style.getHistogramBarLineColor());
		     g.fillRect(oldx, y0, x, oldy);
		     if (outline) {
			 g.drawRect(oldx, y0, x, oldy);
		     } else {
			 g.drawLine(oldx, oldy, x, oldy);
			 //g.drawLine(x, oldy, x, y);
		     }
		 } else {
		     g.setColor(style.getHistogramBarLineColor());
		     g.drawLine(oldx, oldy, x, oldy);
		 }
	    }
	 } // end drawing last point
      }
   }

    private class DateTransformationConverter implements DoubleCoordinateTransformation
    {
	private DateCoordinateTransformation dateCT;
	
	DateTransformationConverter(DateCoordinateTransformation dateCT)
	{
	    this.dateCT = dateCT;
	}	
	public double convert(double d)
	{
	    return dateCT.convert((long) (d*1000));
	}
	public double unConvert(double i)
	{
	    return dateCT.map(i)/1000.;
	}
	public double getPlotMin()
	{
	    return dateCT.getAxisMin()/1000.;
	}
	public double getPlotMax()
	{
	    return dateCT.getAxisMax()/1000.;
	}
    }
}
