package jas.plot.java2;

import jas.plot.SetablePlotGraphics;
import jas.plot.Transformation;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Arc2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;

public class PlotGraphics12 implements SetablePlotGraphics
{
    
   public void setStroke(Stroke s)
   {
      if (s == null) g.setStroke(oldStroke);
      else g.setStroke(s);
   }
   public void setGraphics(Graphics gin)
   {
      this.g = (Graphics2D) gin;
      if (g == null)
      {
        oldClip = null;
        oldStroke = null;
      }
      else
      {
        oldClip = g.getClip();
        oldStroke = g.getStroke();
        g.setRenderingHints(rh);
      }
      clearTransformation();
   }
   
   public Graphics2D graphics() {
       return g;
   }
   
   public void setTransformation(Transformation x, Transformation y)
   {
      xt = x == null ? defaultTransformation : x;
      yt = y == null ? defaultTransformation : y;
   }
   public void clearTransformation()
   {
      xt = defaultTransformation;
      yt = defaultTransformation;
   }
   public void drawLine(double x1, double y1, double x2, double y2)
   {
      line.setLine(xt.convert(x1),yt.convert(y1),
              xt.convert(x2),yt.convert(y2));
      g.draw(line);
   }
   
   public void fillRect(double x1, double y1, double x2, double y2)
   {       
      double xx1 = xt.convert(x1);
      double yy1 = yt.convert(y1);
      double xx2 = xt.convert(x2);
      double yy2 = yt.convert(y2);
      if (xx2 < xx1)
      { double t = xx1; xx1 = xx2; xx2 = t; }
      if (yy2 < yy1)
      { double t = yy1; yy1 = yy2; yy2 = t; }
      
      // force rectangle width and height to be at least 1 pixel
      double w = xx2-xx1;
      if (w < 1.0) w = 1;
      double h = yy2-yy1;
      if (h < 1.0) h = 1;
              
      rect.setRect(xx1,yy1,w,h);
      g.fill(rect);
   }
   public void drawOval(double x1, double y1, double x2, double y2)
   {
      double xx1 = xt.convert(x1);
      double yy1 = yt.convert(y1);
      double xx2 = xt.convert(x2);
      double yy2 = yt.convert(y2);
      if (xx2 < xx1)
      { double t = xx1; xx1 = xx2; xx2 = t; }
      if (yy2 < yy1)
      { double t = yy1; yy1 = yy2; yy2 = t; }
      
      // force oval width and height to be at least 1 pixel
      double w = xx2-xx1;
      if (w < 1.0) w = 1;
      double h = yy2-yy1;
      if (h < 1.0) h = 1;
              
      arc.setArc(xx1,yy1,w,h,0,360,arc.CHORD);
      g.draw(arc);
   }
   
   public void drawRect(double x1, double y1, double x2, double y2)
   {
      double xx1 = xt.convert(x1);
      double yy1 = yt.convert(y1);
      double xx2 = xt.convert(x2);
      double yy2 = yt.convert(y2);
      if (xx2 < xx1)
      { double t = xx1; xx1 = xx2; xx2 = t; }
      if (yy2 < yy1)
      { double t = yy1; yy1 = yy2; yy2 = t; }
      
      // force rectangle width and height to be at least 1 pixel
      double w = xx2-xx1;
      if (w < 1.0) w = 1;
      double h = yy2-yy1;
      if (h < 1.0) h = 1;
              
      rect.setRect(xx1,yy1,w,h);
      g.draw(rect);
   }
   public void setColor(Color c)
   {
      g.setColor(c);
   }
   
   public void drawString(String s, double x, double y)
   {
      g.drawString(s,(float) xt.convert(x), (float) yt.convert(y));
   }
   public FontMetrics getFontMetrics()
   {
      return g.getFontMetrics();
   }
   
