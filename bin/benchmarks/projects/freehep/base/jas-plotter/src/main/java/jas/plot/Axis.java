package jas.plot;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
/**
 * This class represents a plot axis as a GUI component.  It depends on a subclass
 * of <code>AxisType</code> to implement the type-specific properties necessary for
 * a meaningful axis.
 *  @see AxisType
 *  @author Jonas Gifford
 */
public class Axis extends PlotComponent implements HasPopupItems
{
	/** Represents vertical orientation for both labels and axes. */
	public static final int VERTICAL = 1;
	
	/** Represents horizontal orientation for both labels and axes. */
	public static final int HORIZONTAL = 2;
/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * begin constructors
 */
	/**
	 * This consturctor is primarily for object deserialization.  It creates a vertical axis
	 * object with a numeric (class <code>DoubleAxis</code>) which will be on the left side.
	 *  @see DoubleAxis
	 */
	public Axis()
	{
		this(new DoubleAxis(), VERTICAL, true);
	}

	/**
	 * Creates a new axis object.  If this axis is vertical it will be on the left side of
	 * the plot.
	 *  @param type the AxisType object that represents the data type for this axis
	 *  @param axisOrientation the orientation of the axis (use constants
	 *                         <code>Axis.HORIZONTAL</code> and <code>Axis.VERTICAL</code>)
	 */
	public Axis(final AxisType type, final int axisOrientation)
	{
		this(type, axisOrientation, true);
	}

	/**
	 * Creates a new axis object, using a numeric axis (class <code>DoubleAxis</code>).  If this
	 * axis is vertical it will be on the left side of the plot.
	 *  @see DoubleAxis
	 *  @param axisOrientation the orientation of the axis (use constants
	 *                         <code>Axis.HORIZONTAL</code> and <code>Axis.VERTICAL</code>)
	 */
	public Axis(final int axisOrientation)
	{
		this(new DoubleAxis(), axisOrientation, true);
	}

	/**
	 * Creates a numerical axis object (uses class <code>DoubleAxis</code>).
	 *  @see DoubleAxis
	 *  @param axisOrientation the orientation of the axis (use constants
	 *                         <code>Axis.HORIZONTAL</code> and <code>Axis.VERTICAL</code>)
	 *  @param onLeftSide whether this axis is on the left
	 */
	public Axis(final int axisOrientation, final boolean onLeftSide)
	{
		this(new DoubleAxis(), axisOrientation, onLeftSide);
	}

	/**
	 * Creates a vertical axis object.
	 *  @param type the AxisType object that represents the data type for this axis
	 *  @param onLeftSide whether this axis is on the left
	 */
	public Axis(final AxisType type, final boolean onLeftSide)
	{
		this(type, VERTICAL, onLeftSide);
	}

	/**
	 * Creates a new axis object.
	 *  @param type the AxisType object that represents the data type for this axis
	 *  @param axisOrientation the orientation of the axis (use constants
	 *                         <code>Axis.HORIZONTAL</code> and <code>Axis.VERTICAL</code>)
	 *  @param onLeftSide whether this axis is on the left
	 */
	public Axis(final AxisType type, final int axisOrientation, final boolean onLeftSide)
	{
		this.type = type;
		this.axisOrientation = axisOrientation;
		this.onLeftSide = onLeftSide;
		type.setAxis(this);
	}
/*
 * end constructors
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
	public void modifyPopupMenu(final JPopupMenu menu, final Component source)
	{
		if (menu.getComponentCount() > 0) menu.addSeparator();
		menu.add(new FontMenuItem(this,"Axis"));
		
		final DataArea parent = (DataArea) SwingUtilities.getAncestorOfClass(DataArea.class, this);
		final EditableLabel label = parent.getLabel(this);
		menu.add(new JCheckBoxMenuItem("Show Axis Label", label != null)
		{
			final protected void fireActionPerformed(final ActionEvent e)
			{
				if (isSelected())
				{
					final EditableLabel newLabel = new EditableLabel("Label","Axis Label");
					parent.setLabel(Axis.this, newLabel);
					newLabel.edit();
				}
				else
				{
					parent.remove(label);
				}
			}
		});
		if (label != null)
		{
			menu.add(new JMenuItem("Edit label text")
			{
				final protected void fireActionPerformed(final ActionEvent e)
				{
					label.edit();
				}
			});
		}
	}

	/** Sets the AxisType for this axis. */
	public final void setType(final AxisType type)
	{
		this.type = type;
		type.setAxis(this);
		
		if (isVisible)
		{
			revalidate();
		}
	}
	public final void setFont(Font font)
	{
		super.setFont(font);
		type.labelsValid = false;
		revalidate();
	}
	
	/** Returns the AxisType for this axis. */
	public final AxisType getType()
	{
		return type;
	}

	/**
	 * Used by superclasses to convert coordinates.
	 */
	public final int getMinLocation()
	{
		if (axisOrientation == VERTICAL)
		{
			return size.height - type.spaceRequirements.height + location.y;
		}
		else
		{
			return type.spaceRequirements.width + location.x;
		}
	}

	/**
	 * Used by superclasses to convert coordinates.
	 */
	public final int getMaxLocation()
	{
		if (axisOrientation == VERTICAL)
		{
			return padAroundEdge + location.y + type.spaceRequirements.flowPastEnd;
		}
		else
		{
			return size.width - padAroundEdge + location.x - type.spaceRequirements.flowPastEnd;
		}
	}

