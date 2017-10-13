package jas.plot;
import jas.util.border.ShadowBorder;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.LayoutManager;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;

public class PlotComponent extends JComponent
{
	public PlotComponent()
	{
		super();
	}
	public PlotComponent(LayoutManager layout)
	{
		super();
		setLayout(layout);
	}
    public void paintComponent(final Graphics g) 
	{
        if  (paintBackground && !PrintHelper.isPrinting()) 
		{
			// paint the background of the component, not the border
			Insets i = getInsets();
            g.setColor(getBackground());
            g.fillRect(i.left,i.top,
					   getWidth()-i.left-i.right,
					   getHeight()-i.top-i.bottom);
        } 
    }
	public void setBackground(Color bg)
	{
		super.setBackground(bg);
		paintBackground = bg != null;
	}
	public boolean isPaintingBackground()
	{
		return paintBackground;
	}
	/**
	 * Sets the border to one of the standard types
	 * @param type One of NONE, BEVEL_IN, BEVEL_OUT, ETCHED, LINE, SHADOW
	 */
	public void setBorderType(int type)
	{
		setBorder(createBorder(type));
	}
	/**
	 * Gets the border if it is one of the standard types
	 * @return One of NONE, BEVEL_IN, BEVEL_OUT, ETCHED, LINE, SHADOW, OTHER
	 */
	public int getBorderType()
	{
		return getBorderType(getBorder());
	}
	/**
	 * Given a border type will return a suitable border
	 */
	static Border createBorder(int type)
	{
		switch (type)
		{
		case BEVEL_IN:
			return BorderFactory.createLoweredBevelBorder();
		case BEVEL_OUT:
			return BorderFactory.createRaisedBevelBorder();
		case ETCHED:
			return BorderFactory.createEtchedBorder();
		case SHADOW:
			return ShadowBorder.createShadowBorder();
		case LINE:
			return BorderFactory.createLineBorder(Color.black);
		case NONE: 
		case OTHER:
		default:
			return null;
		}
	}
	static int getBorderType(Border b)
	{
		if (b == null) return NONE;
		// the three BorderFactory methods below return shared
		// border objects (canonical static instances)
		if (b == BorderFactory.createLoweredBevelBorder()) return BEVEL_IN;
		if (b == BorderFactory.createRaisedBevelBorder()) return BEVEL_OUT;
		if (b == BorderFactory.createEtchedBorder()) return ETCHED;
		// BorderFactory.createLineBorder() returns a new instance of LineBorder each time
		if (b instanceof LineBorder) return LINE;
		if (b instanceof ShadowBorder) return SHADOW;
		return OTHER;
	}
	public final static int OTHER = -1;
	public final static int NONE = 0;
	public final static int BEVEL_IN = 1;
	public final static int BEVEL_OUT = 2;
	public final static int ETCHED = 3;
	public final static int LINE = 4;
	public final static int SHADOW = 5;
	
	private boolean paintBackground;
}
