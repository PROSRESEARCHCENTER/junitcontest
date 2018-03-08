package jas.hist;

import jas.plot.ColorMap;
import jas.plot.ColorMapAxis;
import jas.plot.CoordinateTransformation;
import jas.plot.DataAreaLayout;
import jas.plot.DateCoordinateTransformation;
import jas.plot.DoubleCoordinateTransformation;
import jas.plot.MutableLegendEntry;
import jas.plot.Overlay;
import jas.plot.OverlayContainer;
import jas.plot.PlotGraphics;
import jas.plot.Transformation;
import jas.plot.java2.PlotGraphics12;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.util.Observable;
import java.util.Observer;


class TwoDOverlay implements Overlay, MutableLegendEntry, Observer
{
   protected OverlayContainer container;
   private Color color;
   private JASHist2DHistogramData parent;
   private double[][] data;
   private double binHeightFixed;
   private double binWidthFixed;
   private double xHigh;
   private double xLow;
   private double yHigh;
   private double yLow;
   private double zlogmin;
   private double zmax;
   private double zmin;
   private int xBins;
   private int yBins;
   
   private ColorMapAxis colorMapAxis;
   private ColorMap colorMap;
   private boolean isColorMapAxisAdded = false;
   
   double cx;
   double cy;
   double cw;
   double ch;
   
   int c_mode, s_mode;
   
   TwoDOverlay(JASHist2DHistogramData parent)
   {
      this.color = Color.black;
      this.parent = parent;
     
      parent.style.addObserver(this);
      colorMap = new ColorMap(parent.style);
      colorMapAxis = new ColorMapAxis(colorMap);
      
      styleChanged(parent.style);
      
   }

   private void styleChanged(JASHist2DHistogramStyle style) {
      s_mode = style.getHistStyle();
      c_mode = style.getColorMapScheme();

      if ( s_mode != 0 && s_mode!= 1 ) {
          if ( ! isColorMapAxisAdded )
              parent.parent.da.add(colorMapAxis,DataAreaLayout.Y_AXIS_RIGHT);
          isColorMapAxisAdded = true;
      } else if ( isColorMapAxisAdded ) {
          parent.parent.da.remove(colorMapAxis);
          isColorMapAxisAdded = false;
      }       
   }

    public void update(Observable o, Object arg) {
        styleChanged((JASHist2DHistogramStyle) o);
    }
   
   
   public void setTitle(String newTitle)
   {
      parent.setLegendText(newTitle);
   }

   public String getTitle()
   {
      return parent.getLegendText();
   }

   public void containerNotify(OverlayContainer c)
   {
      this.container = c;
   }

   // Use local coordinates here
   boolean isInClip(Transformation xt, Transformation yt, double Lx1, double Ly1, double Lx2, double Ly2) {
      double Gx1 = xt.convert(Lx1);
      double Gy1 = yt.convert(Ly1);
      double Gx2 = xt.convert(Lx2);
      double Gy2 = yt.convert(Ly2);
      if (Gx2 < Gx1)
      { double xTmp = Gx1; Gx1 = Gx2; Gx2 = xTmp; }
      if (Gy2 < Gy1)
      { double yTmp = Gy1; Gy1 = Gy2; Gy2 = yTmp; }
       
       double Gw = Gx2 - Gx1 + 5.;
       double Gh = Gy2 - Gy1 + 5.;
       
       boolean ok = false; 
       double dGx = cx + (cw - Gx2 - Gx1)*0.5;
       double dGy = cy + (ch - Gy2 - Gy1)*0.5;
       double dGw = (cw + Gw)*0.5;
       double dGh = (ch + Gh)*0.5;

       if (Math.abs(dGx) <= dGw && Math.abs(dGy) <= dGh) ok = true;
       
       //System.out.println("\t\tIntersects="+ok+", dx="+dGx+", dGy="+dGy+", dGw="+Gw+", dGh="+Gh);
       //System.out.println("\t\t\t\t x1="+ Lx1+",  x2="+ Lx2+",  y1="+ Ly1+",  y2="+ Ly2);
       //System.out.println("\t\t\t\tGx1="+Gx1+", Gx2="+Gx2+", Gy1="+Gy1+", Gy2="+Gy2);
       return ok;
   }
     
