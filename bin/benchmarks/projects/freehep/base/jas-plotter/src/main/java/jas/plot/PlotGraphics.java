package jas.plot;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;

/** 
 * This interface serves two purposes:
 * <ol>
 * <li>To provide an abstract interface which can be used to drive either a JDK1.1
 * Graphics class, or a JDK1.2 Graphics2D class.
 * <li>To provide an mechanism to allow drawing of graphics objects in plot coordinate
 * space.
 * </ol>
 */
public interface PlotGraphics
{
   void setStroke(Stroke s);
	void setColor(Color c);
	void drawLine(double x1, double y1, double x2, double y2);
	void fillRect(double x1, double y1, double x2, double y2);
	void drawRect(double x1, double y1, double x2, double y2);
	void drawString(String s, double x, double y);
	void drawPolyLine(double[] x, double[] y, int n);
	void drawOval(double x, double y, double width, double height);
	void drawSymbol(double x, double y, double size, int type);
	void drawPolySymbol(double[] x, double[] y, double size, int type, int n);

	// Temporary to get scatterplots working for Babar
	void drawImage(Image image, double x, double y, ImageObserver observer);
	void drawImage(Image image, double x, double y, int width, int height, ImageObserver observer);
	
	FontMetrics getFontMetrics();
	void setFont(Font f);
	Font getFont();
	
	void setTransformation(Transformation x, Transformation y);
	void clearTransformation();

	void setClip(int xmin, int xmax, int ymin, int ymax);
	void clearClip();
        
        Rectangle getClipBounds();
        BufferedImage createImage(int width, int height);
	
	final public static int SYMBOL_DOT        = 0;
	final public static int SYMBOL_BOX        = 1;
	final public static int SYMBOL_TRIANGLE   = 2;
	final public static int SYMBOL_DIAMOND    = 3;
	final public static int SYMBOL_STAR       = 4;
	final public static int SYMBOL_VERT_LINE  = 5;
	final public static int SYMBOL_HORIZ_LINE = 6;
	final public static int SYMBOL_CROSS      = 7;
	final public static int SYMBOL_CIRCLE     = 8; 
	final public static int SYMBOL_SQUARE     = 9; 
}