   public void drawSymbol(double x, double y, double size, int type)
   {
      Shape s = getShapeForSymbol(x,y,size,type);
      if (isFilled[type]) g.fill(s);
      else g.draw(s);
   }
   private Shape getShapeForSymbol(double x, double y, double size, int type)
   {
      float xx = (float) xt.convert(x);
      float yy = (float) yt.convert(y);
      float ss = (float) size/2;
      
      switch (type)
      {
         case SYMBOL_CIRCLE:
         case SYMBOL_DOT:
            arc.setArc(xx-ss,yy-ss,size,size,0,360,arc.PIE);
            return arc;
         case SYMBOL_BOX:
         case SYMBOL_SQUARE:
            rect.setRect(xx-ss,yy-ss,size,size);
            return rect;
         case SYMBOL_TRIANGLE:
            path.reset();
            path.moveTo(xx-ss,yy+ss);
            path.lineTo(xx+ss,yy+ss);
            path.lineTo(xx,yy-ss);
            path.closePath();
            return path;
         case SYMBOL_DIAMOND:
            path.reset();
            path.moveTo(xx-ss,yy);
            path.lineTo(xx,yy+ss);
            path.lineTo(xx+ss,yy);
            path.lineTo(xx,yy-ss);
            path.closePath();
            return path;
         case SYMBOL_STAR:
            path.reset();
            path.moveTo(xx,yy+ss);
            path.lineTo(xx,yy-ss);
            path.moveTo(xx-ss,yy);
            path.lineTo(xx+ss,yy);
            path.moveTo(xx-ss,yy-ss);
            path.lineTo(xx+ss,yy+ss);
            path.moveTo(xx+ss,yy-ss);
            path.lineTo(xx-ss,yy+ss);
            return path;
         case SYMBOL_VERT_LINE:
            line.setLine(xx,yy+ss,xx,yy-ss);
            return line;
         case SYMBOL_HORIZ_LINE:
            line.setLine(xx-ss,yy,xx+ss,yy);
            return line;
         case SYMBOL_CROSS:
            path.reset();
            path.moveTo(xx,yy+ss);
            path.lineTo(xx,yy-ss);
            path.moveTo(xx-ss,yy);
            path.lineTo(xx+ss,yy);
            return path;
      }
      throw new RuntimeException("Unknown symbol "+type);
   }
   public void drawPolyLine(double[] x, double[] y, int n)
   {
      if (n <= 0) return;
      path.reset();
      path.moveTo((float) xt.convert(x[0]),(float) yt.convert(y[0]));
      for (int i=1; i<n; i++) path.lineTo((float) xt.convert(x[i]), (float) yt.convert(y[i]));
      g.draw(path);
   }
   public void setFont(Font f)
   {
      g.setFont(f);
   }
   public Font getFont()
   {
      return g.getFont();
   }
   public void drawPolySymbol(double[] x, double[] y, double size, int type, int n)
   {
      for (int i=0; i<n; i++) drawSymbol(x[i],y[i],size,type);
   }
   public void drawImage(Image image, double x, double y, java.awt.image.ImageObserver observer)
   {
      g.drawImage(image,(int) xt.convert(x),(int) yt.convert(y),observer);
   }
   public void drawImage(Image image, double x, double y, int width, int height, java.awt.image.ImageObserver observer)
   {
      g.drawImage(image,(int) xt.convert(x),(int) yt.convert(y),width,height,observer);
   }
   public void setClip(int xmin, int xmax, int ymin, int ymax)
   {
      g.clipRect(xmin,ymax,xmax-xmin,ymin-ymax);
   }
   public void clearClip()
   {
      g.setClip(oldClip);
   }
   
   public Rectangle getClipBounds() {
       if (g == null) return null;
       return g.getClipBounds();
   }
   
   public BufferedImage createImage(int width, int height) {
       BufferedImage bi = null;
       if (g != null) {
           bi = g.getDeviceConfiguration().createCompatibleImage(width, height);
       } else {
            bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
       }
       return bi;
   }
   
   final private static boolean[] isFilled = { true, true, true, true, false, false, false, false, false, false };
   private GeneralPath path = new GeneralPath();
   private Graphics2D g;
   private Line2D.Double line = new Line2D.Double();
   private Arc2D.Double arc = new Arc2D.Double();
   private Rectangle2D.Double rect = new Rectangle2D.Double();
   private RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
   private Shape oldClip;
   private Stroke oldStroke;
   private Transformation xt, yt;
   private final static Transformation defaultTransformation = new Transformation()
   {
      public double convert(double value)
      { return value; }
   };
}