   public void paint(PlotGraphics g, boolean isPrinting)
   {
       //Disable antialiasing for 2D plots. The original rendering hints are set back
       //at the end of the paint method.
       RenderingHints oldRh = null;
       if ( g instanceof PlotGraphics12 ) {
           oldRh = ((PlotGraphics12)g).graphics().getRenderingHints();
           RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_OFF);
           ((PlotGraphics12)g).graphics().setRenderingHints(rh);
       }
           
       
       boolean doClipCheck = true;
       Rectangle rect = g.getClipBounds();
       if (rect == null) {
           doClipCheck = false;
       } else {       
           cx = (double) rect.x;
           cy = (double) rect.y;
           cw = (double) rect.width;
           ch = (double) rect.height;
       }
       
      /////////Begin Test Area////////
      s_mode = parent.style.getHistStyle();
      c_mode = parent.style.getColorMapScheme();
      boolean log = parent.style.getLogZ();
      boolean uoState = false; //parent.style.getShowOverFlow();
      boolean showZeroHeightBins = parent.style.getShowZeroHeightBins();
      //boolean showGrid = true;
      
      /////////End Test Area/////////
      if (data == null)
      {
         return;
      }

      final int x_bins = xBins;
      final int y_bins = yBins;

      CoordinateTransformation xp = container.getXTransformation();
      final CoordinateTransformation yp = container.getYTransformation();

      if (xp instanceof DateCoordinateTransformation)
      {
         xp = new DateTransformationConverter((DateCoordinateTransformation) xp);
      }
      if (xp instanceof DoubleCoordinateTransformation && yp instanceof DoubleCoordinateTransformation) {
          double[] binXEdges = null;
          double[] binYEdges = null;
          if (parent.getDataSource() instanceof Rebinnable2DVariableHistogramData) {
              binXEdges = ((Rebinnable2DVariableHistogramData) parent.getDataSource()).getXBinEdges();
              binYEdges = ((Rebinnable2DVariableHistogramData) parent.getDataSource()).getYBinEdges();
          }
          final DoubleCoordinateTransformation xt = (DoubleCoordinateTransformation) xp;
          final DoubleCoordinateTransformation yt = (DoubleCoordinateTransformation) yp;
          
          g.setTransformation(xt, yt);

         //////Set Background/////
         if ((s_mode == 0) || (s_mode == 1)) //Box/Ellipse Selected
         {
            g.setColor(Color.white);
         }
         else
         {
             /*
            if (c_mode == 0)
            { //Warm selected
               g.setColor(Color.red);
            }
            else if ((c_mode == 1) || (c_mode == 3))
            { //Cool/Rainbow Selected
               //g.setColor(Color.black);
            }
            else if ((c_mode == 2) || (c_mode == 4))
            { //Thermal/GrayScale Selected
               g.setColor(Color.blue);
            }
            else if (c_mode == 5)
            { //SelectRange Selected
               g.setColor(parent.style.getStartDataColor());
            }
            else
            {
               // Do something here...
            }
            g.fillRect(xLow, yHigh, xHigh, yLow);
              */
         }

          ////////// Show overflow and underflow bins /////////
          // FIXME: Does not seem right
          if (((s_mode == 0) || (s_mode == 1)) && (uoState == true)) {
              g.setColor(Color.black);
              
              g.drawLine(xt.convert(xLow + binWidthFixed), yt.convert(yLow), xt.convert(xLow + binWidthFixed), yt.convert(yHigh));
              g.drawLine(xt.convert(xHigh - binWidthFixed), yt.convert(yLow), xt.convert(xHigh - binWidthFixed), yt.convert(yHigh));
              g.drawLine(xt.convert(xLow), yt.convert(yLow + binHeightFixed), xt.convert(xHigh), yt.convert(yLow +binHeightFixed));
              g.drawLine(xt.convert(xLow), yt.convert(yHigh - binHeightFixed), xt.convert(xHigh), yt.convert(yHigh- binHeightFixed));
          }
          
          //////////Grid Lines on the Pane/////////          
          /*
           if ( ((s_mode == 0) || (s_mode == 1)) && showGrid ) {
              g.setColor(Color.black);
              
              double yGrid = yLow;
              double xGrid = xLow;
              for (int j = 0; j < y_bins; j++) {
                  double binHeight = (binYEdges == null) ? binWidthFixed : binYEdges[j+1] - binYEdges[j];
                  yGrid += binHeight;
                  g.drawLine(xt.convert(xLow), yt.convert(yGrid), xt.convert(xHigh), yt.convert(yGrid));
              }
              
              for (int i = 0; i < x_bins; i++) {
                  double binWidth = (binXEdges == null) ? binWidthFixed : binXEdges[i+1] - binXEdges[i];
                  xGrid += binWidth;
                  g.drawLine(xt.convert(xGrid), yt.convert(yLow), xt.convert(xGrid), yt.convert(yHigh));
              }
          }
          */
          
         ///////End Set Background////
          
         // make sure that zero is included in the Z axis range
         double dispZmin = zmin;
         double dispZmax = zmax;
         if (log)
         {
            dispZmin = Math.log(zlogmin);
            dispZmax = Math.log(zmax);
         }
         else
         {
            if ((dispZmin > 0) && (dispZmax > 0))
            {
               dispZmin = 0;
            }
            if ((dispZmin < 0) && (dispZmax < 0))
            {
               dispZmax = 0;
            }
         }

         double zrange = dispZmax - dispZmin;

         if (zrange > 0)
         {
               double binHeight = binHeightFixed;
               double binWidth = binWidthFixed;
               g.setColor(parent.style.getShapeColor());

               double y = yLow;
               for (int j = 0; j < y_bins; j++)
               {
                  if (binYEdges != null) binHeight = binYEdges[j+1] - binYEdges[j];
                  y += binHeight/2;
                  double x = xLow;

                  for (int i = 0; i < x_bins; i++)
                  {
                     if (binXEdges != null) binWidth = binXEdges[i+1] - binXEdges[i];
                     x += binWidth/2;

                     double size = 0;
                     if ((s_mode == 0) || (s_mode == 1))
                         size = log ? Math.sqrt((Math.log(data[i][j]) - dispZmin) / zrange) : Math.sqrt((data[i][j] - dispZmin) / zrange);
                     else
                         size = 1.;
                     
                     double xFact = (binWidth * size) / 2;
                     double yFact = (binHeight * size) / 2;

                     // We dont want anything to appear if size is exactly 0
                     if (size == 0)
                     {
                        x += binWidth/2;
                        continue;
                     }

                     double x1 = x - binWidth/2;
                     double y1 = y - binHeight/2;
                     double x2 = x + binWidth/2;
                     double y2 = y + binHeight/2;
                     
                     if (doClipCheck && !isInClip(xt, yt, x1, y1, x2, y2)) { x += binWidth/2; continue; }
                     
                     x1 = x - xFact;
                     y1 = y - yFact;
                     x2 = x + xFact;
                     y2 = y + yFact;
                     
                     if (s_mode == 0)
                     {
                        g.drawRect(x1, y1, x2, y2);
                     }
                     else if (s_mode == 1)
                     {
                        g.drawOval(x1, y1, x2, y2);
                     } 
                     else // Color Map mode (s_mode == 2)
                     {
                         double colorSize = log ? ((Math.log(data[i][j]) - dispZmin) / zrange) : ((data[i][j] - dispZmin) / zrange);
                         if ( (showZeroHeightBins || colorSize != 0) && colorSize != Double.NEGATIVE_INFINITY ) {
                             g.setColor(colorMap.getColor(colorSize));
                             g.fillRect(x1, y1, x2, y2);
                         }
                     }
                     x += binWidth/2;
                  }
                  y += binHeight/2;
               }
         }
         else if (zrange == 0)
         {
            // empty histogram, so nothing to do
         }
      }

