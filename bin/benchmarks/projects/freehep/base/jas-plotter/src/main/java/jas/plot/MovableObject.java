package jas.plot;

import jas.util.ColorMenu;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.border.AbstractBorder;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;
import javax.swing.colorchooser.ColorSelectionModel;
import javax.swing.colorchooser.DefaultColorSelectionModel;

/**
 *  Base class for any object that can be moved around using handles
 */

public abstract class MovableObject extends PlotComponent implements HasPopupItems, JASPlotMouseListener
{
	public MovableObject(String prefix)
	{
		this.prefix = prefix;
		super.setBorder(border);
	}
	protected String getPrefix()
	{
		return prefix;
	}
        
	public void setMovableObjectBounds(int p1, int p2, int p3, int p4)
	{
		super.setBounds(p1, p2, p3, p4);
                hasBeenMoved = true;
                hasBeenResized = true;
	}
	public void setMovableObjectBounds(Rectangle r)
	{
		super.setBounds(r);
                hasBeenMoved = true;
                hasBeenResized = true;
	}
	public void setBorder(Border newBorder)
	{
		Border oldBorder = super.getBorder();
		if (oldBorder instanceof CompoundBorder) oldBorder = ((CompoundBorder) oldBorder).getOutsideBorder();
		super.setBorder(newBorder == null ? oldBorder : new CompoundBorder(oldBorder,newBorder));
	}
	public int getBorderType()
	{
		return getBorderType(getInsideBorder());
	}	
	public Border getInsideBorder()
	{
		Border oldBorder = super.getBorder();
		if (oldBorder instanceof CompoundBorder) return ((CompoundBorder) oldBorder).getInsideBorder();
		if (oldBorder instanceof HandleBorder) return null;
		return oldBorder;
	}
	public void modifyPopupMenu(JPopupMenu menu, Component source)
	{
		menu.add(new BorderMenu(prefix));
		ColorSelectionModel cm = new DefaultColorSelectionModel()
		{
			public Color getSelectedColor()
			{
				if (!isPaintingBackground()) return null;
				return getBackground();
			}
			public void setSelectedColor(Color c)
			{
				setBackground(c);
			}
		};
		menu.add(new BackgroundColorMenu(prefix,cm));
	}
	final void showHandles()
	{
		handlesVisible = true;
		paintBorder(getGraphics());
		saveCursor = getCursor();
		currentCursor = Cursor.DEFAULT_CURSOR;
	}												
	final void hideHandles()
	{
		handlesVisible = false;
		setCursor(saveCursor);
		Component p = getParent(); // Parent can be null when window is closing
		if (p != null) p.repaint(); // Since we may overlap our siblings 
	}
	public final void mouseEventNotify(final MouseEvent me)
	{
		if (handlesVisible)
		{
			if (me.getID() == MouseEvent.MOUSE_ENTERED)
			{
				saveCursor = getCursor();
			}
			else if (me.getID() == MouseEvent.MOUSE_EXITED)
			{
				setCursor(saveCursor);
			}
			else if (me.getID() == MouseEvent.MOUSE_PRESSED)
			{
				// What happens when the mouse is pressed depends on where it is
				// It either starts some sort of resize or a move

				if (currentCursor == Cursor.MOVE_CURSOR)
				{
					dragOffset = me.getPoint();
				}
				else
				{
				}
			}
			else if (me.getID() == MouseEvent.MOUSE_RELEASED)
			{
				dragOffset = null;
			}
		}
	}
	final void mouseMotionEventNotify(final MouseEvent me)
	{
		if (!handlesVisible) return;
		if (me.getID() == MouseEvent.MOUSE_DRAGGED)
		{
			if (currentCursor == Cursor.MOVE_CURSOR)
			{
				Point p = me.getPoint();
				Point x = getLocation();
				p.translate(x.x,x.y);
				if (dragOffset != null) p.translate(-dragOffset.x,-dragOffset.y);
				setLocation(p);
                                hasBeenMoved = true;
			}
			else
			{
				final Rectangle b = getBounds();
				final Point p = me.getPoint();
				if (currentCursor == Cursor.E_RESIZE_CURSOR)
				{
					b.width = p.x;
				}
				else if (currentCursor == Cursor.W_RESIZE_CURSOR)
				{
					b.width -= p.x;
					b.x += p.x;
				}
				else if (currentCursor == Cursor.N_RESIZE_CURSOR)
				{
					b.height -= p.y;
					b.y += p.y;
				}
				else if (currentCursor == Cursor.S_RESIZE_CURSOR)
				{
					b.height = p.y;
				}
				else if (currentCursor == Cursor.NE_RESIZE_CURSOR)
				{
					// code from N:
					b.height -= p.y;
					b.y += p.y;
					// code from E:
					b.width = p.x;
				}
				else if (currentCursor == Cursor.NW_RESIZE_CURSOR)
				{
					// code from N:
					b.height -= p.y;
					b.y += p.y;
					// code from W:
					b.width -= p.x;
					b.x += p.x;
				}
				else if (currentCursor == Cursor.SE_RESIZE_CURSOR)
				{
					// code from S:
					b.height = p.y;
					// code from E:
					b.width = p.x;
				}
				else if (currentCursor == Cursor.SW_RESIZE_CURSOR)
				{
					// code from S:
					b.height = p.y;
					// code from W:
					b.width -= p.x;
					b.x += p.x;
				}
				else return;
                                hasBeenResized = true;
				setBounds(b);
				validate();
			}
		}
		else if (me.getID() == MouseEvent.MOUSE_MOVED)
		{
			currentCursor = border.getCursor(getBounds(),me.getPoint());
			((Component) me.getSource()).setCursor(Cursor.getPredefinedCursor(currentCursor));
		}
	}
	
        
        public boolean hasBeenResized() {
            return hasBeenResized;
        }
        
