package jas.plot.java1;
import jas.plot.SetablePlotGraphics;
import jas.plot.Transformation;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;

public class PlotGraphics11 implements SetablePlotGraphics
{
	private Transformation xt, yt;
	private Graphics g;
	private Shape oldClip;
	
   public void setStroke(Stroke s)
   {
      // no-op
   }
	public void setGraphics(Graphics g)
	{
		this.g = g;
                if (g == null)
                {
                    oldClip = null;
                }
                else
                {
                    oldClip = g.getClip();
                }
		clearTransformation();
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
	private final static Transformation defaultTransformation = new Transformation()
	{
		public double convert(double value) { return value; }
	};
	
	public void setClip(int xmin, int xmax, int ymin, int ymax)
	{
		g.clipRect(xmin,ymax,xmax-xmin,ymin-ymax);
	}
	public void clearClip()
	{
		g.setClip(oldClip);
	}
	public void drawLine(double x1, double y1, double x2, double y2)
	{
		int ix1 = (int) xt.convert(x1);
		int ix2 = (int) xt.convert(x2);
		int iy1 = (int) yt.convert(y1);
		int iy2 = (int) yt.convert(y2);
		g.drawLine(ix1,iy1,ix2,iy2);
	}
	public void drawPolyLine(double[] x, double[] y, int n)
	{
		polyConvert(x,y,n);
		g.drawPolyline(ix,iy,n);
	}

	public void fillRect(double x1, double y1, double x2, double y2)
	{
		int ix1 = (int) xt.convert(x1);
		int iy1 = (int) yt.convert(y1);
		int ix2 = (int) xt.convert(x2);
		int iy2 = (int) yt.convert(y2);
		int ixmin = Math.min(ix1,ix2);
		int ixmax = Math.max(ix1,ix2);
		int iymin = Math.min(iy1,iy2);
		int iymax = Math.max(iy1,iy2);
		
                  // force oval width and height to be at least 1 pixel
                  int w = ixmax-ixmin;
                  if (w < 1.0) w = 1;
                  int h = iymax-iymin;
                  if (h < 1.0) h = 1;

		g.fillRect(ixmin,iymin,w,h);
	}

	public void drawRect(double x1, double y1, double x2, double y2)
	{
		int ix1 = (int) xt.convert(x1);
		int iy1 = (int) yt.convert(y1);
		int ix2 = (int) xt.convert(x2);
		int iy2 = (int) yt.convert(y2);
		int ixmin = Math.min(ix1,ix2);
		int ixmax = Math.max(ix1,ix2);
		int iymin = Math.min(iy1,iy2);
		int iymax = Math.max(iy1,iy2);
		
                  // force oval width and height to be at least 1 pixel
                  int w = ixmax-ixmin;
                  if (w < 1.0) w = 1;
                  int h = iymax-iymin;
                  if (h < 1.0) h = 1;

		g.drawRect(ixmin,iymin,w,h);
	}
	public void drawOval(double x1, double y1, double x2, double y2)
	{
		int ix1 = (int) xt.convert(x1);
		int iy1 = (int) yt.convert(y1);
		int ix2 = (int) xt.convert(x2);
		int iy2 = (int) yt.convert(y2);
		int ixmin = Math.min(ix1,ix2);
		int ixmax = Math.max(ix1,ix2);
		int iymin = Math.min(iy1,iy2);
		int iymax = Math.max(iy1,iy2);
		
                  // force oval width and height to be at least 1 pixel
                  int w = ixmax-ixmin;
                  if (w < 1.0) w = 1;
                  int h = iymax-iymin;
                  if (h < 1.0) h = 1;

		g.drawOval(ixmin,iymin,w,h);
	}
	public void setColor(Color c)
	{
		g.setColor(c);
	}
	public void drawString(String s, double x, double y)
	{
		g.drawString(s,(int) xt.convert(x), (int) yt.convert(y));
	}

	public FontMetrics getFontMetrics()
	{
		return g.getFontMetrics();
	}
	public void setFont(Font f) {
		g.setFont(f);
	}
	public Font getFont() {
		return g.getFont();
	}
	public void drawPolySymbol(double[] x, double[] y, double size, int type, int n)
	{
		polyConvert(x,y,n);
		int is = (int) size;
		for (int i=0; i<n; i++) drawSymbol(ix[i],iy[i],is,type);
	}
	public void drawSymbol(double x, double y, double size, int type)
	{
		int xx = (int) xt.convert(x);
		int yy = (int) yt.convert(y);
		int is = (int) size;
		drawSymbol(xx,yy,is,type);	
	}
	public void drawImage(Image image, double x, double y, ImageObserver observer)
	{
		g.drawImage(image,(int) xt.convert(x),(int) yt.convert(y),observer);
	}
	public void drawImage(Image image, double x, double y, int width, int height, ImageObserver observer)
	{
		g.drawImage(image,(int) xt.convert(x),(int) yt.convert(y),width,height,observer);
	}
	private void drawSymbol(int x, int y, int is, int type)
	{
		drawSymbol(g,x,y,is,type);
	}
	/**
	 * This method is made public so it can be used for drawing legends.
	 */
	public static void drawSymbol(Graphics g, int x, int y, int is, int type)
	{
		int xx = x - is/2;
		int yy = y - is/2;
		
		switch (type)
		{
		case SYMBOL_DOT:
			g.fillOval(xx,yy,is,is);
			break;
		case SYMBOL_BOX:
			g.fillRect(xx,yy,is,is);
			break;
		case SYMBOL_TRIANGLE:
			int[] x3 = { xx, x, xx+is };
			int[] y3 = { yy+is , yy , yy+is };
			g.fillPolygon(x3,y3,3);
			break;
		case SYMBOL_DIAMOND:
			int[] x4 = { xx, x, xx+is , x };
			int[] y4 = { y , yy+is , y, yy};
			g.fillPolygon(x4,y4,4);
			break;
		case SYMBOL_STAR:
			g.drawLine(xx,y,xx+is,y);
			g.drawLine(xx,yy,xx+is,yy+is);
			g.drawLine(x,yy,x,yy+is);
			g.drawLine(xx,yy+is,xx+is,yy);
			break;
		case SYMBOL_VERT_LINE:
			g.drawLine(x,yy,x,yy+is);
			break;
		case SYMBOL_HORIZ_LINE:
			g.drawLine(xx,y,xx+is,y);
			break;
		case SYMBOL_CROSS:
			g.drawLine(x,yy,x,yy+is);
			g.drawLine(xx,y,xx+is,y);
			break;
		case SYMBOL_CIRCLE: 
			g.drawOval(xx,yy,is,is);
			break;
		case SYMBOL_SQUARE: 
			g.drawRect(xx,yy,is,is);
			break;
		}
	}
	private void polyConvert(double[] x, double[] y, int n)
	{
		if (n > bufSize)
		{
			ix = new int[n];
			iy = new int[n];
			bufSize = n;
		}
		if (xt != defaultTransformation)
		{
			for (int i=0; i<n; i++) ix[i] = (int) xt.convert(x[i]);
		}
		else 
		{
			for (int i=0; i<n; i++) ix[i] = (int)x[i];
		}
		if (yt != defaultTransformation)
		{
			for (int i=0; i<n; i++) iy[i] = (int) yt.convert(y[i]);
		}
		else 
		{
			for (int i=0; i<n; i++) iy[i] = (int)y[i];
		}
        }
        
        public Rectangle getClipBounds() {
            if (g == null) return null;
            return g.getClipBounds();
        }
        
   public BufferedImage createImage(int width, int height) {
       return new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
   }
	private int[] ix, iy;
	private int bufSize;
}