      // else zrange < 0 (should not occur)
       if ( oldRh != null )
           ((PlotGraphics12)g).graphics().setRenderingHints(oldRh);
      
      
   }
    //paint()

   public void paintIcon(PlotGraphics g, int width, int height)
   {
      g.setColor(parent.style.getShapeColor());
      g.fillRect(1, 1, width - 2, height - 2);
   }

   public boolean titleIsChanged()
   {
      return parent.isLegendChanged();
   }

   void setData(double[][] data, double xLow, double xHigh, double yLow, double yHigh, int xBins, int yBins)
   {
      this.data = data;

      this.xBins = xBins;
      this.yBins = yBins;

      this.xLow = xLow;
      this.xHigh = xHigh;

      this.yLow = yLow;
      this.yHigh = yHigh;

      binWidthFixed = Math.abs(xHigh - xLow) / xBins;
      binHeightFixed = Math.abs(yHigh - yLow) / yBins;
   }
   
   void setZMinMax(double zMin, double zMax, double zLogMin)
   {
      boolean log = parent.style.getLogZ();
      this.zmin = zMin;
      this.zlogmin = zLogMin;
      this.zmax = zMax;
      colorMapAxis.setZminZmax(zmin, zmax);
      colorMapAxis.setLogarithmic(log);
   }

}