        public boolean hasBeenMoved() {
            return hasBeenMoved;
        }
        
        public void restoreDefaultLayout() {
            hasBeenResized = false;
            hasBeenMoved = false;
        }
        
        public boolean hasDefaultLayout() {
            return ! hasBeenMoved() && ! hasBeenResized();
        }
        
        // Are these methods really needed?
        
        public void resizeMovableObject(int w, int h) {
            hasBeenResized = true;
            this.setSize(w,h);            
        }
        
        public void moveMovableObject(int x, int y) {
            hasBeenMoved = true;
            this.setLocation(x,y);            
        }
        
	/**
	 * Clicking on a movable object will by default cause its handles to appear.
	 */

	private String prefix; // Prefix for popup menu items
        private boolean hasBeenResized = false;
        private boolean hasBeenMoved = false;
	private int currentCursor;
	private Point dragOffset;
	private Cursor saveCursor;
	private boolean handlesVisible;
	private static final HandleBorder border = new HandleBorder(Color.blue);
	final private static class HandleBorder extends AbstractBorder
	{
		private Color lineColor;

		/** 
		 * Creates a handle border with the specified color and a 
		 * @param color the color for the border
		 */
		HandleBorder(Color color) 
		{
			lineColor = color;
		}

		/**
		 * Paints the border for the specified component with the 
		 * specified position and size.
		 * @param c the component for which this border is being painted
		 * @param g the paint graphics
		 * @param x the x position of the painted border
		 * @param y the y position of the painted border
		 * @param width the width of the painted border
		 * @param height the height of the painted border
		 */
		public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) 
		{
			if (((MovableObject) c).handlesVisible && !PrintHelper.isPrinting())
			{
				Color oldColor = g.getColor();

				int xx = x+thick2;
				int yy = y+thick2;
				int ww = width-thickness;
				int hh = height-thickness;
				Rectangle[] h = getHandles(xx,yy,ww,hh);

				g.setColor(lineColor);
				g.drawRect(xx, yy, ww,hh);
				for (int i=0; i<h.length; i++) g.fillRect(h[i].x,h[i].y,h[i].width,h[i].height);

				g.setColor(oldColor);
			}
		}
		private Handle[] getHandles(int xx, int yy, int ww, int hh)
		{
			handles[0].set(Cursor.NW_RESIZE_CURSOR, xx, yy);
			handles[1].set(Cursor.NE_RESIZE_CURSOR, xx+ww,yy);
			handles[2].set(Cursor.SE_RESIZE_CURSOR, xx+ww,yy+hh);
			handles[3].set(Cursor.SW_RESIZE_CURSOR, xx,yy+hh);
			handles[4].set(Cursor.N_RESIZE_CURSOR, xx+ww/2, yy);
			handles[5].set(Cursor.S_RESIZE_CURSOR, xx+ww/2, yy+hh);
			handles[6].set(Cursor.W_RESIZE_CURSOR, xx, yy+hh/2);
			handles[7].set(Cursor.E_RESIZE_CURSOR, xx+ww, yy+hh/2);	
			return handles;
		}
		int getCursor(final Rectangle b, final Point p)
		{
			int xx = thick2;
			int yy = thick2;
			int ww = b.width-thickness;
			int hh = b.height-thickness;
			Handle[] h = getHandles(xx,yy,ww,hh);
			for (int i=0; i<h.length; i++) if (h[i].contains(p)) return h[i].getCursor();
			final int extra = thick2 + 1;
			if (p.x <= thick2 + extra && p.x >= thick2 - extra ||
				p.y <= thick2 + extra && p.y >= thick2 - extra ||
				p.x >= b.width - thick2 - extra && p.x <= b.width - thick2 + extra ||
				p.y >= b.height - thick2 - extra && p.y <= b.height - thick2 + extra)
			{
				return Cursor.MOVE_CURSOR;
			}
			return Cursor.DEFAULT_CURSOR;
		}