	final void paint(final PlotGraphics g)
	{
		final Point actualOrigin = new Point(onLeftSide ? type.spaceRequirements.width:
			size.width - type.spaceRequirements.width, size.height -
			type.spaceRequirements.height);
		actualOrigin.translate(location.x,location.y);

		g.setFont(getFont());
		
		final int tickLength = type.getMajorTickMarkLength();
		if (axisOrientation == VERTICAL)
		{
			g.setColor(getAxisColor());
                        BasicStroke s = new BasicStroke(getAxisWidth(), BasicStroke.CAP_SQUARE, BasicStroke.JOIN_ROUND, 10, null, 0);
                        g.setStroke(s);
			g.drawLine(actualOrigin.x, actualOrigin.y,
				actualOrigin.x, location.y+padAroundEdge + type.spaceRequirements.flowPastEnd); // axis line

			g.setColor(getAxisOriginDotColor());
			g.drawRect(actualOrigin.x - 1, actualOrigin.y - 1, 
					   actualOrigin.x + 1, actualOrigin.y + 1); // a little box at the origin


			type.paintAxis(g, actualOrigin.x, actualOrigin.y, actualOrigin.y - padAroundEdge - type.spaceRequirements.flowPastEnd - location.y,
				getTextColor(), getMajorTickMarkColor(), getMinorTickMarkColor());
		}
		else if (axisOrientation == HORIZONTAL)
		{
			g.setColor(getAxisColor());
                        BasicStroke s = new BasicStroke(getAxisWidth(), BasicStroke.CAP_SQUARE, BasicStroke.JOIN_ROUND, 10, null, 0);
                        g.setStroke(s);
			g.drawLine(actualOrigin.x, actualOrigin.y,
				location.x + size.width - padAroundEdge - type.spaceRequirements.flowPastEnd, actualOrigin.y); // axis line

			type.paintAxis(g, actualOrigin.x, actualOrigin.y, size.width - padAroundEdge - type.spaceRequirements.flowPastEnd - actualOrigin.x + location.x,
				getTextColor(), getMajorTickMarkColor(), getMinorTickMarkColor());
		}
	}
	
	/** This method is public as an implementation side-effect; do not call. */
	final public void setBounds(final int x, final int y, final int width, final int height)
	{
		// Cache component size and location here to speed up other calculations
		// NB Assumes all other methods (setSize, setLocation etc are routed via
		// this method. It would be safer to do this in set processComponentEvent,
		// but there is a delay in calling that method which causes strange effects
		// when resizing components.
		
		size.setSize(width,height);
		location.setLocation(x,y);
		super.setBounds(x,y,width,height);
	}
	public final int getAxisOrientation()
	{
		return axisOrientation;
	}
	protected final void setAxisOrientation(final int i)
	{
		axisOrientation = i;
	}
	public final boolean getOnLeftSide()
	{
		return onLeftSide;
	}
	public final void setOnLeftSide(final boolean left)
	{
		onLeftSide = left;
	}
	final void assumeAxisLength(final int axisLength)
	{
		if (axisLength != lastAxisLength || !type.labelsValid)
		{
			type.assumeAxisLength(axisLength);
			lastAxisLength = axisLength;
		}
	}
	public Color getAxisColor()
	{
		if (axisColor != null) return axisColor;
		return getForeground();
	}
	public void setAxisWidth(float newWidth)
	{
		axisWidth = newWidth;
	}
	public float getAxisWidth()
	{
		return axisWidth;
	}
	public void setAxisColor(Color newColor)
	{
		axisColor = newColor;
	}
	public Color getAxisOriginDotColor()
	{
		if (axisOriginDotColor != null) return axisOriginDotColor;
		return getAxisColor();
	}
	public void setAxisOriginDotColor(Color newColor)
	{
		axisOriginDotColor = newColor;
	}
	public Color getMajorTickMarkColor()
	{
		if (majorTickMarkColor != null) return majorTickMarkColor;
		return getAxisColor();	
	}
	public void setMajorTickMarkColor(Color newColor)
	{
		majorTickMarkColor = newColor;
	}
	public Color getMinorTickMarkColor()
	{
		if (minorTickMarkColor != null) return minorTickMarkColor;
		return getAxisColor();
	}
	public void setMinorTickMarkColor(Color newColor)
	{
		minorTickMarkColor = newColor;
	}
	public Color getTextColor()
	{
		if (textColor != null) return textColor;
		return getAxisColor();
	}
	public void setTextColor(Color newColor)
	{
		textColor = newColor;
	}
	/** The color of the axis line. */
	private Color axisColor = null;
        /** The width of the axis line. */
        private float axisWidth = 1.0f;
	/** The color of the dot at the origin. */
	private Color axisOriginDotColor = null;
	/** The color of the major tick marks. */
	private Color majorTickMarkColor = null;
	/** The color of the minor tick marks. */
	private Color minorTickMarkColor = null;
	/** The color of the axis labels. */
	private Color textColor = null;
	private int axisOrientation;
	AxisType type;
	private final Point location = new Point();
	private final Dimension size = new Dimension();
	private boolean isVisible = false;
	boolean onLeftSide;
	private static FontMenuItem fontMenuItem = null;
	private int lastAxisLength = -1;

	// cross-axis constants
	static final int padAroundEdge = 3; // how far things must be from the edge of the component
	static final int padFromAxis = 7; // how far the text must be from the axis
}
