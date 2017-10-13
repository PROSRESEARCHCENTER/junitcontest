package jas.hist;

import jas.plot.CoordinateTransformation;
import jas.plot.DateCoordinateTransformation;
import jas.plot.DoubleCoordinateTransformation;
import jas.plot.MutableLegendEntry;
import jas.plot.Overlay;
import jas.plot.OverlayContainer;
import jas.plot.PlotGraphics;
import jas.plot.StringCoordinateTransformation;

import java.awt.BasicStroke;

class OneDOverlay implements Overlay, MutableLegendEntry
{
   private JASHist1DHistogramData source;
   private OverlayContainer container;
   private double[] data;
   private double[] dataX;
   private String[] labels;
   private double[] minus;
   private double[] plus;
   private double xmax;
   private double xmin;
   
   private static final float[][] lineStyles = {null, { 1, 5 },{ 4, 6 },{ 6, 4, 2, 4 }};
   
   OneDOverlay(JASHist1DHistogramData d)
   {
      this.source = d;
   }
   
   public void setTitle(String newTitle)
   {
      source.setLegendText(newTitle);
   }
   
   public String getTitle()
   {
      return source.getLegendText();
   }
   
   public void containerNotify(OverlayContainer c)
   {
      this.container = c;
   }
   
   public void paint(PlotGraphics g, boolean isPrinting)
   {
      JASHist1DHistogramStyle style = source.style;
      double[] binEdges = null;
      if (source.getDataSource() instanceof Rebinnable1DVariableHistogramData) 
          binEdges = ((Rebinnable1DVariableHistogramData) source.getDataSource()).getBinEdges();
          
      CoordinateTransformation xp = container.getXTransformation();
      final CoordinateTransformation yp = container.getYTransformation(source.getYAxis());
      
      if (xp instanceof DateCoordinateTransformation)
      {
         xp = new DateTransformationConverter((DateCoordinateTransformation) xp);
      }
      
      
      if (xp instanceof DoubleCoordinateTransformation && yp instanceof DoubleCoordinateTransformation)
      {
         final DoubleCoordinateTransformation xt = (DoubleCoordinateTransformation) xp;
         final DoubleCoordinateTransformation yt = (DoubleCoordinateTransformation) yp;
         final int bins = data.length;
         final double binWidthFixed = (xmax - xmin) / bins;
         final double pixelWidth = (xt.convert(xmax) - xt.convert(xmin)) / bins;
         final boolean outline = pixelWidth > 5;
         double errorBarWidth = Math.min(binWidthFixed, (3 * binWidthFixed) / pixelWidth);
         
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
         
         double oldy = y0;
         
         double[] lpbx = null;
         double[] lpby = null;
         int lpbn = 0;
         
         if (style.getShowLinesBetweenPoints() || style.getShowDataPoints())
         {
            // Bottleneck: Created each time paint is called
            lpbx = new double[bins];
            lpby = new double[bins];
         }
         
         // Note, we want to skip points which have y == NAN. This takes some
         // care for things such as histogram bars and lines between points.
         for (int i = 0; i < bins; i++)
         {
            double y = data[i];
            double binWidth = (binEdges == null) ? binWidthFixed : binEdges[i+1] - binEdges[i];
            x = (dataX == null) ? (x + binWidth) : dataX[i];
            
            BasicStroke s = new BasicStroke(style.getHistogramBarLineWidth(),BasicStroke.CAP_SQUARE,BasicStroke.JOIN_ROUND,10,lineStyles[style.getHistogramBarLineStyle()],0);
            g.setStroke(s);
            if (style.getShowHistogramBars())
            {
               if (!Double.isNaN(y))
               {
                  g.setColor(style.getHistogramBarColor());
                  if (style.getHistogramFill())
                  {
                     g.fillRect(oldx, y0, x, y);
                     g.setColor(style.getHistogramBarLineColor());
                     if (outline)
                     {
                        g.drawRect(oldx, y0, x, y);
                     }
                     else
                     {
                        g.drawLine(oldx, oldy, oldx, y);
                        g.drawLine(oldx, y, x, y);
                     }
                  }
                  else
                  {
                     g.setColor(style.getHistogramBarLineColor());
                     g.drawLine(oldx, oldy, oldx, y);
                     g.drawLine(oldx, y, x, y);
                  }
               }
               else
               {
                  if (!style.getHistogramFill() || !outline)
                  {
                     g.drawLine(oldx, oldy, oldx, y0);
                  }
               }
            }
            g.setStroke(null);
            if (style.getShowErrorBars() && !Double.isNaN(y))
            {
               g.setColor(style.getErrorBarColor());
               
               final double xm = (binWidth == 0) ? x : (oldx + (binWidth / 2));
               double xe = (style.getErrorBarDecoration() >= 0) ? 
                   (double) (style.getErrorBarDecoration() * binWidth/2) : 
                   Math.min(binWidth, (3 * binWidth) / pixelWidth);
               final double yplus = data[i] + plus[i];
               final double yminus = data[i] - minus[i];
               s = new BasicStroke(style.getErrorBarWidth(),BasicStroke.CAP_SQUARE,BasicStroke.JOIN_ROUND,10,lineStyles[style.getErrorBarStyle()],0);
               g.setStroke(s);
               if (!Double.isNaN(yplus) && !Double.isNaN(yminus) && yminus != yplus) {
                    g.drawLine(xm, yplus, xm, yminus);
                    if ((outline && style.getErrorBarDecoration() < 0) || style.getErrorBarDecoration() > 0)
                    {
                        BasicStroke ss = new BasicStroke(style.getErrorBarWidth(),BasicStroke.CAP_SQUARE,BasicStroke.JOIN_ROUND,10);
                        g.setStroke(ss);
                        g.drawLine(xm - xe, yplus, xm + xe, yplus);
                        g.drawLine(xm - xe, yminus, xm + xe, yminus);
                    }
               }
               g.setStroke(null);
            }
            if (lpbx != null)
            {
               if (Double.isNaN(y))
               {
                  if (lpbn > 0)
                  {
                     if (style.getShowLinesBetweenPoints())
                     {
                        g.setColor(style.getLineColor());
                        s = new BasicStroke(style.getLinesBetweenPointsWidth(),BasicStroke.CAP_SQUARE,BasicStroke.JOIN_ROUND,10,lineStyles[style.getLinesBetweenPointsStyle()],0);
                        g.setStroke(s);
                        g.drawPolyLine(lpbx, lpby, lpbn);
                        g.setStroke(null);
                     }
                     if (style.getShowDataPoints())
                     {
                        g.setColor(style.getDataPointColor());
                        g.drawPolySymbol(lpbx, lpby, style.getDataPointSize(), style.getDataPointStyle(), lpbn);
                     }
                     lpbn = 0;
                  }
               }
               else
               {
                  lpbx[lpbn] = (dataX == null) ? (oldx + (binWidth / 2)) : x;
                  lpby[lpbn++] = y;
               }
            }
            oldx = x;
            oldy = (y == Double.NaN) ? y0 : y;
         }
         if (lpbn > 0)
         {
            if (style.getShowLinesBetweenPoints())
            {
               g.setColor(style.getLineColor());
               BasicStroke s = new BasicStroke(style.getLinesBetweenPointsWidth(),BasicStroke.CAP_SQUARE,BasicStroke.JOIN_ROUND,10,lineStyles[style.getLinesBetweenPointsStyle()],0);
               g.setStroke(s);
               g.drawPolyLine(lpbx, lpby, lpbn);
               g.setStroke(null);
            }
            if (style.getShowDataPoints())
            {
               g.setColor(style.getDataPointColor());
               g.drawPolySymbol(lpbx, lpby, style.getDataPointSize(), style.getDataPointStyle(), lpbn);
            }
         }
      }
      else if (xp instanceof StringCoordinateTransformation && yp instanceof DoubleCoordinateTransformation)
      {
         final StringCoordinateTransformation xt = (StringCoordinateTransformation) xp;
         final DoubleCoordinateTransformation yt = (DoubleCoordinateTransformation) yp;
         
         final int bins = labels.length;
         final double binWidth = xt.binWidth();
         boolean outline = binWidth > 5;
         
         g.setTransformation(null, yt);
         
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
         
         double oldy = yt.getPlotMin();
         
         double[] lpbx = null;
         double[] lpby = null;
         int lpbn = 0;
         
         if (style.getShowLinesBetweenPoints() || style.getShowDataPoints())
         {
            // Bottleneck: Created each time paint is called
            lpbx = new double[bins];
            lpby = new double[bins];
         }
         
         for (int i = 0; i < bins; i++)
         {
            double x = xt.convert(labels[i]) + (binWidth / 2);
            double oldx = x - binWidth;
            double y = data[i];
            
            BasicStroke s = new BasicStroke(style.getHistogramBarLineWidth(),BasicStroke.CAP_SQUARE,BasicStroke.JOIN_ROUND,10,lineStyles[style.getHistogramBarLineStyle()],0);
            g.setStroke(s);
            if (style.getShowHistogramBars())
            {
               if (!Double.isNaN(y))
               {
                  g.setColor(style.getHistogramBarColor());
                  if (style.getHistogramFill())
                  {
                     g.fillRect(oldx, oldy, x, y);
                     g.setColor(style.getHistogramBarLineColor());
                     if (outline)
                     {
                        g.drawRect(oldx, oldy, x, y);
                     }
                     else
                     {
                        g.drawLine(oldx, oldy, oldx, y);
                        g.drawLine(oldx, y, x, y);
                     }
                  }
                  else
                  {
                     g.setColor(style.getHistogramBarLineColor());
                     g.drawLine(oldx, oldy, oldx, y);
                     g.drawLine(oldx, y, x, y);
                  }
               }
               else
               {
                  if (!style.getHistogramFill() || !outline)
                  {
                     g.drawLine(oldx, oldy, oldx, y0);
                  }
               }
            }
            g.setStroke(null);
            
            if (style.getShowErrorBars() && !Double.isNaN(y))
            {
               s = new BasicStroke(style.getErrorBarWidth(),BasicStroke.CAP_SQUARE,BasicStroke.JOIN_ROUND,10,lineStyles[style.getErrorBarStyle()],0);
               g.setStroke(s);
               g.setColor(style.getErrorBarColor());
               
               double xm = x - (binWidth / 2);
               double xe = (style.getErrorBarDecoration() >= 0) ? 
                   (double) (style.getErrorBarDecoration() * binWidth / 2) : 
                   Math.min(3, binWidth / 2);
               double yplus = data[i] + plus[i];
               double yminus = data[i] - minus[i];
               if (!Double.isNaN(yplus) && !Double.isNaN(yminus) && yminus != yplus) {
                    g.drawLine(xm, yplus, xm, yminus);
                    if ((outline && style.getErrorBarDecoration() < 0) || style.getErrorBarDecoration() > 0)
                    {
                        BasicStroke ss = new BasicStroke(style.getErrorBarWidth(),BasicStroke.CAP_SQUARE,BasicStroke.JOIN_ROUND,10);
                        g.setStroke(ss);
                        g.drawLine(xm - xe, yplus, xm + xe, yplus);
                        g.drawLine(xm - xe, yminus, xm + xe, yminus);
                    }
               }
               g.setStroke(null);
            }
            
            if (lpbx != null)
            {
               if (Double.isNaN(y))
               {
                  if (lpbn > 0)
                  {
                     if (style.getShowLinesBetweenPoints())
                     {
                        g.setColor(style.getLineColor());
                        s = new BasicStroke(style.getLinesBetweenPointsWidth(),BasicStroke.CAP_SQUARE,BasicStroke.JOIN_ROUND,10,lineStyles[style.getLinesBetweenPointsStyle()],0);
                        g.setStroke(s);
                        g.drawPolyLine(lpbx, lpby, lpbn);
                        g.setStroke(null);
                     }
                     if (style.getShowDataPoints())
                     {
                        g.setColor(style.getDataPointColor());
                        g.drawPolySymbol(lpbx, lpby, style.getDataPointSize(), style.getDataPointStyle(), lpbn);
                     }
                     lpbn = 0;
                  }
               }
               else
               {
                  lpbx[lpbn] = (dataX == null) ? (oldx + (binWidth / 2)) : x;
                  lpby[lpbn++] = y;
               }
            }
            oldx = x;
//            oldy = (y == Double.NaN) ? y0 : y;
         }
         if (lpbn > 0)
         {
            if (style.getShowLinesBetweenPoints())
            {
               g.setColor(style.getLineColor());
               BasicStroke s = new BasicStroke(style.getLinesBetweenPointsWidth(),BasicStroke.CAP_SQUARE,BasicStroke.JOIN_ROUND,10,lineStyles[style.getLinesBetweenPointsStyle()],0);
               g.setStroke(s);
               g.drawPolyLine(lpbx, lpby, lpbn);
               g.setStroke(null);
            }
            if (style.getShowDataPoints())
            {
               g.setColor(style.getDataPointColor());
               g.drawPolySymbol(lpbx, lpby, style.getDataPointSize(), style.getDataPointStyle(), lpbn);
            }
         }
      }
      
   }
   