		/**
		 * Returns the insets of the border.
		 * @param c the component for which this border insets value applies
		 */
		public Insets getBorderInsets(Component c)       
		{
			return insets;
		}

		/**
		 * Returns the color of the border.
		 */
		public Color getLineColor()     
		{
			return lineColor;
		}

		/**
		 * Returns whether or not the border is opaque.
		 */
		public boolean isBorderOpaque() { return false; }
		private final static int thickness = 5;
		private final static Insets insets = new Insets(thickness, thickness, thickness, thickness);
		private final static int thick2 = thickness/2;
		private static Handle[] handles;
		static 
		{
			handles = new Handle[8];
			for (int i=0; i<handles.length; i++) handles[i] = new Handle(thickness);
		}

		final private static class Handle extends Rectangle
		{
			Handle(int size)
			{
				this.setSize(size-1,size-1);
				thick2 = size/2;
			}
			void set(int type, int x, int y)
			{
				this.setLocation(x - thick2, y - thick2);
				this.type = type;
			}
			int getCursor()
			{
				return type;
			}
			private int thick2;
			private int type;
		}
	}

	public void print(Graphics g)
	{
		// Note, print is called when printing, but so is paint called directly,
		// so there is no point in also printing here??
		// Not true under JDK 1.3, better work around this
		try
		{
			if (System.getProperty("java.version").compareTo("1.3") >= 0) super.print(g);
		}
		catch (SecurityException x) {}
	}
    /**
     *  An array containing an expanded selection of colors.
     */
    public final static Color[] bgcolors = 
    {
        null,							Color.pink,
        Color.orange,                   Color.yellow,
        Color.green,                    Color.cyan,
        new Color(164, 207, 255),       new Color(225, 170, 255),
        new Color(255, 170, 210)
    };

    /**
     *  An array containing names of the expanded selection of colors.
     *
     *  @see    ColorMenu#EXTENDED_COLORS
     */
    public final static String[] bgnames =
    {
		"default",                      "pink",
        "light orange",                 "yellow",
        "light green",                  "cyan",
        "sky blue",                     "violet",
        "light magenta"
    };
	private final class BackgroundColorMenu extends ColorMenu
	{
		BackgroundColorMenu(String prefix, ColorSelectionModel cm)
		{
			super(prefix+" Background",cm,bgcolors,bgnames);
		}
	}
	private final class BorderMenu extends JMenu
	{
		BorderMenu(String prefix)
		{
			super(prefix+" Border");
			addItem("None", NONE);
			addItem("Bevel In", BEVEL_IN);
			addItem("Bevel Out", BEVEL_OUT);
			addItem("Ethched", ETCHED);
			addItem("Shadow", SHADOW);
			ColorSelectionModel cm = new DefaultColorSelectionModel()
			{
				public Color getSelectedColor()
				{
					Border oldBorder = MovableObject.this.getBorder();
					if (oldBorder instanceof CompoundBorder)
					{
						oldBorder = ((CompoundBorder) oldBorder).getInsideBorder();
					}
					if (oldBorder instanceof LineBorder)
					{
						return ((LineBorder) oldBorder).getLineColor();
					}
					return null;
				}
				public void setSelectedColor(Color c)
				{
					MovableObject.this.setBorder(BorderFactory.createLineBorder(c));
				}
			};
			this.add(new ColorMenu("Line",cm,false));
		}
		private void addItem(String name, final int btype)
		{
			JMenuItem i = new JRadioButtonMenuItem(name);
			add(i);
			i.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					setBorderType(btype);
				}
			});
			i.setSelected(getBorderType() == btype);
		}
	}
}


