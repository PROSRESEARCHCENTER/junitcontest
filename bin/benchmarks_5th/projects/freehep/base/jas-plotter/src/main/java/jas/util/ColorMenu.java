package jas.util;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.util.Enumeration;

import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JColorChooser;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.colorchooser.ColorSelectionModel;
import javax.swing.colorchooser.DefaultColorSelectionModel;

public class ColorMenu extends JMenu
{
	public ColorMenu(String name, ColorSelectionModel  model)
	{
		this(name, model,false);
	}
	public ColorMenu(String name)
    {
        this(name,false);
    }
    public ColorMenu(String name, boolean extendedColors)
    {		
        this(name, new DefaultColorSelectionModel(),extendedColors);
	}
	public ColorMenu(String name, ColorSelectionModel model, boolean extendedColors)
	{
        this(name,model,
			 extendedColors ? EXTENDED_COLORS : BASIC_COLORS,
			 extendedColors ? EXTENDED_COLOR_NAMES : BASIC_COLOR_NAMES);
		
	}
	public ColorMenu(String name, ColorSelectionModel model, Color[] colors, String[] names)
	{
		super(name);
		this.colorModel = model;
		for (int i=0; i<colors.length; i++)
		{
			add(new ColorMenuItem(colors[i],names[i]));
		}
		add(new OtherColorMenuItem());
	}
	public ColorSelectionModel getSelectionModel()
	{
		return colorModel;
	}
	public void setSelectionModel(ColorSelectionModel model)
	{
		this.colorModel = model;
	}
    public Color getColor()
	{ 
		return colorModel.getSelectedColor();
	}
    public void setColor(Color color)   
    {
		colorModel.setSelectedColor(color);
    }
	protected void fireMenuSelected()
	{
		Color c = colorModel.getSelectedColor();
		Enumeration e = group.getElements();
		while (e.hasMoreElements())
		{
			ColorMenuItem m = (ColorMenuItem) e.nextElement();
			m.setSelectedColor(c);
		}
		super.fireMenuSelected();
	}

	private ColorSelectionModel colorModel;
	private ButtonGroup group = new ButtonGroup();

	// static stuff

    /**
     *  An array containing the basic colors defined as constants in
     *  <code>java.awt.Color</code>.
     */
    public final static Color[] BASIC_COLORS = 
    { 
        Color.black,        Color.darkGray,         Color.gray, 
        Color.lightGray,    Color.blue,             Color.cyan, 
        Color.green,        Color.magenta,          Color.red, 
        Color.pink,         Color.orange,           Color.yellow, 
        Color.white 
    };

    /**
     *  An array containing the names of colors defined as constants in
     *  <code>java.awt.Color</code>.
     *
     *  @see #BASIC_COLORS
     */
    public final static String[] BASIC_COLOR_NAMES = 
    { 
        "black",            "dark gray",            "gray", 
        "light gray",       "blue",                 "cyan", 
        "green",            "magenta",              "red", 
        "pink",             "orange",               "yellow", 
        "white" 
    };

    // all hail to Crayola

    /**
     *  An array containing an expanded selection of colors.
     */
    public final static Color[] EXTENDED_COLORS = 
    {
        Color.black,                    new Color(0.1f, 0.1f, 0.1f),
        new Color(0.2f, 0.2f, 0.2f),    new Color(0.3f, 0.3f, 0.3f),
        new Color(0.4f, 0.4f, 0.4f),    new Color(0.5f, 0.5f, 0.5f),
        new Color(0.6f, 0.6f, 0.6f),    new Color(0.7f, 0.7f, 0.7f),
        new Color(0.8f, 0.8f, 0.8f),    new Color(0.9f, 0.9f, 0.9f),
        Color.white,                    Color.red,
        new Color(255, 136, 28),        new Color(120, 62, 27),
        new Color(0, 125, 32),          new Color(11, 157, 150),
        Color.blue,                     new Color(109, 0, 168),
        new Color(168, 0, 126),         Color.pink,
        Color.orange,                   Color.yellow,
        Color.green,                    Color.cyan,
        new Color(164, 207, 255),       new Color(225, 170, 255),
        new Color(255, 170, 210)
    };

    /**
     *  An array containing names of the expanded selection of colors.
     *
     *  @see #EXTENDED_COLORS
     */
    public final static String[] EXTENDED_COLOR_NAMES =
    {
        "black",                        "grey 10%",
        "grey 20%",                     "grey 30%",
        "grey 40%",                     "grey 50%",
        "grey 60%",                     "grey 70%",
        "grey 80%",                     "grey 90%",
        "white",                        "red",
        "orange",                       "brown",
        "green",                        "turquoise",
        "blue",                         "purple",
        "magenta",                      "pink",
        "light orange",                 "yellow",
        "light green",                  "cyan",
        "sky blue",                     "violet",
        "light magenta"
    };

	// inner classes
	
	private class ColorMenuItem extends JRadioButtonMenuItem implements Icon
	{
		ColorMenuItem(Color c, String s)
		{
			super(s);
			setIcon(this);
			color = c;
			group.add(this);
		}
		public void fireActionPerformed(ActionEvent e)
		{
			colorModel.setSelectedColor(color);
		}
		void setSelectedColor(Color c)
		{
			this.setSelected(c == color);
		}
		public int getIconHeight()
		{
			return size;
		}
		public int getIconWidth()
		{
			return size;
		}
		public void paintIcon(Component p1, Graphics g, int x, int y)
		{
         Color save = g.getColor();
			g.setColor(color == null ? getBackground() : color);
			g.fill3DRect(x,y,size,size,true);
         g.setColor(save);
		}
		private Color color;
	}
	private final static int size = 12;
	private class OtherColorMenuItem extends JMenuItem
	{
		OtherColorMenuItem()
		{
			super("Other...");
		}
		public void fireActionPerformed(ActionEvent e)
		{
			Color c = JColorChooser.showDialog(this,"Choose color...",colorModel.getSelectedColor());
			if (c != null) colorModel.setSelectedColor(c);
		}
	}
}