   public void paintIcon(PlotGraphics g, int width, int height)
   {
      JASHist1DHistogramStyle style = source.style;
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
            float flw = (float) style.getHistogramBarLineWidth()*3.0f;
            if (flw > (width/2)) flw = (float) (width/2 -1);
            BasicStroke s = new BasicStroke(flw,BasicStroke.CAP_SQUARE,BasicStroke.JOIN_ROUND,10,lineStyles[style.getHistogramBarLineStyle()],0);
            g.setStroke(s);
            g.drawLine(1, height/2, width-2, height/2);
            g.setStroke(null);
         }
      }
      else if (style.getShowLinesBetweenPoints())
      {
         g.setColor(style.getLineColor());
         float flw = (float) style.getHistogramBarLineWidth()*3.0f;
         if (flw > (width/2)) flw = (width > 2) ? (float) (width/2 -1) : 1;
         BasicStroke s = new BasicStroke(flw,BasicStroke.CAP_SQUARE,BasicStroke.JOIN_ROUND,10,lineStyles[style.getLinesBetweenPointsStyle()],0);
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
   
   public boolean titleIsChanged()
   {
      return source.isLegendChanged();
   }
   
   void setData(double[] data, double[] plusError, double[] minusError, double xMin, double xMax)
   {
      this.data = data;
      this.plus = plusError;
      this.minus = minusError;
      this.xmin = xMin;
      this.xmax = xMax;
   }
   
   void setData(double[] x, double[] y, double[] plusError, double[] minusError)
   {
      this.dataX = x;
      this.data = y;
      this.plus = plusError;
      this.minus = minusError;
   }
   
   void setData(double[] data, double[] plusError, double[] minusError, String[] labels)
   {
      this.data = data;
      this.plus = plusError;
      this.minus = minusError;
      this.labels = labels;
   }
}
