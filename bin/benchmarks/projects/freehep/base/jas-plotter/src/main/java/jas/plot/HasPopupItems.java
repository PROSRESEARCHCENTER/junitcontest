package jas.plot;
/**
 * A component that implements this interface
 * may add menu items to a popup menu that is parented to itself or to one
 * of its child components.  When a component receives a
 * mouse event which is a popup trigger, its parent
 * <code>PlotPanel</code> builds a popup menu by asking
 * all of its parent components if they implement this
 * interface, and asks those that do to modify the menu.  The
 * class <code>PlotPopupItem</code> is designed to
 * provide a convenient reflection-based menu item.
 *  @see PlotPanel
 *  @see PlotPopupItem
 *  @author Jonas Gifford
 */
public interface HasPopupItems
{
	/** Modify the given menu object. */
	public void modifyPopupMenu(javax.swing.JPopupMenu menu, java.awt.Component source);
}
