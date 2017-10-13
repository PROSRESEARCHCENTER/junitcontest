package jas.plot;
import java.lang.reflect.Method;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
/**
 * This <code>JMenuItem</code> subclass implements a convenient
 * reflection-based menu item.  When this item is first
 * created, the class of the object specified by the <code>source</code>
 * argument to the constructor is searched for methods that match the
 * string given for the constructor's <code>name</code> argument.
 * The class will be searched for two methods:
 * <ul>
 *  <li><code>on&lt;title&gt;</code>: will be called if the menu itel
 *      is selected.  If this method is not found, the menu item
 *      will appear on the menu but will not enable.</li>
 *  <li><code>enable&lt;title&gt;</code>: will be called before the
 *      menu is shown and the item will enable according to the return
 *      value of this method.  If this method is not found (or if its
 *      return type is not <code>boolean.class</code>) the menu item
 *      will always be enabled.</li>
 * </ul>
 *  @see HasPopupItems
 *  @see HasPopupItems#getPopupItems()
 */
final class PlotPopupItem extends JMenuItem
{
	PlotPopupItem(String name, Object source)
	{
		super(name);
		setEnabled(false);
		if (name.endsWith("...")) 
			name = name.substring(0, name.length() - 3);
		for (int i = name.indexOf(" "); i >= 0; i = name.indexOf(' ')) 
			name = name.substring(0, i).concat(name.substring(i + 1));
		try
		{
			action = source.getClass().getMethod("on"+ name, no_arg_class);
			// if no exceptions...
			enabler = source.getClass().getMethod("enable"+ name, no_arg_class);
			// if no exceptions...
			if (enabler.getReturnType() != boolean.class)
				enabler = null; // we ignore the method because its return is useless
		}
		catch (Exception e)
		{
			if (! (e instanceof NoSuchMethodException))
				JOptionPane.showMessageDialog(this, e.toString(), "", JOptionPane.ERROR_MESSAGE);
		}
	}
	void callEnable()
	{
		if (enabler != null)
		{
			try
			{
				Boolean b = (Boolean) enabler.invoke(source, no_arg_object);
				setEnabled(b.booleanValue());
			}
			catch (Exception e)
			{
				setEnabled(false);
			}
		}
		else
			setEnabled(action != null);
	}
	protected void fireActionPerformed(java.awt.event.ActionEvent event)
	{
		// let's not bother with super.fireActionPerformed(event)
		if (action != null)
		{
			try
			{
				action.invoke(source, no_arg_object);
			}
			catch (Exception e)
			{
				/* BUG */
			}
		}
	}
	private Object source;
	private Method action = null;
	private Method enabler = null;
	static private final Class[] no_arg_class = new Class[0];
	static private final Object[] no_arg_object = new Object[0];
}
