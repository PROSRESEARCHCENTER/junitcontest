package jas.plot;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager2;

public class ChartLayout implements LayoutManager2
{
	public void addLayoutComponent(Component c, Object constraints)
	{
		if (c instanceof Title) title = (Title) c;
		if (c instanceof DataArea) data = (DataArea) c;
		if (c instanceof Legend) legend = (Legend) c;
		if (c instanceof TextBlock) stats = (TextBlock) c;
	}
	public void addLayoutComponent(String s, Component c) 
	{
		addLayoutComponent(c,s);
	}
	public void removeLayoutComponent(Component c)
	{
		if (c == title) title = null;
		if (c == data ) data  = null;
		if (c == legend) legend = null;
		if (c == stats) stats = null;
	}
	public void layoutContainer(Container parent)
	{
		Dimension parentSize = parent.getSize();
		int titleOffset = 0;
		if (title != null && title.isVisible())
		{
			Dimension size = title.getPreferredSize();
                        if ( ! title.hasBeenResized() )
                            title.setSize(parentSize.width-pad-pad,size.height);
                        if ( ! title.hasBeenMoved() )
                            title.setLocation(pad,pad);
			titleOffset += size.height;
		}
		if (data != null )
		{
                    if ( ! data.hasBeenResized() )
                        data.setSize(parentSize.width-pad-pad,parentSize.height-pad-pad-titleOffset);
                    if ( ! data.hasBeenMoved() )
			data.setLocation(pad,pad+titleOffset);
		}
		if (legend != null && legend.isVisible())
		{
			// Position legend at top, right corner of data area by default
			
			Dimension ls = legend.getPreferredSize(); 
                        if ( ! legend.hasBeenMoved() )
                            legend.setLocation(parentSize.width - pad - ls.width , pad+titleOffset);
                        if ( ! legend.hasBeenResized() )
                            legend.setSize(ls);
			titleOffset += ls.height + pad;
		}
		if (stats != null && stats.isVisible())
		{
			// Position stats at top, right corner of data area, below legend
			
			Dimension ss = stats.getPreferredSize(); 
                        if ( ! stats.hasBeenMoved() )
                            stats.setLocation(parentSize.width - pad - ss.width , pad+titleOffset);
                        if ( ! stats.hasBeenResized() )
                            stats.setSize(ss);
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
		return title != null ? title.getPreferredSize() : new Dimension(10,10);
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
	void restoreDefaultLayout()
	{
		if (legend != null) legend.restoreDefaultLayout();
		if (title != null) title.restoreDefaultLayout();
		if (stats != null) stats.restoreDefaultLayout();
		if (data != null) data.restoreDefaultLayout();
	}
	boolean hasDefaultLayout()
	{
		boolean result = true;
		if (legend != null) result &= legend.hasDefaultLayout();
		if (title != null) result &= title.hasDefaultLayout();
		if (stats != null) result &= stats.hasDefaultLayout();
		if (data != null) result &= data.hasDefaultLayout();
		return result;
	}
	private Legend legend;
	private DataArea data;			      
	private Title title;
	private TextBlock stats;
	private static final int pad = 5;
}
