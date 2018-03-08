package jas.util;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;

import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

/**
 * A popup menu which adjust where it is told to appear,
 * so that it will always appear on screen (if possible)
 */
public class OnScreenPopupMenu extends JPopupMenu
{
	public void show(Component source, int x, int y)
	{
		Dimension size = getPreferredSize();
		Point p = new Point(x,y);
		SwingUtilities.convertPointToScreen(p,source);
		Dimension screen = source.getToolkit().getScreenSize();
		if (p.x+size.width > screen.width)
		{
			p.x = Math.max(0,screen.width - size.width);
		}
		if (p.y+size.height > screen.height)
		{
			p.y = Math.max(0,screen.height - size.height);
		}
		SwingUtilities.convertPointFromScreen(p,source);
		super.show(source,p.x,p.y);
	}
}
