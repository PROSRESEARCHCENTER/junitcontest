package jas.plot;

import jas.plot.java2.PlotGraphics12;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.LayoutManager2;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;

/**
 * Represents a plot legend
 */
public class Legend extends MovableObject
{

    private SetablePlotGraphics pg = new PlotGraphics12();

        /** 
	 * Create an empty legend
	 */
	public Legend()
	{
		super("Legend");
		setLayout(new LegendLayout());
		setBorder(BorderFactory.createLineBorder(Color.black));
	}
	/**
	 * Adds a legend entry to a legend
	 */
	public void add(final LegendEntry e)
	{
		super.add(new LegendComponent(e));
		EditableLabel label = new EditableLabel(e.getTitle(),"Legend Entry",JLabel.LEFT)
		{
			protected void fireActionPerformed()
			{
				super.fireActionPerformed();
				if (e instanceof MutableLegendEntry) ((MutableLegendEntry) e).setTitle(getText());
			}
		};
		label.setShowFontMenuItem(false);
		super.add(label);
	}
	/**
	 * Removes a legend entry from a legend
	 */
	public void remove(LegendEntry e)
	{
		int n = getComponentCount();
		for (int i=0; i<n ; i++)
		{
			Component c = getComponent(i);
			if (c instanceof LegendComponent &&
				((LegendComponent) c).le == e)
			{
				remove(i);
				remove(i); // remove the associated label 
				return;
			}
		}
	}
	/**
	 * Removes all entries from the legend
	 */
	public void clear()
	{
		removeAll();
	}
	/**
	 * Return the number of entries in the Legend
	 */
	public int getNEntries()
	{
		return getComponentCount()/2;
	}
	/**
	 * Return the current title for the legend entry.
	 * (After any user changes)
	 */
	public String getCurrentTitle(int index)
	{
		LegendEntry le = ((LegendComponent) getComponent(2*index)).le;
		return le.getTitle();
	}
	/**
	 * True if the legend entry has been changed (by the user)
	 */
	public boolean isTitleChanged(int index)
	{
		LegendEntry le = ((LegendComponent) getComponent(2*index)).le;
		if (le instanceof MutableLegendEntry) return ((MutableLegendEntry) le).titleIsChanged();
		return false;
	}
	/**
	 * Set a new title for the legend entry
	 */
	public void setCurrentTitle(int index, String title)
	{
		LegendEntry le = ((LegendComponent) getComponent(2*index)).le;
		if (le instanceof MutableLegendEntry) ((MutableLegendEntry) le).setTitle(title);
	}
	private class LegendComponent extends Component
	{
		LegendComponent(LegendEntry e)
		{
			this.le = e;
		}
		public void paint(Graphics g)
		{
			Dimension d = getSize();
                        pg.setGraphics(g);
			le.paintIcon(pg,d.width,d.height);
		}
		public Dimension getPreferredSize()
		{
			return preferredIconSize;
		}
		LegendEntry le;
	}
	public void modifyPopupMenu(final JPopupMenu menu, final Component source)
	{
		if (menu.getComponentCount() > 0) menu.addSeparator();
		menu.add(new FontMenuItem(this,getPrefix()));
		super.modifyPopupMenu(menu,source);
	}
	public void setFont(Font font)
	{
		super.setFont(font);
		int n = getComponentCount();
		for (int i=1; i<n ; i+=2)
		{
			getComponent(i).setFont(font);
		}
	}
	public void legendTextChanged()
	{
		int n = getComponentCount();
		for (int i=0; i<n ; )
		{
			LegendComponent c = (LegendComponent) getComponent(i++);
			EditableLabel el = (EditableLabel) getComponent(i++);
			el.setText(c.le.getTitle());
		}
	}
	private static final Dimension preferredIconSize = new Dimension(15,15);
}
class LegendLayout implements LayoutManager2
{
	public void addLayoutComponent(Component c, Object constraints)
	{
		components.addElement(c);
	}
	public void removeLayoutComponent(Component c)
	{
		components.removeElement(c);
	}
	public void addLayoutComponent(String s, Component c) 
	{
		addLayoutComponent(c,s);
	}
	public void layoutContainer(Container parent)
	{
		Dimension parentSize = parent.getSize();
		Insets insets = parent.getInsets();

		int y = insets.top + pad;
		
		// Lay the components out vertically
		
		Enumeration e = components.elements();
		while (e.hasMoreElements())
		{
			Component icon = (Component) e.nextElement();
			Component label = (Component) e.nextElement();
			
			Dimension iconSize = icon.getPreferredSize();
			Dimension labelSize = label.getPreferredSize();
			
			icon.setSize(iconSize);
			icon.setLocation(insets.left+pad,Math.max(y,y+(labelSize.height-iconSize.height)/2));
			
			label.setSize(parentSize.width - 3*pad - iconSize.width - insets.left - insets.right,labelSize.height);
			label.setLocation(insets.left+2*pad+iconSize.width,Math.max(y,y+(iconSize.height-labelSize.height)/2)); 

			y += Math.max(iconSize.height,labelSize.height) + pad;
		}
	
	}
	public Dimension minimumLayoutSize(Container parent)
	{
		return preferredLayoutSize(parent);
	}
	public Dimension maximumLayoutSize(Container parent)
	{
		return preferredLayoutSize(parent);
	}
	public Dimension preferredLayoutSize(Container parent)
	{
		int x = 0;
		int y = 0;
		
		Enumeration e = components.elements();
		while (e.hasMoreElements())
		{
			Component icon = (Component) e.nextElement();
			Component label = (Component) e.nextElement();
			
			Dimension iconSize = icon.getPreferredSize();
			Dimension labelSize = label.getPreferredSize();
						
			x = Math.max(x,iconSize.width + labelSize.width);
			y += Math.max(iconSize.height,labelSize.height) + pad;
		}
		
		Insets insets = parent.getInsets();
		y += 2*pad + insets.top + insets.bottom;
		x += 3*pad + insets.left + insets.right;
		return new Dimension(x,y);
	}
	/**
	 * Returns the alignment along the x axis.  This specifies how
     * the component would like to be aligned relative to other 
     * components.  The value should be a number between 0 and 1
     * where 0 represents alignment along the origin, 1 is aligned
     * the furthest away from the origin, 0.5 is centered, etc.
     */
    public float getLayoutAlignmentX(Container parent)
	{
		return 0.5f;
    }

    /**
     * Returns the alignment along the y axis.  This specifies how
     * the component would like to be aligned relative to other 
     * components.  The value should be a number between 0 and 1
     * where 0 represents alignment along the origin, 1 is aligned
     * the furthest away from the origin, 0.5 is centered, etc.
     */
    public float getLayoutAlignmentY(Container parent) 
	{
		return 0.5f;
    }

    /**
     * Invalidates the layout, indicating that if the layout manager
     * has cached information it should be discarded.
     */
    public void invalidateLayout(Container target) 
	{
    }
	private static final int pad = 3;
	private Vector components = new Vector();
}
